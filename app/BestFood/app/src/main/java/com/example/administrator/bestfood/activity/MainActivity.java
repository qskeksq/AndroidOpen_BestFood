package com.example.administrator.bestfood.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.administrator.bestfood.R;
import com.example.administrator.bestfood.fragment.keep.BestFoodKeepFragment;
import com.example.administrator.bestfood.fragment.list.BestFoodListFragment;
import com.example.administrator.bestfood.item.MemberInfoItem;
import com.example.administrator.bestfood.lib.GoLib;
import com.example.administrator.bestfood.lib.StringLib;
import com.example.administrator.bestfood.remote.IRemoteService;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    MemberInfoItem memberInfoItem;
    CircleImageView profileIconImage;
    DrawerLayout drawer;
    View headerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        memberInfoItem = ((MyApp)getApplication()).getMemberInfoItem();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headerLayout = navigationView.getHeaderView(0);

        GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.content_main, BestFoodListFragment.newInstance());
    }

    /**
     * 프로필 정보는 별도 액티비티에서 변경될 수 있으므로
     * 변경을 바로 감지하기 위해 화면이 새로 보여질 대마다 setProfileView() 를 호출한다.
     */
    @Override
    protected void onResume() {
        super.onResume();

        setProfileView();
    }

    /**
     * 프로필 이미지와 프로필 이름을 설정한다.
     */
    private void setProfileView() {
        profileIconImage = (CircleImageView) headerLayout.findViewById(R.id.profile_icon);
        profileIconImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
                GoLib.getInstance().goProfileActivity(MainActivity.this);
            }
        });

        // 프로필 설정에서 사진을 설정했다면 네비게이션에 사진 설정
        if (StringLib.getInstance().isBlank(memberInfoItem.memberIconFilename)) {
            Picasso.with(this).load(R.drawable.ic_person).into(profileIconImage);
        } else {
            Picasso.with(this)
                    .load(IRemoteService.MEMBER_ICON_URL + memberInfoItem.memberIconFilename)
                    .into(profileIconImage);
        }

        // 프로필 설정을 하지 않았다면 네이게이션에 이름 설정 요청
        TextView nameText = (TextView) headerLayout.findViewById(R.id.name);
        if (memberInfoItem.name == null || memberInfoItem.name.equals("")) {
            nameText.setText(R.string.name_need);
        } else {
            nameText.setText(memberInfoItem.name);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_list) {
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.content_main, BestFoodListFragment.newInstance());
        } else if (id == R.id.nav_map) {
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.content_main, BestFoodListFragment.newInstance());
        } else if (id == R.id.nav_keep) {
            GoLib.getInstance().goFragment(getSupportFragmentManager(), R.id.content_main, BestFoodKeepFragment.newInstance());
        } else if (id == R.id.nav_register) {
            GoLib.getInstance().goBestFoodRegisterActivity(this);
        } else if (id == R.id.nav_profile) {
            GoLib.getInstance().goProfileActivity(this);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
