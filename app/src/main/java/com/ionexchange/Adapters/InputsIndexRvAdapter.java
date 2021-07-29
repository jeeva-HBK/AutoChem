package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Interface.RvOnClick;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

public class InputsIndexRvAdapter extends RecyclerView.Adapter<InputsIndexRvAdapter.ViewHolder> {
    RvOnClick rvOnClick;
    public InputsIndexRvAdapter(RvOnClick rvOnClick) {
        this.rvOnClick = rvOnClick;
    }

    @NonNull
    @NotNull
    @Override
    public InputsIndexRvAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.input_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull InputsIndexRvAdapter.ViewHolder holder, int position) {
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvOnClick.onClick(String.valueOf(position));
            }
        });
        holder.inputNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return 44;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout view;
        TextView inputType, inputNumber;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.rv_view);
            inputType = itemView.findViewById(R.id.typeTv);
            inputNumber = itemView.findViewById(R.id.inputNumberRv);
        }
    }
}