package com.shoppay.wynew.tools;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.shoppay.wynew.R;
import com.shoppay.wynew.adapter.ShoperChoseAdapter;
import com.shoppay.wynew.bean.Shoper;
import com.shoppay.wynew.http.InterfaceBack;

import java.util.List;

import static com.shoppay.wynew.tools.DialogUtil.dip2px;

/**
 * Created by songxiaotao on 2018/5/30.
 */

public class ShoperChoseDialog {
    /**
     * 时间选择
     *
     * @param context
     * @param showingLocation 0：顶部 1：中间 2：底部 3：距离底部100dp 对话框的位置
     */
    public static void shoperChoseDialog(final Context context, final List<Shoper> list,
                                         int showingLocation, final InterfaceBack handler) {
        final Dialog dialog;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_timechose, null);
        ListView listview = (ListView) view.findViewById(R.id.listview_timechose);
        ShoperChoseAdapter carListAdapter = new ShoperChoseAdapter(context, list);
        listview.setAdapter(carListAdapter);
        dialog = new Dialog(context, R.style.DialogNotitle1);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        int screenWidth = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
        dialog.setContentView(view, new LinearLayout.LayoutParams(
                screenWidth-100, LinearLayout.LayoutParams.WRAP_CONTENT));
        dialog.show();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handler.onResponse(list.get(position));
                dialog.dismiss();
            }
        });
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
            case 3:
                WindowManager.LayoutParams params = window.getAttributes();
                dialog.onWindowAttributesChanged(params);
                params.x = screenWidth-dip2px(context,100);// 设置x坐标
                params.gravity = Gravity.TOP;
                params.y = dip2px(context, 45);// 设置y坐标
                Log.d("xx", params.y + "");
                window.setGravity(Gravity.TOP);
                window.setAttributes(params);
                break;
            default:
                window.setGravity(Gravity.CENTER);
                break;
        }
    }
}
