package com.example.yt80.cs591e1_geekin.Common;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Post Detail Class.
 */

public class PostDetail {
    private int pID;
    private String userEmail;
    private String userName;
    private Bitmap userPic;
    private String title;
    private String content;
    private ArrayList<String> photos;
    private int commentNum;
    private int starNum;
    private int starGiven;

    /**
     * Constructor
     */
    public PostDetail() {
    }

    /**
     * Constructor
     * @param pID
     * @param userEmail
     * @param userName
     * @param userPic
     * @param title
     * @param content
     * @param photos
     * @param commentNum
     * @param starNum
     * @param starGiven
     */
    public PostDetail(int pID, String userEmail, String userName, Bitmap userPic, String title, String content, ArrayList<String> photos, int commentNum, int starNum, int starGiven) {
        this.pID = pID;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPic = userPic;
        this.title = title;
        this.content = content;
        this.photos = photos;
        this.commentNum = commentNum;
        this.starNum = starNum;
        this.starGiven = starGiven;
    }

    // ---------------------------------------------------
    // Getter Functions
    // ---------------------------------------------------

    public int getpID() {
        return pID;
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

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public ArrayList<String> getPhotos() {
        return photos;
    }

    public int getCommentNum() {
        return commentNum;
    }

    public int getStarNum() {
        return starNum;
    }

    public int getStarGiven() {
        return starGiven;
    }
}
