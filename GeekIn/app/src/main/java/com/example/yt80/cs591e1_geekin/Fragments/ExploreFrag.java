package com.example.yt80.cs591e1_geekin.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.yt80.cs591e1_geekin.Activities.PostDetailActivity;
import com.example.yt80.cs591e1_geekin.Adapters.BriefPostAdapter;
import com.example.yt80.cs591e1_geekin.Activities.Main;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yt80 on 2017/4/9.
 */

public class ExploreFrag extends Fragment implements MaterialSearchBar.OnSearchActionListener , SwipyRefreshLayout.OnRefreshListener {

    private User user;
    private TabFragmentHost myTabFragmentHost;
    private String functionType = "searchPosts";

    // String array list for filter
    private String[] sort_ways = {"Most Hot", "Most Popular"};

    // 0 is "Most Hot", 1 is "Most Popular"
    private int sort = 0;
    private String keyword = "";
    // Arraylists for grid view
    private GridView gridview;
    public BriefPostAdapter adapter;
    private ArrayList<Integer> pIDs = new ArrayList<>();
    private ArrayList<Bitmap> images=new ArrayList<>();
    private ArrayList<Bitmap> icons=new ArrayList<>();
    private ArrayList<String> descriptions=new ArrayList<>();
    private ArrayList<String> names=new ArrayList<>();

    // variables needed for search bar
    private MaterialSearchBar searchBar;
    private DrawerLayout drawer;
    private List<String> lastSearches;
    private int page = 0;

    // refreshable layout
    private SwipyRefreshLayout mySwipeRefreshLayout;
    private static final int DISMISS_TIMEOUT = 1000;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment2, null);

        myTabFragmentHost = ((Main) getActivity()).getTabHost();
        user = myTabFragmentHost.getUser();
        gridview=(GridView)view.findViewById(R.id.signup_gridview);

        setSearchBar(view);

        if (page == 0) {
            adapter = new BriefPostAdapter(getContext(),new ArrayList<BriefPost>());
            retrievePost(keyword,sort);
        }


        mySwipeRefreshLayout=(SwipyRefreshLayout)view.findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(this);


        setGridview();

        return view;
    }


    // ---------------------------------------------------
    // Layout Set Up Functions
    // ---------------------------------------------------

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

    /**
     * Init search Bar and set onChange listener.
     * @param view
     */
    private void setSearchBar(View view) {
        searchBar = (MaterialSearchBar) view.findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        searchBar.setCardViewElevation(10);
        searchBar.inflateMenu(R.menu.sort_menu);
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString() == sort_ways[0]) {
                    sort = 0;
                } else {
                    sort = 1;
                }
                //Toast.makeText(getContext(),"sort by: " + item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("LOG_TAG", getClass().getSimpleName() + " text changed " + searchBar.getText());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send retrieve post request to server
     * @param keyword
     * @param sort
     */
    private void retrievePost(String keyword, int sort) {
        try{
            JSONObject searchInfo = new JSONObject();
            searchInfo.put("keyword",keyword);
            searchInfo.put("order", sort);
            searchInfo.put("page", page);
            if (adapter.getpIDs().size() != 0) {
                searchInfo.put("pids",adapter.getpIDs());
            }
            page++;
            NetworkUtil.getInstance(getActivity()).retrievePost(functionType,searchInfo, new NetworkUtil.VolleyCallback() {
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
     * Handle the result from server after sending retrieve post request to server
     * @param result
     * @param posts
     */
    private void retrieveHandler(Boolean result, ArrayList<BriefPost> posts) {
        if (result) {
            if(posts.size()==0) Toast.makeText(getContext(),"No More Posts", Toast.LENGTH_SHORT).show();
            adapter.addData(posts);
            if (page > 1) {
                gridview.smoothScrollToPosition(gridview.getBottom()-2);
            }
        } else {
            Toast.makeText(getContext(),"Retrieve posts failed" + posts.size(), Toast.LENGTH_SHORT).show();
        }
    }





    // ---------------------------------------------------
    // Search Bar Functions
    // ---------------------------------------------------
    @Override
    public void onSearchStateChanged(boolean enabled) {
        String s = enabled ? "enabled" : "disabled";
        //Toast.makeText(getActivity(), "Search " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        //if (!text.toString().equals("")) {
        //Toast.makeText(getActivity(), "Search " + text + "Sort: " + sort, Toast.LENGTH_SHORT).show();
        adapter = new BriefPostAdapter(getContext(), new ArrayList<BriefPost>());
        gridview.setAdapter(adapter);
        retrievePost(text.toString(),sort);
        //}
    }

    @Override
    public void onButtonClicked(int buttonCode) {
        switch (buttonCode){
            case MaterialSearchBar.BUTTON_NAVIGATION:
                drawer.openDrawer(Gravity.LEFT);
                break;
            case MaterialSearchBar.BUTTON_SPEECH:
                //TODO: Voice Search
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
        retrievePost(keyword,sort);
        int count = adapter.getCount();
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
        retrievePost(keyword,sort);
    }

    // ---------------------------------------------------
    // Fragments Communication Functions
    // ---------------------------------------------------
    private PostListFragmentListener PLFL;

    public interface PostListFragmentListener {            //this is just an interface definition.
        public void sendID(String msg, Integer id) throws JSONException; //it could live in its own file.  placed here for convenience.
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        PLFL = (PostListFragmentListener) context;  //context is a handle to the main activity, let's bind it to our interface.
    }

}
