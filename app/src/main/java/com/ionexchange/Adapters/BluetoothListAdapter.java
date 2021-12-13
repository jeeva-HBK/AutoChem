package com.ionexchange.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Interface.ItemClickListener;
import com.ionexchange.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.ViewHolder> {
    ArrayList<String> mDeviceList;
    ItemClickListener listener;
    List<String> mList;

    public BluetoothListAdapter(ArrayList<String> deviceList, List<String> list, ItemClickListener listener) {
        this.mDeviceList = deviceList;
        this.listener = listener;
        this.mList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buletooth, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String deviceName = mDeviceList.get(position);
        holder.Name.setText(deviceName);

        if (!mList.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                String[] strArr = mDeviceList.get(position).split("\n");
                if (strArr[1].equals(mList.get(i))) {
                    holder.fav.setChecked(true);
                }
            }
        }

       /* holder.fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    listener.onSaveClicked(deviceName);
                } else {
                    listener.onUnSave(deviceName);
                }
            }
        });*/
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDeviceList.size();
    }

    public void setData(ArrayList<String> deviceList) {
        this.mDeviceList = deviceList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Name;
        CheckBox fav;
        CardView root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.txt_name);
            root = itemView.findViewById(R.id.rootBleItem);
        }
    }
}
