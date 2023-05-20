package com.learn.unidbg.middem.lamoda;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.utils.Inspector;
import com.learn.unidbg.extend.SuperExtendAbstractJni;
import com.learn.unidbg.extend.utils.ExtendFileUtils;

import java.util.HashMap;
import java.util.Map;

public class Signer extends SuperExtendAbstractJni implements IOResolver {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    private final DvmObject NativeLibHelper;
    Signer() {
        // 创建模拟器实例
        emulator = AndroidEmulatorBuilder
                .for64Bit() // 我选择分析 ARM64 的 SO
                .setProcessName("com.lamoda.lite") // 传入进程名
                .build();
        // 获取模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机,传入APK，Unidbg可以替我们做部分签名校验的工作
        vm = emulator.createDalvikVM(ExtendFileUtils.loadApkFile("com.lamoda.lite@0@.apk"));
        vm.setJni(this); // 设置JNI
        vm.setVerbose(true); // 打印日志
        emulator.getSyscallHandler().addIOResolver(this);// 设置文件处理器
        DalvikModule dm = vm.loadLibrary("signer", true); // 加载 libsigner.so，Unidbg 会到 apk 的 lib/arm64-v8a 下寻找。
        module = dm.getModule(); //获取目标模块的句柄
        dm.callJNI_OnLoad(emulator); // 调用目标 SO 的 JNI_OnLoad
        // 构造调用目标函数的对象
        NativeLibHelper = vm.resolveClass("com.adjust.sdk.sig.NativeLibHelper").newObject(null);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main(String[] args) {
        Signer signer = new Signer();
        signer.callNsign();
    }

    ;

    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("file open:" + pathname);
        return null;
    }

    public byte[] callNsign() {
        // arg1
        DvmObject context = vm.resolveClass("com.lamoda.lite.Application", vm.resolveClass("android/content/Context")).newObject(null);
        // arg2
        Map<String, String> map = new HashMap<>();
        map.put("api_level", "29");
        map.put("event_buffering_enabled", "0");
        map.put("hardware_name", "QKQ1.190828.002 test-keys");
        map.put("partner_params", "{\"partner_id\":\"lamodaby\"}");
        map.put("app_version", "4.25.0");
        map.put("app_token", "bnrkfzymzyjp");
        map.put("event_count", "11");
        map.put("session_length", "1018");
        map.put("created_at", "2023-04-02T12:43:23.486Z+0800");
        map.put("device_type", "phone");
        map.put("language", "ru");
        map.put("connectivity_type", "1");
        map.put("mcc", "460");
        map.put("device_manufacturer", "Xiaomi");
        map.put("display_width", "1080");
        map.put("event_token", "oapsei");
        map.put("time_spent", "1014");
        map.put("device_name", "MIX 2S");
        map.put("needs_response_details", "1");
        map.put("os_build", "QKQ1.190828.002");
        map.put("cpu_type", "arm64-v8a");
        map.put("screen_size", "normal");
        map.put("screen_format", "long");
        map.put("subsession_count", "2");
        map.put("secret_id", "4");
        map.put("mnc", "01");
        map.put("os_version", "10");
        map.put("callback_params", "{\"uid\":\"AE024064EF032964D3106E5402F60208\",\"device_model\":\"MIX 2S\",\"app_version\":\"4.25.0\",\"lid\":\"ZEACrmQpA+9UbhDTCAL2AgA=\",\"device_group\":\"Phone\",\"shop_country\":\"BY\",\"display_size\":\"5,2\",\"device_manufacturer\":\"Xiaomi\"}");
        map.put("android_uuid", "b124840b-e2bc-4c96-afad-5c1f13aa8ed8");
        map.put("environment", "production");
        map.put("screen_density", "high");
        map.put("attribution_deeplink", "1");
        map.put("session_count", "1");
        map.put("display_height", "2030");
        map.put("package_name", "com.lamoda.lite");
        map.put("os_name", "android");
        map.put("android_id", "9b8f038015568dfb");
        map.put("app_secret", "187957353611902444387878081751625741774");
        map.put("ui_mode", "1");
        map.put("activity_kind", "event");
        map.put("client_sdk", "android4.33.2");
        DvmObject mapArg = ProxyDvmObject.createObject(vm, map);
        // arg3
        ByteArray barr = new ByteArray(vm, hexStringToByteArray("d9f19cc21bf1c952f4c244b4a2ceaafa388e9d3d6eb90ec618d01bee4bb79424"));
        // arg4
        int i = 29;
        byte[] ret = (byte[]) NativeLibHelper.callJniMethodObject(emulator, "nSign", context, mapArg, barr, i).getValue();
        Inspector.inspect(ret, "result");
        return ret;
    }

    @Override
    public DvmObject<?> callStaticObjectMethod(BaseVM vm, DvmClass dvmClass, String signature, VarArg varArg) {
        switch (signature) {
            case "javax/crypto/Mac->doFinal()[B": {
                return new ByteArray(vm, hexStringToByteArray("d9f19cc21bf1c952f4c244b4a2ceaafa388e9d3d6eb90ec618d01bee4bb79424"));
            }
        }
        return super.callStaticObjectMethod(vm, dvmClass, signature, varArg);
    }
}