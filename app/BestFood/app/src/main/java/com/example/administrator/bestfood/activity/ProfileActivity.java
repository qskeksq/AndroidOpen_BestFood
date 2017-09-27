package com.example.administrator.bestfood.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.item.MemberInfoItem;
import com.example.administrator.bestfood.lib.EtcLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.example.administrator.bestfood.remote.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로필 설정 액티비티
 *
 * 1. 각각 값 설정 함수
 * 2. 필수 입력 값 체크
 * 3. 업데이트 여부(기존값과 비교 함수)
 * 4. ***** 핵심 ***** 저장 함수(1,2,3 함수 호출) -> 신규 저장인지, 업데이트인지 확인 -> MyApp 에도 저장. 다시 들어올 경우 업데이트 가능
 * 5. 닫기 함수
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView profileIcon;
    private ImageView profileIconChange;
    private EditText profileName;
    private EditText profileSextype;
    private EditText profileBirth;
    private EditText profilePhone;
    private TextView phoneState;

    MemberInfoItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        item = ((MyApp)getApplication()).getMemberInfoItem();

        initView();
        setListener();
        setToolbar();
    }

    private void initView() {
        profileIcon = (ImageView) findViewById(R.id.profile_icon);
        profileIconChange = (ImageView) findViewById(R.id.profile_icon_change);
        profileName = (EditText) findViewById(R.id.profile_name);
        profileSextype = (EditText) findViewById(R.id.profile_sextype);
        profileBirth = (EditText) findViewById(R.id.profile_birth);
        profilePhone = (EditText) findViewById(R.id.profile_phone);
        phoneState = (TextView) findViewById(R.id.phone_state);

        profileName.setText(item.name);
        profileSextype.setText(item.sextype);
        profileBirth.setText(item.birthday);
        String phoneNumber = EtcLib.getInstance().getPhoeNumber(this);
        profilePhone.setText(phoneNumber);
    }

    private void setListener(){
        profileIcon.setOnClickListener(this);
        profileIconChange.setOnClickListener(this);
        profileSextype.setOnClickListener(this);
        profileBirth.setOnClickListener(this);
    }

    /**
     * 전역에서 필요하지 않은 자원은 지역으로 넣어준다
     */
    private void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.profile_setting);
        }
    }

    /**
     * TODO 프로필 사진 설정하시오
     */
    @Override
    protected void onResume() {
        super.onResume();
        setProfileIcon();
    }

    private void setProfileIcon(){
        if(StringLib.getInstance().isBlank(item.memberIconFilename)){
            Picasso.with(this).load(R.drawable.ic_person).into(profileIcon);
            Log.e("프로필 사진 확인", "isBlank");
        } else {
            Picasso.with(this).load(IRemoteService.IMAGE_URL+item.memberIconFilename).into(profileIcon);
            Log.e("프로필 사진 확인", "isBlank 아님");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.profile_icon:
            case R.id.profile_icon_change:
                goProfileIconActivity();
                break;
            case R.id.profile_sextype:
                setSextypeDialog();
                break;
            case R.id.profile_birth:
                setBirthdayDialog();
                break;
        }
    }

    /**
     * 성별 선택 다이얼로그
     */
    private void setSextypeDialog(){
        final String[] sexTypes = new String[2];
        sexTypes[0] = getResources().getString(R.string.sex_man);
        sexTypes[1] = getResources().getString(R.string.sex_woman);

        new AlertDialog.Builder(this)
                .setItems(sexTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which >= 0){
                            profileSextype.setText(sexTypes[which]);
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 생년월일 선택 다이얼로그
     * TODO 버전 낮추시오
     */
    private void setBirthdayDialog(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String myMonth = month+"";
                if(month+1 < 10){
                    myMonth = "0"+month;
                }
                String myDay = dayOfMonth+"";
                if(dayOfMonth+1 < 10){
                    myDay = "0"+dayOfMonth;
                }
                String birthDay = year+" "+myMonth+" "+myDay;
                profileBirth.setText(birthDay);
            }
        }, year, month, day).show();
    }

    /**
     * 프로필 설정 액티비티
     */
    private void goProfileIconActivity(){
        Intent intent = new Intent(this, ProfileIconActivity.class);
        startActivity(intent);
    }

    /**
     * 오른쪽 상단 메뉴를 구성한다.
     * 닫기 메뉴만이 설정되어 있는 menu_close.xml를 지정한다.
     * @param menu 메뉴 객체
     * @return 메뉴를 보여준다면 true, 보여주지 않는다면 false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    /**
     * 왼쪽 화살표 메뉴(android.R.id.home)를 클릭했을 때와
     * 오른쪽 상단 닫기 메뉴를 클릭했을 때의 동작을 지정한다.
     * 여기서는 모든 버튼이 액티비티를 종료한다.
     * @param item 메뉴 아이템 객체
     * @return 메뉴를 처리했다면 true, 그렇지 않다면 false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                break;
            case R.id.action_submit:
                save();
                break;
        }
        return true;
    }

    /**
     * 사용자가 입력한 정보를 MemberInfoItem 객체에 저장해서 반환한다.
     * @return 사용자 정보 객체
     */
    private MemberInfoItem getMemberInfoItem() {
        MemberInfoItem item = new MemberInfoItem();
        item.phone = EtcLib.getInstance().getPhoeNumber(this);
        item.name = profileName.getText().toString();
        item.sextype = profileSextype.getText().toString();
        item.birthday = profileBirth.getText().toString().replace(" ", "");
        return item;
    }

    /**
     * 기존 사용자 정보와 새로 입력한 사용자 정보를 비교해서 변경되었는지를 파악한다.
     * @param newItem 사용자 정보 객체
     * @return 변경되었다면 true, 변경되지 않았다면 false
     */
    private boolean isChanged(MemberInfoItem newItem) {
        if (newItem.name.trim().equals(item.name)
                && newItem.sextype.trim().equals(item.sextype)
                && newItem.birthday.trim().equals(item.birthday)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 사용자가 이름을 입력했는지를 확인한다.
     * @param newItem 사용자가 새로 입력한 정보 객체
     * @return 입력하지 않았다면 true, 입력했다면 false
     */
    private boolean isNoName(MemberInfoItem newItem) {
        if (StringLib.getInstance().isBlank(newItem.name)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 사용자가 입력한 정보를 저장한다.
     */
    private void save() {
        final MemberInfoItem newItem = getMemberInfoItem();

        // 변경 사항이 없음
        if (!isChanged(newItem)) {
            Toast.makeText(this, "바뀐 내용이 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        // 변경 사항 있을 경우
        IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
        Call<ResponseBody> call = remoteService.insertMemberInfo(newItem);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String seq = null;
                    try {
                        seq = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.e("Response 리턴값", seq);
                    try {
                        item.seq = Integer.parseInt(seq);
                        if (item.seq == 0) {
                            return;
                        }
                    } catch (Exception e) {
                        return;
                    }
                    item.name = newItem.name;
                    item.sextype = newItem.sextype;
                    item.birthday = newItem.birthday;
                    Log.e("프로필 설정", "성공");
                    finish();
                } else {
                    Log.e("프로필 설정", "오류");
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("프로필 설정", "서버 연결 실패");
            }
        });
    }

    /**
     * 화면이 닫히기 전에 변경 유무를 확인해서
     * 변경사항이 있다면 저장하고 없다면 화면을 닫는다.
     */
    private void close() {
        MemberInfoItem newItem = getMemberInfoItem();

        if (!isChanged(newItem) && !isNoName(newItem)) {
            finish();
        } else if (isNoName(newItem)) {
            finish();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.change_save)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save();
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }


    /**
     * 뒤로가기 버튼을 클릭했을 때, close() 메소드를 호출한다.
     */
    @Override
    public void onBackPressed() {
        close();
    }
}
