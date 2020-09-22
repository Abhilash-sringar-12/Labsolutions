package com.example.labsolutions.workadmin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.labsolutions.R;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.customer.ActivityDetails;
import com.example.labsolutions.customer.CustomerActivity;
import com.example.labsolutions.dateutils.DateUtility;
import com.example.labsolutions.pojos.DateInfo;
import com.example.labsolutions.services.ApiService;
import com.example.labsolutions.services.Client;
import com.example.labsolutions.services.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RescheduleCall extends AppCompatActivity {
    EditText scheduledDate, scheduledTime;
    Button reScheduleCall;
    FirebaseAuth firebaseAuth;
    ApiService apiService;
    int mYear, mMonth, mDay, mHour, mMinute;
    long previousScheduledStamp;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reschedule_call);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Reschedule Call");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            final String activityId = intent.getStringExtra("activityId");
            final String adminToken = intent.getStringExtra("adminToken");
            final String engineerToken = intent.getStringExtra("engineerToken");
            final String customerToken = intent.getStringExtra("customerToken");
            final String instrumentIdValue = intent.getStringExtra("instrumentIdValue");
            scheduledDate = findViewById(R.id.reScheduleDate);
            scheduledTime = findViewById(R.id.reScheduleTime);
            reScheduleCall = findViewById(R.id.reScheduleCall);
            firebaseAuth = FirebaseAuth.getInstance();
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            final DatabaseReference selectedCall = FirebaseDatabase.getInstance().getReference("activities").child(activityId);

            getSelectedCallDetails(activityId, selectedCall);
            scheduledDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final Calendar c = Calendar.getInstance();
                        mYear = c.get(Calendar.YEAR);
                        mMonth = c.get(Calendar.MONTH);
                        mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(RescheduleCall.this,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        Calendar cal = Calendar.getInstance();
                                        cal.setTimeInMillis(0);
                                        cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                        Date chosenDate = cal.getTime();
                                        DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                                        String df_medium_us_str = df_medium_us.format(chosenDate);
                                        scheduledDate.setText(df_medium_us_str);
                                    }
                                }, mYear, mMonth, mDay);
                        DatePicker datePicker = datePickerDialog.getDatePicker();
                        datePicker.setMinDate(System.currentTimeMillis() - 1000);
                        datePickerDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            scheduledTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final Calendar c = Calendar.getInstance();
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(RescheduleCall.this,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay,
                                                          int minute) {

                                        scheduledTime.setText(hourOfDay + ":" + minute);
                                    }
                                }, mHour, mMinute, false);
                        timePickerDialog.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            reScheduleCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validateFields()) {
                        new AlertDialog.Builder(RescheduleCall.this)
                                .setTitle("Labsolutions")
                                .setMessage("Do you want to reschedule call?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        String newScheduleDate = scheduledDate.getText().toString();
                                        String newScheduleTime = scheduledTime.getText().toString() + ":00";
                                        final long newScheduleStamp = Long.parseLong(DateUtility.getTimeStamp(newScheduleDate + " " + newScheduleTime));
                                        if (newScheduleStamp > previousScheduledStamp) {
                                            progressDialog = ProgressDialog.show(RescheduleCall.this, "Please wait", "Rescheduling Call....", true, false);
                                            final DateInfo scheduledDateInfo = new DateInfo(newScheduleDate, newScheduleTime, null);
                                            final DatabaseReference databaseReference = selectedCall.child("scheduled-info");
                                            databaseReference.setValue(scheduledDateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        databaseReference.child("timeStamp").setValue(String.valueOf(newScheduleStamp)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    selectedCall.child("status").setValue("Rescheduled").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Commons.dismissProgressDialog(progressDialog);
                                                                            if (task.isSuccessful()) {
                                                                                SendNotification.notify(adminToken, "Labsolutions", "Call for " + instrumentIdValue + " is rescheduled", apiService, "adminAllActivities");
                                                                                SendNotification.notify(engineerToken, "Labsolutions", "Call for " + instrumentIdValue + " is rescheduled", apiService, "engineerAssignActivity");
                                                                                SendNotification.notify(customerToken, "Labsolutions", "Call for " + instrumentIdValue + " is rescheduled", apiService, "customerCurrentActivity");
                                                                                Toast.makeText(RescheduleCall.this, "Successfully rescheduled call", Toast.LENGTH_SHORT).show();
                                                                                Intent intent1 = new Intent(RescheduleCall.this, AllActivities.class);
                                                                                startActivity(intent1);
                                                                                finishAffinity();
                                                                            } else {
                                                                                Toast.makeText(RescheduleCall.this, "Please try again....", Toast.LENGTH_SHORT).show();
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
                                            Toast.makeText(RescheduleCall.this, "You can only postpone the schedule date & time", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }).setNegativeButton(android.R.string.no, null)
                                .setIcon(R.drawable.alert)
                                .show();

                    } else {
                        Toast.makeText(RescheduleCall.this, "Please select date and time", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getSelectedCallDetails(String activityId, DatabaseReference selectedCall) {
        try {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        DataSnapshot scheduledData = snapshot.child("scheduled-info");
                        if (scheduledData.getValue() != null) {
                            scheduledDate.setText(scheduledData.child("date").getValue() != null ? scheduledData.child("date").getValue(String.class) : "");
                            scheduledTime.setText(scheduledData.child("time").getValue() != null ? scheduledData.child("time").getValue(String.class) : "");
                            previousScheduledStamp = Long.parseLong((String) (scheduledData.child("timeStamp").getValue() != null ? scheduledData.child("timeStamp").getValue() : 0l));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            selectedCall.addValueEventListener(valueEventListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (scheduledDate.getText().toString().isEmpty() || scheduledTime.getText().toString().isEmpty()) {
            isValid = false;
        }
        return isValid;
    }
}
