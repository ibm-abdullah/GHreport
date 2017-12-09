package com.braimahabdullah.ghreport;

import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Ibrahim-Abdullah on 12/5/2017.
 */

public class Post {
    String title;
    String filename;
    String filePath;
    String downlableUri;
    String issue;
    double longitude;
    double latitude;




    public Post(String title,String filename, String filePath, String downlableUri,String issue) {
        this.title = title;
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

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
