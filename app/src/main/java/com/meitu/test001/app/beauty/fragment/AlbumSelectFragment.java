package com.meitu.test001.app.beauty.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.AlbumFolderBean;
import com.meitu.test001.app.beauty.adapter.AlbumSelectFoldersAdapter;
import com.meitu.test001.app.beauty.presenter.BeautyAlbumSelectPresenter;
import com.meitu.test001.app.beauty.presenter.BeautyAlbumSelectPresenterImpl;
import com.meitu.test001.app.beauty.view.AlbumSelectView;
import com.meitu.test001.common.Utils.BaseFragment;

import java.util.List;

/**
 * Created by meitu on 2017/7/19.
 */
public class AlbumSelectFragment extends BaseFragment implements AlbumSelectView {
    private static final String TAG = "AlbumSelectFragment";
    private ListView mOpenAlbumLv;
    public BeautyAlbumSelectPresenter mBeautyAlbumSelectPresenter;
    private AlbumSelectFoldersAdapter mAlbumSelectFoldersAdapter;
    private AlbumSelectFinished mAlbumSelectFinished;
    private String mSelectAlbumName;

    public interface TitleTextCallBack {
        void setTitleCenterText(String text);

        void setTitleLeftText(String text);
    }

    public interface AlbumSelectFinished {
        void selectAlbumFinished(String albumName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAlbumSelectFinished = (AlbumSelectFinished) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beauty_album_select_fragment, container, false);
        mOpenAlbumLv = (ListView) view.findViewById(R.id.lv_album_select);
        mBeautyAlbumSelectPresenter = new BeautyAlbumSelectPresenterImpl(this);
        mBeautyAlbumSelectPresenter.selectAlbum(this.getActivity());
        return view;
    }

    public void setTransitiveData(TitleTextCallBack titleTextCallBack) {
        // 向HomeActivity传递mDefaultAlbumName的数值
        titleTextCallBack.setTitleLeftText(getResources().getString(R.string.beauty_select_album_title_left_text));
        titleTextCallBack.setTitleCenterText(getResources().getString(R.string.beauty_select_album_title_right_text));
    }

    @Override
    public void selectAlbum(Context context, final List<AlbumFolderBean> albumFolderBeanList) {
        mAlbumSelectFoldersAdapter = new AlbumSelectFoldersAdapter(context, albumFolderBeanList);
        mAlbumSelectFoldersAdapter.notifyDataSetChanged();
        mOpenAlbumLv.setAdapter(mAlbumSelectFoldersAdapter);
        mOpenAlbumLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectAlbumName = albumFolderBeanList.get(position).getFolderName();

                mAlbumSelectFinished.selectAlbumFinished(mSelectAlbumName);
            }
        });
    }
}
