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
import com.application.labsolutions.admin.UpdateInstruments;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CreatedInstrumentView extends RecyclerView.Adapter<CreatedInstrumentView.ViewHolder> {

    List<ListInstrumentDetails> objects;
    List<ListInstrumentDetails> filteredObjects;
    String userType;
    Context context;

    public CreatedInstrumentView(Context context, List<ListInstrumentDetails> objects, String userType) {
        this.objects = objects;
        this.context = context;
        this.userType = userType;
        this.filteredObjects = new ArrayList<>(objects);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.row_intruments_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ListInstrumentDetails listUserDetails = objects.get(position);
        holder.instrumentId.setText(listUserDetails.getInstrumentId());
        holder.instrumentType.setText(listUserDetails.getCompanyName() + "/ " + listUserDetails.getInstrumentType() + "/ " + listUserDetails.getDepartment());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("admin")) {
                    Intent newIntent = new Intent(context, UpdateInstruments.class);
                    newIntent.putExtra("company", listUserDetails.getCompanyName());
                    newIntent.putExtra("instrumentType", listUserDetails.getInstrumentType());
                    newIntent.putExtra("instrumentId", listUserDetails.getInstrumentId());
                    newIntent.putExtra("department", listUserDetails.getDepartment());
                    newIntent.putExtra("amcFromDate", listUserDetails.getAmcFromDate());
                    newIntent.putExtra("amcToDate", listUserDetails.getAmcToDate());
                    newIntent.putExtra("department", listUserDetails.getDepartment());
                    context.startActivity(newIntent);
                }
            }
        });
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView instrumentId;
        public TextView instrumentType;
        public TextView companyName;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            instrumentId = (TextView) itemView.findViewById(R.id.instrumentId);
            instrumentType = (TextView) itemView.findViewById(R.id.intrumentType);
            companyName = (TextView) itemView.findViewById(R.id.companyName);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relativeLayout);
        }
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ListInstrumentDetails> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(filteredObjects);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ListInstrumentDetails item : filteredObjects) {
                    if (item.getInstrumentId().toLowerCase().contains(filterPattern)) {
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
