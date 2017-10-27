package com.plus.customviewproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.plus.customviewproject.R;

/**
 * 类描述：
 * 创建人：zw
 * 创建时间：2017/10/24-11:33
 * 修改备注：
 */

public class CustomVolumeControlBar extends View {

    private int mFirstColor;
    private int mSecondColor;
    private int mCircleWidth;//圆环宽度
    private int spaceCount;//空格数量
    private int dotCount;//格子数量
    private int splitSize;//间隔
    private int mCurrentCount = 3;
    private Bitmap bgBitmap;

    private Paint mPaint;
    private Rect rect;

    public CustomVolumeControlBar(Context context) {
        this(context, null);
    }

    public CustomVolumeControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomVolumeControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomVolumeControlBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.CustomVolumeControlBar_firstColor:
                    mFirstColor = typedArray.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomVolumeControlBar_secondColor:
                    mSecondColor = typedArray.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.CustomVolumeControlBar_dotCount:
                    dotCount = typedArray.getInt(attr, 12);//默认12
                    break;
                case R.styleable.CustomVolumeControlBar_spaceCount:
                    spaceCount = typedArray.getInt(attr, 0);//默认0
                    break;
                case R.styleable.CustomVolumeControlBar_circleWidth:
                    mCircleWidth = typedArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                                    20, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomVolumeControlBar_bg:
                    bgBitmap = BitmapFactory.decodeResource(getResources(), typedArray.getResourceId(attr, 0));
                    break;
                case R.styleable.CustomVolumeControlBar_splitSize:
                    splitSize = typedArray.getInt(attr, 20);
            }
        }
        typedArray.recycle();

        mPaint = new Paint();
        rect = new Rect();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);

        int centre = getWidth() / 2;
        int radius = centre - mCircleWidth / 2;

        drawOval(centre, canvas, radius);

        /**
         * 计算内切正方形的位置
         */
        int relRadius = radius - mCircleWidth / 2; //内圆半径

        /**
         * 内切正方形的距离顶部 = mCircleWidth + relRadius - √2 / 2
         */
        rect.left = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;

        /**
         * 内切正方形的距离左边 = mCircleWidth + relRadius - √2 / 2 
         */
        rect.top = (int) (relRadius - Math.sqrt(2) * 1.0f / 2 * relRadius) + mCircleWidth;
        rect.bottom = (int) (rect.left + Math.sqrt(2) * relRadius);
        rect.right = (int) (rect.left + Math.sqrt(2) * relRadius);

        /**
         * 如果图片比较小，那么根据图片的尺寸放置到正中心 
         */
        if (bgBitmap.getWidth() < Math.sqrt(2) * relRadius)
        {
            rect.left = (int) (rect.left + Math.sqrt(2) * relRadius * 1.0f / 2 - bgBitmap.getWidth() * 1.0f / 2);
            rect.top = (int) (rect.top + Math.sqrt(2) * relRadius * 1.0f / 2 - bgBitmap.getHeight() * 1.0f / 2);
            rect.right = (int) (rect.left + bgBitmap.getWidth());
            rect.bottom = (int) (rect.top + bgBitmap.getHeight());

        }
        // 绘图  
        canvas.drawBitmap(bgBitmap, null, rect, mPaint);
    }

    /**
     * 画块
     * @param centre
     * @param canvas
     * @param radius
     */
    private void drawOval(int centre, Canvas canvas, int radius) {
        /**
         * 根据要画的块以及间隙来计算每个要画的块所占比例 * 360
         */
        float itemSize = (360 * 1.0f - dotCount * splitSize) / dotCount;

        float spaceSize = itemSize * spaceCount + splitSize * (spaceCount + 1);

        RectF rectF = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);

        mPaint.setColor(mFirstColor);
        for (int i = 0; i < dotCount - spaceCount; i++) {
            canvas.drawArc(rectF, i * (itemSize + splitSize) + 90 + spaceSize / 2, itemSize, false, mPaint);
        }

        mPaint.setColor(mSecondColor); // 设置圆环的颜色
        for (int i = 0; i < mCurrentCount; i++)
        {
            canvas.drawArc(rectF, i * (itemSize + splitSize) + 90 + spaceSize / 2, itemSize, false, mPaint); // 根据进度画圆弧
        }
    }

    private void up(){
        if(mCurrentCount < dotCount - spaceCount){
            mCurrentCount++;
            postInvalidate();
        }
    }

    private void down(){
        if(mCurrentCount > 0){
            mCurrentCount--;
            postInvalidate();
        }
    }

    private int xDown, xUp;
    private int different = 5;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                xDown = (int) event.getY();
                different = 5;
                break;
            case MotionEvent.ACTION_MOVE:
                xUp = (int) event.getY();
                if(xUp > (xDown+different)){
                    Log.d("TAG", "xDown = " + xDown + ", xUp = " + xUp);
                    up();
                    different += 5;
                }
                if(xUp < (xDown+different)){
                    Log.d("TAG", "xDown = " + xDown + ", xUp = " + xUp);
                    down();
                    different += 5;
                }
                break;

        }
        return true;
    }
}
