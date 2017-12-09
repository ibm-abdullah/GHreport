package com.braimahabdullah.ghreport;

import android.net.Uri;

import java.net.URL;

/**
 * Created by Ibrahim-Abdullah on 12/5/2017.
 */

public class Post {
    String filename;
    String filePath;
    String downlableUri;
    String issue;


    public Post(String filename, String filePath, String downlableUri,String issue) {
        this.filename = filename;
        this.filePath = filePath;
        this.downlableUri = downlableUri;
        this.issue = issue;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getDownlableUri() {
        return downlableUri;
    }

    public void setDownlableUri(String downlableUri) {
        this.downlableUri = downlableUri;
    }
}
