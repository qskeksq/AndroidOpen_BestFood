package com.example.administrator.bestfood.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.fragment.register.BestFoodRegisterLocationFragment;
import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.item.GeoItem;
import com.example.administrator.bestfood.lib.GoLib;


/**
 * FoodItemInfo 를 두고 세 개의 프래그먼트로 나눠서 데이터를 저장하는 형식이다.
 */
public class BestFoodRegisterActivity extends AppCompatActivity {

    public static FoodInfoItem currentItem = null;

    Context context;

    /**
     * BestFoodRegisterLocationFragment를 실행하기 위한 기본적인 정보를 설정하고
     * 프래그먼트를 실행한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bestfood_register);

        context = this;

        int memberSeq = ((MyApp)getApplication()).getMemberSeq();

        //BestFoodRegisterLocationFragment 로 넘길 기본적인 정보를 저장한다.
        FoodInfoItem infoItem = new FoodInfoItem();
        infoItem.memberSeq = memberSeq;
        infoItem.latitude = GeoItem.getKnownLocation().latitude;
        infoItem.longitude = GeoItem.getKnownLocation().longitude;

        Log.e("FoodInfoItem Seq", infoItem.seq+"");
        Log.e("FoodInfoItem 좌표", infoItem.latitude+"");
        Log.e("FoodInfoItem 좌표", infoItem.longitude+"");


        setToolbar();

        //BestFoodRegisterLocationFragment를 화면에 보여준다.
        GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.register_content_main, BestFoodRegisterLocationFragment.newInstance(infoItem));
    }

    /**
     * 툴바를 설정한다.
     */
    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.bestfood_register);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(Fragment fragment : getSupportFragmentManager().getFragments()){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
