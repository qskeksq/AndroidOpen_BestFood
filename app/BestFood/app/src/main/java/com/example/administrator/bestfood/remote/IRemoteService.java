package com.example.administrator.bestfood.remote;

import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.item.KeepItem;
import com.example.administrator.bestfood.item.MemberInfoItem;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017-09-22.
 */

public interface IRemoteService {

    // 결국 레트로핏을 이해하려면 Http 프로토콜을 이해해야 하는 것은, 헤더와 바디에 메시지를 주고받는 형식을 알아야
    // 어떤 값이 오고가는지 알고 어떻게 갖다 쓰는지도 알고, 오류가 어디서 생기는지 알 수 있기 때문이다.

    // Query 는 int
    // Field 는 String
    // File 은 MultiPart


    // 뿐만 아니라 어떤 값으로 보내는랴에 따라 서버에서 값을 받는 형식도 달라진다.
    // 먼저 GsonConvertreFactory 를 사용하는가, 서버에서는 exrpess()를 사용하는가에 따라 또 달라진다.
    // Body 로 보내면 request.body. 에서 바로 꺼내 사용할 수 있다. Path 로 보내면 request.params 에서 꺼내 쓸 수 있다.

    // POST
    // @Body 에 추가된 이러한 객체들은 Retrofit 인스턴스에 추가된 컨버터에 따라 변환됩니다.
    // 만약 해당 타입에 맞는 컨버터가 추가되어있지 않다면, RequestBody 만 사용하실 수 있습니다.
    // 이 말은 서버로 넘어갈 때 변환되어 넘어간다는 뜻인가? RequestBody 만 사용한다는 것은 JSON 으로 넘어간다는 뜻인가?

    // FORM-ENCODED과 MULTIPART
    // 메소드는 form-encoded 데이터와 multipart 데이터 방식으로 정의 가능합니다.
    // @FormUrlEncoded 어노테이션을 메소드에 명시하면 form-encoded 데이터로 전송 됩니다.
    // 각 key-value pair의 key 는 어노테이션 값에, value 는 객체를 지시하는 @Field 어노테이션으로 매개변수에 명시하시면 됩니다.
    // 즉, 데이터가 키-값 형태로 전달되기 때문에 서버의 request 에서 꺼낼 때도 키로 꺼내면 된다.
    // FormUrlEncoded 가 뭔고 하면, URL 에 키값 형태도 들어간다는 뜻이다. 즉, 여기는 Body 로 들어가지 않고
    // 간단한 형식이라서 URL 에 적어서 키-값으로 꺼내 사용하는 것. 객체 전체를 보낸다면 지혜롭지 않은 방법이다.
    // 이거 공부하려면 HTTP 프로토콜, ContentType 공부해야 알 수 있다. 즉, 서버를 기본으로 알아야 진행이 된다.
    // 참고 In the case of x-www-form-urlencoded, the whole form data is sent as a "long query string"
    // 참고2 In the case of x-www-form-urlencoded, all name value pairs are sent as one big query string where
    //      alphanumeric and reserved character are url encoded i.e. replaced by % and their hex value e.g. space is replaced by %20.
    //      The length of this string is not specified by HTTP specification and depends upon server implementation.


    // MULTIPART
    // FormUrlEncoded 와 비슷하게 키-값 형태로 데이터를 보내지만 주로 바이너리 파일, 즉, File 을 서버에 업로드 할 때 주로 사용한다
    // FormUrlEncoded 할 때 형태를 변환시키는 작업이 있어서 늦어질 수 있다.
    // while multipart/form-data is used to send binary data, most notably for uploading files to the server.
    // The multipart/form-data is more efficient than x-www-form-urlencoded because you don't need to replace one character with three bytes as required by URL encoding.


    // 파일 전송
    // 파일 전송은 무조건 RequestBody 아니면 MultipartBody.Part 을 사용해야 한다\
    // Using Retrofit 2, you need to use either OkHttp’s RequestBody or MultipartBody.Part classes and encapsulate your file into a request body


    // 검색은 @Query 로 한다고 생각하자


    String BASE_URL = "http://192.168.1.68:3000";
    String MEMBER_ICON_URL = BASE_URL+"/member/";
    String IMAGE_URL = BASE_URL+"/img/";
//==================================================================================================
//    사용자 정보
//==================================================================================================

    /**
     * IndexActivity
     */
    // 서버에서 번호로 계정 찾기
    @GET("/member/{phone}")
    Call<MemberInfoItem> selectMemberInfo(@Path("phone") String phone);


    // 서버에 번호로 계정 등록
    @FormUrlEncoded
    @POST("/member/phone")
    Call<ResponseBody> insertMemberPhone(@Field("phone") String phone);


    /**
     * ProfileActivity
     */
    // 서버에 사용자 계정 등록
    @POST("/member/info")
    Call<ResponseBody> insertMemberInfo(@Body MemberInfoItem memberInfoItem);


    /**
     * ProfileIconActivity
     */
    // 이미지를 서버에 업로드
    @Multipart
    @POST("/member/icon_upload")
    Call<ResponseBody> uploadMemberIcon(@Part("member_seq")RequestBody memberseq, @Part MultipartBody.Part file);

//==================================================================================================
//    맛집 정보
//==================================================================================================

    /**
     * BestFoodRegisterInputFragment
     */
    // 서버에 맛집 정보 업로드
    @POST("/food/info")
    Call<ResponseBody> insertFoodInto(@Body FoodInfoItem foodInfoItem);


    /**
     * BestFoodRegisterImageFragment
     */
    // 맛집 이미지 업로드
    @Multipart
    @POST("/food/info/image")
    Call<ResponseBody> uploadFoodImage(
            @Part("info_seq") RequestBody infoSeq,
            @Part("image_memo") RequestBody imageMemo,
            @Part MultipartBody.Part file
    );


    /**
     * BestFoodListFragment
     */
    // 맛집 정보
    @GET("/food/list")
    Call<List<FoodInfoItem>> listFoodInfo(@Query("member_seq") int memberSeq,
                                          @Query("user_latitude") double userLatitude,
                                          @Query("user_longitude") double userLongitude,
                                          @Query("order_type") String orderType,
                                          @Query("current_page") int currentPage
    );

    /**
     * BestFoodInfoActivity
     */
    // 맛집 정보
    @GET("/food/list/{info_seq}")
    Call<FoodInfoItem> selectFoodInfo(@Path("info_seq") int infoSeq, @Query("member_seq") int memberSeq);

//==================================================================================================
//    지도 정보
//==================================================================================================

    /**
     * BestFoodMapFragment
     */
    // 지도 프래그먼트 필요한 정보
    @GET("/map/list")
    Call<List<FoodInfoItem>> listMap(@Query("member_seq") int memberSeq,
                                     @Query("latitude") double latitude,
                                     @Query("longitude") double longitude,
                                     @Query("distance") int distance,
                                     @Query("user_latitude") double userLatitude,
                                     @Query("user_longitude") double userLongitude);

//==================================================================================================
//    즐겨찾기
//==================================================================================================

    /**
     * BestFoodListFragment
     */
    // 즐겨찾기 추가
    @POST("/keep/{member_seq}/{info_seq}")
    Call<ResponseBody> insertKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);

    // 즐겨찾기 삭제
    @POST("/keep/{member_seq}/{info_seq}")
    Call<String> deleteKeep(@Path("member_seq") int memberSeq, @Path("info_seq") int infoSeq);


    /**
     * BestFoodKeepFragment
     */
    // 즐겨찾기 페이지
    @GET("/keep/list")
    Call<ArrayList<KeepItem>> listKeep(
            @Query("member_seq") int memberSeq,
            @Query("user_latitude") double userLatitude,
            @Query("user_longitude") double userLongitude
    );


}
