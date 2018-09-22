package com.example.yt80.cs591e1_geekin.Activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Common.Anim;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Fragments.ExploreFrag;
import com.example.yt80.cs591e1_geekin.Fragments.HomeFrag;
import com.example.yt80.cs591e1_geekin.Fragments.NewPostFragMain;
import com.example.yt80.cs591e1_geekin.Fragments.NewPostFragImage;
import com.example.yt80.cs591e1_geekin.Fragments.NewPostFragContent;
import com.example.yt80.cs591e1_geekin.Fragments.NotificationFrag;
import com.example.yt80.cs591e1_geekin.Fragments.ProfileFrag;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


//
public class Main extends AppCompatActivity implements TabHost.OnTabChangeListener, ExploreFrag.PostListFragmentListener,  NewPostFragImage.main_fragment_newpost_1_channel, NewPostFragContent.main_fragment_newpost_2_channel{
    private TabFragmentHost tabhost;
    private User user;
    private final String TAG = "TAG";
    private String textArray[] = { "1", "2", "3","4","5"};
    private int imageViewArray[] = { R.drawable.tab_home_btn, R.drawable.tab_explore_btn, R.drawable.tab_new_post_btn, R.drawable.tab_notification_btn, R.drawable.tab_profile_btn};
    private Class[] fragment_class={HomeFrag.class,ExploreFrag.class,NewPostFragMain.class,NotificationFrag.class,ProfileFrag.class};
    private Anim anim = new Anim();
    private String path;
    private boolean isTab2 = false;
    private int level = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        user = (User) getIntent().getSerializableExtra(LogIn.SER_KEY);
        tabhost = (TabFragmentHost)findViewById(android.R.id.tabhost);
        tabhost.setup(this,getSupportFragmentManager(),android.R.id.tabcontent,user);
        tabhost.setOnTabChangedListener(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        for(int i=0;i<fragment_class.length;i++)
            tabhost.addTab(tabhost.newTabSpec(textArray[i]).setIndicator(getTabItemView(i)),fragment_class[i],null);
    }


    // ---------------------------------------------------
    // Tab related Functions
    // ---------------------------------------------------

    /**
     * If the "create post" tab is clicked, hide the tab bar.
     * @param tabId
     */
    @Override
    public void onTabChanged(String tabId) {
        this.level = 0;
        if (tabhost.getCurrentTab() == 2) {
            isTab2 = true;
            tabhost.setCurrentTab(Integer.parseInt(tabhost.getmLastTabTag())-1);
            tabBarHide();
        }
    }

    /**
     * Let other fragments get the current tab host.
     * @return
     */
    public TabFragmentHost getTabHost() {
        return tabhost;
    }

    /**
     * Get each tab item view.
     * @param i
     * @return
     */
    private View getTabItemView(int i) {
        View view = getLayoutInflater().inflate(R.layout.tab_content, null);

        float density = getResources().getDisplayMetrics().density;
        int padding = (int) (5f * density);
        int imageSize = (int) (36 * density);

        ImageView mImageView = (ImageView) view
                .findViewById(R.id.tab_imageview);
        mImageView.setBackgroundResource(imageViewArray[i]);
        mImageView.getLayoutParams().width = imageSize;
        mImageView.getLayoutParams().height = imageSize;
        mImageView.setPadding(padding, padding, padding, padding);
        return view;
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Receive post content from new post fragment 2.
     * And send add new post request to server
     * @param title
     * @param content
     * @param tagList
     */
    @Override
    public void sendContent(String title, String content, ArrayList<Integer> tagList) {
        JSONObject json=new JSONObject();
        try {
            // Put basic info to the json object
            json.put("email",user.getUserEmail());
            json.put("title",title);
            json.put("content",content);
            json.put("tags", tagList);
            File dir=new File(path);
            JSONArray json_arr=new JSONArray();

            // put each image to a json array
            for(File f : dir.listFiles()){
                if(f.length()==0)//user select photo and quit
                    continue;
                Bitmap image= BitmapFactory.decodeFile(f.getAbsolutePath());
                String image_to_str= ImageUtil.imageToString(image);
                json_arr.put(image_to_str );
            }
            //dir.delete();
            json.put("images",json_arr);

            // call the create new post function in util network and send request to server.
            NetworkUtil.getInstance(this).createNewPost(json, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                    creatPostHandler(result);
                }

                @Override
                public void onLoginSuccess(Boolean result, User user) {

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

                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {

                }

            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handle the result from server after sending create new post request.
     * @param result
     */
    private void creatPostHandler(Boolean result) {

        if (result) {
            // if success, pop up the previous fragment.
            if (tabhost.getmFragmentManager().getBackStackEntryCount() > 0) {
                tabhost.getmFragmentManager().popBackStack();
                tabhost.getBorderLine().setVisibility(View.VISIBLE);
                anim.slideToTop(tabhost.getBorderLine(),tabhost.getTabWidget().getHeight());

                // show the tab bar
                for (int i = 0; i < 5; i++) {
                    TabWidget tab = tabhost.getTabWidget();
                    tab.setVisibility(View.VISIBLE);
                    anim.slideToTop(tab,0);
                }
            }
        } else {
            // make toast if failed.
            Toast.makeText(getApplicationContext(), "Create Post Failed", Toast.LENGTH_SHORT);
        }
    }

    /*
        Recieve pid from fragment
     */
    @Override
    public void sendID(String msg, Integer id) throws JSONException {
    }

    /*
        Recieve path from new post fragment 1
     */
    @Override
    public void sendPath(String path) {
        this.path=path;
    }


    // ---------------------------------------------------
    // Helper Functions
    // ---------------------------------------------------

    /**
     * Hide tab bar when user make new post
     */
    public void tabBarHide() {
        // Hide the tab bar divider
        anim.slideToBottom(tabhost.getBorderLine(),tabhost.getTabWidget().getHeight());
        tabhost.getBorderLine().setVisibility(View.GONE);
        Log.i(TAG, "height: " + tabhost.getTabWidget().getHeight());

        // Hide each tab widget in the tab bar
        for (int i = 0; i < 5; i++) {
            TabWidget tab = tabhost.getTabWidget();
            anim.slideToBottom(tab,0);
            tab.setVisibility(View.GONE);
        }

        // Do the tab.clearAnimation after animation finishes.
        // if doesn't clear, tab is still clickable which will cause bugs
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                for (int i = 0; i < 5; i++) {
                    TabWidget tab = tabhost.getTabWidget();
                    tab.clearAnimation();
                }
            }
        }, 300);

    }

}
