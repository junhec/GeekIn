package com.example.yt80.cs591e1_geekin.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Common.Anim;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Adapters.NewpostAdapter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by yt80 on 2017/4/10.
 */

public class NewPostFragImage extends Fragment{
    private Button cancel;
    private GridView gridview;
    private NewpostAdapter npAdapter;
    private LinearLayout.LayoutParams layoutParams;
    private File photoDir;
    private SimpleDateFormat dateformat;
    private Anim anim = new Anim();
    private String TAG = "FUCK";
    public interface main_fragment_newpost_1_channel{
        public void sendPath(String path);
    }
    main_fragment_newpost_1_channel BFL;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        BFL=(main_fragment_newpost_1_channel) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newpost_fragment_image, null);


        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        layoutParams=new LinearLayout.LayoutParams(dm.widthPixels/2,dm.widthPixels/2);

        gridview=(GridView)view.findViewById(R.id.main_newpost_gridview);
        npAdapter=new NewpostAdapter(getContext(),this);

        gridview.setAdapter(npAdapter);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectImage((String)view.getTag(),position);
            }
        });

        dateformat=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_S");
        photoDir=getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/"+dateformat.format(new Date()));//bug can be called multiple times?
        BFL.sendPath(photoDir.getAbsolutePath());
        return view;

    }

    private File mphotoFile;
    private File mphotoFile1;
    private File mphotoGalleryTemp;
    public void selectImage(final String tag,final int position) {
        final CharSequence[] options;
        if(tag.equals("add"))
            options = new CharSequence[]{ "Take Photo", "Choose from Gallery"};
        else
            options = new CharSequence[]{ "Take Photo", "Choose from Gallery","Delete Photo" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES+"/a");
                   // File storageDir = getContext().getExternalFilesDir("my");
                    File photoFile=null;
                    try {
                        photoFile = File.createTempFile(
                                dateformat.format(new Date()),  // prefix
                                ".jpg",         // suffix
                                photoDir      // directory
                        );
                        mphotoFile=photoFile;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if(photoFile!=null){
                        Uri photoURI = FileProvider.getUriForFile(getContext(),
                                "com.example.android.fileprovider2",
                                photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }

                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQUEST_IMAGE_GALLERY);

                }
                else if (options[item].equals("Delete Photo")) {
                    npAdapter.remove(position);
                    //File file=new File(photoDir);
                    File file_to_delete=photoDir.listFiles()[position];
                    file_to_delete.delete();
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_IMAGE_GALLERY =2;
    static final int REQUEST_IMAGE_CROP_CAPTURE = 3;
    static final int REQUEST_IMAGE_CROP_GALLERY =4;
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Uri uri = Uri.fromFile(mphotoFile);
            cropPhoto(uri,REQUEST_IMAGE_CROP_CAPTURE);
        } else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                mphotoGalleryTemp=new File(photoDir.getAbsolutePath()+"/"+dateformat.format(new Date())+".jpg");
                OutputStream os = new BufferedOutputStream(new FileOutputStream(mphotoGalleryTemp));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();

                cropPhoto(Uri.fromFile(mphotoGalleryTemp),REQUEST_IMAGE_CROP_GALLERY);
            }catch (Exception e){
                e.printStackTrace();
            }
            //cropPhoto(filePath,REQUEST_IMAGE_CROP_GALLERY);
        } else if (requestCode == REQUEST_IMAGE_CROP_CAPTURE && data != null){
            try{
                Bitmap bitmap = BitmapFactory.decodeFile(mphotoFile1.getAbsolutePath());
                mphotoFile.delete();

                DisplayMetrics dm = new DisplayMetrics();
                ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

                Bitmap bitmap1= ImageUtil.getScaledBitmap(bitmap,(int)(layoutParams.width*0.9),(int)(layoutParams.width*0.9)*bitmap.getHeight()/bitmap.getWidth());
                bitmap1 = ImageUtil.getSquareBitmap(bitmap1);

                npAdapter.addData(bitmap1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else if(requestCode == REQUEST_IMAGE_CROP_GALLERY && data != null){
            try{

                Bitmap bitmap = BitmapFactory.decodeFile(mphotoFile1.getAbsolutePath());
                mphotoGalleryTemp.delete();
                DisplayMetrics dm = new DisplayMetrics();
                ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);

                Bitmap bitmap1= ImageUtil.getScaledBitmap(bitmap,(int)(layoutParams.width*0.9),(int)(layoutParams.width*0.9)*bitmap.getHeight()/bitmap.getWidth());
                //Toast.makeText(getContext(),"reach here",Toast.LENGTH_SHORT).show();
                bitmap1 = ImageUtil.getSquareBitmap(bitmap1);

                npAdapter.addData(bitmap1);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void cropPhoto(Uri uri,int REQUEST_CODE){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);
        //intent.putExtra("outputX",800);
        //intent.putExtra("outputY",800);
        intent.putExtra("scale",true);
        intent.putExtra("return-data",true);
        File photoFile=null;
        try {
            photoFile = File.createTempFile(
                    dateformat.format(new Date()),  // prefix
                    ".jpg",         // suffix
                    photoDir      // directory
            );
            mphotoFile1=photoFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(photoFile!=null){
            Uri photoURI = FileProvider.getUriForFile(getContext(),
                    "com.example.android.fileprovider2",
                    photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            List<ResolveInfo> resInfoList = getContext().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                getContext().grantUriPermission(packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    public String getPath(Uri uri) {
        String res = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContext().getContentResolver().query(uri, proj, null, null, null);
        if(cursor.moveToFirst()){;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }
}
