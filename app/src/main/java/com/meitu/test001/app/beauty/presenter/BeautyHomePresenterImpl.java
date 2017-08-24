package com.meitu.test001.app.beauty.presenter;

import android.net.Uri;
import android.util.Log;

import com.meitu.test001.app.beauty.Bean.PhotoBean;
import com.meitu.test001.app.beauty.model.BeautyModel;
import com.meitu.test001.app.beauty.model.BeautyModelImpl;
import com.meitu.test001.app.beauty.view.HomeView;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by meitu on 2017/7/11.
 */
public class BeautyHomePresenterImpl implements BeautyHomePresenter {
    private static final String TAG = "BeautyHomePresenterImpl";
    private BeautyModel mBeautyModel;
    private HomeView mHomeView;

    public BeautyHomePresenterImpl(HomeView mHomeView) {
        this.mBeautyModel = new BeautyModelImpl();
        this.mHomeView = mHomeView;
    }

    private List<PhotoBean> getHomeDefaultPhotoList(String albumName) {
        if (mBeautyModel != null) {
            return mBeautyModel.getHomeDefaultPhotoList(albumName);
        } else {
            return null;
        }
    }

    // 采用RxJava异步显示默认相册的照片
    @Override
    public void showHomeDefaultPhotos(final String albumName) {
        if (mHomeView != null) {
            Observable.create(new Observable.OnSubscribe<List<PhotoBean>>() {
                @Override
                public void call(Subscriber<? super List<PhotoBean>> subscriber) {
                    List<PhotoBean> photoBeanList;
                    photoBeanList = getHomeDefaultPhotoList(albumName);
                    if (photoBeanList == null)
                        subscriber.onNext(null);
                    else {
                        Log.d(TAG, "call: HomeDefaultPhotoNums--" + photoBeanList.size());
                        subscriber.onNext(photoBeanList);
                    }
                }
            })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<PhotoBean>>() {
                        @Override
                        public void call(List<PhotoBean> photoBeenList) {
                            Log.d(TAG, "call: ++" + photoBeenList.size());
                            mHomeView.showDefaultPhotos(photoBeenList);
                        }
                    });

        }
    }

    @Override
    public void openAlbum() {
        if (mHomeView != null)
            mHomeView.openAlbum();
    }

    @Override
    public void openCameraAndStorePhoto(String pathOfStorage) {
        if (mBeautyModel != null) {
            Uri photoUri = mBeautyModel.getPhotoUriOfStorage(pathOfStorage);
            if (mHomeView != null) {
                mHomeView.openCamera(photoUri);
            }
        }
    }

}
