package com.application.labsolutions.engineer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.LoginActivity;
import com.application.labsolutions.admin.UpdateLeaves;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ApplyLeave extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
    Spinner spinner = null;
    String noOfLeaves;
    List<String> categories = new ArrayList<String>();
    EditText leaveFrom, backOn;
    TextView leaveText;
    Button applyLeave;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_apply_leave);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                firebaseAuth = FirebaseAuth.getInstance();
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Apply Leaves");
                setSupportActionBar(toolbar);
                categories.add("");
                categories.add("SL");
                categories.add("EL");
                categories.add("HD");
                spinner = findViewById(R.id.leaveType);
                leaveFrom = findViewById(R.id.leaveFrom);
                backOn = findViewById(R.id.backOn);
                leaveText = findViewById(R.id.leaveText);
                addLeaveType();
                leaveFrom.setKeyListener(null);
                backOn.setKeyListener(null);
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference leaves = rootRef.child("leaves/" + firebaseAuth.getCurrentUser().getUid());
                ValueEventListener leaveValueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            noOfLeaves = snapshot.getValue() == null ? "0" : snapshot.getValue().toString();
                            leaveText.setText("Your balance leaves is " + noOfLeaves + "days!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                leaves.addValueEventListener(leaveValueEventListener);
                applyLeave = findViewById(R.id.applyLeave);
                applyLeave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            final String leaveType = spinner.getSelectedItem().toString();
                            String fromDate = leaveFrom.getText().toString();
                            String backOnDate = backOn.getText().toString();
                            if (spinner.getSelectedItem().toString().isEmpty()) {
                                Toast.makeText(ApplyLeave.this, "Please Select Leave Type", Toast.LENGTH_SHORT).show();
                            } else if (fromDate.isEmpty()) {
                                Toast.makeText(ApplyLeave.this, "Please Select From Date", Toast.LENGTH_SHORT).show();
                            } else if (backOnDate.isEmpty()) {
                                Toast.makeText(ApplyLeave.this, "Please Select Back On Date", Toast.LENGTH_SHORT).show();
                            } else if (leaveType.equals("HD") && DateUtility.calculatedLeaves(DateUtility.formatDate(fromDate), DateUtility.formatDate(backOnDate)) > 1.0) {
                                Toast.makeText(ApplyLeave.this, "You can only apply Half day one at a time", Toast.LENGTH_SHORT).show();
                            } else {
                                validatedLeaveDates(leaveType, fromDate, backOnDate);
                            }
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });
                leaveFrom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Calendar c = Calendar.getInstance();
                        int mYear = c.get(Calendar.YEAR);
                        int mMonth = c.get(Calendar.MONTH);
                        int mDay = c.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog datePickerDialog = new DatePickerDialog(ApplyLeave.this,
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
                                        leaveFrom.setText(df_medium_us_str);
                                    }
                                }, mYear, mMonth, mDay);
                        datePickerDialog.getDatePicker().setMaxDate(new Date("31 Dec " + mYear).getTime());
                        datePickerDialog.getDatePicker().setMinDate(new Date(String.valueOf(new Date())).getTime());
                        datePickerDialog.show();
                        backOn.setText("");
                    }
                });
                backOn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!leaveFrom.getText().toString().isEmpty()) {
                            final Calendar c = Calendar.getInstance();
                            int mYear = c.get(Calendar.YEAR);
                            int mMonth = c.get(Calendar.MONTH);
                            int mDay = c.get(Calendar.DAY_OF_MONTH);
                            DatePickerDialog datePickerDialog = new DatePickerDialog(ApplyLeave.this,
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
                                            backOn.setText(df_medium_us_str);
                                        }
                                    }, mYear, mMonth, mDay);
                            datePickerDialog.getDatePicker().setMinDate(new Date(leaveFrom.getText().toString()).getTime() + MILLIS_IN_A_DAY);
                            datePickerDialog.getDatePicker().setMaxDate(new Date("31 Dec " + mYear).getTime());
                            datePickerDialog.show();
                        } else {
                            Toast.makeText(ApplyLeave.this, "Choose Start Date first!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void addLeaveType() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Leave Type");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_notifications_active_24), "Assigned Calls"));

        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_history_24), "History"));
        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_add_task_24), "Log Attendance"));
        menu.add(0, 4, 4,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_touch_app_24), "Apply Leaves"));
        menu.add(0, 5, 5,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_new_releases_24), "Your Leaves"));
        menu.add(0, 6, 6,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(ApplyLeave.this, AssignedActivities.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(ApplyLeave.this, AllActivities.class);
                startActivity(intentInstruments);
                finishAffinity();
                return true;
            case 3:
                Intent intentAttendance = new Intent(ApplyLeave.this, Attendance.class);
                finishAffinity();
                startActivity(intentAttendance);
                return true;
            case 4:
                Intent intentApplyLeave = new Intent(ApplyLeave.this, ApplyLeave.class);
                finishAffinity();
                startActivity(intentApplyLeave);
                return true;
            case 5:
                Intent intentYourLeaves = new Intent(ApplyLeave.this, AppliedLeaves.class);
                finishAffinity();
                startActivity(intentYourLeaves);
                return true;
            case 6:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(ApplyLeave.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    public void getDaysBetweenDates(Date startdate, Date enddate, final String leaveType) {
        try {
            progressDialog = ProgressDialog.show(ApplyLeave.this, "Please wait", "Apllying leaves!!!.....", true, false);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(startdate);
            final DatabaseReference calenderRef = FirebaseDatabase.getInstance().getReference().child("calender/");
            while (calendar.getTime().before(enddate)) {
                Date result = calendar.getTime();
                final String currentIndexDate = DateUtility.formatDate(result.toString());
                final String currentIndexMonth = monthName[Integer.parseInt(currentIndexDate.split("-")[1]) - 1];
                final String year = currentIndexDate.split("-")[2];
                calendar.add(Calendar.DATE, 1);
                if (result.getDay() != 0 && result.getDay() != 6) {
                    calenderRef.child(year + "/" + currentIndexMonth + "/" + currentIndexDate + "/" + firebaseAuth.getCurrentUser().getUid() + "/type").setValue(leaveType);
                }
            }
            storeAppliedLeaves(startdate, enddate, leaveType);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void storeAppliedLeaves(final Date startdate, final Date enddate, final String leaveType) {
        try {
            LinkedHashMap<String, String> appliedLeavesMap = new LinkedHashMap<>();
            appliedLeavesMap.put("leaveFrom", DateUtility.formatDate(startdate.toString()));
            appliedLeavesMap.put("backOn", DateUtility.formatDate(enddate.toString()));
            appliedLeavesMap.put("leaveType", leaveType);
            FirebaseDatabase.getInstance().getReference().child("applied-leaves/" + firebaseAuth.getCurrentUser().getUid() + "/" + UUID.randomUUID()).setValue(appliedLeavesMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Commons.dismissProgressDialog(progressDialog);
                    if (task.isSuccessful()) {
                        final double noOfDays = leaveType.equals("HD") ? 0.5 : DateUtility.calculatedLeaves(DateUtility.formatDate(startdate.toString()), DateUtility.formatDate(enddate.toString()));
                        FirebaseDatabase.getInstance().getReference().child("leaves/" + firebaseAuth.getCurrentUser().getUid()).setValue(String.valueOf(Double.parseDouble(noOfLeaves) - noOfDays)).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ApplyLeave.this, "Applied leave successfully", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(ApplyLeave.this, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void validatedLeaveDates(final String leaveType, String fromDate, String backOnDate) {
        final Date formatedFromdate = new Date(fromDate);
        final Date formatedBackOndate = new Date(backOnDate);
        if (formatedFromdate.getDay() == 0 || formatedFromdate.getDay() == 6) {
            Toast.makeText(ApplyLeave.this, "From Date Selected is a weekend!!!", Toast.LENGTH_SHORT).show();
        } else if (formatedBackOndate.getDay() == 0 || formatedBackOndate.getDay() == 6) {
            Toast.makeText(ApplyLeave.this, "Back On Date Selected is a weekend!!!", Toast.LENGTH_SHORT).show();
        } else {
            final String currentIndexFromDate = DateUtility.formatDate(fromDate.toString());
            final String currentIndexFromMonth = monthName[Integer.parseInt(currentIndexFromDate.split("-")[1]) - 1];
            final String year = currentIndexFromDate.split("-")[2];
            final String currentIndexBackOnDate = DateUtility.formatDate(backOnDate.toString());
            final String currentIndexBackMonth = monthName[Integer.parseInt(currentIndexFromDate.split("-")[1]) - 1];
            DatabaseReference leavesRef = FirebaseDatabase.getInstance().getReference().child("calender/" + year);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DataSnapshot start = snapshot.child(currentIndexFromMonth).child(currentIndexFromDate + "/" + firebaseAuth.getCurrentUser().getUid());
                    DataSnapshot end = snapshot.child(currentIndexBackMonth).child(currentIndexBackOnDate + "/" + firebaseAuth.getCurrentUser().getUid());
                    if (start.hasChild("type")) {
                    } else if (end.hasChild("type")) {
                    } else {
                        getDaysBetweenDates(formatedFromdate, formatedBackOndate, leaveType);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            leavesRef.addValueEventListener(valueEventListener);
        }
    }
}