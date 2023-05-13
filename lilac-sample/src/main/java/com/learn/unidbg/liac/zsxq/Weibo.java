package com.learn.unidbg.liac.zsxq;


/*

function callcalculateS(){
  Java.perform(function() {
    let WeiboSecurityUtils = Java.use("com.sina.weibo.security.WeiboSecurityUtils");
    let current_application = Java.use('android.app.ActivityThread').currentApplication();
    let arg1 = current_application.getApplicationContext();
    let arg2 = "hello world";
    let arg3 = "123456";
    let ret = WeiboSecurityUtils.$new().calculateS(arg1, arg2, arg3);
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

public class Weibo extends AbstractJni {
    AndroidEmulator androidEmulator;

    VM vm = null;


    Memory memory = null;
    DalvikModule dm;


    private void run() {

        androidEmulator = AndroidEmulatorBuilder.for32Bit().build();
        memory = androidEmulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));

        vm = androidEmulator.createDalvikVM(new File("lilac-sample/src/main/resources/sinaInternational.apk"));
        vm.setVerbose(true);
        vm.setJni(this);


        dm = vm.loadLibrary("utility", true);
        dm.callJNI_OnLoad(androidEmulator);


        androidEmulator.attach().addBreakPoint(dm.getModule().findSymbolByName("free").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                Arm32RegisterContext registerContext = emulator.getContext();
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, 0);
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLR());
                return true;
            }
        });


        DvmObject<?> obj = vm.resolveClass("com.sina.weibo.security.WeiboSecurityUtils").newObject(null);


        DvmObject<?> context = vm.resolveClass("android/app/Application", vm.resolveClass("android/content/ContextWrapper", vm.resolveClass("android/content/Context"))).newObject(null);

        String arg2 = "hello world";

        String arg3 = "123456";
        String result = obj.callJniMethodObject(androidEmulator, "calculateS", context, arg2, arg3).getValue().toString();

        System.out.printf(String.format("result======= %s", result));
    }


    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {


        switch (signature) {

            case "android/content/ContextWrapper->getPackageManager()Landroid/content/pm/PackageManager;":
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);


        }

        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }


    public static void main(String[] args) {

        new Weibo().run();
    }


}
