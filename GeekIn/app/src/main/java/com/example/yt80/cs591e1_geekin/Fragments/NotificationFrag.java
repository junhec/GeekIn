package com.example.yt80.cs591e1_geekin.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yt80.cs591e1_geekin.Activities.PostDetailActivity;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Activities.Main;
import com.example.yt80.cs591e1_geekin.Adapters.NotificationAdapter;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yt80 on 2017/4/9.
 */

public class NotificationFrag extends Fragment implements SwipyRefreshLayout.OnRefreshListener {

    // Arraylists and adapter for Notification list view.
    private ListView notificationLV;
    public NotificationAdapter nf_adapter;
    private ArrayList<Bitmap> user_pic=new ArrayList<>();
    private ArrayList<Bitmap> post_image=new ArrayList<>();
    private ArrayList<String> notifications=new ArrayList<>();

    // For SwipyRefreshLayout
    private SwipyRefreshLayout mySwipeRefreshLayout;
    private static final int DISMISS_TIMEOUT = 1000;

    private User user;
    private View view;
    private TextView noNotification;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment4, null);
        user = ((Main) getActivity()).getTabHost().getUser();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int prefered_width=(int)(metrics.widthPixels/6);

        notificationLV = (ListView) view.findViewById(R.id.notification_lv);
        mySwipeRefreshLayout=(SwipyRefreshLayout)view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this);
        noNotification = (TextView) view.findViewById(R.id.no_notification);
        retrieveNotification();

        nf_adapter = new NotificationAdapter(getContext(),new ArrayList<Notification>());
        notificationLV.setAdapter(nf_adapter);
        notificationLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent postDetailIntent = new Intent(getActivity(),PostDetailActivity.class);
                postDetailIntent.putExtra("pid", String.valueOf(notificationLV.getItemAtPosition(position)));
                postDetailIntent.putExtra("accountEmail",user.getUserEmail());
                startActivity(postDetailIntent);
            }
        });
        return view;
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send retrieve notification request to server.
     */
    private void retrieveNotification() {
        try{
            JSONObject userInfo = new JSONObject();
            userInfo.put("email", user.getUserEmail());
            NetworkUtil.getInstance(getActivity()).retrieveNotification(userInfo, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }
                @Override
                public void onLoginSuccess(Boolean result, User user) {
                }
                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts){
                }

                @Override
                public void onGetPDetailSuccess(Boolean result, PostDetail pDetail) {
                }

                @Override
                public void onGetCommentsSuccess(Boolean result, ArrayList<Comment> commentList) {
                }

                @Override
                public void onGetNotifySuccess(Boolean result, ArrayList<Notification> notificationList) {
                    notifySucessHandler(result,notificationList);
                }

                @Override
                public void onGetUserListSuccess(Boolean result, ArrayList<User> userList) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handle result from server after sending retrieve notification request to server
     * @param result
     * @param notificationList
     */
    private void notifySucessHandler(Boolean result, ArrayList<Notification> notificationList) {
        if (result) {
            if (notificationList.isEmpty()) {
                if (noNotification.getVisibility() == View.GONE) {
                    noNotification.setVisibility(View.VISIBLE);
                }
            } else {
                if (noNotification.getVisibility() == View.VISIBLE) {
                    noNotification.setVisibility(View.GONE);
                }
                nf_adapter.addData(notificationList);
            }
        }

    }

    // ---------------------------------------------------
    // Swipy Refresh Layout Functions
    // ---------------------------------------------------
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        if (direction == SwipyRefreshLayoutDirection.TOP) {
            doUpdate();
        } else {
            doLoad();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }

    public void doLoad() {
        retrieveNotification();
    }
    public void doUpdate() {

    }

}