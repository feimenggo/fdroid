package com.feimeng.fdroid.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片压缩 工具
 * Created by feimeng on 2016/10/12.
 */
public class BitmapCompress {

    /**
     * 根据从ImageView得到的宽高，对Bitmap进行二次采样(压缩)
     *
     * @param bitmap    原始Bitmap
     * @param imageView 从ImageView得到宽高
     * @param quality   压缩的质量
     * @return 压缩后的Bitmap
     */
    public static Bitmap getSmallBitmap(Bitmap bitmap, ImageView imageView, int quality) {
        // 得到ImageView的宽高
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        int width = params.width;
        int height = params.height;
        return getSmallBitmap(bitmap, width, height, quality);
    }

    /**
     * 根据指定宽高，对Bitmap进行二次采样(压缩)
     *
     * @param bitmap  原始Bitmap
     * @param width   期望的宽度
     * @param height  期望的高度
     * @param quality 压缩的质量
     * @return 压缩后的Bitmap
     */
    public static Bitmap getSmallBitmap(Bitmap bitmap, int width, int height, int quality) {
        if (bitmap == null) return null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (quality < 0 || quality > 100) quality = 100;
        boolean compress = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        if (!compress) return null;
        L.d("BitmapCompress", "原图   宽：" + bitmap.getWidth() + " 高：" + bitmap.getHeight() + " 大小：" + bitmap.getByteCount());
        byte[] bytes = bos.toByteArray();
        try {
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // 创建图片选项并设置其参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置图片内部格式
        options.inPreferredConfig = Bitmap.Config.RGB_565; // 新图大小可能减少一半
        // 计算宽高缩放比例
        if (width != 0 && height != 0) {
            int scaleWidth = bitmap.getWidth() / width;
            int scaleHeight = bitmap.getHeight() / height;
            // 设置缩放比例(高缩放比例取最小值)
            int scale = Math.min(scaleWidth, scaleHeight);
            options.inSampleSize = Math.min(scaleWidth, scaleHeight) <= 0 ? 1 : scale;
            L.d("BitmapCompress", "缩放比例：" + options.inSampleSize);
        }
        // 回收资源
        bitmap.recycle();
        // 返回 二次采样 结果
        Bitmap b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        L.d("BitmapCompress", "新图   宽：" + b.getWidth() + " 高：" + b.getHeight() + " 大小：" + b.getByteCount());
        return b;
    }
}
