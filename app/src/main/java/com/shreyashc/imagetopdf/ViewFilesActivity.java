package com.shreyashc.imagetopdf;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ViewFilesActivity extends AppCompatActivity {
    RecyclerView filesRecyclerView;
    private LinearLayout noFiles;
    ImageButton closeInfo;
    LinearLayout infoLayout;
    Toolbar toolbar;


    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImagesToPDF/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_files);

        filesRecyclerView = findViewById(R.id.files_rv);
        noFiles = findViewById(R.id.no_files);
        toolbar = findViewById(R.id.files_toolbar);

        closeInfo = findViewById(R.id.close_info);
        infoLayout = findViewById(R.id.info_linear_layout);

        noFiles.setVisibility(View.GONE);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Files");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        filesRecyclerView.setHasFixedSize(true);
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));

        File directory = new File(path);
        File[] tempFiles = directory.listFiles();
        List<File> files = new ArrayList<File>();
        try {
            Collections.addAll(files, tempFiles);
        } catch (NullPointerException nEx) {
            nEx.printStackTrace();
        }
        if (files.size() == 0) {
            noFiles.setVisibility(View.VISIBLE);
            filesRecyclerView.setVisibility(View.GONE);
        }

        ViewFilesAdapter viewFilesAdapter = new ViewFilesAdapter(files, getApplication(),noFiles);
        filesRecyclerView.setAdapter(viewFilesAdapter);

        closeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoLayout.setVisibility(View.GONE);
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}