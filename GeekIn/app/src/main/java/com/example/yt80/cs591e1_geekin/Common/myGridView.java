package com.example.yt80.cs591e1_geekin.Common;

/**
 * Customized gridview. To solve gridview displays wierd in the scroll view.
 */


import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class myGridView extends GridView {

    public myGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public myGridView(Context context) {
        super(context);
    }

    public myGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
