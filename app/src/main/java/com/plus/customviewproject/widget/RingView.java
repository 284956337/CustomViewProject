package com.plus.customviewproject.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.plus.customviewproject.R;

/**
 * 类描述：自定义圆环
 * 创建人：zw
 * 创建时间：2017/10/24-9:44
 * 修改备注：
 */

public class RingView extends View{

    private int mFirstColor;
    private int mSecondColor;
    private int mCircleWidth;//圆环宽度
    //当前进度
    private int mProgress;
    //速度
    private int mSpeed;
    private Paint mPaint;
    private boolean isNext = false;


    public RingView(Context context) {
        this(context, null);
    }

    public RingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RingView, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr){
                case R.styleable.RingView_firstColor:
                    mFirstColor = typedArray.getColor(attr, Color.RED);
                    break;
                case R.styleable.RingView_secondColor:
                    mSecondColor = typedArray.getColor(attr, Color.RED);
                    break;
                case R.styleable.RingView_speed:
                    mSpeed = typedArray.getInt(attr, 10);//默认10
                    break;
                case R.styleable.RingView_circleWidth:
                    mCircleWidth = typedArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                                    20, getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();

        mPaint = new Paint();

        new Thread(){
            @Override
            public void run() {
                while(true){
                    mProgress++;
                    if(mProgress == 360){
                        mProgress = 0;
                        isNext = !isNext;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(mSpeed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centre = getWidth() / 2; //中心的x坐标
//        Log.d("TAG", "centre = " + centre);
        int radius = centre - mCircleWidth / 2; //半径
//        Log.d("TAG", "radius = " + radius);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setStyle(Paint.Style.STROKE);//设置空心

        //用于定于圆弧的大小和界限
        RectF oval = new RectF(centre - radius, centre - radius,
                centre + radius, centre + radius);

        if(isNext){//第一圈颜色完整， 第二圈跑
            mPaint.setColor(mFirstColor);
            canvas.drawCircle(centre, centre, radius, mPaint);
            mPaint.setColor(mSecondColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint);//根据进度画出圆弧

        }else {
            mPaint.setColor(mSecondColor);
            canvas.drawCircle(centre, centre, radius, mPaint);
            mPaint.setColor(mFirstColor);
            canvas.drawArc(oval, -90, mProgress, false, mPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
