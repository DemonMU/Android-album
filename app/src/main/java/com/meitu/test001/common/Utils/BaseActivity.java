package com.meitu.test001.common.Utils;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by meitu on 2017/7/13.
 */
public class BaseActivity extends Activity {
    private static Application mApplication;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = this.getApplication();
    }

    public static Application getBaseApplication() {
        return mApplication;

    }
}
