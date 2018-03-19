package com.ldcc.pliss.deliveryadvisor.page;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ldcc.pliss.deliveryadvisor.MainActivity;
import com.ldcc.pliss.deliveryadvisor.R;
import com.ldcc.pliss.deliveryadvisor.databases.Delivery;
import com.ldcc.pliss.deliveryadvisor.databases.DeliveryHelper;

import java.util.ArrayList;
import java.util.Random;

import io.realm.RealmResults;

public class NavigationActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
        //http://duzi077.tistory.com/122
        setLayout();
    }

    private void setLayout(){
        //상단 툴바 세팅
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.icon_setting);
        getSupportActionBar().setTitle("지도 보기");

        //상단 툴바 왼쪽의 메뉴 버튼 클릭했을 때 등장하는 뷰 세팅
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.map_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.map_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(final GoogleMap map) {

        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMapToolbarEnabled(true);
        if (ActivityCompat.checkSelfPermission(NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(NavigationActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            finish();
        }else {
            map.setMyLocationEnabled(true);
        }

        DeliveryHelper deliveryHelper = new DeliveryHelper(this);
        RealmResults<Delivery> results = deliveryHelper.getAllDeliveryList();

        MarkerOptions markerOptions = new MarkerOptions();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        ArrayList<LatLng> destinations = new ArrayList();
        for(int i=0; i<results.size() ;i++){
            destinations.add(new LatLng(Double.parseDouble(results.get(i).getRECV_ADDR_LAT()),Double.parseDouble(results.get(i).getRECV_ADDR_LNG())));
            markerOptions.title(results.get(i).getITEM_NM());
            markerOptions.snippet(results.get(i).getRECV_ADDR());
            markerOptions.position(destinations.get(i));
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(new Random().nextInt(360)));
            map.addMarker(markerOptions).showInfoWindow();
            builder.include(destinations.get(i));
        }
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        map.animateCamera(cu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //왼쪽에 숨겨져 있는 네비게이션 메뉴 중 특정 버튼을 누르면, 해당 페이지로 이동시킵니다.
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_main) {
            Toast.makeText(NavigationActivity.this, "업무 내용을 확인합니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, MainActivity.class);
            newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(newIntent);
            finish();
        } else if (id == R.id.nav_navigation) {
            Toast.makeText(NavigationActivity.this, "경로를 확인합니다.", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logs) {
            Toast.makeText(NavigationActivity.this, "로그 기록을 확인합니다.", Toast.LENGTH_SHORT).show();
            Intent newIntent = new Intent(this, LogActivity.class);
            startActivity(newIntent);
            finish();

        } else if (id == R.id.nav_settings) {
            Intent newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.map_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent newIntent = new Intent(this, SettingActivity.class);
            startActivity(newIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
