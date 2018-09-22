package com.example.yt80.cs591e1_geekin.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * View page adapter. Used in the sign up view.
 */

public class SignUpViewPageAdapter extends FragmentPagerAdapter {
    List<Fragment> list;

    public SignUpViewPageAdapter(FragmentManager fm, List<Fragment> list) {
        super(fm);
        this.list=list;
    }
    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public int getCount() {
        return list.size();
    }//设置Item的数量

}


