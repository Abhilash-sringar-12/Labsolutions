package com.application.labsolutions.admin;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.application.labsolutions.listviews.AppliedLeavesView;
import com.application.labsolutions.listviews.LeaveDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class AllUpcomingLeaves extends AppCompatActivity {
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
                toolbar.setTitle("All Upcoming Applied Leaves");
                setSupportActionBar(toolbar);
                emptyImage = findViewById(R.id.emptyImage);
                listview = findViewById(R.id.leavesListView);
                firebaseAuth = FirebaseAuth.getInstance();
                searchView = findViewById(R.id.searchLeaves);
                final String currentUser = firebaseAuth.getCurrentUser().getUid();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                final DatabaseReference AllLeavesDs = rootRef.child("applied-leaves");
                final DatabaseReference totalLeavesDs = rootRef.child("leaves").child(currentUser);
                progressDialog = ProgressDialog.show(AllUpcomingLeaves.this, "Please wait", "Loading Applied Leaves.....", true, false);
                ValueEventListener totalLeavesListner = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        final String totalLeaves = snapshot.getValue() != null ? snapshot.getValue(String.class) : "0.0";
                        ValueEventListener valueEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChildren()) {
                                    leavesList.clear();
                                    try {
                                        for (DataSnapshot allLeaves : snapshot.getChildren()) {
                                            for (final DataSnapshot leavesDs : allLeaves.getChildren()) {
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
                                        }

                                        Collections.sort(leavesList, LeaveDetails.leaves);
                                        final AppliedLeavesView adapter = new AppliedLeavesView(AllUpcomingLeaves.this, leavesList, "admin");
                                        listview.setHasFixedSize(true);
                                        listview.setLayoutManager(new LinearLayoutManager(AllUpcomingLeaves.this));
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
                        AllLeavesDs.addValueEventListener(valueEventListener);
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
                Intent intentAdminActivity = new Intent(AllUpcomingLeaves.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(AllUpcomingLeaves.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(AllUpcomingLeaves.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(AllUpcomingLeaves.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(AllUpcomingLeaves.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(AllUpcomingLeaves.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(AllUpcomingLeaves.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentLeaves = new Intent(AllUpcomingLeaves.this, AllUpcomingLeaves.class);
                finishAffinity();
                startActivity(intentLeaves);
                return true;
            case 9:
                Intent intentExport = new Intent(AllUpcomingLeaves.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 10:
                Intent intentDasboard = new Intent(AllUpcomingLeaves.this, Statistics.class);
                finishAffinity();
                startActivity(intentDasboard);
                return true;
            case 11:
                Intent intentAdminDashboard = new Intent(AllUpcomingLeaves.this, Dashboard.class);
                finishAffinity();
                startActivity(intentAdminDashboard);
                return true;
            case 12:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AllUpcomingLeaves.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_pie_chart_24), "Statistics"));
        menu.add(0, 11, 11,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_dashboard_customize_24), "Dashboard"));
        menu.add(0, 12, 12,
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
