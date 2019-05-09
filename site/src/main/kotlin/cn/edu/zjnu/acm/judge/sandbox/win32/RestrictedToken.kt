/**
 * Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package cn.edu.zjnu.acm.judge.sandbox.win32

import cn.edu.zjnu.acm.judge.sandbox.win32.IntegrityLevel.INTEGRITY_LEVEL_LAST
import jnc.foreign.byref.AddressByReference
import jnc.foreign.byref.IntByReference
import jnc.platform.StructArray
import jnc.platform.win32.*
import jnc.platform.win32.ACCESS_MODE.GRANT_ACCESS
import jnc.platform.win32.ACCESS_MODE.REVOKE_ACCESS
import jnc.platform.win32.AccCtrl.NO_INHERITANCE
import jnc.platform.win32.MULTIPLE_TRUSTEE_OPERATION.NO_MULTIPLE_TRUSTEE
import jnc.platform.win32.SECURITY_IMPERSONATION_LEVEL.SecurityIdentification
import jnc.platform.win32.SECURITY_IMPERSONATION_LEVEL.SecurityImpersonation
import jnc.platform.win32.TOKEN_INFORMATION_CLASS.*
import jnc.platform.win32.TOKEN_TYPE.TokenPrimary
import jnc.platform.win32.TRUSTEE_FORM.TRUSTEE_IS_SID
import jnc.platform.win32.TRUSTEE_TYPE.TRUSTEE_IS_UNKNOWN
import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinRestrictedCodeSid
import jnc.platform.win32.WinError.ERROR_SUCCESS
import jnc.platform.win32.WinNT.*
import org.slf4j.LoggerFactory
import java.io.Closeable

/**
 *
 * @author zhanhb
 */
class RestrictedToken// Initializes the RestrictedToken object with effectiveToken.
// If effectiveToken is nullptr, it initializes the RestrictedToken object
// with the effective token of the current process.
(/*HANDLE*/ effectiveToken: Long) : Closeable {

    // The list of restricting sids in the restricted token.
    private val sidsToRestrict = ArrayList<SID>(10) // PSID
    // The list of privileges to remove in the restricted token.
    private val privilegesToDisable = ArrayList<LUID>(16)
    // The list of sids to mark as Deny Only in the restricted token.
    private val sidsForDenyOnly = ArrayList<SID>(8) // PSID
    // The token to restrict. Can only be set in a constructor.
    private val effectiveToken: Long
    // The token integrity level. Only valid on Vista.
    private var integrityLevel: IntegrityLevel = INTEGRITY_LEVEL_LAST
    // Lockdown the default DACL when creating new tokens.
    private var lockdownDefaultDacl: Boolean = false

    private fun addSidToDacl(pSid: Long,
            /*PACL*/ oldDacl: Long,
                             accessMode: ACCESS_MODE,
                             access: Int,
                             newDacl: AddressByReference) {
        val newAccess = EXPLICIT_ACCESS()
        newAccess.setAccessMode(accessMode)
        newAccess.accessPermissions = access
        newAccess.inheritance = NO_INHERITANCE

        val trustee = newAccess.trustee
        trustee.multipleTrustee = 0/*nullptr*/
        trustee.setMultipleTrusteeOperation(NO_MULTIPLE_TRUSTEE)
        trustee.setTrusteeForm(TRUSTEE_IS_SID)
        trustee.setTrusteeType(TRUSTEE_IS_UNKNOWN)
        trustee.name = pSid

        if (log.isDebugEnabled) {
            log.debug("addSidToDacl: {} {}", accessMode, SID.toString(pSid))
        }

        val error = Advapi32.INSTANCE.SetEntriesInAclW(1, newAccess, oldDacl,
                newDacl)
        if (error != ERROR_SUCCESS) {
            throw Win32Exception(error)
        }
    }

    private fun addSidToDefaultDacl(token: Long,
                                    pSid: Long,
                                    accessMode: ACCESS_MODE,
            /*ACCESS_MASK*/ access: Int) {
        if (token == 0L) {
            throw NullPointerException()
        }
        val defaultDacl = getTokenDefaultDacl(token)

        val newDacl = AddressByReference()
        addSidToDacl(pSid, defaultDacl.defaultDacl, accessMode, access, newDacl)
        val dacl = newDacl.value
        try {
            val newTokenDacl = TOKEN_DEFAULT_DACL()
            newTokenDacl.defaultDacl = dacl
            Kernel32Util.assertTrue(Advapi32.INSTANCE.SetTokenInformation(token,
                    TokenDefaultDacl.value(), newTokenDacl, newTokenDacl.size()))
        } finally {
            Kernel32Util.freeLocalMemory(dacl)
        }
    }

    private fun revokeLogonSidFromDefaultDacl(token: Long) {
        val tokenGroups = getTokenGroups(token)
        var i = 0
        val n = tokenGroups.groupCount
        while (i < n) {
            val group = tokenGroups.get(i)
            if (group.attributes and SE_GROUP_LOGON_ID != 0) {
                addSidToDefaultDacl(token, group.sid, REVOKE_ACCESS, 0)
                break
            }
            ++i
        }
    }

    private fun addUserSidToDefaultDacl(token: Long, /*ACCESS_MASK*/ access: Int) {
        val tokenUser = getTokenUser(token)
        addSidToDefaultDacl(token, tokenUser.user.sid, GRANT_ACCESS, access)
    }

    private fun setTokenIntegrityLevel(token: Long, integrityLevel: IntegrityLevel) {
        // If no mandatory level specified, we don't change it.
        val integrityLevelStr = integrityLevel.string ?: return

        val integritySid = AddressByReference()
        Kernel32Util.assertTrue(Advapi32.INSTANCE.ConvertStringSidToSidW(
                WString.toNative(integrityLevelStr)!!, integritySid))
        try {
            val label = TOKEN_MANDATORY_LABEL()
            val sidAndAttributes = label.label
            sidAndAttributes.attributes = SE_GROUP_INTEGRITY
            sidAndAttributes.sid = integritySid.value

            val size = label.size() + Advapi32.INSTANCE.GetLengthSid(integritySid.value)
            Kernel32Util.assertTrue(Advapi32.INSTANCE.SetTokenInformation(token,
                    TokenIntegrityLevel.value(), label, size))
        } finally {
            Kernel32Util.freeLocalMemory(integritySid.value)
        }
    }

    private fun <T : TOKEN_INFORMATION> getTokenInfo(
            token: Long, infoClass: TOKEN_INFORMATION_CLASS,
            bySize: (Int) -> T): T {
        // get the required buffer size.
        val size = IntByReference()
        Advapi32.INSTANCE.GetTokenInformation(token, infoClass.value(), null, 0, size)
        val value = size.value
        Kernel32Util.assertTrue(value != 0)
        val buffer = bySize(value)
        Kernel32Util.assertTrue(Advapi32.INSTANCE.GetTokenInformation(token,
                infoClass.value(), buffer, buffer.size(), size))
        return buffer
    }

    private fun getTokenGroups(token: Long): TOKEN_GROUPS {
        return getTokenInfo(token, TokenGroups, { TOKEN_GROUPS.ofSize(it) })
    }

    private fun getTokenPrivileges(token: Long): TOKEN_PRIVILEGES {
        return getTokenInfo(token, TokenPrivileges, { TOKEN_PRIVILEGES.ofSize(it) })
    }

    private fun getTokenDefaultDacl(token: Long): TOKEN_DEFAULT_DACL {
        if (token == 0L) {
            throw NullPointerException()
        }
        return getTokenInfo(token, TokenDefaultDacl, { TOKEN_DEFAULT_DACL.ofSize(it) })
    }

    private fun getTokenUser(token: Long): TOKEN_USER {
        val tokenUser = TOKEN_USER.withPadding(SECURITY_MAX_SID_SIZE)

        val size = IntByReference()
        Kernel32Util.assertTrue(Advapi32.INSTANCE.GetTokenInformation(
                token, TokenUser.value(), tokenUser,
                tokenUser.size(), size))
        return tokenUser
    }

    init {
        val tempToken = AddressByReference()
        val hProcess = Kernel32.INSTANCE.GetCurrentProcess()
        val result: Boolean
        if (effectiveToken != 0L) {
            // We duplicate the handle to be able to use it even if the original handle
            // is closed.
            result = Kernel32.INSTANCE.DuplicateHandle(hProcess,
                    effectiveToken, hProcess, tempToken, 0, false,
                    DUPLICATE_SAME_ACCESS)
        } else {
            result = Advapi32.INSTANCE.OpenProcessToken(hProcess,
                    TOKEN_ALL_ACCESS, tempToken)
        }
        Kernel32Util.assertTrue(result)
        this.effectiveToken = tempToken.value
    }

    // Creates a restricted token.
    fun createRestrictedToken() /*const*/: Long {
        val denySize = sidsForDenyOnly.size
        val restrictSize = sidsToRestrict.size
        val privilegesSize = privilegesToDisable.size

        log.debug("createRestrictedToken: {} {} {}", sidsForDenyOnly, privilegesToDisable, sidsToRestrict)

        var denyOnlyArray: StructArray<SID_AND_ATTRIBUTES>? = null
        if (denySize != 0) {
            denyOnlyArray = StructArray({ SID_AND_ATTRIBUTES() }, denySize)

            for (i in 0 until denySize) {
                val sidAndAttributes = denyOnlyArray[i]
                sidAndAttributes.attributes = SE_GROUP_USE_FOR_DENY_ONLY
                sidAndAttributes.sid = sidsForDenyOnly[i].asPSID()
            }
        }

        var sidsToRestrictArray: StructArray<SID_AND_ATTRIBUTES>? = null
        if (restrictSize != 0) {
            sidsToRestrictArray = StructArray({ SID_AND_ATTRIBUTES() }, restrictSize)

            for (i in 0 until restrictSize) {
                val sidAndAttributes = sidsToRestrictArray[i]
                sidAndAttributes.attributes = 0
                sidAndAttributes.sid = sidsToRestrict[i].asPSID()
            }
        }

        var privilegesToDisableArray: StructArray<LUID_AND_ATTRIBUTES>? = null
        if (privilegesSize != 0) {
            privilegesToDisableArray = StructArray({ LUID_AND_ATTRIBUTES() }, privilegesSize)

            for (i in 0 until privilegesSize) {
                val luidAndAttributes = privilegesToDisableArray[i]
                luidAndAttributes.attributes = 0
                luidAndAttributes.luid.copyFrom(privilegesToDisable[i])
            }
        }

        val result: Boolean
        val newTokenHandle = AddressByReference()
        if (denySize != 0 || restrictSize != 0 || privilegesSize != 0) {
            result = Advapi32.INSTANCE.CreateRestrictedToken(
                    effectiveToken, 0,
                    denySize, denyOnlyArray,
                    privilegesSize, privilegesToDisableArray,
                    restrictSize, sidsToRestrictArray,
                    newTokenHandle)
        } else {
            // Duplicate the token even if it's not modified at this point
            // because any subsequent changes to this token would also affect the
            // current process.
            result = Advapi32.INSTANCE.DuplicateTokenEx(effectiveToken,
                    TOKEN_ALL_ACCESS, null, SecurityIdentification.value(),
                    TokenPrimary.value(), newTokenHandle)
        }
        Kernel32Util.assertTrue(result)

        val newToken = newTokenHandle.value
        try {
            if (lockdownDefaultDacl) {
                // Don't add Restricted sid and also remove logon sid access.
                revokeLogonSidFromDefaultDacl(newToken)
            } else {
                val restrictedCodeSid = SID.ofWellKnown(WinRestrictedCodeSid)
                // Modify the default dacl on the token to contain Restricted.
                addSidToDefaultDacl(newToken, restrictedCodeSid.asPSID(),
                        GRANT_ACCESS, GENERIC_ALL)
            }

            // Add user to default dacl.
            addUserSidToDefaultDacl(newToken, GENERIC_ALL)

            setTokenIntegrityLevel(newToken, integrityLevel)

            val tokenHandle = AddressByReference()
            val hProcess = Kernel32.INSTANCE.GetCurrentProcess()
            Kernel32Util.assertTrue(Kernel32.INSTANCE.DuplicateHandle(hProcess,
                    newToken, hProcess, tokenHandle, TOKEN_ALL_ACCESS,
                    false, // Don't inherit.
                    0))
            return tokenHandle.value
        } finally {
            Handle.close(newToken)
        }
    }

    // Creates a restricted token and uses this new token to create a new token
    // for impersonation. Returns this impersonation token.
    //
    // The sample usage is the same as the createRestrictedToken function.
    fun createRestrictedTokenForImpersonation() /*const*/: Long {
        val restrictedToken = createRestrictedToken()
        try {
            val impersonationTokenHandle = AddressByReference()
            Kernel32Util.assertTrue(Advapi32.INSTANCE.DuplicateToken(
                    restrictedToken, SecurityImpersonation.value(),
                    impersonationTokenHandle))
            val impersonationToken = impersonationTokenHandle.value
            try {
                val tokenHandle = AddressByReference()
                val hProcess = Kernel32.INSTANCE.GetCurrentProcess()
                Kernel32Util.assertTrue(Kernel32.INSTANCE.DuplicateHandle(
                        hProcess, impersonationToken, hProcess,
                        tokenHandle, TOKEN_ALL_ACCESS, false, // Don't inherit.
                        0))
                return tokenHandle.value
            } finally {
                Handle.close(impersonationToken)
            }
        } finally {
            Handle.close(restrictedToken)
        }
    }

    // Lists all sids in the token and mark them as Deny Only except for those
    // present in the exceptions parameter. If there is no exception needed,
    // the caller can pass an empty list or nullptr for the exceptions
    // parameter.
    //
    // Sample usage:
    //    List<SID> sidExceptions = new ArrayList<>();
    //    sidExceptions.add(SID.ofWellKnown(WinBuiltinUsersSid));
    //    sidExceptions.add(SID.ofWellKnown(WinWorldSid));
    //    restrictedToken.addAllSidsForDenyOnly(sidExceptions);
    // Note: A SID marked for Deny Only in a token cannot be used to grant
    // access to any resource. It can only be used to deny access.
    fun addAllSidsForDenyOnly(exceptions: Collection<SID>) {
        val tokenGroups = getTokenGroups(effectiveToken)

        // Build the list of the deny only group SIDs
        var i = 0
        val n = tokenGroups.groupCount
        while (i < n) {
            val group = tokenGroups.get(i)
            if (group.attributes and (SE_GROUP_INTEGRITY or SE_GROUP_LOGON_ID) == 0) {
                val sid = SID.copyOf(group.sid)
                val shouldIgnore = exceptions.contains(sid)
                if (!shouldIgnore) {
                    sidsForDenyOnly.add(sid)
                }
            }
            ++i
        }
    }

    // Adds a user or group SID for Deny Only in the restricted token.
    // Parameter: sid is the SID to add in the Deny Only list.
    // The return getPSID is always ERROR_SUCCESS.
    //
    // Sample Usage:
    //    restrictedToken.addSidForDenyOnly(SID.ofWellKnown(WinBuiltinAdministratorsSid));
    fun addSidForDenyOnly(sid: SID) {
        sidsForDenyOnly.add(sid)
    }

    // Adds the user sid of the token for Deny Only in the restricted token.
    fun addUserSidForDenyOnly() {
        val tokenUser = getTokenUser(effectiveToken)
        sidsForDenyOnly.add(SID.copyOf(tokenUser.user.sid))
    }

    // Lists all privileges in the token and add them to the list of privileges
    // to remove except for those present in the exceptions parameter. If
    // there is no exception needed, the caller can pass an empty list or nullptr
    // for the exceptions parameter.
    //
    // Sample usage:
    //    List<LUID> privilegeExceptions = new ArrayList<>();
    //    privilegeExceptions.add(LUID.lookup(SE_CHANGE_NOTIFY_NAME));
    //    privilegeExceptions.deleteAllPrivileges(privilegeExceptions);
    fun deleteAllPrivileges(exceptions: Collection<LUID>) {
        val tokenPrivileges = getTokenPrivileges(effectiveToken)
        log.debug("deleteAllPrivileges: delete all except '{}' from {}", exceptions, tokenPrivileges)
        // Build the list of privileges to disable
        var i = 0
        val n = tokenPrivileges.privilegeCount
        while (i < n) {
            val luid = tokenPrivileges.get(i).luid
            val shouldIgnore = exceptions.contains(luid)
            if (!shouldIgnore) {
                privilegesToDisable.add(luid)
            }
            ++i
        }
    }

    // Adds a privilege to the list of privileges to remove in the restricted
    // token.
    // Parameter: privilege is the privilege name to remove. This is the string
    // representing the privilege. (e.g. "SeChangeNotifyPrivilege").
    //
    // Sample usage:
    //    restrictedToken.deletePrivilege(LUID.lookup(SE_LOAD_DRIVER_NAME));
    fun deletePrivilege(privilege: LUID) {
        privilegesToDisable.add(privilege)
    }

    // Adds a SID to the list of restricting sids in the restricted token.
    // Parameter: sid is the sid to add to the list restricting sids.
    // The return getPSID is always ERROR_SUCCESS.
    //
    // Sample usage:
    //    restrictedToken.addRestrictingSid(SID.ofWellKnown(WinBuiltinUsersSid));
    // Note: The list of restricting is used to force Windows to perform all
    // access checks twice. The first time using your user SID and your groups,
    // and the second time using your list of restricting sids. The access has
    // to be granted in both places to get access to the resource requested.
    fun addRestrictingSid(sid: SID) {
        sidsToRestrict.add(sid)  // No attributes
    }

    // Adds the logon sid of the token in the list of restricting sids for the
    // restricted token.
    fun addRestrictingSidLogonSession() {
        val tokenGroups = getTokenGroups(effectiveToken)

        var logonSid: Long = 0
        var i = 0
        val n = tokenGroups.groupCount
        while (i < n) {
            val group = tokenGroups.get(i)
            if (group.attributes and SE_GROUP_LOGON_ID != 0) {
                logonSid = group.sid
                break
            }
            ++i
        }

        if (logonSid != 0L) {
            sidsToRestrict.add(SID.copyOf(logonSid))
        }
    }

    // Adds the owner sid of the token in the list of restricting sids for the
    // restricted token.
    fun addRestrictingSidCurrentUser() {
        val tokenUser = getTokenUser(effectiveToken)
        sidsToRestrict.add(SID.copyOf(tokenUser.user.sid))
    }

    // Adds all group sids and the user sid to the restricting sids list.
    fun addRestrictingSidAllSids() {
        // Add the current user to the list.
        addRestrictingSidCurrentUser()
        val tokenGroups = getTokenGroups(effectiveToken)

        // Build the list of restricting sids from all groups.
        var i = 0
        val n = tokenGroups.groupCount
        while (i < n) {
            val group = tokenGroups.get(i)
            if (group.attributes and SE_GROUP_INTEGRITY == 0) {
                addRestrictingSid(SID.copyOf(group.sid))
            }
            ++i
        }
    }

    // Sets the token integrity level. This is only valid on Vista. The integrity
    // level cannot be higher than your current integrity level.
    fun setIntegrityLevel(integrityLevel: IntegrityLevel) {
        this.integrityLevel = integrityLevel
    }

    // Set a flag which indicates the created token should have a locked down
    // default DACL when created.
    fun setLockdownDefaultDacl() {
        lockdownDefaultDacl = true
    }

    override fun close() {
        Handle.close(effectiveToken)
    }

    companion object {
        private val log = LoggerFactory.getLogger(RestrictedToken::class.java)
    }
}
