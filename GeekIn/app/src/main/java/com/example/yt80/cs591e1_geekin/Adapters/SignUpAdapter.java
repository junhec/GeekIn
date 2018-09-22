package com.example.yt80.cs591e1_geekin.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.yt80.cs591e1_geekin.R;

import java.util.ArrayList;

/**
 * Customized adapter for (check box, tag). Easy to add more in the future
 */

public class SignUpAdapter extends BaseAdapter{
    Context context;
    private LayoutInflater letterInf;
    private ArrayList<Integer> images=new ArrayList<>();
    private String captions[];
    private DisplayMetrics dm;
    private LinearLayout.LayoutParams layoutParams;
    private TextView txtView;
    private int check_num=0;
    private ArrayList<String> chosenTag;
    DisplayMetrics displaymetrics = new DisplayMetrics();

    /**
     * Constructor
     * @param context
     * @param txtView
     */
    public SignUpAdapter(Context context, TextView txtView){
        this.context = context;
        this.txtView = txtView;
        this.chosenTag = new ArrayList<>();
        letterInf = LayoutInflater.from(context);
        captions =context.getResources().getStringArray(R.array.interests);
        images.add(R.drawable.game_icon);
        images.add(R.drawable.electronic_icon);
        images.add(R.drawable.tools_icon);
        images.add(R.drawable.office_icon);

        dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = ((Activity)context).getResources().getDisplayMetrics().density;
        int imageSize = (int) (140 * density);

        layoutParams=new LinearLayout.LayoutParams(imageSize,imageSize);
        layoutParams.gravity= Gravity.CENTER;

    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------
    @Override
    public int getCount() {
        return 4;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View item;
        if (convertView == null) {
            //inflate the button layout
            item = letterInf.inflate(R.layout.signup_gridview_item, parent, false);
        } else {
            item =  convertView;
        }
        ImageView imageview=(ImageView)item.findViewById(R.id.signup_gridview_imageview);
        imageview.setLayoutParams(layoutParams);
        imageview.setImageResource(images.get(position));
        imageview.requestLayout();

        CheckBox checkbox=(CheckBox)item.findViewById(R.id.signup_gridview_checkbox);
        checkbox.setText(captions[position]);
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_num++;
                    chosenTag.add(captions[position]);
                } else {
                    check_num--;
                    int index = chosenTag.indexOf(captions[position]);
                    chosenTag.remove(index);
                }
                txtView.setText(String.valueOf(check_num)+"/4");
            }
        });

        return item;
    }

    public ArrayList<String> getChosenTag() {
        return chosenTag;
    }
}
