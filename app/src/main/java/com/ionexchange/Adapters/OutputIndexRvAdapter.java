package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

public class OutputIndexRvAdapter extends RecyclerView.Adapter<OutputIndexRvAdapter.ViewHolder> {
    RvOnClick rvOnClick;
    public OutputIndexRvAdapter(RvOnClick rvOnClick) {
        this.rvOnClick = rvOnClick;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.output_rv_item, parent, false);
        return new OutputIndexRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OutputIndexRvAdapter.ViewHolder holder, int position) {

        holder.viewBase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            rvOnClick.onClick(String.valueOf(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return 22;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View viewBase;
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            viewBase = itemView.findViewById(R.id.view_base);
        }
    }
}
