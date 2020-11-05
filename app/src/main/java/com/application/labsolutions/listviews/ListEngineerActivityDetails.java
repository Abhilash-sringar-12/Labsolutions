package com.application.labsolutions.listviews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.application.labsolutions.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import androidx.annotation.NonNull;

public class ListEngineerActivityDetails extends ArrayAdapter<ActivitiesInfoDetails> {
    private Context context;
    FirebaseAuth firebaseAuth;
    List<ActivitiesInfoDetails> objects;
    int resource;

    public ListEngineerActivityDetails(@NonNull Context context, int resource, @NonNull List<ActivitiesInfoDetails> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View userView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_enginner_assigned_activities, null);
        TextView activityName = (TextView) view.findViewById(R.id.activityName);
        TextView activityDescription = (TextView) view.findViewById(R.id.activityDescription);
        TextView activityStatus = (TextView) view.findViewById(R.id.activityStatus);
        final ActivitiesInfoDetails activityDetails = objects.get(position);
        activityName.setText(activityDetails.instrumentId);
        activityDescription.setText(activityDetails.activityName);
        activityStatus.setText(activityDetails.activitystatus);
        return view;
    }


    @Override
    public int getCount() {
        return objects.size();
    }
}


