package com.meitu.test001.app.beauty.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.meitu.test001.R;
import com.meitu.test001.app.beauty.Bean.PhotoBean;
import com.meitu.test001.app.beauty.fragment.AlbumSelectFragment;
import com.meitu.test001.app.beauty.fragment.BeautyDetailFragment;
import com.meitu.test001.app.beauty.fragment.BeautyHomeFragment;
import com.meitu.test001.app.beauty.presenter.BeautyHomePresenter;
import com.meitu.test001.app.beauty.presenter.BeautyHomePresenterImpl;
import com.meitu.test001.app.beauty.view.HomeView;
import com.meitu.test001.common.Utils.BaseActivity;

import java.util.Deque;
import java.util.List;

/**
 * Created by meitu on 2017/7/11.
 */
public class BeautyActivity extends BaseActivity implements View.OnClickListener, HomeView,
        AlbumSelectFragment.AlbumSelectFinished, BeautyHomeFragment.PhotoSelectFinished,
        BeautyDetailFragment.TitleCenterTextListener, BeautyHomeFragment.TitleCenterTextListener,
        BeautyDetailFragment.DeletePhotoToZeroListener {
    private static final String TAG = "BeautyActivity";
    public Uri mPhotoUriOfStorage;
    private String mDefaultAlbumName;
    private TextView mTitleLeftTv;
    private TextView mTitleCenterTv;
    private ImageView mTitleRightIv;
    private ImageView mTitleLeftIv;
    public BeautyHomeFragment mBeautyHomeFragment;
    public AlbumSelectFragment mAlbumSelectFragment;
    public BeautyDetailFragment mBeautyDetailFragment;
    private BeautyHomePresenter mBeautyHomePresenter;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    public Fragment mCurrentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beauty_activity);
        mTitleLeftTv = (TextView) findViewById(R.id.tv_titlebar_left);
        mTitleCenterTv = (TextView) findViewById(R.id.tv_titlebar_center);
        mTitleRightIv = (ImageView) findViewById(R.id.iv_titlebar_right);
        mTitleLeftIv = (ImageView) findViewById(R.id.iv_titlebar_left);
        mDefaultAlbumName = getResources().getString(R.string.beauty_default_album_name);
        mBeautyHomePresenter = new BeautyHomePresenterImpl(this);
        setDefaultFragment();
        mTitleLeftTv.setOnClickListener(this);
        mTitleRightIv.setOnClickListener(this);
        mTitleLeftIv.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    private void setDefaultFragment() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        mBeautyHomeFragment = new BeautyHomeFragment();
        mAlbumSelectFragment = new AlbumSelectFragment();
        mBeautyDetailFragment = new BeautyDetailFragment();
        fragmentTransaction.add(R.id.beauty_fragment, mAlbumSelectFragment);
        fragmentTransaction.add(R.id.beauty_fragment, mBeautyDetailFragment);
        fragmentTransaction.add(R.id.beauty_fragment, mBeautyHomeFragment);

        fragmentTransaction.hide(mAlbumSelectFragment);
        fragmentTransaction.hide(mBeautyDetailFragment);
        fragmentTransaction.commit();
        mCurrentFragment = mBeautyHomeFragment;

    }

    @Override
    public void showDefaultPhotos(List<PhotoBean> mDefaultPhotos) {

    }

    @Override
    public void openAlbum() {

    }

    public void openCamera(Uri uriOfStorage) {
        mPhotoUriOfStorage = uriOfStorage;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uriOfStorage);
        startActivityForResult(intent, 2);
    }

    @Override
    public void onClick(View v) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (v.getId()) {
            case R.id.tv_titlebar_left: {
                // 首页显示跳转到相册列表页
                if (mCurrentFragment == mBeautyHomeFragment) {
                    Log.d(TAG, "onClick: +currentFragment--mBeautyHomeFragment");
                    if (!mAlbumSelectFragment.isAdded()) {
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.hide(mCurrentFragment);
                        fragmentTransaction.add(R.id.beauty_fragment, mAlbumSelectFragment);
                    } else {
                        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        fragmentTransaction.hide(mCurrentFragment);
                        fragmentTransaction.show(mAlbumSelectFragment);
                        // 刷新
                        mAlbumSelectFragment.mBeautyAlbumSelectPresenter.selectAlbum(this);
                        mAlbumSelectFragment.setTransitiveData(new AlbumSelectFragment.TitleTextCallBack() {
                            @Override
                            public void setTitleCenterText(String text) {
                                mTitleCenterTv.setText(text);
                            }

                            @Override
                            public void setTitleLeftText(String text) {
                                mTitleLeftTv.setText(text);
                            }
                        });
                        mCurrentFragment = mAlbumSelectFragment;
                    }
                    fragmentTransaction.commit();
                    break;
                }
                // 相册列表页
                if (mCurrentFragment == mAlbumSelectFragment) {
                    finish();
                    break;
                }
                // 照片详情页跳转到首页显示
                if (mCurrentFragment == mBeautyDetailFragment) {
                    getFragmentManager().popBackStack();
                    // 刷新
                    String albumName = mBeautyHomeFragment.getDefaultAlbumName();
                    mBeautyHomeFragment.mBeautyHomePresenter.showHomeDefaultPhotos(albumName);
                    mCurrentFragment = mBeautyHomeFragment;
                    break;
                }
            }
            case R.id.iv_titlebar_right: {
                String pathOfStorage = this.getResources().getString(R.string.beauty_shot_storage_path);
                mBeautyHomePresenter.openCameraAndStorePhoto(pathOfStorage);
                break;
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && mBeautyHomePresenter != null) {
                    mBeautyHomePresenter.showHomeDefaultPhotos(mDefaultAlbumName);
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (mPhotoUriOfStorage != null) {
                        // 对新增相片进行扫描，加载到媒体库，加载完成后调用回调函数
                        MediaScannerConnection.scanFile(this, new String[]{mPhotoUriOfStorage.getPath()}, null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        if (mCurrentFragment != mBeautyHomeFragment) {
                                            fragmentManager = getFragmentManager();
                                            fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                                            fragmentTransaction.hide(mCurrentFragment);
                                            fragmentTransaction.show(mBeautyHomeFragment);
                                            fragmentTransaction.commitAllowingStateLoss();
                                        }
                                        String albumName = mBeautyHomeFragment.getDefaultAlbumName();
                                        mBeautyHomeFragment.mBeautyHomePresenter.showHomeDefaultPhotos(albumName);
                                        mCurrentFragment = mBeautyHomeFragment;
                                    }
                                });
                    }
                }
                break;
        }
    }

    @Override
    public void selectAlbumFinished(String albumName) {
        if (mAlbumSelectFragment.isAdded()) {
            fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.hide(mAlbumSelectFragment);
            mBeautyHomeFragment.mBeautyHomePresenter.showHomeDefaultPhotos(albumName);
            fragmentTransaction.show(mBeautyHomeFragment);
            mCurrentFragment = mBeautyHomeFragment;
            mBeautyHomeFragment.setDefaultAlbumName(albumName);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void selectPhotoFinished(Bundle bundle) {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.hide(mBeautyHomeFragment);
        mBeautyDetailFragment.getTransitiveData(bundle);
        fragmentTransaction.show(mBeautyDetailFragment);
        mCurrentFragment = mBeautyDetailFragment;
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void setTitleCenter(String text) {
        mTitleCenterTv.setText(text);
    }

    @Override
    public void onBackPressed() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        if (mCurrentFragment == mAlbumSelectFragment)
            finish();
        if (mCurrentFragment == mBeautyHomeFragment) {
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.hide(mCurrentFragment);
            fragmentTransaction.show(mAlbumSelectFragment);
            // 刷新操作
            mAlbumSelectFragment.mBeautyAlbumSelectPresenter.selectAlbum(this);
            mAlbumSelectFragment.setTransitiveData(new AlbumSelectFragment.TitleTextCallBack() {
                @Override
                public void setTitleCenterText(String text) {
                    mTitleCenterTv.setText(text);
                }

                @Override
                public void setTitleLeftText(String text) {
                    mTitleLeftTv.setText(text);
                }
            });
            fragmentTransaction.commitAllowingStateLoss();
            mCurrentFragment = mAlbumSelectFragment;
        }
        if (mCurrentFragment == mBeautyDetailFragment) {
            getFragmentManager().popBackStack();
            // 刷新操作
            String albumName = mBeautyHomeFragment.getDefaultAlbumName();
            mBeautyHomeFragment.mBeautyHomePresenter.showHomeDefaultPhotos(albumName);
            mCurrentFragment = mBeautyHomeFragment;
        }
    }

    @Override
    public void deletePhotoToZero() {
        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.hide(mCurrentFragment);
        fragmentTransaction.show(mAlbumSelectFragment);
        // 刷新操作
        mAlbumSelectFragment.mBeautyAlbumSelectPresenter.selectAlbum(this);
        mCurrentFragment = mAlbumSelectFragment;
        mAlbumSelectFragment.setTransitiveData(new AlbumSelectFragment.TitleTextCallBack() {
            @Override
            public void setTitleCenterText(String text) {
                mTitleCenterTv.setText(text);
            }

            @Override
            public void setTitleLeftText(String text) {
                mTitleLeftTv.setText(text);
            }
        });
        fragmentTransaction.commitAllowingStateLoss();
    }
}
