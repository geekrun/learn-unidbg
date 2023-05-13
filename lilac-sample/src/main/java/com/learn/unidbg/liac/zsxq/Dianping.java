package com.learn.unidbg.liac.zsxq;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.SystemService;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.github.unidbg.virtualmodule.android.JniGraphics;
import com.learn.unidbg.extend.SuperExtendAbstractJni;
import com.learn.unidbg.extend.utils.ExtendFileUtils;

import java.util.HashMap;

public class Dianping extends SuperExtendAbstractJni implements IOResolver {
    private final AndroidEmulator emulator;
    private final DvmObject<?> SIUACollector;
    private final VM vm;


    private HashMap<String, String> sysInfo = new HashMap<>() {
        {
            put("brightness", "0.7");
            put("systemVolume", "0");
        }
    };

    public Dianping() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .addBackendFactory(new Unicorn2Factory(true))
                .setProcessName("")
                .build();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(ExtendFileUtils.loadApkFile("dazhongdianping10.41.15.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        emulator.getSyscallHandler().addIOResolver(this);
        // 使用 libandroid.so 的虚拟模块
        new AndroidModule(emulator, vm).register(memory);
        ;
        // 使用 libjnigraphics.so 的虚拟模块
        new JniGraphics(emulator, vm).register(memory);
        DalvikModule dm = vm.loadLibrary("mtguard", true);
        SIUACollector = vm.resolveClass("com/meituan/android/common/mtguard/NBridge$SIUACollector").newObject(null);
        dm.callJNI_OnLoad(emulator);

        printApkInfo(vm);
    }

    public static void main(String[] args) {

        Dianping nBridge = new Dianping();
        nBridge.run();
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject dvmObject, String signature, VaList vaList) {

        switch (signature){
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->isAccessibilityEnable()Z":
                return false;

        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/StringBuilder-><init>()V":
                return;

        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->getEnvironmentInfo()Ljava/lang/String;": {
                return new StringObject(vm, sysInfo.get("environmentInfo"));
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, DvmMethod dvmMethod, VaList vaList) {
        switch (dvmMethod.getSignature()) {
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->isVPN()Ljava/lang/String;":
                return new StringObject(vm, "");
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->brightness(Landroid/content/Context;)Ljava/lang/String;":
                return new StringObject(vm, sysInfo.get("brightness"));
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->systemVolume(Landroid/content/Context;)Ljava/lang/String;":
                return new StringObject(vm, sysInfo.get("systemVolume"));
            case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":
                StringObject serviceName = vaList.getObjectArg(0);
                assert serviceName != null;
                return new SystemService(vm, serviceName.getValue());


        }
        return super.callObjectMethodV(vm, dvmObject, dvmMethod, vaList);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature) {
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->mContext:Landroid/content/Context;":
                return vm.resolveClass("android/content/Context").newObject(null);
        }
        return super.getObjectField(vm, dvmObject, signature);
    }


    @Override
    public int callIntMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {

        switch (signature){
            case "com/meituan/android/common/mtguard/NBridge$SIUACollector->uiAutomatorClickCount()I":
                return 0;

        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    public void run() {
        var environmentInfo = this.getEnvironmentInfo();
        this.sysInfo.put("environmentInfo", environmentInfo);
        System.out.println("getEnvironmentInfoExtra== "+this.getEnvironmentInfoExtra());
        System.out.println("getExternalEquipmentInfo== "+this.getExternalEquipmentInfo());
    }

    public String getEnvironmentInfo() {
        String result = SIUACollector.callJniMethodObject(emulator, "getEnvironmentInfo()Ljava/lang/String;").getValue().toString();
        return result;
    }


    public String getEnvironmentInfoExtra() {
        String result = SIUACollector.callJniMethodObject(emulator, "getEnvironmentInfoExtra()Ljava/lang/String;").getValue().toString();
        return result;
    }


    public String getExternalEquipmentInfo(){
        String result = SIUACollector.callJniMethodObject(emulator, "getExternalEquipmentInfo()Ljava/lang/String;").getValue().toString();
        return result;
    }
    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("lilac open:" + pathname);
        return null;
    }
}

