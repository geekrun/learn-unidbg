package com.learn.unidbg.liac.call_jni.simple;

/*
package com.weibo.xvideo;

import org.jetbrains.annotations.NotNull;

public final class NativeApi {
    public NativeApi() {
        System.loadLibrary("oasiscore");
    }

    @NotNull
    public final native String d(@NotNull String str);

    @NotNull
    public final native String dg(@NotNull String str, boolean z);

    @NotNull
    public final native String e(@NotNull String str);

    @NotNull
    public final native String s(@NotNull byte[] bArr, boolean z);
}

 */


/*

Java.perform(function() {
  function stringToBytes(str) {
    var javaString = Java.use('java.lang.String');
    return javaString.$new(str).getBytes();
  }

  let NativeApi = Java.use("com.weibo.xvideo.NativeApi");
  let arg1 = "aid=01A-khBWIm48A079Pz_DMW6PyZR8uyTumcCNm4e8awxyC2ANU.&cfrom=28B5295010&cuid=5999578300&noncestr=46274W9279Hr1X49A5X058z7ZVz024&platform=ANDROID&timestamp=1621437643609&ua=Xiaomi-MIX2S__oasis__3.5.8__Android__Android10&version=3.5.8&vid=1019013594003&wm=20004_90024";
  let arg2 = false;
  let ret = NativeApi.$new().s(stringToBytes(arg1), arg2);
  console.log("ret:"+ret);
})
 */

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.learn.unidbg.extend.SuperExtendAbstractJni;
import com.learn.unidbg.extend.utils.ExtendFileUtils;

import java.nio.charset.StandardCharsets;

public class Lvzhou extends SuperExtendAbstractJni {
    AndroidEmulator androidEmulator;

    VM vm;


    Memory memory;
    DalvikModule dm;


    public void run() {


        androidEmulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.weibo.xvideo.NativeApi").build();

        vm = androidEmulator.createDalvikVM(ExtendFileUtils.loadApkFile("lvzhou3.5.8.apk"));
        vm.setJni(this);
        vm.setVerbose(true);


        memory = androidEmulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));


        dm = vm.loadLibrary("oasiscore", true);
        dm.callJNI_OnLoad(androidEmulator);

        String arg1 = "aid=01A-khBWIm48A079Pz_DMW6PyZR8uyTumcCNm4e8awxyC2ANU.&cfrom=28B5295010&cuid=5999578300&noncestr=46274W9279Hr1X49A5X058z7ZVz024&platform=ANDROID&timestamp=1621437643609&ua=Xiaomi-MIX2S__oasis__3.5.8__Android__Android10&version=3.5.8&vid=1019013594003&wm=20004_90024";
        DvmObject<?> obj = vm.resolveClass("com.weibo.xvideo.NativeApi").newObject(null);
        String result = obj.callJniMethodObject(androidEmulator, "s([BZ)Ljava/lang/String;", arg1.getBytes(StandardCharsets.UTF_8), false).getValue().toString();
        System.out.printf(String.format("result %s", result));
        printApkInfo(vm);
    }


    public static void main(String[] args) {

        new Lvzhou().run();
    }


}
