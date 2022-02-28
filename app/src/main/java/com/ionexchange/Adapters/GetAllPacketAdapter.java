package com.ionexchange.Adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Others.GetAllPacketModel;
import com.ionexchange.R;

import java.util.List;

public class GetAllPacketAdapter extends RecyclerView.Adapter<GetAllPacketAdapter.itemHolder> {

    List<GetAllPacketModel> getAllPacketModelList;

    public GetAllPacketAdapter(List<GetAllPacketModel> getAllPacketModelList) {
        this.getAllPacketModelList = getAllPacketModelList;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_get_all_packet, parent, false);
        return new GetAllPacketAdapter.itemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        holder.no.setText(getAllPacketModelList.get(position).getNo());
        holder.type.setText(getAllPacketModelList.get(position).getType());
        holder.updated.setText(getAllPacketModelList.get(position).getUpdate());
        holder.updated.setTextColor((getAllPacketModelList.get(position).getUpdate().equals("UPDATED") ||
                getAllPacketModelList.get(position).getUpdate().equals("SUCCESS")) ?
                Color.parseColor("#006400") : Color.RED);
    }

    @Override
    public int getItemCount() {
        return getAllPacketModelList.size();
    }

    public class itemHolder extends RecyclerView.ViewHolder {
        TextView no, type, updated;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            no = itemView.findViewById(R.id.hardNo);
            type = itemView.findViewById(R.id.type);
            updated = itemView.findViewById(R.id.Update);
        }
    }
}
