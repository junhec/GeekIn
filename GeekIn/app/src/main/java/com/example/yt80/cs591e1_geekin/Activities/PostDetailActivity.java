package com.example.yt80.cs591e1_geekin.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.yt80.cs591e1_geekin.Common.Anim;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.TabFragmentHost;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Fragments.ProfileFrag;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Utils.ImageUtil;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;
import com.example.yt80.cs591e1_geekin.Views.CircleImageView;
import com.example.yt80.cs591e1_geekin.Views.CommentView;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.sackcentury.shinebuttonlib.ShineButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by haotianwu on 5/1/17.
 */

public class PostDetailActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, SwipyRefreshLayout.OnRefreshListener{
    private TextView usernameView;
    private CircleImageView userPicView;
    private TextView postTitleView;
    private SliderLayout slider;
    private ShineButton starButton;
    private ImageView commentIconView;
    private TextView starNumView;
    private TextView commentNumView;
    private ExpandableTextView postContentView;
    private int width;
    private TextView noCommentView;
    private ArrayList<Integer> cIDs = new ArrayList<>();;

    // layout
    private LinearLayout.LayoutParams iconLayout;
    private LinearLayout.LayoutParams numLayout;
    private SwipyRefreshLayout refreshLayout;

    // Animation class
    private Anim anim = new Anim();

    // Post ID
    private int pID;
    private com.example.yt80.cs591e1_geekin.Common.PostDetail postDetail;

    // The account owner's email
    private String accountEmail;

    private static final int DISMISS_TIMEOUT = 1000;
    private final String TAG = "Empty Stack";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail_frag);
        getWindow().setStatusBarColor(getResources().getColor(R.color.primary));
        accountEmail  = getIntent().getStringExtra("accountEmail");
        pID = Integer.parseInt(getIntent().getStringExtra("pid"));
        usernameView = (TextView) findViewById(R.id.user_name);
        userPicView = (CircleImageView) findViewById(R.id.user_pic);
        starNumView = (TextView) findViewById(R.id.star_number);
        commentNumView = (TextView) findViewById(R.id.comment_number);
        commentIconView = (ImageView) findViewById(R.id.comment_icon);
        postTitleView = (TextView) findViewById(R.id.post_title);
        postContentView = (ExpandableTextView) findViewById(R.id.post_description);
        refreshLayout = (SwipyRefreshLayout) findViewById(R.id.refresh);
        noCommentView = (TextView) findViewById(R.id.no_comment);
        refreshLayout.setOnRefreshListener(this);

        // Send retrieve post detail request to server
        try {
            retrieveDetail();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    // ---------------------------------------------------
    // Layout Set Up Functions
    // ---------------------------------------------------

    /**
     * Add post content
     * @param content
     */
    private void setPostContent(String content) {
        postContentView.setText(content);
    }

    /**
     * Add user pic and user name on the top of the screen
     * @param accountEmail
     * @param userName
     * @param userPic
     */
    private void setUserInfo(final String accountEmail, String userName, Bitmap userPic) {
        usernameView.setText(userName);
        userPicView.setImageBitmap(userPic);
        userPicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(PostDetailActivity.this,ProfileActivity.class);
                profileIntent.putExtra("postOwnerEmail", postDetail.getUserEmail());
                profileIntent.putExtra("accountEmail",accountEmail);
                startActivity(profileIntent);
            }
        });

    }

    /**
     * Add star number view to layout
     * @param starNum
     */
    private void setStarNumber(int starNum) {
        starNumView.setText(starNum + " Stars");
        starNumView.setLayoutParams(numLayout);
    }

    /**
     * Add comment number view to layout
     * @param commentNum
     */
    private void setCommentNumber(int commentNum) {
        commentNumView.setText(commentNum + " Comments");
        commentNumView.setLayoutParams(numLayout);
    }

    /**
     * Set up the Comment icon.
     When click on the icon, a dialog will appear.
     User can input their comments.
     Click "Comment" to send comments to server, "Cancel" to dismiss the dialog.
     */
    private void setCommentIcon() {
        commentIconView.setLayoutParams(iconLayout);
        commentIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final MaterialDialog dialog = new MaterialDialog.Builder(PostDetailActivity.this)
                        .title("Comment")
                        .titleColorRes(R.color.primary)
                        .positiveColorRes(R.color.primary)
                        .negativeColorRes(R.color.primary)
                        .widgetColorRes(R.color.primary)
                        .input("Add Comment", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                            }
                        }).show();
                dialog.setActionButton(DialogAction.POSITIVE, "Comment");
                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG,dialog.getInputEditText().getText().toString());
                        if (dialog.getInputEditText().getText().toString().toString().length() == 0) {
                            //Toast.makeText(getActivity(), "Comment is empty", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                            try {
                                sendComment(dialog.getInputEditText().getText().toString().toString());
                                // the number in commentNumView is a string.
                                // convert it to int, plus 1 and ten convert back to string and setText()
                                commentNumView.setText(Integer.toString(Integer.parseInt(commentNumView.getText().toString().split(" ")[0]) + 1) + " comments");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i(TAG,dialog.getInputEditText().getText().toString());
                        }

                    }
                });
                dialog.setActionButton(DialogAction.NEGATIVE, "Cancel");
                dialog.getActionButton(DialogAction.NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    /**
     * Add post title to layout
     * @param title
     */
    private void setPostTitle(String title) {
        postTitleView.setText(title);
    }

    /**
     * Add slider to layout
     * @param photos
     */
    private void setSlider(ArrayList<String> photos) {
        slider = (SliderLayout) refreshLayout.findViewById(R.id.slider);
        for(String photo : photos){
            // initialize a SliderLayout
            String image_URL = "http://" + NetworkUtil.getInstance(this).getIP() + ":8000" + "/getPostImage" + photo;
            DefaultSliderView SliderView = new DefaultSliderView(this);
            SliderView.image(image_URL).setScaleType(BaseSliderView.ScaleType.CenterCrop);
            slider.addSlider(SliderView);
            slider.setPresetTransformer(SliderLayout.Transformer.Default);
            slider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            slider.stopAutoCycle();
        }
    }

    /**
     * Add star button to layout
     */
    private void setStarButton() {
        starButton = (ShineButton) refreshLayout.findViewById(R.id.star);
        starButton.setLayoutParams(iconLayout);
        starButton = (ShineButton) refreshLayout.findViewById(R.id.star);
        starButton.init(this);
        // If current user already clicked before, set un clickable
        if (postDetail.getStarGiven() > 0){
            starButton.setChecked(true);
            starButton.setClickable(false);
        } else {
            starButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendStar();
                        starNumView.setText(Integer.toString(Integer.parseInt(starNumView.getText().toString().split(" ")[0]) + 1) + " Stars");
                        starButton.setClickable(false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send retrieve post detail request to server
     * @throws JSONException
     */
    private void retrieveDetail() throws JSONException {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels/12;
        iconLayout = new LinearLayout.LayoutParams(width, width);
        numLayout = new LinearLayout.LayoutParams(width * 5,width);
        try{
            JSONObject postID = new JSONObject();
            postID.put("pid", pID);
            // user account email to check whether the user has given start to the post
            postID.put("email", accountEmail);
            NetworkUtil.getInstance(this).retrievePostDetail(postID, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }
                @Override
                public void onLoginSuccess(Boolean result, User user) {
                }
                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {
                }
                @Override
                public void onGetPDetailSuccess(Boolean result, com.example.yt80.cs591e1_geekin.Common.PostDetail pDetail) {
                    pDetailResultHandler(result, pDetail);
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

        retrieveComment();
    }

    /**
     * Handle the result from server after getting post details from server.
     * @param result
     * @param pDetail
     */
    private void pDetailResultHandler(Boolean result, com.example.yt80.cs591e1_geekin.Common.PostDetail pDetail) {
        if (result) {
            postDetail = pDetail;
            setUserInfo(accountEmail,postDetail.getUserName(),postDetail.getUserPic());
            setSlider(postDetail.getPhotos());

            setStarNumber(postDetail.getStarNum());
            setCommentNumber(postDetail.getCommentNum());
            setCommentIcon();
            setStarButton();

            setPostTitle(postDetail.getTitle());
            setPostContent(postDetail.getContent());
        } else {

        }
    }
    /**
     * Send retrieveComment request to server.
     * @throws JSONException
     */
    private void retrieveComment() throws JSONException {
        try{
            JSONObject postInfo = new JSONObject();
            postInfo.put("pid", pID);
            if (!cIDs.isEmpty()) {
                postInfo.put("cids",cIDs);
            }
            NetworkUtil.getInstance(this).retrieveComments(postInfo, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }
                @Override
                public void onLoginSuccess(Boolean result, User user) {
                }
                @Override
                public void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts) {
                }
                @Override
                public void onGetPDetailSuccess(Boolean result, com.example.yt80.cs591e1_geekin.Common.PostDetail pDetail) {
                }

                @Override
                public void onGetCommentsSuccess(Boolean result, ArrayList<Comment> commentList) {
                    getCommentResultHandler(result, commentList);
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

    /**
     * Handle the result from server after getting user list from server.
     * If no comment, show "---- No Comments -----"
     * @param result
     * @param commentList
     */
    private void getCommentResultHandler(Boolean result, ArrayList<Comment> commentList) {
        if (result) {
            if (commentList.isEmpty() && cIDs.isEmpty()){
                if (noCommentView.getVisibility() == View.GONE) {
                    noCommentView.setVisibility(View.VISIBLE);
                }
            } else {
                if (noCommentView.getVisibility() == View.VISIBLE) {
                    noCommentView.setVisibility(View.GONE);
                }
                for (Comment c : commentList) {
                    cIDs.add(c.getcID());
                    builtComment(c, width);
                }
            }
        } else {
            Toast.makeText(this, "Something wrong",Toast.LENGTH_SHORT);
        }
    }

    /**
     * Send comment to server.
     * @param comment
     * @throws JSONException
     */
    private void sendComment(String comment) throws JSONException {
        try{
            JSONObject commentJson = new JSONObject();
            commentJson.put("pid", pID);
            commentJson.put("email",accountEmail);
            commentJson.put("content",comment);
            NetworkUtil.getInstance(this).sendComment(commentJson);
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Send add star to server
     * @throws JSONException
     */
    private void sendStar() throws JSONException {
        try{
            JSONObject starJson = new JSONObject();
            starJson.put("pid", pID);
            starJson.put("email",accountEmail);
            NetworkUtil.getInstance(this).addStar(starJson);
        }catch(Exception e){
            throw e;
        }
    }

    /**
     * Build a comment view. Add to the layout.
     * @param c
     * @param width
     */
    private void builtComment(final Comment c, int width) {
        LinearLayout detail = (LinearLayout) refreshLayout.findViewById(R.id.detail);
        CommentView tv = new CommentView(this, ImageUtil.getScaledBitmap(c.getUserPic(),width,width),c.getUserName(),c.getComment(),c.getTime());
        // Make each comment clickable. If user click, move to user profile activity.
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(PostDetailActivity.this,ProfileActivity.class);
                profileIntent.putExtra("postOwnerEmail", c.getUserEmail());
                profileIntent.putExtra("accountEmail",accountEmail);
                startActivity(profileIntent);
            }
        });
        detail.addView(tv);
    }


    // ---------------------------------------------------
    // Slider Functions
    // ---------------------------------------------------

    @Override
    public void onSliderClick(BaseSliderView slider) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.e("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle()
        // on the slider before activity or fragment is destroyed
        slider.stopAutoCycle();
        super.onStop();
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
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Hide the refresh after 2sec
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                    }
                });
            }
        }, DISMISS_TIMEOUT);
    }

    /**
     * Load more comments from database
     * @throws JSONException
     */
    private void doLoad() throws JSONException {
        retrieveComment();
    }

}
