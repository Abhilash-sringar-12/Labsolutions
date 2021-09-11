package com.application.labsolutions.admin;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.pojos.InstrumentInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class AddInstrumentActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextInputLayout companyName, department, instrumentType, instrumentId;
    EditText amcFromDate, amcToDate;
    Button addInstrument;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_instrument);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Add Instrument");
            setSupportActionBar(toolbar);
            firebaseAuth = FirebaseAuth.getInstance();
            addInstrument();
        } catch (Exception e) {
            e.getStackTrace();
        }

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
                Intent intentAdminActivity = new Intent(AddInstrumentActivity.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(AddInstrumentActivity.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(AddInstrumentActivity.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(AddInstrumentActivity.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(AddInstrumentActivity.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(AddInstrumentActivity.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(AddInstrumentActivity.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentLeaves = new Intent(AddInstrumentActivity.this, AllUpcomingLeaves.class);
                finishAffinity();
                startActivity(intentLeaves);
                return true;
            case 9:
                Intent intentExport = new Intent(AddInstrumentActivity.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 10:
                Intent intentDasboard = new Intent(AddInstrumentActivity.this, AdminDashboard.class);
                finishAffinity();
                startActivity(intentDasboard);
                return true;
            case 11:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AddInstrumentActivity.this, LoginActivity.class);
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

    private void addInstrument() {
        try {
            companyName = (TextInputLayout) findViewById(R.id.editCompanyName);
            instrumentId = (TextInputLayout) findViewById(R.id.editTextInstrumentId);
            instrumentType = (TextInputLayout) findViewById(R.id.editTextInstrumentType);
            department = (TextInputLayout) findViewById(R.id.editTextDepartment);
            amcFromDate = findViewById(R.id.amcFromdate);
            amcToDate = findViewById(R.id.amcTodate);
            addInstrument = findViewById(R.id.addInstrument);
            amcToDate.setKeyListener(null);
            amcFromDate.setKeyListener(null);
            amcFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddInstrumentActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(0);
                            cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                            Date chosenDate = cal.getTime();
                            DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                            String df_medium_us_str = df_medium_us.format(chosenDate);
                            amcFromDate.setText(df_medium_us_str);
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
            amcToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(AddInstrumentActivity.this,
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
                                    amcToDate.setText(df_medium_us_str);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
            addInstrument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String companyNameValue = companyName.getEditText().getText().toString();
                        String instrumentIdValue = instrumentId.getEditText().getText().toString();
                        String instrumentTypeValue = instrumentType.getEditText().getText().toString();
                        String departmentValue = department.getEditText().getText().toString();
                        String amcFromDateValue = amcFromDate.getText().toString();
                        String amcToDateValue = amcToDate.getText().toString();
                        if (validateFields(companyNameValue, instrumentIdValue, instrumentTypeValue, departmentValue, amcFromDateValue, amcToDateValue)) {
                            progressDialog = ProgressDialog.show(AddInstrumentActivity.this, "Please wait", "Adding instrument...", true, false);
                            InstrumentInfo instrumentInfo = new InstrumentInfo(companyNameValue, instrumentIdValue, instrumentTypeValue, departmentValue, amcFromDateValue, amcToDateValue);
                            FirebaseDatabase.getInstance().getReference("instruments").child(companyNameValue).child(instrumentTypeValue).child(instrumentIdValue.replaceAll("/", "-")).setValue(instrumentInfo).addOnCompleteListener(AddInstrumentActivity.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Commons.dismissProgressDialog(progressDialog);
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(AddInstrumentActivity.this, "Failed to create instrument", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddInstrumentActivity.this, "Created instrument succesfuly", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.getStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateFields(String companyNameValue, String instrumentIdValue, String instrumentTypeValue, String departmentValue, String amcFromDateValue, String amcToDateValue) {
        boolean isValid = true;
        if (companyNameValue.isEmpty()) {
            companyName.setError("Please enter company name");
            companyName.requestFocus();
            isValid = false;
        } else {
            companyName.setError(null);
        }
        if (instrumentIdValue.isEmpty()) {
            instrumentId.setError("Please enter instrument id");
            instrumentId.requestFocus();
            isValid = false;
        } else {
            instrumentId.setError(null);
        }
        if (instrumentTypeValue.isEmpty()) {
            instrumentType.setError("Please enter instrument type");
            instrumentType.requestFocus();
            isValid = false;
        } else {
            instrumentType.setError(null);
        }
        if (departmentValue.isEmpty()) {
            department.setError("Please enter department");
            department.requestFocus();
            isValid = false;
        } else {
            department.setError(null);
        }
        return isValid;
    }

    private void resetForm() {
        companyName.getEditText().setText("");
        instrumentType.getEditText().setText("");
        instrumentId.getEditText().setText("");
        department.getEditText().setText("");
        amcFromDate.setText("");
        amcToDate.setText("");
    }

}