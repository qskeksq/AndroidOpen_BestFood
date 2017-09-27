package com.example.administrator.bestfood.activity;

import android.app.Application;
import android.os.StrictMode;

import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.item.MemberInfoItem;

/**
 * 어플리케이션 단위에서 지속적으로 가져다 써야 하는 자원(사용자 정보)을 임시 저장해 둔다.
 *
 * 1. 사용자 정보
 * 2. 음식 정보
 */

public class MyApp extends Application {

    private MemberInfoItem memberInfoItem;
    private FoodInfoItem foodInfoItem;

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public MemberInfoItem getMemberInfoItem() {
        if(memberInfoItem == null){
            memberInfoItem = new MemberInfoItem();
        }
        return memberInfoItem;
    }

    public void setMemberInfoItem(MemberInfoItem memberInfoItem) {
        this.memberInfoItem = memberInfoItem;
    }

    public int getMemberSeq() {
        return memberInfoItem.seq;
    }

    public FoodInfoItem getFoodInfoItem() {
        return foodInfoItem;
    }

    public void setFoodInfoItem(FoodInfoItem foodInfoItem) {
        this.foodInfoItem = foodInfoItem;
    }
}
