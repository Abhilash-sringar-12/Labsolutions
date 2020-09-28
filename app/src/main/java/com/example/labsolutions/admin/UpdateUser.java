package com.example.labsolutions.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.labsolutions.R;
import com.example.labsolutions.commons.Commons;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class UpdateUser extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    String userId;
    TextInputLayout emailId, password, userName, phone, company, department, companyAddress;
    RadioGroup radioGroup;
    RadioButton engineer;
    RadioButton customer;
    RadioButton radioButton;
    Button update;
    String phonePattern = "^[6-9]\\d{9}$";
    private ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_user);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Update User");
            setSupportActionBar(toolbar);
            update = findViewById(R.id.button2);
            emailId = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
            password = (TextInputLayout) findViewById(R.id.editTextTextPassword);
            emailId.setVisibility(View.GONE);
            password.setVisibility(View.GONE);
            update.setText("Update");
            firebaseAuth = FirebaseAuth.getInstance();
            Intent intent = getIntent();
            userId = intent.getStringExtra("userId");
            phone = (TextInputLayout) findViewById(R.id.editTextPhone);
            userName = (TextInputLayout) findViewById(R.id.editTextUserName);
            company = (TextInputLayout) findViewById(R.id.editTextCompany);
            companyAddress = (TextInputLayout) findViewById(R.id.editTextCompanyAddress);
            department = (TextInputLayout) findViewById(R.id.editTextDepartment);
            radioGroup = (RadioGroup) findViewById(R.id.usertype);
            setUserData();
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int selectedId = radioGroup.getCheckedRadioButtonId();
                        radioButton = (RadioButton) findViewById(selectedId);
                        final String userNameValue = userName.getEditText().getText().toString();
                        final String companyValue = company.getEditText().getText().toString();
                        final String departmentValue = department.getEditText().getText().toString();
                        final String phoneNumberValue = phone.getEditText().getText().toString();
                        final String userTypeValue = radioButton.getText().toString();
                        final String companyAddressValue = companyAddress.getEditText().getText().toString();
                        if (validateFields(userNameValue, phoneNumberValue, companyValue, departmentValue, companyAddressValue)) {
                            progressDialog = ProgressDialog.show(UpdateUser.this, "Please wait", "Updating user.....", true, false);

                            final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            databaseReference.child("companyName").setValue(companyValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference.child("department").setValue(departmentValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    databaseReference.child("user").setValue(userNameValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                databaseReference.child("phoneNumber").setValue(phoneNumberValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        databaseReference.child("userType").setValue(userTypeValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                databaseReference.child("companyAddress").setValue(companyAddressValue).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        Commons.dismissProgressDialog(progressDialog);
                                                                                        if (task.isSuccessful()) {
                                                                                            resetForm();
                                                                                            Toast.makeText(UpdateUser.this, "Successfully Updated User", Toast.LENGTH_SHORT).show();
                                                                                            Intent intent1 = new Intent(UpdateUser.this, AdminActivity.class);
                                                                                            startActivity(intent1);
                                                                                            finishAffinity();
                                                                                        } else {
                                                                                            Toast.makeText(UpdateUser.this, "Please Tyr again", Toast.LENGTH_SHORT).show();

                                                                                        }
                                                                                    }
                                                                                });
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

    private void setUserData() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String companyName = snapshot.child("companyName").getValue() != null ?
                        snapshot.child("companyName").getValue(String.class) : "";
                String departmentName = snapshot.child("department").getValue() != null ?
                        snapshot.child("department").getValue(String.class) : "";
                String user = snapshot.child("user").getValue() != null ?
                        snapshot.child("user").getValue(String.class) : "";
                String phoneNumber = snapshot.child("phoneNumber").getValue() != null ?
                        snapshot.child("phoneNumber").getValue(String.class) : "";
                String userType = snapshot.child("userType").getValue() != null ?
                        snapshot.child("userType").getValue(String.class) : "";
                String companyAddressStored = snapshot.child("companyAddress").getValue() != null ?
                        snapshot.child("companyAddress").getValue(String.class) : "";
                engineer = (RadioButton) findViewById(R.id.engineer);
                customer = (RadioButton) findViewById(R.id.serviceuser);
                company.getEditText().setText(companyName);
                department.getEditText().setText(departmentName);
                userName.getEditText().setText(user);
                phone.getEditText().setText(phoneNumber);
                companyAddress.getEditText().setText(companyAddressStored);
                if (userType.equals("Engineer")) {
                    engineer.setChecked(true);
                } else {
                    customer.setChecked(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addValueEventListener(valueEventListener);

    }

    private Boolean validateFields(String userNameValue, String phoneNumberValue, String companyValue, String departmentValue, String companyAddressValue) {
        Boolean isFormValid = true;
        if (userNameValue.isEmpty()) {
            userName.setError("Please enter user name");
            isFormValid = false;
            userName.requestFocus();
        } else {
            userName.setError(null);
        }
        if (phoneNumberValue.isEmpty()) {
            phone.setError("Please enter phone number");
            isFormValid = false;
            phone.requestFocus();
        } else {
            phone.setError(null);
        }
        if (!phoneNumberValue.matches(phonePattern)) {
            phone.setError("Please enter valid phone number");
            isFormValid = false;
            phone.requestFocus();
        } else {
            phone.setError(null);
        }
        if (companyValue.isEmpty()) {
            company.setError("Please enter Company Name");
            isFormValid = false;
            company.requestFocus();
        } else {
            company.setError(null);
        }
        if (companyAddressValue.isEmpty()) {
            companyAddress.setError("Please enter Company Name");
            isFormValid = false;
            companyAddress.requestFocus();
        } else {
            companyAddress.setError(null);
        }
        if (departmentValue.isEmpty()) {
            department.setError("Please enter department name");
            isFormValid = false;
            department.requestFocus();
        } else {
            department.setError(null);
        }
        return isFormValid;
    }

    private void resetForm() {
        emailId.getEditText().setText("");
        phone.getEditText().setText("");
        company.getEditText().setText("");
        department.getEditText().setText("");
        userName.getEditText().setText("");
        companyAddress.getEditText().setText("");
    }

}