package com.learn.unidbg.extend.jni;

import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;

import java.util.Map;

public class StandardJavaJni extends AbstractJni {


    @Override
    public DvmObject<?> allocObject(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "java/lang/StringBuilder->allocObject":
                return dvmClass.newObject(new StringBuilder());
        }
        return super.allocObject(vm, dvmClass, signature);
    }

    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "java/util/Map->get(Ljava/lang/Object;)Ljava/lang/Object;":
                Map map = (Map) dvmObject.getValue();
                var key = varArg.getObjectArg(0).getValue();
                return ProxyDvmObject.createObject(vm, map.get(key));
            case "java/util/Map->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;":
                Map map1 = (Map) dvmObject.getValue();
                var key1 = varArg.getObjectArg(0).getValue();
                var val1 = varArg.getObjectArg(1).getValue();
                return ProxyDvmObject.createObject(vm, map1.put(key1, val1));

            case "java/lang/StringBuilder->toString()Ljava/lang/String;":
                StringBuilder stringBuilder= (StringBuilder) dvmObject.getValue();
                return  new StringObject(vm,stringBuilder.toString());

        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }


    @Override
    public boolean callBooleanMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "java/util/Map->isEmpty()Z":
                Map map = (Map) dvmObject.getValue();
                return map.isEmpty();
        }
        return super.callBooleanMethod(vm, dvmObject, signature, varArg);
    }


    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/io/File->getAbsolutePath()Ljava/lang/String;": {
                return new StringObject(vm, dvmObject.getValue().toString());
            }
            case "java/lang/StringBuilder->append(Ljava/lang/String;)Ljava/lang/StringBuilder;": {
                String str = vaList.getObjectArg(0).getValue().toString();
                return ProxyDvmObject.createObject(vm, ((StringBuilder) dvmObject.getValue()).append(str));
            }
            case "java/lang/StringBuilder->toString()Ljava/lang/String;":
                StringBuilder stringBuilder= (StringBuilder) dvmObject.getValue();
                return  new StringObject(vm,stringBuilder.toString());
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }


    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "java/lang/String->valueOf(I)Ljava/lang/String;":
                return new StringObject(vm,String.valueOf(vaList.getIntArg(0)));


        }

        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }
}
