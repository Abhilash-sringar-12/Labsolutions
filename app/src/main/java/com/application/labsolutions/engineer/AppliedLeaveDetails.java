package com.application.labsolutions.engineer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class AppliedLeaveDetails extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Spinner spinner = null;
    List<String> categories = new ArrayList<String>();
    EditText leaveFrom;
    EditText backOn;
    TextView leaveText;
    Button cancelLeave;
    long fromDateTimeStamp, backOnDateTimeStamp, currentDateTimeStamp;
    String leaveType, leaveId, fromDate, backOnDate, totalLeaves;
    ProgressDialog progressDialog;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_apply_leave);
            cancelLeave = findViewById(R.id.applyLeave);
            cancelLeave.setText("Cancel Leaves");
            spinner = findViewById(R.id.leaveType);
            leaveFrom = findViewById(R.id.leaveFrom);
            backOn = findViewById(R.id.backOn);
            leaveText = findViewById(R.id.leaveText);
            leaveText.setVisibility(View.GONE);
            firebaseAuth = FirebaseAuth.getInstance();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Leave Details");
                setSupportActionBar(toolbar);
                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentUserLeavesDs = rootRef.child("applied-leaves").child(currentUser);
                progressDialog = ProgressDialog.show(AppliedLeaveDetails.this, "Please wait", "Loading your Leaves.....", true, false);
                Intent intent = getIntent();
                fromDate = intent.getStringExtra("leaveFrom");
                backOnDate = intent.getStringExtra("backOn");
                leaveType = intent.getStringExtra("leaveType");
                totalLeaves = intent.getStringExtra("totalLeaves");
                leaveId = intent.getStringExtra("leaveId");
                fromDateTimeStamp = DateUtility.getTimeStamForLeaves(fromDate);
                backOnDateTimeStamp = DateUtility.getTimeStamForLeaves(backOnDate);
                currentDateTimeStamp = new Date().getTime();


                populateLeaveDetails();
                cancelLeave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (currentDateTimeStamp > fromDateTimeStamp && currentDateTimeStamp < backOnDateTimeStamp) {
                                fromDate = DateUtility.formatDate(new Date().toString());
                            }
                            deleteLeavesFromCalender(fromDate, backOnDate, leaveType);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteLeavesFromCalender(String startdate, String enddate, final String leaveType) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            progressDialog = ProgressDialog.show(AppliedLeaveDetails.this, "Please wait", "Cancelling leaves!!!.....", true, false);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(startdate));
            final DatabaseReference calenderRef = FirebaseDatabase.getInstance().getReference().child("calender/");
            while (calendar.getTime().before(simpleDateFormat.parse(enddate))) {
                Date result = calendar.getTime();
                final String currentIndexDate = DateUtility.formatDate(result.toString());
                final String currentIndexMonth = monthName[Integer.parseInt(currentIndexDate.split("-")[1]) - 1];
                final String year = currentIndexDate.split("-")[2];
                calendar.add(Calendar.DATE, 1);
                if (result.getDay() != 0 && result.getDay() != 6) {
                    calenderRef.child(year + "/" + currentIndexMonth + "/" + currentIndexDate + "/" + firebaseAuth.getCurrentUser().getUid()).setValue(null);
                }
            }
            deleteAppliedLeaves(fromDate, backOnDate, leaveType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAppliedLeaves(final String startdate, final String enddate, final String leaveType) {
        try {
            LinkedHashMap<String, String> appliedLeavesMap = new LinkedHashMap<>();
            if ((currentDateTimeStamp > fromDateTimeStamp && currentDateTimeStamp < backOnDateTimeStamp) && !DateUtility.formatDate(new Date().toString()).equals(startdate)) {
                appliedLeavesMap.put("leaveFrom", startdate);
                appliedLeavesMap.put("backOn", DateUtility.formatDate(new Date().toString()));
                appliedLeavesMap.put("leaveType", leaveType);
            } else {
                appliedLeavesMap = null;
            }
            FirebaseDatabase.getInstance().getReference().child("applied-leaves/" + firebaseAuth.getCurrentUser().getUid() + "/" + leaveId).setValue(appliedLeavesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Commons.dismissProgressDialog(progressDialog);
                    if (task.isSuccessful()) {
                        final double noOfDays = leaveType.equals("HD") ? 0.5 : DateUtility.calculatedLeaves(startdate, enddate);
                        FirebaseDatabase.getInstance().getReference().child("leaves/" + firebaseAuth.getCurrentUser().getUid()).setValue(String.valueOf(Double.parseDouble((totalLeaves)) + noOfDays)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AppliedLeaveDetails.this, "Cancelled leave successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AppliedLeaveDetails.this, AppliedLeaves.class);
                                startActivity(intent);
                                finishAffinity();
                            }
                        });
                    } else {
                        Toast.makeText(AppliedLeaveDetails.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateLeaveDetails() {
        try {
            leaveFrom.setText(fromDate);
            backOn.setText(backOnDate);
            categories.add(leaveType);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(R.layout.spinner_item);
            spinner.setAdapter(dataAdapter);
            Commons.dismissProgressDialog(progressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}