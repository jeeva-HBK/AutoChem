package com.ionexchange.Adapters;

import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.outputControl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Fragments.Services.FragmentOutputControl;
import com.ionexchange.Interface.RvOutputControl;
import com.ionexchange.R;

import java.util.List;

//created by Silambu
public class OutputControlRvAdapter extends RecyclerView.Adapter<OutputControlRvAdapter.itemHolder> {

    private Context context;
    RvOutputControl rvOutputControl;
    List<OutputConfigurationEntity> outputConfigurationEntityList;
    List<OutputKeepAliveEntity> outputKeepAliveEntityList;

    public OutputControlRvAdapter(RvOutputControl rvOutputControl, List<OutputConfigurationEntity> outputConfigurationEntityList, List<OutputKeepAliveEntity> outputKeepAliveEntityList) {
        this.rvOutputControl = rvOutputControl;
        this.outputConfigurationEntityList = outputConfigurationEntityList;
        this.outputKeepAliveEntityList = outputKeepAliveEntityList;
    }

    @NonNull
    @Override
    public itemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.output_control_item, parent, false);
        context = parent.getContext();
        return new itemHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        holder.outputControl.setAdapter(getAdapter(outputControl, context));
        if (!outputKeepAliveEntityList.get(position).getOutputStatus().equals("N/A")) {
            if (outputKeepAliveEntityList.get(position).getOutputStatus().equals("5") ||
                    outputKeepAliveEntityList.get(position).getOutputStatus().equals("6")) {
                holder.outputControl.setText(outputKeepAliveEntityList.get(position).getOutputRelayStatus());
            } else {
                holder.outputControl.setText(holder.outputControl.getAdapter().
                        getItem(Integer.parseInt(outputKeepAliveEntityList.get(position).getOutputStatus())).toString());
            }
            if (outputKeepAliveEntityList.get(position).getOutputStatus().equals("4")) {
                holder.time.setVisibility(View.VISIBLE);
                holder.time.setTooltipText(outputKeepAliveEntityList.get(position).getOutputRelayStatus());
            }else {
                holder.time.setVisibility(View.INVISIBLE);
            }
        }

        holder.outputName.setText(outputConfigurationEntityList.get(position).getOutputType());

        String outputmode = "";
        switch (outputConfigurationEntityList.get(position).getOutputStatus()){
            case "Continuous":
            case "Bleed/Blow Down":
            case "Water Meter/Biocide":
                outputmode = "Inhibitor";
                break;
            case "On/Off":
            case "PID":
                outputmode = "Sensor";
                break;
            case "Manual":
                outputmode = "Manual";
                break;
            default:
                outputmode = "Disable";
                break;
        }
        holder.outputType.setText(outputmode
                + "-" + outputConfigurationEntityList.get(position).getOutputStatus());
        holder.outputControl.setOnClickListener(View -> {
            FragmentOutputControl.canReceive = false;
            holder.outputControl.showDropDown();
        });
        holder.outputControl.setOnDismissListener(new AutoCompleteTextView.OnDismissListener() {
            @Override
            public void onDismiss() {
                FragmentOutputControl.canReceive = (!holder.outputControl.getText().toString().equals("Manual ON for"));
            }
        });

        if (position % 2 == 0) {
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.ash));
            holder.outputControl.setBackgroundColor(context.getResources().getColor(R.color.ash));
        }
        holder.outputControl.setAdapter(getAdapter(outputControl, context));

    }

    public void updateData(List<OutputConfigurationEntity> outputConfigurationEntityList, List<OutputKeepAliveEntity> outputList) {
        this.outputConfigurationEntityList = outputConfigurationEntityList;
        this.outputKeepAliveEntityList = outputList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 14;
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
        TextView outputName, outputType;
        AutoCompleteTextView outputControl;
        ConstraintLayout root;
        View time;

        public itemHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            outputName = itemView.findViewById(R.id.txt_output_name_value);
            outputType = itemView.findViewById(R.id.txt_output_type_value);
            outputControl = itemView.findViewById(R.id.act_output_type);
            time = itemView.findViewById(R.id.time_view);

            outputControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    FragmentOutputControl.canReceive = false;
                    rvOutputControl.click(outputName, outputType, outputControl, time, outputConfigurationEntityList.get(getAdapterPosition()).outputHardwareNo, pos);
                }
            });

            outputControl.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.e("outputControlRVAda", "onFocusChange: " + b);
                }
            });
        }
    }
}
