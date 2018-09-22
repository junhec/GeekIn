package com.example.yt80.cs591e1_geekin.Common;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by haotianwu on 4/27/17.
 */

public class User implements Serializable {
    private String userEmail;
    private String userName;
    private String userPic;
    private int follower;
    private int following;
    private int postNum;
    private ArrayList<String> tagList = new ArrayList<>();
    private boolean isFollowing;

    /**
     * Constructor
     */
    public User() {
    }

    /**
     * Constructor
     * @param email
     * @param name
     * @param pic
     * @param follower
     * @param following
     * @param postNum
     * @param tagList
     * @param isFollowing
     */
    public User(String email, String name, String pic, int follower, int following, int postNum, ArrayList<String> tagList, boolean isFollowing) {
        this.userEmail = email;
        this.userName = name;
        this.userPic = pic;
        this.follower = follower;
        this.following = following;
        this.postNum = postNum;
        this.tagList = tagList;
        this.isFollowing = isFollowing;
    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public int getFollower() {
        return follower;
    }

    public int getFollowing() {
        return following;
    }

    public int getPostNum() {
        return postNum;
    }

    public ArrayList<String> getTagList() {
        return tagList;
    }

    public boolean getIsFollowing() {
        return isFollowing;
    }

}
