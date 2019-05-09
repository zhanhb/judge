package cn.edu.zjnu.acm.judge.sandbox.win32

import jnc.platform.win32.*
import jnc.platform.win32.JOBOBJECTINFOCLASS.JobObjectBasicLimitInformation
import jnc.platform.win32.JOBOBJECTINFOCLASS.JobObjectBasicUIRestrictions
import jnc.platform.win32.WinNT.*
import java.io.Closeable

class Job : Closeable {

    private val /*HANDLE*/ hJob: Long

    init {
        val handle = Kernel32.INSTANCE.CreateJobObjectW(null, null)
        Kernel32Util.assertTrue(handle != 0L)
        this.hJob = handle
    }

    private fun setInformationJobObject(jobobjectinfoclass: JOBOBJECTINFOCLASS, jobj: JOBOBJECT_INFORMATION) {
        Kernel32Util.assertTrue(Kernel32.INSTANCE.SetInformationJobObject(hJob, jobobjectinfoclass.value(), jobj, jobj.size()))
    }

    fun init() {
        val jobli = JOBOBJECT_BASIC_LIMIT_INFORMATION()
        jobli.activeProcessLimit =1
        // These are the only 1 restrictions I want placed on the job (process).
        jobli.limitFlags = JOB_OBJECT_LIMIT_ACTIVE_PROCESS
        setInformationJobObject(JobObjectBasicLimitInformation, jobli)

        // Second, set some UI restrictions.
        val jbur = JOBOBJECT_BASIC_UI_RESTRICTIONS()
        jbur.uiRestrictionsClass = (
                // The process can't access USER objects (such as other windows)
                // in the system.
                JOB_OBJECT_UILIMIT_HANDLES
                        or JOB_OBJECT_UILIMIT_READCLIPBOARD
                        or JOB_OBJECT_UILIMIT_WRITECLIPBOARD
                        or JOB_OBJECT_UILIMIT_SYSTEMPARAMETERS
                        or JOB_OBJECT_UILIMIT_DISPLAYSETTINGS
                        or JOB_OBJECT_UILIMIT_GLOBALATOMS
                        or // Prevents processes associated with the job from creating desktops
                        // and switching desktops using the CreateDesktop and SwitchDesktop functions.
                        JOB_OBJECT_UILIMIT_DESKTOP
                        or // The process can't log off the system.
                        JOB_OBJECT_UILIMIT_EXITWINDOWS)
        setInformationJobObject(JobObjectBasicUIRestrictions, jbur)
    }

    fun assignProcess(/*HANDLE*/ hProcess: Long) {
        Kernel32Util.assertTrue(Kernel32.INSTANCE.AssignProcessToJobObject(hJob, hProcess))
    }

    override fun close() {
        Handle.close(hJob)
    }

}
