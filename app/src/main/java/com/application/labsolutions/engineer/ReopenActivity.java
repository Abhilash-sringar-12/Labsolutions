package com.application.labsolutions.engineer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.mailutils.MailUtility;
import com.application.labsolutions.services.ApiService;
import com.application.labsolutions.services.Client;
import com.application.labsolutions.services.SendNotification;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReopenActivity extends AppCompatActivity {

    Button send;
    TextInputLayout resolutionDescriptionField;
    TextView sprOneQty, sprTwoQty, sprThreeQty, sprFourQty, sprOneDesc, sprTwoDesc, sprThreeDesc, sprFourDesc;
    RadioGroup radioGroup;
    ApiService apiService;
    String customerName;
    String customerPhone;
    String customerMail;
    String customerDepartment;
    String customerCompany;
    String instrumentId;
    String problemDescription;
    String activityStartTime;
    String activityStartDate;
    String engineerName;
    String engineerStartTime;
    String engineerStartDate;
    String resolutionDescription;
    String activityId;
    String hoursSpent;
    String minutesSpent;
    String closureDate;
    String closureTime;
    String workAdminTokenId;
    String customerId;
    String customerTokenId;
    String adminTokenId;
    String scheduledTime;
    String scheduledDate;
    String scheduledTimeStamp;
    FirebaseAuth firebaseAuth;
    String workAdminId;
    long waitingTime = 0;
    long engineerAceeptedTime;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_approve_activity);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Edit Service Report");
            setSupportActionBar(toolbar);
            Intent intent = getIntent();
            firebaseAuth = FirebaseAuth.getInstance();
            radioGroup = (RadioGroup) findViewById(R.id.closureType);
            activityId = intent.getStringExtra("activityId");
            radioGroup.setVisibility(View.GONE);
            resolutionDescriptionField = findViewById(R.id.editTextResolutionDescription);
            sprOneQty = findViewById(R.id.spareOneQty);
            sprTwoQty = findViewById(R.id.spareTwoQty);
            sprThreeQty = findViewById(R.id.spareThreeQty);
            sprFourQty = findViewById(R.id.spareFourQty);
            sprOneDesc = findViewById(R.id.spareOneDesc);
            sprTwoDesc = findViewById(R.id.spareTwoDesc);
            sprThreeDesc = findViewById(R.id.spareThreeDesc);
            sprFourDesc = findViewById(R.id.spareFourDesc);
            apiService = Client.getClient("https://fcm.googleapis.com/").create(ApiService.class);
            if (!activityId.isEmpty()) {
                progressDialog = ProgressDialog.show(ReopenActivity.this, "Please wait", "Loading  activity....", true, false);
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
                            DataSnapshot sparesInfo = snapshot.child("resolved-info").child("spares");
                            DataSnapshot resolutionDescriptionInfo = snapshot.child("resolved-info").child("resolution-description");
                            DataSnapshot closureInfo = snapshot.child("closure-info");
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
                            instrumentId = activityInfo.child("instrumentId").getValue(String.class);
                            problemDescription = activityInfo.child("problemDescription").getValue() != null
                                    ? activityInfo.child("problemDescription").getValue(String.class) : "";
                            activityStartTime = startInfo.child("time").getValue() != null ? startInfo.child("time").getValue(String.class) : "";
                            activityStartDate = startInfo.child("date").getValue() != null ? startInfo.child("date").getValue(String.class) : "";
                            engineerName = engineerActivityInfo.child("user").getValue() != null ? engineerActivityInfo.child("user").getValue(String.class) : "";
                            engineerStartTime = engineerStartInfo.child("time").getValue() != null ? engineerStartInfo.child("time").getValue(String.class) : "";
                            engineerStartDate = engineerStartInfo.child("date").getValue() != null ? engineerStartInfo.child("date").getValue(String.class) : "";
                            engineerAceeptedTime = (long) (engineerStartInfo.child("timeStamp").getValue() != null ? engineerStartInfo.child("timeStamp").getValue() : 0l);
                            scheduledDate = scheduledInfo.child("date").getValue() != null ? scheduledInfo.child("date").getValue(String.class) : "";
                            scheduledTime = scheduledInfo.child("time").getValue() != null ? scheduledInfo.child("time").getValue(String.class) : "";
                            scheduledTimeStamp = scheduledInfo.child("timeStamp").getValue() != null ? scheduledInfo.child("timeStamp").getValue(String.class) : "";

                            if (waitingDetails.getValue() != null) {
                                waitingTime = (long) (waitingDetails.child("start-data").child("timeStamp").getValue() != null ? waitingDetails.child("start-data").child("timeStamp").getValue() : 0l);
                            }
                            if (sparesInfo.getValue() != null) {
                                sprOneQty.setText(sparesInfo.child("sprOneQty").getValue() != null ? sparesInfo.child("sprOneQty").getValue(String.class) : "");
                                sprTwoQty.setText(sparesInfo.child("sprTwoQty").getValue() != null ? sparesInfo.child("sprTwoQty").getValue(String.class) : "");
                                sprThreeQty.setText(sparesInfo.child("sprThreeQty").getValue() != null ? sparesInfo.child("sprThreeQty").getValue(String.class) : "");
                                sprFourQty.setText(sparesInfo.child("sprFourQty").getValue() != null ? sparesInfo.child("sprFourQty").getValue(String.class) : "");
                                sprOneDesc.setText(sparesInfo.child("sprOneDesc").getValue() != null ? sparesInfo.child("sprOneDesc").getValue(String.class) : "");
                                sprTwoDesc.setText(sparesInfo.child("sprTwoDesc").getValue() != null ? sparesInfo.child("sprTwoDesc").getValue(String.class) : "");
                                sprThreeDesc.setText(sparesInfo.child("sprThreeDesc").getValue() != null ? sparesInfo.child("sprThreeDesc").getValue(String.class) : "");
                                sprFourDesc.setText(sparesInfo.child("sprFourDesc").getValue() != null ? sparesInfo.child("sprFourDesc").getValue(String.class) : "");

                            }
                            if (resolutionDescriptionInfo.getValue() != null) {
                                resolutionDescription = resolutionDescriptionInfo.getValue() != null ? resolutionDescriptionInfo.getValue(String.class) : "";
                                resolutionDescriptionField.getEditText().setText(resolutionDescription);
                            }
                            if (closureInfo.getValue() != null) {
                                closureTime = closureInfo.child("time").getValue() != null ? closureInfo.child("time").getValue(String.class) : "";
                                closureDate = closureInfo.child("date").getValue() != null ? closureInfo.child("date").getValue(String.class) : "";
                                hoursSpent = snapshot.child("duration").child("hours").getValue() != null
                                        ? snapshot.child("duration").child("hours").getValue(String.class) : "";
                                minutesSpent = snapshot.child("duration").child("minutes").getValue() != null
                                        ? snapshot.child("duration").child("minutes").getValue(String.class) : "";
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
                            Commons.dismissProgressDialog(progressDialog);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    currentActivityDs.addValueEventListener(valueEventListener);
                }
                Commons.dismissProgressDialog(progressDialog);
            }
            send = findViewById(R.id.submit);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (resolutionDescription.isEmpty()) {
                            Toast.makeText(ReopenActivity.this, "Please type Resolution description", Toast.LENGTH_SHORT).show();

                        } else {
                            new AlertDialog.Builder(ReopenActivity.this)
                                    .setTitle("Resolve Activity")
                                    .setMessage("Are you sure?")
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog = ProgressDialog.show(ReopenActivity.this, "Please wait", "Resolving  activity....", true, false);
                                            resolutionDescription = resolutionDescriptionField.getEditText().getText().toString();
                                            resolveActivity();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            MailUtility.sendMail(customerMail, "Labsolution Service Report", "check pdf", bytes);
            SendNotification.notify(adminTokenId, "Labsolutions", engineerName + " resolved the reopened call", apiService, "adminAllActivities");
            SendNotification.notify(workAdminTokenId, "Labsolutions", engineerName + " resolved the reopened call", apiService, "workAdminAllActivities");
            SendNotification.notify(customerTokenId, "Labsolutions", engineerName + " resolved the reopened call", apiService, "customerAllActivities");
            Toast.makeText(ReopenActivity.this, "You have successfully resolved the reopened call", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ReopenActivity.this, AssignedActivities.class);
            Commons.dismissProgressDialog(progressDialog);
            startActivity(intent);
            finish();
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
                                    if (task.isSuccessful()) {
                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("resolved-info").child("resolution-description").setValue(resolutionDescription);
                                        FirebaseDatabase.getInstance().getReference("activities").child(activityId).child("resolved-info").child("spares").setValue(Commons.setSparesData(sprOneQty, sprOneDesc, sprTwoQty, sprTwoDesc, sprThreeQty, sprThreeDesc, sprFourQty, sprFourDesc)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseDatabase.getInstance().getReference()
                                                        .child("activities").child(activityId).child("status").setValue("Resolved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
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
                                                    }
                                                });

                                            }
                                        });

                                    }
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
}
