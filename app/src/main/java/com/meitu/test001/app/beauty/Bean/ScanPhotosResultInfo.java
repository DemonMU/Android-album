package com.meitu.test001.app.beauty.Bean;

import java.util.List;

/**
 * 照片扫描结果
 * Created by meitu on 2017/7/14.
 */
public class ScanPhotosResultInfo {
    // 相册目录名称
    private List<String> albumFolderNameList;
    // 相册目录路径
    private List<String> albumFolderPathList;

    public void setAlbumFolderNameList(List<String> albumFolderNameList) {
        this.albumFolderNameList = albumFolderNameList;
    }

    public List<String> getAlbumFolderNameList() {

        return albumFolderNameList;
    }

    public List<String> getAlbumFolderPathList() {
        return albumFolderPathList;
    }

    public void setAlbumFolderPathList(List<String> albumFolderPathList) {
        this.albumFolderPathList = albumFolderPathList;
    }
}
