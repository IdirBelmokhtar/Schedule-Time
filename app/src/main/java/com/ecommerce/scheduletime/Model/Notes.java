package com.ecommerce.scheduletime.Model;


public class Notes {
    private String id, _id_,  title, description, time;

    public Notes(){

    }

    public Notes(String id, String _id_, String title, String date, String time) {
        this.id = id;
        this._id_ = _id_;
        this.title = title;
        this.description = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String get_id_() {
        return _id_;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}


