package com.application.labsolutions.engineer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.InstrumentsActivity;
import com.application.labsolutions.admin.LoginActivity;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.application.labsolutions.listviews.ActivitiesInfoDetails;
import com.application.labsolutions.listviews.AppliedLeavesView;
import com.application.labsolutions.listviews.CreatedInstrumentView;
import com.application.labsolutions.listviews.LeaveDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AppliedLeaves extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView listview;
    ImageView emptyImage;
    SearchView searchView;
    ArrayList<LeaveDetails> leavesList = new ArrayList<LeaveDetails>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_applied_leaves);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Your Upcoming Leaves");
                setSupportActionBar(toolbar);
                emptyImage = findViewById(R.id.emptyImage);
                listview = findViewById(R.id.leavesListView);
                firebaseAuth = FirebaseAuth.getInstance();
                searchView = findViewById(R.id.searchLeaves);
                final String currentUser = firebaseAuth.getCurrentUser().getUid();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference currentUserLeavesDs = rootRef.child("applied-leaves").child(currentUser);
                final DatabaseReference totalLeavesDs = rootRef.child("leaves").child(currentUser);
                progressDialog = ProgressDialog.show(AppliedLeaves.this, "Please wait", "Loading your Leaves.....", true, false);
                ValueEventListener totalLeavesListner = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String totalLeaves = snapshot.getValue() != null ? snapshot.getValue(String.class) : "0.0";
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    try {
                                        for (DataSnapshot leavesDs : snapshot.getChildren()) {
                                            long timeStamp = 0l;
                                            String leaveFrom = leavesDs.child("leaveFrom").getValue() != null
                                                    ? leavesDs.child("leaveFrom").getValue(String.class) : "";
                                            String backOn = leavesDs.child("backOn").getValue() != null
                                                    ? leavesDs.child("backOn").getValue(String.class) : "";
                                            String leaveType = leavesDs.child("leaveType").getValue() != null
                                                    ? leavesDs.child("leaveType").getValue(String.class) : "";
                                            String userName = leavesDs.child("userName").getValue() != null
                                                    ? leavesDs.child("userName").getValue(String.class) : "";
                                            if (!leaveFrom.isEmpty()) {

                                                timeStamp = DateUtility.getTimeStamForLeaves(leaveFrom);
                                            }
                                            long backOnDateTimeStamp = 0;
                                            if (!backOn.isEmpty())
                                                backOnDateTimeStamp = DateUtility.getTimeStamForLeaves(backOn);
                                            long currentDateTimeStamp = new Date(DateUtility.getCurrentDate()).getTime();
                                            if ((backOnDateTimeStamp == 0 && currentDateTimeStamp <= timeStamp) || backOnDateTimeStamp > currentDateTimeStamp)
                                                leavesList.add(new LeaveDetails(leaveType, leaveFrom, backOn, timeStamp, userName, leavesDs.getKey(), totalLeaves));
                                        }
                                        Collections.sort(leavesList, LeaveDetails.leaves);
                                        final AppliedLeavesView adapter = new AppliedLeavesView(AppliedLeaves.this, leavesList, "user");
                                        listview.setHasFixedSize(true);
                                        listview.setLayoutManager(new LinearLayoutManager(AppliedLeaves.this));
                                        listview.setAdapter(adapter);
                                        listview.setAdapter(adapter);

                                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                            @Override
                                            public boolean onQueryTextSubmit(String query) {
                                                adapter.getFilter().filter(query);
                                                return false;
                                            }

                                            @Override
                                            public boolean onQueryTextChange(String newText) {
                                                adapter.getFilter().filter(newText);
                                                return false;
                                            }
                                        });
                                        Commons.dismissProgressDialog(progressDialog);
                                        if (leavesList.isEmpty())
                                            emptyImage.setImageResource(R.drawable.no_data);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Commons.dismissProgressDialog(progressDialog);
                                    emptyImage.setImageResource(R.drawable.no_data);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        currentUserLeavesDs.addValueEventListener(valueEventListener);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                totalLeavesDs.addValueEventListener(totalLeavesListner);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(AppliedLeaves.this, AssignedActivities.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(AppliedLeaves.this, AllActivities.class);
                startActivity(intentInstruments);
                finishAffinity();
                return true;
            case 3:
                Intent intentAttendance = new Intent(AppliedLeaves.this, Attendance.class);
                finishAffinity();
                startActivity(intentAttendance);
                return true;
            case 4:
                Intent intentApplyLeave = new Intent(AppliedLeaves.this, ApplyLeave.class);
                finishAffinity();
                startActivity(intentApplyLeave);
                return true;
            case 5:
                Intent intentYourLeaves = new Intent(AppliedLeaves.this, AppliedLeaves.class);
                finishAffinity();
                startActivity(intentYourLeaves);
                return true;
            case 6:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AppliedLeaves.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private CharSequence menuIconWithText(Drawable r, String title) {
        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }
}