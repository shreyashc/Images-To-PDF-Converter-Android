package com.shreyashc.imagetopdf;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private ArrayList<Uri> uri;

    public ImageAdapter(ArrayList<Uri> uri) {
        this.uri = uri;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.recyclerImageView.setImageURI(uri.get(position));
    }

    @Override
    public int getItemCount() {
        return uri.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView recyclerImageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImageView = itemView.findViewById(R.id.image);
        }
    }
}
