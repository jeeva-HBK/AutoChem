package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

public class TimerIndexRvAdapter extends RecyclerView.Adapter<TimerIndexRvAdapter.itemHolder> {
    RvOnClick listener;

    public TimerIndexRvAdapter(RvOnClick listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimerIndexRvAdapter.itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_index_item, parent, false);
        return new TimerIndexRvAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimerIndexRvAdapter.itemHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        View view;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.view_1);
            listener.onClick(String.valueOf(getAdapterPosition()));
        }
    }
}
