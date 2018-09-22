package com.example.yt80.cs591e1_geekin.Utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.yt80.cs591e1_geekin.Common.BriefPost;
import com.example.yt80.cs591e1_geekin.Common.Comment;
import com.example.yt80.cs591e1_geekin.Common.Notification;
import com.example.yt80.cs591e1_geekin.Common.PostDetail;
import com.example.yt80.cs591e1_geekin.Common.User;
import com.example.yt80.cs591e1_geekin.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by yt80 on 2017/4/8.
 */

public class NetworkUtil {
    private static final String IP_ADDRESS="54.187.138.210";
    private static final String PORT="8000";
    private Context context;
    public Context current_context;
    private static RequestQueue queue;
    private static NetworkUtil Instance=null;

    private errorListener errorlistener;
    public boolean result;
    public String message = "";

    /**
     * Constructor
     * @param context
     */
    public NetworkUtil(Context context) {
        this.context=context;
        queue= Volley.newRequestQueue(context.getApplicationContext());
        errorlistener=new errorListener("",context);
    }

    /**
     * Instance contructor
     * @param context
     * @return
     */
    public static synchronized NetworkUtil getInstance(Context context){
        if(Instance==null){
            Instance=new NetworkUtil(context);

        }
        Instance.current_context=context;//
        return Instance;
    }

    /**
     * Send login request to server
     * @param jsonData
     * @param callback
     * @throws InterruptedException
     */
    public void Login(JSONObject jsonData,final VolleyCallback callback) throws InterruptedException {
        String function="login";
        errorlistener.info=function;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getBoolean("result") == true) {
                                result = true;
                                JSONObject userJson = new JSONObject(response.getString("user_info"));
                                User user = userIns(userJson);
                                //Toast.makeText(context.getApplicationContext(), "User Login sucsess" + user.getUserEmail(), Toast.LENGTH_LONG).show();
                                callback.onLoginSuccess(result, user);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "login fail!" + response.getString("message"), Toast.LENGTH_LONG).show();
                                result = false;
                                callback.onSuccess(result);
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(context.getApplicationContext(), "Fuck" + response.getString("message"), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(),error.getMessage()+",please try again.",Toast.LENGTH_SHORT).show();
                        callback.onSuccess(result);
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send get user profile request to server
     * @param jsonData
     * @param callback
     * @throws InterruptedException
     */
    public void getUserProfile(JSONObject jsonData,final VolleyCallback callback) throws InterruptedException {
        String function="getProfile";
        errorlistener.info=function;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getBoolean("result") == true) {
                                result = true;
                                JSONObject userJson = new JSONObject(response.getString("user_info"));
                                User user = userIns(userJson);
                                callback.onLoginSuccess(result, user);
                            } else {
                                Toast.makeText(context.getApplicationContext(), "login fail!", Toast.LENGTH_LONG).show();
                                result = false;
                                callback.onLoginSuccess(result,null);
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(context.getApplicationContext(), "Fuck" + response.getString("message"), Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(),error.getMessage()+",please try again.",Toast.LENGTH_SHORT).show();
                        callback.onSuccess(result);
                    }
                }
        );

        queue.add(jsObjRequest);

    }

    /**
     * Send sign up request to server
     * @param jsonData
     * @param callback
     */
    public void SignUp(JSONObject jsonData,final VolleyCallback callback){
        String function="createUser";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
            Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getBoolean("result")) {
                            result = true;
                            callback.onSuccess(result);
                        } else {
                            result = false;
                            message = response.getString("message");
                            //Toast.makeText(context,"hahahahah" + message,Toast.LENGTH_SHORT).show();
                            callback.onSuccess(result);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(),error.getMessage()+",please try again.",Toast.LENGTH_SHORT).show();
                    result = false;
                }
            }
        );

        queue.add(jsObjRequest);
    }

    /**
     * After sign up user name and password. Send user interested areas(tags) to the server.
     * @param jsonData
     * @param callback
     */
    public void addTags(JSONObject jsonData,final VolleyCallback callback) {
        String function = "addUserTags";
        errorlistener.info = function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
            //(Request.Method.GET, "http://10.0.2.2:8000/index?name="+"Wuhe"+"&password="+"0624", null , new Response.Listener<JSONObject>() {
            Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    //progressDialog.dismiss();
                    try {
                        if (response.getBoolean("result") == true) {
                            result = true;
                            JSONObject userJson = new JSONObject(response.getString("user_info"));
                            User user = userIns(userJson);
                            //Toast.makeText(context.getApplicationContext(), "User Login sucsess" + user.getUserEmail(), Toast.LENGTH_LONG).show();
                            callback.onLoginSuccess(result, user);
                        } else {
                            Toast.makeText(context.getApplicationContext(), "Add tags failed!" + response.getString("message"), Toast.LENGTH_LONG).show();
                            result = false;
                            callback.onSuccess(result);
                        }

                    } catch (JSONException e) {
                        //Toast.makeText(context.getApplicationContext(), "Fuck" + response.getString("message"), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                    result = false;
                }
            }
        );
        queue.add(jsObjRequest);
    }

    /**
     * Send create new post request to the server
     * @param jsonData
     * @param callback
     */
    public  void createNewPost(JSONObject jsonData, final VolleyCallback callback){
        String function="newPost";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getBoolean("result")) {
                                result = true;
                                callback.onSuccess(result);
                            } else {
                                result = false;
                                message = response.getString("message");
                                //Toast.makeText(context.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                callback.onSuccess(result);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send create new post request to the server
     * @param function
     * @param jsonData
     * @param callback
     */
    public  void retrievePost(String function, JSONObject jsonData, final VolleyCallback callback){
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getBoolean("result")) {
                                result = true;
                                JSONArray post = response.getJSONArray("data");
                                ArrayList<BriefPost> posts = new ArrayList<>();

                                for(int i = 0; i < post.length(); i++) {
                                    BriefPost briefPost = createBriefPost(post.getJSONObject(i));
                                    posts.add(briefPost);
                                }
                                callback.onRetrieveSuccess(result, posts);
                            } else {
                                result = false;
                                message = response.getString("message");
                                Log.i("XXXXXXXXXX",message);
                                callback.onSuccess(result);
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(context.getApplicationContext(), "caonima2222222", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send retrieve post detail request to server
     * @param jsonData
     * @param callback
     */
    public  void retrievePostDetail(JSONObject jsonData, final VolleyCallback callback){
        String function="getPostDetail";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getBoolean("result")) {
                                result = true;
                                //Toast.makeText(context,"Fuck here", Toast.LENGTH_SHORT).show();
                                //Toast.makeText(context,"PID:::::" , Toast.LENGTH_SHORT).show();
                                JSONObject postDetail = response.getJSONObject("data");
                                PostDetail pDetail = postDetailIns(postDetail);
                                callback.onGetPDetailSuccess(result, pDetail);
                            } else {
                                result = false;
                                message = response.getString("message");
                                //Toast.makeText(context.getApplicationContext(), "caonima", Toast.LENGTH_SHORT).show();
                                callback.onGetPDetailSuccess(result, null);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(context.getApplicationContext(), "retrievePostFail", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send get user list request to server
     * @param jsonData
     * @param callback
     */
    public void getUserList(String function,JSONObject jsonData, final VolleyCallback callback){
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("result")) {
                                result = true;
                                JSONArray userListJson = response.getJSONArray("data");
                                ArrayList<User> userList = createUserList(userListJson);
                                callback.onGetUserListSuccess(result, userList);
                            } else {
                                result = false;
                                message = response.getString("message");
                                //TODO
                                //callback.onGetUsersSuccess(result,new ArrayList<User>());
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(context.getApplicationContext(), "caonimaXXXXX", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send comment to server
     * @param jsonData
     */
    public void sendComment(JSONObject jsonData){
        String function="addComment";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getBoolean("result")) {
                                result = true;
                                //Toast.makeText(context,"Comment sucess" , Toast.LENGTH_SHORT).show();
                                //callback.onGetPDetailSuccess(result, pDetail);
                            } else {
                                result = false;
                                message = response.getString("message");
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context.getApplicationContext(), "send Comment fail", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send add start request to server
     * @param jsonData
     */
    public void addStar(JSONObject jsonData){
        String function="addLike";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getBoolean("result")) {
                                result = true;
                            } else {
                                result = false;
                                message = response.getString("message");
                            }
                        } catch (JSONException e) {
                            Toast.makeText(context.getApplicationContext(), "Add Star Fail", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send retrieve comments request to server
     * @param jsonData
     * @param callback
     */
    public  void retrieveComments(JSONObject jsonData, final VolleyCallback callback) {
        String function="getComments";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getBoolean("result")) {
                                result = true;
                                JSONArray commentListJson = response.getJSONArray("data");
                                ArrayList<Comment> commentList = createCommentList(commentListJson);
                                callback.onGetCommentsSuccess(result, commentList);
                            } else {
                                result = false;
                                message = response.getString("message");
                                callback.onGetCommentsSuccess(result, null);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(context.getApplicationContext(), "retrieve Comment fail", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send retrieve notification request to server
     * @param jsonData
     * @param callback
     */
    public void retrieveNotification(JSONObject jsonData, final VolleyCallback callback) {
        String function="getNotifications";
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getBoolean("result")) {
                                result = true;
                                JSONArray likeListJson = response.getJSONArray("likes");
                                JSONArray commentListJson = response.getJSONArray("comments");
                                ArrayList<Notification> notificationList = createNotifyList(likeListJson,commentListJson);
                                //Toast.makeText(context.getApplicationContext(), "caonima2", Toast.LENGTH_SHORT).show();
                                callback.onGetNotifySuccess(result, notificationList);
                            } else {
                                result = false;
                                message = response.getString("message");
                                //TODO
                                //callback.onGetUsersSuccess(result);
                            }

                        } catch (JSONException e) {
                            //Toast.makeText(context.getApplicationContext(), "aaaaaaa retrieve Notification fail", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    /**
     * Send follow / unfollow request to server
     * @param function
     * @param jsonData
     * @param callback
     */
    public void follow(String function, JSONObject jsonData, final VolleyCallback callback) {
        errorlistener.info=function;
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + IP_ADDRESS + ":" + PORT + "/" + function + "/", jsonData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getBoolean("result")) {
                                result = true;
                                callback.onSuccess(result);
                            } else {
                                result = false;
                                message = response.getString("message");
                                //TODO
                                callback.onSuccess(result);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(context.getApplicationContext(), "Follow Failed", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage() + ",please try again.", Toast.LENGTH_SHORT).show();
                        result = false;
                    }
                }
        );

        queue.add(jsObjRequest);
    }

    // ---------------------------------------------------
    // Swipy Refresh Layout Functions
    // ---------------------------------------------------


    private BriefPost createBriefPost(JSONObject post) throws JSONException {
        int pID = (int) post.get("pid");
        String title = post.getString("title");
        String userName = post.getString("username");
        Bitmap userPic = ImageUtil.StringToBitMap(post.getString("user_photo"));
        Bitmap postCover = ImageUtil.StringToBitMap(post.getString("post_cover"));
        BriefPost briefPost = new BriefPost(pID, userName, userPic, title, postCover);
        return briefPost;
    }

    /**
     * Generate a user object
     * @param userJson
     * @return
     * @throws JSONException
     */
    private User userIns(JSONObject userJson) throws JSONException {
        String userEmail = userJson.getString("user_email");
        String userName = userJson.getString("user_name");
        String userPic = userJson.getString("photo");
        int follower = userJson.getInt("follower_count");
        int following = userJson.getInt("followee_count");
        int postNum = userJson.getInt("post_count");
        boolean isFollowing;
        if (!userJson.getString("isFollowing").equals("None")) {
            if (userJson.getInt("isFollowing") == 1) {
                isFollowing = true;
            } else {
                isFollowing = false;
            }
        } else {
            isFollowing = false;
        }
        User user = new User(userEmail, userName, userPic, follower, following, postNum, null, isFollowing);
        return user;
    }

    /**
     * Generate post detail object
     * @param postDetailJson
     * @return
     * @throws JSONException
     */
    private PostDetail postDetailIns(JSONObject postDetailJson) throws JSONException {
        int pID = postDetailJson.getInt("pid");
        String userEmail = postDetailJson.getString("user_email");
        String userName = postDetailJson.getString("user_name");
        Bitmap userPic = ImageUtil.StringToBitMap(postDetailJson.getString("user_photo"));
        int starGiven = postDetailJson.getInt("isLiked");
        int commentNum = postDetailJson.getInt("comment_count");
        int starNum = postDetailJson.getInt("star_count");
        String title = postDetailJson.getString("title");
        String content = postDetailJson.getString("content");
        ArrayList<String> photos = new ArrayList<>();
        JSONArray photoArray = postDetailJson.getJSONArray("photos");
        for (int i = 0; i < photoArray.length(); i++) {
            photos.add(photoArray.getString(i));
        }

        PostDetail pDetail = new PostDetail(pID, userEmail, userName, userPic, title, content, photos, commentNum, starNum, starGiven);
        return pDetail;
    }

    /**
     * Generate user list
     * @param userListJson
     * @return
     * @throws JSONException
     */
    private ArrayList<User> createUserList(JSONArray userListJson) throws JSONException {
        ArrayList<User> userList = new ArrayList<>();
        for (int i = 0; i < userListJson.length(); i++) {
            JSONObject userJson = userListJson.getJSONObject(i);
            String userName = userJson.getString("username");
            String userEmail = userJson.getString("email");
            String userPic = userJson.getString("user_photo");
            User user = new User(userEmail, userName, userPic, 0, 0, 0, null, false);
            userList.add(user);
        }

        return userList;
    }

    /**
     * Generate comment list
     * @param commentListJson
     * @return
     * @throws JSONException
     */
    private ArrayList<Comment> createCommentList(JSONArray commentListJson) throws JSONException {
        ArrayList<Comment> commentList = new ArrayList<>();
        for (int i = 0; i < commentListJson.length(); i++) {
            JSONObject commentJson = commentListJson.getJSONObject(i);
            int cID = commentJson.getInt("cid");
            String userEmail = commentJson.getString("email");
            String userName = commentJson.getString("username");
            Bitmap userPic = ImageUtil.StringToBitMap(commentJson.getString("user_photo"));
            String comment = commentJson.getString("content");
            String time = commentJson.getString("date");
            Comment newComment = new Comment(cID,userEmail,userName,userPic,comment,time);
            commentList.add(newComment);
        }
        return commentList;
    }

    /**
     * Generate a notification list
     * @param likeListJson
     * @param commentListJson
     * @return
     * @throws JSONException
     */
    private ArrayList<Notification> createNotifyList(JSONArray likeListJson, JSONArray commentListJson) throws JSONException {
        ArrayList<Notification> notificationList = new ArrayList<>();
        for (int i = 0; i < likeListJson.length(); i++) {
            JSONObject notificationJson = likeListJson.getJSONObject(i);
            String userEmail = notificationJson.getString("email");
            String userName = notificationJson.getString("username");
            Bitmap userPic = ImageUtil.StringToBitMap(notificationJson.getString("user_photo"));
            String type = "like";
            int pID = notificationJson.getInt("pid");
            Bitmap postCover = ImageUtil.StringToBitMap(notificationJson.getString("post_cover"));
            Notification newNotification = new Notification(userEmail,userName,userPic,type,pID,postCover);
            notificationList.add(newNotification);
        }

        for (int i = 0; i < commentListJson.length(); i++) {
            JSONObject notificationJson = commentListJson.getJSONObject(i);
            String userEmail = notificationJson.getString("email");
            String userName = notificationJson.getString("username");
            Bitmap userPic = ImageUtil.StringToBitMap(notificationJson.getString("user_photo"));
            String type = "Comment";
            int pID = notificationJson.getInt("pid");
            Bitmap postCover = ImageUtil.StringToBitMap(notificationJson.getString("post_cover"));
            Notification newNotification = new Notification(userEmail,userName,userPic,type,pID,postCover);
            notificationList.add(newNotification);
        }
        return notificationList;
    }

    /**
     * Wait for the response from server.
     Implemented where the above request function has been called.
     */
    public interface VolleyCallback{
        void onSuccess(Boolean result);
        void onLoginSuccess(Boolean result, User user);
        void onRetrieveSuccess(Boolean result, ArrayList<BriefPost> posts);
        void onGetPDetailSuccess(Boolean result, PostDetail pDetail);
        void onGetCommentsSuccess(Boolean result, ArrayList<Comment> commentList);
        void onGetNotifySuccess(Boolean result,ArrayList<Notification> notificationList);
        void onGetUserListSuccess(Boolean result, ArrayList<User> userList);
    }

    public String getIP() {
        return IP_ADDRESS;
    }
}

/*

 */
class errorListener implements Response.ErrorListener{
    public  String info="";
    public  Context context;
    errorListener(String info,Context context){
        this.info=info;
        this.context=context;
    }

    // Make a toast when receive error.
    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(context.getApplicationContext(),error.getMessage()+info+",please try again.",Toast.LENGTH_SHORT).show();
    }
}