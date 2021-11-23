package com.ionexchange.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.R;

public class EventLogRvAdapter extends RecyclerView.Adapter<EventLogRvAdapter.itemHolder> {
    Context context;
    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_log_item, parent, false);
        context = parent.getContext();
        return new EventLogRvAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        if (position % 2 == 0) {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.ash));

        }
    }

    @Override
    public int getItemCount() {
        return 16;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        ConstraintLayout root;
        public itemHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
        }
    }
}
