package com.example.yt80.cs591e1_geekin.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yt80.cs591e1_geekin.Views.CircleImageView;
import com.example.yt80.cs591e1_geekin.R;

import java.util.ArrayList;

/**
 * Created by haotianwu on 4/13/17.
 */

public class CommentAdapter extends BaseAdapter {
    private int num = 0;
    private LayoutInflater letterInf;
    private Context context;
    private ArrayList<Bitmap> user_pic = new ArrayList<>();
    private ArrayList<String> comment  = new ArrayList<>();
    private LinearLayout.LayoutParams user_pic_layout;
    private LinearLayout.LayoutParams comment_layout;

    public void addData( ArrayList<Bitmap> user_pic, ArrayList<String> comment){
        this.user_pic.addAll(user_pic);
        this.comment.addAll(comment);
        this.num += user_pic.size();
        this.notifyDataSetChanged();
    }

    /*
    Used for updating. Add data to the front.
     */

    public void addDataToFront(ArrayList<Bitmap> user_pic, ArrayList<String> comment) {
        int count = user_pic.size();
        user_pic.addAll(this.user_pic);
        this.user_pic = new ArrayList<>(user_pic);
        comment.addAll(this.comment);
        this.comment = new ArrayList<>(comment);
        this.num += count;
        this.notifyDataSetChanged();
    }

    public CommentAdapter(Context context, int num, ArrayList<Bitmap> user_pic, ArrayList<String> comment){
        this.num = num;
        this.context = context;
        this.user_pic = user_pic;
        this.comment = comment;
        letterInf = LayoutInflater.from(context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) dm.widthPixels/8;
        user_pic_layout=new LinearLayout.LayoutParams(width,width);
        user_pic_layout.gravity= Gravity.CENTER;

        comment_layout = new LinearLayout.LayoutParams(width * 8, width);
        comment_layout.gravity= Gravity.CENTER;

//        post_image_layout.setMargins(notification_layout.width/3, notification_layout.width/3,0,0);
    }


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
    public View getView(int position, View convertView, ViewGroup parent) {
        View item;
        if (convertView == null) {
            //inflate the button layout
            item = letterInf.inflate(R.layout.comment_view, parent, false);
        } else {
            item =  convertView;
        }
        CircleImageView user_pic_view=(CircleImageView)item.findViewById(R.id.user_pic);
        user_pic_view.setLayoutParams(user_pic_layout);
        user_pic_view.setImageBitmap(user_pic.get(position));
        user_pic_view.requestLayout();

        TextView comment_text = (TextView)item.findViewById(R.id.comment);
        comment_text.setLayoutParams(comment_layout);
        comment_text.setText(comment.get(position));
        comment_text.requestLayout();

//        ImageView imageview=(ImageView)item.findViewById(R.id.post_image);
//        imageview.setLayoutParams(post_image_layout);
//        imageview.setImageBitmap(post_images.get(position));
//        imageview.requestLayout();

        return item;
    }
}
