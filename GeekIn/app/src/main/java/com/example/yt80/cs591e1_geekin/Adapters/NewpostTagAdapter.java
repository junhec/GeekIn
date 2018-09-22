package com.example.yt80.cs591e1_geekin.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.example.yt80.cs591e1_geekin.R;

import java.util.ArrayList;

/**
 * Customized adapter for tag view. Easy to add more tags in the future.
 */

public class NewpostTagAdapter extends BaseAdapter {
    private int tag_num;
    private ArrayList<Integer> chosenTag = new ArrayList<>();
    private String[] tags;
    private Context context;
    public NewpostTagAdapter(Context context){
        this.context=context;
        tags=context.getResources().getStringArray(R.array.interests);
    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------

    @Override
    public int getCount() {
        return tags.length;
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
        View item=new LinearLayout(context);
        if(convertView==null){
            CheckBox cb=new CheckBox(context);
            cb.setText(tags[position]);
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        tag_num++;
                        chosenTag.add(position + 1);
                    } else {
                        tag_num--;
                        int index = chosenTag.indexOf(position + 1);
                        chosenTag.remove(index);
                    }
                }
            });
            ((LinearLayout) item).addView(cb);
        }
        else{
            item=convertView;
        }
        return item;
    }

    public ArrayList<Integer> getChosenTag() {
        return chosenTag;
    }

    public boolean tagIsChosen() {
        if (chosenTag.isEmpty()){
            return false;
        } else {
            return true;
        }
    }
}
