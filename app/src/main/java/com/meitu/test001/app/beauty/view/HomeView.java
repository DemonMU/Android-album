package com.meitu.test001.app.beauty.view;

import android.net.Uri;

import com.meitu.test001.app.beauty.Bean.PhotoBean;

import java.util.List;

/**
 * Created by meitu on 2017/7/11.
 */
public interface HomeView {
    void showDefaultPhotos(List<PhotoBean> mDefaultPhotos);

    void openAlbum();

    void openCamera(Uri uriOfStorage);
}
