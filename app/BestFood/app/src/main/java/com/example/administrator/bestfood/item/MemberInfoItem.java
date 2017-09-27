package com.example.administrator.bestfood.item;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017-09-22.
 */

public class MemberInfoItem {

    // @SerializedName 은 서버 키와 현재 클래스의 아이디가 다를 경우 member_icon_filename 로 매핑을 시켜준다.

    public int seq;
    public String phone;
    public String name;
    public String sextype;
    public String birthday;
    @SerializedName("member_icon_filename") public String memberIconFilename;
    public String regDate;

}
