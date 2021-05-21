package com.application.labsolutions.admin;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class ExportToExcel extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Button export;
    private ProgressDialog progressDialog;
    EditText fromDate, toDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_export_to_excel);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Export Service Reports");
            setSupportActionBar(toolbar);
            export = findViewById(R.id.exportReport);
            fromDate = findViewById(R.id.startDate);
            toDate = findViewById(R.id.endDate);
            fromDate.setKeyListener(null);
            toDate.setKeyListener(null);
            firebaseAuth = FirebaseAuth.getInstance();
            fromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ExportToExcel.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(0);
                                    cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                    Date chosenDate = cal.getTime();
                                    DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                                    String df_medium_us_str = df_medium_us.format(chosenDate);
                                    fromDate.setText(df_medium_us_str);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
            toDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(ExportToExcel.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(0);
                                    cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                                    Date chosenDate = cal.getTime();
                                    DateFormat df_medium_us = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
                                    String df_medium_us_str = df_medium_us.format(chosenDate);
                                    toDate.setText(df_medium_us_str);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
            export.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!fromDate.getText().toString().equals("") && !toDate.getText().toString().equals("")) {
                            if (ContextCompat.checkSelfPermission(ExportToExcel.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                progressDialog = ProgressDialog.show(ExportToExcel.this, "Please wait", "Downloading..", true, false);
                                exportToExcel();
                            } else {
                                ActivityCompat.requestPermissions(ExportToExcel.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                            }
                        } else {
                            Toast.makeText(ExportToExcel.this, "Please choose date range to export", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    private void exportToExcel() {
        try {
            String Fnamexls = "/service-report" + ".xlsx";
            File sdCard = Environment.getExternalStorageDirectory();
            File file = new File(sdCard + Fnamexls);
            final FileOutputStream out = new FileOutputStream(file);
            final XSSFWorkbook workbook = new XSSFWorkbook();
            final XSSFSheet spreadsheet = workbook.createSheet(" report_info ");
            final XSSFRow[] row = new XSSFRow[1];
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference activities = rootRef.child("activities");
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        Map<String, Object[]> activitiesInfo = new TreeMap<String, Object[]>();
                        int index = 0;
                        activitiesInfo.put(String.valueOf(index), new Object[]{
                                "Company Name", "User Name", "Department/Lab", "Instrument Id", "Call Reported Date & Time"
                                , "Call Scheduled Date & Time", "Call Attended Date & Time", "Call Completed Date & Time", "Instrument Down Time",
                                "Problem Reported", "problemDescription", "Spares Used", "Engineer Name", "Status"});
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            index++;
                            DataSnapshot currentActivityInfo = snapshot.child("customer-info");
                            DataSnapshot engineerActivityInfo = snapshot.child("engineer-info");
                            DataSnapshot activityInfo = snapshot.child("activity-info");
                            DataSnapshot startInfo = snapshot.child("start-info");
                            DataSnapshot scheduledInfo = snapshot.child("scheduled-info");
                            DataSnapshot closureInfo = snapshot.child("closure-info");
                            DataSnapshot spares = snapshot.child("resolved-info").child("spares");
                            DataSnapshot engineerInfo = snapshot.child("engineer-info");
                            String status = snapshot.child("status").getValue() != null ? snapshot.child("status").getValue(String.class) : "";
                            String date = startInfo.child("date").getValue() != null
                                    ? startInfo.child("date").getValue(String.class) : "";
                            String selectedStartTime = fromDate.getText().toString() + " 00:00:00";
                            String selectedEndTime = toDate.getText().toString() + " 00:00:00";
                            if (Long.parseLong(DateUtility.getTimeStamp(date + " 00:00:00")) >= Long.parseLong(DateUtility.getTimeStamp(selectedStartTime)) &&
                                    Long.parseLong(DateUtility.getTimeStamp(date + " 00:00:00")) <= Long.parseLong(DateUtility.getTimeStamp(selectedEndTime)
                                    )) {
                                String companyName = currentActivityInfo.child("companyName").getValue() != null ? currentActivityInfo.child("companyName").getValue(String.class) : "";
                                String instrumentId = activityInfo.child("instrumentId").getValue() != null
                                        ? activityInfo.child("instrumentId").getValue(String.class) : "";
                                String problemDescription = activityInfo.child("problemDescription").getValue() != null
                                        ? activityInfo.child("problemDescription").getValue(String.class) : "";
                                String customerName = currentActivityInfo.child("user").getValue() != null
                                        ? currentActivityInfo.child("user").getValue(String.class) : "";
                                String customerDepartment = currentActivityInfo.child("department").getValue() != null
                                        ? currentActivityInfo.child("department").getValue(String.class) : "";
                                String time = startInfo.child("time").getValue() != null
                                        ? startInfo.child("time").getValue(String.class) : "";
                                String scheduledDate = scheduledInfo.child("date") != null
                                        ? scheduledInfo.child("date").getValue(String.class) : "";
                                String scheduledTime = scheduledInfo.child("time") != null
                                        ? scheduledInfo.child("time").getValue(String.class) : "";
                                String engineerName = engineerActivityInfo.child("user").getValue() != null ? engineerInfo.child("user").getValue(String.class) : "";
                                String closureTime = closureInfo.child("time").getValue() != null ? closureInfo.child("time").getValue(String.class) : "";
                                String closureDate = closureInfo.child("date").getValue() != null ? closureInfo.child("date").getValue(String.class) : "";
                                String resolutionDescription = snapshot.child("resolved-info").child("resolution-description").getValue() != null
                                        ? snapshot.child("resolved-info").child("resolution-description").getValue(String.class) : "";
                                String spareQtyOne = spares.child("sprOneQty").getValue() != null ? spares.child("sprOneQty").getValue(String.class) : "";
                                String spareQtyTwo = spares.child("sprTwoQty").getValue() != null ? spares.child("sprTwoQty").getValue(String.class) : "";
                                String spareQtyThree = spares.child("sprThreeQty").getValue() != null ? spares.child("sprThreeQty").getValue(String.class) : "";
                                String spareQtyFour = spares.child("sprFourQty").getValue() != null ? spares.child("sprFourQty").getValue(String.class) : "";
                                String spareDescOne = spares.child("sprOneDesc").getValue() != null ? spares.child("sprOneDesc").getValue(String.class) : "";
                                String spareDescTwo = spares.child("sprTwoDesc").getValue() != null ? spares.child("sprTwoDesc").getValue(String.class) : "";
                                String spareDescThree = spares.child("sprThreeDesc").getValue() != null ? spares.child("sprThreeDesc").getValue(String.class) : "";
                                String spareDescFour = spares.child("sprFourDesc").getValue() != null ? spares.child("sprFourDesc").getValue(String.class) : "";
                                String durationHours = snapshot.child("duration").child("hours").getValue() != null
                                        ? snapshot.child("duration").child("hours").getValue(String.class) : "0";
                                String durationMinutes = snapshot.child("duration").child("minutes").getValue() != null ? snapshot.child("duration").child("minutes").getValue(String.class) : "0";

                                activitiesInfo.put(String.valueOf(index), new Object[]{
                                        companyName, customerName, customerDepartment, instrumentId, date + "  " + time, scheduledDate + "  " + scheduledTime
                                        , scheduledDate + "  " + scheduledTime, closureDate + "  " + closureTime, durationHours + "hrs " + durationMinutes + "min", problemDescription, resolutionDescription, spareDescOne + "  " + spareQtyOne + "\n" + spareDescTwo + "  " + spareQtyTwo + "\n"
                                        + spareDescThree + "  " + spareQtyThree + "\n" + spareDescFour + "  " + spareQtyFour + "\n", engineerName, status});
                            }
                        }


                        Set<String> keyid = activitiesInfo.keySet();
                        int rowid = 0;
                        for (String key : keyid) {
                            row[0] = spreadsheet.createRow(rowid++);
                            Object[] objectArr = activitiesInfo.get(key);
                            int cellid = 0;
                            for (Object obj : objectArr) {
                                Cell cell = row[0].createCell(cellid++);
                                cell.setCellValue((String) obj);
                            }
                        }
                        try {
                            workbook.write(out);
                            Commons.dismissProgressDialog(progressDialog);
                            Toast.makeText(ExportToExcel.this, "Successfully downloaded service reportl", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            Commons.dismissProgressDialog(progressDialog);
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            Commons.dismissProgressDialog(progressDialog);
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            activities.addValueEventListener(valueEventListener);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                Intent intentAdminActivity = new Intent(ExportToExcel.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(ExportToExcel.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(ExportToExcel.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(ExportToExcel.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(ExportToExcel.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(ExportToExcel.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentUpdateLeaves = new Intent(ExportToExcel.this, UpdateLeaves.class);
                finishAffinity();
                startActivity(intentUpdateLeaves);
                return true;
            case 8:
                Intent intentLeaves = new Intent(ExportToExcel.this, AllUpcomingLeaves.class);
                finishAffinity();
                startActivity(intentLeaves);
                return true;
            case 9:
                Intent intentExport = new Intent(ExportToExcel.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 10:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(ExportToExcel.this, LoginActivity.class);
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
