package com.jetlee.scaleimageviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author：Jet啟思
 * @date:2018/8/13 16:46
 */
public class ScaleImageView extends View {
    private Bitmap bitmap;
    private static final float IMAGE_SIZE = Utils.dpToPx(200);
    private Paint paint;

    // 缩放比例
    private float scale;
    private float smallScale;

    // 拖动图片的偏移量
    private float dragOffsetX;
    private float dragOffsetY;

    private float bitmapWidth;
    private float bitmapHeight;
    private boolean isScale;
    private ObjectAnimator objectAnimator;
    // 动画系数
    private float scalingFraction;

    private GestureDetector gestureDetector;
    private GestureDetector.OnGestureListener simpleGestureListener = new SimpleGestureListener();
    private GestureDetector.OnDoubleTapListener doubleTapListener = new DoubleTapListener();

    public ScaleImageView(Context context) {
        super(context);
        init(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        gestureDetector = new GestureDetector(context, simpleGestureListener);
        gestureDetector.setOnDoubleTapListener(doubleTapListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Utils.getBitmap(getResources(), getWidth());
        // 图片宽度
        bitmapWidth = getWidth();
        // 图片高度
        bitmapHeight = bitmapWidth / (bitmap.getWidth() / bitmap.getHeight());
        scale = getHeight() / bitmapHeight;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 拖动图片
        canvas.translate(dragOffsetX * scalingFraction, dragOffsetY * scalingFraction);

        float realScale = 1 + (scale - 1) * scalingFraction;
        canvas.scale(realScale, realScale, getWidth() / 2, getHeight() / 2);
        //  把原点移到中心
        canvas.translate(0, (getHeight() - bitmapHeight) / 2);
        canvas.drawBitmap(bitmap, 0, 0, paint);
    }

    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent down, MotionEvent event, float distanceX, float distanceY) {
            if (isScale) {
                float widthBound = (bitmapWidth * scale - getWidth()) / 2;
                float heightBound = (bitmapHeight * scale - getHeight()) / 2;
                dragOffsetX -= distanceX;
                dragOffsetY -= distanceY;

                // -widthBound是左边界，widthBound是右边界，移动的范围在两者内
                dragOffsetX = Math.min(Math.max(dragOffsetX, -widthBound), widthBound);
                dragOffsetY = Math.min(Math.max(dragOffsetY, -heightBound), heightBound);

                invalidate();
            }
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);
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
