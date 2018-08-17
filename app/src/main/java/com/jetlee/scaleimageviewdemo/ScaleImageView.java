package com.jetlee.scaleimageviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

/**
 * @author：Jet啟思
 * @date:2018/8/13 16:46
 */
public class ScaleImageView extends View {

    private Context context;

    private Bitmap bitmap;
    private static final float IMAGE_SIZE = Utils.dpToPx(200);
    private Paint paint;

    // 缩放比例
    private float scale;

    // 拖动图片的偏移量
    private float dragOffsetX;
    private float dragOffsetY;

    private float bitmapWidth;
    private float bitmapHeight;
    private boolean isScale;
    private ObjectAnimator objectAnimator;
    // 动画系数
    private float scalingFraction;

    private ScaleGestureDetector scaleGestureDetector;
    private SimpleScaleGestureListener simpleScaleGestureListener = new SimpleScaleGestureListener();
    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener simpleGestureListener = new SimpleGestureListener();
    private GestureDetector.OnDoubleTapListener doubleTapListener = new DoubleTapListener();
    private OverScroller overScroller;
    FlingRunnable flingRunnable = new FlingRunnable();
    // 用于判断惯性滑动后是否有按下事件
    private boolean isFling;

    private int resource;

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(attrs);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
        resource = typedArray.getResourceId(R.styleable.ScaleImageView_src, -1);
        typedArray.recycle();

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scaleGestureDetector = new ScaleGestureDetector(context, simpleScaleGestureListener);
        gestureDetector = new GestureDetector(context, simpleGestureListener);
        gestureDetector.setOnDoubleTapListener(doubleTapListener);
        overScroller = new OverScroller(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Utils.getBitmap(getResources(), getWidth(), resource != -1 ? resource : R.mipmap.test1);
        // 图片宽度
        bitmapWidth = bitmap.getWidth();
        bitmapHeight = bitmap.getHeight();
        Log.e("20000", "bitmapWidth" + bitmap.getWidth());
        Log.e("20000", "bitmapHeight" + bitmap.getHeight());
        Log.e("20000", "height" + getHeight());
        // 图片高度
//        if (bitmap.getHeight() >= getHeight()) {  // 图片本身高度 >= 控件高度，把
//            bitmapHeight = getHeight();
//        } else {
//            bitmapHeight = bitmap.getHeight();
//        }
        if (bitmapHeight > getHeight()) {
            scale = bitmapHeight / getHeight();
//            scalingFraction = 1;
        } else if (bitmapHeight > getHeight() * 3 / 4 && bitmapHeight < getHeight()) {
            scale = getHeight() / bitmapHeight * 2;
        } else {
            scale = getHeight() / bitmapHeight;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 拖动图片
        if (!isScale && bitmapHeight > getHeight()) {
            canvas.translate(dragOffsetX, dragOffsetY);
        } else {
            canvas.translate(dragOffsetX * scalingFraction, dragOffsetY * scalingFraction);
        }

        float realScale = 1 + (scale - 1) * scalingFraction;
        canvas.scale(realScale, realScale, getWidth() / 2, getHeight() / 2);
        //  把原点移到中心
        canvas.translate((getWidth() - bitmapWidth) / 2, (getHeight() - bitmapHeight) / 2);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    class SimpleScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();
            float scaleFactor = detector.getScaleFactor();

            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            isFling = false;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent down, MotionEvent event, float distanceX, float distanceY) {
            float widthBound = (bitmapWidth * scale - getWidth()) / 2;
            float heightBound = (bitmapHeight * scale - getHeight()) / 2;
            dragOffsetX -= distanceX;
            dragOffsetY -= distanceY;

            // -widthBound是左边界，widthBound是右边界，移动的范围在两者内
            dragOffsetX = Math.min(Math.max(dragOffsetX, -widthBound), widthBound);
            dragOffsetY = Math.min(Math.max(dragOffsetY, -heightBound), heightBound);

            invalidate();
            return false;
        }

        @Override
        public boolean onFling(MotionEvent down, MotionEvent event, float velocityX, float velocityY) {
            isFling = true;
            // ⽤用于自动计算滑动的偏移。
            overScroller.fling((int) dragOffsetX, (int) dragOffsetY, (int) velocityX, (int) velocityY,
                    (int) (-(bitmapWidth * scale - getWidth()) / 2), (int) (bitmapWidth * scale - getWidth()) / 2,
                    (int) (-(bitmapHeight * scale - getHeight()) / 2), (int) (bitmapHeight * scale - getHeight()) / 2);

            // 下一帧更新
            postOnAnimation(flingRunnable);
            return false;
        }
    }

    @SuppressLint("NewApi")
    class FlingRunnable implements Runnable {
        @Override
        public void run() {
            // 判断是否还在滑动，返回false即代表滑动停止
            if (isFling && overScroller.computeScrollOffset()) {
                dragOffsetX = overScroller.getCurrX();
                dragOffsetY = overScroller.getCurrY();
                invalidate();
                // 继续下一帧
                postOnAnimation(this);
            }
        }
    }

    class DoubleTapListener implements GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            isScale = !isScale;
            if (isScale) {
                // 相当于拖动偏移，也是要边界的判断
                dragOffsetX = getWidth() / 2 - e.getX();
                dragOffsetY = getHeight() / 2 - e.getY();

                float widthBound = (bitmapWidth * scale - getWidth()) / 2;
                float heightBound = (bitmapHeight * scale - getHeight()) / 2;
                // -widthBound是左边界，widthBound是右边界，移动的范围在两者内
                dragOffsetX = Math.min(Math.max(dragOffsetX, -widthBound), widthBound);
                dragOffsetY = Math.min(Math.max(dragOffsetY, -heightBound), heightBound);

                getScaleObjectAnimator().start();
            } else {
                getScaleObjectAnimator().reverse();
            }
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }

    private ObjectAnimator getScaleObjectAnimator() {
        if (objectAnimator == null) {
            objectAnimator = ObjectAnimator.ofFloat(this, "scalingFraction", 0, 1);
            objectAnimator.setDuration(200);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    if (isReverse) {  // 当恢复时把偏移置0，回到中心。
                        dragOffsetX = 0;
                        dragOffsetY = 0;
                    }
                }
            });
        }
        return objectAnimator;
    }

    public float getScalingFraction() {
        return scalingFraction;
    }

    public void setScalingFraction(float scalingFraction) {
        this.scalingFraction = scalingFraction;
        invalidate();
    }
}
