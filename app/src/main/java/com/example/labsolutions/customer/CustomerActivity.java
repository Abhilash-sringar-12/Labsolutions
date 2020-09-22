package com.example.labsolutions.customer;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.labsolutions.R;
import com.example.labsolutions.admin.LoginActivity;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.dateutils.DateUtility;
import com.example.labsolutions.pojos.ActivityInfo;
import com.example.labsolutions.pojos.DateInfo;
import com.example.labsolutions.pojos.UserInfo;
import com.example.labsolutions.services.ApiService;
import com.example.labsolutions.services.Client;
import com.example.labsolutions.services.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CustomerActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button createActivity;
    TextInputLayout propblemDescription;
    EditText scheduledDate, scheduledTime;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ProgressDialog progressDialog;
    UserInfo userInfo;
    String adminTokenId;
    ApiService apiService;
    int mYear, mMonth, mDay, mHour, mMinute;
    AutoCompleteTextView autocomplete;
    final List<String> categories = new ArrayList<String>();
    final Map<String, Boolean> instrumentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_customer);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Register Call");
            setSupportActionBar(toolbar);
            propblemDescription = findViewById(R.id.editTextProblemDescription);
            radioGroup = (RadioGroup) findViewById(R.id.calltype);
            scheduledDate = findViewById(R.id.editDate);
            scheduledTime = findViewById(R.id.editTime);
            autocomplete = (AutoCompleteTextView)
                    findViewById(R.id.autoCompleteTextView1);
            firebaseAuth = FirebaseAuth.getInstance();
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            progressDialog = ProgressDialog.show(CustomerActivity.this, "Please wait", "Loading....", true, false);
            final String currentUser = firebaseAuth.getCurrentUser().getUid();
            final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser);
            final DatabaseReference databaseReferenceUInstruments = FirebaseDatabase.getInstance().getReference().child("instruments");
            databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String companyName = snapshot.child("companyName").getValue() != null
                            ? snapshot.child("companyName").getValue(String.class) : "";
                    String department = snapshot.child("department").getValue() != null
                            ? snapshot.child("department").getValue(String.class) : "";
                    String userName = snapshot.child("user").getValue() != null
                            ? snapshot.child("user").getValue(String.class) : "";
                    String phoneNumber = snapshot.child("phoneNumber").getValue() != null
                            ? snapshot.child("phoneNumber").getValue(String.class) : "";
                    String emailId = snapshot.child("mailId").getValue() != null
                            ? snapshot.child("mailId").getValue(String.class) : "";
                    String type = snapshot.child("type").getValue() != null
                            ? snapshot.child("type").getValue(String.class) : "";
                    userInfo = new UserInfo(userName, emailId, phoneNumber, companyName, department, type);
                    loadInstruments(databaseReferenceUInstruments, companyName, department);
                    final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("admin");
                    ValueEventListener adminValueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    adminTokenId = ds.child("token").child("token").getValue() != null
                                            ? ds.child("token").child("token").getValue(String.class) : "";
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    adminDatabaseReference.addValueEventListener(adminValueEventListener);
                    Commons.dismissProgressDialog(progressDialog);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            createActivity = findViewById(R.id.createActivity);
            scheduledDate.setKeyListener(null);
            scheduledTime.setKeyListener(null);
            scheduledDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    mYear = c.get(Calendar.YEAR);
                    mMonth = c.get(Calendar.MONTH);
                    mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(CustomerActivity.this,
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
                }
            });
            scheduledTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    mHour = c.get(Calendar.HOUR_OF_DAY);
                    mMinute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(CustomerActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay,
                                                      int minute) {

                                    scheduledTime.setText(hourOfDay + ":" + minute);
                                }
                            }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });
            createActivity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                        final String instrumentIdSelected = autocomplete.getText().toString();
                        final String callType = radioButton.getText().toString();
                        final String problemDescriptionValue = propblemDescription.getEditText().getText().toString();
                        if (instrumentIdSelected.isEmpty() || !categories.contains(instrumentIdSelected)) {
                            Toast.makeText(CustomerActivity.this, "Please select an instrument from dropdown", Toast.LENGTH_SHORT).show();
                        } else if (!instrumentMap.get(instrumentIdSelected)) {
                            Toast.makeText(CustomerActivity.this, "Instrument selected is not under AMC dates! Please contact admin", Toast.LENGTH_SHORT).show();
                        } else if (scheduledDate.getText().toString().isEmpty()) {
                            Toast.makeText(CustomerActivity.this, "Please select a schedule date", Toast.LENGTH_SHORT).show();
                            scheduledDate.requestFocus();
                        } else if (scheduledTime.getText().toString().isEmpty()) {
                            Toast.makeText(CustomerActivity.this, "Please select a schedule time", Toast.LENGTH_SHORT).show();
                            scheduledTime.requestFocus();
                        } else if (problemDescriptionValue.isEmpty()) {
                            Toast.makeText(CustomerActivity.this, "Please enter problem description", Toast.LENGTH_SHORT).show();
                            propblemDescription.requestFocus();
                        } else if (Long.parseLong(DateUtility.getTimeStamp(scheduledDate.getText().toString() + " " + scheduledTime.getText().toString() + ":00")) < new Date().getTime()) {
                            Toast.makeText(CustomerActivity.this, "Scheduled time should not be less than the current time", Toast.LENGTH_SHORT).show();
                        } else {
                            propblemDescription.setError(null);
                            scheduledDate.setError(null);
                            scheduledTime.setError(null);
                            progressDialog = ProgressDialog.show(CustomerActivity.this, "Please wait", "Creating activity....", true, false);
                            DatabaseReference workAdminRef = FirebaseDatabase.getInstance().getReference("workadmin");
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String startDate = DateUtility.getCurrentDate();
                                    String startTime = DateUtility.getCurrentTime();
                                    final String scheduledDateValue = scheduledDate.getText().toString();
                                    final String scheduledTimeValue = scheduledTime.getText().toString() + ":00";
                                    final DateInfo scheduledDateInfo = new DateInfo(scheduledDateValue, scheduledTimeValue, null);
                                    final String issueId = UUID.randomUUID().toString();
                                    final String workadminId = snapshot.getChildren().iterator().next().getKey();
                                    final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
                                    FirebaseDatabase.getInstance().getReference("activities").child(issueId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                    ActivityInfo activityInfo = new ActivityInfo(instrumentIdSelected, callType, problemDescriptionValue);
                                    final String workAdminToken = snapshot.getChildren().iterator().next().child("token").child("token").getValue() != null
                                            ? snapshot.getChildren().iterator().next().child("token").child("token").getValue(String.class) : "";
                                    FirebaseDatabase.getInstance().getReference("activities").child(issueId)
                                            .child("activity-info").setValue(activityInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("activities").child(issueId)
                                                        .child("status").setValue("Waiting for approval").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseDatabase.getInstance().getReference("activities").child(issueId).child("customer-info").setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FirebaseDatabase.getInstance().getReference("activity-users").child("current-activity").child(issueId).child("customer").setValue(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    FirebaseDatabase.getInstance().getReference("activities").child(issueId).child("start-info").setValue(dateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                FirebaseDatabase.getInstance().getReference("activities").child(issueId).child("scheduled-info").setValue(scheduledDateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            FirebaseDatabase.getInstance().getReference("activities").child(issueId).child("scheduled-info").child("timeStamp").setValue(DateUtility.getTimeStamp(scheduledDateValue + " " + scheduledTimeValue)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if (task.isSuccessful()) {
                                                                                                                        FirebaseDatabase.getInstance().getReference("activity-users").child("current-activity").child(issueId).child("work-admin").setValue(workadminId)
                                                                                                                                .addOnCompleteListener(CustomerActivity.this, new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        Commons.dismissProgressDialog(progressDialog);
                                                                                                                                        if (!task.isSuccessful()) {
                                                                                                                                            Toast.makeText(CustomerActivity.this, "Failed to create a call", Toast.LENGTH_SHORT).show();
                                                                                                                                        } else {
                                                                                                                                            propblemDescription.getEditText().setText("");
                                                                                                                                            SendNotification.notify(workAdminToken, "Labsolutions", "New call registered by " + userInfo.getUser(), apiService, "workAdminAssignActivity");
                                                                                                                                            SendNotification.notify(adminTokenId, "Labsolutions", "New call registered by " + userInfo.getUser(), apiService, "adminAllActivities");
                                                                                                                                            Intent intent = new Intent(CustomerActivity.this, CurrentCustomerActivity.class);
                                                                                                                                            startActivity(intent);
                                                                                                                                            Toast.makeText(CustomerActivity.this, "Created a call successfully", Toast.LENGTH_SHORT).show();
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

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            workAdminRef.addListenerForSingleValueEvent(eventListener);

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

    private void loadInstruments(final DatabaseReference databaseReference,
                                 final String companyName, final String department) {
        categories.clear();
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        for (DataSnapshot type : ds.getChildren()) {
                            if (type.child("department").getValue(String.class).equals(department))
                                categories.add(type.child("instrumentId").getValue(String.class));
                            String startDate = type.child("amcFromDate").getValue() != null ? type.child("amcFromDate").getValue(String.class) : "";
                            String toDate = type.child("amcToDate").getValue() != null ? type.child("amcToDate").getValue(String.class) : "";
                            if (!startDate.isEmpty() && !toDate.isEmpty()) {
                                instrumentMap.put(type.child("instrumentId").getValue(String.class), checkAMCValidation(DateUtility.getTimeStampOfAmc(startDate), DateUtility.getTimeStampOfAmc(toDate)));
                            } else {
                                instrumentMap.put(type.child("instrumentId").getValue(String.class), true);
                            }

                        }

                    }
                    addInstruments(categories);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(companyName).addListenerForSingleValueEvent(eventListener);
    }


    private boolean checkAMCValidation(long startDate, long endDate) {
        boolean isAMCValid = false;
        long currentTime = new Date().getTime();
        if (currentTime >= startDate && currentTime <= endDate) {
            isAMCValid = true;
        }
        return isAMCValid;
    }

    private void addInstruments(List<String> categories) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, categories);
        autocomplete.setThreshold(2);
        autocomplete.setDropDownBackgroundResource(R.color.colorAccent);
        autocomplete.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_create_24), "Register Call"));
        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_notifications_active_24), "Current Calls"));
        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_history_24), "History"));
        menu.add(0, 4, 4,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));

        return true;
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(CustomerActivity.this, CustomerActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentCreateUserActivity = new Intent(CustomerActivity.this, CurrentCustomerActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 3:
                Intent intentAddInstrument = new Intent(CustomerActivity.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 4:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(CustomerActivity.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}