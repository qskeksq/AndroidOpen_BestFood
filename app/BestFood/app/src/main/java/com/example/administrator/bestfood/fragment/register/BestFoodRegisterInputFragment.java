package com.example.administrator.bestfood.fragment.register;


import android.content.Context;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.activity.BestFoodRegisterActivity;
import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.lib.GeoLib;
import com.example.administrator.bestfood.lib.GoLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.example.administrator.bestfood.remote.ServiceGenerator;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 1. FoodItemInfo 불러오고 - getArguemnts() 가 핵심
 * 2. 입력값 받고, 저장하고 - save() 가 핵심
 * 3. 서버에 업로드 - insertFoodInfo() 가 핵심
 */
public class BestFoodRegisterInputFragment extends Fragment implements View.OnClickListener {

    FoodInfoItem foodInfoItem;

    public static final String INFO_ITEM = "INFO_ITEM";

    Context context;
    // Point 같이 장소에 관한 데이터를 모아둔 객체
    Address address;

    EditText nameEdit;
    EditText telEdit;
    EditText descriptionEdit;
    TextView currentLength;

    /**
     * FoodInfoItem 객체를 인자로 저장하는
     * BestFoodRegisterInputFragment 인스턴스를 생성해서 반환한다.
     * @param infoItem 맛집 정보를 저장하는 객체
     * @return BestFoodRegisterInputFragment 인스턴스
     */
    public static BestFoodRegisterInputFragment newInstance(FoodInfoItem infoItem) {
        BestFoodRegisterInputFragment fragment = new BestFoodRegisterInputFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(INFO_ITEM, infoItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 프래그먼트가 생성될 때 호출되며 인자에 저장된 FoodInfoItem를
     * BestFoodRegisterActivity에 currentItem를 저장한다.
     * @param savedInstanceState 프래그먼트가 새로 생성되었을 경우, 이전 상태 값을 가지는 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            foodInfoItem = getArguments().getParcelable(INFO_ITEM);
            if (foodInfoItem.seq != 0) {
                BestFoodRegisterActivity.currentItem = foodInfoItem;
            }
        }
    }

    /**
     * fragment_bestfood_register_input.xml 기반으로 뷰를 생성한다.
     * @param inflater XML를 객체로 변환하는 LayoutInflater 객체
     * @param container null이 아니라면 부모 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        address = GeoLib.getInstance().getAddressString(context, new LatLng(foodInfoItem.latitude, foodInfoItem.longitude));

        return inflater.inflate(R.layout.fragment_best_food_register_input, container, false);
    }

    /**
     * onCreateView() 메소드 뒤에 호출되며 맛집 정보를 입력할 뷰들을 생성한다.
     * @param view onCreateView() 메소드에 의해 반환된 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentLength = (TextView) view.findViewById(R.id.current_length);
        nameEdit = (EditText) view.findViewById(R.id.bestfood_name);
        telEdit = (EditText) view.findViewById(R.id.bestfood_tel);
        descriptionEdit = (EditText) view.findViewById(R.id.bestfood_description);
        descriptionEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentLength.setText(String.valueOf(s.length()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        EditText addressEdit = (EditText) view.findViewById(R.id.bestfood_address);

        foodInfoItem.address = GeoLib.getInstance().getAddressString(address);
        if (!StringLib.getInstance().isBlank(foodInfoItem.address)) {
            addressEdit.setText(foodInfoItem.address);
        }

        Button prevButton = (Button) view.findViewById(R.id.prev);
        prevButton.setOnClickListener(this);

        Button nextButton = (Button) view.findViewById(R.id.next);
        nextButton.setOnClickListener(this);
    }

    /**
     * 클릭이벤트를 처리한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        foodInfoItem.name = nameEdit.getText().toString();
        foodInfoItem.tel = telEdit.getText().toString();
        foodInfoItem.description = descriptionEdit.getText().toString();

        if (v.getId() == R.id.prev) {
            GoLib.getInstance().goFragment(getFragmentManager(), R.id.register_content_main, BestFoodRegisterLocationFragment.newInstance(foodInfoItem));
        } else if (v.getId() == R.id.next) {
            save();
        }
    }

    /**
     * 사용자가 입력한 정보를 확인하고 저장한다.
     */
    private void save() {
        if (StringLib.getInstance().isBlank(foodInfoItem.name)) {
            Log.e("맛집 등록", "이름 없음");
            return;
        }

        if (StringLib.getInstance().isBlank(foodInfoItem.tel)) {
            Log.e("맛집 등록", "번호 없음");
            return;
        }
        insertFoodInfo();
    }


    private void insertFoodInfo(){
        Log.e("맛집 등록", "insertFoodInfo");
        final IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
        Call<ResponseBody> call = remoteService.insertFoodInto(foodInfoItem);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    // 서버에서 response의 body로 insertId 값이 리턴된다. 이 값이 위에서 정의한 제네릭이 String
                    // 이므로 String 으로 리턴되는 것이다.
                    int seq = 0;
                    String seqString = null;
                    try {
                        seqString = response.body().string();
                        Log.e("[음식점등록]seq", seqString);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // 그 값이 이상한 값이 넘어오면 등록 실패
                    try{
                        seq = Integer.parseInt(seqString);
                    } catch (Exception e){
                        seq = 0;
                    }
                    if(seq == 0){
                        Log.e("[음식점등록]", "등록 실패");
                    } else {
                        foodInfoItem.seq = seq;
                        Log.e("[음식점등록]", "성공");
                        goNextPage();
                    }
                } else {
                    int statusCode = response.code();
                    ResponseBody errBody = response.errorBody();
                    Log.e("응답 오류", statusCode+":"+errBody);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("[음식점등록]", "서버 통신 실패");
            }
        });
    }

    /**
     * 맛집 이미지를 등록할 수 있는 프래그먼트로 이동한다.
     */
    private void goNextPage() {
        GoLib.getInstance().goFragmentBack(getFragmentManager(), R.id.register_content_main, BestFoodRegisterImageFragment.newInstance(foodInfoItem.seq));
    }
}
