package com.example.zappalex.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zappalex.popularmovies.R;
import com.example.zappalex.popularmovies.models.Video;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder>{

    private ArrayList<Video> mVideoList;
    private final VideoAdapterOnClickHandler mVideoOnClickHandler;

    public interface VideoAdapterOnClickHandler{
        void onCLick(int position);
    }

    public VideoAdapter(VideoAdapterOnClickHandler videoOnClickHandler){
        mVideoOnClickHandler = videoOnClickHandler;
    }

    public class VideoAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final TextView mVideoTitleTextView;

        public VideoAdapterViewHolder(View view){
            super(view);

            mVideoTitleTextView = (TextView)view.findViewById(R.id.tv_video_title);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mVideoOnClickHandler.onCLick(getAdapterPosition());
        }
    }

    @Override
    public VideoAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.video_list_item;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);

        return new VideoAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoAdapterViewHolder holder, int position) {
        Video currentVideo = mVideoList.get(position);
        holder.mVideoTitleTextView.setText(currentVideo.getName());
    }

    @Override
    public int getItemCount() {
        if(mVideoList == null){
            return 0;
        }else{
            return mVideoList.size();
        }
    }

    public void setVideoList(ArrayList<Video> videoList){
        mVideoList = videoList;
        notifyDataSetChanged();
    }
}
