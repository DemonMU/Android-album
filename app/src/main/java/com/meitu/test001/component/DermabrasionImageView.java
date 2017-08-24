package com.meitu.test001.component;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.meitu.test001.R;
import com.meitu.test001.common.Utils.ImageCompressUtil;

/**
 * Created by meitu on 2017/7/28.
 */
public class DermabrasionImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener {
    public static final int MAGNIFIER_LEFT_MODE = 1;
    public static final int MAGNIFIER_RIGHT_MODE = 2;
    public static final int ANIMATION_RUN_MODE = 3;
    public static final int ANIMATION_STOP_MODE = 4;
    public static float ANIMATION_DURATION_TRANSLATE_MAX = 800;

    private final Xfermode mDstOut = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    /**
     * 用于存放矩阵的9个值
     */
    private final float[] matrixValues = new float[9];

    private ImageView mImageView;
    private int mWidthOfPhoto;
    private int mHeightOfPhoto;

    /**
     * 缩放的最大，最小比例
     */
    public float mScaleMax = 2.5f;
    public float mScaleMin = 0.8f;

    private int mMagnifierMode = 0;
    private float mTouchSlop = 50;
    private float mAnimationDuration;
    private float initScale;
    private float endScaleFocusX;
    private float endScaleFocusY;
    private float deltaXBoundsWhenMove;
    private float deltaYBoundsWhenMove;
    private float scaleOfScaleEnd;
    private float scaleFactorOfScaleEnd;
    private int mMode;
    private float mStayX;
    private float mStayY;
    // 放大镜的宽度
    private static int mMagnifierSide = 400;

    // 放大倍数
    private static final int FACTOR = 2;

    /**
     * 缩放的手势检测
     */
    private ScaleGestureDetector mScaleGestureDetector = null;
    private boolean isCanDrag;
    private boolean isCheckTopAndBottom;
    private boolean isCheckLeftAndRight;
    private boolean isCheckScaleSelf = false;
    private boolean isCheckTranslateSelf = false;
    private boolean isCheckCenterSelf = false;
    private boolean isCanDrawPath = false;
    private boolean FlagPointerUp = false;

    private Paint mPaint;
    private Path mPath;
    private Canvas mTempCanvas;

    private int mDrawPaintSize = 60;
    private int mDrawPaintColor = Color.RED;
    private float mPointerLastX = 0;
    private float mPointerLastY = 0;
    private float mSingleLastX = 0;
    private float mSingleLastY = 0;
    private float x = 0, y = 0;

    private int imageViewWidth;
    private int imageViewHeight;

    private Bitmap mBitmap;
    private Bitmap mTempBitmap;
    private Matrix matrix = new Matrix();
    private Context mContext;
    public static final String TAG = "DermabrasionImageView";

    public DermabrasionImageView(Context context) {
        this(context, null);
    }

    public DermabrasionImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DermabrasionImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (isInEditMode()) {
            return;
        }
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.DermabrasionImageView);
        mDrawPaintColor = types.getColor(R.styleable.DermabrasionImageView_paintColor, Color.RED);
        mDrawPaintSize = types.getInteger(R.styleable.DermabrasionImageView_paintSize, 60);
        mMagnifierSide = types.getInteger(R.styleable.DermabrasionImageView_magnifierSide, 400);
        types.recycle();

        mContext = context;
        mImageView = this;
        init();
    }

    private void init() {
        //初始化缩放手势监听和移动手势监听
        mScaleGestureDetector = new ScaleGestureDetector(mContext, this);
        onTouchFunction();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true); // 消除锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setFilterBitmap(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);//定义画笔转弯处的连接形状
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 定义画笔结束处的形状
        mPaint.setStrokeWidth(mDrawPaintSize);// 设置圆环的宽度
        mPaint.setColor(mDrawPaintColor);
        mPath = new Path();
    }

    public void displayPhoto(String path) {
        //加载图片工具
        mBitmap = ImageCompressUtil.compressAccordingToSize(path, 1080, 1920);
        mWidthOfPhoto = mBitmap.getWidth();
        mHeightOfPhoto = mBitmap.getHeight();
        mTempBitmap = Bitmap.createBitmap(mWidthOfPhoto, mHeightOfPhoto, Bitmap.Config.ARGB_8888);
        mTempCanvas = new Canvas(mTempBitmap);
        mTempCanvas.drawBitmap(mTempBitmap, 0, 0, null);
        initImage(mBitmap);

        Log.d(TAG, "displayPhotoCompress: width--" + mWidthOfPhoto + " height--" + mHeightOfPhoto);
    }

    private void initImage(Bitmap bitmap) {
        this.setImageBitmap(bitmap);
    }


    /**
     * 触摸事件处理
     */
    private void onTouchFunction() {
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //将x，y置0
                x = 0;
                y = 0;
                // 拿到触摸点的个数
                final int pointerCount = event.getPointerCount();
                // 得到多个触摸点的x与y均值
                if (pointerCount > 2 || mMode == ANIMATION_RUN_MODE) {
                    return true;
                }
                mScaleGestureDetector.onTouchEvent(event);
                Log.d(TAG, "HaHaEventTouch: " + mMode);
                for (int i = 0; i < pointerCount; i++) {
                    x += event.getX(i);
                    y += event.getY(i);
                }
                x = x / pointerCount;
                y = y / pointerCount;
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        mSingleLastX = x;
                        mSingleLastY = y;
                        isCanDrawPath = false;
                        Log.d(TAG, "HaHaEventACTION_DOWN: " + mMode);
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mPointerLastX = x;
                        mPointerLastY = y;
                        Log.d(TAG, "HaHaEventPOINTER_DOWN: " + mMode);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int pointerCountMove = event.getPointerCount();
                        if (pointerCountMove == 1) {
                            if (x > getMatrixRectF(matrix).left && x < getMatrixRectF(matrix).right
                                    && y > getMatrixRectF(matrix).top && y < getMatrixRectF(matrix).bottom) {
                                isCanDrawPath = true;
                                if (getMatrixRectF(matrix).top < 400) {
                                    if (x < 400 && y < 400) {
                                        mMagnifierMode = MAGNIFIER_RIGHT_MODE;
                                    } else {
                                        mMagnifierMode = MAGNIFIER_LEFT_MODE;
                                    }
                                } else {
                                    mMagnifierMode = MAGNIFIER_LEFT_MODE;
                                }
                                mPath.moveTo(mSingleLastX, mSingleLastY);
                                mPath.lineTo(x, y);
                                if (isCanDrawPath)
                                    mTempCanvas.drawPath(mPath, mPaint);
                                invalidate();
                            }
                            mSingleLastX = x;
                            mSingleLastY = y;
                        }
                        //双指移动
                        if (pointerCountMove == 2) {
                            float dx = x - mPointerLastX;
                            float dy = y - mPointerLastY;

                            RectF rectF = getMatrixRectF(matrix);
                            if (getDrawable() != null) {
                                Log.d(TAG, "HaHaEventACTION_MOVE " + mMode);
                                isCheckLeftAndRight = isCheckTopAndBottom = true;
                                // 如果宽度小于屏幕宽度，则禁止左右移动
                                if (rectF.width() <= getWidth()) {
                                    dx = 0;
                                    isCheckLeftAndRight = false;
                                }
                                // 如果高度小于屏幕高度，则禁止上下移动
                                if (rectF.height() <= getHeight()) {
                                    dy = 0;
                                    isCheckTopAndBottom = false;
                                }
                                matrix.postTranslate(dx, dy);
                                setImageMatrix(matrix);

                            }
                            mPointerLastX = x;
                            mPointerLastY = y;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        FlagPointerUp = true;
                        Log.d(TAG, "HaHaEventACTION_POINTER_UP: " + mMode + " " + FlagPointerUp);
                        Log.d(TAG, "onTouchPOINTER_UP: Top" + isCheckTopAndBottom + " Left:" + isCheckLeftAndRight);
                        float[] array = checkTranslateDeltaXY(matrix);
                        //当image长宽大于view长宽时才>0
                        deltaXBoundsWhenMove = array[0];
                        deltaYBoundsWhenMove = array[1];
                        isCheckTranslateSelf = false;
                        isCheckCenterSelf = false;
                        if (Math.abs(deltaXBoundsWhenMove) > 0 || Math.abs(deltaYBoundsWhenMove) > 0) {
                            isCheckTranslateSelf = true;
                        }
                        if (Math.abs(deltaXBoundsWhenMove) == 0 || Math.abs(deltaYBoundsWhenMove) == 0) {
                            isCheckCenterSelf = checkCenterSelfBoolean(matrix);
                        }
                        Log.d(TAG, "IsJudgeOnTouch: isCheckScaleSelf: " + isCheckScaleSelf);
                        Log.d(TAG, "IsJudgeOnTouch: isCheckCenterSelf:" + isCheckCenterSelf);
                        Log.d(TAG, "IsJudgeOnTouch: isCheckTranslateSelf:" + isCheckTranslateSelf);
                        setAnimatorOfScaleAndMove();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        Log.d(TAG, "HaHaEventACTION_CANCEL: ");
                    case MotionEvent.ACTION_UP:
                        Log.d(TAG, "HaHaEventACTION_UP: " + mMode + " " + FlagPointerUp);
                        if (!FlagPointerUp)
                            setAnimatorOfScaleAndMove();
                        FlagPointerUp = false;
                        break;
                }
                return true;
            }
        });
    }

    /**
     * 判断此时是否要居中回弹
     *
     * @param matrix
     * @return
     */
    private boolean checkCenterSelfBoolean(Matrix matrix) {
        RectF rect = getMatrixRectF(matrix);
        imageViewWidth = getWidth();
        imageViewHeight = getHeight();
        // 如果宽或高大于屏幕，则返回false
        if (rect.width() > imageViewWidth && rect.height() > imageViewHeight) {
            return false;
        }
        if ((rect.top + rect.height() / 2) == imageViewHeight / 2 || (rect.left + rect.width() / 2) == imageViewWidth) {
            return false;
        } else {
            return true;
        }
    }



    /**
     * 是否是拖动行为
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw:");
        if (mTempBitmap != null)
            canvas.drawBitmap(mTempBitmap, 0, 0, null);
        canvas.save();
        if (mMagnifierMode == MAGNIFIER_LEFT_MODE) {
            //剪切显示区域
            canvas.clipRect(0, 0, mMagnifierSide, mMagnifierSide);
            //画剪切区域应该显示的内容(Bitmap移动)
            //保证放大区域不超过边界
            if (x > getMatrixRectF(matrix).left + mMagnifierSide / 2 && x < getMatrixRectF(matrix).right - mMagnifierSide / 2
                    && y > getMatrixRectF(matrix).top + mMagnifierSide / 2 && y < getMatrixRectF(matrix).bottom - mMagnifierSide / 2) {
                canvas.translate(mMagnifierSide / 2 - x, mMagnifierSide / 2 - y);
                mStayX = x;
                mStayY = y;
            } else {
                canvas.translate(mMagnifierSide / 2 - mStayX, mMagnifierSide / 2 - mStayY);
            }
        }

        if (mMagnifierMode == MAGNIFIER_RIGHT_MODE) {
            //剪切显示区域
            canvas.clipRect(getWidth() - mMagnifierSide, 0, getWidth(), mMagnifierSide);
            if (x > getMatrixRectF(matrix).left + mMagnifierSide / 2 && x < getMatrixRectF(matrix).right - mMagnifierSide / 2
                    && y > getMatrixRectF(matrix).top + mMagnifierSide / 2 && y < getMatrixRectF(matrix).bottom - mMagnifierSide / 2) {
                canvas.translate(getWidth() - mMagnifierSide / 2 - x, mMagnifierSide / 2 - y);
                mStayX = x;
                mStayY = y;
            } else {
                canvas.translate(getWidth() - mMagnifierSide / 2 - mStayX, mMagnifierSide / 2 - mStayY);
            }
        }
        canvas.drawBitmap(mBitmap, matrix, null);
        //绘制
        if (isCanDrawPath)
            canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        Log.d(TAG, "HaHaEventonScale: ");
        float scale = getScale(matrix);
        float scaleFactor = detector.getScaleFactor(); //缩放因子
        float focusX = detector.getFocusX();
        float focusY = detector.getFocusY();
        if (scale <= 3.0f * initScale && scale >= 0.5 * initScale && Math.abs(scaleFactor - 1) > 0.001) {
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
        }
        setImageMatrix(matrix);
        endScaleFocusX = focusX;
        endScaleFocusY = focusY;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isCheckScaleSelf = false;
        //算出相对于initScale的scale值
        scaleOfScaleEnd = getScale(matrix) / initScale;
        scaleFactorOfScaleEnd = detector.getScaleFactor(); //缩放因子
        /**
         * 最大值最小值判断
         */
        if (IsImageUnderScaleMin(matrix)) {
            scaleFactorOfScaleEnd = mScaleMin / scaleOfScaleEnd;
            isCheckScaleSelf = true;
        }
        if (IsImageOverScaleMax(matrix)) {
            scaleFactorOfScaleEnd = mScaleMax / scaleOfScaleEnd;
            isCheckScaleSelf = true;
        }
        Log.d(TAG, "HaHaEventScaleEnd: scaleFactorOfScaleEnd " + scaleFactorOfScaleEnd);

    }

    /**
     * 判断Image是否大于最大值
     *
     * @param matrix
     * @return
     */
    private boolean IsImageOverScaleMax(Matrix matrix) {
        float scale = getScale(matrix);
        if (scale > initScale * mScaleMax)
            return true;
        else
            return false;
    }

    /**
     * 判断Image是否小于最小比例
     *
     * @param matrix
     * @return
     */
    private boolean IsImageUnderScaleMin(Matrix matrix) {
        float scale = getScale(matrix);
        if (scale < initScale * mScaleMin)
            return true;
        else
            return false;
    }

    /**
     * 设置缩放和移动动画
     */
    private void setAnimatorOfScaleAndMove() {
        final float[] arrayCenter = checkCenterDeltaXY(matrix);
        final float[] arrayTranslate = checkTranslateDeltaXY(matrix);
        if (!isCheckScaleSelf && !isCheckTranslateSelf && !isCheckCenterSelf) {
            return;
        }
        if (isCheckScaleSelf) {
            //设置动画时间
            setAnimationDuration(Math.abs(scaleOfScaleEnd - 1), Math.max(Math.abs(arrayCenter[0]), Math.abs(arrayCenter[1])));
            if (isCheckTranslateSelf) {
                //设置动画时间
                setAnimationDuration(Math.abs(scaleOfScaleEnd - 1), Math.max(Math.abs(arrayCenter[0]), Math.abs(arrayCenter[1])));
            }
        }
        if (!isCheckScaleSelf && !isCheckTranslateSelf && isCheckCenterSelf) {
            //设置动画时间
            setAnimationDuration(0, Math.max(Math.abs(arrayCenter[0]), Math.abs(arrayCenter[1])));
        }
        if (!isCheckScaleSelf && isCheckTranslateSelf) {
            //设置动画时间
            setAnimationDuration(0, Math.max(Math.abs(arrayTranslate[0]), Math.abs(arrayTranslate[1])));
        }

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            FloatEvaluator mFloatEvaluator = new FloatEvaluator();
            private float lastValue = 1;
            private float lastFraction = 0;


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获得当前进度占整个动画的比例
                float currentFraction = animation.getAnimatedFraction();
                if (isCheckScaleSelf) {
                    Log.d(TAG, "onAnimationUpdate: Mode : 1");
                    //根据当前进度计算当前值
                    float currentValue = (scaleFactorOfScaleEnd - 1) * currentFraction + 1;
                    float lastLeft = getMatrixRectF(matrix).left;
                    float lastRight = getMatrixRectF(matrix).right;
                    float lastTop = getMatrixRectF(matrix).top;
                    float lastBottom = getMatrixRectF(matrix).bottom;
                    float lastWidth = getMatrixRectF(matrix).width();
                    float lastHeight = getMatrixRectF(matrix).height();
                    //图片放大时
                    if (currentValue >= 1 && scaleFactorOfScaleEnd > 1) {
                        //计算每次动画缩放的大小
                        matrix.postScale(currentValue / lastValue, currentValue / lastValue, imageViewWidth / 2, imageViewHeight / 2);
                        //计算动画缩放后的距离
                        float currentScaleLeft = getMatrixRectF(matrix).left;
                        float currentScaleTop = getMatrixRectF(matrix).top;
                        float currentScaleWidth = getMatrixRectF(matrix).width();
                        float currentScaleHeight = getMatrixRectF(matrix).height();
                        //取动画中间状态计算偏移量
                        matrix.postTranslate(mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayCenter[0]) - (currentScaleLeft - lastRight + lastWidth / 2 + currentScaleWidth / 2)
                                , mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayCenter[1]) - (currentScaleTop - lastBottom + lastHeight / 2 + currentScaleHeight / 2));
                    }
                    //图片缩小时
                    if (currentValue <= 1 && scaleFactorOfScaleEnd < 1) {
                        //缩放还原
                        matrix.postScale(currentValue / lastValue, currentValue / lastValue, endScaleFocusX, endScaleFocusY);
                        //取动画中间状态计算偏移量
                        float currentScaleLeft = getMatrixRectF(matrix).left;
                        float currentScaleRight = getMatrixRectF(matrix).right;
                        float currentScaleTop = getMatrixRectF(matrix).top;
                        float currentScaleBottom = getMatrixRectF(matrix).bottom;
                        //下铺满，上左上右回弹
                        if (currentScaleTop > 0) {
                            if (currentScaleLeft > 0)
                                matrix.postTranslate(lastLeft - currentScaleLeft, lastTop - currentScaleTop);
                            if (currentScaleRight < imageViewWidth)
                                matrix.postTranslate(lastRight - currentScaleRight, lastTop - currentScaleTop);
                        }
                        //上铺满，下左下右回弹
                        if (currentScaleBottom < imageViewHeight) {
                            if (currentScaleLeft > 0)
                                matrix.postTranslate(lastLeft - currentScaleLeft, lastBottom - currentScaleBottom);
                            if (currentScaleRight < imageViewWidth)
                                matrix.postTranslate(lastRight - currentScaleRight, lastBottom - currentScaleBottom);
                        }
                        //上下铺满，左右回弹
                        if (currentScaleTop < 0 && currentScaleBottom > imageViewHeight) {
                            if (currentScaleLeft > 0)
                                matrix.postTranslate(lastLeft - currentScaleLeft, 0);
                            if (currentScaleRight < imageViewWidth)
                                matrix.postTranslate(lastRight - currentScaleRight, 0);
                        }
                        //左右铺满，上下回弹
                        if (currentScaleLeft < 0 && currentScaleRight > imageViewWidth) {
                            if (currentScaleTop > 0)
                                matrix.postTranslate(0, lastTop - currentScaleTop);
                            if (currentScaleBottom < imageViewHeight)
                                matrix.postTranslate(0, lastBottom - currentScaleBottom);
                        }
                        //平移还原
                        if (isCheckTranslateSelf) {
                            matrix.postTranslate(mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayTranslate[0]), mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayTranslate[1]));
                            //缩放还原,根据边界判断缩放点
                        }
                    }
                    lastValue = currentValue;
                }
                if (!isCheckScaleSelf && !isCheckTranslateSelf && isCheckCenterSelf) {
                    Log.d(TAG, "onAnimationUpdate: Mode : 2");
                    matrix.postTranslate(mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayCenter[0]), mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayCenter[1]));
                }
                if (!isCheckScaleSelf && isCheckTranslateSelf && !isCheckCenterSelf) {
                    Log.d(TAG, "onAnimationUpdate: Mode : 3");
                    matrix.postTranslate(mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayTranslate[0]), mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayTranslate[1]));
                }
                if (!isCheckScaleSelf && isCheckTranslateSelf && isCheckCenterSelf) {
                    Log.d(TAG, "onAnimationUpdate: Mode : 4");
                    matrix.postTranslate(mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayCenter[0]), mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayCenter[1]));
                    matrix.postTranslate(mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayTranslate[0]), mFloatEvaluator.evaluate(currentFraction - lastFraction, 0f, arrayTranslate[1]));
                }
                lastFraction = currentFraction;
                mImageView.setImageMatrix(matrix);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mMode = ANIMATION_RUN_MODE;
                Log.d(TAG, "HaHaEventOnAnimationStart: ");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isCheckScaleSelf = false;
                mMode = ANIMATION_STOP_MODE;
                Log.d(TAG, "HaHaEventonAnimationEnd: ");
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Log.d(TAG, "HaHaEventOnAnimationCancel: ");
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(TAG, "HaHaEventOnAnimationRepeat: ");

            }
        });
        Log.d(TAG, "DurationSetAnimatorOfScaleAndMove: " + mAnimationDuration);
        valueAnimator.setDuration((long) mAnimationDuration);
        valueAnimator.start();
    }

    /**
     * 设置动画时间
     *
     * @param scale     缩放变化值。
     * @param translate 平移变化值。
     */

    private void setAnimationDuration(float scale, float translate) {
        if (translate != 0) {
            mAnimationDuration = translate / ANIMATION_DURATION_TRANSLATE_MAX * 500;
        } else {
            mAnimationDuration = scale * 10 / (mScaleMax - 1) * 300;
            if (mAnimationDuration > 300)
                mAnimationDuration = 300;
        }

    }

    public final float getScale(Matrix matrix) {
        matrix.getValues(matrixValues);
        return matrixValues[Matrix.MSCALE_X];
    }

    /**
     * 根据当前图片的Matrix获得图片的范围
     *
     * @return
     */
    private RectF getMatrixRectF(Matrix matrix) {
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }


    /**
     * 在回弹居中时，进行图片归中的控制
     */
    private float[] checkCenterDeltaXY(Matrix matrix) {
        RectF rect = getMatrixRectF(matrix);
        float deltaX = 0;
        float deltaY = 0;

        imageViewWidth = getWidth();
        imageViewHeight = getHeight();

        // 如果宽或高大于屏幕，则不动
        if (rect.width() > imageViewWidth) {
            deltaX = 0;
        }
        if (rect.height() > imageViewHeight) {
            deltaY = 0;
        }

        // 如果宽或高小于屏幕，则让其居中
        if (rect.width() <= imageViewWidth) {
            deltaX = imageViewWidth * 0.5f - rect.right + 0.5f * rect.width();
        }
        if (rect.height() <= imageViewHeight) {
            deltaY = imageViewHeight * 0.5f - rect.bottom + 0.5f * rect.height();
        }

        float[] array = new float[2];
        array[0] = deltaX;
        array[1] = deltaY;
        return array;
    }

    /**
     * 在平移时，进行边界控制
     */
    private float[] checkTranslateDeltaXY(Matrix matrix) {
        RectF rect = getMatrixRectF(matrix);
        float[] array = new float[2];
        array[0] = 0;
        array[1] = 0;
        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {
            deltaY = viewHeight - rect.bottom;
        }
        //&& isCheckLeftAndRight
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right;
        }
        array[0] = deltaX;
        array[1] = deltaY;
        return array;
    }


    /**
     * 动画估值器
     */
    public class FloatEvaluator implements TypeEvaluator<Float> {

        @Override
        public Float evaluate(float fraction, Float startValue, Float endValue) {
            return startValue + fraction * (endValue - startValue);
        }

    }


    /**
     * 加载图片时居中显示
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //初始化图片自适应
        float bitmapWidth = mWidthOfPhoto;
        float bitmapHeight = mHeightOfPhoto;
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        Log.d(TAG, "onSizeChanged: bitmapWidth" + bitmapWidth + " viewWidth" + viewWidth);
        if (bitmapWidth > viewWidth && bitmapHeight > viewHeight || bitmapWidth < viewWidth && bitmapHeight < viewHeight)
            matrix.setScale(Math.min(viewWidth / bitmapWidth, viewHeight / bitmapHeight), Math.min(viewWidth / bitmapWidth, viewHeight / bitmapHeight), viewWidth / 2, viewHeight / 2);
        if (bitmapWidth > viewWidth && bitmapHeight < viewHeight)
            matrix.setScale(viewWidth / bitmapWidth, viewWidth / bitmapWidth);
        if (bitmapWidth < viewWidth && bitmapHeight > viewHeight)
            matrix.setScale(viewHeight / bitmapHeight, viewHeight / bitmapHeight);
        float[] array = checkCenterDeltaXY(matrix);
        matrix.postTranslate(array[0], array[1]);
        initScale = getScale(matrix);
        setImageMatrix(matrix);
        Log.d(TAG, "HaHaOnSizeChanged: " + array[0] + " " + array[1]);
    }

    /**
     * 橡皮擦模式。
     *
     * @param eraseMode true 开启
     */
    public void setEraseMode(boolean eraseMode) {
        mPaint.setXfermode(eraseMode ? mDstOut : null);
    }

    /**
     * 设置缩放的最大最小比例
     *
     * @param mScaleMax
     */
    public void setmScaleMax(float mScaleMax) {
        this.mScaleMax = mScaleMax;
    }

    public void setmScaleMin(float mScaleMin) {
        this.mScaleMin = mScaleMin;
    }

    /**
     * 设置画笔的尺寸
     *
     * @param mDrawPaintSize
     */
    public void setmDrawPaintSize(int mDrawPaintSize) {
        this.mDrawPaintSize = mDrawPaintSize;
    }

    /**
     * 设置画笔的颜色
     *
     * @param mDrawPaintColor
     */
    public void setmDrawPaintColor(int mDrawPaintColor) {
        this.mDrawPaintColor = mDrawPaintColor;
    }

}

