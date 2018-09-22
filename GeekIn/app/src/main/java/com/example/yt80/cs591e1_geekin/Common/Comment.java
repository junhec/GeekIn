package com.example.yt80.cs591e1_geekin.Common;

import android.graphics.Bitmap;

/**
 * Created by haotianwu on 4/28/17.
 */

public class Comment {
    private int cID;
    private String userEmail;
    private String userName;
    private Bitmap userPic;
    private String comment;
    private String time;

    /**
     * Constructor
     */
    public Comment() {
    }

    /**
     * Constructor
     * @param cID
     * @param userEmail
     * @param userName
     * @param userPic
     * @param comment
     * @param time
     */
    public Comment(Integer cID,String userEmail, String userName, Bitmap userPic, String comment, String time) {
        this.cID = cID;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPic = userPic;
        this.comment = comment;
        this.time = time;
    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------

    public int getcID() {
        return cID;
    }

    public String getTime() {
        return time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public Bitmap getUserPic() {
        return userPic;
    }

    public String getComment() {
        return comment;
    }
}
