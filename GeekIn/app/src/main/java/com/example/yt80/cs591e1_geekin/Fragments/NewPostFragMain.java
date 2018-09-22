package com.example.yt80.cs591e1_geekin.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Common.Anim;
import com.example.yt80.cs591e1_geekin.Activities.Main;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Adapters.SignUpViewPageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yt80 on 2017/4/10.
 */

public class NewPostFragMain extends Fragment {

    private ViewPager vp;
    private List<Fragment> list = new ArrayList<Fragment>();
    private NewPostFragImage newPostFragment1;
    private NewPostFragContent newPostFragment2;
    private LinearLayout.LayoutParams layoutParams;
    private String path;
    private Button post;
    private Button next;
    private Button cancel;
    private Anim anim = new Anim();
    private String TAG = "FUCK";
    private TabFragmentHost tabhost;
    private FragmentManager fm;
    private FragmentTransaction ft;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newpost_fragment_main, null);

        vp = (ViewPager) view.findViewById(R.id.main_newpost_pager);

        setGoBackButton(view);
        newPostFragment1 = new NewPostFragImage();
        newPostFragment2 = new NewPostFragContent();


        tabhost= ((Main) getActivity()).getTabHost();
        fm = tabhost.getmFragmentManager();

        next=(Button)view.findViewById(R.id.next);
        next.setTag((Object)true);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(vp.getCurrentItem()==0){
                    next();
                    next.setText("Post");
                }
                else post();
            }
        });



        list.add(newPostFragment1);
        list.add(newPostFragment2);

        //绑定Fragment适配器
        vp.setAdapter(new SignUpViewPageAdapter(getActivity().getSupportFragmentManager(), list));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(vp.getCurrentItem()==0){
                    next.setText("Next");
                } else {
                    next.setText("Post");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    private void setGoBackButton(View view) {
        cancel = (Button) view.findViewById(R.id.back);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // Pop the previous fragment
                if (fm.getBackStackEntryCount() > 0) {
                    ft = fm.beginTransaction();
                    ft.remove(newPostFragment1);
                    ft.remove(newPostFragment2);
                    ft.commit();
                    fm.popBackStack();
                    // Show tab bar with animation
                    tabhost.getBorderLine().setVisibility(View.VISIBLE);
                    anim.slideToTop(tabhost.getBorderLine(),tabhost.getTabWidget().getHeight());
                    for (int i = 0; i < 5; i++) {
                        TabWidget tab = tabhost.getTabWidget();
                        tab.setVisibility(View.VISIBLE);
                        anim.slideToTop(tab,0);
                    }
                } else {
                    Log.i(TAG,"SB Stack");
                }
            }
        });
    }

    private void post(){
        if (!newPostFragment2.isEmpty()) {
            newPostFragment2.post();
            ft = fm.beginTransaction();
            ft.remove(newPostFragment1);
            ft.remove(newPostFragment2);
            ft.commit();
        } else {
            Toast.makeText(getContext(),"title cannot be empty",Toast.LENGTH_LONG).show();
        }


    }
    private void next(){
        vp.setCurrentItem(1);
    }

    @Override
    public void onDestroy(){
        ft = fm.beginTransaction();
        ft.remove(newPostFragment1);
        ft.remove(newPostFragment2);
        ft.commit();
        fm.popBackStack();
        // Show tab bar with animation
        tabhost.getBorderLine().setVisibility(View.VISIBLE);
        anim.slideToTop(tabhost.getBorderLine(),tabhost.getTabWidget().getHeight());
        for (int i = 0; i < 5; i++) {
            TabWidget tab = tabhost.getTabWidget();
            tab.setVisibility(View.VISIBLE);
            anim.slideToTop(tab,0);
        }
        super.onDestroy();
    }
}
