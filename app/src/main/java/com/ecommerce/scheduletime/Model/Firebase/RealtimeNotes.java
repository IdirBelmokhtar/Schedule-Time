package com.ecommerce.scheduletime.Model.Firebase;

public class RealtimeNotes {
    String _id, _id_, note_title, note_description, note_time;

    public RealtimeNotes(){

    }

    public RealtimeNotes(String _id, String _id_, String note_title, String note_description, String note_time) {
        this._id = _id;
        this._id_ = _id_;
        this.note_title = note_title;
        this.note_description = note_description;
        this.note_time = note_time;
    }

    public String get_id() {
        return _id;
    }

    public String get_id_() {
        return _id_;
    }

    public String getNote_title() {
        return note_title;
    }

    public String getNote_description() {
        return note_description;
    }

    public String getNote_time() {
        return note_time;
    }
}
