package com.learn.unidbg.extend.jni;

import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;

import javax.crypto.Mac;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StandardJavaJni extends AbstractJni {


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


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
                return ProxyDvmObject.createObject(vm, map.get(varArg.getObjectArg(0).getValue()));
            case "java/util/Map->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;":
                Map map1 = (Map) dvmObject.getValue();
                var key1 = varArg.getObjectArg(0).getValue();
                var val1 = varArg.getObjectArg(1).getValue();
                return ProxyDvmObject.createObject(vm, map1.put(key1, val1));

            case "java/lang/StringBuilder->toString()Ljava/lang/String;":
                StringBuilder stringBuilder = (StringBuilder) dvmObject.getValue();
                return new StringObject(vm, stringBuilder.toString());
            case "java/security/MessageDigest->digest()[B":
                MessageDigest messageDigest = (MessageDigest) dvmObject.getValue();
                return new ByteArray(vm, messageDigest.digest());
            case "java/security/KeyStore->getKey(Ljava/lang/String;[C)Ljava/security/Key;":
                return vm.resolveClass("java/security/Key").newObject(null);
            case "java/util/HashMap->toString()Ljava/lang/String;": {
                HashMap hashMap = (HashMap) dvmObject.getValue();
                return new StringObject(vm, hashMap.toString());
            }
            case "java/lang/String->getBytes()[B": {
                String str = (String) dvmObject.getValue();
                return new ByteArray(vm, str.getBytes());
            }
            case "java/util/HashMap->get(Ljava/lang/Object;)Ljava/lang/Object;": {
                HashMap hashMap = (HashMap) dvmObject.getValue();
                String key = varArg.getObjectArg(0).getValue().toString();
                System.out.println("key:" + key);
                Object value = hashMap.get(key);
                if (value != null) {
                    return new StringObject(vm, value.toString());
                } else {
                    return null;
                }
            }
            case "java/util/HashMap->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;": {
                HashMap hashMap = new HashMap();
                String key = varArg.getObjectArg(0).getValue().toString();
                String value = varArg.getObjectArg(1).getValue().toString();
                hashMap.put(key, value);
                return dvmObject;
            }
            case "javax/crypto/Mac->doFinal()[B":
//                Mac mac=(Mac) dvmObject.getValue();
//                return new ByteArray(vm,mac.doFinal());
                //todo
                return new ByteArray(vm, hexStringToByteArray("d9f19cc21bf1c952f4c244b4a2ceaafa388e9d3d6eb90ec618d01bee4bb79424"));

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
            case "java/lang/StringBuilder->append(I)Ljava/lang/StringBuilder;":
                return ProxyDvmObject.createObject(vm, ((StringBuilder) dvmObject.getValue()).append(vaList.getIntArg(0)));


        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }


    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/String->valueOf(I)Ljava/lang/String;":
                return new StringObject(vm, String.valueOf(vaList.getIntArg(0)));

        }

        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }


    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "java/security/MessageDigest->getInstance(Ljava/lang/String;)Ljava/security/MessageDigest;":
                try {
                    var messageDigest = MessageDigest.getInstance(varArg.getObjectArg(0).getValue().toString());
                    return ProxyDvmObject.createObject(vm, messageDigest);
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            case "java/security/KeyStore->getInstance(Ljava/lang/String;)Ljava/security/KeyStore;":
                String type = (String) varArg.getObjectArg(0).getValue();
                try {
                    return ProxyDvmObject.createObject(vm, KeyStore.getInstance(type));
                } catch (KeyStoreException e) {
                    return vm.resolveClass(dvmClass.getClassName()).newObject(type);
                }
            case "javax/crypto/Mac->getInstance(Ljava/lang/String;)Ljavax/crypto/Mac;":
                try {
                    var cc = ProxyDvmObject.createObject(vm, Mac.getInstance(varArg.getObjectArg(0).getValue().toString()));
                    return cc;
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }

        }
        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }


    @Override
    public void callVoidMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {

        switch (signature) {
            case "java/security/MessageDigest->update([B)V":
                MessageDigest messageDigest = (MessageDigest) dvmObject.getValue();
                messageDigest.update((byte[]) varArg.getObjectArg(0).getValue());
                return;
            case "java/security/KeyStore->load(Ljava/security/KeyStore$LoadStoreParameter;)V":
//                try {
//                    ((KeyStore) dvmObject.getValue()).load((KeyStore.LoadStoreParameter) varArg.getObjectArg(0));
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                } catch (NoSuchAlgorithmException e) {
//                    throw new RuntimeException(e);
//                } catch (CertificateException e) {
//                    throw new RuntimeException(e);
//                }
                return;
            case "javax/crypto/Mac->init(Ljava/security/Key;)V":
//                try {
//                    ( (Mac) dvmObject.getValue()).init((Key) varArg.getObjectArg(0));
//                } catch (InvalidKeyException e) {
//                    throw new RuntimeException(e);
//                }
                return;
            case "javax/crypto/Mac->update([B)V": {
//                ( (Mac) dvmObject.getValue()).update((byte[]) varArg.getObjectArg(0).getValue());
                return;
            }
        }
        super.callVoidMethod(vm, dvmObject, signature, varArg);
    }


    @Override
    public int callIntMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "java/util/List->size()I":
                return ((List) dvmObject.getValue()).size();
        }
        return super.callIntMethod(vm, dvmObject, signature, varArg);
    }
}
