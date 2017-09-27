package com.example.administrator.bestfood.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.item.MemberInfoItem;
import com.example.administrator.bestfood.lib.EtcLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.example.administrator.bestfood.lib.RemoteLib;
import com.example.administrator.bestfood.remote.ServiceGenerator;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 1. 인터넷 연결 확인
 * 2. 전화번호, 기기번호 확인
 * 3. 기존 계정이 있는지 확인 -> 있으면 정보 저장 -> 메인으로 넘어감 // 각각 서버 통신 여부 확인
 * 4. 없으면 새로 정보 저장 -> 프로필 저장 페이지로 넘어감   // 각가 서버 통신 여부 확인
 */
public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        // 인터넷 연결 확인 - 연결되지 않았다면 리턴
        if (!RemoteLib.getInstance().isConnected(this)) {
            showNoService();
            Log.e("[index화면] 1. 인터넷 연결 확인", "실패");
            return;
        }
        Log.e("[index화면] 1. 인터넷 연결 확인", "성공");
    }

    /**
     * 인터넷 연결 확인 후 전화번호/기기정보를 통해 계정 확인
     */
    @Override
    protected void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startTask();
            }
        }, 1200);
    }

    /**
     * 인터넷에 연결되지 않았다면 다음 페이지로 넘어갈 수 없음
     *
     * 필요할 때만 쓰는 위젯이기 때문에 지역변수로 할당. 메모리 절약할 수 있다.
     */
    private void showNoService(){

        // "네크워크에 연결되어 있지 않습니다" 텍스트 VISIBLE
        TextView messageTxt = (TextView) findViewById(R.id.message);
        messageTxt.setVisibility(View.VISIBLE);

        // 종료 버튼 VISIBLE
        Button button = (Button) findViewById(R.id.close);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        button.setVisibility(View.VISIBLE);
    }

    /**
     * 사용자 계정 확인1. 전화번호/기기정보 가져오기
     */
    private void startTask() {
        String phoneNumber = EtcLib.getInstance().getPhoeNumber(this);
        Log.e("[index화면] 2. 전화번호 확인", phoneNumber+"");
        selectMemberInfoFromServer(phoneNumber);
    }

    /**
     * 사용자 계정 확인2. 가져온 정보 서버에서 확인
     */
    private void selectMemberInfoFromServer(String phoneNumber){
        // 결국 Retrofit 생성 -> GET 요청으로 데이터 불러오기 -> 성공 여부에 따라 다음으로 넘어갈지 결정
        IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
        Call<MemberInfoItem> call = remoteService.selectMemberInfo(phoneNumber);
        call.enqueue(new Callback<MemberInfoItem>() {
            @Override
            public void onResponse(Call<MemberInfoItem> call, Response<MemberInfoItem> response) {
                Log.e("[index화면] 3.1 계정 불러오기", "성공");
                MemberInfoItem item = response.body();
                if(response.isSuccessful() && !StringLib.getInstance().isBlank(item.name)){
                    Log.e("[index화면] 3.2 계정 불러오기", "성공, 기존 계정 확인");
                    setMemberInfo(item);
                } else {
                    Toast.makeText(IndexActivity.this, "계정이 없습니다. 프로필 창으로 넘어갑니다", Toast.LENGTH_SHORT).show();
                    Log.e("[index화면] 3.2 계정 불러오기", "성공, 하지만 기존 계정 없음");
                    goProfileActivity(item);
                }
            }
            @Override
            public void onFailure(Call<MemberInfoItem> call, Throwable t) {
                Log.e("[index화면] 3. 계정 불러오기", "서버 통신에 실패");
                Toast.makeText(IndexActivity.this, "서버 통신에 실패했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 사용자 계정 확인3. 서버로부터 데이터 받아오는데 성공했다면 현재 앱에 계정 정보 저장
     */
    private void setMemberInfo(MemberInfoItem item){
        ((MyApp) getApplicationContext()).setMemberInfoItem(item);
        startMain();
    }

    /**
     * ******경   축******
     * 메인 액티비티로 넘어감
     */
    private void startMain(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        Log.e("[index화면] 4. 초기 작업 완료", "메인 화면으로 넘어감");
        finish();
    }

    /**
     * 계정 불러오기에 실패한경우
     */
    private void goProfileActivity(MemberInfoItem item){

        // 만약 처음 등록한 사람 즉, 계정이 없는 사람이라면 번호를 등록해준다.
        if(item == null || item.seq <= 0){
            Log.e("[index화면] 4.1 계정 없음", "계정 저장 시도");
            insertMemberInfo();
        }

        // 메인 액티비티 띄운 다음
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        // 프로필 액티비티에서 계정 작성 하도록 한다
        Intent intent2 = new Intent(this, ProfileActivity.class);
        startActivity(intent2);

        finish();
    }

    /**
     * 계정이 없는 경우 새로 서버에 등록한다.
     */
    private void insertMemberInfo(){
        String phoneNumber = EtcLib.getInstance().getPhoeNumber(this);

        final IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);

        Call<ResponseBody> call = remoteService.insertMemberPhone(phoneNumber);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(IndexActivity.this, "번호 등록 성공", Toast.LENGTH_SHORT).show();
                    Log.e("[index화면] 4.2 계정 없음", "계정 저장 성공");
                } else {
                    int statusCode = response.code();
                    Toast.makeText(IndexActivity.this, statusCode+" 오류 발생", Toast.LENGTH_SHORT).show();
                    ResponseBody errbody = response.errorBody();
                    Log.e("[index화면] 4.2 계정 없음", "계정 저장 실패 "+statusCode);
                    Log.e("[index화면] 4.2 계정 없음", errbody.toString());
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(IndexActivity.this, "서버 통신에 실패했습니다", Toast.LENGTH_SHORT).show();
                Log.e("[index화면] 4.2 계정 없음", "계정 저장 서버 통신 실패");
            }
        });
    }
}
