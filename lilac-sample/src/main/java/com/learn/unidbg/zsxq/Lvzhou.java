package com.learn.unidbg.zsxq;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Lvzhou  extends AbstractJni {

    public void run() {
        //1.创建Android模拟器实例
        AndroidEmulator emulator = AndroidEmulatorBuilder
                .for32Bit().setProcessName("com.sina.oasis")
                .addBackendFactory(new DynarmicFactory(true))
                .build();

        //2.获取操作内存的接口
        Memory memory = emulator.getMemory();

        //3.设置库解析器(SDK版本)
        memory.setLibraryResolver(new AndroidResolver(23));

        //4.创建虚拟机
        VM vm = emulator.createDalvikVM(new File("lilac-sample/src/main/resources/lvzhou.apk"));
        vm.setJni(this);

        //5.加载ELF文件
        Module module = emulator.loadLibrary(new File("lilac-sample/src/main/resources/liboasiscore.so"), true);

        //6.调用JNI_OnLoad
        vm.callJNI_OnLoad(emulator, module);


        //7.模拟执行目标函数
        //创建一个类的实例对象
        DvmObject<?> obj = vm.resolveClass("com/weibo/xvideo/NativeApi").newObject(null);
        String arg1 = "aid=01A-khBWIm48A079Pz_DMW6PyZR8uyTumcCNm4e8awxyC2ANU.&cfrom=28B5295010&cuid=5999578300&noncestr=46274W9279Hr1X49A5X058z7ZVz024&platform=ANDROID&timestamp=1621437643609&ua=Xiaomi-MIX2S__oasis__3.5.8__Android__Android10&version=3.5.8&vid=1019013594003&wm=20004_90024";
        String result= obj.callJniMethodObject(emulator,"s([BZ)Ljava/lang/String;",arg1.getBytes(StandardCharsets.UTF_8),false).toString();


        //9.打印执行结果
        System.out.println("执行结果:" + result);
    }





    public static void main(String[] args) {
        new Lvzhou().run();
    }
}
