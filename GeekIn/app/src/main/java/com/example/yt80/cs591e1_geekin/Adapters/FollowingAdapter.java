package com.example.yt80.cs591e1_geekin.Adapters;

/**
 * Customized adapter for Following user view.
 */

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

import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Views.CircleImageView;

import java.util.ArrayList;


public class FollowingAdapter extends BaseAdapter {
    private int num = 0;
    private LayoutInflater letterInf;
    private Context context;
    private ArrayList<String> userEmail = new ArrayList<>();
    private ArrayList<Bitmap> userPic = new ArrayList<>();
    private ArrayList<String> userName = new ArrayList<>();
    private LinearLayout.LayoutParams userPicLayout;
    private LinearLayout.LayoutParams userNameLayout;

    /**
     * Constructor
     * @param context
     * @param userList
     */
    public FollowingAdapter(Context context, ArrayList<User> userList){
        this.context = context;
        for (User user : userList) {
            this.num++;
            this.userEmail.add(user.getUserEmail());
            this.userPic.add(ImageUtil.StringToBitMap(user.getUserPic()));
            this.userName.add(user.getUserName());
        }
        letterInf = LayoutInflater.from(context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = (int) dm.widthPixels/6;
        userPicLayout=new LinearLayout.LayoutParams(width,width);
        userPicLayout.gravity= Gravity.CENTER;

        userNameLayout =new LinearLayout.LayoutParams(width * 4, width);
        userNameLayout.gravity= Gravity.LEFT;
    }

    /**
     * Add latest data from server
     * @param userList
     */
    public void addData(ArrayList<User> userList){
        for (User user: userList) {
            this.num++;
            this.userEmail.add(user.getUserEmail());
            this.userPic.add(ImageUtil.StringToBitMap(user.getUserPic()));
            this.userName.add(user.getUserName());
        }
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
        return userEmail.get(position);
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
        user_pic_view.setLayoutParams(userPicLayout);
        user_pic_view.setImageBitmap(userPic.get(position));
        user_pic_view.requestLayout();

        TextView userNameTV = (TextView)item.findViewById(R.id.notification);
        userNameTV.setLayoutParams(userNameLayout);
        userNameTV.setText(userName.get(position));


        return item;
    }
}
