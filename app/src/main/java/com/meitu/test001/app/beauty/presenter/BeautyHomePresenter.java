package com.meitu.test001.app.beauty.presenter;

/**
 * Created by meitu on 2017/7/18.
 */
public interface BeautyHomePresenter {
    // Beauty_HomeView
    void showHomeDefaultPhotos(String albumName);

    void openAlbum();

    void openCameraAndStorePhoto(String pathOfStorage);
}
