package com.meitu.test001.common.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by meitu on 2017/7/28.
 */
public class ImageCompressUtil {

    private static final String TAG = "ImageCompressUtil";
    public static LoadedInterface mLoadedInterface;

    /**
     * 从图片路径获得Bitmap（默认不压缩）
     *
     * @param imgPath
     * @return
     */
    public static Bitmap getBitmap(String imgPath) {
        // Get bitmap through image path
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        // Do not compress
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        return BitmapFactory.decodeFile(imgPath, newOpts);
    }

    /**
     * 存储Bitmap到路径
     *
     * @param bitmap
     * @param outPath
     * @throws FileNotFoundException
     */
    public static void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outPath);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
    }

    /**
     * Compress image by pixel, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param pixelW  target pixel of width
     * @param pixelH  target pixel of height
     * @return
     */
    public static Bitmap compressAccordingToPixel(String imgPath, float pixelW, float pixelH) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap;
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 想要缩放的目标尺寸
        float setH = pixelH;
        float setW = pixelW;
        // 由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > setW) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / setW);
        } else if (w < h && h > setH) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / setH);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        // 开始压缩图片，此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    /**
     * 根据设定size进行图片压缩(百度方案)
     *
     * @param image
     * @param pixelW target pixel of width
     * @param pixelH target pixel of height
     * @return
     */
    public static Bitmap compressAccordingToPixel(Bitmap image, float pixelW, float pixelH) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, os);
        if (os.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            os.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, os);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap;
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float setH = pixelH;// 设置高度为240f时，可以明显看到图片缩小了
        float setW = pixelW;// 设置宽度为120f，可以明显看到图片缩小了
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > setW) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / setW);
        } else if (w < h && h > setH) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / setH);
        }
        if (be <= 0) be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        is = new ByteArrayInputStream(os.toByteArray());
        bitmap = BitmapFactory.decodeStream(is, null, newOpts);
        //压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    /**
     * Compress image by retio, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @param ratio   ratio of image
     * @return
     */
    public static Bitmap compressAccordingToRatio(String imgPath, int ratio) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inJustDecodeBounds = false;
        //设置缩放比例
        newOpts.inSampleSize = ratio + 1;
        // Get bitmap info, but notice that bitmap is null now
        Bitmap bitmap;
        // 开始压缩图片，此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        // 压缩好比例大小后再进行质量压缩
        return bitmap;
    }

    /**
     * 根据设定大小加载图片
     *
     * @param imgPath
     * @param pixelW
     * @param pixelH
     * @return
     */

    public static Bitmap compressAccordingToSize(final String imgPath, final float pixelW, final float pixelH) {
        //不加载图片获取图片的原始大小 inJustDecodeBounds设置为true
        BitmapFactory.Options oPts = new BitmapFactory.Options();
        oPts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, oPts);
        final float preBitmapWidth = oPts.outWidth;
        final float preBitmapHeight = oPts.outHeight;
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        newOpts.inJustDecodeBounds = false;
        //设置缩放比例（固定比例缩放）
        int sampleSize = 1;//1表示不缩放
        //如果宽度大的话根据宽度固定大小缩放
        if (preBitmapWidth > preBitmapHeight && preBitmapWidth > pixelW) {
            sampleSize = (int) (preBitmapWidth / pixelW);
        }
        //如果高度大的话根据高度固定大小缩放
        else if (preBitmapWidth < preBitmapHeight && preBitmapHeight > pixelH) {
            sampleSize = (int) (preBitmapHeight / pixelH);
        }
        if (sampleSize < 0) sampleSize = 1;
        newOpts.inSampleSize = sampleSize;
        newOpts.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
        return bitmap;
    }

    /**
     * Compress image by retio, this will modify image width/height.
     * Used to get thumbnail
     *
     * @param imgPath image path
     * @return
     */
    public static void compressAccordingToAdapting(final String imgPath, final View parentView, final LoadedInterface loadedInterface) {
        //不加载图片获取图片的原始大小
        BitmapFactory.Options oPts = new BitmapFactory.Options();
        oPts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, oPts);
        final float preBitmapWidth = oPts.outWidth;
        final float preBitmapHeight = oPts.outHeight;
        mLoadedInterface = loadedInterface;
        parentView.post(new Runnable() {
            @Override
            public void run() {
                float viewWidth = parentView.getWidth();
                float viewHeight = parentView.getHeight();
                BitmapFactory.Options newOpts = new BitmapFactory.Options();
                newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
                newOpts.inJustDecodeBounds = false;
                //设置缩放比例
                if (preBitmapWidth < viewWidth && preBitmapHeight < viewHeight || preBitmapWidth > viewWidth && preBitmapHeight > viewHeight)
                    newOpts.inSampleSize = Math.max(Math.round(preBitmapWidth / viewWidth), Math.round(preBitmapHeight / viewHeight));
                if (preBitmapWidth > viewWidth && preBitmapHeight < viewHeight)
                    newOpts.inSampleSize = (int) (preBitmapWidth / viewWidth);
                if (preBitmapWidth < viewWidth && preBitmapHeight > viewHeight)
                    newOpts.inSampleSize = (int) (preBitmapHeight / viewHeight);
                Log.d(TAG, "compressAccordingToAdapting: inSampleSize: "
                        + newOpts.inSampleSize + " preBitmapWidth: " + preBitmapWidth + " screenWidth: " + viewWidth
                        + " preBitmapHeight: " + preBitmapHeight + " screenHeight: " + viewHeight);
                // 开始压缩图片，此时已经把options.inJustDecodeBounds 设回false了
                Bitmap bitmap = BitmapFactory.decodeFile(imgPath, newOpts);
                float bitmapWidth = bitmap.getWidth();
                float bitmapHeight = bitmap.getHeight();
                // 压缩好比例大小后再进行矩阵变换
                Matrix matrix = new Matrix();
                if (bitmapWidth > viewWidth && bitmapHeight > viewHeight || bitmapWidth < viewWidth && bitmapHeight < viewHeight)
                    matrix.setScale(Math.min(viewWidth / bitmapWidth, viewHeight / bitmapHeight), Math.min(viewWidth / bitmapWidth, viewHeight / bitmapHeight), viewWidth / 2, viewHeight / 2);
                if (bitmapWidth > viewWidth && bitmapHeight < viewHeight)
                    matrix.setScale(viewWidth / bitmapWidth, viewHeight / bitmapHeight);
                if (bitmapWidth < viewWidth && bitmapHeight > viewHeight)
                    matrix.setScale(viewWidth / bitmapHeight, viewHeight / bitmapHeight);
                Bitmap newBitmap = bitmap.createBitmap(bitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight, matrix, true);
                mLoadedInterface.loadedFinish(newBitmap);
            }
        });

    }

    public interface LoadedInterface {
        void loadedFinish(Bitmap bitmap);
    }

}
