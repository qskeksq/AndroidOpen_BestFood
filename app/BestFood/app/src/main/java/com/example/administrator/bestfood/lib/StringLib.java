package com.example.administrator.bestfood.lib;

import android.content.Context;

import com.example.administrator.bestfood.R;

/**
 * Created by Administrator on 2017-09-22.
 */

public class StringLib {

    private static StringLib sStringLib;

    public static StringLib getInstance(){
        if(sStringLib == null){
            sStringLib = new StringLib();
        }
        return sStringLib;
    }

    /**
     * 문자열 null 측정
     */
    public boolean isBlank(String str){
        if(str == null || str.equals("")){
            return true;
        } else {
            return false;
        }
    }

    /**
     * 문자열를 지정된 길이로 잘라서 반환한다.
     * @param context 컨텍스트
     * @param str 문자열 객체
     * @param max 최대 문자열 길이
     * @return 변경된 문자열 객체
     */
    public String getSubString(Context context, String str, int max) {
        if (str != null && str.length() > max) {
            return str.substring(0, max) + context.getResources().getString(R.string.skip_string);
        } else {
            return str;
        }
    }



}



