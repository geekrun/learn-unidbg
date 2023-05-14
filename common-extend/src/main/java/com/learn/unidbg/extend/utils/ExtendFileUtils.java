package com.learn.unidbg.extend.utils;

import java.io.File;

public class ExtendFileUtils {


    public static File loadApkFile(String apkName){
        return loadApkFile(apkName,"apk");
    }


    public static File loadApkFile(String apkName,String apkType){
        return new File(String.format("common-extend/src/main/resources/%s/%s", apkType,apkName));
    }
}
