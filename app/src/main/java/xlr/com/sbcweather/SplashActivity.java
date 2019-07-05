package xlr.com.sbcweather;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import xlr.com.utils.SbcUtils;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
    }

    void initData() {
        //延时2s，跳转。
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String city = (String) SbcUtils.get(SplashActivity.this, "001", "");
                System.out.println(city);
                if (!TextUtils.isEmpty(city)) {
                    //直接跳转主页面
                    Intent intent = new Intent(SplashActivity.this, ShowActivity.class);
                    intent.putExtra("cityType", city);
                    startActivity(intent);
                    finish();
                } else {
                    //跳转到登录页面
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}
