package com.example.notesmitra;


public class FirebaseModel {
    private String title;
    private String content;
    private String date;

    public FirebaseModel(){}

    public FirebaseModel(String title, String content, String Date) {
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {return date;}

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setDate(String date) {this.date = date;}
}
