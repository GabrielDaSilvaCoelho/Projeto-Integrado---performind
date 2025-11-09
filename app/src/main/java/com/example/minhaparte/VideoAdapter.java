package com.example.minhaparte;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private final Context context;
    private final List<String> videoUrls;

    public VideoAdapter(Context context, List<String> videoUrls) {
        this.context = context;
        this.videoUrls = videoUrls;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        String url = videoUrls.get(position);
        Uri uri = Uri.parse(url);

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(holder.videoView);
        holder.videoView.setMediaController(mediaController);
        holder.videoView.setVideoURI(uri);
        holder.videoView.start();
    }

    @Override
    public int getItemCount() {
        return videoUrls.size();
    }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoViewItem);
        }
    }
}
