package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

public class OutputIndexRvAdapter extends RecyclerView.Adapter<OutputIndexRvAdapter.ViewHolder> {
    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.output_rv_item, parent, false);
        return new OutputIndexRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OutputIndexRvAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 22;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
        }
    }
}
