package com.example.zemoso.downloadmanager.list_helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zemoso.downloadmanager.R;

/**
 * Created by zemoso on 15/11/17.
 */

public class BurstViewHolder extends RecyclerView.ViewHolder{

    private TextView mTextView;
    private ProgressBar mProgressBar;

    public BurstViewHolder(View itemView) {
        super(itemView);
        mTextView = itemView.findViewById(R.id.burst_text);
        mProgressBar = itemView.findViewById(R.id.progressbar);
    }

   public TextView getTextView(){
        return mTextView;
    }
    public ProgressBar getProgressBar(){
        return mProgressBar;
    }
}
