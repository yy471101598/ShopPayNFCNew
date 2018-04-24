package com.shoppay.wynew.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.WriterException;
import com.shoppay.wynew.R;
import com.shoppay.wynew.zxing.encoding.EncodingHandler;

/**
 * Created by songxiaotao on 2017/8/3.
 */

public class WeixinPayDialog {

    public static Dialog weixinPayDialog(final Context context,
                                       int showingLocation,String weixinCode,String money) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_weixincode, null);
        ImageView img= (ImageView) view.findViewById(R.id.img_code);
        TextView tv_money= (TextView) view.findViewById(R.id.tv_money);
        tv_money.setText(StringUtil.twoNum(money));
        dialog = new Dialog(context, R.style.DialogNotitle1);
        try {
          Bitmap bm_code = EncodingHandler.createQRCode2(weixinCode, 200);
            img.setImageBitmap(bm_code);
        } catch (WriterException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth-30, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();
        dialog.show();
        Window window = dialog.getWindow();
        switch (showingLocation) {
            case 0:
                window.setGravity(Gravity.TOP); // 此处可以设置dialog显示的位置
                break;
            case 1:
                window.setGravity(Gravity.CENTER);
                break;
            case 2:
                window.setGravity(Gravity.BOTTOM);
                break;
            default:
                window.setGravity(Gravity.CENTER);
                break;
        }
        return dialog;
    }
}
