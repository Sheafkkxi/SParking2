package com.sheaf.sparking.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.sheaf.sparking.R;
import com.sheaf.sparking.beans.UserBean;

import java.util.ArrayList;
import java.util.List;

import scut.carson_ho.searchview.ICallBack;
import scut.carson_ho.searchview.SearchView;
import scut.carson_ho.searchview.bCallBack;

import static com.baidu.location.g.a.i;

public class MainActivity extends BaseActivity {

    private static final String TAG ="MainActivity" ;
    private DrawerLayout mDrawerLayout;
    public LocationClient mLocationClient;
    private BaiduMap mBaiduMap;
    private MapView mv;
    private boolean isFirstNavigate = true;
    private PoiSearch mPoiSearch;
    private static UserBean tempUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化位置服务
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        //导航页面
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_account_box_white_36dp);
        }
        navView.setCheckedItem(R.id.nav_carid);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //mDrawerLayout.closeDrawers();
                return true;
            }
        });

        mv = findViewById(R.id.mv_baidu);
        //绑定搜索框
        // 3. 绑定组件
        SearchView searchView = findViewById(R.id.search_view);

        // 4. 设置点击搜索按键后的操作（通过回调接口）
        // 参数 = 搜索框输入的内容
        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String key) {


                /*mPoiSearch.searchInCity((new PoiCitySearchOption())
                        .city(tempUser.getCity())
                        .keyword(key)
                        .pageNum(10));*/
                //搜索结果排序规则，PoiSortType.comprehensive->距离排序//检索半径范围，单位：米
                mPoiSearch.searchNearby(new PoiNearbySearchOption()
                        .sortType(PoiSortType.comprehensive)
                        .radius(5*1000)
                        .location(tempUser.getTempPosition())
                        .keyword(key));
                System.out.println("我收到了" + key);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAciton() {

                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        if (getPermission()) {
            requestLocation();
        }
        mPoiSearch = PoiSearch.newInstance();

        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
    }

    private boolean getPermission(){
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.
                permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.
                permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.
                permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }if(!permissionList.isEmpty()){
            String [] permission = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permission,1);
        }else {
            return true;
        }
        return  false;
    }
    //开始定位
    private void requestLocation(){
        mLocationClient.start();
        initLocation();
    }
    //初始化定位dd
    private void initLocation() {
        mBaiduMap = mv.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(1000);
        option.setIsNeedAddress(true);
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);
        mLocationClient.setLocOption(option);
    }

/*    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }*/

    @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()){
                /*case R.id.backup:
                    Toast.makeText(this,"Backup",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.delete:
                    Toast.makeText(this,"Delete",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.settings:
                    Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                    break;*/
                case android.R.id.home:
                    mDrawerLayout.openDrawer(GravityCompat.START);
                    break;
                default:
            }
    return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mv.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mv.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mv.onDestroy();
        mBaiduMap.setMyLocationEnabled(false);
        if(mPoiSearch!= null) {
            mPoiSearch.destroy();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result:grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须统一所有权限才能使用本程序",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append("").append(bdLocation.getLatitude()).
                    append("\n");
            StringBuilder append = currentPosition.append("经线：").append("" + bdLocation.getLongitude()).
                    append("\n").append(bdLocation.getCountry()).append(bdLocation.
                    getCity()).append(bdLocation.getStreet()).append("\n");
            //currentPosition.append(bdLocation.getCountry());
            currentPosition.append("定位方式："+bdLocation.getLocType());
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation)
                currentPosition.append("网络基站");
            tempUser = new UserBean();
            tempUser.setCity(bdLocation.getCity());
            tempUser.setTempPosition(new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude()));
            if (bdLocation.getLocType()==BDLocation.TypeGpsLocation||
                    bdLocation.getLocType()==BDLocation.TypeNetWorkLocation) {
                navigateTo(bdLocation);
            }
            //positionText.setText(currentPosition);
        }
    }

    private void navigateTo(BDLocation bdLocation) {
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(bdLocation.getLatitude());
        builder.longitude(bdLocation.getLongitude());
        Log.d(TAG, "navigateTo: 定位完成");
        MyLocationData myLocation = builder.build();
        mBaiduMap.setMyLocationData(myLocation);

        if (isFirstNavigate){
            Log.d(TAG, "navigateTo: 开始定位自己"+isFirstNavigate);
            LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            MapStatus mMapStatus = new MapStatus.Builder().target(ll).zoom(18).build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            mBaiduMap.animateMapStatus(mMapStatusUpdate);
            isFirstNavigate = false;
        }
    }

    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }

        public void onGetPoiResult(PoiResult result){

            //获取POI检索结果
            List<PoiInfo> allAddr = result.getAllPoi();
            if (allAddr == null) {
                return;
            }
            for (PoiInfo p: allAddr) {
                Log.d("MainActivity", "p.name--->" + p.name +"p.phoneNum"
                        + p.phoneNum +" -->p.address:" + p.address + "p.location" + p.location);
                /*mv.addOverlay(View.inflate(getApplicationContext(), R.layout.view_baidumap, null)
                        , new LatLng(p.location.latitude, p.location.longitude), "title");*/
                addOverlap(p.location);

            }
        }
        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }
    };

    private void addOverlap(LatLng ll){
        BitmapDescriptor bdC = BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_park);

        /*
        此处BitmapDescriptorFactory.fromView(view);采用的是自定义覆盖物
        view = View.inflate(getApplicationContext(), R.layout.view_baidumap, null);
        也可以使用BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)
        BitmapDescriptorFactory.fromBitmap(bitmap)
        */
        //LatLng ll = new LatLng(stations.get(i).getLatitude(), stations.get(i).getLongitude());
        MarkerOptions ooC = null;
        if (true) {//true 居中对齐
            ooC = new MarkerOptions().position(ll)//经纬度
                    .icon(bdC)//覆盖物的icon，可以选择icons(ArrayList<BitmapDescriptor>)多个icon实现轮播动画效果
                    .perspective(false)
                    .anchor(0.5f, 1f)//覆盖物的对齐点，0.5f,0.5f为覆盖物的中心点
                    .zIndex(i);
        }else{//左对齐
            ooC = new MarkerOptions().position(ll).icon(bdC)
                    .perspective(false).anchor(0f, 1f).zIndex(i);
        }

        if (true) {
            //生长动画
            ooC.animateType(MarkerOptions.MarkerAnimateType.grow);//还可以选择掉落的动画
        }
        mBaiduMap.addOverlay(ooC);
                        /*此处可以强转(Marker) (mBaidumap.addOverlay(ooD));
                            通过Marker.setPosition(LatLng)控制覆盖物的位置
*/


    }
}
