package com.ecommerce.scheduletime.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.scheduletime.R;

public class DialogSearch extends Dialog {

    //field
    private SearchView searchViewDialog;
    private RecyclerView recyclerViewSearch;
    private TextView noData;

    //constructor
    public DialogSearch(Activity activity) {
        super(activity, R.style.ThemeOverlay_AppCompat_Dialog);
        setContentView(R.layout.dialog_search);

        this.searchViewDialog = findViewById(R.id.searchViewDialog);
        this.recyclerViewSearch = findViewById(R.id.recyclerViewSearch);
        this.noData = findViewById(R.id.search_no_data);

    }

    public SearchView getSearchViewDialog() {
        return searchViewDialog;
    }

    public RecyclerView getRecyclerViewSearch() {
        return recyclerViewSearch;
    }

    public TextView getNoData() {
        return noData;
    }

    public void build() {
        show();
    }
}
