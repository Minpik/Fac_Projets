package com.example.rhodier.mplrss;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MySelectAdapter extends RecyclerView.Adapter<MySelectAdapter.ViewHolder> {
    private Cursor cursor;
    private OnAdapterInteractionListener listener;
    private String tag;
    private String columnName;
    private List<Integer> selected;
    private boolean isSelection;

    public MySelectAdapter(OnAdapterInteractionListener listener,
                           String columnName, String tag) {
        this.listener = listener;
        this.columnName = columnName;
        this.tag = tag;
        this.selected = new ArrayList<>();
    }

    @NonNull
    @Override
    public MySelectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelection) {
                    if (!selected.contains(view.getId())) {
                        selected.add(view.getId());
                        view.setBackgroundColor(Color.RED);
                    }
                } else
                    listener.onAdapterInteraction(tag, view, false);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isSelection) {
                    isSelection = true;
                    selected.add(view.getId());
                    view.setBackgroundColor(Color.RED);
                    listener.onAdapterInteraction(tag, view, true);
                }

                return true;
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySelectAdapter.ViewHolder viewHolder, int i) {
        if (cursor.moveToPosition(i)) {
            TextView tv = (TextView) viewHolder.itemView;
            String value = cursor.getString(cursor.getColumnIndex(columnName));
            tv.setText(value);
            tv.setId(i);

            if (selected.contains(i))
                tv.setBackgroundColor(Color.RED);
            else
                tv.setBackgroundColor(Color.WHITE);
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

    public List<Integer> getSelected() {
        return selected;
    }

    void clearSelection() {
        selected.clear();
        isSelection = false;
        notifyDataSetChanged();
    }

    public boolean isSelection() {
        return isSelection;
    }

    public interface OnAdapterInteractionListener {
        void onAdapterInteraction(String tag, View view, boolean selection);
    }
}
