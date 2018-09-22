package com.example.yt80.cs591e1_geekin.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;


import java.io.ByteArrayOutputStream;
import java.io.File;
//https://developer.android.com/topic/performance/graphics/load-bitmap.html
//BitmapFactory document

/**
 * Created by yt80 on 2017/4/10.
 */

public class ImageUtil {
    public static int[] getDimension(Context context,int ResId){//with context and resid (make the package more f* retarded)
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), ResId, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new int[]{ imageWidth,imageHeight };
    }
    public static int[] getDimension(Resources res, int ResId){//with res and resid
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, ResId, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new int[]{ imageWidth,imageHeight };
    }
    public static int[] getDimension(String filePath)  {//with file path
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( filePath,options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new int[]{ imageWidth,imageHeight };
    }

    public static int[] getDimension( Uri uri)  {//with uri
        File myFile = new File(uri.toString());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( myFile.getAbsolutePath(),options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        return new int[]{ imageWidth,imageHeight };
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap getScaledBitmap(Bitmap bitmap , int reqWidth, int reqHeight){
        return Bitmap.createScaledBitmap(bitmap, reqWidth, reqHeight, false);
    }

/*
    public static Bitmap getScaledBitmap(Uri uri , int reqWidth, int reqHeight){
        return getScaledBitmap(new File(uri.toString()),reqWidth,reqHeight);
    }
    public static Bitmap getScaledBitmap(File file, int reqWidth, int reqHeight){
        return getScaledBitmap(file.getAbsoluteFile(),reqWidth,reqHeight);
    }
    public static Bitmap getScaledBitmap(String filePath , int reqWidth, int reqHeight){
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);

    }

    public static Bitmap getScaledBitmap(Context context, int resId, int reqWidth, int reqHeight){
        return decodeSampledBitmapFromResource(context.getResources(),resId,reqWidth,reqHeight);
    }

    public static Bitmap getScaledBitmap(Resources res, int resId, int reqWidth, int reqHeight){
        return decodeSampledBitmapFromResource(res,resId,reqWidth,reqHeight);
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }*/



    //
    public static Bitmap getSquareBitmap(File file){
        int []xy= ImageUtil.getDimension(file.getAbsolutePath());
        Bitmap bitmap;
        if(xy[0]<xy[1])
            bitmap=Bitmap.createBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()),0,(xy[1]-xy[0])/2,xy[0],xy[0]);
        else
            bitmap=Bitmap.createBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()),(xy[0]-xy[1])/2,0,xy[1],xy[1]);
        return bitmap;
    }
    public static Bitmap getSquareBitmap(Bitmap bitmap){
        //int []xy=ImageUtil.getDimension(file.getAbsolutePath());
        int []xy=new int[]{bitmap.getWidth(),bitmap.getHeight()};
        if(xy[0]<xy[1])
            bitmap=Bitmap.createBitmap(bitmap,0,(xy[1]-xy[0])/2,xy[0],xy[0]);
        else
            bitmap=Bitmap.createBitmap(bitmap,(xy[0]-xy[1])/2,0,xy[1],xy[1]);
        return bitmap;
    }

    public static String imageToString(Bitmap image){
        //Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.gutswing);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, baos); //bm is the bitmap object

        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
