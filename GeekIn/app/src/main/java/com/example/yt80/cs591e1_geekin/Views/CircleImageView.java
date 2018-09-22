package com.example.yt80.cs591e1_geekin.Views;
/***
 * Created by yt80 on 2017/4/8.
 */

/*
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

    //基本的三个构造函数
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    //自定义View实现过程中很重要的onDraw绘制图形的方法
    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        //空值判断，必要步骤，避免由于没有设置src导致的异常错误
        if (drawable == null) {
            return;
        }

        //必要步骤，避免由于初始化之前导致的异常错误
        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if (null == b) {
            return;
        }

        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = Math.min(getWidth(),getHeight());

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);

    }

    /**
     * 初始Bitmap对象的缩放裁剪过程
     * @param bmp        初始Bitmap对象
     * @param diameter    圆形图片直径大小
     * @return 返回一个圆形的缩放裁剪过后的Bitmap对象
     */
/*
    public static Bitmap getCroppedBitmap(Bitmap bmp, int diameter) {
        Bitmap sbmp;
        //比较初始Bitmap宽高和给定的圆形直径，判断是否需要缩放裁剪Bitmap对象
        if (bmp.getWidth() != diameter || bmp.getHeight() != diameter)
            sbmp = Bitmap.createScaledBitmap(bmp, diameter, diameter, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2.0f ,
                sbmp.getHeight() / 2.0f, sbmp.getWidth() / 2 , paint);
        //核心部分，设置两张图片的相交模式，在这里就是上面绘制的Circle和下面绘制的Bitmap
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

}
*/


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/**
 * 自定义的圆形ImageView，可以直接当组件在布局中使用。
 */
@SuppressLint("DrawAllocation")
public class CircleImageView extends ImageView{

    private Paint paint ;
    public CircleImageView(Context context) {
        this(context,null);
    }
    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();

    }

    /**
     *  绘制圆形图片
     *  @author se7en
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //canvas.drawARGB(1,255,0,0);
        //canvas.drawLine(0.0f,0.0f,400.0f,400.0f,paint);
        Drawable drawable = getDrawable();
        if (drawable != null) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap b = getCircleBitmap(bitmap, 14);
            int z = Math.min(b.getWidth(),b.getHeight());
            final Rect rectSrc = new Rect(0, 0, z , z);

            DisplayMetrics dm = new DisplayMetrics();
            ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

            final Rect rectDest = new Rect(0,0,getWidth(),getHeight());//new Rect(0,0,dm.widthPixels/2,dm.widthPixels/2);
            paint.reset();
            canvas.drawBitmap(b, rectSrc, rectDest, paint);

        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取圆形图片方法
     * @param bitmap
     * @param pixels
     * @return Bitmap
     * @author se7en
     */
    private Bitmap getCircleBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        int min = Math.min(bitmap.getWidth(),bitmap.getHeight());
        final Rect rect = new Rect(0, 0, min , min);
        paint.setAntiAlias(true);//抗锯齿

        canvas.drawARGB(0, 0, 0, 0);//绘制背景颜色，第一个参数为透明度
        paint.setColor(color);//
        canvas.drawCircle(min / 2, min / 2, min / 2, paint);


        //paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));//add a rectangle at the bottom
        //canvas.drawRect(0,min/2,min,min,paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));//http://blog.csdn.net/oqihaogongyuan/article/details/48269099
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}