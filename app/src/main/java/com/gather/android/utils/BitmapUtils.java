package com.gather.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Levi on 2015/8/20.
 */
public class BitmapUtils {

    public static void recycleBitmap(Bitmap bitmap){
        if (bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static long getBitmapsize(Bitmap bitmap){
        if (bitmap == null){
            return 0L;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    /**
     * 这种方式压缩保存的图片大小不变，质量下降。如800*800的图片压缩保存后还是800*800，但质量下降
     * @param bitmap
     * @param quality
     * @return
     */
    public static boolean compressBitmap(Bitmap bitmap, int quality, String savedFilePath){
        if (bitmap == null || bitmap.isRecycled() || quality > 100 || quality <= 0){
            return false;
        }
        FileOutputStream fos = null;
        boolean success = false;
        try {
            fos = new FileOutputStream(savedFilePath);
            success = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);
        } catch (FileNotFoundException e) {
        }
        finally {
            if (fos != null){
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return success;
    }


    /**
     * 这种压缩后保存的图片大小会改变，但质量较好。如1200*1200的图片压缩保存后会变成1000*1000，但质量较好
     * @param filePath
     * @param maxNumOfPixels
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath, int maxNumOfPixels){
        Bitmap bitmap;
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            opts.inSampleSize = computeSampleSize(opts, -1, maxNumOfPixels);
            opts.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        }catch (Exception e) {
            bitmap = null;
        }
        return bitmap;
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
