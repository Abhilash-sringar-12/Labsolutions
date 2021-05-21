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
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.listviews.ActivitiesInfoDetails;
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

import static com.application.labsolutions.commons.Commons.setRecycleItems;

public class AllActivities extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    RecyclerView listview;
    ImageView emptyImage;
    SearchView searchView;
    ArrayList<ActivitiesInfoDetails> activitiesList = new ArrayList<ActivitiesInfoDetails>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_current_customer);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("All Call Activities");
                setSupportActionBar(toolbar);
                listview = findViewById(R.id.activitiesListView);
                emptyImage = findViewById(R.id.emptyImage);
                firebaseAuth = FirebaseAuth.getInstance();
                searchView = findViewById(R.id.searchInstruments);
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference allActivities = rootRef.child("activities");
                progressDialog = ProgressDialog.show(AllActivities.this, "Please wait", "Loading all activities.....", true, false);

                final ValueEventListener eventActivityListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        try {
                            if (ds.hasChildren()) {
                                activitiesList.clear();
                                for (DataSnapshot activitiesDs : ds.getChildren()) {
                                    if (activitiesDs.child("customer-info").getValue() != null) {
                                        String activityId = activitiesDs.getKey().toString();
                                        String activityInstrumentId = activitiesDs.child("customer-info").child("companyName").getValue(String.class);
                                        String activityName = activitiesDs.child("activity-info").child("instrumentId").getValue(String.class) + "/" + activitiesDs.child("activity-info").child("callType").getValue(String.class);
                                        String activityDescription = activitiesDs.child("activity-info").child("instrumentId").getValue(String.class);
                                        String activityStatus = activitiesDs.child("status").getValue(String.class);
                                        long timeStamp = activitiesDs.child("timeStamp").getValue() != null ? (long) activitiesDs.child("timeStamp").getValue() : 0l;
                                        activitiesList.add(new ActivitiesInfoDetails(activityId, activityInstrumentId, activityName, activityDescription, activityStatus, timeStamp));
                                    }
                                }

                                Commons.dismissProgressDialog(progressDialog);
                                Collections.reverse(activitiesList);
                                setRecycleItems(AllActivities.this, activitiesList, searchView, listview, "info");

                            } else {
                                Commons.dismissProgressDialog(progressDialog);
                                searchView.setVisibility(View.GONE);
                                emptyImage.setImageResource(R.drawable.no_data);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                allActivities.orderByChild("timeStamp").addValueEventListener(eventActivityListener);
            } else {
                Intent intent = new Intent(AllActivities.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(AllActivities.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(AllActivities.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(AllActivities.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(AllActivities.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(AllActivities.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(AllActivities.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(AllActivities.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentLeaves = new Intent(AllActivities.this, AllUpcomingLeaves.class);
                finishAffinity();
                startActivity(intentLeaves);
                return true;
            case 9:
                Intent intentExport = new Intent(AllActivities.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 10:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AllActivities.this, LoginActivity.class);
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
