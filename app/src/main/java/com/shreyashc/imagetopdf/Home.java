package com.shreyashc.imagetopdf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


public class Home extends Fragment {
    private Activity activity;
    List<String> imagesUri;
    private static final int REQUEST_GET_IMAGES = 13;
    private Button create_pdf_btn;
    private FloatingActionButton select_images_btn, view_files_btn;
    String path, filename;
    TextView textView, viewFilesTextView, addFilesTextView, noOfImagesSelected;
    Image image;
    RecyclerView recyclerView;
    ImageAdapter adapter;
    ArrayList<Uri> imagesUris;
    boolean enoughPermissions = true;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        imagesUri = new ArrayList<>();
        select_images_btn = root.findViewById(R.id.select_images);
        recyclerView = root.findViewById(R.id.recyclerView);
        create_pdf_btn = root.findViewById(R.id.create_pdf);
        textView = root.findViewById(R.id.text);
        viewFilesTextView = root.findViewById(R.id.view_files_hint);
        addFilesTextView = root.findViewById(R.id.add_files_hint);
        view_files_btn = root.findViewById(R.id.view_files_btn);
        noOfImagesSelected = root.findViewById(R.id.no_images_selected);


        select_images_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddImages();
            }
        });

        create_pdf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPdf();
            }
        });


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requestPermessions();
        view_files_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path2 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImagesToPDF/";
                File directory = new File(path2);
                if (!directory.exists()) {
                    requestPermessions();
                    directory.mkdir();
                }
                if (enoughPermissions) {
                    Intent toViewFiles = new Intent(getContext(), ViewFilesActivity.class);
                    startActivity(toViewFiles);
                } else {
                    requestPermessions();
                }
            }
        });

    }


    private void toAddImages() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 1);
            } else {
                selectImages();
            }
        } else {
            selectImages();
        }
    }

    private void selectImages() {
        Config config = new Config();
        config.setToolbarTitleRes(R.string.Image_picker_Title);
        ImagePickerActivity.setConfig(config);

        Intent toPickImages = new Intent(activity, ImagePickerActivity.class);
        ArrayList<Uri> uris = new ArrayList<>(imagesUri.size());
        for (String stringUri : imagesUri) {
            uris.add(Uri.parse(stringUri));
        }
        toPickImages.putExtra(ImagePickerActivity.EXTRA_IMAGE_URIS, uris);
        startActivityForResult(toPickImages, REQUEST_GET_IMAGES);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GET_IMAGES && resultCode == Activity.RESULT_OK) {
            imagesUri.clear();

            imagesUris = data.getParcelableArrayListExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

            adapter = new ImageAdapter(imagesUris);
            recyclerView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
            recyclerView.setAdapter(adapter);


            create_pdf_btn.setVisibility(View.VISIBLE);
            view_files_btn.hide();
            select_images_btn.setImageResource(R.drawable.ic_baseline_edit_24);
            viewFilesTextView.setVisibility(View.GONE);
            addFilesTextView.setText("Add/Remove Files");


            for (int i = 0; i < imagesUris.size(); i++) {
                imagesUri.add(imagesUris.get(i).getPath());

            }
            Toast.makeText(activity, "Images Added", Toast.LENGTH_SHORT).show();
            noOfImagesSelected.setText(imagesUris.size() + " images selected ");
            noOfImagesSelected.setVisibility(View.VISIBLE);

        }
    }

    private void requestPermessions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, 1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enoughPermissions = true;
                Toast.makeText(activity, "permissions given", Toast.LENGTH_SHORT).show();
            } else {
                enoughPermissions = false;
                Toast.makeText(activity, "insufficient permissions", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void createPdf() {
        if (imagesUri.size() == 0) {
            Toast.makeText(activity, "No Images Selexted", Toast.LENGTH_SHORT).show();
        } else {
            new MaterialDialog.Builder(activity)
                    .title("Creating PDF")
                    .content("Enter FIle Name")

                    .input("filename", null, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            if (input == null || input.toString().trim().isEmpty()) {
                                Toast.makeText(activity, "Enter Name", Toast.LENGTH_SHORT).show();
                            } else {
                                filename = input.toString();

                                new CreatePDFInBackground().execute();

                            }
                        }
                    }).show();
        }
    }

    public  class CreatePDFInBackground extends AsyncTask<String, String, String> {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(activity)
                .title("Please Wait")
                .content("Converting")
                .cancelable(false)
                .progress(true, 0);
        MaterialDialog dialog = builder.build();


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            File folder = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImagesToPDF/"
            );


            if (!folder.exists()) {
               folder.mkdir();
            }

            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/ImagesToPDF/";

            File file = new File(path);
            path = path + filename + ".pdf";
            Document document = new Document(PageSize.A4, 38, 38, 50, 38);

            Rectangle documentRect = document.getPageSize();

            try {
                PdfWriter writer = PdfWriter.getInstance(
                        document,
                        new FileOutputStream(path)
                );

                document.open();
                for (int i = 0; i < imagesUri.size(); i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagesUri.get(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);


                    image = Image.getInstance(imagesUri.get(i));

                    if (bitmap.getWidth() > documentRect.getWidth() || bitmap.getHeight() > documentRect.getHeight()) {

                        image.scaleToFit(documentRect.getWidth(), documentRect.getHeight());
                    } else {

                        image.scaleToFit(bitmap.getWidth(), bitmap.getHeight());

                    }
                    image.setAbsolutePosition(
                            (documentRect.getWidth() - image.getScaledWidth()) / 2,
                            (documentRect.getHeight() - image.getScaledHeight()) / 2);

                    image.setBorder(Image.BOX);

                    image.setBorderWidth(15);

                    document.add(image);

                    document.newPage();

                }

                document.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            document.close();
            imagesUri.clear();


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            dialog.dismiss();
            Snackbar.make(getActivity().findViewById(android.R.id.content), "success", 3000)
                    .setAction("View", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent viewFilesIntent = new Intent(getContext(), ViewFilesActivity.class);
                            startActivity(viewFilesIntent);


                        }
                    }).show();

            imagesUris.clear();
            adapter.notifyDataSetChanged();
            create_pdf_btn.setVisibility(View.INVISIBLE);

            view_files_btn.show();
            select_images_btn.setImageResource(R.drawable.ic_baseline_add_24);
            viewFilesTextView.setVisibility(View.VISIBLE);
            noOfImagesSelected.setVisibility(View.GONE);
            addFilesTextView.setText("New");

        }
    }


}