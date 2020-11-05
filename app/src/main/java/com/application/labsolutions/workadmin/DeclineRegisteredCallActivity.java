package com.application.labsolutions.workadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.application.labsolutions.pojos.DateInfo;
import com.application.labsolutions.services.ApiService;
import com.application.labsolutions.services.Client;
import com.application.labsolutions.services.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DeclineRegisteredCallActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ApiService apiService;
    String activityId, adminTokenId, customerTokenId, instrumentIdValue;
    Button submit;
    TextInputLayout declineReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_decline_registered_call);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Decline Call Activity");
            setSupportActionBar(toolbar);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            Intent intent = getIntent();
            declineReason = (TextInputLayout) findViewById(R.id.editTextDeclineReason);
            activityId = intent.getStringExtra("activityId");
            adminTokenId = intent.getStringExtra("adminTokenId");
            customerTokenId = intent.getStringExtra("customerTokenId");
            instrumentIdValue = intent.getStringExtra("instrumentIdValue");
            submit = findViewById(R.id.declineCall);
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!declineReason.getEditText().getText().toString().isEmpty()) {
                            progressDialog = ProgressDialog.show(DeclineRegisteredCallActivity.this, "Please wait", "In progress...", true, false);
                            update(activityId);
                        } else {
                            Toast.makeText(DeclineRegisteredCallActivity.this, "Please enter decline reason", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void update(final String activityId) {
        try {
            FirebaseDatabase.getInstance().getReference()
                    .child("activity-users").child("current-activity").child(activityId).child("customer").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String startDate = DateUtility.getCurrentDate();
                    String startTime = DateUtility.getCurrentTime();
                    final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
                    if (snapshot.getValue() != null) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("activity-users").child("completed-activity").child(activityId).child("customer").setValue(snapshot.getValue().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseDatabase.getInstance().getReference()
                                        .child("activity-users").child("current-activity").child(activityId).child("customer").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("activity-users").child("current-activity").child(activityId).child("work-admin").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("activities").child(activityId).child("status").setValue("Declined by admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                FirebaseDatabase.getInstance().getReference()
                                                                        .child("activities").child(activityId).child("admin-approved-info").setValue(dateInfo).addOnCompleteListener(DeclineRegisteredCallActivity.this, new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("declineReason").setValue(declineReason.getEditText().getText().toString());
                                                                        Commons.dismissProgressDialog(progressDialog);
                                                                        if (task.isSuccessful()) {
                                                                            SendNotification.notify(adminTokenId, "Labsolutions", "Work Admin declined call for " + instrumentIdValue, apiService, "adminAllActivities");
                                                                            SendNotification.notify(customerTokenId, "Labsolutions", "Work Admin declined call for " + instrumentIdValue, apiService, "customerAllActivities");
                                                                            Toast.makeText(DeclineRegisteredCallActivity.this, "Successfully declined the call", Toast.LENGTH_SHORT).show();
                                                                            Intent intent = new Intent(DeclineRegisteredCallActivity.this, WorkAdminAssignActivity.class);
                                                                            startActivity(intent);
                                                                            finishAffinity();
                                                                        } else {
                                                                            Toast.makeText(DeclineRegisteredCallActivity.this, "Failed to decline the call", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

