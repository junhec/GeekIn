package com.example.yt80.cs591e1_geekin.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Activities.MyFollowing;
import com.example.yt80.cs591e1_geekin.Activities.PostDetailActivity;
import com.example.yt80.cs591e1_geekin.Adapters.BriefPostAdapter;
import com.example.yt80.cs591e1_geekin.Activities.Main;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.Common.myGridView;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.example.yt80.cs591e1_geekin.Common.myScrollView;

/**
 * Created by yt80 on 2017/4/9.
 */

public class ProfileFrag extends Fragment implements SwipyRefreshLayout.OnRefreshListener{
    private User user;
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

    public static BriefPostAdapter adapter;
    private int width;

    private static final int DISMISS_TIMEOUT = 1000;


    // ---------------------------------------------------
    // Fragments Communication Functions
    // ---------------------------------------------------

    public interface PostListFragmentListener {            //this is just an interface definition.
        public void sendID(String msg, Integer id); //it could live in its own file.  placed here for convenience.
    }
    ExploreFrag.PostListFragmentListener PLFL;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        PLFL = (ExploreFrag.PostListFragmentListener) context;  //context is a handle to the main activity, let's bind it to our interface.
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment5, null);
        // Get variables in tabFragmentHost
        // Relate variables to elements in layout
        usernameDisplay = (TextView) view.findViewById(R.id.tv_userNameDisplay);
        postTag = (TextView) view.findViewById(R.id.tv_postTag);
        userIconDisplay = (ImageView) view.findViewById(R.id.img_userIconDisplay);
        infoName_linearlayout =(LinearLayout) view.findViewById(R.id.infoName_linearlayout);
        infoNum_linearlayout = (LinearLayout) view.findViewById(R.id.infoNum_linearlayout);
        myPostView = (LinearLayout)view.findViewById(R.id.myPostView);
        gridviewProfile = (myGridView) view.findViewById(R.id.profile_gridView);
        numOfFollower = (TextView) view.findViewById(R.id.tv_numOfFollowers);
        numOfFollowing = (TextView) view.findViewById(R.id.tv_numOfFollowing);
        numOfPost = (TextView) view.findViewById(R.id.tv_numOfPost);
        Post = (TextView)view.findViewById(R.id.tv_Post);
        Follower = (TextView)view.findViewById(R.id.tv_Follower);
        Following = (TextView)view.findViewById(R.id.tv_Following);
        mainheadview = (LinearLayout) view.findViewById(R.id.mainheadview);
        mySwipeRefreshLayout=(SwipyRefreshLayout)view.findViewById(R.id.refresh);
        scrollView_profile = (myScrollView)view.findViewById(R.id.scrollview_userProfile);

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

        tabFragmentHost = ((Main) getActivity()).getTabHost();
        myFragmentManager = tabFragmentHost.getmFragmentManager();
        user = tabFragmentHost.getUser();

        setUser(user);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        adapter = new BriefPostAdapter(getContext(), new ArrayList<BriefPost>());
        try {
            retrievePost();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        widthCaculator();
        setGridview();


        return view;
    }

    // ---------------------------------------------------
    // Layout Set Up Functions
    // ---------------------------------------------------
    private int heightCaculator() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int)(0.9*metrics.heightPixels/2);
    }

    private void setScrollControl(){
        scrollView_profile.setOnScrollListener(new myScrollView.OnScrollListener(){
            @Override
            public void onScrollChanged(int x,int y, int oldX, int oldY){
                android.util.Log.d("@", "x:" + oldX + "->" + x + ", y:" + oldY + "->" + y);
            }

            @Override
            public void onScrollStopped() {
                if (scrollView_profile.isAtTop()) {
                    mainheadview.setVisibility(View.VISIBLE);
                } else {
                }
            }

            @Override
            public void onScrolling(){
                android.util.Log.d("@", "scrolling...");
            }

        });
    }

    private void setUser(User user) {
        userIconDisplay.setImageBitmap(ImageUtil.StringToBitMap(user.getUserPic()));
        usernameDisplay.setText(user.getUserName().toString());

        numOfFollower.setText(String.valueOf(user.getFollower()));
        numOfPost.setText(String.valueOf(user.getPostNum()));
        numOfFollowing.setText(String.valueOf(user.getFollowing()));

        //final Intent it_follower = new Intent(this.getActivity(), MyFollower.class);
        final Intent followUserIntent = new Intent(getActivity(), MyFollowing.class);
        followUserIntent.putExtra("accountEmail", user.getUserEmail());
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
    }


    private void widthCaculator() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width=(int)(0.9*metrics.widthPixels/2);
    }

    /**
     * Set up each grid view from adapter.
     If a gridview is clicked, jump in to post detail fragment.
     */
    private void setGridview() {
        gridviewProfile.setAdapter(adapter);
        gridviewProfile.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent postDetailIntent = new Intent(getActivity(),PostDetailActivity.class);
                postDetailIntent.putExtra("pid", String.valueOf(gridviewProfile.getItemAtPosition(position)));
                postDetailIntent.putExtra("accountEmail",user.getUserEmail());
                startActivity(postDetailIntent);
            }
        });
    }


    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send retrieve post request to server
     * @throws JSONException
     * @throws InterruptedException
     */
    public void retrievePost() throws JSONException, InterruptedException {

        try{

            JSONObject userInfo = new JSONObject();
            userInfo.put("email", user.getUserEmail());
            if (adapter.getpIDs().size() != 0) {
                userInfo.put("pids",adapter.getpIDs());
            }

            NetworkUtil.getInstance(getActivity()).retrievePost(type,userInfo, new NetworkUtil.VolleyCallback() {
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

    /**
     * Handle result from server after sending retrieve profile request
     * @param result
     * @param posts
     */
    private void retrieveHandler(Boolean result, ArrayList<BriefPost> posts) {
        if (result) {
            adapter.addData(posts);
        } else {
            Toast.makeText(getContext(),"Cannot get posts", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------------------------------------------
    // Swipy Refresh Layout Functions
    // ---------------------------------------------------
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }

    public void doLoad() throws JSONException, InterruptedException {
        retrievePost();
    }

}
