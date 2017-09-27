package com.example.administrator.bestfood.lib;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2017-09-24.
 */

public class FileLib {

    private volatile static FileLib instance;

    public static FileLib getInstance() {
        if (instance == null) {
            synchronized (FileLib.class) {
                if (instance == null) {
                    instance = new FileLib();
                }
            }
        }
        return instance;
    }

    /**
     * 파일 객체를 저장할 디렉토리
     * TODO 이해 안 됨. 공부 필요
     */
    public File getFilesDir(Context context){

        String state = Environment.getExternalStorageState();
        File fileDir;

        if(Environment.MEDIA_MOUNTED.equals(state)){
            fileDir = context.getExternalFilesDir(null);
        } else {
            fileDir = context.getFilesDir();
        }
        return fileDir;
    }

    /**
     * 프로필 사진 파일 리턴
     * @param context
     * @param name
     * @return
     */
    public File getProfileIconFile(Context context, String name){
        return new File(FileLib.getInstance().getFilesDir(context) , name+".png");
    }

    /**
     * 이미지 파일 리턴
     * @param context
     * @param name
     * @return
     */
    public File getImageFile(Context context, String name){
        return new File(FileLib.getInstance().getFilesDir(context), name+".png");
    }

}
