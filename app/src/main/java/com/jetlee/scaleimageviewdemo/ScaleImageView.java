package com.jetlee.scaleimageviewdemo;

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
        bitmap = Utils.getBitmap(getResources(), (int) IMAGE_SIZE);

        gestureDetector = new GestureDetector(context, simpleGestureListener);
        gestureDetector.setOnDoubleTapListener(doubleTapListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isDetect = gestureDetector.onTouchEvent(event);
        if (isDetect) {
            return gestureDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, (getWidth() - IMAGE_SIZE) / 2, (getHeight() - IMAGE_SIZE) / 2, paint);
    }

    class SimpleGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
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
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    }


}
