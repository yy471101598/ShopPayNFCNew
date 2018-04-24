package com.shoppay.wynew.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by songxiaotao on 2017/6/30.
 */
public class MyGridViews extends GridView {

    public MyGridViews(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGridViews(Context context) {
        super(context);
    }

    public MyGridViews(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(
                Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}