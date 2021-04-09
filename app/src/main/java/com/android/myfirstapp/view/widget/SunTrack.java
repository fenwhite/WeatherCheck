package com.android.myfirstapp.view.widget;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.android.myfirstapp.R;
import com.android.myfirstapp.utils.Utils;

import java.text.SimpleDateFormat;

public class SunTrack extends View {
    private static final String TAG = "SunTrack";
    //attr
    private int bgColor;
    private int trackColor;
    private float mWidth,mHeight;
    private int lineColor;
    private float lineWidth,lineLength;

    private float windowWidth;
    private float ovalStrokeWidth;

    private SimpleDateFormat format;
    private String startTime,finishTime; //format 2021-12-30T05:44+08:00

    //is sun raise time and sunset time be assigned
    private boolean requestInvalidate = false;

    //paint
    private Paint ovalPaint;
    private Paint linePaint;
    private Paint trackPaint;
    private Paint sunPaint;

    private RectF rectF;
    private float rectLeft,rectRight,rectTop,rectButtom;
    private Bitmap sunIcon;
    private float iconWidth,iconHeight;

    private float theta;
    private ValueAnimator mAnimator;

// ----------------------------------------------------------------------

    public SunTrack(Context context) {
        super(context,null);
    }

    public SunTrack(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SunTrack(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SunTrack);
        if(typedArray!=null){
            bgColor = typedArray.getColor(R.styleable.SunTrack_oval_color, Color.BLACK);
            trackColor = typedArray.getColor(R.styleable.SunTrack_track_color,Color.YELLOW);
            mHeight = typedArray.getDimension(R.styleable.SunTrack_oval_height, Utils.dp2px(context,50));
            mWidth = typedArray.getDimension(R.styleable.SunTrack_oval_width,Utils.dp2px(context,100));
            lineColor = typedArray.getColor(R.styleable.SunTrack_line_color,bgColor);
            lineLength = typedArray.getDimension(R.styleable.SunTrack_line_length,mWidth + Utils.dp2px(context,30));
            lineWidth = typedArray.getDimension(R.styleable.SunTrack_line_width,Utils.dp2px(context,1));
//            int sunIconId = typedArray.getResourceId(R.styleable.SunTrack_sun_icon,R.drawable.sun);
            //todo classify drawable and bitmap
            Drawable d = getResources().getDrawable(R.drawable.sun);
            sunIcon = drawableToBitmap(d);
            iconHeight = sunIcon.getHeight();
            iconWidth = sunIcon.getWidth();
            ovalStrokeWidth = Utils.dp2px(context,1);

            typedArray.recycle();
        }
        //params init
        windowWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        theta = 0f;
        rectLeft = (windowWidth - mWidth)/2;
        rectRight = (windowWidth + mWidth)/2;
        rectTop = ovalStrokeWidth + iconHeight/2;
        rectButtom = mHeight + ovalStrokeWidth + iconHeight/2;
        rectF = new RectF((windowWidth - mWidth)/2,
                ovalStrokeWidth + iconHeight/2,
                (windowWidth + mWidth)/2,
                mHeight + ovalStrokeWidth + iconHeight/2);

        //init paint
//        ovalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        ovalPaint.setStyle(Paint.Style.STROKE);
//        ovalPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
//        ovalPaint.setStrokeWidth(ovalStrokeWidth);
//        ovalPaint.setStrokeCap(Paint.Cap.ROUND);

        trackPaint = new Paint();
        trackPaint.setAntiAlias(true);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(ovalStrokeWidth);
        trackPaint.setPathEffect(new DashPathEffect(new float[]{10, 10}, 0));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(lineColor);

        sunPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int computeWidth = (int) windowWidth;
        switch (widthModel){
            case MeasureSpec.EXACTLY:
                computeWidth = width;
                break;
            case MeasureSpec.AT_MOST:
                computeWidth = (int) Math.max(width,lineLength);
                break;
            case MeasureSpec.UNSPECIFIED:
                computeWidth = width;
                break;
        }

        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int paddingTop = (int)Utils.dp2px(getContext(),5);
        int computeHeight = (int) (mHeight+0.5f);
        switch(heightModel){
            case MeasureSpec.EXACTLY:
                computeHeight = height;
                break;
            case MeasureSpec.AT_MOST:
                computeHeight = (int) Math.max(height,mHeight/2+ ovalStrokeWidth + iconHeight/2+paddingTop);
                break;
            case MeasureSpec.UNSPECIFIED:
                computeHeight = (int) Math.max(height,mHeight/2+ ovalStrokeWidth + iconHeight/2+paddingTop);
                break;
        }
        setMeasuredDimension(computeWidth,computeHeight);
    }

    /*
        calculate (finish HH*60+finish mm) - (start HH*60+start mm)
        return min between start and finish
     */
    private int betweenTimeInMin(String start,String finish){
        int pos = start.indexOf('T');
        int h = Integer.valueOf(start.substring(pos+1,pos+3));
        int m = Integer.valueOf(start.substring(pos+4,pos+6));
        int startMin = h * 60 + m;

        pos = finish.indexOf('T');
        h = Integer.valueOf(finish.substring(pos+1,pos+3));
        m = Integer.valueOf(finish.substring(pos+4,pos+6));
        int finishMin =  h * 60 + m;

        return finishMin - startMin;
    }

    /*
    use parameter equation of oval
    x = a sin/theta
    y = b sin/theta
    to calculate coordination where sun pic to draw
    by the way, theta also means the sweep arc of track
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //draw under graph
        trackPaint.setColor(bgColor);
        canvas.drawArc(rectF,180,180,
                false,trackPaint);
        canvas.save();

        //draw track by theta
        trackPaint.setColor(trackColor);
        canvas.drawArc(rectF,180,theta,
                false,trackPaint);

        //draw sun pic
        float x = (float) (mHeight/2 * (1 - Math.sin(theta/180*Math.PI)));
        float y = (float) (mWidth/2 * (1 - Math.cos(theta/180*Math.PI)));
        canvas.drawBitmap(sunIcon,y + rectLeft - iconWidth/2,
                x,sunPaint);

        //draw line
        canvas.drawLine((windowWidth-lineLength)/2,
                mHeight/2+ ovalStrokeWidth + iconHeight/2 - 1,
                (windowWidth+lineLength)/2,
                mHeight/2+ ovalStrokeWidth + iconHeight/2,
                linePaint);
    }

    private String getNow(){
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss+8:00");
        return format.format(System.currentTimeMillis());
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        requestInvalidate = isRequestInvalidate();
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
        requestInvalidate = isRequestInvalidate();
    }

    private boolean isRequestInvalidate(){
        if((finishTime!=null) && (startTime!=null)){
            // todo check time legal
            return true;
        }
        return false;
    }

    private ValueAnimator.AnimatorUpdateListener listener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            theta = (float) animation.getAnimatedValue();
            invalidate();
        }
    };
    private void updateAnimation(){
        int totalMin = betweenTimeInMin(startTime,finishTime);
        int nowMin = betweenTimeInMin(startTime,getNow());
        float sweepArc = ((float)nowMin)/totalMin * 180;
        // avoid time at when before sun arise or after sun set
        sweepArc = Math.min(180,Math.max(sweepArc,0));
        mAnimator = ValueAnimator.ofFloat(0f,sweepArc);
        mAnimator.addUpdateListener(listener);
    }

    public void startAnimation(int duration){
        if(requestInvalidate){
            if(mAnimator!=null){
                //todo how to pause animation
//                mAnimator.cancel();
//                mAnimator = null;
            }
            updateAnimation();

            mAnimator.setDuration(duration);
            mAnimator.start();
        }else{
            Log.d(TAG, "startAnimation: please check your sun raise time or sunset time");
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void destroy(){
        if(mAnimator!=null){
            mAnimator.cancel();
        }
    }
}
