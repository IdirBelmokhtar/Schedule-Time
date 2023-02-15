package com.ecommerce.scheduletime.Model.Firebase;

public class RealtimeTasks {
    String _id, _id_, task_date, task_title, task_description, task_priority, task_category, task_time, task_done, task_reminder;

    public RealtimeTasks(){

    }

    public RealtimeTasks(String _id, String _id_, String task_date, String task_title, String task_description, String task_priority, String task_category, String task_time, String task_done, String task_reminder) {
        this._id = _id;
        this._id_ = _id_;
        this.task_date = task_date;
        this.task_title = task_title;
        this.task_description = task_description;
        this.task_priority = task_priority;
        this.task_category = task_category;
        this.task_time = task_time;
        this.task_done = task_done;
        this.task_reminder = task_reminder;
    }

    public String get_id() {
        return _id;
    }

    public String get_id_() {
        return _id_;
    }

    public String getTask_date() {
        return task_date;
    }

    public String getTask_title() {
        return task_title;
    }

    public String getTask_description() {
        return task_description;
    }

    public String getTask_priority() {
        return task_priority;
    }

    public String getTask_category() {
        return task_category;
    }

    public String getTask_time() {
        return task_time;
    }

    public String getTask_done() {
        return task_done;
    }

    public String getTask_reminder() {
        return task_reminder;
    }
}
