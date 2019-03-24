package com.example.zappalex.popularmovies.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.zappalex.popularmovies.R
import com.example.zappalex.popularmovies.models.Video

import java.util.ArrayList

class VideoAdapter(private val mVideoOnClickHandler: VideoAdapterOnClickHandler) : RecyclerView.Adapter<VideoAdapter.VideoAdapterViewHolder>() {
    private var videoList: ArrayList<Video>? = null

    interface VideoAdapterOnClickHandler {
        fun onCLick(position: Int)
    }

    inner class VideoAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val videoTitleTextView: TextView
        init {
            videoTitleTextView = view.findViewById(R.id.videoTitleTextView) as TextView
            view.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            mVideoOnClickHandler.onCLick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoAdapterViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.video_list_item

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layoutIdForListItem, parent, false)

        return VideoAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoAdapterViewHolder, position: Int) {
        videoList?.let {
            val (_, _, name) = it[position]
            holder.videoTitleTextView.text = name
        }
    }

    override fun getItemCount(): Int {
        return videoList?.size ?: 0
    }

    fun setVideoList(videoList: ArrayList<Video>) {
        this.videoList = videoList
        notifyDataSetChanged()
    }
}
