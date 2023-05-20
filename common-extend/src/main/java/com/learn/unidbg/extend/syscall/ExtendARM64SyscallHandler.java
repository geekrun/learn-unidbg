package com.learn.unidbg.extend.syscall;

import com.github.unidbg.Emulator;
import com.github.unidbg.linux.ARM64SyscallHandler;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.sun.jna.Pointer;
import unicorn.Arm64Const;

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

    /*
    00 00 00 00 00 00 00 00 9f 4a 0b 00 00 00 00 00  .........J......
    00 00 00 00 00 00 00 00 c5 e1 01 00 00 00 00 00  ................
    90 52 01 00 00 00 00 00 00 00 00 00 00 00 00 00  .R..............
    00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
    25 5e 00 00 00 00 00 00 00 00 00 00 00 00 00 00  %^..............
    00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
    00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
    00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00  ................
    0d 02 00 00 00 00 00 00 d3 00 00 00 00 00 00 00  ................
     */
    private void getrusage(Emulator<?> emulator) {
        Pointer rusage = UnidbgPointer.register(emulator, Arm64Const.UC_ARM64_REG_X1);
        rusage.setLong(0, 0);
        rusage.setLong(8, 0xB4A9FL);
        rusage.setLong(16, 0);
        rusage.setLong(24, 0x1E1C5L);
        rusage.setLong(32, 0x15290L);
        // 继续往下
    }

}