package com.example.yt80.cs591e1_geekin.Common;

import android.graphics.Bitmap;

/**
 * Created by haotianwu on 4/28/17.
 */

public class BriefPost {
    private int pID;
    private String userName;
    private Bitmap userPic;
    private String title;
    private Bitmap postCover;

    /**
     * Constructor
     */
    public BriefPost() {

    }

    /**
     * Constructor
     */
    public BriefPost(int pID, String userName, Bitmap userPic, String title, Bitmap postCover) {
        this.pID = pID;
        this.userName = userName;
        this.userPic = userPic;
        this.title = title;
        this.postCover = postCover;
    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------
    public int getpID() {
        return pID;
    }

    public String getUserName() {
        return userName;
    }

    public Bitmap getUserPic() {
        return userPic;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getPostCover() {
        return postCover;
    }
}
