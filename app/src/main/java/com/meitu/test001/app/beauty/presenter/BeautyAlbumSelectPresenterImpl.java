package com.meitu.test001.app.beauty.presenter;

import android.content.Context;

import com.meitu.test001.app.beauty.Bean.AlbumFolderBean;
import com.meitu.test001.app.beauty.model.BeautyModel;
import com.meitu.test001.app.beauty.model.BeautyModelImpl;
import com.meitu.test001.app.beauty.view.AlbumSelectView;

import java.util.List;

/**
 * Created by meitu on 2017/7/18.
 */
public class BeautyAlbumSelectPresenterImpl implements BeautyAlbumSelectPresenter {
    private BeautyModel mBeautyModel;
    private AlbumSelectView mAlbumSelectView;

    public BeautyAlbumSelectPresenterImpl(AlbumSelectView albumSelectView) {
        this.mBeautyModel = new BeautyModelImpl();
        this.mAlbumSelectView = albumSelectView;
    }

    @Override
    public void selectAlbum(final Context context) {
        if (mBeautyModel != null) {
            mBeautyModel.getAllAlbumFolderInfo(new BeautyModel.OnGetAlbumFolderInfoFinish() {
                @Override
                public void onFinish(List<AlbumFolderBean> albumFolderBeanList) {
                    if (mAlbumSelectView != null)
                        mAlbumSelectView.selectAlbum(context, albumFolderBeanList);
                }
            });
        }
    }

}
