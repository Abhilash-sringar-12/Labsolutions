package com.application.labsolutions.admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.engineer.ApplyLeave;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdminDashboard extends AppCompatActivity {
    final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    FirebaseAuth firebaseAuth;
    Spinner spinner = null;
    Spinner companySpinner = null;
    Spinner typeSpinner = null;
    Spinner statusSpinner = null;
    Button result= null;
    EditText fromScheduledDate, toScheduledDate;
    TextView resultView=null;
    ProgressDialog progressDialog;
    ArrayList<String> keys = new ArrayList();
    ArrayList<String> companyNames = new ArrayList();
    ArrayList<String> types = new ArrayList();
    ArrayList<String> callStatus = new ArrayList();
    List<String> filteredActivities = new ArrayList<>();
    HashMap<String, String> spinnerMap = new HashMap<String, String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adim_dahboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            toolbar.setTitle("Dashboard");
            setSupportActionBar(toolbar);
            firebaseAuth = FirebaseAuth.getInstance();
            fromScheduledDate= findViewById(R.id.fromActivity);
            toScheduledDate= findViewById(R.id.toActivity);
            spinner = findViewById(R.id.engineer);
            companySpinner = findViewById(R.id.company);
            statusSpinner= findViewById(R.id.callStatus);
            typeSpinner = findViewById(R.id.type);
            result = findViewById(R.id.dashboardResult);
            resultView = findViewById(R.id.resultText);
            progressDialog = ProgressDialog.show(AdminDashboard.this, "Please wait", "Loading data.....", true, false);
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference users = rootRef.child("users");
            DatabaseReference instruments = rootRef.child("instruments");
            DatabaseReference activities = rootRef.child("activities");
            addTypes();
            addCallStatus();
            fromScheduledDate.setKeyListener(null);
            toScheduledDate.setKeyListener(null);

            fromScheduledDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AdminDashboard.this,
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
                                    fromScheduledDate.setText(df_medium_us_str);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                    toScheduledDate.setText("");
                }
            });
            toScheduledDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!fromScheduledDate.getText().toString().isEmpty()) {
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(AdminDashboard.this,
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
                                        toScheduledDate.setText(df_medium_us_str);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMinDate(new Date(fromScheduledDate.getText().toString()).getTime());
                        datePickerDialog.show();
                    } else {
                        Toast.makeText(AdminDashboard.this, "Choose Start Date first!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            result.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        filteredActivities.clear();
                        progressDialog = ProgressDialog.show(AdminDashboard.this, "Please wait", "Getting Results.....", true, false);
                        String selectedEngineer = spinner.getSelectedItem().toString();
                        String selectedCompany = companySpinner.getSelectedItem().toString();
                        String selectedType = typeSpinner.getSelectedItem().toString();
                        String selectedStatus = statusSpinner.getSelectedItem().toString();
                        String fromDate = fromScheduledDate.getText().toString();
                        String toDate = toScheduledDate.getText().toString();
                        ValueEventListener activitiesValueListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot activitiesSnapShot) {
                                for (DataSnapshot activityDs : activitiesSnapShot.getChildren()) {
                                    String currentActivityCompany = activityDs.child("customer-info/companyName").getValue(String.class);
                                    String currentActivityEngineer = activityDs.child("preAssignedEngineer").getValue(String.class);
                                    String currentActivityType = activityDs.child("activity-info/callType").getValue(String.class);
                                    String currentActivityStatus = activityDs.child("status").getValue(String.class);
                                    Long currentActivityScheduledDate = activityDs.child("timeStamp").getValue(Long.class);
                                    if (selectedCompany.equals("All") || selectedCompany.equals(currentActivityCompany)) {
                                        if(selectedEngineer.equals("All") ||selectedEngineer.equals(currentActivityEngineer))
                                            if (selectedType.equals("All") || selectedType.equals(currentActivityType)) {
                                                if (selectedStatus.equals("All") || selectedStatus.equals(currentActivityStatus)) {
                                                    if ((fromDate.isEmpty() && toDate.isEmpty()) ) {
                                                        filteredActivities.add(activityDs.getKey());
                                                    } else if( currentActivityScheduledDate >=new Date(fromDate).getTime()
                                                    && currentActivityScheduledDate<=new Date(toDate).getTime()) {
                                                        filteredActivities.add(activityDs.getKey());
                                                    }
                                                }
                                            }
                                    }
                                }
                                resultView.setText("Total Calls based on the fillters applied : "+String.valueOf(filteredActivities.size()));
                                Commons.dismissProgressDialog(progressDialog);
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
            });
            ValueEventListener instrumentValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot companySnapshot) {
                    companyNames.add("All");
                    for (DataSnapshot companyDs : companySnapshot.getChildren()) {
                        companyNames.add(companyDs.getKey());
                    }
                    addCompanies(companyNames);
                    ValueEventListener usersEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            keys.add("All");
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                spinnerMap.put(ds.child("user").getValue().toString(), ds.child("user").getValue().toString());
                                keys.add(ds.child("user").getValue().toString());
                            }

                            addEngineers(keys);
                            Commons.dismissProgressDialog(progressDialog);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    users.orderByChild("userType").equalTo("Engineer").addValueEventListener(usersEventListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            instruments.addValueEventListener(instrumentValueListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
    private void addEngineers(List<String> categories) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.spinner.setAdapter(dataAdapter);
    }
    private void addCompanies(List<String> categories) {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.companySpinner.setAdapter(dataAdapter);
    }
    private void addTypes() {
        types.add("All");
        types.add("PM");
        types.add("CAL");
        types.add("BD");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, types);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.typeSpinner.setAdapter(dataAdapter);
    }
    private void addCallStatus() {
        callStatus.add("All");
        callStatus.add("Resolved");
        callStatus.add("Waiting for approval");
        callStatus.add("Scheduled");
        callStatus.add("Declined by admin");
        callStatus.add("Editing Service Report");
        callStatus.add("Waiting for spares");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, callStatus);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        this.statusSpinner.setAdapter(dataAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_supervised_user_circle_24), "Users"));
        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_person_add_24), "Add Admin"));
        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_person_add_24), "Add User"));
        menu.add(0, 4, 4,
                menuIconWithText(getResources().getDrawable(R.drawable.tools), "Add Instruments"));
        menu.add(0, 5, 5,
                menuIconWithText(getResources().getDrawable(R.drawable.tools), "Instruments"));
        menu.add(0, 6, 6,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_local_activity_24), "Activities"));
        menu.add(0, 7, 7,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_account_balance_wallet_24), "Update Leaves"));
        menu.add(0, 8, 8,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_new_releases_24), "Upcoming Leaves"));
        menu.add(0, 9, 9,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cloud_download_24), "Export Activities"));
        menu.add(0, 10, 10,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_dashboard_customize_24), "Dashboard"));
        menu.add(0, 11, 11,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(AdminDashboard.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(AdminDashboard.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(AdminDashboard.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(AdminDashboard.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(AdminDashboard.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(AdminDashboard.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(AdminDashboard.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentLeaves = new Intent(AdminDashboard.this, AllUpcomingLeaves.class);
                finishAffinity();
                startActivity(intentLeaves);
                return true;
            case 9:
                Intent intentExport = new Intent(AdminDashboard.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 10:
                Intent intentDasboard = new Intent(AdminDashboard.this, AdminDashboard.class);
                finishAffinity();
                startActivity(intentDasboard);
                return true;
            case 11:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AdminDashboard.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}