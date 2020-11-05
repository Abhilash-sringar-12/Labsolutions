package com.application.labsolutions.listviews;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.labsolutions.R;
import com.application.labsolutions.customer.ActivityDetails;
import com.application.labsolutions.workadmin.WorkAdminActivityDetails;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ListActivitiesDetails extends RecyclerView.Adapter<ListActivitiesDetails.ViewHolder> {

    List<ActivitiesInfoDetails> objects;
    List<ActivitiesInfoDetails> filteredObjects;
    String type;
    Context context;

    public ListActivitiesDetails(Context context, List<ActivitiesInfoDetails> objects, String type) {
        this.objects = objects;
        this.context = context;
        this.type = type;
        this.filteredObjects = new ArrayList<>(objects);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_activities_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ActivitiesInfoDetails activitiesInfoDetails = objects.get(position);
        holder.activityName.setText(activitiesInfoDetails.getInstrumentId());
        holder.activityDescription.setText(activitiesInfoDetails.getActivityName());
        holder.activityStatus.setText(activitiesInfoDetails.getActivitystatus());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = type.equals("info") ? new Intent(context, ActivityDetails.class) : new Intent(context, WorkAdminActivityDetails.class);
                newIntent.putExtra("activityId", activitiesInfoDetails.getActivityId());
                context.startActivity(newIntent);
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName;
        public TextView activityDescription;
        public TextView activityStatus;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            activityName = (TextView) itemView.findViewById(R.id.activityName);
            activityDescription = (TextView) itemView.findViewById(R.id.activityDescription);
            activityStatus = (TextView) itemView.findViewById(R.id.activityStatus);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayoutActivities);
        }
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ActivitiesInfoDetails> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filteredObjects);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ActivitiesInfoDetails item : filteredObjects) {
                    if (item.getActivityName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getInstrumentId().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getActivityDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    } else if (item.getActivitystatus().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
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
