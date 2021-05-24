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
import com.application.labsolutions.services.ApiService;
import com.application.labsolutions.services.Client;
import com.application.labsolutions.services.SendNotification;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class AppliedLeaveDetails extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Spinner spinner = null;
    List<String> categories = new ArrayList<String>();
    EditText leaveFrom;
    EditText backOn;
    TextView leaveText;
    Button cancelLeave;
    long fromDateTimeStamp, backOnDateTimeStamp, currentDateTimeStamp;
    String leaveType, leaveId, fromDate, backOnDate, totalLeaves, month, year, userName, adminTokenId;
    List<String> holidaysList = new ArrayList<>();
    ProgressDialog progressDialog;
    Boolean needNoteToDelete = false;
    ApiService apiService;
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
            Intent intent = getIntent();
            if (intent.getStringExtra("userType").equals("admin")) {
                cancelLeave.setVisibility(View.GONE);
            }

            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            firebaseAuth = FirebaseAuth.getInstance();
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Leave Details");
                setSupportActionBar(toolbar);
                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentUserLeavesDs = rootRef.child("applied-leaves").child(currentUser);
                progressDialog = ProgressDialog.show(AppliedLeaveDetails.this, "Please wait", "Loading your Leaves.....", true, false);
                fromDate = intent.getStringExtra("leaveFrom");
                backOnDate = intent.getStringExtra("backOn");
                if (backOnDate.isEmpty()) {
                    backOn.setVisibility(View.GONE);
                } else {
                    backOnDateTimeStamp = DateUtility.getTimeStamForLeaves(backOnDate);
                }
                leaveType = intent.getStringExtra("leaveType");
                totalLeaves = intent.getStringExtra("totalLeaves");
                leaveId = intent.getStringExtra("leaveId");
                userName = intent.getStringExtra("userName");
                fromDateTimeStamp = DateUtility.getTimeStamForLeaves(fromDate);
                currentDateTimeStamp = new Date().getTime();
                month = monthName[Integer.parseInt(fromDate.split("-")[1]) - 1];
                year = fromDate.split("-")[2];
                final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference().child("admin");
                final DatabaseReference holidays = rootRef.child("holidays");
                final DatabaseReference calenderRef = FirebaseDatabase.getInstance().getReference().child("calender/");
                final DatabaseReference databaseReference = calenderRef.child(year + "/" + month + "/" + fromDate + "/" + firebaseAuth.getCurrentUser().getUid());
                final ValueEventListener holidaysListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    holidaysList.add(ds.getKey());
                                    populateLeaveDetails();

                                    ValueEventListener adminValueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChildren()) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    adminTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                                }
                                            }
                                            if (leaveType.equals("HD") && DateUtility.formatDate(new Date().toString()).equals(fromDate)) {
                                                final ValueEventListener valueEventListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.hasChildren() && (snapshot.child("loginDetails").getValue() != null))
                                                            needNoteToDelete = true;

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                };
                                                databaseReference.addValueEventListener(valueEventListener);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    };
                                    adminDatabaseReference.addValueEventListener(adminValueEventListener);

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                holidays.addValueEventListener(holidaysListener);
                cancelLeave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            progressDialog = ProgressDialog.show(AppliedLeaveDetails.this, "Please wait", "Cancelling leaves!!!.....", true, false);
                            if (leaveType.equals("EL")) {
                                deleteLeavesFromCalender(fromDate, backOnDate, leaveType);
                            } else {
                                final String currentIndexMonth = monthName[Integer.parseInt(fromDate.split("-")[1]) - 1];
                                final String year = fromDate.split("-")[2];
                                if (needNoteToDelete) {
                                    calenderRef.child(year + "/" + currentIndexMonth + "/" + fromDate + "/" + firebaseAuth.getCurrentUser().getUid() + "/type").setValue("PA").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            deleteBasedOnconditions(task);
                                        }
                                    });
                                } else {
                                    calenderRef.child(year + "/" + currentIndexMonth + "/" + fromDate + "/" + firebaseAuth.getCurrentUser().getUid()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            deleteBasedOnconditions(task);
                                        }
                                    });
                                }
                            }
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

    public void deleteBasedOnconditions(Task<Void> task) {
        if (task.isSuccessful()) {
            if (leaveType.equals("HD")) {
                proceedDeleting(new LinkedHashMap<String, String>(), 0.5);
            } else {
                proceedDeleting(new LinkedHashMap<String, String>(), 1.0);
            }
        }

    }

    public void deleteLeavesFromCalender(String startdate, String enddate, final String leaveType) {
        try {

            if (currentDateTimeStamp > fromDateTimeStamp && currentDateTimeStamp < backOnDateTimeStamp) {
                startdate = DateUtility.formatDate(new Date().toString());
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            DatabaseReference calenderRef = FirebaseDatabase.getInstance().getReference().child("calender/");
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(startdate));
            while (calendar.getTime().before(simpleDateFormat.parse(enddate))) {
                Date result = calendar.getTime();
                final String currentIndexDate = DateUtility.formatDate(result.toString());
                final String currentIndexMonth = monthName[Integer.parseInt(currentIndexDate.split("-")[1]) - 1];
                final String year = currentIndexDate.split("-")[2];
                calendar.add(Calendar.DATE, 1);
                if (result.getDay() != 0 && result.getDay() != 6) {
                    if (needNoteToDelete) {
                        calenderRef.child(year + "/" + currentIndexMonth + "/" + currentIndexDate + "/" + firebaseAuth.getCurrentUser().getUid() + "/type").setValue("PA");
                    } else {
                        calenderRef.child(year + "/" + currentIndexMonth + "/" + currentIndexDate + "/" + firebaseAuth.getCurrentUser().getUid()).setValue(null);
                    }
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
            final double calculate;
            if ((currentDateTimeStamp > fromDateTimeStamp && currentDateTimeStamp < backOnDateTimeStamp) && !DateUtility.formatDate(new Date().toString()).equals(startdate)) {
                appliedLeavesMap.put("leaveFrom", startdate);
                appliedLeavesMap.put("backOn", DateUtility.formatDate(new Date().toString()));
                appliedLeavesMap.put("leaveType", leaveType);
                calculate = DateUtility.calculatedLeaves(DateUtility.formatDate(new Date().toString()), enddate, holidaysList);
            } else {
                appliedLeavesMap = null;
                calculate = DateUtility.calculatedLeaves(startdate, enddate, holidaysList);
            }
            proceedDeleting(appliedLeavesMap, calculate);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedDeleting(LinkedHashMap<String, String> appliedLeavesMap, final double calculate) {
        try {
            FirebaseDatabase.getInstance().getReference().child("applied-leaves/" + firebaseAuth.getCurrentUser().getUid() + "/" + leaveId).setValue(appliedLeavesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Commons.dismissProgressDialog(progressDialog);
                    if (task.isSuccessful()) {
                        final double noOfDays = calculate;
                        FirebaseDatabase.getInstance().getReference().child("leaves/" + firebaseAuth.getCurrentUser().getUid()).setValue(String.valueOf(Double.parseDouble((totalLeaves)) + noOfDays)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                SendNotification.notify(adminTokenId, "Labsolutions", userName + " has cancelled leaves!!!", apiService, "applyLeaves");
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
            categories.clear();
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