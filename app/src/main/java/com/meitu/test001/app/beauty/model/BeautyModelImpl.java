package com.meitu.test001.app.beauty.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.meitu.test001.app.beauty.Bean.AlbumFolderBean;
import com.meitu.test001.app.beauty.Bean.PhotoBean;
import com.meitu.test001.common.Utils.BaseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by meitu on 2017/7/11.
 */
public class BeautyModelImpl implements BeautyModel {
    public static String TAG = "BeautyModelImpl";
    public ContentResolver contentResolver = BaseActivity.getBaseApplication().getContentResolver();
    public Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    public OnQueryAlbumInfoFinish mOnQueryAlbumInfoFinish;
    public OnGetAlbumFolderInfoFinish mOnGetAlbumFolderInfoFinish;
    /**
     * 加载相册数据的映射
     */
    private final static String[] IMAGE_ALBUM_PROJECTION = new String[] {"count([_id])",
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,// 相册名称
        MediaStore.Images.Thumbnails.DATA // 缩略图路径
        };
    /**
     * 加载相册目录数据的Selection
     */
    private final static String IMAGE_ALBUM_SELECTION = "1=1) group by ([bucket_id]";
    /**
     * 加载相片数据的映射
     */
    private final static String[] IMAGE_PHOTO_PROJECTION = new String[] {MediaStore.Images.Media.DATA,// 图片路径
        MediaStore.Images.Media._ID};

    /**
     * @param albumName
     * @return 首页默认的照片信息数组
     */
    @Override
    public List<PhotoBean> getHomeDefaultPhotoList(String albumName) {
        List<PhotoBean> photoList = new ArrayList<>();
        Cursor cursor =
            contentResolver.query(uri, IMAGE_PHOTO_PROJECTION, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?",
                new String[] {albumName}, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        if (cursor == null || cursor.getCount() <= 0)
            return null; // 没有图片
        while (cursor.moveToNext()) {
            int photoPathIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            int photoIdIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            String path = cursor.getString(photoPathIndex);
            String id = cursor.getString(photoIdIndex);
            Log.d(TAG, "getHomeDefaultPhotoList: +id--" + id);
            PhotoBean photo = new PhotoBean();
            photo.setPath(path);
            photo.setId(id);
            photoList.add(photo);
            Log.i(TAG, "getHomeDefaultPhotoList:  path" + path);
        }
        cursor.close();
        if (photoList != null)
            return photoList;
        else {
            Log.i(TAG, "getHomeDefaultPhotoList: " + "null");
            return null;
        }

    }

    public void startScanPhotos(OnQueryAlbumInfoFinish onQueryAlbumInfoFinish) {
        mOnQueryAlbumInfoFinish = onQueryAlbumInfoFinish;
        // 查询所有照片信息
        Cursor cursor =
            contentResolver.query(uri, IMAGE_ALBUM_PROJECTION, IMAGE_ALBUM_SELECTION, null,
                MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        // 没有图片
        if (cursor == null || cursor.getCount() <= 0)
            Log.d(TAG, "startScanPhotos: " + "查询cursor为null");
        // 相册目录信息链表
        List<AlbumFolderBean> albumFolderBeanList = new ArrayList<>();
        while (cursor.moveToNext()) {
            AlbumFolderBean albumFolderBean = new AlbumFolderBean();
            int photoNum = cursor.getInt(0);
            String albumName = cursor.getString(1);
            String thumbnailsPath = cursor.getString(2);
            albumFolderBean.setFolderName(albumName);
            albumFolderBean.setPhotoNumbers(photoNum);
            albumFolderBean.setFrontCoverPath(thumbnailsPath);
            albumFolderBeanList.add(albumFolderBean);
            Log.d(TAG, "startScanPhotos: photoNum--" + photoNum);
            Log.d(TAG, "startScanPhotos: albumName--" + albumName);
        }

        cursor.close();

        mOnQueryAlbumInfoFinish.onFinish(albumFolderBeanList);
    }

    @Override
    public int deletePhotoFromAlbum(String photoId) {
        File deleteFile = new File(photoId);
        deleteFile.delete();
        // String where = MediaStore.Images.Media._ID + "='" + photoId + "'";
        String where = String.format("%s='%s'", MediaStore.Images.Media._ID, photoId);
        // 删除图片
        return contentResolver.delete(uri, where, null);
    }

    @Override
    public void getAllAlbumFolderInfo(OnGetAlbumFolderInfoFinish onGetAlbumFolderInfoFinish) {
        mOnGetAlbumFolderInfoFinish = onGetAlbumFolderInfoFinish;
        Observable.create(new Observable.OnSubscribe<List<AlbumFolderBean>>() {
            @Override
            public void call(final Subscriber<? super List<AlbumFolderBean>> subscriber) {
                // 异步执行扫描相册
                startScanPhotos(new OnQueryAlbumInfoFinish() {
                    @Override
                    public void onFinish(List<AlbumFolderBean> albumFolderBeanList) {
                        subscriber.onNext(albumFolderBeanList);
                    }
                });
            }
        })
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<List<AlbumFolderBean>>() {
                @Override
                public void call(List<AlbumFolderBean> albumFolderBeanList) {
                    mOnGetAlbumFolderInfoFinish.onFinish(albumFolderBeanList);
                }
            });

    }

    @Override
    public Uri getPhotoUriOfStorage(String photoPath) {
        // 创建File对象，用于存储拍照后的图片(以时间秒为文件名)
        // File outputImage = new File(photoPath + Calendar.getInstance().getTimeInMillis() + ".jpg");
        String str = String.valueOf(Calendar.getInstance().getTimeInMillis());
        String path = String.format("%s%s.jpg", photoPath, str);
        File outputImage = new File(path);
        File outputDir = new File(photoPath);
        if (!outputDir.exists()) {
            outputDir.mkdir();
        }
        Uri imageUri = Uri.fromFile(outputImage);
        Log.d(TAG, "getPhotoUriOfStorage: imageUri--" + imageUri);

        return imageUri;
    }

}
