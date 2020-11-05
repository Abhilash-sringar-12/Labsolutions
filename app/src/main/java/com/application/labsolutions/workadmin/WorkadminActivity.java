package com.application.labsolutions.workadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.LoginActivity;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.listviews.ListUserDetails;
import com.application.labsolutions.listviews.RegisteredUsersView;
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

public class WorkadminActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    ArrayList<ListUserDetails> userList = new ArrayList<ListUserDetails>();
    AbsListView listview;
    TextView emptyText;
    ImageView emtyImage;
    private ProgressDialog progressDialog;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.adminactivity);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Users");
            setSupportActionBar(toolbar);
            emtyImage = findViewById(R.id.emptyImage);
            searchView = findViewById(R.id.searchUsers);
            listview = findViewById(R.id.listView1);
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference usersdRef = rootRef.child("users");
            progressDialog = ProgressDialog.show(WorkadminActivity.this, "Please wait", "Loading users....", true, false);


            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        if (snapshot.hasChildren()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {
                                String name = ds.child("user").getValue(String.class);
                                String type = ds.child("userType").getValue(String.class);
                                String phoneNumber = ds.child("phoneNumber").getValue(String.class);
                                String emailId = ds.child("mailId").getValue(String.class);
                                String uid = ds.child("uid").getValue(String.class);
                                userList.add(new ListUserDetails(R.drawable.login_user, name, phoneNumber, uid, emailId, type));
                            }
                            Commons.dismissProgressDialog(progressDialog);
                            final RegisteredUsersView registeredUsersView = new RegisteredUsersView(WorkadminActivity.this, R.layout.row_item, userList, "workadmin");
                            listview.setAdapter(registeredUsersView);
                            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    registeredUsersView.getFilter().filter(query);
                                    return false;
                                }

                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    registeredUsersView.getFilter().filter(newText);
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
            usersdRef.orderByChild("user").addListenerForSingleValueEvent(eventListener);
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
                Intent intentAdminActivity = new Intent(WorkadminActivity.this, WorkadminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(WorkadminActivity.this, WorkAdminInstruments.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 3:
                Intent intentCurrentActivities = new Intent(WorkadminActivity.this, WorkAdminAssignActivity.class);
                finishAffinity();
                startActivity(intentCurrentActivities);
                return true;
            case 4:
                Intent intentAllActivities = new Intent(WorkadminActivity.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 5:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(WorkadminActivity.this, LoginActivity.class);
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
