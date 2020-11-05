package com.application.labsolutions.listviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.UpdateUser;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class RegisteredUsersView extends ArrayAdapter<ListUserDetails> implements Filterable {

    private Context context;
    List<ListUserDetails> objects;
    List<ListUserDetails> filteredObjects;
    int resource;
    Button phone;
    Button email;
    ValueFilter valueFilter;
    String userType;


    public RegisteredUsersView(@NonNull Context context, int resource, @NonNull List<ListUserDetails> users, String type) {
        super(context, resource, users);
        this.context = context;
        this.resource = resource;
        objects = users;
        this.filteredObjects = users;
        this.userType = type;
    }


    @Override
    public View getView(int position, View userView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_item, null);
        TextView textUserNameView = (TextView) view.findViewById(R.id.username);
        TextView textUserTypeView = (TextView) view.findViewById(R.id.userType);
        ImageView imageView = view.findViewById(R.id.userImage);
        final ListUserDetails listUserDetails = filteredObjects.get(position);
        textUserNameView.setText(listUserDetails.name);
        textUserTypeView.setText(listUserDetails.type);
        imageView.setImageDrawable(context.getResources().getDrawable(listUserDetails.getImage()));
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.mail);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + listUserDetails.phoneNumber));
                context.startActivity(callIntent);
            }
        });
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{listUserDetails.emailId});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, "");
                try {
                    context.startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userType.equals("admin")) {
                    Intent intent = new Intent(context, UpdateUser.class);
                    intent.putExtra("userId", listUserDetails.getUid());
                    context.startActivity(intent);
                }
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                List<ListUserDetails> usersList = new ArrayList<>();
                for (int i = 0; i < objects.size(); i++) {
                    if ((objects.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {
                        ListUserDetails listUserDetails = new ListUserDetails(objects.get(i).getImage(), objects.get(i).getName(), objects.get(i).getPhoneNumber(), objects.get(i).getUid(), objects.get(i).getEmailId(), objects.get(i).getType());
                        usersList.add(listUserDetails);
                    }
                }
                results.count = usersList.size();
                results.values = usersList;
            } else {
                results.count = objects.size();
                results.values = objects;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filteredObjects = (ArrayList<ListUserDetails>) results.values;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return filteredObjects.size();
    }

    @Override
    public ListUserDetails getItem(int i) {
        return filteredObjects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}
