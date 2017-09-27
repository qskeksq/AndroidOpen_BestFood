package com.example.administrator.bestfood.fragment.register;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.activity.BestFoodRegisterActivity;
import com.example.administrator.bestfood.item.FoodInfoItem;
import com.example.administrator.bestfood.lib.GeoLib;
import com.example.administrator.bestfood.lib.GoLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * 구글맵 세팅
 * onViewCreated(getMapSync) -> onMapReady -> onMapClick -> onMarkerDragEnd
 * 리스너, UiSettings location, zoomControl, compass
 *
 * onMapClick, onMarkerDragEnd 호출시
 * 1. setCurrentLatLng  : 현재 위치 설정
 * 2. moveLocation      : 선택 지점으로 이동
 * 3. addMarker         : 그 지점에 마커 등록
 * 4. setAddressText    : 주소 입력
 *
 * GeoItem -> GeoLib -> 현재 위치 설정, GeoCoding
 */
public class BestFoodRegisterLocationFragment
        extends Fragment
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
                    GoogleMap.OnMarkerDragListener, View.OnClickListener {

    private final float DEFAULT_ZOOM_LEVEL = 16;
    private final float DETAIL_ZOOM_LEVEL = 18;
    public static final String INFO_ITEM = "INFO_ITEM";

    private FoodInfoItem foodInfoItem;

    Context context;
    GoogleMap googleMap;

    TextView addressText;


    public static BestFoodRegisterLocationFragment newInstance(FoodInfoItem infoItem){
        BestFoodRegisterLocationFragment fragment = new BestFoodRegisterLocationFragment();
        // 원하는 객체 전체를 Parcelabe 로 담아서 보내준다.
        Bundle bundle = new Bundle();
        // FoodInfoItem 에 @org.parceler.Parcel 어노테이션을 해주지 않으면 안 됨.
        bundle.putParcelable(INFO_ITEM, infoItem);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(getArguments() != null){
            foodInfoItem = getArguments().getParcelable(INFO_ITEM);
            Log.e("FoodInfoItem Seq", foodInfoItem.seq+"");
            Log.e("FoodInfoItem 좌표", foodInfoItem.latitude+"");
            Log.e("FoodInfoItem 좌표", foodInfoItem.longitude+"");
            if(foodInfoItem.seq != 0){
                BestFoodRegisterActivity.currentItem = foodInfoItem;
            }
        }
        return inflater.inflate(R.layout.fragment_best_food_register_location, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 맵이 프래그먼트로 들어갔기 때문에 childFragment 로 관리한다
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);

        // 불러오지 못하는 경우는 새로 만들어서 띄워준다
        if(mapFragment == null){
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.register_content_main, mapFragment).commit();
        }
        // 맵프래그먼트가 싱크될 때 OnMapReady()를 호출한다.
        mapFragment.getMapAsync(this);

        addressText = (TextView) view.findViewById(R.id.bestfood_address);

        Button nextButton = (Button) view.findViewById(R.id.next);
        nextButton.setOnClickListener(this);
    }

    /**
     * 구글맵이 준비되었을 때 호출되며 구글맵을 설정하고 기본 마커를 추가하는 작업을 한다.
     * @param googleMap 구글맵 객체
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        // 리스너 세팅
        if(getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMapClickListener(this);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMarkerDragListener(this);
        }

        // 맵에 여러 기능을 추가해 줄 수 있다.
        UiSettings settings = googleMap.getUiSettings();
        settings.setMyLocationButtonEnabled(true);
        settings.setCompassEnabled(true);
        settings.setZoomControlsEnabled(true);

        LatLng firstLocation = new LatLng(foodInfoItem.latitude, foodInfoItem.longitude);
        if(foodInfoItem.latitude != 0 && foodInfoItem.longitude != 0){
            addMarker(firstLocation, DEFAULT_ZOOM_LEVEL);
        }

    }

    /**
     * 위치 이동
     * @param targetPosition
     * @param zoomLevel
     */
    private void moveLocation(LatLng targetPosition, float zoomLevel){
        // 빌드 패턴 배울 것.
        CameraPosition cp = new CameraPosition.Builder().target(targetPosition).zoom(zoomLevel).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    private void addMarker(LatLng markerPosition, float zoomLevel){
        MarkerOptions options = new MarkerOptions();
        options.position(markerPosition);
        options.title("현재위치");
        options.draggable(true);

        // 이전 마커는 지워준다
        googleMap.clear();
        googleMap.addMarker(options);

        // 마커 위치로 이동한다
        moveLocation(markerPosition, zoomLevel);
    }

    /**
     * 위도 경도를 기반으로 주소를 addressText 뷰에 출력한다.
     * @param latLng 위도, 경도 객체
     */
    private void setAddressText(LatLng latLng) {
        Address address = GeoLib.getInstance().getAddressString(getContext(), latLng);
        String addressStr = GeoLib.getInstance().getAddressString(address);
        if (!StringLib.getInstance().isBlank(addressStr)) {
            addressText.setText(addressStr);
        }
    }

    /**
     * 지정된 latLng의 위도와 경도를 infoItem에 저장한다.
     * @param latLng 위도, 경도 객체
     */
    private void setCurrentLatLng(LatLng latLng) {
        foodInfoItem.latitude = latLng.latitude;
        foodInfoItem.longitude = latLng.longitude;
    }

    /**
     * 맵이 클릭되었을 경우
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        // 1. 위치 설정
        setCurrentLatLng(latLng);
        // 2. 주소 설정
        setAddressText(latLng);
        // 3, 4 마커 설정 & 이동
        addMarker(latLng, googleMap.getCameraPosition().zoom);
    }

    /**
     * 마커가 클릭되었을 경우
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * 마커를 Drag 하는 경우
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        setCurrentLatLng(marker.getPosition());
    }


    /**
     * 클릭이벤트를 처리하며 맛집 정보를 담당하는 프래그먼트로 이동한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        GoLib.getInstance().goFragment(getFragmentManager(), R.id.content_main, BestFoodRegisterInputFragment.newInstance(foodInfoItem));
    }

}
