package com.example.yt80.cs591e1_geekin.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Adapters.BriefPostAdapter;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Common.myGridView;
import com.example.yt80.cs591e1_geekin.Common.myScrollView;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by haotianwu on 5/1/17.
 */

public class ProfileActivity extends AppCompatActivity implements SwipyRefreshLayout.OnRefreshListener{
    private String accountEmail;
    private String postOwnerEmail;

    private myGridView gridviewProfile;
    private TextView usernameDisplay;
    private ImageView userIconDisplay;
    private TextView numOfFollower;
    private TextView numOfFollowing;
    private TextView Follower,Following,Post;
    private LinearLayout infoName_linearlayout;
    private LinearLayout infoNum_linearlayout;
    private LinearLayout myPostView;
    private LinearLayout mainheadview;
    private LinearLayout followButtonView;
    private TextView numOfPost;
    private TextView postTag;
    private Button btn_follow;
    private Context mContext;
    private FragmentManager myFragmentManager;
    private SwipyRefreshLayout mySwipeRefreshLayout;
    private myScrollView scrollView_profile;
    private Button cancel;
    private int page = 0;


    private String type = "getPosts";
    private TabFragmentHost tabFragmentHost;
    //
    public static BriefPostAdapter adapter;
    private int width;

    private static final int DISMISS_TIMEOUT = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        //user = (User) getIntent().getSerializableExtra(LogIn.SER_KEY);
        postOwnerEmail  = getIntent().getStringExtra("postOwnerEmail");
        accountEmail = getIntent().getStringExtra("accountEmail");
        usernameDisplay = (TextView) findViewById(R.id.tv_userNameDisplay);
        postTag = (TextView) findViewById(R.id.tv_postTag);
        userIconDisplay = (ImageView) findViewById(R.id.img_userIconDisplay);
        infoName_linearlayout =(LinearLayout) findViewById(R.id.infoName_linearlayout);
        infoNum_linearlayout = (LinearLayout) findViewById(R.id.infoNum_linearlayout);
        myPostView = (LinearLayout) findViewById(R.id.myPostView);
        gridviewProfile = (myGridView) findViewById(R.id.profile_gridView);
        numOfFollower = (TextView) findViewById(R.id.tv_numOfFollowers);
        numOfFollowing = (TextView) findViewById(R.id.tv_numOfFollowing);
        numOfPost = (TextView) findViewById(R.id.tv_numOfPost);
        Post = (TextView) findViewById(R.id.tv_Post);
        Follower = (TextView) findViewById(R.id.tv_Follower);
        Following = (TextView) findViewById(R.id.tv_Following);
        btn_follow = (Button) findViewById(R.id.btn_Follow);
        mainheadview = (LinearLayout) findViewById(R.id.mainheadview);
        followButtonView = (LinearLayout) findViewById(R.id.followButtonView);
        if (postOwnerEmail.equals(accountEmail)) {
            followButtonView.setVisibility(GONE);
            btn_follow.setVisibility(GONE);
        }

        int height = heightCaculator();
        ViewGroup.LayoutParams lp_head;
        lp_head = mainheadview.getLayoutParams();
        lp_head.height = height;
        mainheadview.setLayoutParams(lp_head);

        ViewGroup.LayoutParams lp_usericon;
        lp_usericon = userIconDisplay.getLayoutParams();
        lp_usericon.height = 5 * height / 12;
        lp_usericon.width = 5 * height / 12;
        userIconDisplay.setLayoutParams(lp_usericon);

        mainheadview = (LinearLayout) findViewById(R.id.mainheadview);
        scrollView_profile = (myScrollView) findViewById(R.id.scrollview_userProfile);

        try {
            getUserProfile(postOwnerEmail);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new BriefPostAdapter(this, new ArrayList<BriefPost>());
        try {
            retrievePost();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        widthCaculator();
        setGridview();
        mySwipeRefreshLayout=(SwipyRefreshLayout) findViewById(R.id.refresh);
        mySwipeRefreshLayout.setOnRefreshListener(this);
    }


    private void setUser(User postOwner) {
        //Toast.makeText(getApplicationContext(),"User Email: " + postOwner.getUserEmail(), Toast.LENGTH_SHORT).show();
        userIconDisplay.setImageBitmap(ImageUtil.StringToBitMap(postOwner.getUserPic()));
        usernameDisplay.setText(postOwner.getUserName().toString());

        numOfFollower.setText(String.valueOf(postOwner.getFollower()));
        numOfPost.setText(String.valueOf(postOwner.getPostNum()));
        numOfFollowing.setText(String.valueOf(postOwner.getFollowing()));

        final Intent followUserIntent = new Intent(this, MyFollowing.class);
        if (accountEmail.equals(postOwnerEmail)) {
            followUserIntent.putExtra("accountEmail", accountEmail);
        } else {
            followUserIntent.putExtra("accountEmail", postOwnerEmail);
        }

        Follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUserIntent.putExtra("function", "getFollowers");
                startActivity(followUserIntent);
            }
        });

        Following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUserIntent.putExtra("function", "getFollowings");
                startActivity(followUserIntent);
            }
        });

        numOfFollower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUserIntent.putExtra("function", "getFollowers");
                startActivity(followUserIntent);
            }
        });

        numOfFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUserIntent.putExtra("function", "getFollowings");
                startActivity(followUserIntent);
            }
        });


        if (!postOwnerEmail.equals(accountEmail)) {
            if (postOwner.getIsFollowing()){
                btn_follow.setText("Unfollow");
            } else {
                btn_follow.setText("Follow");
            }
            btn_follow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (btn_follow.getText().toString().equals("Unfollow")) {
                        follow("removeFollowing");
                        btn_follow.setText("Follow");
                        btn_follow.setBackground(ProfileActivity.this.getResources().getDrawable(R.drawable.login_button));
                        btn_follow.setTextColor(ProfileActivity.this.getResources().getColor(R.color.white));
                    } else {
                        follow("addFollowing");
                        btn_follow.setText("Unfollow");
                        btn_follow.setBackground(ProfileActivity.this.getResources().getDrawable(R.drawable.newpost_button));
                        btn_follow.setTextColor(ProfileActivity.this.getResources().getColor(R.color.primary));
                    }

                }
            });
        }
    }

    private void getUserProfile(String postOwnerEmail) throws JSONException, InterruptedException {
        try{
            JSONObject postOwnerInfo = new JSONObject();
            postOwnerInfo.put("email", postOwnerEmail);
            if (!postOwnerEmail.equals(accountEmail)) {
                postOwnerInfo.put("self",accountEmail);
            }
            NetworkUtil.getInstance(this).getUserProfile(postOwnerInfo, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }
                @Override
                public void onLoginSuccess(Boolean result, User user) {
                    getProfileResultHandler(result,user);
                }
                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {
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

                }
            });
        }catch(Exception e){
            throw e;
        }
    }

    private void getProfileResultHandler(Boolean result, User user) {
        if (result) {
            setUser(user);
        } else{
            Toast.makeText(getApplicationContext(),"Can't get user profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void widthCaculator() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width=(int)(0.9*metrics.widthPixels/2);
    }

    private int heightCaculator() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int)(0.9*metrics.heightPixels/2);
    }

    /*
          Functions for Swipy Refresh Layout
       */
    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        try {
            doLoad();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    /*
        Load more posts before from the server
     */
    public void doLoad() throws JSONException, InterruptedException {
        retrievePost();
        //gridviewProfile.smoothScrollToPositionFromTop(10, 0, 0);
    }

    /*
       Set up each grid view from adapter.
       If a gridview is clicked, jump in to post detail fragment.
    */
    private void setGridview() {
        gridviewProfile.setAdapter(adapter);
        gridviewProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent postDetailIntent = new Intent(ProfileActivity.this,PostDetailActivity.class);
                postDetailIntent.putExtra("pid", String.valueOf(gridviewProfile.getItemAtPosition(position)));
                postDetailIntent.putExtra("accountEmail",accountEmail);
                startActivity(postDetailIntent);
            }
        });
    }

    /*
       Handle the result responded from server.
       If result is true, add post array list received from server to the adapter and show on screen
       TODO: if result is false, handle it.
    */


    //    /*
//       Get post from server
//    */
    public void retrievePost() throws JSONException, InterruptedException {

        try{
            JSONObject postOwnerInfo = new JSONObject();
            postOwnerInfo.put("email", postOwnerEmail);
            if (adapter.getpIDs().size() != 0) {
                postOwnerInfo.put("pids",adapter.getpIDs());
            }
//            postOwnerInfo.put("page", page);
//            page++;
            NetworkUtil.getInstance(this).retrievePost(type,postOwnerInfo, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }
                @Override
                public void onLoginSuccess(Boolean result, User user) {
                }
                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts){
                    retrieveHandler(result,posts);
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

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void retrieveHandler(Boolean result, ArrayList<BriefPost> posts) {
        if (result) {
            adapter.addData(posts);
//            if (page > 1) {
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Do something after 5s = 5000ms
//                        gridviewProfile.smoothScrollToPositionFromTop(gridviewProfile.getBottom()-2, 2, 500);
//                    }
//                }, 500);
//            }
//        } else {

        }
    }

    private void follow(String type) {
        try{
            JSONObject followInfo = new JSONObject();
            followInfo.put("followee", postOwnerEmail);
            followInfo.put("follower", accountEmail);
            NetworkUtil.getInstance(this).follow(type,followInfo, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    followResultHandler(result);
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

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void followResultHandler(Boolean result) {
        if (result) {
            Toast.makeText(getApplicationContext(),"Follow Success", Toast.LENGTH_SHORT).show();
        }
    }
}

