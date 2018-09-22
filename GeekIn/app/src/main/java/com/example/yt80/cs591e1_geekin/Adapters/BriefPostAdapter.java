package com.example.yt80.cs591e1_geekin.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Views.CircleImageView;
import com.example.yt80.cs591e1_geekin.R;

import java.util.ArrayList;

/**
 * Customized adapter for BriefPost object.
 */

public class BriefPostAdapter extends BaseAdapter {

    private Context context;

    // Variables in each brief post grid view.
    private int num = 0;
    private ArrayList<Integer> pIDs = new ArrayList<>();
    private ArrayList<String> userNames = new ArrayList<>();
    private ArrayList<Bitmap> userPics = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<Bitmap> postCovers = new ArrayList<>();

    // Layouts variables
    private LayoutInflater letterInf;
    private LinearLayout.LayoutParams layoutParams1;
    private LinearLayout.LayoutParams layoutParams2;
    private LinearLayout.LayoutParams layoutParams3;

    // Constructor
    public BriefPostAdapter(Context context) {
        this.context = context;
    }

    /**
     * Constructor
     * @param context
     * @param posts
     */
    public BriefPostAdapter(Context context, ArrayList<BriefPost> posts){
        this.context=context;
        for (BriefPost post : posts) {
            this.num++;
            this.pIDs.add(post.getpID());
            this.postCovers.add(post.getPostCover());
            this.userPics.add(post.getUserPic());
            this.titles.add(post.getTitle());
            this.userNames.add(post.getUserName());
        }

        letterInf = LayoutInflater.from(context);

        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int prefered_width=(int)(0.9*dm.widthPixels/2);;
        layoutParams1=new LinearLayout.LayoutParams(prefered_width,prefered_width);
        layoutParams1.gravity= Gravity.CENTER;

        layoutParams2=new LinearLayout.LayoutParams(prefered_width/6,prefered_width/6);
        layoutParams2.setMargins(prefered_width/12,prefered_width/100,0,0);
        layoutParams2.gravity= Gravity.CENTER;

        layoutParams3= new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams3.setMargins(layoutParams2.width/6,layoutParams2.width/6,0,0);

    }

    /**
     * Add latest data from server
     * @param posts
     */
    public void addData(ArrayList<BriefPost> posts){
        for (BriefPost post : posts) {
            this.num++;
            this.pIDs.add(post.getpID());
            this.userNames.add(post.getUserName());
            this.userPics.add(post.getUserPic());
            this.titles.add(post.getTitle());
            this.postCovers.add(post.getPostCover());
        }
        this.notifyDataSetChanged();
    }

    /**
     * Used for updating. Add data to the front.
     * @param pIDs
     * @param images
     * @param icons
     * @param descriptions
     * @param names
     */
    public void addDataToFront(ArrayList<Integer> pIDs,ArrayList<Bitmap> images, ArrayList<Bitmap> icons, ArrayList<String> descriptions, ArrayList<String> names) {
        int count = images.size();
        pIDs.addAll(this.pIDs);
        this.pIDs = new ArrayList<>(pIDs);
        images.addAll(this.postCovers);
        this.postCovers = new ArrayList<>(images);
        icons.addAll(this.userPics);
        this.userPics = new ArrayList<>(icons);
        descriptions.addAll(this.titles);
        this.titles = new ArrayList<>(descriptions);
        names.addAll(this.userNames);
        this.userNames = new ArrayList<>(names);
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
            item = letterInf.inflate(R.layout.main_grid1_item, parent, false);
        } else {
            item =  convertView;
        }
        CircleImageView cimageview=(CircleImageView)item.findViewById(R.id.main_grid1_cimageview);
        cimageview.setLayoutParams(layoutParams2);
        cimageview.setImageBitmap(userPics.get(position));
        cimageview.requestLayout();

        TextView tv_name=(TextView)item.findViewById(R.id.main_grid1_txt_name);
        tv_name.setLayoutParams(layoutParams3);
        tv_name.setText(userNames.get(position));

        final ImageView imageview=(ImageView)item.findViewById(R.id.main_grid1_imageview);
        imageview.setLayoutParams(layoutParams1);
        imageview.setImageBitmap(postCovers.get(position));
        imageview.requestLayout();


        ((TextView)item.findViewById(R.id.main_grid1_txt_description)).setText(titles.get(position));

        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        animation.setDuration(500 * (position % 6));
        item.startAnimation(animation);


        return item;
    }

    public ArrayList<Integer> getpIDs() {
        return pIDs;
    }

}
