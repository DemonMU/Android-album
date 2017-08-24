package com.meitu.test001.app.beauty.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.PhotoBean;

import java.io.File;
import java.util.List;

/**
 * Created by meitu on 2017/7/11.
 */
public class HomeDefaultPhotosAdapter extends RecyclerView.Adapter<HomeDefaultPhotosAdapter.HomeDefaultPhotoViewHolder> {

    private static final String TAG = "DefaultPhotosAdapter";
    private Context context;
    private List<PhotoBean> mDefaultPhotos;
    private OnItemClickListener mOnItemClickListener;

    public HomeDefaultPhotosAdapter(List<PhotoBean> mDefaultPhotos) {
        this.mDefaultPhotos = mDefaultPhotos;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    @Override
    public HomeDefaultPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.beauty_home_rv_item, parent, false);

        HomeDefaultPhotoViewHolder homeDefaultPhotoViewHolder = new HomeDefaultPhotoViewHolder(itemView);

        return homeDefaultPhotoViewHolder;
    }

    @Override
    public void onBindViewHolder(final HomeDefaultPhotoViewHolder holder, int position) {
        PhotoBean showPhoto = mDefaultPhotos.get(position);
        if (showPhoto.getPath() != null) {
            // 图片路径转换为uri
            Uri uri = Uri.fromFile(new File(showPhoto.getPath()));
            Log.d(TAG, "onBindViewHolder: path:" + showPhoto.getPath());
            Log.i(TAG, "onBindViewHolder: Uri:" + uri);
            Glide.with(context).load(uri).centerCrop().into(holder.showPhotosIv);
        }
        holder.showPhotosIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = holder.getLayoutPosition();
                mOnItemClickListener.onItemClick(v, pos);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDefaultPhotos.size();
    }

    class HomeDefaultPhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView showPhotosIv;

        public HomeDefaultPhotoViewHolder(View itemView) {
            super(itemView);
            showPhotosIv = (ImageView) itemView.findViewById(R.id.iv_rv_item);
        }
    }
}
