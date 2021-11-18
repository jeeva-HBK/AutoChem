package com.ionexchange.Adapters;

import static com.ionexchange.Others.ApplicationClass.getAdapter;
import static com.ionexchange.Others.ApplicationClass.outputControl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.ionexchange.Database.Dao.OutputConfigurationDao;
import com.ionexchange.Database.Entity.OutputConfigurationEntity;
import com.ionexchange.Database.Entity.OutputKeepAliveEntity;
import com.ionexchange.Database.WaterTreatmentDb;
import com.ionexchange.Interface.RvOutputControl;
import com.ionexchange.R;

import java.util.List;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull itemHolder holder, int position) {
        holder.outputControl.setAdapter(getAdapter(outputControl, context));
        holder.outputControl.setText(holder.outputControl.getAdapter().
                getItem(Integer.parseInt(outputKeepAliveEntityList.get(position).getOutputStatus())).toString());

        holder.outputName.setText(outputConfigurationEntityList.get(position).getOutputType()
                +"-"+outputConfigurationEntityList.get(position).getOutputLabel());
        holder.outputType.setText(outputConfigurationEntityList.get(position).getOutputMode()
                +"-"+outputConfigurationEntityList.get(position).getOutputStatus());
        holder.outputControl.setOnClickListener(View ->{
            holder.outputControl.showDropDown();
        });

        if (position%2==0){
            holder.root.setBackgroundColor(context.getResources().getColor(R.color.ash));
            holder.outputControl.setBackgroundColor(context.getResources().getColor(R.color.ash));
        }
        holder.outputControl.setAdapter(getAdapter(outputControl, context));
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
                    rvOutputControl.click(outputName,outputType,outputControl,time,outputConfigurationEntityList.get(getAdapterPosition()).outputHardwareNo,pos);
                }
            });
        }
    }
}
