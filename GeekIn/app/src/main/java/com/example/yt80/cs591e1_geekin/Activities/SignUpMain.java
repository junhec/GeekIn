package com.example.yt80.cs591e1_geekin.Activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yt80.cs591e1_geekin.Adapters.SignUpViewPageAdapter;
import com.example.yt80.cs591e1_geekin.R;

import java.util.ArrayList;
import java.util.List;

public class SignUpMain extends AppCompatActivity {
    private ViewPager vp;
    private List<Fragment> list = new ArrayList<Fragment>();
    private int cur=0;
    private Button btn_prev;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_prev=(Button)findViewById(R.id.btn_prev);
        btn_next=(Button)findViewById(R.id.btn_next);

        vp = (ViewPager) findViewById(R.id.pager);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
                ;
            }
        });

        vp.setAdapter(new SignUpViewPageAdapter(getSupportFragmentManager(), list));
    }

    public void next(View view){
        if(++cur==1) {
            btn_prev.setEnabled(true);
            btn_next.setText("finish");
            vp.setCurrentItem(cur);
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submit();
                }
            });
        }
    }

    public void submit(){;}
}
