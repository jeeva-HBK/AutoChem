package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Dao.InputConfigurationDao;
import com.ionexchange.Database.Entity.DiagnosticDataEntity;
import com.ionexchange.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DiagnosticDataAdapter extends RecyclerView.Adapter<DiagnosticDataAdapter.ViewHolder> {
    List<DiagnosticDataEntity> mList;
    InputConfigurationDao dao;

    public DiagnosticDataAdapter(List<DiagnosticDataEntity> mList, InputConfigurationDao inputDao) {
        this.mList = mList;
        this.dao = inputDao;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diagnosticdata_rv_item, parent, false);
        return new DiagnosticDataAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DiagnosticDataAdapter.ViewHolder holder, int position) {
        if (position % 2 == 0) {
            holder.root.setBackgroundColor(R.color.gray50);
        }

        holder.hNo.setText(mList.get(position).hardWare + "");
        holder.data.setText(mList.get(position).diagnosticData);
        holder.time.setText(mList.get(position).timeStamp);
        holder.label.setText(dao.getInputLabel(mList.get(position).hardWare));
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView hNo, data, time, label;
        ConstraintLayout root;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            hNo = itemView.findViewById(R.id.item_inputNo);
            time = itemView.findViewById(R.id.item_time);
            data = itemView.findViewById(R.id.data);
            root = itemView.findViewById(R.id.root);
            label = itemView.findViewById(R.id.item_label);
        }
    }
}
