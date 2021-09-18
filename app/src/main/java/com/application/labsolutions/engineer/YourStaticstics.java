package com.application.labsolutions.engineer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.LoginActivity;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.listviews.ActivitiesInfoDetails;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static com.application.labsolutions.commons.Commons.setRecycleItems;

public class YourStaticstics extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    PieChart pieChart;
    PieData pieData;
    PieDataSet pieDataSet;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_staticstics);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try {
            toolbar.setTitle("Your Stats");
            setSupportActionBar(toolbar);
            firebaseAuth = FirebaseAuth.getInstance();
            final String currentUser = firebaseAuth.getCurrentUser().getEmail();
            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            progressDialog = ProgressDialog.show(YourStaticstics.this, "Please wait", "Loading your Stats....", true, false);
            final DatabaseReference allActivities = rootRef.child("activities");
            linearLayout = findViewById(R.id.pie);
            final int[] BD = {0};
            final int[] CAL = {0};
            final int[] PM = {0};

            final ValueEventListener eventActivityListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot activitiesDs) {
                    try {
                        for (DataSnapshot ds : activitiesDs.getChildren()) {
                            if (ds.hasChildren() && ds.child("engineer-info/mailId").getValue() != null && ds.child("engineer-info/mailId").getValue(String.class).equals(currentUser) && ds.child("status").getValue() != null
                                    && ds.child("status").getValue(String.class).equals("Resolved")) {
                                String callType = ds.child("activity-info").child("callType").getValue() != null
                                        ? ds.child("activity-info").child("callType").getValue(String.class) : "";

                                if (callType.equals("BD")) {
                                    BD[0]++;
                                } else if (callType.equals("CAL")) {
                                    CAL[0]++;
                                } else if (callType.equals("PM")) {
                                    PM[0]++;
                                }
                            }
                        }
                        int totalCalls = CAL[0] + BD[0] + PM[0];
                        ArrayList pieList = new ArrayList<>();
                        if (CAL[0] != 0)
                            pieList.add(new PieEntry(CAL[0], "CAL", 0));
                        if (PM[0] != 0)
                            pieList.add(new PieEntry(PM[0], "PM", 1));
                        if (BD[0] != 0)
                            pieList.add(new PieEntry(BD[0], "BD", 2));
                        pieDataSet = new PieDataSet(pieList, "Total Calls Resolved : " + totalCalls);
                        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                        pieDataSet.setSliceSpace(2f);
                        pieDataSet.setValueTextColor(Color.WHITE);
                        pieDataSet.setValueTextSize(10f);
                        pieDataSet.setSliceSpace(5f);
                        pieChart = findViewById(R.id.pieChart);
                        pieData = new PieData(pieDataSet);
                        pieChart.setData(pieData);
                        pieChart.setVisibility(View.VISIBLE);
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
            allActivities.addListenerForSingleValueEvent(eventActivityListener);

        } catch (Exception e) {
            e.printStackTrace();
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
                Intent intentAdminActivity = new Intent(YourStaticstics.this, AssignedActivities.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(YourStaticstics.this, AllActivities.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 3:
                Intent intentAttendance = new Intent(YourStaticstics.this, Attendance.class);
                finishAffinity();
                startActivity(intentAttendance);
                return true;
            case 4:
                Intent intentApplyLeave = new Intent(YourStaticstics.this, ApplyLeave.class);
                finishAffinity();
                startActivity(intentApplyLeave);
                return true;
            case 5:
                Intent intentYourLeaves = new Intent(YourStaticstics.this, AppliedLeaves.class);
                finishAffinity();
                startActivity(intentYourLeaves);
                return true;
            case 6:
                Intent intentYourStats = new Intent(YourStaticstics.this, YourStaticstics.class);
                finishAffinity();
                startActivity(intentYourStats);
                return true;
            case 7:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(YourStaticstics.this, LoginActivity.class);
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