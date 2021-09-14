package com.application.labsolutions.admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Dashboard extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextView calibration,pm,bd,pmResolvedCount,pmWaitingCount,pmApprovalCount,pmScheduledCount,
            bdResolvedCount,bdWaitingCount,bdApprovalCount,bdScheduledCount,
            calResolvedCount,calWaitingCount,calApprovalCount,calScheduledCount;
    ProgressDialog progressDialog;
    int calResolved,calWaiting,calScheduled,calForApproval, pmResolved,pmWaiting,pmScheduled,pmForApproval,bdResolved,bdWaiting,bdScheduled,bdForApproval =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            toolbar.setTitle("Dashboard");
            setSupportActionBar(toolbar);
            firebaseAuth = FirebaseAuth.getInstance();
            calibration= findViewById(R.id.calibrationStats);
            pm= findViewById(R.id.pm);
            bd= findViewById(R.id.bd);
            pmResolvedCount = findViewById(R.id.pmResolvedCount);
            pmApprovalCount = findViewById(R.id.pmApprovalCount);
            pmWaitingCount = findViewById(R.id.pmWaitingCount);
            pmScheduledCount = findViewById(R.id.pmScheduledCount);
            bdResolvedCount = findViewById(R.id.bdResolvedCount);
            bdApprovalCount = findViewById(R.id.bdApprovalCount);
            bdScheduledCount = findViewById(R.id.bdScheduledCount);
            bdWaitingCount = findViewById(R.id.bdWaitingCount);
            calApprovalCount = findViewById(R.id.calApprovalCount);
            calResolvedCount = findViewById(R.id.calResolvedCount);
            calWaitingCount = findViewById(R.id.calWaitingCount);
            calScheduledCount = findViewById(R.id.calScheduledCount);
            progressDialog = ProgressDialog.show(Dashboard.this, "Please wait", "Loading data.....", true, false);
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference activities = rootRef.child("activities");
            Date referenceDate = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(referenceDate);
            c.add(Calendar.MONTH, -3);
            Long startDate=c.getTime().getTime();
            c.setTime(new Date());
            c.add(Calendar.DAY_OF_YEAR, 1);
            Long currentDate = c.getTime().getTime();
            ValueEventListener activitiesValueListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot activitiesSnapShot) {
                    try {
                        for (DataSnapshot activityDs : activitiesSnapShot.getChildren()) {
                            String currentActivityType = activityDs.child("activity-info/callType").getValue(String.class);
                            String currentActivityStatus = activityDs.child("status").getValue(String.class);
                            Long currentActivityScheduledDate = activityDs.child("timeStamp").getValue(Long.class);
                            if(currentActivityScheduledDate>=startDate && currentActivityScheduledDate<=currentDate) {
                                if(currentActivityType.equals("CAL") && currentActivityStatus.equals("Resolved")) {
                                    calResolved++;
                                }
                                else if(currentActivityType.equals("CAL") && currentActivityStatus.equals("Waiting for approval")) {
                                    calForApproval++;
                                }
                                else if(currentActivityType.equals("CAL") && currentActivityStatus.equals("Waiting for spares")) {
                                    calWaiting++;
                                }
                                else if(currentActivityType.equals("CAL") && currentActivityStatus.equals("Scheduled")) {
                                    calScheduled++;
                                }

                                else if(currentActivityType.equals("PM") && currentActivityStatus.equals("Resolved")) {
                                    pmResolved++;
                                }
                                else if(currentActivityType.equals("PM") && currentActivityStatus.equals("Waiting for approval")) {
                                    pmForApproval++;
                                }
                                else if(currentActivityType.equals("PM") && currentActivityStatus.equals("Waiting for spares")) {
                                    pmWaiting++;
                                }
                                else if(currentActivityType.equals("PM") && currentActivityStatus.equals("Scheduled")) {
                                    pmScheduled++;
                                }

                                else if(currentActivityType.equals("BD") && currentActivityStatus.equals("Resolved")) {
                                    bdResolved++;
                                }
                                else if(currentActivityType.equals("BD") && currentActivityStatus.equals("Waiting for approval")) {
                                    bdForApproval++;
                                }
                                else if(currentActivityType.equals("BD") && currentActivityStatus.equals("Waiting for spares")) {
                                    bdWaiting++;
                                }
                                else if(currentActivityType.equals("BD") && currentActivityStatus.equals("Scheduled")) {
                                    bdScheduled++;
                                }

                            }
                        }
                        calResolvedCount.setText(String.valueOf(calResolved));
                        calApprovalCount.setText(String.valueOf(calForApproval));
                        calScheduledCount.setText(String.valueOf(calScheduled));
                        calWaitingCount.setText(String.valueOf(calWaiting));

                        pmResolvedCount.setText(String.valueOf(pmResolved));
                        pmApprovalCount.setText(String.valueOf(pmForApproval));
                        pmScheduledCount.setText(String.valueOf(pmScheduled));
                        pmWaitingCount.setText(String.valueOf(pmWaiting));

                        bdResolvedCount.setText(String.valueOf(bdResolved));
                        bdApprovalCount.setText(String.valueOf(bdForApproval));
                        bdScheduledCount.setText(String.valueOf(bdScheduled));
                        bdWaitingCount.setText(String.valueOf(bdWaiting));

                        Commons.dismissProgressDialog(progressDialog);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Commons.dismissProgressDialog(progressDialog);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            activities.addValueEventListener(activitiesValueListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
        calibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(Dashboard.this, DashboardDetails.class);
                newIntent.putExtra("type", "CAL");
                startActivity(newIntent);
            }
        });
        bd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(Dashboard.this, DashboardDetails.class);
                newIntent.putExtra("type", "BD");
                startActivity(newIntent);
            }
        });
        pm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(Dashboard.this, DashboardDetails.class);
                newIntent.putExtra("type", "PM");
                startActivity(newIntent);
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(Dashboard.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(Dashboard.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(Dashboard.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(Dashboard.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(Dashboard.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(Dashboard.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(Dashboard.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentLeaves = new Intent(Dashboard.this, AllUpcomingLeaves.class);
                finishAffinity();
                startActivity(intentLeaves);
                return true;
            case 9:
                Intent intentExport = new Intent(Dashboard.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 10:
                Intent intentDasboard = new Intent(Dashboard.this, Statistics.class);
                finishAffinity();
                startActivity(intentDasboard);
                return true;
            case 11:
                Intent intentAdminDashboard = new Intent(Dashboard.this, Dashboard.class);
                finishAffinity();
                startActivity(intentAdminDashboard);
                return true;
            case 12:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(Dashboard.this, LoginActivity.class);
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