package com.meitu.test001.app.beauty.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.AlbumFolderBean;
import com.meitu.test001.common.Utils.BaseActivity;

import java.io.File;
import java.util.List;

/**
 * Created by meitu on 2017/7/16.
 */
public class AlbumSelectFoldersAdapter extends BaseAdapter {
    private List<AlbumFolderBean> mAlbumFolderBeanList;
    private Context context;

    public AlbumSelectFoldersAdapter(Context context, List<AlbumFolderBean> albumFolderBeanList) {
        this.mAlbumFolderBeanList = albumFolderBeanList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mAlbumFolderBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAlbumFolderBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.beauty_album_select_lvitem, null);
            viewHolder = new ViewHolder();
            viewHolder.ivAlbumFolderFrontCover =
                    (ImageView) convertView.findViewById(R.id.iv_album_select_album_frontCover);
            viewHolder.tvAlbumFolderName = (TextView) convertView.findViewById(R.id.tv_album_select_album_name);
            viewHolder.tvAlbumFolderNumbers = (TextView) convertView.findViewById(R.id.tv_album_select_album_number);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Uri uri = Uri.fromFile(new File(mAlbumFolderBeanList.get(position).getFrontCoverPath()));
        Glide.with(context).load(uri).centerCrop().into(viewHolder.ivAlbumFolderFrontCover);
        viewHolder.tvAlbumFolderName.setText(mAlbumFolderBeanList.get(position).getFolderName());
        String str = String.format("%s%s", mAlbumFolderBeanList.get(position).getPhotoNumbers(), BaseActivity.getBaseApplication().getResources().getString(R.string.photo_unit));
        viewHolder.tvAlbumFolderNumbers.setText(str);
        return convertView;
    }

    class ViewHolder {
        public ImageView ivAlbumFolderFrontCover;
        public TextView tvAlbumFolderName;
        public TextView tvAlbumFolderNumbers;
    }
}
