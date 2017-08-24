package com.meitu.test001.app.beauty.Bean;

import java.io.Serializable;

/**
 * Created by meitu on 2017/7/11.
 */
public class PhotoBean implements Serializable {
    private static final long serialVersionUID = 15L;

    private String id;

    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
