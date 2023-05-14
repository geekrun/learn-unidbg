package com.learn.unidbg.extend.syscall;


import com.github.unidbg.Emulator;
import com.github.unidbg.linux.ARM64SyscallHandler;
import com.github.unidbg.memory.SvcMemory;

public class ExtendARM64SyscallHandler extends ARM64SyscallHandler {
    public ExtendARM64SyscallHandler(SvcMemory svcMemory) {
        super(svcMemory);
    }

    // 处理尚未模拟实现的系统调用
    @Override
    protected boolean handleUnknownSyscall(Emulator<?> emulator, int NR) {
        if (NR == 165) {
            getrusage(emulator);
            return true;
        }
        return super.handleUnknownSyscall(emulator, NR);
    }

    private void getrusage(Emulator<?> emulator){

    }
}
