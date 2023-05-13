package com.learn.unidbg.extend;


import com.github.unidbg.linux.android.dvm.VM;
import com.learn.unidbg.extend.jni.AndroidEnvJni;

import java.util.HashMap;

public class SuperExtendAbstractJni extends AndroidEnvJni {





    public void printApkInfo(VM vm){

        var map= new HashMap<String,String>();
        map.put("包名",vm.getPackageName());
        map.put("版本",vm.getVersionName());
        System.out.println(map);
    }


}
