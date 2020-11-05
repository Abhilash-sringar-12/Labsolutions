package com.application.labsolutions.engineer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.application.labsolutions.pojos.DateInfo;
import com.application.labsolutions.pojos.UserInfo;
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
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ViewAssignedActivities extends AppCompatActivity {
    private TextView companyName, instrumentId, problemDescription, customerName, scheduledDateAndTime;
    FirebaseAuth firebaseAuth;
    Spinner spinner = null;
    Button assign;
    Button decline;
    ApiService apiService;
    HashMap<String, String> spinnerMap = new HashMap<String, String>();
    ArrayList<String> keys = new ArrayList();
    UserInfo userInfo;
    String workAdminId;
    String workAdminTokenId;
    String customerId;
    String customerMailId;
    String customerTokenId;
    String adminTokenId;
    String instrumentIdValue;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_engineer_view_activity_details);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("View Assigned Call Activities");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            final String activityId = intent.getStringExtra("activityId");
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            customerName = findViewById(R.id.customerName);
            companyName = findViewById(R.id.activityCompanyName);
            instrumentId = findViewById(R.id.activityInstrumentId);
            problemDescription = findViewById(R.id.activityProblemDescription);
            scheduledDateAndTime = findViewById(R.id.scheduledDateAndTime);
            if (!activityId.isEmpty()) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentActivityDs = rootRef.child("activities").child(activityId);
                if (currentActivityDs != null) {
                    final ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot currentActivityInfo = snapshot.child("customer-info");
                            DataSnapshot activityInfo = snapshot.child("activity-info");
                            DataSnapshot scheduledInfo = snapshot.child("scheduled-info");
                            String customerNameValue = currentActivityInfo.child("user").getValue() != null
                                    ? currentActivityInfo.child("user").getValue(String.class) : "";
                            customerMailId = currentActivityInfo.child("mailId").getValue() != null
                                    ? currentActivityInfo.child("mailId").getValue(String.class) : "";
                            String modelAndMake = activityInfo.child("modelAndMake").getValue() != null
                                    ? activityInfo.child("modelAndMake").getValue(String.class) : "";
                            instrumentIdValue = activityInfo.child("instrumentId").getValue() != null
                                    ? activityInfo.child("instrumentId").getValue(String.class) : "";
                            String problemDescriptionValue = activityInfo.child("problemDescription").getValue() != null
                                    ? activityInfo.child("problemDescription").getValue(String.class) : "";
                            String companyNameValue = currentActivityInfo.child("companyName").getValue() != null
                                    ? currentActivityInfo.child("companyName").getValue(String.class) : "";
                            String scheduledDate = scheduledInfo.child("date").getValue() != null ? scheduledInfo.child("date").getValue(String.class) : "";
                            String scheduledTime = scheduledInfo.child("time").getValue() != null ? scheduledInfo.child("time").getValue(String.class) : "";
                            scheduledDateAndTime.setText(scheduledDate + " " + scheduledTime);
                            customerName.setText(customerNameValue);
                            instrumentId.setText(instrumentIdValue);
                            companyName.setText(companyNameValue);
                            problemDescription.setText("Model and Make : " + modelAndMake + "\nProblem description : " + problemDescriptionValue);
                            final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot != null) {
                                        String companyName = snapshot.child("companyName").getValue() != null
                                                ? snapshot.child("companyName").getValue(String.class) : "";
                                        String department = snapshot.child("department").getValue() != null
                                                ? snapshot.child("department").getValue(String.class) : "";
                                        String userName = snapshot.child("user").getValue(String.class);
                                        String phoneNumber = snapshot.child("phoneNumber").getValue() != null
                                                ? snapshot.child("phoneNumber").getValue(String.class) : "";
                                        String emailId = snapshot.child("mailId").getValue() != null ?
                                                snapshot.child("mailId").getValue(String.class) : "";
                                        String type = snapshot.child("type").getValue() != null
                                                ? snapshot.child("type").getValue(String.class) : "";
                                        String companyAddress = snapshot.child("companyAddress").getValue() != null
                                                ? snapshot.child("companyAddress").getValue(String.class) : "";
                                        userInfo = new UserInfo(userName, emailId, phoneNumber, companyName, department, type, companyAddress);
                                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child("workadmin");
                                        final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child("admin");
                                        final DatabaseReference customerDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference()
                                                .child("users");
                                        final ValueEventListener valueEventListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChildren()) {
                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                        workAdminId = ds.getKey();
                                                        workAdminTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                                    }
                                                }
                                                ValueEventListener adminValueEventListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.hasChildren()) {
                                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                                adminTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                                            }
                                                        }

                                                        final ValueEventListener customerValueEventListener = new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                if (snapshot.hasChildren()) {
                                                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                                                        customerTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                                                        customerId = ds.getKey();
                                                                    }
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
                            databaseReferenceUser.addValueEventListener(valueEventListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    };
                    currentActivityDs.addListenerForSingleValueEvent(eventListener);
                }

            }

            assign = findViewById(R.id.approve);
            decline = findViewById(R.id.enginnerDecline);
            assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        progressDialog = ProgressDialog.show(ViewAssignedActivities.this, "Please wait", "Processing....", true, false);
                        FirebaseDatabase.getInstance().getReference()
                                .child("activities").child(activityId).child("engineer-info").setValue(userInfo)
                                .addOnCompleteListener(ViewAssignedActivities.this, new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            String startDate = DateUtility.getCurrentDate();
                                            String startTime = DateUtility.getCurrentTime();
                                            final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("activities").child(activityId).child("status").setValue("Scheduled").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("activities").child(activityId).child("engineer-approved-info").setValue(dateInfo).addOnCompleteListener(ViewAssignedActivities.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    FirebaseDatabase.getInstance().getReference()
                                                                            .child("activities").child(activityId).child("engineer-approved-info").child("engineer-availability").setValue("available").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                            if (task.isSuccessful()) {
                                                                                SendNotification.notify(adminTokenId, "Labsolutions", userInfo.getUser() + " accepted the call for " + instrumentIdValue, apiService, "adminAllActivities");
                                                                                SendNotification.notify(workAdminTokenId, "Labsolutions", userInfo.getUser() + "  accepted the call for " + instrumentIdValue, apiService, "workAdminAllActivities");
                                                                                SendNotification.notify(customerTokenId, "Labsolutions", userInfo.getUser() + " will be working on the call for " + instrumentIdValue, apiService, "customerCurrentActivity");
                                                                                Commons.dismissProgressDialog(progressDialog);
                                                                                Toast.makeText(ViewAssignedActivities.this, "You have accepted the call request", Toast.LENGTH_SHORT).show();
                                                                                Intent intent = new Intent(ViewAssignedActivities.this, AssignedActivities.class);
                                                                                startActivity(intent);
                                                                                finishAffinity();
                                                                            }
                                                                        }
                                                                    });
                                                                } else {
                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                    Toast.makeText(ViewAssignedActivities.this, "Please try again", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ViewAssignedActivities.this, "Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        new AlertDialog.Builder(ViewAssignedActivities.this)
                                .setTitle("Decline Activity")
                                .setMessage("Are you sure you want to decline this activity?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog = ProgressDialog.show(ViewAssignedActivities.this, "Please wait", "Processing....", true, false);
                                        String startDate = DateUtility.getCurrentDate();
                                        String startTime = DateUtility.getCurrentTime();
                                        final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
                                        FirebaseDatabase.getInstance().getReference()
                                                .child("activity-users").child("current-activity").child(activityId).child("work-admin").setValue(workAdminId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference()
                                                            .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                FirebaseDatabase.getInstance().getReference().child("activities").child(activityId).child("declined-data").child(UUID.randomUUID().toString()).setValue(userInfo.getUser()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {

                                                                            FirebaseDatabase.getInstance().getReference()
                                                                                    .child("activities").child(activityId).child("engineer-approved-info").setValue(dateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                                    if (task.isSuccessful()) {
                                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                                .child("activities").child(activityId).child("engineer-approved-info").child("engineer-availability").setValue("unavailable").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    SendNotification.notify(adminTokenId, "Labsolutions", userInfo.getUser() + " dropped the call for " + instrumentIdValue, apiService, "workAdminAssignActivity");
                                                                                                    SendNotification.notify(workAdminTokenId, "Labsolutions", userInfo.getUser() + " dropped the call for " + instrumentIdValue, apiService, "workAdminAssignActivity");
                                                                                                    Toast.makeText(ViewAssignedActivities.this, "You have dropped the call", Toast.LENGTH_SHORT).show();
                                                                                                    Intent intent = new Intent(ViewAssignedActivities.this, AssignedActivities.class);
                                                                                                    startActivity(intent);
                                                                                                    finishAffinity();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        Toast.makeText(ViewAssignedActivities.this, "Please try again", Toast.LENGTH_SHORT).show();

                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                }
                                            }
                                        });
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
}
