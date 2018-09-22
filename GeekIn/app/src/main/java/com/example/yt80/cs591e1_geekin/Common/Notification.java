package com.example.yt80.cs591e1_geekin.Common;

import android.graphics.Bitmap;

/**
 * Notification class.
 */

public class Notification {
    private String userEmail;
    private String userName;
    private Bitmap userPic;
    private String type;
    private int pID;
    private Bitmap postCover;

    /**
     * Constructor
     */
    public Notification() {
    }

    /**
     * Constructor
     * @param userEmail
     * @param userName
     * @param userPic
     * @param type
     * @param pID
     * @param postCover
     */
    public Notification(String userEmail, String userName, Bitmap userPic, String type, int pID, Bitmap postCover) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPic = userPic;
        this.type = type;
        this.pID = pID;
        this.postCover = postCover;
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

    public Bitmap getUserPic() {
        return userPic;
    }

    public String getType() {
        return type;
    }

    public int getpID() {
        return pID;
    }

    public Bitmap getPostCover() {
        return postCover;
    }
}
