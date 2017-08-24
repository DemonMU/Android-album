package com.meitu.test001.app.beauty.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.PhotoBean;

import java.io.File;
import java.util.List;

/**
 * Created by meitu on 2017/7/17.
 */
public class PhotoDetailAdapter extends PagerAdapter {
    private List<PhotoBean> mPhotoBeanList;
    private Context mContext;

    public PhotoDetailAdapter(Context context, List<PhotoBean> photoBeanList) {
        this.mPhotoBeanList = photoBeanList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        if (mPhotoBeanList == null)
            return 0;
        else
            return mPhotoBeanList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View galleryItemView = View.inflate(mContext, R.layout.beauty_detail_photo_item, null);
        ImageView photoIv = (ImageView) galleryItemView.findViewById(R.id.iv_detail_photo_item);
        PhotoBean photoBean = mPhotoBeanList.get(position);
        Uri uri = Uri.fromFile(new File(photoBean.getPath()));
        Glide.with(mContext).load(uri).centerCrop().into(photoIv);

        container.addView(galleryItemView);
        return galleryItemView;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
