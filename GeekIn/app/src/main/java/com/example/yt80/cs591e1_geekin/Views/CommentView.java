package com.example.yt80.cs591e1_geekin.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Views.CircleImageView;

/**
 * Created by haotianwu on 4/25/17.
 */

public class CommentView extends LinearLayout{
    private Context context;
    private Bitmap user_pic;
    private String user_name;
    private String comments;
    private String time;

    public CommentView (Context context) {
        super(context);
    }

    public CommentView (Context context, Bitmap userPic, String userName, String comment, String time) {
        super(context);
        this.user_pic = userPic;
        this.user_name = userName;
        this.comments = comment;
        this.time = time;

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.comment_view,this);

        CircleImageView user_pic = (CircleImageView) findViewById(R.id.user_pic);
        TextView user_name = (TextView) findViewById(R.id.user_name);
        TextView comments = (TextView) findViewById(R.id.comment);
        TextView timeView = (TextView) findViewById(R.id.time);

        user_pic.setImageBitmap(userPic);
        user_name.setText(userName);
        comments.setText(comment);
        timeView.setText(time);
    }
}
