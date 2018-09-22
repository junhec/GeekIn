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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yt80.cs591e1_geekin.Views.CircleImageView;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.Notification;

import java.util.ArrayList;

/**
 * Created by haotianwu on 4/13/17.
 */

public class NotificationAdapter extends BaseAdapter {
    private int num = 0;
    private LayoutInflater letterInf;
    private Context context;
    private ArrayList<Integer> pIDs = new ArrayList<>();
    private ArrayList<Bitmap> post_images = new ArrayList<>();
    private ArrayList<Bitmap> user_pic  =new ArrayList<>();
    private ArrayList<String> notification = new ArrayList<>();
    private LinearLayout.LayoutParams user_pic_layout;
    private LinearLayout.LayoutParams notification_layout;
    private LinearLayout.LayoutParams post_image_layout;


    /**
     * Constructor
     * @param context
     * @param notificationList
     */
    public NotificationAdapter(Context context, ArrayList<Notification> notificationList){
        this.context = context;
        for (Notification each : notificationList) {
            this.num++;
            this.pIDs.add(each.getpID());
            this.post_images.add(each.getPostCover());
            this.user_pic.add(each.getUserPic());
            if (each.getType() == "like"){
                String comment = each.getUserName() + " likes your post.";
                notification.add(comment);
            } else {
                String comment = each.getUserName() + " make Comment to your post";
                notification.add(comment);
            }
        }
        letterInf = LayoutInflater.from(context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) dm.widthPixels/8;

        user_pic_layout=new LinearLayout.LayoutParams(width,width);
        user_pic_layout.setMargins(5,5,0,5);
        user_pic_layout.gravity= Gravity.CENTER;

        notification_layout =new LinearLayout.LayoutParams(width * 6, width);
        notification_layout.gravity= Gravity.CENTER_VERTICAL;

        post_image_layout= new LinearLayout.LayoutParams(width, width);
        post_image_layout.gravity = Gravity.RIGHT;
    }

    /**
     * Add latest data from server
     * @param notificationList
     */
    public void addData(ArrayList<Notification> notificationList){
        for (Notification each: notificationList) {
            this.num++;
            this.pIDs.add(each.getpID());
            this.post_images.add(each.getPostCover());
            this.user_pic.add(each.getUserPic());
            if (each.getType() == "like"){
                String comment = each.getUserName() + " likes your post.";
                notification.add(comment);
            } else {
                String comment = each.getUserName() + " make Comment to your post";
                notification.add(comment);
            }
        }
        this.notifyDataSetChanged();
    }

    /**
     * Used for updating. Add data to the front.
     * @param post_images
     * @param user_pic
     * @param notification
     */
    public void addDataToFront(ArrayList<Bitmap> post_images, ArrayList<Bitmap> user_pic, ArrayList<String> notification) {
        int count = post_images.size();
        post_images.addAll(this.post_images);
        this.post_images = new ArrayList<>(post_images);
        user_pic.addAll(this.user_pic);
        this.user_pic = new ArrayList<>(user_pic);
        notification.addAll(this.notification);
        this.notification = new ArrayList<>(notification);
        this.num += count;
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
        return pIDs.get(position);
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
            item = letterInf.inflate(R.layout.notification_view, parent, false);
        } else {
            item =  convertView;
        }
        CircleImageView user_pic_view=(CircleImageView)item.findViewById(R.id.user_pic);
        user_pic_view.setLayoutParams(user_pic_layout);
        user_pic_view.setImageBitmap(user_pic.get(position));
        user_pic_view.requestLayout();

        TextView notification_text = (TextView)item.findViewById(R.id.notification);
        notification_text.setLayoutParams(notification_layout);
        notification_text.setText(notification.get(position));

        ImageView imageview=(ImageView)item.findViewById(R.id.post_image);
        imageview.setLayoutParams(post_image_layout);
        imageview.setImageBitmap(post_images.get(position));
        imageview.requestLayout();

        return item;
    }
}
