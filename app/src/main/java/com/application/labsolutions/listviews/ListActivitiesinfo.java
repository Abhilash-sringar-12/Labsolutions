package com.application.labsolutions.listviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;

import java.util.List;

import androidx.annotation.NonNull;

public class ListActivitiesinfo extends ArrayAdapter<ActivityInfo> {
    private Context context;
    List<ActivityInfo> objects;
    int resource;
    Button phone;
    Button email;

    public ListActivitiesinfo(@NonNull Context context, int resource, @NonNull List<ActivityInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View userView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_activities_details_item, null);
        TextView activityText = (TextView) view.findViewById(R.id.activityInfo);
        TextView activityTime = (TextView) view.findViewById(R.id.activityDate);
        final ActivityInfo activityInfo = objects.get(position);
        activityText.setText(activityInfo.getInfo());
        activityTime.setText(activityInfo.getTime());
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.mail);
        if (!activityInfo.getMailId().isEmpty() && !activityInfo.getPhone().isEmpty()) {
            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + activityInfo.getPhone()));
                    context.startActivity(callIntent);
                }
            });
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{activityInfo.getMailId()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    try {
                        context.startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            phone.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }
        return view;
    }
}
