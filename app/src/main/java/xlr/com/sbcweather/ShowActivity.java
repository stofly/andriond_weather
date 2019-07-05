package xlr.com.sbcweather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import okhttp3.Call;
import okhttp3.Response;
import xlr.com.db.DBManager;
import xlr.com.model.*;
import xlr.com.utils.Config;

import java.util.List;

//实时天气类
//
public class ShowActivity extends AppCompatActivity {
    private String str;
    //布局类
    public DrawerLayout drawerLayout;
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    //title
    private Button navButton, navButton2;
    //now
    private TextView tvCity, tvdateTime, tvTemperatureNow, tvWeatherInfo;
    //forecast
    private LinearLayout forecastLayout;
    //aqi
    private TextView tvAqi, tvPm25;
    private TextView dressing, exercise, wash, travel;
    //图片
    private ImageView imageView;
    private Result result;

    //初始化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        //加载城市
        initData();
        //网络请求
        initNet(str);
        //初始化
        iniView();
        //设置图片

        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //相当于又进行重新请求
                initNet(str);
                //防止出现一直刷新一直旋转
                swipeRefresh.setRefreshing(false);
            }
        });
        //back:跳转至主页面
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(ShowActivity.this, MainActivity.class);
                startActivity(mainActivity);
                //go MainActivity,befor destroy  this fram
                finish();
            }
        });
        //home：跳转至城市管理页面
        navButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(ShowActivity.this, CityManageActivity.class);
                mainActivity.putExtra("cityType", str);
                startActivity(mainActivity);
                finish();
            }
        });
    }

    void iniView() {
        // 初始化各控件
        //布局类
        drawerLayout = this.<DrawerLayout>findViewById(R.id.drawer_layout);
        swipeRefresh = this.<SwipeRefreshLayout>findViewById(R.id.swipe_refresh);
        weatherLayout = this.<ScrollView>findViewById(R.id.weather_layout);
        //title
        navButton = this.<Button>findViewById(R.id.nav_button_back);
        navButton2 = this.<Button>findViewById(R.id.nav_button2_home);
        //now
        tvCity = this.<TextView>findViewById(R.id.city_text);
        tvdateTime = this.<TextView>findViewById(R.id.dateTime_text);
        tvTemperatureNow = this.<TextView>findViewById(R.id.temperature_text);
        tvWeatherInfo = findViewById(R.id.weather_info_text);
        imageView = this.<ImageView>findViewById(R.id.image_now);
        //forecast
        forecastLayout = this.<LinearLayout>findViewById(R.id.forecast_layout);
        //aqi
        tvAqi = this.<TextView>findViewById(R.id.aqi_text);
        tvPm25 = this.<TextView>findViewById(R.id.pm25_text);
        //suggestion
        dressing = this.<TextView>findViewById(R.id.dressing_text);
        exercise = this.<TextView>findViewById(R.id.exercise_text);
        wash = this.<TextView>findViewById(R.id.wash_text);
        travel = this.<TextView>findViewById(R.id.travel_text);

    }

    void initData() {
        str = getIntent().getExtras().get("cityType").toString().trim();
        Log.e("ShowActivity", "cityType:" + str);
    }

    /**
     * 初始化网络连接
     *
     * @param type
     */
    private void initNet(String type) {
        //显示Material进度样式
        final SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("获取天气中...");
        pDialog.setCancelable(false);
        pDialog.show();
        //联网获取天气数据
        OkGo.get(Config.HOST)
                .params("cityname", type)
                .params("key", Config.Key)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(final String s, Call call, Response response) {
                        Log.e("ShowActivity", "onSuccess: ========---------======" + s);
                        Gson gson = new Gson();
                        //根json
                        Root root = gson.fromJson(s, Root.class);
                        //获取数据
                        if (root.getResult() != null) {
                            result = root.getResult();
                            showWeatherInfo(result);
                            setImage(result);
                            pDialog.dismiss();
                        } else {
                            pDialog.dismiss();
                            new SweetAlertDialog(ShowActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("提示信息")
                                    .setContentText("该地区暂无天气信息！！！")
                                    .show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent chosseIntent = new Intent(ShowActivity.this, MainActivity.class);
                                    startActivity(chosseIntent);
                                    finish();
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        Toast.makeText(ShowActivity.this, "当前网络繁忙，请稍后再试", Toast.LENGTH_SHORT);
                    }
                });
    }

    //加载数据
    private void showWeatherInfo(Result result) {
        List<Future> future = result.getFuture();
        Today today = result.getToday();
        Sk sk = result.getSk();
        //今日天气
        tvCity.setText(today.getCity());
        tvdateTime.setText(sk.getTime());
        tvTemperatureNow.setText(sk.getTemp() + "℃");
        tvWeatherInfo.setText(sk.getWind_direction());
        //未来五日预报
        forecastLayout.removeAllViews();
        for (int i = 0; i < future.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView tvWeather = view.<TextView>findViewById(R.id.weather_text);
            TextView tvWind = view.<TextView>findViewById(R.id.wind_text);
            TextView tvWeek = view.<TextView>findViewById(R.id.week_text);
            TextView tvTemperatureForecast = view.<TextView>findViewById(R.id.temperature_text);

            System.out.println(future.get(i).getDate() + "---" + future.get(i).getWeather() + "---" + future.get(i).getWind() + "---" + future.get(i).getWeek() + "---" + future.get(i).getTemperature());
            tvWeather.setText(future.get(i).getWeather());
            tvWind.setText(future.get(i).getWind());
            tvWeek.setText(future.get(i).getWeek());
            tvTemperatureForecast.setText(future.get(i).getTemperature());
            forecastLayout.addView(view);
        }
        //aqi
        //天气实况
        if (sk != null) {
            tvAqi.setText(today.getUv_index());
            tvPm25.setText(sk.getHumidity());
        }
        //生活指数建议
        String dressingPonit = "穿衣指数：" + today.getDressing_advice();
        String exercisePonit = "晨练指数：" + today.getExercise_index();
        String cartWashPonit = "洗车指数：" + today.getWash_index();
        String travelPonit = "旅行指数：" + today.getTravel_index();
        dressing.setText(dressingPonit);
        exercise.setText(exercisePonit);
        wash.setText(cartWashPonit);
        travel.setText(travelPonit);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    public void add(View view) {
        Today today = result.getToday();
        String city = today.getCity();
        DBManager dbManager = new DBManager(ShowActivity.this);
        int city1 = dbManager.getCity(today.getCity());
        if(city1!=0){
            new SweetAlertDialog(ShowActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("提示信息")
                    .setContentText("该地区已添加！！！")
                    .show();
        }else{
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("提示")
                    .setContentText("是否添加常用城市？")
                    .setCancelText("否")
                    .setConfirmText("是")
                    .showCancelButton(true)
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {
                            Today today = result.getToday();
                            Sk sk = result.getSk();
                            Temperature temperature = new Temperature(today.getCity(), today.getWeather(), sk.getTemp());
                            DBManager dbManager = new DBManager(ShowActivity.this);
                            dbManager.addTemperature(temperature);
                            sDialog.dismissWithAnimation();
                            Toast.makeText(ShowActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
        }

    }

    //根据不同状态显示不同的图片
    void setImage(Result result) {
        System.out.println(result);
        if (result.getToday().getWeather().contains("晴")) {
            imageView.setBackgroundResource(R.drawable.sun);
        } else if (result.getToday().getWeather().contains("雨")) {
            imageView.setBackgroundResource(R.drawable.rain);
        } else if (result.getToday().getWeather().contains("云")) {
            imageView.setBackgroundResource(R.drawable.cloud);
        } else if (result.getToday().getWeather().contains("阴")) {
            imageView.setBackgroundResource(R.drawable.wind);
        }
    }

}
