package com.learn.unidbg.liac.zsxq;


/*
Java.perform(function() {
  let LibBili = Java.use("com.bilibili.nativelibrary.LibBili");
  let Map = Java.use('java.util.HashMap');
  LibBili["g"].implementation = function (map) {
    console.log('g is called' + ', ' + 'map: ' + Java.cast(map, Map));
    let ret = this.g(map);
    console.log('g ret value is ' + ret);
    return ret;
  };
})


function callS(){
  Java.perform(function() {
    let LibBili = Java.use("com.bilibili.nativelibrary.LibBili");
    let TreeMap = Java.use("java.util.TreeMap");
    var map = TreeMap.$new();

    map.put("build", "6180500");
    map.put("mobi_app", "android")
    map.put("channel", "shenma069")
    map.put("appkey", "1d8b6e7d45233436")
    map.put("s_locale", "zh_CN")

    let result = LibBili.s(map);
    console.log("ret:"+result);
  })
}



 */

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmClass;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.memory.Memory;
import com.learn.unidbg.liac.util.SignedQuery;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;


public class Bili extends AbstractJni implements IOResolver {


    AndroidEmulator androidEmulator;

    VM vm = null;


    Memory memory = null;
    DalvikModule dm;


    private void run() {

        androidEmulator = AndroidEmulatorBuilder.for32Bit().setProcessName("tv.danmaku.bili").addBackendFactory(new Unicorn2Factory(true)).build();
        androidEmulator.getBackend().registerEmuCountHook(10000); // 设置执行多少条指令切换一次线程
        androidEmulator.getSyscallHandler().setEnableThreadDispatcher(true);
        androidEmulator.getSyscallHandler().addIOResolver(this);
        vm = androidEmulator.createDalvikVM(new File("lilac-sample/src/main/resources/bilibili.apk"));

        vm.setJni(this);
        vm.setVerbose(true);
        memory = androidEmulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        dm = vm.loadLibrary("bili", true);
        dm.callJNI_OnLoad(androidEmulator);


        DvmClass dvmClass = vm.resolveClass("com.bilibili.nativelibrary.LibBili");
        TreeMap<String, String> map = new TreeMap<>();
        map.put("build", "6180500");
        map.put("mobi_app", "android");
        map.put("channel", "shenma069");
        map.put("appkey", "1d8b6e7d45233436");
        map.put("s_locale", "zh_CN");
        String result = dvmClass.callStaticJniMethodObject(androidEmulator, "s(Ljava/util/SortedMap;)Lcom/bilibili/nativelibrary/SignedQuery;", ProxyDvmObject.createObject(vm, map)).getValue().toString();
        System.out.printf(String.format("result======= %s", result));


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

        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }


    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {

        switch (signature) {
            case "com/bilibili/nativelibrary/SignedQuery->r(Ljava/util/Map;)Ljava/lang/String;":
                Map map = (Map) varArg.getObjectArg(0).getValue();
                var rr = SignedQuery.m79203c(map);
                return new StringObject(vm, rr);


        }
        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }


    @Override
    public DvmObject<?> newObject(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "com/bilibili/nativelibrary/SignedQuery-><init>(Ljava/lang/String;Ljava/lang/String;)V":
                String arg1 = varArg.getObjectArg(0).getValue().toString();
                String arg2 = varArg.getObjectArg(1).getValue().toString();
                return vm.resolveClass("com/bilibili/nativelibrary/SignedQuery").newObject(new SignedQuery(arg1, arg2));

        }


        return super.newObject(vm, dvmClass, signature, varArg);


    }

    public static void main(String[] args) {
        new Bili().run();
    }

    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("lilac open:"+pathname);
        if(pathname.equals("/proc/self/cmdline")){
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, "tv.danmaku.bili\0".getBytes(StandardCharsets.UTF_8)));
        }
        return null;
    }
}
