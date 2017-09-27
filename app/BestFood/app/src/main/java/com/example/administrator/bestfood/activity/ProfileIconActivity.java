package com.example.administrator.bestfood.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.item.MemberInfoItem;
import com.example.administrator.bestfood.lib.FileLib;
import com.example.administrator.bestfood.lib.RemoteLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * 1. 기존 이미지 띄우기 / 없으면 준비한 디폴트 이미지 설정 함수
 * 2. 이미지를 저장할 파일 / 이미지 파일 이름 준비 함수
 * 3. 카메라에서 찍기 / 앨범에서 가져오기 함수
 * 4. 카메라에서 자르기 / 앨범에서 자르기 함수
 * 5. ***** 핵심 ***** 받아와서 서버에 업로드하기
 */
public class ProfileIconActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;

    ImageView profileIconImage;

    MemberInfoItem memberInfoItem;

    File profileIconFile;

    String profileIconFileName;

    private final int PICK_FROM_CAMERA = 0;
    private final int PICK_FROM_ALBUM = 1;
    private final int CROP_FROM_CAMERA = 2;
    private final int CROP_FROM_ALBUM = 3;

    /**
     * 액티비티를 생성하고 화면을 구성한다.
     * @param savedInstanceState 액티비티가 새로 생성되었을 경우, 이전 상태 값을 가지는 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_icon);

        context = this;

        memberInfoItem = ((MyApp) getApplication()).getMemberInfoItem();

        setToolbar();
        setView();
        setProfileIcon();
    }

    /**
     * 액티비티 툴바를 설정한다.
     */
    private void setToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.profile_setting);
        }
    }

    /**
     * 액티비티 화면을 설정한다.
     */
    public void setView() {
        profileIconImage = (ImageView) findViewById(R.id.profile_icon);

        Button albumButton = (Button) findViewById(R.id.album);
        albumButton.setOnClickListener(this);

        Button cameraButton = (Button) findViewById(R.id.camera);
        cameraButton.setOnClickListener(this);
    }

    /**
     *  프로필 이미지 설정
     */
    private void setProfileIcon(){

        // 만약 서버에서 불러온 사용자 정보에 이미지 파일이 없다면 디폴트 이미지 설정
        if(StringLib.getInstance().isBlank(memberInfoItem.memberIconFilename)){
            Picasso.with(this).load(R.drawable.ic_person).into(profileIconImage);
        // 사진이 있다면 넣어준다
        } else {
            // 아하 이게 서버 "http://192.168.1.198:3000/member/img/25_1506311037478.png"에서 불러오는 것이었군.
            Picasso.with(this).load(IRemoteService.IMAGE_URL+memberInfoItem.memberIconFilename).into(profileIconImage);
        }
    }

    /**
     * 이미지 파일 / 이미지 파일 이름 설
     */
    private void setProfileIconFile(){
        // 파일 이름 설정
        profileIconFileName = memberInfoItem.seq+"_"+System.currentTimeMillis();

        // 파일 만들기
        profileIconFile = FileLib.getInstance().getProfileIconFile(this, profileIconFileName);
    }


    /**
     * 프로필 아이콘을 설정하기 위해 선택할 수 있는 앨범이나 카메라 버튼의 클릭 이벤트를 처리한다.
     * @param v 클릭한 뷰 객체
     */
    @Override
    public void onClick(View v) {
        setProfileIconFile();

        if (v.getId() == R.id.album) {
            getImageFromAlbum();

        } else if (v.getId() == R.id.camera) {
            getImageFromCamera();
        }
    }

    /**
     * 오른쪽 상단 메뉴를 구성한다.
     * 닫기 메뉴만이 설정되어 있는 menu_close.xml를 지정한다.
     * @param menu 메뉴 객체
     * @return 메뉴를 보여준다면 true, 보여주지 않는다면 false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_close, menu);
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
                finish();
                break;

            case R.id.action_close:
                finish();
                break;
        }

        return true;
    }

    /**
     * 카메라에서 이미지 가져오기
     *
     * MediaStore.EXTRA_OUTPUT 설정을 해주면 캡쳐한 사진을 파일 객체에 저장해준다
     */
    private void getImageFromCamera() {
        // 인텐트를 통해 캡쳐할 카메라를 불러온다
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 어떤 파일에 이미지를 저장할지 알려준다
        intent.putExtra(MediaStore.EXTRA_OUTPUT, profileIconFile);
        // 시작
        startActivityForResult(intent, PICK_FROM_CAMERA);
        Log.e("[ProfileIcon 확인]", "getImageFromCamera");
    }

    /**
     * 앨범에서 이미지 가져오기
     */
    private void getImageFromAlbum() {
        //
        Intent intent =  new Intent(Intent.ACTION_PICK);
        // 앨법 앱을 선택하겠다
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        // 시작
        startActivityForResult(intent, PICK_FROM_ALBUM);
        Log.e("[ProfileIcon 확인]", "getImageFromAlbum");
    }

    /**
     * 이미지를 자르기 위한 인텐트
     * @param beforeCrop    자르기 전 이미지 파일 주소
     * @param afterCrop     자른 후 저장할 이미지 파일 주소
     * @return  실행할 인텐트
     * TODO 인텐트 공부할 것
     */
    private Intent getCropIntent(Uri beforeCrop, Uri afterCrop){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(beforeCrop, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, afterCrop);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        Log.e("[ProfileIcon 확인]", "getCropIntent");
        return intent;
    }

    /**
     * 카메라에서 가져온 이미지 자르기
     */
    private void cropImageFromCamera(){
        Uri cropUri = Uri.fromFile(profileIconFile);
        // 가져온 주소에 잘라낸 이미지를 덮어쓴다.
        Intent intent = getCropIntent(cropUri, cropUri);
        startActivityForResult(intent, CROP_FROM_CAMERA);
        Log.e("[ProfileIcon 확인]", "cropImageFromCamera");
    }

    /**
     * 앨범에서 가져온 이미지 자르기
     * @param beforeCrop
     */
    private void cropImageFromAlbum(Uri beforeCrop){
        Uri afterCrop = Uri.fromFile(profileIconFile);
        Intent intent = getCropIntent(beforeCrop, afterCrop);
        startActivityForResult(intent, CROP_FROM_ALBUM);
        Log.e("[ProfileIcon 확인]", "cropImageFromAlbum");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e("[ProfileIcon 확인]", "onActivityResult");
        if(resultCode != RESULT_OK){
            Log.e("[ProfileIcon 확인]", "RESULT_OK가 아님");
            return;
        } else if(requestCode == PICK_FROM_CAMERA){
            cropImageFromCamera();
            Log.e("[ProfileIcon 확인]", "PICK_FROM_CAMERA");
        } else if(requestCode == CROP_FROM_CAMERA){
            Picasso.with(this).load(profileIconFile).into(profileIconImage);
            uploadImageFile();
            Log.e("[ProfileIcon 확인]", "CROP_FROM_CAMERA");
        } else if(requestCode == PICK_FROM_ALBUM && data != null){
            Uri uri = data.getData();
            if(uri != null){
                cropImageFromAlbum(uri);
                Log.e("[ProfileIcon 확인]", "PICK_FROM_ALBUM");
            }
        } else if(requestCode == CROP_FROM_ALBUM && data != null){
            Picasso.with(this).load(profileIconFile).into(profileIconImage);
            uploadImageFile();
            Log.e("[ProfileIcon 확인]", "CROP_FROM_ALBUM");
        }
    }


    private void uploadImageFile(){
        RemoteLib.getInstance().uploadMemberIcon(memberInfoItem.seq, profileIconFile);

        memberInfoItem.memberIconFilename = profileIconFileName + ".png";
    }



}