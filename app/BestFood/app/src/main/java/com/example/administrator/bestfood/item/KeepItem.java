package com.example.administrator.bestfood.item;

/**
 * Created by Administrator on 2017-09-27.
 */

import com.google.gson.annotations.SerializedName;

/**
 * 즐겨찾기 정보를 저장하는 객체
 */
public class KeepItem extends FoodInfoItem{
    @SerializedName("keep_seq") public String keepSeq;
    @SerializedName("keep_member_seq") public String keepMemberSeq;
    @SerializedName("keep_date") public String keepDate;

    @Override
    public String toString() {
        return "KeepItem{" +
                "keepSeq='" + keepSeq + '\'' +
                ", keepMemberSeq='" + keepMemberSeq + '\'' +
                ", keepDate='" + keepDate + '\'' +
                '}';
    }
}