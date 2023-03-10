package com.learn.unidbg.zsxq;


/*
    public static native String sign(String str, byte[] bArr);

function callSign(){
  Java.perform(function() {
    function stringToBytes(str) {
      var javaString = Java.use('java.lang.String');
      return javaString.$new(str).getBytes();
    }

    let NetCrypto = Java.use("com.izuiyou.network.NetCrypto");
    let arg1 = "hello world";
    let arg2 = "V I 50";
    let ret = NetCrypto.sign(arg1, stringToBytes(arg2));
    console.log("ret:"+ret);
  })
}

 */

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.context.Arm32RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;
import unicorn.ArmConst;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Zuiyou extends AbstractJni {
    AndroidEmulator androidEmulator;

    VM vm = null;


    Memory memory = null;
    DalvikModule dm;

    private void run() {

        androidEmulator = AndroidEmulatorBuilder.for32Bit().setProcessName("cn.xiaochuankeji.tieba").build();

        vm = androidEmulator.createDalvikVM(new File("lilac-sample/src/main/resources/right573.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        memory = androidEmulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        dm = vm.loadLibrary("net_crypto", true);
        dm.callJNI_OnLoad(androidEmulator);


        DvmClass dvmClass = vm.resolveClass("com.izuiyou.network.NetCrypto");
        String arg1 = "hello world";
        byte[] arg2 = "V I 50".getBytes(StandardCharsets.UTF_8);
        String result = dvmClass.callStaticJniMethodObject(androidEmulator, "sign(Ljava/lang/String;[B)Ljava/lang/String;", arg1, arg2).getValue().toString();

        System.out.printf(String.format("result======= %s", result));


    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "cn/xiaochuankeji/tieba/AppController->getPackageManager()Landroid/content/pm/PackageManager;": {
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
            }

            case "cn/xiaochuankeji/tieba/AppController->getPackageName()Ljava/lang/String;": {
                String packageName = vm.getPackageName();
                if (packageName != null) {
                    return new StringObject(vm, packageName);
                }
                break;
            }
            case "cn/xiaochuankeji/tieba/AppController->getClass()Ljava/lang/Class;":
                return dvmObject.getObjectType();
            case "java/lang/Class->getSimpleName()Ljava/lang/String;":
                return new StringObject(vm, ((DvmClass) dvmObject).getName());

            case "cn/xiaochuankeji/tieba/AppController->getFilesDir()Ljava/io/File;": {
                return vm.resolveClass("java/io/File").newObject("/data/data/cn.xiaochuankeji.tieba/files");
            }
            case "java/io/File->getAbsolutePath()Ljava/lang/String;": {
                return new StringObject(vm, dvmObject.getValue().toString());
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }


    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "com/izuiyou/common/base/BaseApplication->getAppContext()Landroid/content/Context;": {
                DvmObject<?> context = vm.resolveClass("cn/xiaochuankeji/tieba/AppController").newObject(null);
                return context;
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }


    @Override
    public boolean callStaticBooleanMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "android/os/Debug->isDebuggerConnected()Z": {
                return false;
            }
        }
        return super.callStaticBooleanMethodV(vm, dvmClass, signature, vaList);
    }


    @Override
    public int callStaticIntMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {

        switch (signature) {
            case "android/os/Process->myPid()I":
                return androidEmulator.getPid();
        }


        return super.callStaticIntMethodV(vm, dvmClass, signature, vaList);
    }


    public static void main(String[] args) {

        new Zuiyou().run();
    }


}
