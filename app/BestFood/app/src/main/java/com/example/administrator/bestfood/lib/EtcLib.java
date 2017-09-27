package com.example.administrator.bestfood.lib;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017-09-22.
 */

public class EtcLib {

    private static EtcLib sEtcLib;

    public static EtcLib getInstance(){
        if(sEtcLib == null){
            sEtcLib = new EtcLib();
        }
        return sEtcLib;
    }

    /**
     * 전화번호 확인하고 null 이 아니면 리턴, 없으면 기기 정보 리턴
     */
    public String getPhoeNumber(Context context){
        // 정상적으로 번호가 있으면 번호를 리턴
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = tm.getLine1Number();

        // 번호가 null 로 리턴되면 디바이스 정보 리턴
        if(number == null && number.equals("") && number.length() <= 0){
            return getDeviceId(context);
        }
        return  number;
    }

    /**
     * 3단계에 거쳐 디바이스 구분자 리턴
     */
    public String getDeviceId(Context context){

        // 1. TelephonyManager 에서 기기 번호 가져오기
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if(deviceId != null){
            return "01"+deviceId;
        }
        // 2. global system-level device preferences 에서 제공하는 ANDROID_ID 으로 데이터베이스에서 이름 검색
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId != null){
            return "02"+androidId;
        }
        // 3. 1,2 가 없으면 버전 정보 리턴
        String serial = "";
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO){
            serial = Build.SERIAL;
        }
        if(serial != null){
            return serial;
        }
        return null;
    }

    /**
     * 전화번호가 유효한 자리수를 가지고 있는지를 체크한다.
     * @param number 전화번호 문자열
     * @return 유효한 전화번호일 경우 true, 그렇지 않으면 false
     */
    public boolean isValidPhoneNumber(String number) {
        if (number == null) {
            return false;
        } else {
            if (Pattern.matches("\\d{2}-\\d{3}-\\d{4}", number)
                    || Pattern.matches("\\d{3}-\\d{3}-\\d{4}", number)
                    || Pattern.matches("\\d{3}-\\d{4}-\\d{4}", number)
                    || Pattern.matches("\\d{10}", number)
                    || Pattern.matches("\\d{11}", number) ) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 전화번호에 '-'를 붙여서 반환한다.
     * @param number 전화번호 문자열
     * @return 변경된 전화번호 문자열
     */
    public String getPhoneNumberText(String number) {
        String phoneText = "";

        if (StringLib.getInstance().isBlank(number)) {
            return phoneText;
        }

        number = number.replace("-", "");

        int length = number.length();

        if (number.length() >= 10) {
            phoneText = number.substring(0, 3) + "-"
                    + number.substring(3, length-4) + "-"
                    + number.substring(length-4, length);
        }

        return phoneText;
    }

}
