package com.kdz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSRuntimeException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.squareup.picasso.Transformation;

public class BlurTransform implements Transformation {

    private static int MAX_RADIUS = 25;
    private static int DEFAULT_DOWN_SAMPLING = 1;

    private Context mContext;

    private int mRadius;
    private int mSampling;

    public BlurTransform(Context context) {
        this(context, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransform(Context context, int radius) {
        this(context, radius, DEFAULT_DOWN_SAMPLING);
    }

    public BlurTransform(Context context, int radius, int sampling) {
        mContext = context.getApplicationContext();
        mRadius = radius;
        mSampling = sampling;
    }

    @Override public Bitmap transform(Bitmap source) {

        int scaledWidth = source.getWidth() / mSampling;
        int scaledHeight = source.getHeight() / mSampling;
        Matrix matrix = new Matrix();
        matrix.preScale(-1, 1);
        //Bitmap bitmap = Bitmap.createBitmap(source, 0,0 , scaledWidth, scaledHeight, matrix, true);

        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        //canvas.scale(1 / (float) mSampling, 1 / (float) mSampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                bitmap = RSBlur.blur(mContext, bitmap, mRadius);
            } catch (RSRuntimeException e) {
                bitmap = FastBlur.blur(bitmap, mRadius, true);
            }
        } else {
            bitmap = FastBlur.blur(bitmap, mRadius, true);
        }

        source.recycle();

        return bitmap;
    }

    @Override public String key() {
        return "BlurTransformation(radius=" + mRadius + ", sampling=" + mSampling + ")";
    }

}