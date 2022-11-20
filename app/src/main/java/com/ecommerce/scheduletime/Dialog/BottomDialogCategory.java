package com.ecommerce.scheduletime.Dialog;

import static com.ecommerce.scheduletime.HomeActivity.EditTaskActivity.category_ids_new;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.ecommerce.scheduletime.R;
import com.ecommerce.scheduletime.SQLite.MyDatabaseHelper_category;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomDialogCategory extends Dialog {

    //field
    private CircleImageView new_category_add;
    private LinearLayout categorys_layout, add_category_layout;

    MyDatabaseHelper_category myDB_category;
    ArrayList<String> category_id, category_name, category_color, category_deleted;
    Button[] category_btn;

    List<String> category = new ArrayList<>();
    int color_;

    //category
    private EditText editText_category;
    private LinearLayout _editText_category, category_colors;
    private CardView editText_category_icon;
    private ImageView editText_category_icon_;
    private CircleImageView editText_category_done;
    private CircleImageView category_color1, category_color2, category_color3, category_color4, category_color5, category_color6, category_color7;

    //constructor
    public BottomDialogCategory(Activity activity){
        super(activity , R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.bottom_sheet_dialog_category);


        this.new_category_add = findViewById(R.id.new_category_add);
        this.categorys_layout = findViewById(R.id.categorys_layout);
        this.add_category_layout = findViewById(R.id.add_category_layout);
        categorys_layout.setVisibility(View.VISIBLE);
        add_category_layout.setVisibility(View.GONE);
        new_category_add.setVisibility(View.VISIBLE);
        //First color of new_category_add and icon when dialog started
        Glide.with(getContext()).load(R.drawable.ic_baseline_add_24).into(new_category_add);
        changeColorBackgroundCircleImageView(new_category_add, R.color.blue);

        myDB_category = new MyDatabaseHelper_category(getContext());
        category_id = new ArrayList<>();
        category_name = new ArrayList<>();
        category_color = new ArrayList<>();
        category_deleted = new ArrayList<>();

        try {
            category.clear();
            storeDataCategoryInArraysAndLayout();
        } catch (Exception e) {
            e.printStackTrace();
        }

        color_ = R.color.colorViewOne; // colorViewOne for default color

        editText_category = findViewById(R.id.editText_category);
        _editText_category = findViewById(R.id._editText_category);
        category_colors = findViewById(R.id.category_colors);
        editText_category_icon = findViewById(R.id.editText_category_icon);
        editText_category_icon_ = findViewById(R.id.editText_category_icon_);
        editText_category_done = findViewById(R.id.editText_category_done);

        category_color1 = findViewById(R.id.category_color1);
        category_color2 = findViewById(R.id.category_color2);
        category_color3 = findViewById(R.id.category_color3);
        category_color4 = findViewById(R.id.category_color4);
        category_color5 = findViewById(R.id.category_color5);
        category_color6 = findViewById(R.id.category_color6);
        category_color7 = findViewById(R.id.category_color7);

        new_category_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((categorys_layout.getVisibility() == View.VISIBLE) && (add_category_layout.getVisibility() == View.GONE)) {
                    /*Show add categories layout*/
                    categorys_layout.setVisibility(View.GONE);
                    add_category_layout.setVisibility(View.VISIBLE);
                    //change new_category_add icon

                    editText_category.setVisibility(View.VISIBLE);
                    //Start writing directly
                    editText_category.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText_category, InputMethodManager.SHOW_IMPLICIT);

                    category_colors.setVisibility(View.GONE);
                    editText_category_icon_.setVisibility(View.GONE);
                    editText_category_icon.setVisibility(View.GONE);

                    //change new_category_add icon
                    Glide.with(getContext()).load(R.drawable.icons8_delete_2).into(new_category_add);
                    changeColorBackgroundCircleImageView(new_category_add, R.color.delete);

                } else if ((categorys_layout.getVisibility() == View.GONE) && (add_category_layout.getVisibility() == View.VISIBLE)) {
                    /*Show category*/
                    categorys_layout.setVisibility(View.VISIBLE);
                    add_category_layout.setVisibility(View.GONE);

                    //change new_category_add icon
                    Glide.with(getContext()).load(R.drawable.ic_baseline_add_24).into(new_category_add);
                    changeColorBackgroundCircleImageView(new_category_add, R.color.blue);
                }

            }
        });

        editText_category_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText_category_icon_.getVisibility() == View.GONE) {
                    editText_category_done.setVisibility(View.GONE);
                    editText_category.setVisibility(View.GONE);
                    category_colors.setVisibility(View.VISIBLE);
                    new_category_add.setVisibility(View.GONE);

                    _editText_category.setVisibility(View.GONE);
                    colorsClicked(view);
                } else if (editText_category_icon_.getVisibility() == View.VISIBLE) {
                    //Save
                    MyDatabaseHelper_category myDB = new MyDatabaseHelper_category(getContext());
                    myDB.addCategory(editText_category.getText().toString().trim(), color_, "no");

                    category.clear();
                    storeDataCategoryInArraysAndLayout();

                    //Toast.makeText(getContext(), "Save", Toast.LENGTH_SHORT).show();

                    /*Show category*/
                    categorys_layout.setVisibility(View.VISIBLE);
                    add_category_layout.setVisibility(View.GONE);

                    //change new_category_add icon
                    Glide.with(getContext()).load(R.drawable.ic_baseline_add_24).into(new_category_add);
                    changeColorBackgroundCircleImageView(new_category_add, R.color.blue);
                }
            }
        });
        editText_category_icon_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editText_category_done.setVisibility(View.GONE);
                editText_category.setVisibility(View.GONE);
                category_colors.setVisibility(View.VISIBLE);
                new_category_add.setVisibility(View.GONE);
                //colors clicked

                _editText_category.setVisibility(View.GONE);
                colorsClicked(view);

            }
        });


    }

    public void changeColorBackgroundCircleImageView(CircleImageView imageView, int color) {
        Drawable background = imageView.getBackground();

        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(getContext(), color));
        }
    }

    private void storeDataCategoryInArraysAndLayout() {
        category_id.clear();
        category_name.clear();
        category_color.clear();
        category_deleted.clear();

        Cursor cursor = myDB_category.readAllData();
        while (cursor.moveToNext()) {
            category_id.add(cursor.getString(0));
            category_name.add(cursor.getString(1));
            category_color.add(cursor.getString(2));
            category_deleted.add(cursor.getString(3));
        }

        category_btn = new Button[cursor.getCount()];
        categorys_layout.removeAllViews();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 108);
        params.setMarginEnd(8);

        for (int i = 0; i < category_btn.length; i++) {
            if (category_deleted.get(i).equals("no")) {

                category_btn[i] = new Button(getContext());
                category_btn[i].setText(category_name.get(i).toString());
                category_btn[i].setTextSize(12);
                category_btn[i].setAllCaps(false);
                category_btn[i].setTextColor(ContextCompat.getColor(getContext(), R.color.blue));
                category_btn[i].setBackgroundResource(R.drawable.custom_button_priority);
                changeColorBtn(category_btn[i], Integer.parseInt(category_color.get(i)));
                category_btn[i].setLayoutParams(params);

                categorys_layout.addView(category_btn[i]);

                int finalI = i;
                category_btn[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int j = 0; j < category_ids_new.size(); j++) {
                            if (category_ids_new.get(j).equals(category_id.get(finalI))){
                                Toast.makeText(getContext(), "This category exist!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        category_ids_new.add(category_id.get(finalI).toString());
                        dismiss();
                    }
                });
                category_btn[i].setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.bottom_sheet_dialog_delete);

                        TextView title = dialog.findViewById(R.id.sheet_dialog_title);
                        LinearLayout sheet_dialog_cancel = dialog.findViewById(R.id.sheet_dialog_cancel);
                        LinearLayout sheet_dialog_delete = dialog.findViewById(R.id.sheet_dialog_delete);

                        title.setText("Delete this category :\n\"" + String.valueOf(category_btn[finalI].getText()) + "\" ?");
                        sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                        sheet_dialog_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                myDB_category.updateData(category_id.get(finalI), category_name.get(finalI), Integer.parseInt(category_color.get(finalI)), "yes");
                                storeDataCategoryInArraysAndLayout();

                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.getWindow().getAttributes().windowAnimations = R.style.SheetDialogAnimation;
                        dialog.getWindow().setGravity(Gravity.BOTTOM);

                        return true;//true for show just the long click listener
                    }
                });
            }
        }
    }

    private void colorsClicked(View view) {

        category_color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewOne;
                changeColorCategory(color_);
            }
        });
        category_color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewTwo;
                changeColorCategory(color_);
            }
        });
        category_color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewThree;
                changeColorCategory(color_);
            }
        });
        category_color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewFour;
                changeColorCategory(color_);
            }
        });
        category_color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewFive;
                changeColorCategory(color_);
            }
        });
        category_color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewSix;
                changeColorCategory(color_);
            }
        });
        category_color7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_ = R.color.colorViewSeven;
                changeColorCategory(color_);
            }
        });
    }

    private void changeColorCategory(int color_) {
        editText_category_icon_.setVisibility(View.VISIBLE);
        editText_category_icon.setVisibility(View.VISIBLE);

        editText_category_done.setVisibility(View.VISIBLE);
        new_category_add.setVisibility(View.VISIBLE);
        category_colors.setVisibility(View.GONE);

        editText_category.setVisibility(View.VISIBLE);
        editText_category.setTextColor(ContextCompat.getColor(getContext(), color_));
        editText_category.setHintTextColor(ContextCompat.getColor(getContext(), color_));

        editText_category_icon_.setBackgroundResource(color_);
        changeColorBackground(_editText_category, color_);
        _editText_category.setVisibility(View.VISIBLE);
    }

    private void changeColorBackground(LinearLayout _editText_category, int color_) {
        GradientDrawable background = (GradientDrawable) _editText_category.getBackground();

        background.mutate();
        background.setStroke(1, ContextCompat.getColor(getContext(), color_));
    }

    public void changeColorBtn(Button btn, int color) {
        Drawable background = btn.getBackground();

        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(ContextCompat.getColor(getContext(), color));
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(ContextCompat.getColor(getContext(), color));
        }
    }

    public void build(){
        show();
    }
}
