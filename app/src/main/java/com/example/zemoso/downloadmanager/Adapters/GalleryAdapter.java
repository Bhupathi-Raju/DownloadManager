package com.example.zemoso.downloadmanager.Adapters;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zemoso.downloadmanager.Interface.DownloadUrl;
import com.example.zemoso.downloadmanager.POJO.GalleryData;
import com.example.zemoso.downloadmanager.R;

import java.util.List;

/**
 * Created by zemoso on 14/11/17.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private List<GalleryData> galleryDataList;
    private DownloadUrl downloadUrl;


    public GalleryAdapter(List<GalleryData> galleryDataList, DownloadUrl downloadUrl)
    {
       this.galleryDataList = galleryDataList;
       this.downloadUrl = downloadUrl;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.getmTextView().setText(galleryDataList.get(position).getId());
        holder.getmTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              downloadUrl.setData(galleryDataList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.burst_text);
        }

        public TextView getmTextView(){
            return mTextView;
        }
    }

}
