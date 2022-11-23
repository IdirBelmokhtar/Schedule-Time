package com.ecommerce.scheduletime.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecommerce.scheduletime.R;

public class BottomDialogLanguages extends Dialog {

    //field
    private LinearLayout dialog_lang_default_system, dialog_lang_arabic, dialog_lang_english, dialog_lang_french;
    private TextView dialog_lang_default_system_;
    //constructor
    public BottomDialogLanguages(Activity activity){
        super(activity , R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.bottom_sheet_dialog_languages);

        this.dialog_lang_default_system = findViewById(R.id.dialog_lang_system_default);
        this.dialog_lang_arabic = findViewById(R.id.dialog_lang_arabic);
        this.dialog_lang_english = findViewById(R.id.dialog_lang_english);
        this.dialog_lang_french = findViewById(R.id.dialog_lang_french);

        this.dialog_lang_default_system_ = findViewById(R.id.dialog_lang_system_default_);

    }

    public LinearLayout getDialog_lang_default_system() {
        return dialog_lang_default_system;
    }

    public LinearLayout getDialog_lang_arabic() {
        return dialog_lang_arabic;
    }

    public LinearLayout getDialog_lang_english() {
        return dialog_lang_english;
    }

    public LinearLayout getDialog_lang_french() {
        return dialog_lang_french;
    }

    public TextView getDialog_lang_default_system_() {
        return dialog_lang_default_system_;
    }

    public void build(){
        show();
    }
}
