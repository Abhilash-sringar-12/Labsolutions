package com.application.labsolutions.engineer;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.LoginActivity;
import com.application.labsolutions.admin.MainActivity;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.listviews.ActivitiesInfoDetails;
import com.application.labsolutions.listviews.ListEngineerActivityDetails;
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


public class AssignedActivities extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    AbsListView listview;
    ImageView emptyImage;
    ArrayList<ActivitiesInfoDetails> activitiesList = new ArrayList<ActivitiesInfoDetails>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_assigned_activities);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Assigned Call Activities");
                setSupportActionBar(toolbar);
                listview = findViewById(R.id.enginnerActivitiesListView);
                firebaseAuth = FirebaseAuth.getInstance();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final String currentUser = firebaseAuth.getCurrentUser().getUid();
                emptyImage = findViewById(R.id.emptyImage);
                DatabaseReference currentUserActivitiesDs = rootRef.child("activity-users").child("current-activity");
                progressDialog = ProgressDialog.show(AssignedActivities.this, "Please wait", "Loading your assigned activities....", true, false);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.hasChildren()) {
                                final ListEngineerActivityDetails[] listActivitiesDetail = new ListEngineerActivityDetails[1];
                                for (DataSnapshot activitiesCurrentUserDs : snapshot.getChildren()) {
                                    final String activityId = activitiesCurrentUserDs.getKey().toString();
                                    final DatabaseReference currentActivitiesDs = rootRef.child("activities").child(activityId);
                                    final ValueEventListener eventActivityListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot activitiesDs) {
                                            try {
                                                if (activitiesDs.hasChildren()) {
                                                    String activityName = activitiesDs.child("activity-info").child("callType").getValue() != null
                                                            ? activitiesDs.child("activity-info").child("callType").getValue(String.class) : "";
                                                    String activityInstrumentId = activitiesDs.child("activity-info").child("instrumentId").getValue(String.class);
                                                    String activityDescription = activitiesDs.child("activity-info").child("instrumentId").getValue() != null
                                                            ? activitiesDs.child("activity-info").child("instrumentId").getValue(String.class) : "";
                                                    String activityStatus = activitiesDs.child("status").getValue() != null
                                                            ? activitiesDs.child("status").getValue().toString() : "";
                                                    long timeStamp = activitiesDs.child("timeStamp").getValue() != null ? (long) activitiesDs.child("timeStamp").getValue() : 0l;
                                                    if (activityStatus.equals("Approved by admin")) {
                                                        activityStatus = "Assigned";
                                                    } else if (activityStatus.equals("Rescheduled")) {
                                                        activityStatus = "Rescheduled";
                                                    } else if (activityStatus.equals("Editing Service Report")) {
                                                        activityStatus = "Request To Edit";
                                                    } else {
                                                        activityStatus = "Resolving";
                                                    }
                                                    activitiesList.add(new ActivitiesInfoDetails(activityId, activityInstrumentId, activityName, activityDescription, activityStatus, timeStamp));
                                                    Collections.sort(activitiesList, ActivitiesInfoDetails.activites);
                                                    Collections.reverse(activitiesList);
                                                    listActivitiesDetail[0] = new ListEngineerActivityDetails(AssignedActivities.this, R.layout.row_enginner_assigned_activities, activitiesList);
                                                    listview.setAdapter(listActivitiesDetail[0]);
                                                    Commons.dismissProgressDialog(progressDialog);
                                                } else {
                                                    Commons.dismissProgressDialog(progressDialog);
                                                }
                                            } catch (
                                                    Exception e) {
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
                                emptyImage.setImageResource(R.drawable.no_data);
                            }
                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                };
                currentUserActivitiesDs.orderByChild("engineer").equalTo(currentUser).addListenerForSingleValueEvent(eventListener);

                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        try {
                            Intent newIntent;
                            ActivitiesInfoDetails activitiesDetails = activitiesList.get(i);
                            if (activitiesDetails.getActivitystatus().equals("Assigned")) {
                                newIntent = new Intent(AssignedActivities.this, ViewAssignedActivities.class);
                            } else if (activitiesDetails.getActivitystatus().equals("Rescheduled")) {
                                newIntent = new Intent(AssignedActivities.this, ViewAssignedActivities.class);
                            } else if (activitiesDetails.getActivitystatus().equals("Resolving")) {
                                newIntent = new Intent(AssignedActivities.this, ResolveActivity.class);
                            } else {
                                newIntent = new Intent(AssignedActivities.this, ReopenActivity.class);
                            }
                            newIntent.putExtra("activityId", activitiesDetails.getActivityId());
                            startActivity(newIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Intent intent = new Intent(AssignedActivities.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        } catch (
                Exception e) {
            e.getStackTrace();
        }

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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_pie_chart_24), "Your Stats"));
        menu.add(0, 7, 7,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(AssignedActivities.this, AssignedActivities.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(AssignedActivities.this, AllActivities.class);
                startActivity(intentInstruments);
                finishAffinity();
                return true;
            case 3:
                Intent intentAttendance = new Intent(AssignedActivities.this, Attendance.class);
                finishAffinity();
                startActivity(intentAttendance);
                return true;
            case 4:
                Intent intentApplyLeave = new Intent(AssignedActivities.this, ApplyLeave.class);
                finishAffinity();
                startActivity(intentApplyLeave);
                return true;
            case 5:
                Intent intentYourLeaves = new Intent(AssignedActivities.this, AppliedLeaves.class);
                finishAffinity();
                startActivity(intentYourLeaves);
                return true;
            case 6:
                Intent intentYourStats = new Intent(AssignedActivities.this, YourStaticstics.class);
                finishAffinity();
                startActivity(intentYourStats);
                return true;
            case 7:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AssignedActivities.this, LoginActivity.class);
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