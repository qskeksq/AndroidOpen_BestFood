package com.example.administrator.bestfood.item;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;




/**
 * Created by Administrator on 2017-09-22.
 */

public class FoodInfoItem implements Parcelable{

    public int seq;
    @SerializedName("member_seq") public int memberSeq;
    public String name;
    public String tel;
    public String address;
    public double latitude;
    public double longitude;
    public String description;
    @SerializedName("reg_date") public String regDate;
    @SerializedName("mod_date") public String modDate;
    @SerializedName("user_distance_meter") public double userDistanceMeter;
    @SerializedName("is_keep") public boolean isKeep;
    @SerializedName("image_filename") public String imageFilename;

    public FoodInfoItem() {
    }

    public FoodInfoItem(Parcel in) {
        seq = in.readInt();
        memberSeq = in.readInt();
        name = in.readString();
        tel = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        description = in.readString();
        regDate = in.readString();
        modDate = in.readString();
        userDistanceMeter = in.readDouble();
        isKeep = in.readByte() != 0;
        imageFilename = in.readString();

    }

    public static final Creator<FoodInfoItem> CREATOR = new Creator<FoodInfoItem>() {
        @Override
        public FoodInfoItem createFromParcel(Parcel in) {
            return new FoodInfoItem(in);
        }

        @Override
        public FoodInfoItem[] newArray(int size) {
            return new FoodInfoItem[size];
        }
    };

    @Override
    public String toString() {
        return "FoodInfoItem{" +
                "seq=" + seq +
                ", memberSeq=" + memberSeq +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", regDate='" + regDate + '\'' +
                ", modDate='" + modDate + '\'' +
                ", userDistanceMeter=" + userDistanceMeter +
                ", isKeep=" + isKeep +
                ", imageFilename='" + imageFilename + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeInt(seq);
        dest.writeInt(memberSeq);
        dest.writeString(name);
        dest.writeString(tel);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(description);
        dest.writeString(regDate);
        dest.writeString(modDate);
        dest.writeDouble(userDistanceMeter);
        dest.writeValue(isKeep);
        dest.writeString(imageFilename);
    }
}
