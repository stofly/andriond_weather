package xlr.com.sbcweather;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import xlr.com.adapter.CityRecyclerAdapter;
import xlr.com.db.DBManager;
import xlr.com.model.City;
import xlr.com.utils.AddressResolutionUtil;
import xlr.com.utils.SbcUtils;

import java.util.List;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private RecyclerView mRecyCity;
    private DBManager dbManager;
    private List<City> allCities;
    private SideBar mContactSideber;
    private CityRecyclerAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private AddressResolutionUtil addressResolutionUtil;

    private final int RC_SETTINGS_SCREEN = 125;
    private final int RC_LOCATION_CONTACTS_PERM = 124;
    private AMapLocationClient mLocationClient =null;

    String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initLocation();
    }


    private void initView() {
        dbManager = new DBManager(this);
        dbManager.copyDBFile();
        mRecyCity = (RecyclerView) findViewById(R.id.recy_city);
        TextView mContactDialog = (TextView) findViewById(R.id.contact_dialog);
        mContactSideber = (SideBar) findViewById(R.id.contact_sidebar);
        mContactSideber.setTextView(mContactDialog);
    }

    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void initLocation() {
        //高德定位
        //初始化client
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位参数
        mLocationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onLocationChanged(AMapLocation loc) {
                if (null != loc) {
                    if(loc.getErrorCode()==0){
                        //定位获取对应的市行政名称
                        addressResolutionUtil = new AddressResolutionUtil();
                        String cityWeather = addressResolutionUtil.addressResolution(loc.getAddress()).get(0).get("city");
                        String substring = cityWeather.substring(0, cityWeather.length() - 1);
                        adapter.updateLocateState(CityRecyclerAdapter.SUCCESS,substring);
                        //如果为空则将key值为001得添加进去
                       if(!SbcUtils.contains(MainActivity.this,"001")){
                            SbcUtils.put(MainActivity.this,"001",substring);
                        }else{
                           //如果包含，先将文件内得数据全部清空，再将key值为001得添加进去
                            SbcUtils.clear(MainActivity.this);
                            SbcUtils.put(MainActivity.this,"001",substring);
                        }
                    }else{
                        adapter.updateLocateState(CityRecyclerAdapter.FAILED, null);
                        //打印错误输出结果
                        Log.e("errorInfo",loc.getErrorInfo());
                    }
                } else {
                    adapter.updateLocateState(CityRecyclerAdapter.FAILED, null);
                }
            }
        });
        //权限判断，当没有打开位置权限，会进行询问
        if (EasyPermissions.hasPermissions(this, perms)) {
            mLocationClient.startLocation();
        } else {
            EasyPermissions.requestPermissions(this, "定位需要相关权限",
                    RC_LOCATION_CONTACTS_PERM, perms);
        }
    }
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }



    @AfterPermissionGranted(RC_LOCATION_CONTACTS_PERM)
    private void initData() {
        allCities = dbManager.getAllCities();
        adapter = new CityRecyclerAdapter(MainActivity.this, allCities);
        linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        mRecyCity.setLayoutManager(linearLayoutManager);
        mRecyCity.setAdapter(adapter);

        adapter.setOnCityClickListener(new CityRecyclerAdapter.OnCityClickListener() {
            @Override
            public void onCityClick(String name) {
                //获取城市
                Log.e("MainActivity", "onCityClick:" + name);
                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                //传递城市名称
                Intent showIntent = new Intent(MainActivity.this, ShowActivity.class);
                //传值
                showIntent.putExtra("cityType", name);
                //跳转城市
                startActivity(showIntent);
                finish();
            }

            @Override
            public void onLocateClick() {
                //重新定位
                Log.e("MainActivity", "onLocateClick");

                adapter.updateLocateState(CityRecyclerAdapter.LOCATING, null);

                if (EasyPermissions.hasPermissions(MainActivity.this, perms)) {

                    mLocationClient.startLocation();
                } else {
                    // Ask for both permissions
                    EasyPermissions.requestPermissions(MainActivity.this, "定位需要相关权限",RC_LOCATION_CONTACTS_PERM, perms);
                }
            }
        });


        mContactSideber.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {

                int position = adapter.getPositionForSection(s);
                if (position != -1) {
//                    mRecyCity.scrollToPosition(position);
                    linearLayoutManager.scrollToPositionWithOffset(position, 0);
                }

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }


    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.title_settings_dialog))
                    .setPositiveButton(R.string.setting)
                    .setNegativeButton(R.string.cancel)
                    .setRequestCode(RC_SETTINGS_SCREEN)
                    .build()
                    .show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SETTINGS_SCREEN) {
            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(this, R.string.returned_from_app_settings_to_activity, Toast.LENGTH_SHORT)
                    .show();
        }
    }

}
