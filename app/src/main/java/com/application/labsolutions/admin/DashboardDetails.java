package com.application.labsolutions.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDetails extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    String type;
    Map<String, Map<String, Integer>> map = null;
    int res, wai, appr, sche = 0;
    TableRow row;
    TextView t1, t2, t3, t4, t5,text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            toolbar.setTitle("Dashboard Company Details");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            type = intent.getStringExtra("type");
            firebaseAuth = FirebaseAuth.getInstance();
            TableLayout tableLayout = (TableLayout) findViewById(R.id.table_layout_details);
            progressDialog = ProgressDialog.show(DashboardDetails.this, "Please wait", "Loading data.....", true, false);
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            tableLayout.setStretchAllColumns(true);
            text= findViewById(R.id.text);
            DatabaseReference activities = rootRef.child("activities");
            Date referenceDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            c.add(Calendar.MONTH, -3);
            Long startDate = c.getTime().getTime();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_YEAR, 1);
            Long currentDate = c.getTime().getTime();
            text.setText(type +" three months data of each company ");
            ValueEventListener activitiesValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot activitiesSnapShot) {
                    try {
                        map = new HashMap<>();
                        Map<String, Integer> statusMap = null;
                        String currentActivityCompany = null;
                        for (DataSnapshot activityDs : activitiesSnapShot.getChildren()) {
                            Long currentActivityScheduledDate = activityDs.child("timeStamp").getValue(Long.class);
                            String currentActivityType = activityDs.child("activity-info/callType").getValue(String.class);
                            if (currentActivityScheduledDate >= startDate && currentActivityScheduledDate <= currentDate
                                    && type.equals(currentActivityType)) {
                                statusMap = new HashMap<>();
                                currentActivityCompany = activityDs.child("customer-info/companyName").getValue(String.class);
                                String currentActivityStatus = activityDs.child("status").getValue(String.class);
                                if (!currentActivityStatus.equals("Declined by admin") && !currentActivityStatus.equals("Editing Service Report")
                                        && !currentActivityStatus.equals("Rescheduled") && !currentActivityStatus.equals("Approved by admin")) {
                                    statusMap.put(currentActivityStatus, (map.get(currentActivityCompany) == null || map.get(currentActivityCompany).get(currentActivityStatus) == null) ? 1
                                            : map.get(currentActivityCompany).get(currentActivityStatus) + 1);
                                    if (map.get(currentActivityCompany) == null) {
                                        map.put(currentActivityCompany, statusMap);
                                    } else {
                                        map.get(currentActivityCompany).put(currentActivityStatus, statusMap.get(currentActivityStatus));
                                    }
                                }
                            }

                        }
                        for (Map.Entry<String, Map<String, Integer>> entry : map.entrySet()) {
                            String company = entry.getKey().contains("Pvt Ltd") ? entry.getKey().replace("Pvt Ltd", "") : entry.getKey();
                            company = company.contains("Instruments & Consultancy") ? company.replace("Instruments & Consultancy", "") : company;
                            company = company.contains("process technologies") ? company.replace("process technologies", "") : company;

                            row = (TableRow) getLayoutInflater().inflate(R.layout.table_row_layout, null);

                            t1 = (TextView) getLayoutInflater().inflate(R.layout.table_layout, null);
                            t1.setTextColor(getResources().getColor(R.color.view_black));
                            t2 = (TextView) getLayoutInflater().inflate(R.layout.table_layout, null);
                            t2.setTextColor(getResources().getColor(R.color.view_black));
                            t3 = (TextView) getLayoutInflater().inflate(R.layout.table_layout, null);
                            t3.setTextColor(getResources().getColor(R.color.view_black));
                            t4 = (TextView) getLayoutInflater().inflate(R.layout.table_layout, null);
                            t4.setTextColor(getResources().getColor(R.color.view_black));
                            t5 = (TextView) getLayoutInflater().inflate(R.layout.table_layout, null);
                            t5.setTextColor(getResources().getColor(R.color.view_black));
                            t1.setText(company);
                            t2.setText(map.get(entry.getKey()).get("Resolved") != null ? map.get(entry.getKey()).get("Resolved").toString() : "0");
                            t3.setText(map.get(entry.getKey()).get("Waiting for spares") != null ? map.get(entry.getKey()).get("Waiting for spares").toString() : "0");
                            t4.setText(map.get(entry.getKey()).get("Waiting for approval") != null ? map.get(entry.getKey()).get("Waiting for approval").toString() : "0");
                            t5.setText(map.get(entry.getKey()).get("Scheduled") != null ? map.get(entry.getKey()).get("Scheduled").toString() : "0");
                            t1.setTextSize(13);
                            t2.setTextSize(12);
                            t3.setTextSize(12);
                            t4.setTextSize(12);
                            t5.setTextSize(12);
                            row.addView(t1);
                            row.addView(t2);
                            row.addView(t3);
                            row.addView(t4);
                            row.addView(t5);
                            tableLayout.addView(row, new TableLayout.LayoutParams(
                                    TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        }
                        Commons.dismissProgressDialog(progressDialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Commons.dismissProgressDialog(progressDialog);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            activities.addValueEventListener(activitiesValueListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}