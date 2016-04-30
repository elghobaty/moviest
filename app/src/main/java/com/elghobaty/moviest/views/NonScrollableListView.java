package com.elghobaty.moviest.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

public class NonScrollableListView extends ListView {

    private int mCount = 0;

    public NonScrollableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int itemsCount = getCount();

        if (itemsCount != mCount && itemsCount > 0) {
            int height = getChildAt(0).getHeight();
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = (itemsCount * height);
            setLayoutParams(layoutParams);
            mCount= itemsCount;
        }

        super.onDraw(canvas);
    }
}
