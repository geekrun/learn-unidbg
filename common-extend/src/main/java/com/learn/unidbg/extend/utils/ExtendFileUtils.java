package com.learn.unidbg.extend.utils;

import java.io.File;

public class ExtendFileUtils {


    public static File loadApkFile(String apkName){
        return new File("lilac-sample/src/main/resources/apk/"+apkName);
    }


    public static File loadApkFile(String apkName,String apkType){
        return new File(String.format("lilac-sample/src/main/resources/%s/%s", apkType,apkName));
    }
}
