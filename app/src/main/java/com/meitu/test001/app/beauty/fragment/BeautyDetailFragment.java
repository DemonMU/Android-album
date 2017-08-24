package com.meitu.test001.app.beauty.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.PhotoBean;
import com.meitu.test001.app.beauty.activity.SuperBeautyActivity;
import com.meitu.test001.app.beauty.adapter.PhotoDetailAdapter;
import com.meitu.test001.app.beauty.presenter.BeautyDetailPresenter;
import com.meitu.test001.app.beauty.presenter.BeautyDetailPresenterImpl;
import com.meitu.test001.app.beauty.view.BeautyDetailView;
import com.meitu.test001.common.Utils.BaseFragment;

import java.util.List;

/**
 * Created by meitu on 2017/7/19.
 */
public class BeautyDetailFragment extends BaseFragment implements BeautyDetailView, View.OnClickListener {
    private static final String TAG = "BeautyDetailFragment";
    private ImageView mDetailPhotoDeleteIv;
    private BeautyDetailPresenter mBeautyDetailPresenter;
    private List<PhotoBean> mPhotoBeanList;
    private ViewPager mDetailPhotoVp;
    private int mCurrentPhotoId;
    private String mAlbumName;
    private PhotoDetailAdapter photoDetailAdapter;
    private PhotoDetailChangeListener mPhotoDetailChangeListener;
    private RelativeLayout mBeautyDetailIntoSuperbeautyRl;
    private PhotoBean mCurrentPhoto;
    public Bundle bundle;
    public TitleCenterTextListener mTitleCenterTextListener;
    public DeletePhotoToZeroListener mDeletePhotoToZeroListener;

    public interface TitleCenterTextListener {
        void setTitleCenter(String text);
    }

    public interface DeletePhotoToZeroListener {
        void deletePhotoToZero();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mTitleCenterTextListener = (TitleCenterTextListener) activity;
        mDeletePhotoToZeroListener = (DeletePhotoToZeroListener) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.beauty_detail_fragment, container, false);
        mBeautyDetailPresenter = new BeautyDetailPresenterImpl(this);
        mBeautyDetailIntoSuperbeautyRl = (RelativeLayout) view.findViewById(R.id.rl_beauty_detail_into_superbeauty);
        mDetailPhotoDeleteIv = (ImageView) view.findViewById(R.id.iv_detail_photo_delete);
        mDetailPhotoVp = (ViewPager) view.findViewById(R.id.vp_detail_photo);
        mDetailPhotoDeleteIv.setOnClickListener(this);
        mBeautyDetailIntoSuperbeautyRl.setOnClickListener(this);
        getTransitiveData(bundle);

        return view;
    }

    public void getTransitiveData(Bundle bundle) {
        if (bundle != null) {
            mPhotoBeanList = (List<PhotoBean>) bundle.getSerializable("PhotoBeanList");
            mCurrentPhotoId = bundle.getInt("PhotoPosition", 0);
            mAlbumName = bundle.getString("albumName");
            mCurrentPhoto = mPhotoBeanList.get(mCurrentPhotoId);
            if (mAlbumName != null)
                // mBeautyActivity.setTitleLeftText(mAlbumName);
                if (mPhotoBeanList != null) {
                    photoDetailAdapter = new PhotoDetailAdapter(this.getActivity(), mPhotoBeanList);
                    mDetailPhotoVp.setAdapter(photoDetailAdapter);
                    mDetailPhotoVp.setCurrentItem(mCurrentPhotoId);
                    mTitleCenterTextListener.setTitleCenter(mCurrentPhotoId + 1 + "/" + mPhotoBeanList.size());
                }
            mPhotoDetailChangeListener = new PhotoDetailChangeListener();
            mDetailPhotoVp.addOnPageChangeListener(mPhotoDetailChangeListener);
            Log.d(TAG, "onCreate: " + mPhotoBeanList.get(0).getPath());
        }
    }

    @Override
    public void deletePhotoFromAlbum() {
        AlertDialog.Builder alDialog = new AlertDialog.Builder(getActivity());
        alDialog.setTitle(getResources().getString(R.string.alertdialog_delete_title));
        alDialog.setMessage(getResources().getString(R.string.alertdialog_delete_message));
        alDialog.setPositiveButton(getResources().getString(R.string.alertdialog_delete_positive_button_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPhotoBeanList != null) {
                            Log.d(TAG, "onClick: " + "alertDialog");
                            // 异步删除照片
                            if (mPhotoBeanList.size() == 1) {
                                mBeautyDetailPresenter.deletePhoto(mPhotoBeanList.get(mCurrentPhotoId).getId(),
                                        new BeautyDetailPresenter.OnDeletePhotoFinish() {
                                            @Override
                                            public void onFinish() {
                                                mPhotoBeanList.remove(mCurrentPhotoId);
                                                mDeletePhotoToZeroListener.deletePhotoToZero();
                                            }
                                        });
                            } else {
                                mBeautyDetailPresenter.deletePhoto(mPhotoBeanList.get(mCurrentPhotoId).getId(),
                                        new BeautyDetailPresenter.OnDeletePhotoFinish() {
                                            @Override
                                            public void onFinish() {
                                                mPhotoBeanList.remove(mCurrentPhotoId);
                                                mDetailPhotoVp.getAdapter().notifyDataSetChanged();
                                                mTitleCenterTextListener.setTitleCenter(mCurrentPhotoId + 1 + "/"
                                                        + mPhotoBeanList.size());
                                            }
                                        });
                            }

                        }
                    }
                });
        alDialog.setNegativeButton(getResources().getString(R.string.alertdialog_delete_negative_button_text),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_detail_photo_delete:
                deletePhotoFromAlbum();
                break;
            case R.id.rl_beauty_detail_into_superbeauty:
                Intent intent = new Intent(getActivity(), SuperBeautyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("pendingPhoto", mPhotoBeanList.get(mCurrentPhotoId));
                intent.putExtras(bundle);
                startActivity(intent);
                break;

        }

    }

    /**
     * 照片详情界面滑动监听
     */
    private class PhotoDetailChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mPhotoBeanList != null) {
                mCurrentPhotoId = position;
                position++;
                String str1 = String.valueOf(mCurrentPhotoId + 1);
                String str2 = String.valueOf(mPhotoBeanList.size());
                String str = String.format("%s/%s", str1, str2);
                mTitleCenterTextListener.setTitleCenter(str);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

}
