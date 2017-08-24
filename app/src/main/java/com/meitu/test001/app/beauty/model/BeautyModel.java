package com.meitu.test001.app.beauty.model;

import android.net.Uri;

import com.meitu.test001.app.beauty.Bean.AlbumFolderBean;
import com.meitu.test001.app.beauty.Bean.PhotoBean;

import java.util.List;

/**
 * Created by meitu on 2017/7/11.
 */
public interface BeautyModel {
    List<PhotoBean> getHomeDefaultPhotoList(String albumName);

    // 采用回调方法获取相册目录信息和文件信息
    void startScanPhotos(OnQueryAlbumInfoFinish onQueryAlbumInfoFinish);

    int deletePhotoFromAlbum(String photoPath);

    void getAllAlbumFolderInfo(OnGetAlbumFolderInfoFinish onGetAlbumFolderInfoFinish);

    Uri getPhotoUriOfStorage(String photoPath);

    interface OnQueryAlbumInfoFinish {
        /**
         * 查询结束的时候执行此函数
         *
         * @param albumFolderBeanList  返回扫描结果，不存在则返回null
         */
        void onFinish(List<AlbumFolderBean> albumFolderBeanList);
    }

    interface OnGetAlbumFolderInfoFinish {
        /**
         * 异步获取相册目录信息时候执行此函数
         *@param albumFolderBeanList 返回获取结果
         */
        void onFinish(List<AlbumFolderBean> albumFolderBeanList);

    }


}
