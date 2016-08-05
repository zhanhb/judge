package com.github.zhanhb.judge.win32;

import com.github.zhanhb.judge.common.ExecuteResult;
import com.github.zhanhb.judge.common.Options;
import com.github.zhanhb.judge.common.Status;
import com.github.zhanhb.judge.jna.win32.Advapi32;
import com.github.zhanhb.judge.jna.win32.Advapi32.SID_AND_ATTRIBUTES;
import com.github.zhanhb.judge.jna.win32.Advapi32.SID_IDENTIFIER_AUTHORITY;
import com.github.zhanhb.judge.jna.win32.Advapi32.TOKEN_MANDATORY_LABEL;
import com.github.zhanhb.judge.jna.win32.Advapi32Util;
import com.github.zhanhb.judge.jna.win32.Kernel32;
import com.github.zhanhb.judge.jna.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinBase.PROCESS_INFORMATION;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.platform.win32.WinNT.TOKEN_INFORMATION_CLASS;
import java.nio.file.Path;

import static com.github.zhanhb.judge.common.Constants.TERMINATE_TIMEOUT;
import static com.github.zhanhb.judge.common.Constants.UPDATE_TIME_THRESHOLD;
import static com.github.zhanhb.judge.jna.win32.Advapi32.SANDBOX_INERT;
import static com.github.zhanhb.judge.jna.win32.Advapi32.SECURITY_MANDATORY_LOW_RID;
import static com.github.zhanhb.judge.jna.win32.Advapi32.SE_GROUP_INTEGRITY;
import static com.github.zhanhb.judge.jna.win32.Kernel32.HIGH_PRIORITY_CLASS;
import static com.sun.jna.platform.win32.WinBase.CREATE_BREAKAWAY_FROM_JOB;
import static com.sun.jna.platform.win32.WinBase.CREATE_NEW_PROCESS_GROUP;
import static com.sun.jna.platform.win32.WinBase.CREATE_NO_WINDOW;
import static com.sun.jna.platform.win32.WinBase.CREATE_SUSPENDED;
import static com.sun.jna.platform.win32.WinBase.CREATE_UNICODE_ENVIRONMENT;
import static com.sun.jna.platform.win32.WinBase.INVALID_HANDLE_VALUE;
import static com.sun.jna.platform.win32.WinBase.STARTF_FORCEOFFFEEDBACK;
import static com.sun.jna.platform.win32.WinBase.STARTF_USESTDHANDLES;
import static com.sun.jna.platform.win32.WinNT.CREATE_ALWAYS;
import static com.sun.jna.platform.win32.WinNT.FILE_ATTRIBUTE_NORMAL;
import static com.sun.jna.platform.win32.WinNT.FILE_FLAG_DELETE_ON_CLOSE;
import static com.sun.jna.platform.win32.WinNT.FILE_FLAG_WRITE_THROUGH;
import static com.sun.jna.platform.win32.WinNT.FILE_SHARE_READ;
import static com.sun.jna.platform.win32.WinNT.FILE_SHARE_WRITE;
import static com.sun.jna.platform.win32.WinNT.GENERIC_READ;
import static com.sun.jna.platform.win32.WinNT.GENERIC_WRITE;
import static com.sun.jna.platform.win32.WinNT.OPEN_ALWAYS;
import static com.sun.jna.platform.win32.WinNT.OPEN_EXISTING;
import static com.sun.jna.platform.win32.WinNT.TOKEN_ADJUST_DEFAULT;
import static com.sun.jna.platform.win32.WinNT.TOKEN_ASSIGN_PRIMARY;
import static com.sun.jna.platform.win32.WinNT.TOKEN_DUPLICATE;
import static com.sun.jna.platform.win32.WinNT.TOKEN_QUERY;

public class Executor {

    public static final int _O_RDONLY = 0;
    public static final int _O_WRONLY = 1;
    public static final int _O_RDWR = 2;

    /* Mask for access mode bits in the _open flags. */
    public static final int _O_ACCMODE = _O_WRONLY | _O_RDWR;

    public static final int _O_APPEND = 0x0008;
    /* Writes will add to the end of the file. */

    public static final int _O_RANDOM = 0x0010;
    public static final int _O_SEQUENTIAL = 0x0020;
    public static final int _O_TEMPORARY = 0x0040;
    /* Make the file dissappear after closing.
     * WARNING: Even if not created by _open! */
    public static final int _O_NOINHERIT = 0x0080;

    public static final int _O_CREAT = 0x0100;
    /* Create the file if it does not exist. */
    public static final int _O_TRUNC = 0x0200;
    /* Truncate the file if it does exist. */
    public static final int _O_EXCL = 0x0400;
    /* Open only if the file does not exist. */

    public static final int _O_SHORT_LIVED = 0x1000;

    /* NOTE: Text is the default even if the given _O_TEXT bit is not on. */
    public static final int _O_TEXT = 0x4000;
    /* CR-LF in file becomes LF in memory. */
    public static final int _O_BINARY = 0x8000;
    /* Input and output is not translated. */
    public static final int _O_RAW = _O_BINARY;

    public static final int _O_WTEXT = 0x10000;
    public static final int _O_U16TEXT = 0x20000;
    public static final int _O_U8TEXT = 0x40000;

    /* POSIX/Non-ANSI names for increased portability */
    public static final int O_RDONLY = _O_RDONLY;
    public static final int O_WRONLY = _O_WRONLY;
    public static final int O_RDWR = _O_RDWR;
    public static final int O_ACCMODE = _O_ACCMODE;
    public static final int O_APPEND = _O_APPEND;
    public static final int O_CREAT = _O_CREAT;
    public static final int O_TRUNC = _O_TRUNC;
    public static final int O_EXCL = _O_EXCL;
    public static final int O_TEXT = _O_TEXT;
    public static final int O_BINARY = _O_BINARY;
    public static final int O_TEMPORARY = _O_TEMPORARY;
    public static final int O_NOINHERIT = _O_NOINHERIT;
    public static final int O_SEQUENTIAL = _O_SEQUENTIAL;
    public static final int O_RANDOM = _O_RANDOM;
    public static final int O_SYNC = 0x0800;
    public static final int O_DSYNC = 0x2000;

    private HANDLE fileOpen(Path path, int flags) {
        int access
                = (flags & O_WRONLY) != 0 ? GENERIC_WRITE
                        : (flags & O_RDWR) != 0 ? (GENERIC_READ | GENERIC_WRITE)
                                : GENERIC_READ;
        int sharing = FILE_SHARE_READ | FILE_SHARE_WRITE;
        /* Note: O_TRUNC overrides O_CREAT */
        int disposition
                = (flags & O_TRUNC) != 0 ? CREATE_ALWAYS
                        : (flags & O_CREAT) != 0 ? OPEN_ALWAYS
                                : OPEN_EXISTING;
        int maybeWriteThrough
                = (flags & (O_SYNC | O_DSYNC)) != 0
                        ? FILE_FLAG_WRITE_THROUGH
                        : FILE_ATTRIBUTE_NORMAL;
        int maybeDeleteOnClose
                = (flags & O_TEMPORARY) != 0
                        ? FILE_FLAG_DELETE_ON_CLOSE
                        : FILE_ATTRIBUTE_NORMAL;

        int flagsAndAttributes = maybeWriteThrough | maybeDeleteOnClose;
        HANDLE h = Kernel32.INSTANCE.CreateFile(
                path.toString(), /* Wide char path name */
                access, /* Read and/or write permission */
                sharing, /* File sharing flags */
                null, /* Security attributes */
                disposition, /* creation disposition */
                flagsAndAttributes, /* flags and attributes */
                null);
        Kernel32Util.assertTrue(h != null && !INVALID_HANDLE_VALUE.equals(h));
        return h;
    }

    public ExecuteResult execute(Options options) {
        Path inputFile = options.getInputFile();
        Path outputPath = options.getOutputFile();
        boolean redirectErrorStream = options.isRedirectErrorStream();
        Path errorPath = options.getErrFile();
        Path workDirectory = options.getWorkDirectory();
        String command = options.getCommand();

        long timeLimit = options.getTimeLimit();
        long memoryLimit = options.getMemoryLimit();
        long outputLimit = options.getOutputLimit();

        PROCESS_INFORMATION pi;

        try (SafeHandle hIn = new SafeHandle(fileOpen(inputFile, O_RDONLY));
                SafeHandle hOut = new SafeHandle(fileOpen(outputPath, O_WRONLY | O_CREAT | O_TRUNC));
                SafeHandle hErr = redirectErrorStream ? hOut : new SafeHandle(fileOpen(errorPath, O_WRONLY | O_CREAT | O_TRUNC))) {
            pi = createProcess(command, hIn.getValue(), hOut.getValue(), hErr.getValue(), redirectErrorStream, workDirectory);
        }

        try (Sandbox sandbox = new Sandbox();
                SafeHandle hProcess = new SafeHandle(pi.hProcess);
                SafeHandle hThread = new SafeHandle(pi.hThread)) {
            JudgeProcess judgeProcess = new JudgeProcess(hProcess.getValue());
            try {
                sandbox.beforeProcessStart(hProcess.getValue());

                int dwCount = Kernel32.INSTANCE.ResumeThread(hThread.getValue());
                Kernel32Util.assertTrue(dwCount != -1);
                hThread.close();

                while (true) {
                    long memory = judgeProcess.getPeakMemory();
                    if (memory > memoryLimit) {
                        judgeProcess.terminate(Status.memoryLimitExceed);
                        break;
                    }
                    long time = judgeProcess.getActiveTime() - 1000; // extra 1000 millis
                    if (time > timeLimit) {
                        judgeProcess.terminate(Status.timeLimitExceed);
                        judgeProcess.join(TERMINATE_TIMEOUT);
                        break;
                    }
                    if (judgeProcess.getTime() > timeLimit) {
                        judgeProcess.terminate(Status.timeLimitExceed);
                        judgeProcess.join(TERMINATE_TIMEOUT);
                    }
                    long dwWaitTime = timeLimit - time;
                    if (dwWaitTime > UPDATE_TIME_THRESHOLD) {
                        dwWaitTime = UPDATE_TIME_THRESHOLD;
                    }
                    if (judgeProcess.join(dwWaitTime)) {
                        break;
                    }
                    // TODO maybe we should not check the output limit for current process may wait for the file to be finish
                    if (checkOle(outputPath, errorPath, redirectErrorStream, outputLimit)) {
                        judgeProcess.terminate(Status.outputLimitExceed);
                        break;
                    }
                }
                if (checkOle(outputPath, errorPath, redirectErrorStream, outputLimit)) {
                    judgeProcess.terminate(Status.outputLimitExceed);
                }
            } finally {
                judgeProcess.terminate(null);
            }
            judgeProcess.join(Long.MAX_VALUE);
            Status status = judgeProcess.getStatus();
            int exitCode = judgeProcess.getExitCode();
            if ((status == null || status == Status.accepted) && exitCode != 0) {
                status = Status.runtimeError;
            }
            long time = judgeProcess.getTime();
            return ExecuteResult.builder()
                    .time(status == Status.timeLimitExceed ? ((time - timeLimit - 1) % 200 + 200) % 200 + 1 + timeLimit : time)
                    .memory(judgeProcess.getPeakMemory())
                    .haltCode(status)
                    .exitCode(exitCode)
                    .build();
        }
    }

    private PROCESS_INFORMATION createProcess(String lpCommandLine, HANDLE hIn, HANDLE hOut, HANDLE hErr,
            boolean redirectErrorStream, Path lpCurrentDirectory) {
        WinBase.SECURITY_ATTRIBUTES sa = new WinBase.SECURITY_ATTRIBUTES();
        sa.bInheritHandle = true;

        String lpApplicationName = null;
        WinBase.SECURITY_ATTRIBUTES lpProcessAttributes = new WinBase.SECURITY_ATTRIBUTES();
        WinBase.SECURITY_ATTRIBUTES lpThreadAttributes = new WinBase.SECURITY_ATTRIBUTES();
        WinDef.DWORD dwCreationFlags = new WinDef.DWORD(
                CREATE_SUSPENDED
                | HIGH_PRIORITY_CLASS
                | CREATE_NEW_PROCESS_GROUP
                | CREATE_UNICODE_ENVIRONMENT
                | CREATE_BREAKAWAY_FROM_JOB
                | CREATE_NO_WINDOW
        );
        WinBase.STARTUPINFO lpStartupInfo = new WinBase.STARTUPINFO();
        WinBase.PROCESS_INFORMATION lpProcessInformation = new WinBase.PROCESS_INFORMATION();

        // without cursor feed back
        lpStartupInfo.dwFlags = STARTF_USESTDHANDLES | STARTF_FORCEOFFFEEDBACK;
        lpStartupInfo.hStdInput = hIn;
        lpStartupInfo.hStdOutput = hOut;
        lpStartupInfo.hStdError = hErr;

        Kernel32Util.setInheritable(hIn);
        Kernel32Util.setInheritable(hOut);
        if (!redirectErrorStream) {
            Kernel32Util.setInheritable(hErr);
        }

        try (SafeHandle hToken = new SafeHandle(createRestrictedToken())) {
            SID_IDENTIFIER_AUTHORITY pIdentifierAuthority = new SID_IDENTIFIER_AUTHORITY(new byte[]{0, 0, 0, 0, 0, 16});

            WinNT.PSID pSid = Advapi32Util.newPSID(pIdentifierAuthority, SECURITY_MANDATORY_LOW_RID);

            try {
                TOKEN_MANDATORY_LABEL tokenInformation = new TOKEN_MANDATORY_LABEL();
                SID_AND_ATTRIBUTES sidAndAttributes = new SID_AND_ATTRIBUTES();
                sidAndAttributes.Attributes = SE_GROUP_INTEGRITY;
                sidAndAttributes.Sid = pSid.getPointer();
                tokenInformation.Label = sidAndAttributes;

                Kernel32Util.assertTrue(Advapi32.INSTANCE.SetTokenInformation(
                        hToken.getValue(),
                        TOKEN_INFORMATION_CLASS.TokenIntegrityLevel,
                        tokenInformation,
                        tokenInformation.size() + Advapi32.INSTANCE.GetLengthSid(pSid)));

                ProcessCreationHelper.execute(()
                        -> Kernel32Util.assertTrue(Kernel32.INSTANCE.CreateProcessAsUser(
                                hToken.getValue(),
                                lpApplicationName, // executable name
                                lpCommandLine,// command line
                                lpProcessAttributes, // process security attribute
                                lpThreadAttributes, // thread security attribute
                                true, // inherits system handles
                                dwCreationFlags, // selected based on exe type
                                null,
                                lpCurrentDirectory != null ? lpCurrentDirectory.toString() : null,
                                lpStartupInfo,
                                lpProcessInformation)));
            } finally {
                Kernel32Util.assertTrue(Advapi32.INSTANCE.FreeSid(pSid) == null);
            }
        }
        return lpProcessInformation;
    }

    private HANDLE createRestrictedToken() {
        try (SafeHandle token = new SafeHandle(
                Advapi32Util.openProcessToken(Kernel32.INSTANCE.GetCurrentProcess(),
                        TOKEN_DUPLICATE | TOKEN_ASSIGN_PRIMARY | TOKEN_QUERY | TOKEN_ADJUST_DEFAULT))) {
            return Advapi32Util.createRestrictedToken(
                    token.getValue(), // ExistingTokenHandle
                    SANDBOX_INERT, // Flags
                    null, // SidsToDisable
                    null, // PrivilegesToDelete
                    null // SidsToRestrict
            );
        }
    }

    private boolean checkOle(Path outputPath, Path errorPath, boolean redirectErrorStream, long outputLimit) {
        return outputPath.toFile().length() > outputLimit
                || !redirectErrorStream && errorPath.toFile().length() > outputLimit;
    }

}
