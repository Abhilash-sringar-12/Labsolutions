package com.example.labsolutions.customer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labsolutions.R;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.listviews.ActivityInfo;
import com.example.labsolutions.listviews.ListActivitiesinfo;
import com.example.labsolutions.mailutils.MailUtility;
import com.example.labsolutions.services.ApiService;
import com.example.labsolutions.services.Client;
import com.example.labsolutions.services.SendNotification;
import com.example.labsolutions.workadmin.AllActivities;
import com.example.labsolutions.workadmin.RescheduleCall;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ActivityDetails extends AppCompatActivity {

    private TextView mainInfo;
    FirebaseAuth firebaseAuth;
    String currentuserMailId;
    ArrayList<ActivityInfo> activityInfoList = new ArrayList<ActivityInfo>();
    AbsListView listview;
    Button export, reassign, reschedule, sendMail;
    RelativeLayout relativeLayout;
    RelativeLayout resceduleRelativeLayout;
    ProgressDialog progressDialog;
    String activityId;
    String customerId;
    String engineerMailId;
    String enginnerId;
    String customerMailId;
    String spareQtyOne, spareQtyTwo, spareQtyThree, spareQtyFour, spareDescOne, spareDescTwo, spareDescThree, spareDescFour;
    String closureTime, adminTokenId, enginnerTokenid, customerTokenId, closureDate, durationHours, durationMinutes, resolutionDescription, customerCompany, customerCompanyAddress, customerName, customerDepartment, instrumentId, problemDescription, date, time, approvedTime, approvedDate, engineerName, scheduledDate, scheduledTime;
    ApiService apiService;
    final static String MESSAGE_BODY = "\"<head>\\n\" +\n" +
            "                                                                \"<title>Labsolutions</title>\\n\" +\n" +
            "                                                                \"<meta content=\\\"text/html; charset=utf-8\\\" http-equiv=\\\"Content-Type\\\">\\n\" +\n" +
            "                                                                \"<meta content=\\\"width=device-width\\\" name=\\\"viewport\\\">\\n\" +\n" +
            "                                                                \"\\n\" +\n" +
            "                                                                \"</head>\\n\" +\n" +
            "                                                                \"<body style=\\\"background-color: #f4f4f5;\\\">\\n\" +\n" +
            "                                                                \"<table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" style=\\\"width: 100%; height: 100%; background-color: #f4f4f5; text-align: center;\\\">\\n\" +\n" +
            "                                                                \"<tbody><tr>\\n\" +\n" +
            "                                                                \"<td style=\\\"text-align: center;\\\">\\n\" +\n" +
            "                                                                \"<table align=\\\"center\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" id=\\\"body\\\" style=\\\"background-color: #fff; width: 100%; max-width: 680px; height: 100%;\\\">\\n\" +\n" +
            "                                                                \"<tbody><tr>\\n\" +\n" +
            "                                                                \"<td>\\n\" +\n" +
            "                                                                \"<table align=\\\"center\\\" cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" class=\\\"page-center\\\" style=\\\"text-align: left; padding-bottom: 88px; width: 100%; padding-left: 120px; padding-right: 120px;\\\">\\n\" +\n" +
            "                                                                \"<tbody><tr>\\n\" +\n" +
            "                                                                \"<td style=\\\"padding-top: 24px;\\\">\\n\" +\n" +
            "                                                                \"<img src=\\\"http://www.hostgator.co.in/files/writeable/uploads/hostgator166687/image/labsolutionslogo3.png\\\" style=\\\"width: auto;\\\">\\n\" +\n" +
            "                                                                \"</td>\\n\" +\n" +
            "                                                                \"</tr>\\n\" +\n" +
            "                                                                \"<tr>\\n\" +\n" +
            "                                                                \"<td colspan=\\\"2\\\" style=\\\"padding-top: 72px; -ms-text-size-adjust: 100%; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: 100%; color: #000000; font-family: 'Postmates Std', 'Helvetica', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif; font-size: 15px; font-smoothing: always; font-style: normal; font-weight: 600; letter-spacing: -1.6px; line-height: 52px; mso-line-height-rule: exactly; text-decoration: none;\\\">Thank you for choosing Labsoluntions Instruments & Consultancy pvt ltd</td>\\n\" +\n" +
            "                                                                \"</tr>\\n\" +\n" +
            "                                                                \"<tr>\\n\" +\n" +
            "                                                                \"  <td>Please download the attached service report </td>\\n\" +\n" +
            "                                                                \"  </tr>\\n\" +\n" +
            "                                                                \"<tr>\\n\" +\n" +
            "                                                                \"<td style=\\\"padding-top: 48px; padding-bottom: 48px;\\\">\\n\" +\n" +
            "                                                                \"<table cellpadding=\\\"0\\\" cellspacing=\\\"0\\\" style=\\\"width: 100%\\\">\\n\" +\n" +
            "                                                                \"<tbody><tr>\\n\" +\n" +
            "                                                                \"<td style=\\\"width: 100%; height: 1px; max-height: 1px; background-color: #d9dbe0; opacity: 0.81\\\"></td>\\n\" +\n" +
            "                                                                \"</tr>\\n\" +\n" +
            "                                                                \"</tbody></table>\\n\" +\n" +
            "                                                                \"</td>\\n\" +\n" +
            "                                                                \"</tr>\\n\" +\n" +
            "                                                                \"</tbody></table>\\n\" +\n" +
            "                                                                \"</td>\\n\" +\n" +
            "                                                                \"</tr>\\n\" +\n" +
            "                                                                \"</tbody></table>\\n\" +\n" +
            "                                                                \"\\n\" +\n" +
            "                                                                \"\\n\" +\n" +
            "                                                                \"\\n\" +\n" +
            "                                                                \"</body>\";";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_details);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Call Activity Details");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            currentuserMailId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            listview = findViewById(R.id.activityDetailsView);
            export = findViewById(R.id.generateReport);
            reassign = findViewById(R.id.reassign);
            sendMail = findViewById(R.id.sendMail);
            reschedule = findViewById(R.id.reScheduleCallButton);
            relativeLayout = findViewById(R.id.exportLayout);
            resceduleRelativeLayout = findViewById(R.id.rescheduleLayout);
            activityId = intent.getStringExtra("activityId");
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            if (!activityId.isEmpty()) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentActivityDs = rootRef.child("activities").child(activityId);
                final DatabaseReference customerDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference()
                        .child("users");
                if (currentActivityDs != null) {
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot currentActivityInfo = snapshot.child("customer-info");
                            DataSnapshot engineerActivityInfo = snapshot.child("engineer-info");
                            DataSnapshot activityInfo = snapshot.child("activity-info");
                            DataSnapshot startInfo = snapshot.child("start-info");
                            DataSnapshot scheduledInfo = snapshot.child("scheduled-info");
                            final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference()
                                    .child("admin");
                            customerCompany = currentActivityInfo.child("companyName").getValue() != null ? currentActivityInfo.child("companyName").getValue(String.class) : "";
                            customerCompanyAddress = currentActivityInfo.child("companyAddress").getValue() != null ? currentActivityInfo.child("companyAddress").getValue(String.class) : "";
                            instrumentId = activityInfo.child("instrumentId").getValue() != null
                                    ? activityInfo.child("instrumentId").getValue(String.class) : "";
                            String callType = activityInfo.child("callType").getValue() != null
                                    ? activityInfo.child("callType").getValue(String.class) : "";
                            String modelAndMake = activityInfo.child("modelAndMake").getValue() != null
                                    ? activityInfo.child("modelAndMake").getValue(String.class) : "";
                            problemDescription = activityInfo.child("problemDescription").getValue() != null
                                    ? activityInfo.child("problemDescription").getValue(String.class) : "";
                            customerName = currentActivityInfo.child("user").getValue() != null
                                    ? currentActivityInfo.child("user").getValue(String.class) : "";
                            customerDepartment = currentActivityInfo.child("department").getValue() != null
                                    ? currentActivityInfo.child("department").getValue(String.class) : "";
                            time = startInfo.child("time").getValue() != null
                                    ? startInfo.child("time").getValue(String.class) : "";
                            date = startInfo.child("date").getValue() != null
                                    ? startInfo.child("date").getValue(String.class) : "";
                            scheduledDate = scheduledInfo.child("date") != null
                                    ? scheduledInfo.child("date").getValue(String.class) : "";
                            scheduledTime = scheduledInfo.child("time") != null
                                    ? scheduledInfo.child("time").getValue(String.class) : "";
                            String customerPhoneNumber = currentActivityInfo.child("phoneNumber").getValue() != null
                                    ? currentActivityInfo.child("phoneNumber").getValue(String.class) : "";
                            customerMailId = currentActivityInfo.child("mailId").getValue() != null
                                    ? currentActivityInfo.child("mailId").getValue(String.class) : "";
                            engineerMailId = engineerActivityInfo.child("mailId").getValue() != null ? engineerActivityInfo.child("mailId").getValue(String.class) : "";
                            String status = snapshot.child("status").getValue() != null ? snapshot.child("status").getValue(String.class) : "";
                            activityInfoList.add(new ActivityInfo("Instrument id : " + instrumentId + "\n\n" + "Call Type : " + callType + "\n\n" + "Model and Make : " + modelAndMake + "\n\n" + "Problem description : " + problemDescription, "", "", ""));
                            activityInfoList.add(new ActivityInfo(customerName + " registered a call", date + " " + time, customerPhoneNumber, customerMailId));
                            activityInfoList.add(new ActivityInfo("Call Scheduled date & time : ", scheduledDate + " " + scheduledTime, "", ""));
                            if (status.equals("Approved by admin")) {
                                DataSnapshot approvedInfo = snapshot.child("admin-approved-info");
                                activityInfoList.add(new ActivityInfo("Admin approved the registered call", approvedInfo.child("date").getValue(String.class) + " " + approvedInfo.child("time").getValue(String.class), "", ""));
                                if (currentuserMailId.equals("labsolutions.ic.app@gmail.com") || currentuserMailId.equals("service@labsolutions-ic.in")) {
                                    if (snapshot.child("declined-data") != null) {
                                        for (DataSnapshot dataSnapshot : snapshot.child("declined-data").getChildren()) {
                                            activityInfoList.add(new ActivityInfo(dataSnapshot.getValue(String.class) + " declined the registered call", snapshot.child("engineer-approved-info").child("date").getValue(String.class) + " " + snapshot.child("engineer-approved-info").child("time").getValue(String.class), "", ""));
                                        }
                                    }
                                }
                            }
                            if (status.equals("Rescheduled")) {
                                DataSnapshot approvedInfo = snapshot.child("admin-approved-info");
                                activityInfoList.add(new ActivityInfo("Admin rescheduled the registered call", approvedInfo.child("date").getValue(String.class) + " " + approvedInfo.child("time").getValue(String.class), "", ""));
                            }
                            if (status.equals("Declined by admin")) {
                                DataSnapshot approvedInfo = snapshot.child("admin-approved-info");
                                activityInfoList.add(new ActivityInfo("Admin declined the registered call\nDecline Reason: " + snapshot.child("declineReason").getValue(String.class), approvedInfo.child("date").getValue(String.class) + " " + approvedInfo.child("time").getValue(String.class), "", ""));

                            }
                            if (status.equals("Scheduled")) {

                                setEngineerData(snapshot, engineerActivityInfo);
                            }
                            if (status.equals("Resolved") || status.equals("Editing Service Report")) {
                                setEngineerData(snapshot, engineerActivityInfo);
                                setClosureData(snapshot, "Resolved");
                            }
                            if (status.equals("Waiting for spares")) {
                                DataSnapshot approvedInfo = snapshot.child("admin-approved-info");
                                activityInfoList.add(new ActivityInfo("Admin approved the registered call", approvedInfo.child("date").getValue(String.class) + " " + approvedInfo.child("time").getValue(String.class), "", ""));
                                DataSnapshot waitingDataInfo = snapshot.child("waiting-data").child("start-data");
                                String closureTime = waitingDataInfo.child("time").getValue() != null ? waitingDataInfo.child("time").getValue(String.class) : "";
                                String closureDate = waitingDataInfo.child("date").getValue() != null ? waitingDataInfo.child("date").getValue(String.class) : "";
                                if (currentuserMailId.equals("labsolutions.ic.app@gmail.com") || currentuserMailId.equals("service@labsolutions-ic.in")) {
                                    if (snapshot.child("declined-data") != null) {
                                        for (DataSnapshot dataSnapshot : snapshot.child("declined-data").getChildren()) {
                                            activityInfoList.add(new ActivityInfo(dataSnapshot.getValue(String.class) + " declined the registered call", snapshot.child("engineer-approved-info").child("date").getValue(String.class) + " " + snapshot.child("engineer-approved-info").child("time").getValue(String.class), "", ""));
                                        }
                                    }
                                }
                                activityInfoList.add(new ActivityInfo("Admin will reassign an Engineer once spares are available.", closureDate + " " + closureTime, "", ""));
                            }
                            ListActivitiesinfo listActivitiesinfo = new ListActivitiesinfo(ActivityDetails.this, R.layout.row_activities_details_item, activityInfoList);
                            listview.setAdapter(listActivitiesinfo);
                            if ((status.equals("Approved by admin") || status.equals("Scheduled") || status.equals("Rescheduled")) && currentuserMailId.equals("service@labsolutions-ic.in")) {
                                resceduleRelativeLayout.setVisibility(View.VISIBLE);
                            }
                            final ValueEventListener customerValueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            customerId = ds.getKey().toString();
                                            customerTokenId = ds.child("token").child("token").getValue() != null
                                                    ? ds.child("token").child("token").getValue(String.class) : "";
                                        }
                                    }
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.hasChildren()) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    enginnerId = ds.getKey().toString();
                                                    enginnerTokenid = ds.child("token").child("token").getValue() != null
                                                            ? ds.child("token").child("token").getValue(String.class) : "";
                                                }
                                            }
                                            ValueEventListener adminValueEventListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChildren()) {
                                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                                            adminTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            adminDatabaseReference.addValueEventListener(adminValueEventListener);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    };
                                    customerDatabaseReferenceUser.orderByChild("mailId").equalTo(engineerMailId).addValueEventListener(valueEventListener);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            customerDatabaseReferenceUser.orderByChild("mailId").equalTo(customerMailId).addValueEventListener(customerValueEventListener);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    currentActivityDs.addListenerForSingleValueEvent(eventListener);

                    reschedule.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                Intent intent = new Intent(ActivityDetails.this, RescheduleCall.class);
                                intent.putExtra("activityId", activityId);
                                intent.putExtra("adminToken", adminTokenId);
                                intent.putExtra("engineerTokenId", enginnerTokenid);
                                intent.putExtra("customerTokenId", customerTokenId);
                                intent.putExtra("instrumentIdValue", instrumentId);
                                startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    sendMail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                createPdf("mail");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public void setClosureData(DataSnapshot snapshot, String status) {
        try {
            String waitingText = "Waiting duration: 0hrs 0mins ";
            DataSnapshot closureInfo = snapshot.child("closure-info");
            closureTime = closureInfo.child("time").getValue() != null ? closureInfo.child("time").getValue(String.class) : "";
            closureDate = closureInfo.child("date").getValue() != null ? closureInfo.child("date").getValue(String.class) : "";
            final String engineerName = snapshot.child("engineer-info").child("user").getValue() != null ? snapshot.child("engineer-info").child("user").getValue(String.class) : "";
            durationHours = snapshot.child("duration").child("hours").getValue() != null
                    ? snapshot.child("duration").child("hours").getValue(String.class) : "";
            durationMinutes = snapshot.child("duration").child("minutes").getValue() != null
                    ? snapshot.child("duration").child("minutes").getValue(String.class) : "";
            resolutionDescription = snapshot.child("resolved-info").child("resolution-description").getValue() != null
                    ? snapshot.child("resolved-info").child("resolution-description").getValue(String.class) : "";
            DataSnapshot spares = snapshot.child("resolved-info").child("spares");
            activityInfoList.add(new ActivityInfo(engineerName + " resolved the call", closureDate + " " + closureTime, "", ""));

            DataSnapshot waitingDataEndInfo = snapshot.child("waiting-data").child("end-data");
            if (waitingDataEndInfo.getValue() != null) {
                DataSnapshot waitingDataInfo = snapshot.child("waiting-data").child("start-data");
                long startTime = (long) (waitingDataInfo.child("timeStamp").getValue() != null ? waitingDataInfo.child("timeStamp").getValue() : 0l);
                long closureTime = (long) (waitingDataEndInfo.child("timeStamp").getValue() != null ? waitingDataEndInfo.child("timeStamp").getValue() : 0l);
                Map<String, String> data = Commons.getDuration(startTime, closureTime);
                waitingText = "Waiting duration: " + data.get("hours") + "hrs " + data.get("minutes") + "mins";
            }
            if (spares.getValue() != null) {
                spareQtyOne = spares.child("sprOneQty").getValue() != null ? spares.child("sprOneQty").getValue(String.class) : "";
                spareQtyTwo = spares.child("sprTwoQty").getValue() != null ? spares.child("sprTwoQty").getValue(String.class) : "";
                spareQtyThree = spares.child("sprThreeQty").getValue() != null ? spares.child("sprThreeQty").getValue(String.class) : "";
                spareQtyFour = spares.child("sprFourQty").getValue() != null ? spares.child("sprFourQty").getValue(String.class) : "";
                spareDescOne = spares.child("sprOneDesc").getValue() != null ? spares.child("sprOneDesc").getValue(String.class) : "";
                spareDescTwo = spares.child("sprTwoDesc").getValue() != null ? spares.child("sprTwoDesc").getValue(String.class) : "";
                spareDescThree = spares.child("sprThreeDesc").getValue() != null ? spares.child("sprThreeDesc").getValue(String.class) : "";
                spareDescFour = spares.child("sprFourDesc").getValue() != null ? spares.child("sprFourDesc").getValue(String.class) : "";
                activityInfoList.add(new ActivityInfo("Duration of the call: " + durationHours + "hrs " + durationMinutes + "mins\n\n" + waitingText + "\n\nResolution Description : " + resolutionDescription + "\n\nSpares used:\n\n"
                        + spareDescOne + "  " + spareQtyOne + "\n" + spareDescTwo + "  " + spareQtyTwo + "\n"
                        + spareDescThree + "  " + spareQtyThree + "\n" + spareDescFour + "  "
                        + spareQtyFour + "\n", "", "", ""));
            } else {
                activityInfoList.add(new ActivityInfo("Duration of the call: " + durationHours + "hrs " + durationMinutes + "mins\n\n" + waitingText + "\n\nResolution Description : " + resolutionDescription, "", "", ""));
            }
            if (currentuserMailId.equals("labsolutions.ic.app@gmail.com") || currentuserMailId.equals("service@labsolutions-ic.in")) {
                relativeLayout.setVisibility(View.VISIBLE);
                reassign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            new AlertDialog.Builder(ActivityDetails.this)
                                    .setTitle("Labsolutions")
                                    .setMessage("Do you want to reassign to engineer to edit report?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog = ProgressDialog.show(ActivityDetails.this, "Please wait", "Reopening activity...", true, false);
                                            FirebaseDatabase.getInstance().getReference()
                                                    .child("activity-users").child("current-activity").child(activityId).child("customer").setValue(customerId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        FirebaseDatabase.getInstance().getReference()
                                                                .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(enginnerId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    FirebaseDatabase.getInstance().getReference().child("activities").child(activityId).child("status").setValue("Editing Service Report").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Commons.dismissProgressDialog(progressDialog);
                                                                            if (task.isSuccessful()) {
                                                                                Toast.makeText(ActivityDetails.this, "Reassigned to " + engineerName + " edit service report", Toast.LENGTH_SHORT).show();
                                                                                SendNotification.notify(enginnerTokenid, "Labsolutions", "Admin requested you to edit the service report", apiService, "engineerAssignActivity");
                                                                                SendNotification.notify(customerTokenId, "Labsolutions", engineerName + " has been reassigned to edit service report", apiService, "customerCurrentActivity");
                                                                                SendNotification.notify(adminTokenId, "Labsolutions", engineerName + " has been reassigned to edit service report by work admin", apiService, "adminAllActivities");


                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }).setNegativeButton(android.R.string.no, null)
                                    .setIcon(R.drawable.alert)
                                    .show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                export.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (ContextCompat.checkSelfPermission(ActivityDetails.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                createPdf("download");
                            } else {
                                ActivityCompat.requestPermissions(ActivityDetails.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {

        }
    }

    private void createPdf(String type) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if (type.equals("download")) {
                File file = new File(Environment.getExternalStorageDirectory(), "/service-report.pdf");
                PdfWriter.getInstance(document, new FileOutputStream(file));
            } else if (type.equals("mail")) {
                PdfWriter.getInstance(document, outputStream);
            }
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.labsolutionslogo3);
            document.open();
            float[] pointColumnWidths = {150f, 150f};
            PdfPTable table = new PdfPTable(pointColumnWidths);
            Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
            table.setWidthPercentage(100f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream1);
            Image bitmapImage = Image.getInstance(stream1.toByteArray());
            PdfPCell imageCell = new PdfPCell();
            PdfPCell headingCell = new PdfPCell(new Phrase("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t Field Service Report", boldFont));
            headingCell.setColspan(2);
            imageCell.addElement(bitmapImage);
            table.addCell(imageCell);
            table.addCell("\t\t\tLabsolutions Instruments & Consultancy\t\t\t\n" +
                    "\t\t\t#395, 6th Main Road, KS Town, Bangalore-60,\t\t\t\n" +
                    "\t\t\tKarnataka, INDIA.Phone:08028482001\t\t\t\n" +
                    "\t\t\tE-mail. service@labsolutions-ic.in\t\t\t");
            table.addCell(headingCell);
            table.addCell("Company Name :");
            table.addCell(customerCompany);
            table.addCell("Company Address :");
            table.addCell(customerCompanyAddress);
            table.addCell("User Name :");
            table.addCell(customerName);
            table.addCell("Department/Lab :");
            table.addCell(customerDepartment);
            table.addCell("Instrument Id :");
            table.addCell(instrumentId);
            table.addCell("Call Reported Date & Time :");
            table.addCell(date + " " + time);
            table.addCell("Call Scheduled Date & Time :");
            table.addCell(scheduledDate + " " + scheduledTime);
            table.addCell("Call Attended Date & Time :");
            table.addCell(scheduledDate + " " + scheduledTime);
            table.addCell("Call Completed Date & Time :");
            table.addCell(closureDate + " " + closureTime);
            table.addCell("Instrument Down Time :");
            table.addCell(durationHours + " hrs, " + durationMinutes + " mins");
            table.addCell("Problem Reported : ");
            table.addCell(problemDescription);
            table.addCell("Resolution Description : ");
            table.addCell(resolutionDescription);
            PdfPCell sparesCell = new PdfPCell(new Phrase("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tSpares Used", boldFont));
            sparesCell.setColspan(2);
            table.addCell(sparesCell);
            float[] sparesColumnWidths = {150f, 150f, 150f};
            PdfPTable sparesTable = new PdfPTable(sparesColumnWidths);
            sparesTable.addCell("SL NO:");
            sparesTable.addCell("Description:");
            sparesTable.addCell("QTY:");
            sparesTable.addCell("1");
            sparesTable.addCell(spareDescOne);
            sparesTable.addCell(spareQtyOne);
            sparesTable.addCell("2");
            sparesTable.addCell(spareDescTwo);
            sparesTable.addCell(spareQtyTwo);
            sparesTable.addCell("3");
            sparesTable.addCell(spareDescThree);
            sparesTable.addCell(spareQtyThree);
            sparesTable.addCell("4");
            sparesTable.addCell(spareDescFour);
            sparesTable.addCell(spareQtyFour);
            PdfPCell commentCell = new PdfPCell(new Phrase("Comment:"));
            commentCell.setColspan(3);
            commentCell.setMinimumHeight(100);
            sparesTable.addCell(commentCell);
            PdfPCell customerCell = new PdfPCell(new Phrase("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tCustomer Details", boldFont));
            customerCell.setColspan(3);
            sparesTable.addCell(customerCell);
            sparesTable.addCell("Call Registered By");
            sparesTable.addCell(customerName);
            sparesTable.addCell("");
            PdfPCell engineerCell = new PdfPCell(new Phrase("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tEnginner Details", boldFont));
            engineerCell.setColspan(3);
            sparesTable.addCell(engineerCell);
            sparesTable.addCell("Call Completed By");
            sparesTable.addCell(engineerName);
            sparesTable.addCell("");
            sparesTable.setWidthPercentage(100);
            document.add(table);
            document.add(sparesTable);
            document.close();
            if (type.equals("download")) {
                Toast.makeText(ActivityDetails.this, "Successfully downloaded service report", Toast.LENGTH_SHORT).show();
            } else {
                byte[] bytes = outputStream.toByteArray();
                MailUtility.sendMail(customerMailId, "Labsolutions Service Report", MESSAGE_BODY, bytes);
                Toast.makeText(ActivityDetails.this, "Successfully sent service report to " + customerName, Toast.LENGTH_SHORT).show();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    public void setEngineerData(DataSnapshot snapshot, DataSnapshot engineerActivityInfo) {
        try {
            DataSnapshot approvedInfo = snapshot.child("admin-approved-info");
            activityInfoList.add(new ActivityInfo("Admin approved the registered call", approvedInfo.child("date").getValue(String.class) + " " + approvedInfo.child("time").getValue(String.class), "", ""));
            if (currentuserMailId.equals("labsolutions.ic.app@gmail.com") || currentuserMailId.equals("service@labsolutions-ic.in")) {
                if (snapshot.child("declined-data") != null) {
                    for (DataSnapshot dataSnapshot : snapshot.child("declined-data").getChildren()) {
                        activityInfoList.add(new ActivityInfo(dataSnapshot.getValue(String.class) + " declined the registered call", snapshot.child("engineer-approved-info").child("date").getValue(String.class) + " " + snapshot.child("engineer-approved-info").child("time").getValue(String.class), "", ""));
                    }
                }
            }
            DataSnapshot engineerApprovedInfo = snapshot.child("engineer-approved-info");
            DataSnapshot engineerInfo = snapshot.child("engineer-info");
            approvedTime = engineerApprovedInfo.child("time").getValue() != null ? engineerApprovedInfo.child("time").getValue(String.class) : "";
            approvedDate = engineerApprovedInfo.child("date").getValue() != null ? engineerApprovedInfo.child("date").getValue(String.class) : "";
            engineerName = engineerInfo.child("user").getValue() != null ? engineerInfo.child("user").getValue(String.class) : "";
            String engineerPhoneNumber = engineerActivityInfo.child("phoneNumber").getValue() != null ? engineerActivityInfo.child("phoneNumber").getValue(String.class) : "";
            engineerMailId = engineerActivityInfo.child("mailId").getValue() != null ? engineerActivityInfo.child("mailId").getValue(String.class) : "";
            activityInfoList.add(new ActivityInfo("Call is Scheduled! \n" + engineerName + " will be working on it", approvedDate + " " + approvedTime, engineerPhoneNumber, engineerMailId));
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}