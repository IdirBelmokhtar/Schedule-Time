package com.ecommerce.scheduletime.Dialog;

import static com.ecommerce.scheduletime.Fragments.ListFragment.sort;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ecommerce.scheduletime.R;

public class DialogDetails extends Dialog {

    //field
    private ImageView details_exit;
    private TextView details_title, details_description, details_reminder;
    private Button details_priority, details_edit;
    private LinearLayout details_category_layout_;

    //constructor
    public DialogDetails(Activity activity){
        super(activity , R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.dialog_details);

        this.details_exit = findViewById(R.id.exit_dialog_details);
        this.details_title = findViewById(R.id.details_title);
        this.details_description = findViewById(R.id.details_description);
        this.details_reminder = findViewById(R.id.details_reminder);
        this.details_priority = findViewById(R.id.details_priority);
        this.details_category_layout_ = findViewById(R.id.details_category_layout_);
        this.details_edit = findViewById(R.id.details_edit);

        details_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public TextView getDetails_title() {
        return details_title;
    }

    public TextView getDetails_description() {
        return details_description;
    }

    public TextView getDetails_reminder() {
        return details_reminder;
    }

    public Button getDetails_priority() {
        return details_priority;
    }

    public LinearLayout getDetails_category_layout_() {
        return details_category_layout_;
    }

    public Button getDetails_edit() {
        return details_edit;
    }

    public void build(){
        show();
    }
}
