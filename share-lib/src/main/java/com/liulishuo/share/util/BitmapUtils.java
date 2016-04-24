package com.liulishuo.share.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Created by Levi on 2015/8/20.
 */
public class BitmapUtils {

    // 32k 缩略图的限制，所以采用 RGB_565 比较小
    private static final Bitmap.Config CONFIG = Bitmap.Config.RGB_565;

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

    public static Bitmap getBitmapFromUrl(String imageUrl) {
        Bitmap bmp = null;
        try {
            if (!TextUtils.isEmpty(imageUrl) && imageUrl.startsWith("http")) {
                URL url = new URL(imageUrl);
                bmp = BitmapFactory.decodeStream(url.openStream());
            } else {
                bmp = BitmapFactory.decodeFile(imageUrl);
            }
        } catch (Throwable ex) {

        }
        return bmp;
    }

    // 根据给定的高宽居中缩放+裁剪
    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, CONFIG);
        Canvas canvas = new Canvas(dest);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(source, null, targetRect, paint);

        return dest;
    }
}
