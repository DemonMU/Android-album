package com.meitu.test001.app.beauty.view;

import android.content.Context;
import android.net.Uri;

import com.meitu.test001.app.beauty.Bean.AlbumFolderBean;

import java.util.List;

/**
 * Created by meitu on 2017/7/16.
 */
public interface AlbumSelectView {
    void selectAlbum(Context context, List<AlbumFolderBean> albumFolderBeanList);
}
