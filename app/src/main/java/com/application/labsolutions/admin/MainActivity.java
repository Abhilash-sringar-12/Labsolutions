package com.application.labsolutions.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.application.labsolutions.R;
import com.application.labsolutions.customer.CustomerActivity;
import com.application.labsolutions.engineer.AssignedActivities;
import com.application.labsolutions.services.UpdateToken;
import com.application.labsolutions.workadmin.WorkadminActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("labsolutions.ic.app@gmail.com")) {
                            UpdateToken.updateAccessToken("", "admin");
                            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (FirebaseAuth.getInstance().getCurrentUser().getEmail().equals("service@labsolutions-ic.in")) {
                            UpdateToken.updateAccessToken("", "workAdmin");
                            Intent intent = new Intent(MainActivity.this, WorkadminActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            loadActivityBasedOnUser();
                        }
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);
    }

    private void loadActivityBasedOnUser() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    String userType = snapshot.child("userType").getValue().toString();
                    if (userType.equals("Engineer")) {
                        UpdateToken.updateAccessToken("", "user");
                        Intent intent = new Intent(MainActivity.this, AssignedActivities.class);
                        startActivity(intent);
                        finish();
                    } else if (userType.equals("Customer")) {
                        UpdateToken.updateAccessToken("", "user");
                        Intent intent = new Intent(MainActivity.this, CustomerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        rootRef.addValueEventListener(valueEventListener);
    }
}

