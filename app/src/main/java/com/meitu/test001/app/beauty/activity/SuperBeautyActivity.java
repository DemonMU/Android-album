package com.meitu.test001.app.beauty.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.PhotoBean;
import com.meitu.test001.common.Utils.BaseActivity;
import com.meitu.test001.component.DermabrasionImageView;

import java.util.Hashtable;

/**
 * Created by meitu on 2017/7/26.
 */
public class SuperBeautyActivity extends BaseActivity {
    private static final String TAG = "SuperBeautyActivity";
    private PhotoBean mPendingPhoto;
    private DermabrasionImageView mDermabrasionImageView;
    private TextView mLightCleanTextView;
    private TextView mEraserTextView;
    private TextView mCancelTextView;
    private int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.beauty_detail_super_beauty_activity);
        mDermabrasionImageView = (DermabrasionImageView) findViewById(R.id.iv_super_beauty_pending_photo);
        mLightCleanTextView = (TextView) findViewById(R.id.tv_super_beauty_light_clean);
        mEraserTextView = (TextView) findViewById(R.id.tv_super_beauty_eraser);
        mCancelTextView = (TextView) findViewById(R.id.tv_super_beauty_cancel);

        getTransitiveData();
        mDermabrasionImageView.displayPhoto(mPendingPhoto.getPath());
        mEraserTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDermabrasionImageView.setEraseMode(true);
                return true;
            }
        });
        initData();
    }

    private void getTransitiveData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPendingPhoto = (PhotoBean) bundle.getSerializable("pendingPhoto");
            Log.d(TAG, "getTransitiveData: " + mPendingPhoto.getPath());
        }
    }

    private void initData() {
    }

}
