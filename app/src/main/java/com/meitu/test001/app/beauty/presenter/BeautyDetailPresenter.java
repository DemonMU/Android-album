package com.meitu.test001.app.beauty.presenter;

/**
 * Created by meitu on 2017/7/18.
 */
public interface BeautyDetailPresenter {
    // Beauty_BeautyDetailView
    void deletePhoto(String photoPath, OnDeletePhotoFinish onDeletePhotoFinish);
    /**
     * 异步删除相片时执行此函数
     *
     */
    interface OnDeletePhotoFinish {
        void onFinish();
    }
}


