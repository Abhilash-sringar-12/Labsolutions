package com.example.labsolutions.engineer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.labsolutions.R;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.customer.ActivityDetails;
import com.example.labsolutions.dateutils.DateUtility;
import com.example.labsolutions.mailutils.MailUtility;
import com.example.labsolutions.pojos.DateInfo;
import com.example.labsolutions.services.ApiService;
import com.example.labsolutions.services.Client;
import com.example.labsolutions.services.SendNotification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.example.labsolutions.commons.Commons.getDuration;

public class ResolveActivity extends AppCompatActivity {
    Button send;
    TextInputLayout resolutionDescriptionField;
    TextView sprOneQty, sprTwoQty, sprThreeQty, sprFourQty, sprOneDesc, sprTwoDesc, sprThreeDesc, sprFourDesc, callDetails;
    RadioGroup radioGroup;
    RadioButton radioButton;
    ApiService apiService;
    String customerName;
    String customerPhone;
    String customerMail;
    String customerDepartment;
    String customerCompany, customerCompanyAddress;
    String instrumentId;
    String problemDescription;
    String activityStartTime;
    String activityStartDate;
    String engineerName;
    String engineerStartTime;
    String engineerStartDate;
    String resolutionDescription;
    String activityId;
    long startDate;
    Map<String, String> endTimeStamp;
    String hoursSpent;
    String minutesSpent;
    String closureDate;
    String closureTime;
    String workAdminMailId;
    String workAdminTokenId;
    String customerId;
    String customerMailId;
    String customerTokenId;
    String adminTokenId;
    String scheduledTime;
    String scheduledDate;
    String scheduledTimeStamp;
    FirebaseAuth firebaseAuth;
    String workAdminId;
    long waitingStartTime = 0;
    long waitingEndTime = 0;
    long engineerAceeptedTime;
    ProgressDialog progressDialog;
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
            setContentView(R.layout.activity_approve_activity);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Resolve Call Activity");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            firebaseAuth = FirebaseAuth.getInstance();
            radioGroup = (RadioGroup) findViewById(R.id.closureType);
            activityId = intent.getStringExtra("activityId");
            sprOneQty = findViewById(R.id.spareOneQty);
            sprTwoQty = findViewById(R.id.spareTwoQty);
            sprThreeQty = findViewById(R.id.spareThreeQty);
            sprFourQty = findViewById(R.id.spareFourQty);
            sprOneDesc = findViewById(R.id.spareOneDesc);
            sprTwoDesc = findViewById(R.id.spareTwoDesc);
            sprThreeDesc = findViewById(R.id.spareThreeDesc);
            sprFourDesc = findViewById(R.id.spareFourDesc);
            callDetails = findViewById(R.id.viewMore);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            callDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callDetails = new Intent(ResolveActivity.this, ActivityDetails.class);
                    callDetails.putExtra("activityId", activityId);
                    startActivity(callDetails);
                }
            });
            if (!activityId.isEmpty()) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference currentActivityDs = rootRef.child("activities").child(activityId);
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("workadmin");
                final DatabaseReference adminDatabaseReference = FirebaseDatabase.getInstance().getReference()
                        .child("admin");
                final DatabaseReference customerDatabaseReferenceUser = FirebaseDatabase.getInstance().getReference()
                        .child("users");
                if (currentActivityDs != null) {
                    ValueEventListener valueEventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            DataSnapshot currentActivityInfo = snapshot.child("customer-info");
                            DataSnapshot engineerActivityInfo = snapshot.child("engineer-info");
                            DataSnapshot engineerStartInfo = snapshot.child("engineer-approved-info");
                            DataSnapshot activityInfo = snapshot.child("activity-info");
                            DataSnapshot startInfo = snapshot.child("start-info");
                            DataSnapshot waitingDetails = snapshot.child("waiting-data");
                            DataSnapshot scheduledInfo = snapshot.child("scheduled-info");
                            customerName = currentActivityInfo.child("user").getValue() != null
                                    ? currentActivityInfo.child("user").getValue(String.class) : "";
                            customerPhone = currentActivityInfo.child("phoneNumber").getValue() != null
                                    ? currentActivityInfo.child("phoneNumber").getValue(String.class) : "";
                            customerMail = currentActivityInfo.child("mailId").getValue() != null
                                    ? currentActivityInfo.child("mailId").getValue(String.class) : "";
                            customerDepartment = currentActivityInfo.child("department").getValue() != null
                                    ? currentActivityInfo.child("department").getValue(String.class) : "";
                            customerCompany = currentActivityInfo.child("companyName").getValue() != null
                                    ? currentActivityInfo.child("companyName").getValue(String.class) : "";
                            customerCompanyAddress = currentActivityInfo.child("companyAddress").getValue() != null
                                    ? currentActivityInfo.child("companyAddress").getValue(String.class) : "";
                            instrumentId = activityInfo.child("instrumentId").getValue(String.class);
                            problemDescription = activityInfo.child("problemDescription").getValue() != null
                                    ? activityInfo.child("problemDescription").getValue(String.class) : "";
                            activityStartTime = startInfo.child("time").getValue() != null ? startInfo.child("time").getValue(String.class) : "";
                            activityStartDate = startInfo.child("date").getValue() != null ? startInfo.child("date").getValue(String.class) : "";
                            engineerName = engineerActivityInfo.child("user").getValue() != null ? engineerActivityInfo.child("user").getValue(String.class) : "";
                            engineerStartTime = engineerStartInfo.child("time").getValue() != null ? engineerStartInfo.child("time").getValue(String.class) : "";
                            engineerStartDate = engineerStartInfo.child("date").getValue() != null ? engineerStartInfo.child("date").getValue(String.class) : "";
                            engineerAceeptedTime = (long) (engineerStartInfo.child("timeStamp").getValue() != null ? engineerStartInfo.child("timeStamp").getValue() : 0l);
                            startDate = (long) (startInfo.child("timeStamp").getValue() != null ? startInfo.child("timeStamp").getValue() : 0l);
                            scheduledDate = scheduledInfo.child("date").getValue() != null ? scheduledInfo.child("date").getValue(String.class) : "";
                            scheduledTime = scheduledInfo.child("time").getValue() != null ? scheduledInfo.child("time").getValue(String.class) : "";
                            scheduledTimeStamp = scheduledInfo.child("timeStamp").getValue() != null ? scheduledInfo.child("timeStamp").getValue(String.class) : "";

                            if (waitingDetails.getValue() != null) {
                                waitingStartTime = (long) (waitingDetails.child("start-data").child("timeStamp").getValue() != null ? waitingDetails.child("start-data").child("timeStamp").getValue() : 0l);
                                if (waitingDetails.child("end-data").getValue() != null)
                                    waitingEndTime = (long) (waitingDetails.child("end-data").child("timeStamp").getValue() != null ? waitingDetails.child("end-data").child("timeStamp").getValue() : 0l);

                            }
                            final ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            workAdminId = ds.getKey();
                                            workAdminTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
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

                                            final ValueEventListener customerValueEventListener = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.hasChildren()) {
                                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                                            customerTokenId = ds.child("token").child("token").getValue() != null ? ds.child("token").child("token").getValue(String.class) : "";
                                                            customerId = ds.getKey().toString();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            customerDatabaseReferenceUser.orderByChild("mailId").equalTo(customerMail).addValueEventListener(customerValueEventListener);
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
                            databaseReference.addValueEventListener(valueEventListener);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    currentActivityDs.addValueEventListener(valueEventListener);
                }
                send = findViewById(R.id.submit);
                send.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        try {
                            int selectedId = radioGroup.getCheckedRadioButtonId();
                            radioButton = (RadioButton) findViewById(selectedId);
                            resolutionDescriptionField = findViewById(R.id.editTextResolutionDescription);
                            resolutionDescription = resolutionDescriptionField.getEditText().getText().toString();
                            if (radioButton == null) {
                                Toast.makeText(ResolveActivity.this, "Please select the resolution type", Toast.LENGTH_SHORT).show();

                            } else if (resolutionDescription.isEmpty()) {
                                Toast.makeText(ResolveActivity.this, "Please type Resolution description", Toast.LENGTH_SHORT).show();

                            } else {
                                final String resolutionType = radioButton.getText().toString();
                                new AlertDialog.Builder(ResolveActivity.this)
                                        .setTitle(resolutionType)
                                        .setMessage("Are you sure?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (resolutionType.equals("Resolved")) {
                                                    progressDialog = ProgressDialog.show(ResolveActivity.this, "Please wait", "Resolving activity.....", true, false);
                                                    if (Long.parseLong(scheduledTimeStamp) <= new Date().getTime()) {
                                                        resolveActivity();
                                                    } else {
                                                        Commons.dismissProgressDialog(progressDialog);
                                                        Toast.makeText(ResolveActivity.this, "You cannot resolve it before the scheduled time", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else if (resolutionType.equals("Drop")) {
                                                    progressDialog = ProgressDialog.show(ResolveActivity.this, "Please wait", "Cancelling  activity....", true, false);
                                                    cancelActivity();
                                                } else if (resolutionType.equals("Waiting")) {
                                                    progressDialog = ProgressDialog.show(ResolveActivity.this, "Please wait", "Notifying Admin....", true, false);
                                                    if (Long.parseLong(scheduledTimeStamp) <= new Date().getTime()) {
                                                        waitingForSpares();
                                                    } else {
                                                        Commons.dismissProgressDialog(progressDialog);
                                                        Toast.makeText(ResolveActivity.this, "You cannot resolve it before the scheduled time", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }).setNegativeButton(android.R.string.no, null)
                                        .setIcon(R.drawable.alert)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitingForSpares() {
        try {
            String startDate = DateUtility.getCurrentDate();
            String startTime = DateUtility.getCurrentTime();
            final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
            FirebaseDatabase.getInstance().getReference()
                    .child("activity-users").child("current-activity").child(activityId).child("work-admin").setValue(workAdminId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference().child("activities").child(activityId).child("waiting-data").child("start-data").setValue(dateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("status").setValue("Waiting for spares").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                        if (task.isSuccessful()) {
                                                            SendNotification.notify(adminTokenId, "Labsolutions", engineerName + " is waiting for spares", apiService, "adminAllActivities");
                                                            SendNotification.notify(workAdminTokenId, "Labsolutions", engineerName + " is waiting for spares", apiService, "workAdminAssignActivity");
                                                            SendNotification.notify(customerTokenId, "Labsolutions", engineerName + " is waiting for spares", apiService, "customerCurrentActivity");
                                                            Toast.makeText(ResolveActivity.this, "Notified Admin", Toast.LENGTH_SHORT).show();
                                                            returnToALLDetailsPage();
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void returnToALLDetailsPage() {
        Intent intent = new Intent(ResolveActivity.this, AssignedActivities.class);
        Commons.dismissProgressDialog(progressDialog);
        startActivity(intent);
        finish();
    }

    private void cancelActivity() {
        try {
            String startDate = DateUtility.getCurrentDate();
            String startTime = DateUtility.getCurrentTime();
            final DateInfo dateInfo = new DateInfo(startDate, startTime, ServerValue.TIMESTAMP);
            FirebaseDatabase.getInstance().getReference()
                    .child("activity-users").child("current-activity").child(activityId).child("work-admin").setValue(workAdminId).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference().child("activities").child(activityId).child("declined-data").child(UUID.randomUUID().toString()).setValue(engineerName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("activities").child(activityId).child("engineer-approved-info").setValue(dateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("activities").child(activityId).child("engineer-approved-info").child("engineer-availability").setValue("unavailable").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FirebaseDatabase.getInstance().getReference()
                                                                                .child("activities").child(activityId).child("engineer-info").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    FirebaseDatabase.getInstance().getReference()
                                                                                            .child("activities").child(activityId).child("status").setValue("Approved by admin").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                        .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                                                        if (task.isSuccessful()) {
                                                                                                            SendNotification.notify(adminTokenId, "Labsolutions", "Engineer unavailable", apiService, "adminAllActivities");
                                                                                                            SendNotification.notify(workAdminTokenId, "Labsolutions", "Engineer unavailable", apiService, "workAdminAssignActivity");
                                                                                                            SendNotification.notify(customerTokenId, "Labsolutions", "Admin will reassign an engineer", apiService, "customerCurrentActivity");
                                                                                                            Commons.dismissProgressDialog(progressDialog);
                                                                                                            Toast.makeText(ResolveActivity.this, "You have dropped the call", Toast.LENGTH_SHORT).show();
                                                                                                            Intent intent = new Intent(ResolveActivity.this, AssignedActivities.class);
                                                                                                            startActivity(intent);
                                                                                                            finishAffinity();
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
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(ResolveActivity.this, "Please try again", Toast.LENGTH_SHORT).show();

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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void resolveActivity() {
        try {
            FirebaseDatabase.getInstance().getReference()
                    .child("activity-users").child("current-activity").child(activityId).child("customer").setValue(null);
            FirebaseDatabase.getInstance().getReference()
                    .child("activity-users").child("current-activity").child(activityId).child("engineer").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isComplete()) {
                        FirebaseDatabase.getInstance().getReference()
                                .child("activity-users").child("completed-activity").child(activityId).child("engineer").setValue(firebaseAuth.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()) {
                                    closureDate = DateUtility.getCurrentDate();
                                    closureTime = DateUtility.getCurrentTime();
                                    endTimeStamp = ServerValue.TIMESTAMP;
                                    DateInfo dateInfo = new DateInfo(closureDate, closureTime, endTimeStamp);
                                    FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("closure-info").setValue(dateInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("status").setValue("Resolved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("duration").setValue(calculateDownTime()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("resolved-info").child("resolution-description").setValue(resolutionDescription);
                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("resolved-info").child("spares").setValue(Commons.setSparesData(sprOneQty, sprOneDesc, sprTwoQty, sprTwoDesc, sprThreeQty, sprThreeDesc, sprFourQty, sprFourDesc)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                        .child("activity-users").child("completed-activity").child(activityId).child("customer").setValue(customerId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("timeStamp").setValue(ServerValue.TIMESTAMP);
                                                                                        if (task.isSuccessful()) {
                                                                                            createPdf();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        });

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
                            }
                        });
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Map<String, String> calculateDownTime() {
        if (waitingStartTime != 0 && waitingEndTime != 0) {
            long waitingTime = waitingStartTime - waitingEndTime;
            long overallTime = Long.parseLong(scheduledTimeStamp) - new Date().getTime();
            Map<String, String> actualDuration = getDuration(overallTime, waitingTime);
            hoursSpent = actualDuration.get("hours");
            minutesSpent = actualDuration.get("minutes");
            return actualDuration;
        } else {

            Map<String, String> durations = getDuration(Long.parseLong(scheduledTimeStamp), new Date().getTime());
            if (!durations.isEmpty()) {
                hoursSpent = durations.get("hours");
                minutesSpent = durations.get("minutes");
                return durations;
            }
        }
        return null;
    }

    private void createPdf() {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Document document = new Document();
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.labsolutionslogo3);
            PdfWriter.getInstance(document, outputStream);
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
            table.addCell(activityStartDate + " " + activityStartTime);
            table.addCell("Call Scheduled Date & Time :");
            table.addCell(scheduledDate + " " + scheduledTime);
            table.addCell("Call Attended Date & Time :");
            table.addCell(scheduledDate + " " + scheduledTime);
            table.addCell("Call Completed Date & Time :");
            table.addCell(closureDate + " " + closureTime);
            table.addCell("Instrument Down Time :");
            table.addCell(hoursSpent + " hrs, " + minutesSpent + " mins");
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
            sparesTable.addCell(sprOneDesc.getText().toString());
            sparesTable.addCell(sprOneQty.getText().toString());
            sparesTable.addCell("2");
            sparesTable.addCell(sprTwoDesc.getText().toString());
            sparesTable.addCell(sprTwoQty.getText().toString());
            sparesTable.addCell("3");
            sparesTable.addCell(sprThreeDesc.getText().toString());
            sparesTable.addCell(sprThreeQty.getText().toString());
            sparesTable.addCell("4");
            sparesTable.addCell(sprFourDesc.getText().toString());
            sparesTable.addCell(sprFourQty.getText().toString());
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
            byte[] bytes = outputStream.toByteArray();
            MailUtility.sendMail(customerMail, "Labsolutions Service Report", MESSAGE_BODY, bytes);
            SendNotification.notify(adminTokenId, "Labsolutions", engineerName + " resolved the call", apiService, "adminAllActivities");
            SendNotification.notify(workAdminTokenId, "Labsolutions", engineerName + " resolved the call", apiService, "workAdminAllActivities");
            SendNotification.notify(customerTokenId, "Labsolutions", engineerName + " resolved the call", apiService, "customerAllActivities");
            Toast.makeText(ResolveActivity.this, "You have successfully resolved the call", Toast.LENGTH_SHORT).show();
            returnToALLDetailsPage();
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
}
