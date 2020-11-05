package com.application.labsolutions.workadmin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.dateutils.DateUtility;
import com.application.labsolutions.pojos.DateInfo;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WorkAdminActivityDetails extends AppCompatActivity {
    private TextView companyName, instrumentId, scheduledDateAndTime, problemDescription;
    FirebaseAuth firebaseAuth;
    Spinner spinner = null;
    Button assign;
    Button decline;
    String adminMailId;
    String adminTokenId;
    String customerMailId;
    String customerTokenId;
    HashMap<String, String> spinnerMap = new HashMap<String, String>();
    ArrayList<String> keys = new ArrayList();
    ApiService apiService;
    String instrumentIdValue;
    Object waitingData, waitingEndData;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_workadmin_activity_details);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Assign Call Activity");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            spinner = findViewById(R.id.engineer);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            final String activityId = intent.getStringExtra("activityId");
            companyName = findViewById(R.id.activityCompanyName);
            instrumentId = findViewById(R.id.activityInstrumentId);
            scheduledDateAndTime = findViewById(R.id.scheduledDateAndTime);
            problemDescription = findViewById(R.id.activityProblemDescription);
            if (!activityId.isEmpty()) {
                progressDialog = ProgressDialog.show(WorkAdminActivityDetails.this, "Please wait", "Loading data.....", true, false);
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentActivityDs = rootRef.child("activities").child(activityId);
                DatabaseReference users = rootRef.child("users");
                if (currentActivityDs != null) {
                    final ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (snapshot.hasChildren()) {
                                DataSnapshot currentActivityInfo = snapshot.child("customer-info");
                                DataSnapshot scheduledInfo = snapshot.child("scheduled-info");
                                DataSnapshot activityInfo = snapshot.child("activity-info");
                                instrumentIdValue = activityInfo.child("instrumentId").getValue() != null ?
                                        activityInfo.child("instrumentId").getValue(String.class) : "";
                                String modelAndMake = activityInfo.child("modelAndMake").getValue() != null
                                        ? activityInfo.child("modelAndMake").getValue(String.class) : "";
                                String problemDescriptionValue = activityInfo.child("problemDescription").getValue().toString();
                                String companyNameValue = currentActivityInfo.child("companyName").getValue().toString();
                                String scheduledDate = scheduledInfo.child("date").getValue() != null ? scheduledInfo.child("date").getValue(String.class) : "";
                                String scheduledTime = scheduledInfo.child("time").getValue() != null ? scheduledInfo.child("time").getValue(String.class) : "";
                                waitingData = snapshot.child("waiting-data").getValue() != null ? snapshot.child("waiting-data").getValue() : null;
                                waitingEndData = snapshot.child("waiting-data").child("end-data").getValue() != null
                                        ? snapshot.child("waiting-data").child("end-data").getValue() : null;
                                scheduledDateAndTime.setText(scheduledDate + " " + scheduledTime);
                                customerMailId = currentActivityInfo.child("mailId").getValue().toString();
                                instrumentId.setText(instrumentIdValue);
                                companyName.setText(companyNameValue);
                                problemDescription.setText("Model and Make : " + modelAndMake + "\nProblem description : " + problemDescriptionValue);
                                final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child("admin");
                                final DatabaseReference customerDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference()
                                        .child("users");
                                ValueEventListener adminValueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChildren()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                adminTokenId = ds.child("token").child("token").getValue() != null
                                                        ? ds.child("token").child("token").getValue(String.class) : "";
                                            }
                                        }
                                        final ValueEventListener customerValueEventListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChildren()) {
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        customerTokenId = ds.child("token").child("token").getValue() != null
                                                                ? ds.child("token").child("token").getValue(String.class) : "";
                                                    }
                                                    progressDialog.dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        };
                                        customerDatabaseReferenceUser.orderByChild("mailId").equalTo(customerMailId).addValueEventListener(customerValueEventListener);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                adminDatabaseReference.addValueEventListener(adminValueEventListener);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    };

                    currentActivityDs.addListenerForSingleValueEvent(eventListener);
                    ValueEventListener usersEventListener = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            keys.add("");
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                spinnerMap.put(ds.child("user").getValue().toString(), ds.getKey());
                                keys.add(ds.child("user").getValue().toString());
                            }

                            addEngineers(keys);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    users.orderByChild("userType").equalTo("Engineer").addValueEventListener(usersEventListener);
                }

            }

            assign = findViewById(R.id.approve);
            decline = findViewById(R.id.decline);
            assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final String selectedEngineer = spinner.getSelectedItem().toString();
                        if (selectedEngineer.isEmpty()) {
                            Toast.makeText(WorkAdminActivityDetails.this, "Please Choose an engineer", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog = ProgressDialog.show(WorkAdminActivityDetails.this, "Please wait", "In Progres....", true, false);
                            final String engineerId = spinnerMap.get(selectedEngineer);
                            DatabaseReference engDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(engineerId);
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    final String engineerToken = snapshot.child("token").child("token").getValue() != null
                                            ? snapshot.child("token").child("token").getValue().toString() : "";
                                    FirebaseDatabase.getInstance().getReference()
                                            .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(engineerId).addOnCompleteListener(WorkAdminActivityDetails.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                String startDate = DateUtility.getCurrentDate();
                                                String startTime = DateUtility.getCurrentTime();
                                                final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("activity-users").child("current-activity").child(activityId).child("work-admin").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("activities").child(activityId).child("status").setValue("Approved by admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        if (waitingData != null && waitingEndData == null) {
                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("activities").child(activityId).child("waiting-data").child("end-data").setValue(dateInfo);
                                                                        }
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("activities").child(activityId).child("admin-approved-info").setValue(dateInfo).addOnCompleteListener(WorkAdminActivityDetails.this, new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                                progressDialog.dismiss();
                                                                                if (task.isSuccessful()) {
                                                                                    SendNotification.notify(adminTokenId, "Labsolutions", "Call for " + instrumentIdValue + " is assigned to " + selectedEngineer, apiService, "adminAllActivities");
                                                                                    SendNotification.notify(engineerToken, "Labsolutions", "Call for " + instrumentIdValue + " is assigned to you", apiService, "engineerAssignActivity");
                                                                                    SendNotification.notify(customerTokenId, "Labsolutions", "Call for " + instrumentIdValue + " is approved", apiService, "customerCurrentActivity");
                                                                                    Toast.makeText(WorkAdminActivityDetails.this, "Successfully assigned the call to the engineer", Toast.LENGTH_SHORT).show();
                                                                                    backToWorkadminAssignActivity();
                                                                                    finishAffinity();
                                                                                } else {
                                                                                    Toast.makeText(WorkAdminActivityDetails.this, "Failed to assign the call to the engineer", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(WorkAdminActivityDetails.this, "Failed to assign the activity to the engineer", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            engDatabaseReference.addValueEventListener(valueEventListener);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new AlertDialog.Builder(WorkAdminActivityDetails.this)
                                .setTitle("Decline Activity")
                                .setMessage("Are you sure you want to decline this activity?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent1 = new Intent(WorkAdminActivityDetails.this, DeclineRegisteredCallActivity.class);
                                        intent1.putExtra("adminTokenId", adminTokenId);
                                        intent1.putExtra("customerTokenId", customerTokenId);
                                        intent1.putExtra("activityId", activityId);
                                        intent1.putExtra("instrumentIdValue", instrumentIdValue);
                                        startActivity(intent1);
                                    }
                                }).setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.alert)
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addEngineers(List<String> categories) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Engineers");
    }

    private void backToWorkadminAssignActivity() {
        Intent intent = new Intent(WorkAdminActivityDetails.this, WorkAdminAssignActivity.class);
        startActivity(intent);
    }

}
