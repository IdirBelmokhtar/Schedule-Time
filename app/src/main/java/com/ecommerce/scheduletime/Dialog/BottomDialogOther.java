package com.ecommerce.scheduletime.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.scheduletime.R;

public class BottomDialogOther extends Dialog {

    //field
    private LinearLayout other_dialog_edit, other_dialog_duplicate, other_dialog_delete, other_dialog_cancel;
    //constructor
    public BottomDialogOther(Activity activity){
        super(activity , R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.bottom_sheet_dialog_other);

        this.other_dialog_edit = findViewById(R.id.other_dialog_edit);
        this.other_dialog_duplicate = findViewById(R.id.other_dialog_duplicate);
        this.other_dialog_delete = findViewById(R.id.other_dialog_delete);
        this.other_dialog_cancel = findViewById(R.id.other_dialog_cancel);

    }

    public LinearLayout getOther_dialog_edit() {
        return other_dialog_edit;
    }

    public LinearLayout getOther_dialog_duplicate() {
        return other_dialog_duplicate;
    }

    public LinearLayout getOther_dialog_delete() {
        return other_dialog_delete;
    }

    public LinearLayout getOther_dialog_cancel() {
        return other_dialog_cancel;
    }

    public void build(){
        show();
    }
}
