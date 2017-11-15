package com.example.zemoso.downloadmanager.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.zemoso.downloadmanager.Interface.DownloadManager;
import com.example.zemoso.downloadmanager.pojo.GalleryData;
import com.example.zemoso.downloadmanager.R;
import com.example.zemoso.downloadmanager.list_helper.BurstViewHolder;

import java.util.List;

/**
 * Created by zemoso on 14/11/17.
 */

public class GalleryAdapter extends RecyclerView.Adapter<BurstViewHolder>{

    private static final String TAG = GalleryAdapter.class.getSimpleName();
    private Context mContext;
    private List<GalleryData> galleryDataList;
    private DownloadManager downloadManager;
    private int downloadingID = -1;
    private BurstViewHolder downloadingHolder;

    //region Public Constructor
    public GalleryAdapter(Context context, List<GalleryData> galleryDataList, DownloadManager downloadManager) {
        this.mContext = context;
        this.galleryDataList = galleryDataList;
        this.downloadManager = downloadManager;
    }
    //endregion

    //region Overridden Methods
    @Override
    public BurstViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.gallery_list,parent,false);
        return new BurstViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BurstViewHolder holder, final int position) {
        final int adapterPosition = holder.getAdapterPosition();
        if(downloadingID == adapterPosition && downloadManager.isDownloadInProgress()){
            holder.getProgressBar().setVisibility(View.VISIBLE);
        }
        else{
            holder.getProgressBar().setVisibility(View.GONE);
        }

        holder.getTextView().setBackgroundColor(
                galleryDataList.get(adapterPosition).isDownloaded() ?
                ContextCompat.getColor(mContext,R.color.success) :
                ContextCompat.getColor(mContext,R.color.card_background));

        holder.getTextView().setText(mContext.getResources().getString(R.string.Busrt_Id,galleryDataList.get(adapterPosition).getId()));
        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("clicked","positon "+holder.getAdapterPosition());
                if(downloadManager.isDownloadInProgress()){
                    Toast.makeText(mContext, "Previous download in progress", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(galleryDataList.get(adapterPosition).isDownloaded()){
                    Toast.makeText(mContext, "This burst is already downloaded in this session. " +
                            "Restart the app to download it again", Toast.LENGTH_SHORT).show();
                    return;
                }
                downloadingID = adapterPosition;
                downloadingHolder = holder;
                downloadManager.setData(galleryDataList.get(adapterPosition));
                holder.getProgressBar().setIndeterminate(true);
                holder.getProgressBar().setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return galleryDataList.size();
    }
    //endregion

    //region Public Methods
    public void downloadComplete(boolean success) {
        downloadingHolder.getProgressBar().setVisibility(View.GONE);
        downloadingHolder.getTextView().setBackgroundColor(
                success ?
                ContextCompat.getColor(mContext,R.color.success) :
                ContextCompat.getColor(mContext,R.color.failed));
        galleryDataList.get(downloadingID).setDownloaded(success);
    }
    //endregion
}
