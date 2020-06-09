package com.shreyashc.imagetopdf;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ViewFilesAdapter extends RecyclerView.Adapter<ViewFilesAdapter.FilesViewHolder> {
    private Context mContext;
    private List<File> allFiles;

    public ViewFilesAdapter(List<File> allFiles, Context context) {
        this.allFiles = allFiles;
        mContext = context;

    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new FilesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        holder.title.setText(allFiles.get(position).getName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent target = new Intent(Intent.ACTION_VIEW);
                target.setDataAndType(Uri.fromFile(allFiles.get(position)), "application/pdf");
                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                target.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent intent = Intent.createChooser(target, "Open File");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    mContext.getApplicationContext().startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "Not app to view pdf files", Toast.LENGTH_SHORT).show();
                }
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImagesToPDF/";
                File file = new File(path, allFiles.get(position).getName());
                Log.d(TAG, "onClick: " + file);
                Uri uri = FileProvider.getUriForFile(mContext, "com.shreyashc.imagetopdf.fileprovider", file);
                mContext.grantUriPermission("com.shreyashc.imagetopdf.fileprovider", uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mContext.grantUriPermission("com.shreyashc.imagetopdf.fileprovider", uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setType(mContext.getContentResolver().getType(uri));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Intent chooserIntent = Intent.createChooser(intent, "Share");
                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.getApplicationContext().startActivity(chooserIntent);



            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fdelete = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImagesToPDF/" + allFiles.get(position).getName());
                if (fdelete.exists()) {
                    if (fdelete.delete()) {
                        Toast.makeText(mContext, "deleted", Toast.LENGTH_LONG).show();
                        allFiles.remove(position);

                        notifyDataSetChanged();
                        if (allFiles.size() == 0) {
                            ViewFilesActivity.noFiles.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allFiles.size();
    }

    public class FilesViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private ImageButton share, delete;

        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.file_name);
            share = itemView.findViewById(R.id.share_btn);
            delete = itemView.findViewById(R.id.delete_btn);
        }


    }


}
