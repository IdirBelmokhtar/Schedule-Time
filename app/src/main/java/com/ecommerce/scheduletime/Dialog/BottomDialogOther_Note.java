package com.ecommerce.scheduletime.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.LinearLayout;

import com.ecommerce.scheduletime.R;

public class BottomDialogOther_Note extends Dialog {

    //field
    private LinearLayout other_dialog_edit_note, other_dialog_select_note, other_dialog_create_task_note, other_dialog_duplicate_note, other_dialog_delete_note, other_dialog_cancel_note;
    //constructor
    public BottomDialogOther_Note(Activity activity){
        super(activity , R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.bottom_sheet_dialog_other_note);

        this.other_dialog_edit_note = findViewById(R.id.other_dialog_edit_note);
        this.other_dialog_select_note = findViewById(R.id.other_dialog_select_note);
        this.other_dialog_create_task_note = findViewById(R.id.other_dialog_create_task_note);
        this.other_dialog_duplicate_note = findViewById(R.id.other_dialog_duplicate_note);
        this.other_dialog_delete_note = findViewById(R.id.other_dialog_delete_note);
        this.other_dialog_cancel_note = findViewById(R.id.other_dialog_cancel_note);

    }

    public LinearLayout getOther_dialog_edit_note() {
        return other_dialog_edit_note;
    }

    public LinearLayout getOther_dialog_select_note() {
        return other_dialog_select_note;
    }

    public LinearLayout getOther_dialog_create_task_note() {
        return other_dialog_create_task_note;
    }

    public LinearLayout getOther_dialog_duplicate_note() {
        return other_dialog_duplicate_note;
    }

    public LinearLayout getOther_dialog_delete_note() {
        return other_dialog_delete_note;
    }

    public LinearLayout getOther_dialog_cancel_note() {
        return other_dialog_cancel_note;
    }

    public void build(){
        show();
    }
}
