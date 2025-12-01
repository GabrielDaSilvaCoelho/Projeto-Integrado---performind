package com.example.minhaparte.minhaparte.all;

import android.content.Context;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.minhaparte.R;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(VideoModel video);
    }

    private List<VideoModel> videoList;
    private Context context;
    private OnVideoClickListener listener;

    public VideoAdapter(List<VideoModel> videoList, Context context, OnVideoClickListener listener) {
        this.videoList = videoList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoModel video = videoList.get(position);
        holder.title.setText(video.title);
        holder.desc.setText(video.description);

        Glide.with(context).load(video.thumb_url).into(holder.thumb);

        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumb;
        TextView title, desc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumb = itemView.findViewById(R.id.thumbImage);
            title = itemView.findViewById(R.id.videoTitle);
            desc = itemView.findViewById(R.id.videoDesc);
        }
    }
}
