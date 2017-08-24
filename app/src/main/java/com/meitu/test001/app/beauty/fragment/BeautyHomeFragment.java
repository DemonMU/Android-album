package com.meitu.test001.app.beauty.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.PhotoBean;
import com.meitu.test001.app.beauty.adapter.HomeDefaultPhotosAdapter;
import com.meitu.test001.app.beauty.presenter.BeautyHomePresenter;
import com.meitu.test001.app.beauty.presenter.BeautyHomePresenterImpl;
import com.meitu.test001.app.beauty.view.HomeView;
import com.meitu.test001.common.Utils.BaseFragment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by meitu on 2017/7/19.
 */
public class BeautyHomeFragment extends BaseFragment implements HomeView {
    private static final String TAG = "BeautyHomeFragment";
    private RecyclerView mHomeGridRv;
    public BeautyHomePresenter mBeautyHomePresenter;
    private HomeDefaultPhotosAdapter mHomeDefaultPhotosAdapter;
    private String mDefaultAlbumName;
    private PhotoSelectFinished mPhotoSelectFinished;
    private TitleCenterTextListener mTitleCenterTextListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mPhotoSelectFinished = (PhotoSelectFinished) activity;
        mTitleCenterTextListener = (TitleCenterTextListener) activity;
    }

    public interface PhotoSelectFinished {
        void selectPhotoFinished(Bundle bundle);
    }

    public interface TitleCenterTextListener {
        void setTitleCenter(String text);
    }

    public void setDefaultAlbumName(String mDefaultAlbumName) {
        this.mDefaultAlbumName = mDefaultAlbumName;
    }

    public String getDefaultAlbumName() {
        return mDefaultAlbumName;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beauty_home_fragment, container, false);
        mHomeGridRv = (RecyclerView) view.findViewById(R.id.grid_rv);
        getTransitiveData();
        mBeautyHomePresenter = new BeautyHomePresenterImpl(this);
        mBeautyHomePresenter.showHomeDefaultPhotos(mDefaultAlbumName);
        return view;
    }

    private void getTransitiveData() {
        if (mDefaultAlbumName == null)
            mDefaultAlbumName = "Camera";
    }

    @Override
    public void showDefaultPhotos(final List<PhotoBean> mDefaultPhotos) {
        Log.d(TAG, "showDefaultPhotos: +++" + mDefaultPhotos.size());
        mTitleCenterTextListener.setTitleCenter(mDefaultAlbumName);
        if (mDefaultPhotos == null) {
            mBeautyHomePresenter.openAlbum();
            return;
        }
        mHomeDefaultPhotosAdapter = new HomeDefaultPhotosAdapter(mDefaultPhotos);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getActivity(), 4);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mHomeGridRv.setLayoutManager(gridLayoutManager);
        mHomeGridRv.setAdapter(mHomeDefaultPhotosAdapter);
        /*if (mPhotoUriOfStorage != null) {
            mHomeDefaultPhotosAdapter.notifyDataSetChanged();
        }*/
        mHomeDefaultPhotosAdapter.setOnItemClickListener(new HomeDefaultPhotosAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("PhotoBeanList", (Serializable) mDefaultPhotos);
                bundle.putInt("PhotoPosition", position);
                bundle.putString("albumName", mDefaultAlbumName);
                mPhotoSelectFinished.selectPhotoFinished(bundle);
            }
        });
    }

    @Override
    public void openAlbum() {

    }

    @Override
    public void openCamera(Uri uriOfStorage) {

    }

}
