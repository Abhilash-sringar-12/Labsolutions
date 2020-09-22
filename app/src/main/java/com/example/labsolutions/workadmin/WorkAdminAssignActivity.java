package com.example.labsolutions.workadmin;

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
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.labsolutions.R;
import com.example.labsolutions.admin.LoginActivity;
import com.example.labsolutions.admin.MainActivity;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.listviews.ActivitiesInfoDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.labsolutions.commons.Commons.setRecycleItems;

public class WorkAdminAssignActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView listview;
    ImageView emtyImage;
    SearchView searchView;
    ArrayList<ActivitiesInfoDetails> activitiesList = new ArrayList<ActivitiesInfoDetails>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_current_customer);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Current Call Activities");
                setSupportActionBar(toolbar);
                emtyImage = findViewById(R.id.emptyImage);
                listview = findViewById(R.id.activitiesListView);
                firebaseAuth = FirebaseAuth.getInstance();
                emtyImage = findViewById(R.id.emptyImage);
                searchView = findViewById(R.id.searchInstruments);
                searchView.setVisibility(View.GONE);
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final String currentUser = firebaseAuth.getCurrentUser().getUid();
                DatabaseReference currentUserActivitiesDs = rootRef.child("activity-users").child("current-activity");
                progressDialog = ProgressDialog.show(WorkAdminAssignActivity.this, "Please wait", "Loading your current activities....", true, false);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot activitiesCurrentUserDs : snapshot.getChildren()) {
                                    final String activityId = activitiesCurrentUserDs.getKey().toString();
                                    final DatabaseReference currentActivitiesDs = rootRef.child("activities").child(activityId);
                                    final ValueEventListener eventActivityListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot activitiesDs) {
                                            try {
                                                if (activitiesDs.hasChildren()) {
                                                    showAssignedActivities(activitiesDs, activityId);
                                                } else {
                                                    Commons.dismissProgressDialog(progressDialog);
                                                    emtyImage.setImageResource(R.drawable.no_data);
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    };
                                    currentActivitiesDs.addListenerForSingleValueEvent(eventActivityListener);
                                }
                            } else {
                                Commons.dismissProgressDialog(progressDialog);
                                searchView.setVisibility(View.GONE);
                                emtyImage.setImageResource(R.drawable.no_data);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                currentUserActivitiesDs.orderByChild("work-admin").equalTo(currentUser).addListenerForSingleValueEvent(eventListener);
            } else {
                Intent intent = new Intent(WorkAdminAssignActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAssignedActivities(DataSnapshot activitiesDs, String activityId) {
        try {
            String activityInstrumentId = activitiesDs.child("customer-info").child("companyName").getValue() != null
                    ? activitiesDs.child("customer-info").child("companyName").getValue().toString() : "";
            String instrumentId = activitiesDs.child("activity-info").child("instrumentId").getValue() != null
                    ? activitiesDs.child("activity-info").child("instrumentId").getValue().toString() : "";
            String callType = activitiesDs.child("activity-info").child("callType").getValue() != null
                    ? activitiesDs.child("activity-info").child("callType").getValue().toString() : "";
            String activityName = instrumentId + "/" + callType;
            String activityDescription = activitiesDs.child("activity-info").child("instrumentId").getValue() != null
                    ? activitiesDs.child("activity-info").child("instrumentId").getValue().toString() : "";
            long timeStamp = activitiesDs.child("timeStamp").getValue() != null ? (long) activitiesDs.child("timeStamp").getValue() : 0l;
            String activityStatus = "";
            if (activitiesDs.child("engineer-approved-info").child("engineer-availability").getValue() != null && activitiesDs.child("engineer-approved-info").child("engineer-availability").getValue().toString().equals("unavailable")) {
                activityStatus = "Engineer Unavailable";
            } else {
                activityStatus = activitiesDs.child("status").getValue().toString();
            }
            activitiesList.add(new ActivitiesInfoDetails(activityId, activityInstrumentId, activityName, activityDescription, activityStatus, timeStamp));
            Collections.sort(activitiesList, ActivitiesInfoDetails.activites);
            Collections.reverse(activitiesList);
            setRecycleItems(WorkAdminAssignActivity.this, activitiesList, searchView, listview, "assign");
            Commons.dismissProgressDialog(progressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 1,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_supervised_user_circle_24), "Users"));
        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.tools), "Instruments"));
        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_local_activity_24), "Assign Calls"));
        menu.add(0, 4, 4,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_history_24), "History"));
        menu.add(0, 5, 5,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(WorkAdminAssignActivity.this, WorkadminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(WorkAdminAssignActivity.this, WorkAdminInstruments.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 3:
                Intent intentCurrentActivities = new Intent(WorkAdminAssignActivity.this, WorkAdminAssignActivity.class);
                finishAffinity();
                startActivity(intentCurrentActivities);
                return true;
            case 4:
                Intent intentAllActivities = new Intent(WorkAdminAssignActivity.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 5:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(WorkAdminAssignActivity.this, LoginActivity.class);
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
}
