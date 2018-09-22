package com.example.yt80.cs591e1_geekin.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Adapters.FollowingAdapter;
import com.example.yt80.cs591e1_geekin.Adapters.NotificationAdapter;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.facebook.Profile;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by junhec on 4/16/2017.
 */
public class MyFollowing extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener {

    private ListView followingLV;
    private Context context;
    private SwipyRefreshLayout mySwipeRefreshLayout;
    private static final int DISMISS_TIMEOUT = 1000;
    public FollowingAdapter mfAdapter;

    private String accountEmail;
    private String function;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_myfollowing);

        getWindow().setStatusBarColor(getResources().getColor(R.color.primary));

        accountEmail = getIntent().getStringExtra("accountEmail");
        function = getIntent().getStringExtra("function");
        followingLV = (ListView) findViewById(R.id.notification_lv);
        mySwipeRefreshLayout=(SwipyRefreshLayout)findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        retrieveUserList(function);

        mfAdapter = new FollowingAdapter(MyFollowing.this,new ArrayList<User>());
        followingLV.setAdapter(mfAdapter);
        // Make each list view clickable. And go to the user profile activity.
        followingLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent postDetailIntent = new Intent(MyFollowing.this, ProfileActivity.class);
                postDetailIntent.putExtra("postOwnerEmail", String.valueOf(followingLV.getItemAtPosition(position)));
                postDetailIntent.putExtra("accountEmail",accountEmail);
                startActivity(postDetailIntent);
            }
        });

    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send retrieveUserList request to Server.
     * @param getFollowing
     */
    private void retrieveUserList(String getFollowing) {
        try{
            JSONObject userInfo = new JSONObject();
            userInfo.put("email", accountEmail);
            NetworkUtil.getInstance(this).getUserList(getFollowing,userInfo, new NetworkUtil.VolleyCallback() {
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
                }

                @Override
                public void onGetUserListSuccess(Boolean result, ArrayList<User> userList) {
                    getUserLIstResultHandler(result,userList);
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the result from server after getting user list from server.
     * @param result
     */
    private void getUserLIstResultHandler(Boolean result, ArrayList<User> userList) {
        if (result) {
            if (!userList.isEmpty()) {
                mfAdapter.addData(userList);
            } else {
            }
        } else {
            Toast.makeText(context,"Get user failed",Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------------------------------------------
    // Refreshable Layout Functions
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }

    /**
     * Future work
     */
    public void doLoad() {
    }
    public void doUpdate() {
    }
}