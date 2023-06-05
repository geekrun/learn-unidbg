package com.learn.unidbg.dachang.meituan;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.SystemPropertyHook;
import com.github.unidbg.linux.android.SystemPropertyProvider;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.ApplicationInfo;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.linux.file.ByteArrayFileIO;
import com.github.unidbg.linux.file.DirectoryFileIO;
import com.github.unidbg.linux.file.SimpleFileIO;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.github.unidbg.virtualmodule.android.JniGraphics;
import com.learn.unidbg.extend.utils.ExtendFileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class MeiTuan extends AbstractJni implements IOResolver {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmClass NBridge;

    MeiTuan() {
        emulator = AndroidEmulatorBuilder.for32Bit().
                setRootDir(ExtendFileUtils.loadFile("meituan","roofts")).
                setProcessName("com.sankuai.meituan").
                addBackendFactory(new Unicorn2Factory(false)).
                build(); // 创建模拟器实例

        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(ExtendFileUtils.loadFile("meituan","apk","mt.apk"));
        vm.setVerbose(true);
        new JniGraphics(emulator, vm).register(memory);
        new AndroidModule(emulator, vm).register(memory);
        emulator.getSyscallHandler().addIOResolver(this);
        emulator.getSyscallHandler().setEnableThreadDispatcher(true);
        emulator.getBackend().registerEmuCountHook(100000); // 设置执行多少条指令切换一次线程

        SystemPropertyHook systemPropertyHook = new SystemPropertyHook(emulator);
        systemPropertyHook.setPropertyProvider(new SystemPropertyProvider() {
            @Override
            public String getProperty(String key) {
                System.out.println("lilac systemkey:" + key);
                switch (key) {
                    case "ro.build.user": {
                        return "builder";
                    }
                    case "ro.build.display.id": {
                        return "QKQ1.190828.002 test-keys";
                    }
                    case "ro.build.host": {
                        return "c3-miui-ota-bd134.bj";
                    }
                    case "ro.build.version.sdk": {
                        return "29";
                    }
                    case "ro.product.cpu.abi": {
                        return "arm64-v8a";
                    }
                    case "ro.product.cpu.abilist64": {
                        return "arm64-v8a";
                    }
                    case "debug.atrace.tags.enableflags": {
                        return "0";
                    }
                }
                return "";
            }

            ;
        });
        memory.addHookListener(systemPropertyHook);
        DalvikModule dm = vm.loadLibrary("mtguard", true);
        vm.setJni(this);
        module = dm.getModule();
        dm.callJNI_OnLoad(emulator);
        NBridge = vm.resolveClass("com/meituan/android/common/mtguard/NBridge");
    }

    /* s must be an even-length string. */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public void callChain(){
        // so init
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 1, ArrayObject.newStringArray(vm, "9b69f861-e054-4bc4-9daf-d36ae205ed3e"));
        // so internal init
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 4, ProxyDvmObject.createObject(vm, new Object[1]));
        // 某种aes相关的操作
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 31, new ArrayObject(new ByteArray(vm, hexStringToByteArray("f6dd5f0eb4f9976531e8a9b9527ff753b51886fe3129115d1f020ee55d429fbfe25ab7f0484856befdde0f509c72babac0ef7aa150f3ae561145497658c6a76c286f58f36be59bcd15d88977d8ecac75e588faa05c4987cef0d08c741d4bee95")), new StringObject(vm, "aesKey")));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 31, new ArrayObject(new ByteArray(vm, hexStringToByteArray("ac8f2341b36f90b456c76858875f54a69b868dd47c2c1dfaf70259a04a043648392446459c4879f1cb1151460ca946ad60b08827ef150c93b790141d759571ed9a770f63e1b8bd3002d506ecaa54363d64293c86a382c642d59c4022ebc2d679")), new StringObject(vm, "aesKey")));
        // main1 的一次调用
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 49, ProxyDvmObject.createObject(vm, new Object[0]));
        // loadinitSuccess成功后，所执行的一个，即疑似又是一个初始化
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 3, ProxyDvmObject.createObject(vm, new Object[1]));

        // 未知，JADX中没搜到，大概是jADX bug
        //参数：53,new Object[]{316}
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 316)}));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 317)}));

        // UUIDListener 的前置逻辑
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 153)}));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 153)}));

        // LocationInfoListener 的前置逻辑
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 127)}));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 512)}));

        // 对battery change intent 的注册
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 24)}));

        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 29)}));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, ProxyDvmObject.createObject(vm, new Object[]{DvmInteger.valueOf(vm, 138)}));

        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 4, new ArrayObject());
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 1, new ArrayObject(new StringObject(vm, "e4709324d998c52231ad3e46b6387accdb619ca1a308ac7118721cfa3a097ed95c6a52cd52a59cdb84693ebf9b72cd7959f1cc9be00bdaaf8ee3a5cbcfa8184d3cf67876925281388db2d06bb02a892e56af63f8ab27e9d89cffd1739643cd1f")));
        // inSandBox
        System.out.println("call inSandBox method");
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 19, new ArrayObject());
        // hasMalware
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 17, new ArrayObject());
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 3, new ArrayObject());
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 153)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 51, new ArrayObject(new StringObject(vm, "4"), new StringObject(vm, "gxdDeMFEMIYo4q+OiwtePQ==")));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 51, new ArrayObject(new StringObject(vm, "8"), new StringObject(vm, "W+Sihy6djZU0L1vGI8Fsu1nVAaGg8DnJM06gICa7KreD/gsH4JHFucMe4OCJc/ZGEAhSmtqhBUX3Z5NYq+6mBQ==")));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 51, new ArrayObject(new StringObject(vm, "2"), new StringObject(vm, "3hw46f08WBBnksQbjU/Jm6jqmWfHVZFSHpLh6Vh+kdV4oYiXog75XnVk61btO24nmM/Kj2DnVbBA5FNNCKbBZsGqzljjezrFG2l2weOTHzZkACsZL0sybA1YG+yw7pj0t1/FAlpYrYX0vKX0Tb2+2z+61b20Yh7MaWXG9yjzO9IEKljQ/K6//mPh7DjcN/n9ubpdZjqXgdsOyZYCG4sExo+BBdy1lhBdWNUo72Ht9qD1w+1xHQlzwOfgfTSZilbnRZBr2oJl3nmd1ldRd8a12qfDZB1K/mhc3rpjYPbiWfuu2JCB/824GE53RVzzlHTDmKyxq84P5gqqIZDoZpucrw==")));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 51, new ArrayObject(new StringObject(vm, "7"), new StringObject(vm, "H9XS/opbnaKf30quHzxacoK5WcFsrV5ZSjDTQXwi9sKgKu3NcvnslGCq0QcRE8mWp+JhneYNkUvzH0e6PYx76637xFARXRGDgTnWcud9G6ZIJYAFQUhsKclypdDKnAn4B2ZRWoLszDS+iZE1zKg1so01ISI6jHv/O/el43lFkQNcWWRF/yGJOGp3ju4CgnxJy69HGhfYV9ibK/jlCCYNsdLb8BYCY5riwmlkahe1cM2IUmXL8Vzl4Nzac6wvJt/V")));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 51, new ArrayObject(new StringObject(vm, "1"), new StringObject(vm, "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\n" + "piOeTATXZ0iG2GX92o=")));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 153)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 317)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 316)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 127)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 512)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 24)));
        NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 53, new ArrayObject(DvmInteger.valueOf(vm, 29)));
    }

    public void callTarget(){
        StringObject arg1 = new StringObject(vm, "9b69f861-e054-4bc4-9daf-d36ae205ed3e");
        ByteArray arg2 = new ByteArray(vm, ("GET /abtest/v1/getDivideStrategies __reqTraceID=0bb38434-745f-41ed-a18b-80b1409fe3b3&app=group&ci=1&layerKeys=2%2C1772%2C1586&p_appid=10&platform=Android&userId=-1&userid=-1&utm_campaign=AgroupBgroupC0E0Ghomepage&utm_medium=android&utm_source=qq&utm_term=1100200205&uuid=0000000000000145B8AC3CDC1418AA8A9CB0D113706910000000000000451327&version_name=11.20.205").getBytes(StandardCharsets.UTF_8));
        ByteArray arg3 = new ByteArray(vm, "wmapi-mt.meituan.com".getBytes(StandardCharsets.UTF_8));
        ArrayObject retobj = NBridge.callStaticJniMethodObject(emulator, "main(I[Ljava/lang/Object;)[Ljava/lang/Object;", 2, new ArrayObject(arg1, arg2, arg3));
        String ret = retobj.getValue()[0].getValue().toString();
        System.out.println("callTarget result!!!!!!!!!!!!!!!!!!!!!!     ");
        System.out.println(ret);
    }

    public static void main(String[] args) {
        MeiTuan meiTuan = new MeiTuan();
        meiTuan.callChain();
        meiTuan.callTarget();
    }

    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("lilac open:"+pathname);
        if(pathname.equals("/data/app/com.sankuai.meituan-2nOCxLCJUl7lL3J_S7uSPA==/base.apk")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/apk/mt.apk"), pathname));
        }
        if(pathname.equals("/data/data/com.sankuai.meituan/files/.mtg_dfpid_com.sankuai.meituan")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/files/.mtg_dfpid_com.sankuai.meituan"), pathname));
        }
        if(pathname.equals("/system/bin/ls")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/files/ls"), pathname));
        }
        if(pathname.equals("/sys/class/power_supply/battery/temp")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/files/temp"), pathname));
        }
        if(pathname.equals("/sys/class/power_supply/battery/voltage_now")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/files/voltage_now"), pathname));
        }
        if(pathname.equals("/data/data/com.sankuai.meituan/files/.mtg_sequence")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/files/sequence.txt"), pathname));
        }
        if(pathname.equals("/proc/meminfo")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/files/meminfo.txt"), pathname));
        }
        if(pathname.equals("/proc/self/status")) {
            return FileResult.success(new ByteArrayFileIO(oflags, pathname, ("TracerPid: 0").getBytes()));
        }
        if(pathname.equals("/data/app/com.sankuai.meituan-1/lib/arm/libmtguard.so")){
            return FileResult.success(new SimpleFileIO(oflags, new File("common-extend/src/main/resources/meituan/apk/libmtguard.so"), pathname));
        }
        if(pathname.equals("/sys/devices/system/cpu/")){
            return FileResult.success(new DirectoryFileIO(oflags, pathname, new File("common-extend/src/main/resources/meituan/files/cpu")));
        }
        return null;
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "com/meituan/android/common/mtguard/NBridge->getClassLoader()Ljava/lang/ClassLoader;":{
                return vm.resolveClass("dalvik/system/PathClassLoader", vm.resolveClass("java/lang/ClassLoader")).newObject(null);
            }
            case "com/meituan/android/common/mtguard/NBridge->main2(I[Ljava/lang/Object;)Ljava/lang/Object;":{
                int cmd = vaList.getIntArg(0);
                System.out.println("cmd:"+cmd);
                if(cmd == 4){
                    //  return MTGuard.sPic;
                    return new StringObject(vm, "ms_com.sankuai.meituan");
                }
                if(cmd == 5){
                    // return MTGuard.sSec;
                    return new StringObject(vm, "ppd_com.sankuai.meituan.xbt");
                }
                if(cmd == 1){
                    // return MTGuard.sPackageName;
                    return new StringObject(vm, vm.getPackageName());
                }
                if(cmd == 2){
                    // return MTGuard.sSystemContext;
                    return vm.resolveClass("android.app.ContextImpl").newObject(null);
                }
                if(cmd == 6){
                    //return "5.9.8";
                    return new StringObject(vm, "5.9.8");
                }
                if(cmd == 41){
                    // MTGlibInterface.raptorFakeAPI((String) objArr[0], ((Integer) objArr[1]).intValue(), ((Integer) objArr[2]).intValue());
                    // return null;
                    // 这就是典型的side effect
                    return null;
                }
                if(cmd == 3){
                    // if (MTGuard.getAdapter() != null) {
                    //    return MTGuard.getAdapter().a;
                    //}
                    // maybe fix
                    return null;
//                    return vm.resolveClass("com.meituan.android.common.mtguard.c$a").newObject(null);
                }
                if(cmd == 51){
                    //StringBuilder sb = new StringBuilder();
                    //sb.append(b.b());
                    //return sb.toString();
                    return new StringObject(vm ,"2");
                }
                if(cmd == 8){
                    // return MTGuard.DfpId;
                    return new StringObject(vm, "06AA75F9F2B829AF0FFC710A9FB9FBBC1A98FB483CE9385AEBC43793".toLowerCase());
                }
                if(cmd == 40){
                    return new StringObject(vm, "6QJedZdk4LdXekn5lvCVqjT/rFEMS6suI0LCRjk5alM8z1tx++1hRKzQ7+cy9u+uIU+yxCgSnk33cPAGwhU7ZJkWvV76LbIZLHCbHqUwN+0=");
                }
                if(cmd == 32){
                    return new StringObject(vm, "{\"health\":2,\"level\":100,\"plugged\":1,\"present\":true,\"scale\":100,\"status\":2,\"telephony\":\"Li-poly\",\"temperature\":2,\"voltage\":4334}");
                }
                if(cmd == 19){
                    return new StringObject(vm, "ICM20690");
                }
                if(cmd == 17){
                    return new StringObject(vm, "qualcomm");
                }
                if(cmd == 9){
                    return new StringObject(vm, "0");
                }
                if(cmd == 26){
                    return new StringObject(vm, "[2,100]");
                }
                if(cmd == 37){
                    return new StringObject(vm, "");
                }
                if(cmd == 13){
                    return new StringObject(vm, "qualcomm");
                }
                if(cmd == 11){
                    // AccessibilityUtils.isAccessibilityEnable(MTGuard.getAdapter().a) ? "1" : "0";
                    return new StringObject(vm, "0");
                }
                if(cmd == 28){
                    // AppInfoWorker.getFirstLaunchTime(MTGuard.getAdapter().a);
                    return new StringObject(vm, "1653727513999");
                }
                if(cmd == 29){
                    // n.a(MTGuard.getAdapter().a).a();
                    return new StringObject(vm, "00000000000006AC246A1664949EBB0A4F2714A58851FA160546669715806613");
                }
                if(cmd == 46){
                    // i.a(MTGuard.getAdapter().a).b();
                    return new StringObject(vm, "0.0000000000|0.0000000000");
                }
                if(cmd == 34){
                    // l.b().toString();
                    return new StringObject(vm, "[]");
                }
                if(cmd == 35){
                    // l.c().toString();
                    return new StringObject(vm, "[]");
                }
                if(cmd == 58){
                    // MTGuard.getAdapter().c();
                    return new StringObject(vm, "");
                }
                if(cmd == 61){
                    return null;
                }
                break;
            }
            case "java/lang/System->getProperty(Ljava/lang/String;)Ljava/lang/String;":{
                String propertyName = vaList.getObjectArg(0).getValue().toString();
                System.out.println(propertyName);
                if(propertyName.equals("java.io.tmpdir")){
                    return new StringObject(vm, "/data/user/0/com.sankuai.meituan/cache");
                }
                if(propertyName.equals("http.proxyHost")){
                    return null;
                }
                if(propertyName.equals("https.proxyHost")){
                    return null;
                }
                if(propertyName.equals("http.proxyPort")){
                    return null;
                }
                if(propertyName.equals("https.proxyPort")){
                    return null;
                }
            }
            case "android/os/SystemProperties->get(Ljava/lang/String;)Ljava/lang/String;":{
                String propertName = vaList.getObjectArg(0).getValue().toString();
                System.out.println(propertName);
                if(propertName.equals("ro.build.id")){
                    return new StringObject(vm, "QKQ1.190828.002");
                }
                if(propertName.equals("persist.sys.usb.config")){
                    return new StringObject(vm, "adb");
                }
                if(propertName.equals("sys.usb.config")){
                    return new StringObject(vm, "adb");
                }
                if(propertName.equals("sys.usb.state")){
                    return new StringObject(vm, "adb");
                }
                if(propertName.equals("ro.debuggable")){
                    return new StringObject(vm, "0");
                }
                if(propertName.equals("gsm.sim.state")){
                    return new StringObject(vm, "ABSENT,ABSENT");
                }
                if(propertName.equals("gsm.version.ril-impl")){
                    return new StringObject(vm, "Qualcomm RIL 1.0");
                }
                if(propertName.equals("ro.secure")){
                    return new StringObject(vm, "1");
                }
                if(propertName.equals("wifi.interface")){
                    return new StringObject(vm, "wlan0");
                }
            }
            case "java/lang/ClassLoader->getSystemClassLoader()Ljava/lang/ClassLoader;":{
                return vm.resolveClass("java/lang/ClassLoader").newObject(null);
            }
            case "java/net/NetworkInterface->getNetworkInterfaces()Ljava/util/Enumeration;":{
                // 真实情况这个数组要长很多
                String[] NetworkInterfaceNameList = new String[]{"dummy0","r_rmnet_data2","r_rmnet_data3","ip_vti0","wlan0","wlan1"};
                int length = NetworkInterfaceNameList.length;
                List<DvmObject<?>> NetworkInterfacelist = new ArrayList<>();

                for (int i = 0; i < length; i++) {
                    NetworkInterfacelist.add(vm.resolveClass("java/net/NetworkInterface").newObject(NetworkInterfaceNameList[i]));
                }

                return new Enumeration(vm,  NetworkInterfacelist);
            }
            case "java/util/Collections->list(Ljava/util/Enumeration;)Ljava/util/ArrayList;":
                return new ArrayListObject(vm, (List<? extends DvmObject<?>>) vaList.getObjectArg(0).getValue());
            case "android/os/Environment->getExternalStorageDirectory()Ljava/io/File;":{
                return vm.resolveClass("java/io/File").newObject(signature);
            }
            case "android/os/Environment->getDataDirectory()Ljava/io/File;":{
                return vm.resolveClass("java/io/File").newObject(signature);
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "java/lang/ClassLoader->loadClass(Ljava/lang/String;)Ljava/lang/Class;":{
                String clssName = vaList.getObjectArg(0).getValue().toString();
                System.out.println(clssName);
                if(clssName.equals("com/meituan/android/common/mtguard/NBridge")){
                    return vm.resolveClass(clssName);
                }
                if(clssName.equals("de.robv.android.xposed.XposedBridge")){
                    emulator.getDalvikVM().throwException(vm.resolveClass("java/lang/NoClassDefFoundError").newObject(clssName));
                    return null;
                }
                if(clssName.equals("com/meituan/android/privacy/interfaces/PermissionGuard")){
                    return vm.resolveClass(clssName);
                }
            }
            case "android/app/ContextImpl->getPackageManager()Landroid/content/pm/PackageManager;":{
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
            }
            case "android/content/pm/PackageManager->getApplicationInfo(Ljava/lang/String;I)Landroid/content/pm/ApplicationInfo;":{
                String appName = vaList.getObjectArg(0).getValue().toString();
                if(appName.equals("com.sankuai.meituan")){
                    return new ApplicationInfo(vm);
                }
            }
            case "com/meituan/android/common/mtguard/c$a->getPackageManager()Landroid/content/pm/PackageManager;":{
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
            }
            case "com/meituan/android/common/mtguard/c$a->getFilesDir()Ljava/io/File;":{
                return vm.resolveClass("java/io/File").newObject(null);
            }
            case "java/util/ArrayList->iterator()Ljava/util/Iterator;":{
                ArrayList<?> arrayList = (ArrayList<?>) dvmObject.getValue();
                return vm.resolveClass("java/util/Iterator").newObject(arrayList.iterator());
            }
            case "java/util/Iterator->next()Ljava/lang/Object;":{
                Iterator<?> iterator = (Iterator<?>) dvmObject.getValue();
                return vm.resolveClass("java/net/NetworkInterface").newObject(iterator.next());
            }
            case "java/net/NetworkInterface->getName()Ljava/lang/String;":{
                return new StringObject(vm, dvmObject.getValue().toString());
            }
            case "android/app/ContextImpl->getResources()Landroid/content/res/Resources;":{
                return vm.resolveClass("android/content/res/Resources").newObject(null);
            }
            case "android/content/res/Resources->getConfiguration()Landroid/content/res/Configuration;":{
                return vm.resolveClass("android/content/res/Configuration").newObject(null);
            }
            case "android/app/ContextImpl->getSystemService(Ljava/lang/String;)Ljava/lang/Object;":{
                String SystemServiceName = vaList.getObjectArg(0).getValue().toString();
                System.out.println(SystemServiceName);
                if(SystemServiceName.equals("display")){
                    return vm.resolveClass("android.hardware.display.DisplayManager").newObject(null);
                }
                if(SystemServiceName.equals("audio")){
                    return vm.resolveClass("android.media.AudioManager").newObject(null);
                }
                if(SystemServiceName.equals("location")){
                    return vm.resolveClass("android.location.LocationManager").newObject(null);
                }
            }
            case "android/hardware/display/DisplayManager->getDisplay(I)Landroid/view/Display;":{
                return vm.resolveClass("android/view/Display").newObject(null);
            }
            case "java/io/File->getPath()Ljava/lang/String;":{
                String pathSource = dvmObject.getValue().toString();
                if(pathSource.contains("getExternalStorageDirectory")){
                    return new StringObject(vm, "/storage/emulated/0");
                }
                if(pathSource.contains("getDataDirectory")){
                    return new StringObject(vm, "/data");
                }
            }
            case "android/app/ContextImpl->getContentResolver()Landroid/content/ContentResolver;":{
                return vm.resolveClass("android/content/ContentResolver").newObject(null);
            }
            case "java/io/File->listFiles()[Ljava/io/File;":{
                return null;
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/content/pm/ApplicationInfo->sourceDir:Ljava/lang/String;":{
                return new StringObject(vm, "/data/app/com.sankuai.meituan-2nOCxLCJUl7lL3J_S7uSPA==/base.apk");
            }
            case "android/content/res/Configuration->locale:Ljava/util/Locale;":{
                return ProxyDvmObject.createObject(vm, Locale.getDefault());
            }
        }
        return super.getObjectField(vm, dvmObject, signature);
    }

    @Override
    public int getIntField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/content/pm/PackageInfo->versionCode:I":{
                return 1100200205;
            }
            case "android/util/DisplayMetrics->widthPixels:I":{
                return 1080;
            }
            case "android/util/DisplayMetrics->heightPixels:I":{
                return 2160;
            }
        }
        return super.getIntField(vm, dvmObject, signature);
    }

    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "java/lang/Integer-><init>(I)V":{
                return DvmInteger.valueOf(vm, vaList.getIntArg(0));
            }
            case "java/io/File-><init>(Ljava/lang/String;)V":{
                return vm.resolveClass("java/io/File").newObject(null);
            }
            case "java/lang/Boolean-><init>(Z)V":{
                boolean b;
                b = vaList.getIntArg(0) != 0;
                return DvmBoolean.valueOf(vm, b);
            }
            case "android/util/DisplayMetrics-><init>()V":{
                return dvmClass.newObject(null);
            }
            case "android/os/StatFs-><init>(Ljava/lang/String;)V":{
                String path = vaList.getObjectArg(0).getValue().toString();
                return dvmClass.newObject(path);
            }
        }
        return super.newObjectV(vm, dvmClass, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "java/io/File->canRead()Z":{
                return false;
            }
            case "android/content/pm/PackageManager->hasSystemFeature(Ljava/lang/String;)Z":{
                String feature = vaList.getObjectArg(0).getValue().toString();
                System.out.println(feature);
                if(feature.equals("android.hardware.bluetooth")){
                    return true;
                }
                if(feature.equals("android.hardware.location.gps")){
                    return true;
                }
                if(feature.equals("gsm.sim.state")){
                    return true;
                }
            }
            case "java/net/NetworkInterface->isUp()Z":{
                return true;
            }
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature){
            case "android/os/Build->BRAND:Ljava/lang/String;":{
                return new StringObject(vm, "Xiaomi");
            }
            case "android/os/Build->TYPE:Ljava/lang/String;":{
                return new StringObject(vm, "user");
            }
            case "android/os/Build->HARDWARE:Ljava/lang/String;":{
                return new StringObject(vm, "qcom");
            }
            case "android/os/Build->MODEL:Ljava/lang/String;":{
                return new StringObject(vm, "MIX 2S");
            }
            case "android/os/Build->TAGS:Ljava/lang/String;":{
                return new StringObject(vm, "release-keys");
            }
            case "android/os/Build$VERSION->RELEASE:Ljava/lang/String;":{
                return new StringObject(vm, "10");
            }
            case "android/os/Build->BOARD:Ljava/lang/String;":{
                return new StringObject(vm, "sdm845");
            }
            case "android/os/Build->MANUFACTURER:Ljava/lang/String;":{
                return new StringObject(vm, "Xiaomi");
            }
            case "android/os/Build->PRODUCT:Ljava/lang/String;":{
                return new StringObject(vm, "polaris");
            }
            case "android/os/Build->SUPPORTED_ABIS:[Ljava/lang/String;":{
                return ArrayObject.newStringArray(vm, "arm64-v8a", "armeabi-v7a", "armeabi");
            }
            case "android/os/Build->DEVICE:Ljava/lang/String;":{
                return new StringObject(vm, "polaris");
            }
            case "android/os/Build->HOST:Ljava/lang/String;":{
                return new StringObject(vm, "c3-miui-ota-bd134.bj");
            }
            case "com/meituan/android/privacy/interfaces/PermissionGuard->PERMISSION_PHONE_READ:Ljava/lang/String;":{
                return new StringObject(vm, "Phone.read");
            }
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

    @Override
    public int getStaticIntField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature){
            // 获取Android版本
            case "android/os/Build$VERSION->SDK_INT:I":{
                return 29;
            }
            case "android/content/pm/PackageManager->GET_SIGNATURES:I":{
                return 64;
            }
        }
        return super.getStaticIntField(vm, dvmClass, signature);
    }

    @Override
    public int callStaticIntMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "com/meituan/android/common/mtguard/NBridge->getPermissionState(Ljava/lang/String;)I":{
                String arg = vaList.getObjectArg(0).getValue().toString();
                System.out.println(arg);
                switch (arg){
                    case "android.permission.ACCESS_NETWORK_STATE":{
                        return 3;
                    }
                    case "android.permission.ACCESS_WIFI_STATE":{
                        return 3;
                    }
                }

            }
            case "com/meituan/android/common/mtguard/NBridge->getPermissionState(Ljava/lang/String;Ljava/lang/String;)I":{
                return 2;
            }
            case "android/provider/Settings$System->getInt(Landroid/content/ContentResolver;Ljava/lang/String;)I":{
                String arg = vaList.getObjectArg(1).getValue().toString();
                System.out.println(arg);
                // https://developer.android.com/reference/android/provider/Settings.System#SCREEN_BRIGHTNESS
                if (arg.equals("screen_brightness")) {
                    return 45;
                }
            }
        }
        return super.callStaticIntMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "android/view/Display->getRealMetrics(Landroid/util/DisplayMetrics;)V":{
                return;
            }
            case "android/location/LocationManager->removeTestProvider(Ljava/lang/String;)V":{
                return;
            }
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public int callIntMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "android/os/StatFs->getBlockSize()I":{
                return 4096;
            }
            case "android/os/StatFs->getBlockCount()I":{
                return 29048414;
            }
            case "android/view/Display->getStreamVolume(I)I":{
                return 6;
            }
            case "android/view/Display->getStreamMaxVolume(I)I":{
                return 15;
            }
            case "android/view/Display->getCallState()I":{
                //  "电话状态[0 无活动/1 响铃/2 摘机]"
                return 0;
            }
            case "android/media/AudioManager->getStreamVolume(I)I":{
                return 10;
            }
            case "android/media/AudioManager->getStreamMaxVolume(I)I":{
                return 15;
            }
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    // 应该没有副作用
    @Override
    public long callStaticLongMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "java/lang/System->currentTimeMillis()J":{
                return 1655191868L;
                //return System.currentTimeMillis();
            }
        }
        return super.callStaticLongMethodV(vm, dvmClass, signature, vaList);
    }

    // 应该没有副作用
    @Override
    public boolean callStaticBooleanMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature){
            case "android/os/Debug->isDebuggerConnected()Z":{
                return false;
            }
        }
        return super.callStaticBooleanMethodV(vm, dvmClass, signature, vaList);
    }
}
