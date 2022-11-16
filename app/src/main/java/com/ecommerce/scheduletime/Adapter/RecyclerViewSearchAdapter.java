package com.ecommerce.scheduletime.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecommerce.scheduletime.HomeActivity.EditTaskActivity;
import com.ecommerce.scheduletime.Model.RecyclerViewSearch;
import com.ecommerce.scheduletime.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class RecyclerViewSearchAdapter extends RecyclerView.Adapter<RecyclerViewSearchAdapter.RecyclerViewSearchHolder> {

    private List<RecyclerViewSearch> recyclerViewSearch;
    private Context context;

    public RecyclerViewSearchAdapter(Context context, List<RecyclerViewSearch> recyclerViewSearch) {
        this.recyclerViewSearch = recyclerViewSearch;
        this.context = context;
    }
    public void setSearchList(Context context, List<RecyclerViewSearch> searchList){
        this.recyclerViewSearch = searchList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewSearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_recycler_view_search, parent, false);
        return new RecyclerViewSearchHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSearchHolder holder, int position) {
        RecyclerViewSearch search = recyclerViewSearch.get(position);

        String id = search.getId();
        holder.search_title.setText(search.getTitle());

        List<String> string_date = Arrays.asList(search.getDate().split("-"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, Integer.parseInt(string_date.get(1)));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(string_date.get(2)));
        holder.search_date.setText((String) DateFormat.format("MMM", calendar) + " " + (String) DateFormat.format("dd", calendar) + ", " + string_date.get(0));
        holder.search_time.setText(search.getTime());
        holder.search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Edit Your Task in EditTaskActivity.class
                Intent intent = new Intent(context, EditTaskActivity.class);
                intent.putExtra("id", id);
                ((Activity) context).startActivityForResult(intent,1);
            }
        });

    }

    @Override
    public int getItemCount() {
        return recyclerViewSearch.size();
    }

    public class RecyclerViewSearchHolder extends RecyclerView.ViewHolder {

        private LinearLayout search_layout;
        private TextView search_title, search_date, search_time;

        public RecyclerViewSearchHolder(@NonNull View itemView) {
            super(itemView);

            search_layout = itemView.findViewById(R.id.my_recycler_view_search_layout);
            search_date = itemView.findViewById(R.id.my_recycler_view_search_date);
            search_title = itemView.findViewById(R.id.my_recycler_view_search_title);
            search_time = itemView.findViewById(R.id.my_recycler_view_search_time);

        }
    }
}