package com.example.yt80.cs591e1_geekin.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;


import com.example.yt80.cs591e1_geekin.Activities.PostDetailActivity;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Adapters.BriefPostAdapter;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Activities.Main;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.SimpleCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yt80 on 2017/4/9.
 */

public class HomeFrag extends Fragment implements SwipyRefreshLayout.OnRefreshListener {
    private String[] categories = {"All Categories", "Game Accessories", "Electronics", "Tools", "Home & Office"};
    private GridView gridview;
    private SwipyRefreshLayout mySwipeRefreshLayout;
    private static final int DISMISS_TIMEOUT = 1000;
    public BriefPostAdapter adapter;

    private TabFragmentHost myTabFragmentHost;
    private User user;
    private int page = 0;
    private BoomMenuButton bmb;
    private int searchCat = 0;
    private String type = "getTrends";





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1, null);

        bmb = (BoomMenuButton) view.findViewById(R.id.bmb);
        setBMB();

        myTabFragmentHost = ((Main) getActivity()).getTabHost();
        user = myTabFragmentHost.getUser();
        gridview=(GridView)view.findViewById(R.id.signup_gridview);
        mySwipeRefreshLayout=(SwipyRefreshLayout)view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this);

        if (page == 0) {
            adapter = new BriefPostAdapter(getContext(), new ArrayList<BriefPost>());
            retrieveTrending(0);
        }


        setGridview();

        return view;
    }

    // ---------------------------------------------------
    // Layout Set Up Functions
    // ---------------------------------------------------
    private void setBMB() {
        bmb.setNormalColor(Color.parseColor("#19B5FE"));
        for (int i = 0; i < categories.length; i++) {

            HamButton.Builder builder = new HamButton.Builder()
                    .normalText(categories[i]).textGravity(Gravity.LEFT).textSize(20).normalTextColor(Color.WHITE).highlightedColor(Color.RED);
            builder.pieceColor(Color.WHITE);
            builder.listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                    // When the boom-button corresponding this builder is clicked.
                    adapter = new BriefPostAdapter(getContext(), new ArrayList<BriefPost>());
                    page = 0;
                    searchCat = index;
                    gridview.setAdapter(adapter);
                    retrieveTrending(index);
                }
            });

            bmb.addBuilder(builder);
        }
    }

    /**
     * Set up each grid view from adapter.
     If a gridview is clicked, jump in to post detail fragment.
     */
    private void setGridview() {
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent postDetailIntent = new Intent(getActivity(),PostDetailActivity.class);
                postDetailIntent.putExtra("pid", String.valueOf(gridview.getItemAtPosition(position)));
                postDetailIntent.putExtra("accountEmail",user.getUserEmail());
                startActivity(postDetailIntent);
            }
        });
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send retrieve trending posts to server.
     * @param searchTag
     */
    private void retrieveTrending(int searchTag) {
        try {
            JSONObject trendingInfo = new JSONObject();
            trendingInfo.put("email", user.getUserEmail());
            trendingInfo.put("tag", searchTag);
            //trendingInfo.put("page", page);
            if (adapter.getpIDs().size() != 0) {
                trendingInfo.put("pids",adapter.getpIDs());
            }

            page++;
            NetworkUtil.getInstance(getActivity()).retrievePost(type,trendingInfo, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }

                @Override
                public void onLoginSuccess(Boolean result, User user) {
                }

                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {
                    retrieveHandler(result, posts);
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
     * Handle the result responded from server.
     If result is true, add post array list received from server to the adapter and show on screen
     * @param result
     * @param posts
     */
    private void retrieveHandler(Boolean result, ArrayList<BriefPost> posts) {
        if (result) {
            //Toast.makeText(getContext(),"add data", Toast.LENGTH_SHORT).show();
            //Toast.makeText(getContext(),"length: " + posts.size(), Toast.LENGTH_SHORT).show();
            adapter.addData(posts);
            adapter.notifyDataSetChanged();
//            if (page > 1) {
//                gridview.smoothScrollToPosition(gridview.getBottom()-2);
//            }

        } else {

        }
    }





    /*
        Functions for refreshable layout.
     */
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
        retrieveTrending(searchCat);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                gridview.smoothScrollToPositionFromTop(gridview.getBottom()-2, 2, 500);
            }
        }, 500);
    }

    public void doUpdate() {
        //retrieveTrending(searchCat);
    }


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
}
