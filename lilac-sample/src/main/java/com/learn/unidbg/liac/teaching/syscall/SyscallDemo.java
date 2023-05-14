package com.learn.unidbg.liac.teaching.syscall;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.AndroidARM64Emulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import com.github.unidbg.unix.UnixSyscallHandler;
import com.learn.unidbg.extend.SuperExtendAbstractJni;
import com.learn.unidbg.extend.syscall.ExtendARM64SyscallHandler;
import com.learn.unidbg.extend.utils.ExtendFileUtils;

import java.io.File;

public class SyscallDemo extends SuperExtendAbstractJni {
    private final AndroidEmulator emulator;
    private final DvmClass MainActivity;
    private final VM vm;

    public SyscallDemo() {
        AndroidEmulatorBuilder builder = new AndroidEmulatorBuilder(true){
            @Override
            public AndroidEmulator build() {
                return new AndroidARM64Emulator(processName,rootDir,backendFactories) {
                    @Override
                    protected UnixSyscallHandler<AndroidFileIO> createSyscallHandler(SvcMemory svcMemory) {
                        return new ExtendARM64SyscallHandler(svcMemory);
                    }
                };
            }
        };

        emulator = builder
                .addBackendFactory(new Unicorn2Factory(true))
                .build();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));

        vm = emulator.createDalvikVM(ExtendFileUtils.loadApkFile("syscall_demo.apk","ctf"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary("demo", true);
        MainActivity = vm.resolveClass("com.example.demo.MainActivity");
        dm.callJNI_OnLoad(emulator);
    }

    public String call() {
        return MainActivity.newObject(null).callJniMethodObject(emulator, "stringFromJNI").getValue().toString();
    }

    public static void main(String[] args) {
        SyscallDemo demo = new SyscallDemo();
        System.out.println("ret:" + demo.call());
    }

}