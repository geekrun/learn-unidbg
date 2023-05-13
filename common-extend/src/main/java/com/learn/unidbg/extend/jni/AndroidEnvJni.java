package com.learn.unidbg.extend.jni;

import com.github.unidbg.linux.android.dvm.*;

public class AndroidEnvJni extends StandardJavaJni {



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

        }

        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }


    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "android/content/Context->getApplicationContext()Landroid/content/Context;":{
                return vm.resolveClass("android/content/Context").newObject(null);
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }
}
