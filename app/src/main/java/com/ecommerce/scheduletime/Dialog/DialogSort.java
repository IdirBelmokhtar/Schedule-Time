package com.ecommerce.scheduletime.Dialog;

import static com.ecommerce.scheduletime.Fragments.ListFragment.sort;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ecommerce.scheduletime.R;

public class DialogSort extends Dialog {

    //field
    private ImageView exit;
    private RadioGroup sort_radioGroup;
    private RadioButton sort_date, sort_priority, sort_category, sort_task_done;
    //constructor
    public DialogSort(Activity activity){
        super(activity , R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.dialog_sort);

        this.exit = findViewById(R.id.exit_dialog_sort);
        this.sort_radioGroup = findViewById(R.id.sort_radioGroup);
        this.sort_date = findViewById(R.id.sort_date);
        this.sort_priority = findViewById(R.id.sort_priority);
        this.sort_category = findViewById(R.id.sort_category);
        this.sort_task_done = findViewById(R.id.sort_task_done);

        if (sort == 0) {
            sort_date.setChecked(true);
        } else if (sort == 1){
            sort_priority.setChecked(true);
        } else if (sort == 2) {
            sort_category.setChecked(true);
        } else if (sort == 3){
            sort_task_done.setChecked(true);
        }

    }

    public ImageView getExit() {
        return exit;
    }

    public RadioGroup getSort_radioGroup() {
        return sort_radioGroup;
    }

    public RadioButton getSort_date() {
        return sort_date;
    }

    public RadioButton getSort_priority() {
        return sort_priority;
    }

    public RadioButton getSort_category() {
        return sort_category;
    }

    public RadioButton getSort_task_done() {
        return sort_task_done;
    }

    public void build(){
        show();
    }
}
