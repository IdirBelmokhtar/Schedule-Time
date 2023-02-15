package com.ecommerce.scheduletime.Model.Firebase;

public class RealtimeCategory {
    String _id, _id_, category_name, category_color_Ref, category_deleted;

    public RealtimeCategory(){

    }

    public RealtimeCategory(String _id, String _id_, String category_name, String category_color_Ref, String category_deleted) {
        this._id = _id;
        this._id_ = _id_;
        this.category_name = category_name;
        this.category_color_Ref = category_color_Ref;
        this.category_deleted = category_deleted;
    }

    public String get_id() {
        return _id;
    }

    public String get_id_() {
        return _id_;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getCategory_color_Ref() {
        return category_color_Ref;
    }

    public String getCategory_deleted() {
        return category_deleted;
    }
}
