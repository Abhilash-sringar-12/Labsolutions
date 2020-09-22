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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.labsolutions.R;
import com.example.labsolutions.admin.LoginActivity;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.listviews.CreatedInstrumentView;
import com.example.labsolutions.listviews.ListInstrumentDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WorkAdminInstruments extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ArrayList<ListInstrumentDetails> instrumentList = new ArrayList<ListInstrumentDetails>();
    RecyclerView listview;
    TextView emptyText;
    ImageView emtyImage;
    SearchView searchView;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_instruments);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Instruments");
            setSupportActionBar(toolbar);
            emtyImage = findViewById(R.id.emptyImage);
            listview = findViewById(R.id.instrumentListView);
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference instrumentsdRef = rootRef.child("instruments");
            searchView = findViewById(R.id.searchInstruments);
            progressDialog = ProgressDialog.show(WorkAdminInstruments.this, "Please wait", "Loading instruments....", true, false);
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot companyDs : snapshot.getChildren()) {
                                for (DataSnapshot instrumentTypeDs : companyDs.getChildren()) {
                                    for (DataSnapshot instrumentDetailsDs : instrumentTypeDs.getChildren()) {
                                        String companyName = instrumentDetailsDs.child("companyName").getValue().toString();
                                        String instrumentId = instrumentDetailsDs.child("instrumentId").getValue().toString();
                                        String instrumentType = instrumentDetailsDs.child("instrumentType").getValue().toString();
                                        String department = instrumentDetailsDs.child("department").getValue().toString();
                                        String amcFromDate = instrumentDetailsDs.child("amcFromDate").getValue().toString();
                                        String amcToDate = instrumentDetailsDs.child("amcToDate").getValue().toString();
                                        instrumentList.add(new ListInstrumentDetails(companyName, instrumentId, instrumentType, department, amcFromDate, amcToDate));
                                    }
                                }
                            }
                            Commons.dismissProgressDialog(progressDialog);
                            final CreatedInstrumentView adapter = new CreatedInstrumentView(WorkAdminInstruments.this, instrumentList, "workAdmin");
                            listview.setHasFixedSize(true);
                            listview.setLayoutManager(new LinearLayoutManager(WorkAdminInstruments.this));
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
                        } else {
                            Commons.dismissProgressDialog(progressDialog);
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
            instrumentsdRef.addListenerForSingleValueEvent(eventListener);
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
                Intent intentAdminActivity = new Intent(WorkAdminInstruments.this, WorkadminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(WorkAdminInstruments.this, WorkAdminInstruments.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 3:
                Intent intentCurrentActivities = new Intent(WorkAdminInstruments.this, WorkAdminAssignActivity.class);
                finishAffinity();
                startActivity(intentCurrentActivities);
                return true;
            case 4:
                Intent intentAllActivities = new Intent(WorkAdminInstruments.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 5:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(WorkAdminInstruments.this, LoginActivity.class);
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
