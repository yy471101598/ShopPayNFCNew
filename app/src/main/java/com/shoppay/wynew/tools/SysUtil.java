package com.shoppay.wynew.tools;

/**
 * Created by songxiaotao on 2017/7/7.
 */

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 08. * 该应用程序的公共方法的集合类
 * 09. * 作者：vaecer on 2015/7/19 12:03
 * 10. * 邮箱：wuxm1011@163.com.
 * 11.
 */
public class SysUtil {

    public void SysUtil() {

    }

    public static void setImageBackground(Bitmap bitmap, ImageView imageView, int width,
                                          int height) {
        //计算最佳缩放倍数,以填充宽高为目标
        Log.d("xxx",width+";"+height+";"+bitmap.getWidth()+";"+bitmap.getHeight());
        float scaleX = (float) width / bitmap.getWidth();
//        float scaleY = (float) height / bitmap.getHeight();
//        float bestScale = scaleX > scaleY ? scaleX : scaleY;
        float bestScale = scaleX;
        int imgheight=(int)(bitmap.getHeight()*bestScale);

        //动态设置img高度
        ViewGroup.LayoutParams para;
        para = imageView.getLayoutParams();

        para.height = imgheight;
        para.width = width;
        imageView.setLayoutParams(para);
        //以填充高度的前提下，计算最佳缩放倍数
        float subX = (width - bitmap.getWidth() * bestScale) / 2;
        float subY = (height - bitmap.getHeight() * bestScale) / 2;

        Matrix imgMatrix = new Matrix();
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        //缩放最佳大小
        imgMatrix.postScale(bestScale, bestScale);
        //移动到居中位置显示
//        imgMatrix.postTranslate(subX, subY);
        //设置矩阵
        imageView.setImageMatrix(imgMatrix);
        imageView.setImageBitmap(bitmap);

    }
}
