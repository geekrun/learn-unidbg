package com.learn.unidbg.extend.jni;

import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.SystemService;

import java.util.Collections;

public class AndroidEnvJni extends StandardJavaJni {


    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "android/os/Build$VERSION->SDK_INT:I":
                return 29;
            case "android/hardware/Sensor->TYPE_ALL:I":
                return -1;
        }
        return super.getStaticIntField(vm, dvmClass, signature);
    }


    @Override
    public int getIntField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature) {
            case "android/util/DisplayMetrics->widthPixels:I": {
                return 1080;
            }
            case "android/util/DisplayMetrics->heightPixels:I": {
                return 2160;
            }
        }
        return super.getIntField(vm, dvmObject, signature);
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
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {


        switch (signature) {

            case "android/content/ContextWrapper->getPackageManager()Landroid/content/pm/PackageManager;":
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
            case "android/content/Context->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":
                StringObject serviceName = varArg.getObjectArg(0);
                assert serviceName != null;
                return new SystemService(vm, serviceName.getValue());
            case "android/hardware/SensorManager->getSensorList(I)Ljava/util/List;":
                return new ArrayListObject(vm, Collections.<DvmObject<?>>emptyList());

            case "android/view/WindowManager->getDefaultDisplay()Landroid/view/Display;":
                return vm.resolveClass("android/view/Display").newObject(signature);
        }

        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }


    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "android/content/Context->getApplicationContext()Landroid/content/Context;": {
                return vm.resolveClass("android/content/Context").newObject(null);
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }


    @Override
    public DvmObject<?> newObject(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "android/util/DisplayMetrics-><init>()V":
                return dvmClass.newObject(null);
        }
        return super.newObject(vm, dvmClass, signature, varArg);
    }


    @Override
    public void callVoidMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "android/view/Display->getMetrics(Landroid/util/DisplayMetrics;)V":
                return;

        }
        super.callVoidMethod(vm, dvmObject, signature, varArg);
    }
}
