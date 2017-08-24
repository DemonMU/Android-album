package com.meitu.test001.app.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.meitu.test001.R;
import com.meitu.test001.app.beauty.activity.BeautyActivity;
import com.meitu.test001.common.Utils.BaseActivity;

public class HomeActivity extends BaseActivity {
    private Button mBeautyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        setContentView(R.layout.home_activity);
        mBeautyBtn = (Button) findViewById(R.id.btn_beauty);
        mBeautyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, BeautyActivity.class);
                startActivity(intent);
            }
        });

    }
}
