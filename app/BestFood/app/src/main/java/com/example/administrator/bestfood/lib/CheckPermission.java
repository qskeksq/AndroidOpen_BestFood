package com.example.administrator.bestfood.lib;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import com.example.administrator.bestfood.activity.PermissionActivity;
import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.activity.SettingsActivity;

/**
 * Created by Administrator on 2017-09-22.
 */

public class CheckPermission {

    // 체크할 퍼미션
    private static String[] permissions = {
            Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE};
    public static int REQ_CODE = 999;
    private volatile static CheckPermission permission;

    /**
     * 데이터베이스 연결이 아니고, 가장 먼저 하는 일이기 때문에 싱글턴으로 해도 메모리 누수가 생기지 않는다.
     */
    public static CheckPermission getInstance(){
        if(permission == null){
            permission = new CheckPermission();
        }
        return permission;
    }

    public void checkVersion(Activity activity){
        // 버전이 마시멜로 미만인 경우는 패스
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            ((PermissionActivity)activity).init();
            // 이상인 경우는 일단 허용이 된 퍼미션이 무엇인지 체크한다.
        } else {
            checkAlreadyGrantedPermission(activity);
        }
    }

    /**
     * 이미 체크된 퍼미션이 있는지 확인하고, 체크되지 않았다면 시스템에 onRequestPermission()으로 권한을 요청한다.
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkAlreadyGrantedPermission(Activity activity) {
        boolean isAllGranted = true;
        for(String perm : permissions){
            // 만약 원하는 퍼미션이 하나라도 허용이 안 되었다면 false로 전환
            if(activity.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                isAllGranted = false;
            }
        }
        // 만약 전부 허용이 되었다면 다음 액티비티로 넘어간다.
        if(isAllGranted){
            ((PermissionActivity)activity).init();
            // 허용되지 않는 것이 있다면 시스템에 권한신청한다.
        } else {
            activity.requestPermissions(permissions, REQ_CODE);
        }
    }


    /**
     * 시스템 권한체크가 끝난 후 호출
     */
    public void onResult(int[] grantResults, Activity activity){
        boolean isAllGranted = true;
        for(int granted : grantResults){
            if(granted != PackageManager.PERMISSION_GRANTED){
                isAllGranted = false;
            }
        }
        // 허용되면 init()으로 원하는 함수를 실행하고
        if(isAllGranted){
            ((PermissionActivity)activity).init();
            // 허용되지 않는 것이 있다면 시스템에 권한신청한다.
        } else {
            showPermissionDialog(activity);
        }
    }

    /**
     * 권한 설정 페이지로 넘어갈 것인지 앱을 종료할 것인지 묻는 다이얼로그
     */
    private void showPermissionDialog(final Activity activity){
        AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(R.string.permission_setting_title);
        dialog.setMessage(R.string.permission_setting_message);
        // 권한 페이지로 넘어갈 경우
        dialog.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(activity, "권한 페이지로 넘어갑니다", Toast.LENGTH_SHORT).show();
                goSettingsActivity(activity);
                activity.finish();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(activity, "권한이 없으면 앱을 실행할 수 없습니다", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
        });
        dialog.show();
    }

    private void goSettingsActivity(Activity activity){
        Intent intent = new Intent(activity, SettingsActivity.class);
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    /**
     * MainActivity가 스스로를 넘겨주면, 이곳에서 MainActivity 를 대신해 메소드를 호출해주는 콜백 메서드
     */
    public interface CallBack {
        void init();
    }
}
