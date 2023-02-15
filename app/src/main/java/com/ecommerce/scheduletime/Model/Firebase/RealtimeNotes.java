package com.ecommerce.scheduletime.Model.Firebase;

public class RealtimeNotes {
    String _id, note_title, note_description, note_time;

    public RealtimeNotes(){

    }

    public RealtimeNotes(String _id, String note_title, String note_description, String note_time) {
        this._id = _id;
        this.note_title = note_title;
        this.note_description = note_description;
        this.note_time = note_time;
    }

    public String get_id() {
        return _id;
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
