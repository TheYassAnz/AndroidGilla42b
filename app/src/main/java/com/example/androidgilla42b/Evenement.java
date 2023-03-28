package com.example.androidgilla42b;

public class Evenement {
    private String mId;
    private String mTitle;
    private String mDescription;
    private String mDate_time;

    // Constructeur de la classe Evenement :
    public Evenement(String id, String title, String description, String date_time) {
        mId = id;
        mTitle = title;
        mDescription = description;
        mDate_time = date_time;
    }

    public String getId() {
        return mId;
    }
    public String getTitle() {
        return mTitle;
    }
    public String getDescription() {
        return mDescription;
    }
    public String getDateTime() {
        return mDate_time;
    }

    private static int lastEvenementId = 0;

}