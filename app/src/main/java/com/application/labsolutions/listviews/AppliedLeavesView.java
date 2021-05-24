package com.application.labsolutions.listviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.UpdateInstruments;
import com.application.labsolutions.engineer.AppliedLeaveDetails;
import com.application.labsolutions.engineer.AppliedLeaves;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;

import java.util.ArrayList;
import java.util.List;

public class AppliedLeavesView extends RecyclerView.Adapter<AppliedLeavesView.ViewHolder> {
    List<LeaveDetails> objects;
    List<LeaveDetails> filteredObjects;
    String userType;
    Context context;

    public AppliedLeavesView(Context context, List<LeaveDetails> objects, String userType) {
        this.objects = objects;
        this.filteredObjects = new ArrayList<>(objects);
        this.userType = userType;
        this.context = context;
    }


    @NonNull
    @Override
    public AppliedLeavesView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_leaves_item, parent, false);
        AppliedLeavesView.ViewHolder viewHolder = new AppliedLeavesView.ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppliedLeavesView.ViewHolder holder, int position) {
        final LeaveDetails leaveDetails = objects.get(position);
        if (userType.equals("admin")) {
            holder.name.setText(leaveDetails.getName());
            holder.leaveType.setText(leaveDetails.getLeaveType());
            holder.date.setText(leaveDetails.getLeaveFrom());
        } else {

            holder.name.setText(leaveDetails.getLeaveType());
            holder.leaveType.setText(leaveDetails.getLeaveFrom());
            holder.date.setText("Click To View");
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(context, AppliedLeaveDetails.class);
                newIntent.putExtra("leaveId", leaveDetails.getLeaveID());
                newIntent.putExtra("leaveType", leaveDetails.getLeaveType());
                newIntent.putExtra("leaveFrom", leaveDetails.getLeaveFrom());
                newIntent.putExtra("backOn", leaveDetails.getBackToWork());
                newIntent.putExtra("totalLeaves", leaveDetails.getTotalLeaves());
                newIntent.putExtra("userName", leaveDetails.getName());
                newIntent.putExtra("userType", userType);
                context.startActivity(newIntent);
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView leaveType;
        public TextView date;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            leaveType = (TextView) itemView.findViewById(R.id.leaveType);
            date = (TextView) itemView.findViewById(R.id.date);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<LeaveDetails> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filteredObjects);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (LeaveDetails item : filteredObjects) {
                    if (item.getLeaveFrom().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getLeaveType().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            objects.clear();
            objects.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    public Filter getFilter() {
        return exampleFilter;
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
