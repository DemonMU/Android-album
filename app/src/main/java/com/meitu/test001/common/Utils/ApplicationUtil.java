package com.meitu.test001.common.Utils;

import android.util.DisplayMetrics;

/**
 * Created by meitu on 2017/8/4.
 */
public class ApplicationUtil {
    public static DisplayMetrics dm = BaseActivity.getBaseApplication().getResources().getDisplayMetrics(); //获取屏幕分辨率

    /**
     * 获取屏幕宽度。
     * @return 屏幕宽度（单位像素）
     */
    public static int getScreenWidthPx() {
        return dm.widthPixels;
    }
    /**
     * 获取屏幕高度
     * @return 屏幕高度（单位像素）
     */
    public static int getScreenHeightPx() {
        return dm.heightPixels;
    }

}
