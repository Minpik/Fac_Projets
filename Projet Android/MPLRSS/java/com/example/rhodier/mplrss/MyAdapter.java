package com.example.rhodier.mplrss;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Cursor cursor;
    private OnAdapterInteractionListener context;
    private String tag;
    private String columnName;

    MyAdapter(OnAdapterInteractionListener context, String columnName, String tag) {
        this.context = context;
        this.columnName = columnName;
        this.tag = tag;
    }

    @NonNull
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.onAdapterInteraction(tag, view);
            }
        };

        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1,
                        viewGroup, false);
        view.setId(i);

        if (cursor != null) {
            cursor.moveToPosition(i);

            TextView tv = (TextView) view;
            tv.setText(cursor.getString(cursor.getColumnIndex(columnName)));
        }

        view.setOnClickListener(listener);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyAdapter.ViewHolder viewHolder, int i) {
        if (cursor.moveToPosition(i)) {
            TextView tv = (TextView) viewHolder.itemView;
            tv.setText(cursor.getString(cursor.getColumnIndex(columnName)));
            tv.setId(i);
        }
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }
    }

    void swapCursor(Cursor cursor) {
        if (this.cursor == cursor)
            return;

        this.cursor = cursor;

        if (cursor != null)
            this.notifyDataSetChanged();
    }

    Cursor getCursor() {
        return cursor;
    }

    public interface OnAdapterInteractionListener {
        void onAdapterInteraction(String tag, View view);
    }
}
