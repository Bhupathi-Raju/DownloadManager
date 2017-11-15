package com.example.zemoso.downloadmanager.list_helper;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.zemoso.downloadmanager.R;

/**
 * Created by zemoso on 15/11/17.
 */

public class BurstItemSpacing extends RecyclerView.ItemDecoration {

    private final int VERTICAL_SPACING;
    private final int HORIZONTAL_SPACING;

    public BurstItemSpacing(Context context) {
        this.VERTICAL_SPACING = Math.round(context.getResources().getDimension(R.dimen.view_spacing));
        this.HORIZONTAL_SPACING = Math.round(context.getResources().getDimension(R.dimen.standard_padding));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        Log.d("TAG", "position "+position);
        if(position == 0){
            outRect.top = VERTICAL_SPACING;
        }
        outRect.bottom = VERTICAL_SPACING;
        outRect.left = outRect.right = HORIZONTAL_SPACING;
    }
}
