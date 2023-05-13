package com.learn.unidbg.extend.utils;

import java.io.File;

public class ExtendFileUtils {


    public static File loadApkFile(String apkName){
        return new File("lilac-sample/src/main/resources/apk/"+apkName);
    }
}
