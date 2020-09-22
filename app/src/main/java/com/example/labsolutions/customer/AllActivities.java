package com.example.labsolutions.customer;

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
                emptyImage = findViewById(R.id.emptyImage);
                listview = findViewById(R.id.activitiesListView);
                firebaseAuth = FirebaseAuth.getInstance();
                searchView = findViewById(R.id.searchInstruments);
                final String currentUser = firebaseAuth.getCurrentUser().getUid();
                final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentUserActivitiesDs = rootRef.child("activity-users").child("completed-activity");
                progressDialog = ProgressDialog.show(AllActivities.this, "Please wait", "Loading your activities.....", true, false);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.hasChildren()) {
                                activitiesList.clear();
                                for (DataSnapshot activitiesCurrentUserDs : snapshot.getChildren()) {
                                    final String activityId = activitiesCurrentUserDs.getKey().toString();
                                    final DatabaseReference allActivities = rootRef.child("activities").child(activityId);
                                    final ValueEventListener eventActivityListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot activitiesDs) {
                                            try {
                                                if (activitiesDs.hasChildren()) {
                                                    String activityInstrumentId = activitiesDs.child("activity-info").child("instrumentId").getValue() != null
                                                            ? activitiesDs.child("activity-info").child("instrumentId").getValue(String.class) : "";
                                                    String activityName = activitiesDs.child("activity-info").child("callType").getValue() != null
                                                            ? activitiesDs.child("activity-info").child("callType").getValue(String.class) : "";
                                                    String activityDescription = activitiesDs.child("activity-info").child("instrumentId").getValue() != null
                                                            ? activitiesDs.child("activity-info").child("instrumentId").getValue(String.class) : "";
                                                    String activityStatus = activitiesDs.child("status").getValue() != null ?
                                                            activitiesDs.child("status").getValue(String.class) : "";
                                                    long timeStamp = activitiesDs.child("timeStamp").getValue() != null ? (long) activitiesDs.child("timeStamp").getValue() : 0l;
                                                    activitiesList.add(new ActivitiesInfoDetails(activityId, activityInstrumentId, activityName, activityDescription, activityStatus, timeStamp));
                                                    Collections.sort(activitiesList, ActivitiesInfoDetails.activites);
                                                    Collections.reverse(activitiesList);
                                                    Commons.dismissProgressDialog(progressDialog);
                                                    setRecycleItems(AllActivities.this, activitiesList, searchView, listview, "info");
                                                } else {
                                                    Commons.dismissProgressDialog(progressDialog);
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
                                    allActivities.addListenerForSingleValueEvent(eventActivityListener);
                                }
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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
                currentUserActivitiesDs.orderByChild("customer").equalTo(currentUser).addListenerForSingleValueEvent(eventListener);
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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_create_24), "Register Call"));
        menu.add(0, 2, 2,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_notifications_active_24), "Current Calls"));
        menu.add(0, 3, 3,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_history_24), "History"));
        menu.add(0, 4, 4,
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(AllActivities.this, CustomerActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentCreateUserActivity = new Intent(AllActivities.this, CurrentCustomerActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 3:
                Intent intentAddInstrument = new Intent(AllActivities.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 4:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(AllActivities.this, LoginActivity.class);
                finishAffinity();
                startActivity(intentSignOut);
                return true;

        }
        return super.onOptionsItemSelected(item);

    }

}
