package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

public class VirtualInputIndexRvAdapter extends RecyclerView.Adapter<VirtualInputIndexRvAdapter.ViewHolder> {
    RvOnClick rvOnClick;

    public VirtualInputIndexRvAdapter(RvOnClick rvOnClick) {
        this.rvOnClick = rvOnClick;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vi_rv_item, parent, false);
        return new VirtualInputIndexRvAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull VirtualInputIndexRvAdapter.ViewHolder holder, int position) {
        holder.tv.setText("Virtual Input " + String.valueOf(position + 1));
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvOnClick.onClick(String.valueOf(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        View view;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.vite_header_txt);
            view = itemView.findViewById(R.id.view_base);
        }
    }
}
