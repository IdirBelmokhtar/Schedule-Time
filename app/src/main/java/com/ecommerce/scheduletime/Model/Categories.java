package com.ecommerce.scheduletime.Model;


public class Categories {
    private String id, _id_, name, color_Ref, deleted;

    public Categories(){

    }

    public Categories(String id, String _id_, String name, String color_ref, String deleted) {
        this.id = id;
        this._id_ = _id_;
        this.name = name;
        color_Ref = color_ref;
        this.deleted = deleted;
    }

    public String getId() {
        return id;
    }

    public String getId_() {
        return _id_;
    }

    public String getName() {
        return name;
    }

    public String getColor_Ref() {
        return color_Ref;
    }

    public String getDeleted() {
        return deleted;
    }
}


