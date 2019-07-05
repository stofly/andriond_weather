package xlr.com.sbcweather;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import xlr.com.db.DBManager;
import xlr.com.model.Temperature;

import java.util.List;

public class CityManageActivity extends AppCompatActivity{
    public SwipeRefreshLayout swipeRefresh;
    private ScrollView weatherLayout;
    private LinearLayout cityManagelayout;
    private List<Temperature> allTemperature;
    private DBManager dbManager;
    private Button navButton,navButton2;
    private TextView tv_title;
    private String str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_manage);
        cityManagelayout = this.<LinearLayout>findViewById(R.id.cityManage_layout);
        swipeRefresh = this.<SwipeRefreshLayout>findViewById(R.id.swipe_refresh);
        weatherLayout = this.<ScrollView>findViewById(R.id.weather_layout);
        //加载布局文件
        initView();
        initData();
        navButton = this.<Button>findViewById(R.id.nav_button_back);
        navButton2 = this.<Button>findViewById(R.id.nav_button2_home);
        //隐藏home图标
        navButton2.setVisibility(View.GONE);

        tv_title = this.<TextView>findViewById(R.id.tv_title);
        //隐藏文字图标
        tv_title.setText("常用城市管理");


        //back:跳转天气详情页面
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(CityManageActivity.this,ShowActivity.class);
                mainActivity.putExtra("cityType", str);
                startActivity(mainActivity);
                finish();
            }
        });
        //下拉刷新
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //相当于又进行重新请求
                initView();
                //防止出现一直刷新一直旋转
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    void initData() {
        str = getIntent().getExtras().get("cityType").toString().trim();
        Log.e("ShowActivity", "cityType:" + str);
    }

    void initView(){
        dbManager = new DBManager(CityManageActivity.this);
        allTemperature = dbManager.getAllTemperature();
        cityManagelayout.removeAllViews();
        for (int i = 0; i <allTemperature.size(); i++) {
            View view = LayoutInflater.from(this).inflate(R.layout.city_item, cityManagelayout, false);
            Button btnUpdata = view.<Button>findViewById(R.id.updata_btn);
            Button btndiscr = view.<Button>findViewById(R.id.discr_btn);
            final TextView cityMgName = view.<TextView>findViewById(R.id.cityMgName_text);
            TextView cityMgweather = view.<TextView>findViewById(R.id.cityMgweather_text);
            TextView cityMgtemperature = view.<TextView>findViewById(R.id.cityMgtemperature_text);
            cityMgName.setText(allTemperature.get(i).getCname());
            cityMgweather.setText(allTemperature.get(i).getCweather());
            cityMgtemperature.setText(allTemperature.get(i).getCtemperature());
            cityManagelayout.addView(view);
            btnUpdata.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBManager dbManager = new DBManager(CityManageActivity.this);
                    dbManager.deleteTemperature(cityMgName.getText().toString());
                    initView();
                }
            });
            //点击详情，返回该城市的天气详情
            btndiscr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent showIntent = new Intent(CityManageActivity.this,ShowActivity.class);
                    showIntent.putExtra("cityType", cityMgName.getText().toString());
                    startActivity(showIntent);
                    finish();
                }
            });
        }
        //下滑显示
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
