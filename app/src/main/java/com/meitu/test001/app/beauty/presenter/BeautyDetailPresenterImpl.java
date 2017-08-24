package com.meitu.test001.app.beauty.presenter;

import com.meitu.test001.app.beauty.model.BeautyModel;
import com.meitu.test001.app.beauty.model.BeautyModelImpl;
import com.meitu.test001.app.beauty.view.BeautyDetailView;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by meitu on 2017/7/18.
 */
public class BeautyDetailPresenterImpl implements BeautyDetailPresenter {
    private static final String TAG = "BeautyDetailPreImpl";
    private BeautyModel mBeautyModel;
    private BeautyDetailView mBeautyDetailView;

    public BeautyDetailPresenterImpl(BeautyDetailView beautyDetailView) {
        this.mBeautyModel = new BeautyModelImpl();
        this.mBeautyDetailView = beautyDetailView;
    }

    @Override
    public void deletePhoto(final String photoId, final OnDeletePhotoFinish onDeletePhotoFinish) {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (mBeautyModel != null) {
                    mBeautyModel.deletePhotoFromAlbum(photoId);
                }
                subscriber.onNext(mBeautyModel.deletePhotoFromAlbum(photoId));
            }
        })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Integer>() {
                @Override
                public void call(Integer s) {
                    onDeletePhotoFinish.onFinish();
                }
            });
    }

}
