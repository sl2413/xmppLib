package com.shenl.xmpplibrary.bean;

public class Msg {

    private String name;
    private String date;
    private String title;
    private String myself;
    private String imgPath;

    public Msg(String date, String name, String title, String myself) {
        this.date = date;
        this.name = name;
        this.title = title;
        this.myself = myself;
    }

    public Msg(String date, String name, String title, String myself, String img_path) {
        this.date = date;
        this.name = name;
        this.title = title;
        this.myself = myself;
        this.imgPath = img_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMyself() {
        return myself;
    }

    public void setMyself(String myself) {
        this.myself = myself;
    }

    public String getImg_path() {
        return imgPath;
    }

    public void setImg_path(String img_path) {
        this.imgPath = img_path;
    }
}
