package com.application.labsolutions.engineer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.admin.LoginActivity;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.google.android.gms.common.internal.service.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Attendance extends AppCompatActivity implements LocationListener {
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    FirebaseAuth firebaseAuth;
    String adminTokenId, userName, details, type = "";
    double lat, longi = 0;
    ProgressDialog progressDialog;
    Button logAttendance;
    Button logOutAttendance;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    String currentDate = DateUtility.formatDate(new Date().toString());
    String currentYear = currentDate.split("-")[2];
    String currentMonth = monthName[Integer.parseInt(currentDate.split("-")[1]) - 1];
    DatabaseReference attendanceDatabaseReference;
    TextView text;
    List<String> holidaysList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_attendance);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Log Attendance");
            setSupportActionBar(toolbar);
            firebaseAuth = FirebaseAuth.getInstance();
            logAttendance = findViewById(R.id.logAttendance);
            logOutAttendance = findViewById(R.id.logOutAttendance);
            text = findViewById(R.id.attendanceText);
            if (new Date().getDay() != 0 && new Date().getDay() != 6) {
                attendanceDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("calender/" + currentYear + "/" + currentMonth + "/" + currentDate + "/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("admin");
                final DatabaseReference currentUserDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("users/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
                final DatabaseReference holidays = FirebaseDatabase.getInstance().getReference().child("holidays");
                if (currentUserDatabaseReference != null) {
                    progressDialog = ProgressDialog.show(Attendance.this, "Please wait", "Loading....", true, false);
                    ValueEventListener userValueEventListener = new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChildren()) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    userName = ds.child("user").child("user").getValue() != null ? ds.child("user").child("user").getValue(String.class) : "";
                                }
                                ValueEventListener adminValueEventListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.hasChildren()) {
                                            for (DataSnapshot ds : snapshot.getChildren()) {
                                                adminTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                            }
                                            ValueEventListener holidaysListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChildren()) {
                                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                                            holidaysList.add(ds.getKey());
                                                        }
                                                    }
                                                    ValueEventListener attendanceValueListener = new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if (snapshot.hasChildren()) {
                                                                if (!snapshot.child("type").getValue(String.class).equals("HD") && !snapshot.child("type").getValue(String.class).equals("PA")) {
                                                                    logAttendance.setVisibility(View.GONE);
                                                                    logOutAttendance.setVisibility(View.GONE);
                                                                    text.setText("You are on leave today! You cannot log attendance when you are on leave!");
                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                } else if (holidaysList.contains(DateUtility.formatDate(new Date().toString()))) {
                                                                    logAttendance.setVisibility(View.GONE);
                                                                    logOutAttendance.setVisibility(View.GONE);
                                                                    text.setText("Today is a Holiday!");
                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                } else if (snapshot.child("loginDetails").exists() && snapshot.child("logoutDetails").exists()) {
                                                                    logAttendance.setVisibility(View.GONE);
                                                                    logOutAttendance.setVisibility(View.GONE);
                                                                    text.setText("You have Logged Attendance for Today!!!");
                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                } else if (snapshot.child("loginDetails").exists()) {
                                                                    logAttendance.setVisibility(View.GONE);
                                                                    logOutAttendance.setVisibility(View.VISIBLE);
                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                } else {
                                                                    logAttendance.setVisibility(View.VISIBLE);
                                                                    logOutAttendance.setVisibility(View.GONE);
                                                                    Commons.dismissProgressDialog(progressDialog);
                                                                }
                                                            } else {
                                                                logAttendance.setVisibility(View.VISIBLE);
                                                                logOutAttendance.setVisibility(View.GONE);
                                                                Commons.dismissProgressDialog(progressDialog);
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    };
                                                    attendanceDatabaseReference.addValueEventListener(attendanceValueListener);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            holidays.addValueEventListener(holidaysListener);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                adminDatabaseReference.addValueEventListener(adminValueEventListener);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    currentUserDatabaseReference.addValueEventListener(userValueEventListener);

                }
                logAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            progressDialog = ProgressDialog.show(Attendance.this, "Please wait", "Loading Logging In....", true, false);
                            execute();
                            details = "loginDetails";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                logOutAttendance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            progressDialog = ProgressDialog.show(Attendance.this, "Please wait", "Loading Logging Out....", true, false);
                            execute();
                            details = "logoutDetails";
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                logAttendance.setVisibility(View.GONE);
                logOutAttendance.setVisibility(View.GONE);
                text.setText("Happy Weekend!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void execute() {
        if (ContextCompat.checkSelfPermission(Attendance.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Attendance.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(Attendance.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(Attendance.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {

            getLocation();
            details = "loginDetails";
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(Attendance.this, "Your attendance will not be logged if you deny", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } catch (SecurityException e) {
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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(Attendance.this, AssignedActivities.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent intentInstruments = new Intent(Attendance.this, AllActivities.class);
                startActivity(intentInstruments);
                finishAffinity();
                return true;
            case 3:
                Intent intentAttendance = new Intent(Attendance.this, Attendance.class);
                finishAffinity();
                startActivity(intentAttendance);
                return true;
            case 4:
                Intent intentApplyLeave = new Intent(Attendance.this, ApplyLeave.class);
                finishAffinity();
                startActivity(intentApplyLeave);
                return true;
            case 5:
                Intent intentYourLeaves = new Intent(Attendance.this, AppliedLeaves.class);
                finishAffinity();
                startActivity(intentYourLeaves);
                return true;
            case 6:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(Attendance.this, LoginActivity.class);
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


    public void setAttendance(double lat, double longi, final String details) {
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, longi, 1);
            String address = addresses.get(0).getAddressLine(0);
            final Map<String, String> attendanceDetails = new HashMap<>();
            attendanceDetails.put("loginDate", DateUtility.getCurrentDate());
            attendanceDetails.put("loginTime", DateUtility.getCurrentTime());
            attendanceDetails.put("loginTimeStamp", String.valueOf(new Date().getTime()));
            attendanceDetails.put("loginLocation", address);

            attendanceDatabaseReference.child("type").setValue(type.isEmpty() ? "PA" : "HD").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        attendanceDatabaseReference.child(details).setValue(attendanceDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Commons.dismissProgressDialog(progressDialog);
                                if (task.isSuccessful()) {
                                    Toast.makeText(Attendance.this, "You have successfully Logged your attendance", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(Attendance.this, "Oops something went wrong", Toast.LENGTH_SHORT).show();


                                }

                            }
                        });
                    }
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if (location != null) {
            locationManager.removeUpdates(this);
            setAttendance(location.getLatitude(), location.getLongitude(), details);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(Attendance.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }
}