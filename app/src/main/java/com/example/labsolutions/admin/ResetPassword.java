package com.example.labsolutions.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.labsolutions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ResetPassword extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button reset;
    TextInputLayout email;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reset_password);
            firebaseAuth = FirebaseAuth.getInstance();
            reset = findViewById(R.id.resetButton);
            email = (TextInputLayout) findViewById(R.id.editTextTextEmailAddress);
            reset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String emailValue = email.getEditText().getText().toString();
                        if (emailValue.isEmpty()) {
                            email.setError("Please enter emailid");
                            email.requestFocus();
                        } else if (!emailValue.matches(emailPattern)) {
                            email.setError("Please enter valid emailid");
                            email.requestFocus();
                        } else {
                            email.setError(null);
                            firebaseAuth = FirebaseAuth.getInstance();
                            firebaseAuth.sendPasswordResetEmail(emailValue).addOnCompleteListener(ResetPassword.this, new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(ResetPassword.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
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
}
