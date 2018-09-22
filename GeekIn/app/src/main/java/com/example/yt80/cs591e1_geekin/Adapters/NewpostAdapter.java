package com.example.yt80.cs591e1_geekin.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.yt80.cs591e1_geekin.Fragments.NewPostFragImage;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yt80 on 2017/4/9.
 */

public class NewpostAdapter extends BaseAdapter {
    private int num = 1;
    private LayoutInflater letterInf;
    private Context context;
    private LinearLayout.LayoutParams layoutParams1;
    private ArrayList<Bitmap> images = new ArrayList<>();
    private NewPostFragImage fg1;

    /**
     * Constructor
     * @param context
     * @param fg1
     */
    public NewpostAdapter(Context context, NewPostFragImage fg1){
        this.context=context;
        this.fg1=fg1;
        letterInf = LayoutInflater.from(context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int prefered_width=(int)(0.9*dm.widthPixels/2);;
        layoutParams1=new LinearLayout.LayoutParams(prefered_width,prefered_width);
        layoutParams1.gravity= Gravity.CENTER;

    }

    /**
     * Add more data in the future.
     * @param image
     */
    public void addData(Bitmap image){
        this.images.add(image);
        this.num++;
        this.notifyDataSetChanged();
    }

    public void remove(int index){
        images.remove(index);
        num--;
        this.notifyDataSetChanged();
    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------

    @Override
    public int getCount() {
        return num;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent) {
         View item;
        if (convertView == null) {
            //inflate the button layout
            item = letterInf.inflate(R.layout.newpost_grid_image_item, parent, false);
        } else {
            item =  convertView;
        }

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int prefered_width = (int) (0.9* dm.widthPixels/2);

        ImageButton imageview=(ImageButton)item.findViewById(R.id.newpost_grid_image);
        Bitmap icon1= BitmapFactory.decodeResource(((Activity)context).getResources(),R.drawable.plus);
        Bitmap plus = ImageUtil.getScaledBitmap(icon1,prefered_width,prefered_width);

        if(position==num-1) {
            imageview.setImageBitmap(plus);
            item.setTag((Object)"add");
        }
        else {
            imageview.setImageBitmap(images.get(position));
            item.setTag((Object)"photo");
        }

        imageview.setLayoutParams(layoutParams1);
        imageview.requestLayout();
        final String tag=(String)item.getTag();
        final int pos=position;
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fg1.selectImage(tag,pos);
            }
        });

        return item;
    }


//    private void selectImage(final String tag,final int position) {
//        final CharSequence[] options;
//        if(tag.equals("add"))
//            options = new CharSequence[]{ "Take Photo", "Choose from Gallery"};
//        else
//            options = new CharSequence[]{ "Take Photo", "Choose from Gallery","Delete Photo" };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo"))
//                {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//                    File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//                    File photoFile=null;
//                    try {
//                        photoFile = File.createTempFile(
//                                "temp",  // prefix
//                                ".jpg",         // suffix
//                                storageDir      // directory
//                        );
//                        mphotoFile=photoFile;
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    if(photoFile!=null){
//                        Uri photoURI = FileProvider.getUriForFile(context,
//                                "com.example.android.fileprovider2",
//                                photoFile);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                        ((Activity)context).startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//                    }
//
//                }
//                else if (options[item].equals("Choose from Gallery"))
//                {
//                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    ((Activity)context).startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
//
//                }
//                else if (options[item].equals("Delete Photo")) {
//                    remove(position);
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }
//
//    static final int REQUEST_IMAGE_CAPTURE = 1;
//    static final int REQUEST_IMAGE_GALLERY =2;
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null)
//            ((Activity)context).startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//    }


}
