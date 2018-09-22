package com.example.yt80.cs591e1_geekin.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yt80.cs591e1_geekin.Adapters.SignUpAdapter;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.R;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.Utils.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SignUpTag extends AppCompatActivity {
    public  final static String SER_KEY = "com.example.yt80.cs591e1_geekin";
    private String userEmail;
    private GridView gridview;
    private TextView txtview;
    private Button btn_signup;
    private SignUpAdapter adapter;
    private NetworkUtil utilRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        utilRequest = new NetworkUtil(getApplicationContext());
        userEmail = getIntent().getExtras().getString("User_Email");
        txtview=(TextView)findViewById(R.id.signup_txt_num);
        gridview=(GridView)findViewById(R.id.signup_gridview);
        adapter=new SignUpAdapter(this,txtview);
        gridview.setAdapter(adapter);
        btn_signup=(Button)findViewById(R.id.done);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addTags();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // ---------------------------------------------------
    // Server Communication Functions
    // ---------------------------------------------------

    /**
     * Send add tags request to server
     * @throws JSONException
     */
    private void addTags() throws JSONException {
        final ArrayList<String> tagList = getTagList();
        if (tagList.size() == 0) {
            Toast.makeText(getApplicationContext(),"Please select you interest area", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            JSONObject tags = new JSONObject();
            tags.put("email", userEmail);
            tags.put("tags",tagList.toString());
            utilRequest.addTags(tags, new NetworkUtil.VolleyCallback() {
                @Override
                public void onSuccess(Boolean result) {
                }

                @Override
                public void onLoginSuccess(Boolean result, User user) {
                    addTagResultHandler(result,user);
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

    /**
     * Handle the result from server after sending add tag request.
     * @param result
     * @param user
     */
    private void addTagResultHandler(boolean result, User user) {
        if (result) {
            Intent intent=new Intent(this,Main.class);
            //intent.putExtra("user", user);
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(SER_KEY,user);
            intent.putExtras(mBundle);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Add Tag Failed", Toast.LENGTH_LONG).show();
        }
    }


    // ---------------------------------------------------
    // Helper Functions
    // ---------------------------------------------------

    private ArrayList<String> getTagList() {
        return adapter.getChosenTag();
    }

}
