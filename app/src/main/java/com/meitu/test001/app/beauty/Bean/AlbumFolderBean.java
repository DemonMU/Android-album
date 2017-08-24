package com.meitu.test001.app.beauty.Bean;

import java.io.Serializable;

/**
 * Created by meitu on 2017/7/14.
 */
public class AlbumFolderBean implements Serializable {
    /**
     * 目录名
     */
    private String folderName;
    /**
     * 图片数量
     */
    private int photoNumbers;

    /**
     * 第一张图片的路径
     */
    private String frontCoverPath;

    public String getFrontCoverPath() {
        return frontCoverPath;
    }

    public void setFrontCoverPath(String frontCoverPath) {
        this.frontCoverPath = frontCoverPath;
    }

    public String getFolderName() {

        return folderName;
    }

    public void setFolderName(String folderName) {

        this.folderName = folderName;
    }

    public int getPhotoNumbers() {
        return photoNumbers;
    }

    public void setPhotoNumbers(int photoNumbers) {
        this.photoNumbers = photoNumbers;
    }

}
