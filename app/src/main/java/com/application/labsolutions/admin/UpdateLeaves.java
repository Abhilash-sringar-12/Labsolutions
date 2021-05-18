package com.application.labsolutions.admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class UpdateLeaves extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    Spinner spinner = null;
    EditText editText = null;
    HashMap<String, String> spinnerMap = new HashMap<String, String>();
    HashMap<String, String> leavesMap = new HashMap<String, String>();
    ArrayList<String> keys = new ArrayList();
    Button updateLeaves, createCalender = null;
    String year;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_leaves);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Update Leaves");
        setSupportActionBar(toolbar);
        try {
            spinner = findViewById(R.id.engineer);
            editText = findViewById(R.id.engineerLeaves);
            updateLeaves = findViewById(R.id.updateLeave);
            createCalender = findViewById(R.id.updateYearCalender);
            progressDialog = ProgressDialog.show(UpdateLeaves.this, "Please wait", "Loading data.....", true, false);
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference users = rootRef.child("users");
            final DatabaseReference leaves = rootRef.child("leaves");
            year = DateUtility.getCurrentDate().split(" ")[2];
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    if (spinner.getSelectedItem() != "") {
                        String engineerId = spinnerMap.get(spinner.getSelectedItem());
                        editText.setText(leavesMap.get(engineerId));
                    }

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            createCalender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        progressDialog = ProgressDialog.show(UpdateLeaves.this, "Please wait", "Creating Calender.....", true, false);
                        Date todaysDate = new Date("1 Jan " + year);
                        Date anotherDate = new Date("1 Jan " + String.valueOf(Integer.parseInt(year) + 1));
                        getDaysBetweenDates(todaysDate, anotherDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            updateLeaves.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final String engineerName = spinner.getSelectedItem().toString();
                        String engineerId = spinnerMap.get(engineerName);
                        String updatedLeaves = editText.getText().toString();
                        if (engineerName.isEmpty()) {
                            Toast.makeText(UpdateLeaves.this, "Please Select Engineer", Toast.LENGTH_SHORT).show();

                        } else if (updatedLeaves.isEmpty()) {
                            Toast.makeText(UpdateLeaves.this, "Please enter number of leaves", Toast.LENGTH_SHORT).show();

                        } else {
                            progressDialog = ProgressDialog.show(UpdateLeaves.this, "Please wait", "Updating Leaves.....", true, false);
                            leavesMap.put(engineerId, updatedLeaves);
                            rootRef.child("leaves/" + engineerId).setValue(updatedLeaves).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Commons.dismissProgressDialog(progressDialog);
                                        Toast.makeText(UpdateLeaves.this, "Success Fully Updated Leave for " + engineerName, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            });
            ValueEventListener usersEventListener = new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    keys.add("");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        spinnerMap.put(ds.child("user").getValue().toString(), ds.getKey());
                        leavesMap.put(ds.getKey(), "0");
                        keys.add(ds.child("user").getValue().toString());
                    }
                    addEngineers(keys);
                    ValueEventListener leavesEventListener = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String engineerId = ds.getKey();
                                String leaves = ds.getValue() == null ? "0" : ds.getValue().toString();
                                leavesMap.put(engineerId, leaves);
                            }

                            Commons.dismissProgressDialog(progressDialog);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    leaves.addValueEventListener(leavesEventListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            users.orderByChild("userType").equalTo("Engineer").addValueEventListener(usersEventListener);
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

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cloud_download_24), "Export Activities"));
        menu.add(0, 9, 9,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(UpdateLeaves.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(UpdateLeaves.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(UpdateLeaves.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(UpdateLeaves.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(UpdateLeaves.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(UpdateLeaves.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(UpdateLeaves.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentExport = new Intent(UpdateLeaves.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 9:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(UpdateLeaves.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDaysBetweenDates(Date startdate, Date enddate) {
        try {
            LinkedHashMap<String, LinkedHashMap<String, String>> monthMap = new LinkedHashMap<>();
            List<String> daysList = new LinkedList<>();
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(startdate);
            DatabaseReference calenderRef = FirebaseDatabase.getInstance().getReference().child("calender/" + year);
            while (calendar.getTime().before(enddate)) {
                Date result = calendar.getTime();
                String currentIndexDate = DateUtility.formatDate(result.toString());
                daysList.add(DateUtility.formatDate(result.toString()));
                calendar.add(Calendar.DATE, 1);
            }
            for (int i = 0; i < monthName.length; i++) {
                LinkedHashMap<String, String> monthDays = new LinkedHashMap<>();
                for (int j = 0; j < daysList.size(); j++) {
                    if (monthName[i] == monthName[Integer.parseInt(daysList.get(j).split("-")[1]) - 1]) {
                        monthDays.put(daysList.get(j), "");
                    }
                }
                monthMap.put(monthName[i], monthDays);
            }
            calenderRef.setValue(monthMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("applied-leaves").setValue(null);
                        Commons.dismissProgressDialog(progressDialog);
                        Toast.makeText(UpdateLeaves.this, "Successfully created Calender", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
}