package com.android.myfirstapp.view.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.myfirstapp.R;
import com.android.myfirstapp.utils.Utils;

public class CircleProgressView extends View {
    private static final String TAG = "CircleProgressView";
    // 背景画笔
    private Paint bgPaint;
    // 进度条画笔
    private Paint progressPaint;
    private Paint textPaint;
    // 进度
    private int value = 0;          //控件进度
    private Integer current = 0;    //动画进度 取值[0,value]
    private float progressWidth;
    private int progressColor;
    private float progressRadius;
    private String text;
    private int textColor;
    // 最大进度值
    private Integer maxCurrent = 0;
    private ValueAnimator mAnimator;

    RectF rectf;

    private boolean needRefresh = false;

    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        if (typedArray!=null){
            progressColor = typedArray.getInt(R.styleable.CircleProgressView_progress_color, Color.WHITE);
            value = typedArray.getInt(R.styleable.CircleProgressView_progress_val, 0);
            maxCurrent = typedArray.getInt(R.styleable.CircleProgressView_progress_max,100);
            progressWidth = typedArray.getDimension(R.styleable.CircleProgressView_progress_width, Utils.dp2px(context,4));
            text = typedArray.getString(R.styleable.CircleProgressView_info_text);
            progressRadius = typedArray.getDimension(R.styleable.CircleProgressView_progress_radius, Utils.dp2px(context,30));
            textColor = typedArray.getColor(R.styleable.CircleProgressView_info_color,Color.BLACK);
            typedArray.recycle();
        }
        rectf = new RectF(progressWidth,progressWidth, progressRadius * 2 + progressWidth ,  progressRadius * 2 + progressWidth);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setStrokeWidth(progressWidth);
        bgPaint.setColor(Color.parseColor("#eaecf0"));

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(progressColor);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(textColor);
        textPaint.setTextSize(Utils.sp2px(getContext(),15));
    }

    @Override
    //make width as same as height
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int computeWidth = (int) (progressRadius * 2 + .5f);
        switch (widthModel){
            case MeasureSpec.UNSPECIFIED:
                computeWidth = (int)((progressWidth + progressRadius)*2 + 0.5f);
                break;
            case MeasureSpec.AT_MOST:
                computeWidth = Math.max(width,(int)((progressWidth + progressRadius)*2 + 0.5f));
                break;
            case MeasureSpec.EXACTLY:
                computeWidth = width;
                break;
        }
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int computeHeight = (int) (progressRadius * 2 + .5f);
        switch (heightModel){
            case MeasureSpec.UNSPECIFIED:
                computeHeight = (int)((progressWidth + progressRadius)*2 + 0.5f);
                break;
            case MeasureSpec.AT_MOST:
                computeHeight = Math.max(height,(int)((progressWidth + progressRadius)*2 + 0.5f));
                break;
            case MeasureSpec.EXACTLY:
                computeHeight = height;
                break;
        }
        setMeasuredDimension(computeWidth,computeHeight);
    }

    @Override
    /*
    draw background first
    then recover with process circle
     */
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(rectf,120,300,false,bgPaint);

        float sweepAngle = 300 * current / maxCurrent;
        canvas.drawArc(rectf,120,sweepAngle,false,progressPaint);

        int currentLen = current.toString().length();
        canvas.drawText(current.toString(),progressWidth+progressRadius - (textPaint.getTextSize() * currentLen / 4f),progressWidth+progressRadius+textPaint.getTextSize()*1.5f,textPaint);
        if(text!=null && !"".equals(text)){
            textPaint.setTextSize(Utils.sp2px(getContext(),20));
            canvas.drawText(text,progressWidth+progressRadius - (textPaint.getTextSize() * text.length() / 2f),progressWidth+progressRadius,textPaint);
            textPaint.setTextSize(Utils.sp2px(getContext(),15));
        }
    }

    ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int animatedValue = (int) animation.getAnimatedValue();
            if(value!=animatedValue){
                setCurrent(animatedValue);
            }else{
                setNeedRefresh(false);
            }
        }
    };
    public void startAnimProgress(int duration){
        if(mAnimator!=null){
            //todo how to pause animation
//            mAnimator.cancel();
//            mAnimator = null;
        }
        mAnimator = ValueAnimator.ofInt(0, value);
        mAnimator.addUpdateListener(listener);
        mAnimator.setDuration(duration);
        setNeedRefresh(true);
        mAnimator.start();
    }

    public void setValue(int val){
        this.value = val;
    }
    private Integer getCurrent() {
        return current;
    }
    private void setCurrent(Integer current) {
        this.current = current;
        ifNeedRefresh();
    }
    public Integer getMaxCurrent() {
        return maxCurrent;
    }
    public void setMaxCurrent(Integer maxCurrent) {
        this.maxCurrent = maxCurrent;
        ifNeedRefresh();
    }
    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
        ifNeedRefresh();
    }
    /*
     *control if need to refresh this progress
     *this method only update the field but not do refresh immediately
     *if you want refresh immediately,use {@link #ifNeedRefresh()}
     */
    public void setNeedRefresh(boolean needRefresh) {
        this.needRefresh = needRefresh;
    }

    private void ifNeedRefresh(){
        if(needRefresh){
            // do refresh operate
            invalidate();
        }
    }

    public void destroy(){
        if(mAnimator!=null){
            mAnimator.cancel();
        }
    }


}

