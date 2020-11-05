package com.application.labsolutions.admin;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

public class UpdateInstruments extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextInputLayout companyName, department, instrumentType, instrumentId;
    EditText amcFromDate, amcToDate;
    Button updateInstrument;
    private ProgressDialog progressDialog;
    String instrument, company, type, fromDate, toDate, departmentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_instrument);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Update Instrument");
            setSupportActionBar(toolbar);
            firebaseAuth = FirebaseAuth.getInstance();
            Intent intent = getIntent();
            instrument = intent.getStringExtra("instrumentId");
            company = intent.getStringExtra("company");
            type = intent.getStringExtra("instrumentType");
            fromDate = intent.getStringExtra("amcFromDate");
            toDate = intent.getStringExtra("amcToDate");
            departmentName = intent.getStringExtra("department");
            firebaseAuth = FirebaseAuth.getInstance();
            initializeFields();
            populateInstrumentData();
            updateInstrument.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (validateFields()) {
                            progressDialog = ProgressDialog.show(UpdateInstruments.this, "Please wait", "Updating instrument.....", true, false);
                            FirebaseDatabase.getInstance().getReference("instruments").child(company).child(type).child(instrument.replaceAll("/", "-")).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        company = companyName.getEditText().getText().toString();
                                        instrument = instrumentId.getEditText().getText().toString();
                                        departmentName = department.getEditText().getText().toString();
                                        fromDate = amcFromDate.getText().toString();
                                        toDate = amcToDate.getText().toString();
                                        type = instrumentType.getEditText().getText().toString();
                                        final InstrumentInfo instrumentInfo = new InstrumentInfo(company, instrument, type, departmentName, fromDate, toDate);
                                        FirebaseDatabase.getInstance().getReference("instruments").child(company).child(type).child(instrument.replaceAll("/", "-")).setValue(instrumentInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Commons.dismissProgressDialog(progressDialog);
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(UpdateInstruments.this, "Failed to update instrument", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(UpdateInstruments.this, "Updated instrument succesfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent1 = new Intent(UpdateInstruments.this, InstrumentsActivity.class);
                                                    startActivity(intent1);
                                                }
                                            }
                                        });
                                    }
                                }
                            });
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


    private void initializeFields() {
        try {
            companyName = (TextInputLayout) findViewById(R.id.editCompanyName);
            instrumentId = (TextInputLayout) findViewById(R.id.editTextInstrumentId);
            instrumentType = (TextInputLayout) findViewById(R.id.editTextInstrumentType);
            department = (TextInputLayout) findViewById(R.id.editTextDepartment);
            amcFromDate = findViewById(R.id.amcFromdate);
            amcToDate = findViewById(R.id.amcTodate);
            amcToDate.setKeyListener(null);
            amcFromDate.setKeyListener(null);
            updateInstrument = findViewById(R.id.addInstrument);
            amcFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateInstruments.this,
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
                    DatePickerDialog datePickerDialog = new DatePickerDialog(UpdateInstruments.this,
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
            updateInstrument.setText("Update");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void populateInstrumentData() {
        companyName.getEditText().setText(company);
        instrumentId.getEditText().setText(instrument);
        department.getEditText().setText(departmentName);
        amcFromDate.setText(fromDate);
        amcToDate.setText(toDate);
        instrumentType.getEditText().setText(type);
    }

    private boolean validateFields() {
        boolean isValid = true;
        if (company.isEmpty()) {
            companyName.setError("Please enter company name");
            companyName.requestFocus();
            isValid = false;
        } else {
            companyName.setError(null);
        }
        if (instrument.isEmpty()) {
            instrumentId.setError("Please enter instrument id");
            instrumentId.requestFocus();
            isValid = false;
        } else {
            instrumentId.setError(null);
        }
        if (type.isEmpty()) {
            instrumentType.setError("Please enter instrument type");
            instrumentType.requestFocus();
            isValid = false;
        } else {
            instrumentType.setError(null);
        }
        if (departmentName.isEmpty()) {
            department.setError("Please enter department");
            department.requestFocus();
            isValid = false;
        } else {
            department.setError(null);
        }
        return isValid;
    }

}
