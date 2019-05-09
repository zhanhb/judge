/**
 * Copyright (c) 2006-2008 The Chromium Authors. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be
 * found in the LICENSE file.
 */
package cn.edu.zjnu.acm.judge.sandbox.win32

import jnc.platform.win32.LUID
import jnc.platform.win32.SID
import jnc.platform.win32.Win32Exception
import com.google.common.collect.ImmutableSet

import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinAuthenticatedUserSid
import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinBuiltinUsersSid
import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinInteractiveSid
import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinNullSid
import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinRestrictedCodeSid
import jnc.platform.win32.WELL_KNOWN_SID_TYPE.WinWorldSid
import jnc.platform.win32.WinError.ERROR_BAD_ARGUMENTS
import jnc.platform.win32.WinNT.SE_CHANGE_NOTIFY_NAME

enum class Sandbox {

    INSTANCE;

    private val SID_NULL = SID.ofWellKnown(WinNullSid)
    private val SID_WORLD = SID.ofWellKnown(WinWorldSid)
    private val SID_INTERACTIVE = SID.ofWellKnown(WinInteractiveSid)
    private val SID_AUTHENTICATED_USER = SID.ofWellKnown(WinAuthenticatedUserSid)
    private val SID_RESTRICTED_CODE = SID.ofWellKnown(WinRestrictedCodeSid)
    private val SID_BUILTIN_USERS = SID.ofWellKnown(WinBuiltinUsersSid)
    private val CHANGE_NOTIFY = ImmutableSet.of(LUID.lookup(SE_CHANGE_NOTIFY_NAME))
    private val USER_NON_ADMIN_EXCEPTION = ImmutableSet.of(SID_WORLD, SID_INTERACTIVE, SID_AUTHENTICATED_USER, SID_BUILTIN_USERS)
    private val USER_INTERACTIVE_EXCEPTION = USER_NON_ADMIN_EXCEPTION
    private val USER_LIMITED_EXCEPTION = ImmutableSet.of(SID_WORLD, SID_INTERACTIVE, SID_BUILTIN_USERS)

    fun createRestrictedToken(
            securityLevel: TokenLevel,
            integrityLevel: IntegrityLevel,
            tokenType: TokenType,
            lockdownDefaultDacl: Boolean): Long {
        // Initialized with the current process token
        RestrictedToken(/*nullptr*/0).use { restrictedToken ->
            if (lockdownDefaultDacl) {
                restrictedToken.setLockdownDefaultDacl()
            }

            var privilegeExceptions: Set<LUID> = ImmutableSet.of()
            var sidExceptions: Set<SID> = ImmutableSet.of()

            var denySids = true
            var removePrivileges = true

            when (securityLevel) {
                TokenLevel.USER_UNPROTECTED -> {
                    denySids = false
                    removePrivileges = false
                }
                TokenLevel.USER_RESTRICTED_SAME_ACCESS -> {
                    denySids = false
                    removePrivileges = false

                    restrictedToken.addRestrictingSidAllSids()
                }
                TokenLevel.USER_NON_ADMIN -> {
                    sidExceptions = USER_NON_ADMIN_EXCEPTION
                    privilegeExceptions = CHANGE_NOTIFY
                }
                TokenLevel.USER_INTERACTIVE -> {
                    sidExceptions = USER_INTERACTIVE_EXCEPTION
                    privilegeExceptions = CHANGE_NOTIFY
                    restrictedToken.addRestrictingSid(SID_BUILTIN_USERS)
                    restrictedToken.addRestrictingSid(SID_WORLD)
                    restrictedToken.addRestrictingSid(SID_RESTRICTED_CODE)
                    restrictedToken.addRestrictingSidCurrentUser()
                    restrictedToken.addRestrictingSidLogonSession()
                }
                TokenLevel.USER_LIMITED -> {
                    sidExceptions = USER_LIMITED_EXCEPTION
                    privilegeExceptions = CHANGE_NOTIFY
                    restrictedToken.addRestrictingSid(SID_BUILTIN_USERS)
                    restrictedToken.addRestrictingSid(SID_WORLD)
                    restrictedToken.addRestrictingSid(SID_RESTRICTED_CODE)

                    // This token has to be able to create objects in BNO.
                    // Unfortunately, on Vista+, it needs the current logon sid
                    // in the token to achieve this. You should also set the process to be
                    // low integrity level so it can't access object created by other
                    // processes.
                    restrictedToken.addRestrictingSidLogonSession()
                }
                TokenLevel.USER_RESTRICTED -> {
                    privilegeExceptions = CHANGE_NOTIFY
                    restrictedToken.addUserSidForDenyOnly()
                    restrictedToken.addRestrictingSid(SID_RESTRICTED_CODE)
                }
                TokenLevel.USER_LOCKDOWN -> {
                    restrictedToken.addUserSidForDenyOnly()
                    restrictedToken.addRestrictingSid(SID_NULL)
                }
                else -> throw Win32Exception(ERROR_BAD_ARGUMENTS)
            }

            if (denySids) {
                restrictedToken.addAllSidsForDenyOnly(sidExceptions)
            }

            if (removePrivileges) {
                restrictedToken.deleteAllPrivileges(privilegeExceptions)
            }

            restrictedToken.setIntegrityLevel(integrityLevel)

            return when (tokenType) {
                TokenType.PRIMARY -> restrictedToken.createRestrictedToken()
                TokenType.IMPERSONATION -> restrictedToken.createRestrictedTokenForImpersonation()
            }
        }
    }

}
