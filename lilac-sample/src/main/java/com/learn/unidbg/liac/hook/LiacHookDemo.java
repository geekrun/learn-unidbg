package com.learn.unidbg.liac.hook;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.learn.unidbg.extend.SuperExtendAbstractJni;
import com.learn.unidbg.extend.utils.ExtendFileUtils;

public class LiacHookDemo extends SuperExtendAbstractJni {


    private final AndroidEmulator emulator;
    private final DvmClass MainActivity;
    private final VM vm;

    public LiacHookDemo() {
//         创建模拟器实例
        emulator = AndroidEmulatorBuilder.for32Bit()
                .build();

        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(ExtendFileUtils.loadApkFile("liac_hook_demo.apk", "ctf"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary("hookinunidbg", false);
        MainActivity = vm.resolveClass("com.example.demo.MainActivity");
        dm.callJNI_OnLoad(emulator);


//         加载好的so对应为一个模块
        Module module = dm.getModule();
//         打印libnative-lib.so在Unidbg虚拟内存中的基地址
        System.out.println("dddddddddddddd:" + module.base);

    }


    public static void main(String[] args) {
        new LiacHookDemo();
    }


}
