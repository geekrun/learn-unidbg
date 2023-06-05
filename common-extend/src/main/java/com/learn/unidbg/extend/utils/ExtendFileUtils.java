package com.learn.unidbg.extend.utils;

import java.io.File;

public class ExtendFileUtils {


    public static File loadApkFile(String apkName){
        return loadApkFile(apkName,"apk");
    }


    public static File loadApkFile(String apkName,String apkType){
        return new File(String.format("common-extend/src/main/resources/%s/%s", apkType,apkName));
    }

    public static File loadFile(String apkTopDir,String dirName,String fileName){
        return new File(String.format("common-extend/src/main/resources/%s/%s/%s", apkTopDir,dirName,fileName));

    }

    public static File loadFile(String apkTopDir,String dirName){
        return new File(String.format("common-extend/src/main/resources/%s/%s", apkTopDir,dirName));

    }


}


