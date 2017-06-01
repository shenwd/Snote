package com.shen.snote.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import com.shen.snote.R;
import com.shen.snote.utils.DensityUtils;


/**
 * Created by shen on 2017/3/14.
 */

public class RecorderView extends View {

    private Context mContext;
    private int pandding = 7;//圆环到四周的间隔
    private int widthing = 6;//圆环的厚度
    private int length = 200;//view的总长度和宽度

    private static final double PI = 3.1415926;
    private boolean start = false;//控制圆环转动

    private float pointX ;//点的x坐标
    private float pointY ;//点的Y坐标

    private float caverageDegress = 0;

    public int getShowTime() {
        return showTime;
    }

    public void setShowTime(int showTime) {
        this.showTime = showTime;
    }

    private int showTime;

    public RecorderView(Context context) {
        this(context,null);

    }

    public RecorderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RecorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        
        init();
    }

    private void init() {
        countPointXY();
    }

    public void start(){
        start = true;
        invalidate();
    }

    /**
     * 重置
     * */
    public void reset(){
        degress =  0;
        countPointXY();
        startProgressCircleDegress = 0;
        caverageDegress = 0;
        start = false;
        showTime = 0;
    }


    private float degress = 0;//转动的度数
    /**
     * 开始转动圆环和圆点
     * */
    private void startCircleRun() {
        degress+=2;
        if(caverageDegress <= 180){
            caverageDegress += 2;
        } else {
            startProgressCircleDegress+=2;
        }
        if(degress == 360){
            degress = 0;
        }
        if(startProgressCircleDegress == 360){
            startProgressCircleDegress = 0;
        }

        countPointXY();

        invalidate();
    }

    private void countPointXY() {
        float r = (length-pandding*2)/2;
        float huDegress = (float) (degress * PI/180);
        double sin = Math.sin(huDegress);
        pointX = (float) (length/2 + r*sin);
        double cos = Math.cos(huDegress);
        pointY = (float) (pandding + (r - r*cos));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(getResources().getColor(R.color.transparent));
        drawBackground(canvas);
        drawCircle(canvas);
        drawProgressCircle(canvas);
        drawPoint(canvas);

        if(caverageDegress <=180){
            drawCaverageCircle(canvas);
        }

//        drawHintText(canvas);
//        drawTime(canvas);
        canvas.save();
        if(start){
            startCircleRun();
        }


    }

    /**
     * 在圆环刚开始转动时，覆盖圆
     *
     * @param canvas*/
    private void drawCaverageCircle(Canvas canvas) {
        Paint mPaint = new Paint();
        /**
         * 画一个大圆(纯色)
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DensityUtils.dp2px(mContext, widthing));
        mPaint.setColor(mContext.getResources().getColor(R.color.RoundColor));
        RectF oval1 = new RectF( DensityUtils.dp2px(mContext, pandding),
                DensityUtils.dp2px(mContext, pandding),
                getWidth()-DensityUtils.dp2px(mContext, pandding),
                getHeight()-DensityUtils.dp2px(mContext, pandding));
        canvas.drawArc(oval1, 270+caverageDegress, 180, false, mPaint); //绘制圆弧
    }
    /**
     * 渐变的点
     * */
    private void drawPoint(Canvas canvas) {

        int[] colors = new int[]{
                0xFF03FAF6,0x11ffffff
        };

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        RadialGradient rg = new RadialGradient(DensityUtils.dp2px(mContext, pointX),DensityUtils.dp2px(mContext, pointY),DensityUtils.dp2px(mContext,10),
                colors,null, Shader.TileMode.REPEAT);
        paint.setShader(rg);
        canvas.drawCircle(DensityUtils.dp2px(mContext, pointX),DensityUtils.dp2px(mContext, pointY),DensityUtils.dp2px(mContext,10),paint);

    }

    private int progress = 0;
    private int startProgressCircleDegress = 0;
    /**
     * 画绕圈渐变的圆环
     * */
    private void drawProgressCircle(Canvas canvas) {

        int[] colors = new int[]{
                0x00000000,0x00000000,0x00000000,0x00000000,0x00000000,
                0x1104F2C6,0x2204F2C6,0x3304F2C6,0x4404F2C6,0x5504F2C6,
                0x5504F2C6,0x6604F2C6,0x8804F2C6,0x9904F2C6,0xFF04F2C6
        };

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(DensityUtils.dp2px(mContext, widthing));
//        paint.setColor(mContext.getResources().getColor(R.color.RoundProgressColor));
        SweepGradient sweepGradient = new SweepGradient(getWidth() / 2, getHeight() / 2,colors, null);
        //旋转 不然是从0度开始渐变
        Matrix matrix = new Matrix();
        matrix.setRotate(-90+startProgressCircleDegress+180, this.getWidth() / 2, this.getHeight() / 2);
        sweepGradient.setLocalMatrix(matrix);
        paint.setShader(sweepGradient);

        RectF oval1 = new RectF( DensityUtils.dp2px(mContext, pandding),
                DensityUtils.dp2px(mContext, pandding),
                getWidth()-DensityUtils.dp2px(mContext, pandding),
                getHeight()-DensityUtils.dp2px(mContext, pandding));
        canvas.drawArc(oval1, startProgressCircleDegress,360,false,paint);

    }

    private void drawBackground(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawCircle(DensityUtils.dp2px(mContext,length/2),DensityUtils.dp2px(mContext,length/2),DensityUtils.dp2px(mContext,100-pandding),paint);
    }

    private void drawTime(Canvas canvas){
        /**
         * 画时间
         * */
        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setTextSize(DensityUtils.dp2px(mContext,60));
        paint2.setColor(mContext.getResources().getColor(R.color.TimeTextColor));
        paint2.setTextAlign(Paint.Align.CENTER);
        if(showTime < 10){
            canvas.drawText("0"+showTime+"s", getWidth()/2, getHeight()/2-20, paint2);
        } else {
            canvas.drawText(showTime+"s", getWidth()/2, getHeight()/2-20, paint2);
        }
        paint2.setTextSize(DensityUtils.dp2px(mContext,30));
        canvas.drawText("s", getWidth()/2+20, getHeight()/2-20, paint2);

    }

    /**
     * 画显示的文字
     * */
    private void drawHintText(Canvas canvas) {
        int textHintSize = 16;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(DensityUtils.dp2px(mContext,textHintSize));
        paint.setColor(mContext.getResources().getColor(R.color.RoundHintTextColor));
// 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("正在录制:", getWidth()/2-30, getHeight()/2-80, paint);
    }


    private void drawCircle(Canvas canvas) {


        Paint mPaint = new Paint();
        /**
         * 画一个大圆(纯色)
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(DensityUtils.dp2px(mContext, widthing));
        mPaint.setColor(mContext.getResources().getColor(R.color.RoundColor));
        RectF oval1 = new RectF( DensityUtils.dp2px(mContext, pandding),
                DensityUtils.dp2px(mContext, pandding),
                getWidth()-DensityUtils.dp2px(mContext, pandding),
                getHeight()-DensityUtils.dp2px(mContext, pandding));
        canvas.drawArc(oval1, 0, 360, false, mPaint); //绘制圆弧

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(DensityUtils.dp2px(mContext,length),DensityUtils.dp2px(mContext,length));
    }
}
