package com.application.labsolutions.admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.customer.CustomerActivity;
import com.application.labsolutions.engineer.AssignedActivities;
import com.application.labsolutions.services.UpdateToken;
import com.application.labsolutions.workadmin.WorkadminActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout emailId, password;
    TextView forgotPassword;
    FirebaseAuth firebaseAuth;
    Button logIn;
    ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            firebaseAuth = FirebaseAuth.getInstance();
            emailId = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
            password = (TextInputLayout) findViewById(R.id.editTextTextPassword);
            logIn = findViewById(R.id.button2);
            forgotPassword = findViewById(R.id.forgorPassword);
            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
                    startActivity(intent);
                }
            });
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    try {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("labsolutions.ic.app@gmail.com")) {
                                UpdateToken.updateAccessToken("", "admin");
                                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                                Commons.dismissProgressDialog(progressDialog);
                                startActivity(intent);
                                finish();
                            } else if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("service@labsolutions-ic.in")) {
                                UpdateToken.updateAccessToken("", "workAdmin");
                                Intent intent = new Intent(LoginActivity.this, WorkadminActivity.class);
                                Commons.dismissProgressDialog(progressDialog);
                                startActivity(intent);
                                finish();
                            } else {
                                loadActivityBasedOnUser();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            logIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String userEmail = emailId.getEditText().getText().toString();
                        String userPaswd = password.getEditText().getText().toString();
                        if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                            Toast.makeText(LoginActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                        } else if (userEmail.isEmpty()) {
                            emailId.setError("Please enter email Id!");
                            emailId.requestFocus();
                        } else if (userPaswd.isEmpty()) {
                            password.setError("Please enter password!");
                            password.requestFocus();
                        } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                            emailId.setError(null);
                            password.setError(null);
                            progressDialog = ProgressDialog.show(LoginActivity.this, "Please wait", "Signing in....", true, false);
                            firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (!task.isSuccessful()) {
                                        Commons.dismissProgressDialog(progressDialog);
                                        Toast.makeText(LoginActivity.this, "Email entered is not registered! Please contact your Admin", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    private void loadActivityBasedOnUser() {
        try {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userType = snapshot.child("userType").getValue().toString();
                    if (userType.equals("Engineer")) {
                        UpdateToken.updateAccessToken("", "user");
                        Intent intent = new Intent(LoginActivity.this, AssignedActivities.class);
                        Commons.dismissProgressDialog(progressDialog);
                        startActivity(intent);
                        finish();
                    } else if (userType.equals("Customer")) {
                        UpdateToken.updateAccessToken("", "user");
                        Intent intent = new Intent(LoginActivity.this, CustomerActivity.class);
                        Commons.dismissProgressDialog(progressDialog);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            rootRef.addValueEventListener(valueEventListener);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
