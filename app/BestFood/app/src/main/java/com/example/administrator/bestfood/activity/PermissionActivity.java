package com.example.administrator.bestfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.lib.CheckPermission;

/**
 * 퍼미션 체크하는 런처 액티비티. 권한설정이 하나라도 되지 않으면 실행할 수 없다.
 *
 * 1. 버전 체크
 * 2. 기존에 허용된 버전 체크
 * 3. 없는 버전 시스템에 요청
 * 4. 요청한 권한 승인 여부 검사
 * 5. 모두 통과하면 메인으로 넘어가고
 * 6. 통과하지 않았다면 권한 설정 페이지로 이동
 */
public class PermissionActivity extends AppCompatActivity implements CheckPermission.CallBack {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        CheckPermission.getInstance().checkVersion(this);
    }

    @Override
    public void init() {
        Intent intent = new Intent(this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 시스템에게 권한 요청하면 시스템이 인자로 결과 값들을 넘겨준다.
     * @param requestCode   REQ_CODE
     * @param permissions   시스템에게 요청한 권한들
     * @param grantResults  허용됬는지 여부가 담겨온다
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CheckPermission.getInstance().onResult(grantResults, this);
    }
}
