package com.example.administrator.bestfood.fragment.list;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.activity.MainActivity;
import com.example.administrator.bestfood.activity.MyApp;
import com.example.administrator.bestfood.adapter.InfoListAdapter;
import com.example.administrator.bestfood.custom.EndlessRecyclerViewScrollListener;
import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.item.GeoItem;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.example.administrator.bestfood.remote.ServiceGenerator;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * setLayoutManager(int row) : staggeredLayout 정의
 * setRecyclerView - addOnScrollListener
 * loadListInfoFromServer - @Query 인자 사용자번호, 위도 경도, 정렬 순서, 현재 페이지 / 아이템 추가
 * changeOrderType - 정렬
 * changeListType - 보이는 방법
 */
public class BestFoodListFragment extends Fragment implements View.OnClickListener{

    Context context;

    int memberSeq;

    RecyclerView bestFoodList;
    TextView noDataText;

    TextView orderMeter;
    TextView orderFavorite;
    TextView orderRecent;

    ImageView listType;

    InfoListAdapter infoListAdapter;
    StaggeredGridLayoutManager layoutManager;
    EndlessRecyclerViewScrollListener scrollListener;

    int listTypeValue = 2;
    String orderType;

    String ORDER_TYPE_METER = "";
    String ORDER_TYPE_FAVORITE = "keep_cnt";
    String ORDER_TYPE_RECENT = "reg_date";

    /**
     * BestFoodListFragment 인스턴스를 생성한다.
     * @return BestFoodListFragment 인스턴스
     */
    public static BestFoodListFragment newInstance() {
        BestFoodListFragment f = new BestFoodListFragment();
        return f;
    }

    /**
     * fragment_bestfood_list.xml 기반으로 뷰를 생성한다.
     * @param inflater XML를 객체로 변환하는 LayoutInflater 객체
     * @param container null이 아니라면 부모 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();

        memberSeq = ((MyApp)this.getActivity().getApplication()).getMemberSeq();

        View layout = inflater.inflate(R.layout.fragment_best_food_list, container, false);

        return layout;
    }

    /**
     * onCreateView() 메소드 뒤에 호출되며 화면 뷰들을 설정한다.
     * @param view onCreateView() 메소드에 의해 반환된 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_list);

        orderType = ORDER_TYPE_METER;

        bestFoodList = (RecyclerView) view.findViewById(R.id.list);
        noDataText = (TextView) view.findViewById(R.id.no_data);
        listType = (ImageView) view.findViewById(R.id.list_type);

        orderMeter = (TextView) view.findViewById(R.id.order_meter);
        orderFavorite = (TextView) view.findViewById(R.id.order_favorite);
        orderRecent = (TextView) view.findViewById(R.id.order_recent);

        orderMeter.setOnClickListener(this);
        orderFavorite.setOnClickListener(this);
        orderRecent.setOnClickListener(this);
        listType.setOnClickListener(this);

        setRecyclerView();

        listInfo(memberSeq, GeoItem.getKnownLocation(), orderType, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyApp myApp = ((MyApp)getActivity().getApplication());
        FoodInfoItem foodInfoItem = myApp.getFoodInfoItem();
        if(infoListAdapter != null && foodInfoItem != null){
            infoListAdapter.setItem(foodInfoItem);
            myApp.setFoodInfoItem(null);
        }
    }

    private void setLayoutManager(int row){
        layoutManager = new StaggeredGridLayoutManager(row, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        bestFoodList.setLayoutManager(layoutManager);
    }

    /**
     * 리사이클러뷰를 설정하고 스크롤 리스너를 추가한다.
     */
    private void setRecyclerView() {
        setLayoutManager(listTypeValue);

        infoListAdapter = new InfoListAdapter(context,
                R.layout.row_bestfood_list, new ArrayList<FoodInfoItem>());
        bestFoodList.setAdapter(infoListAdapter);

        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                listInfo(memberSeq, GeoItem.getKnownLocation(), orderType, page);
            }
        };
        bestFoodList.addOnScrollListener(scrollListener);
    }

    private void listInfo(int memberSeq, LatLng knownLocation, String orderType, int page){

        IRemoteService remoteService = ServiceGenerator.createService(IRemoteService.class);
        Call<List<FoodInfoItem>> call = remoteService.listFoodInfo(memberSeq, knownLocation.latitude, knownLocation.longitude, orderType, page);
        call.enqueue(new Callback<List<FoodInfoItem>>() {
            @Override
            public void onResponse(Call<List<FoodInfoItem>> call, Response<List<FoodInfoItem>> response) {
                List<FoodInfoItem> list = response.body();
                if(response.isSuccessful() && list != null){
                    infoListAdapter.addItemList(list);
                    if(infoListAdapter.getItemCount() == 0){
                        noDataText.setVisibility(View.VISIBLE);
                    } else {
                        noDataText.setVisibility(View.GONE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<FoodInfoItem>> call, Throwable t) {

            }
        });
    }

    /**
     * 각종 버튼에 대한 클릭 처리를 정의한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.list_type) {
            changeListType();
        } else {
            if (v.getId() == R.id.order_meter) {
                orderType = ORDER_TYPE_METER;
                setOrderTextColor(R.color.text_color_green, R.color.text_color_black, R.color.text_color_black);
            } else if (v.getId() == R.id.order_favorite) {
                orderType = ORDER_TYPE_FAVORITE;
                setOrderTextColor(R.color.text_color_black, R.color.text_color_green, R.color.text_color_black);
            } else if (v.getId() == R.id.order_recent) {
                orderType = ORDER_TYPE_RECENT;
                setOrderTextColor(R.color.text_color_black, R.color.text_color_black, R.color.text_color_green);
            }
            setRecyclerView();
            listInfo(memberSeq, GeoItem.getKnownLocation(), orderType, 0);
        }
    }

    /**
     * 맛집 정보 정렬 방식의 텍스트 색상을 설정한다.
     * @param color1 거리순 색상
     * @param color2 인기순 색상
     * @param color3 최근순 색상
     */
    private void setOrderTextColor(int color1, int color2, int color3) {
        orderMeter.setTextColor(ContextCompat.getColor(context, color1));
        orderFavorite.setTextColor(ContextCompat.getColor(context, color2));
        orderRecent.setTextColor(ContextCompat.getColor(context, color3));
    }

    /**
     * 리사이클러뷰의 리스트 형태를 변경한다.
     */
    private void changeListType() {
        if (listTypeValue == 1) {
            listTypeValue = 2;
            listType.setImageResource(R.drawable.ic_list2);
        } else {
            listTypeValue = 1;
            listType.setImageResource(R.drawable.ic_list);

        }
        setLayoutManager(listTypeValue);
    }
}
