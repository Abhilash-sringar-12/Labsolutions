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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.application.labsolutions.R;
import com.application.labsolutions.commons.Commons;
import com.application.labsolutions.dateutils.DateUtility;
import com.application.labsolutions.mailutils.MailUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
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
    Spinner actionType, monthsSpinner;
    TextView text;
    String[] monthName = {"January", "February",
            "March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};
    String[] days = {"Sunday", "Monday",
            "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    Map<String, String> engineers = new LinkedHashMap<>();
    Map<String, String> leaveBalance = new LinkedHashMap<>();
    List<String> holidays = new ArrayList<>();
    int paCount = 0;
    int hCount = 0;
    int slCount = 0;
    int elCount = 0;
    int hdCount = 0;
    int offCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_export_to_excel);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Export Service Reports");
            setSupportActionBar(toolbar);
            actionType = findViewById(R.id.actionType);
            monthsSpinner = findViewById(R.id.month);
            export = findViewById(R.id.exportReport);
            fromDate = findViewById(R.id.startDate);
            toDate = findViewById(R.id.endDate);
            text = findViewById(R.id.monthText);
            fromDate.setKeyListener(null);
            toDate.setKeyListener(null);
            addActionType();
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
            actionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    try {
                        if (actionType.getSelectedItem() != "") {
                            String action = actionType.getSelectedItem().toString();
                            if (action.equals("Export Call Details")) {
                                text.setVisibility(View.GONE);
                                monthsSpinner.setVisibility(View.GONE);
                                fromDate.setVisibility(View.VISIBLE);
                                toDate.setVisibility(View.VISIBLE);
                                export.setText("Export");
                            } else {
                                progressDialog = ProgressDialog.show(ExportToExcel.this, "Please wait", "Loading Details!!!.....", true, false);
                                DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("users");
                                final DatabaseReference leaves = FirebaseDatabase.getInstance().getReference().child("leaves");
                                final DatabaseReference holidaysDates = FirebaseDatabase.getInstance().getReference().child("holidays");
                                ValueEventListener userValueEvenetListener = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot user : snapshot.getChildren()) {
                                            final String engineerId = user.getKey();
                                            final String engineerName = user.child("user").getValue() != null ? user.child("user").getValue(String.class) : "";
                                            engineers.put(engineerId, engineerName);
                                        }
                                        ValueEventListener leavesValueEventListener = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot leaves : snapshot.getChildren()) {
                                                    leaveBalance.put(leaves.getKey(), leaves.getValue(String.class));
                                                }
                                                ValueEventListener holidaysValueEventListener = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for (DataSnapshot holidaysDs : snapshot.getChildren()) {
                                                            holidays.add(holidaysDs.getKey());
                                                        }
                                                        Commons.dismissProgressDialog(progressDialog);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                };
                                                holidaysDates.addValueEventListener(holidaysValueEventListener);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        };
                                        leaves.addValueEventListener(leavesValueEventListener);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                users.orderByChild("userType").equalTo("Engineer").addValueEventListener(userValueEvenetListener);
                                addMonths();
                                text.setVisibility(View.VISIBLE);
                                monthsSpinner.setVisibility(View.VISIBLE);
                                fromDate.setVisibility(View.GONE);
                                toDate.setVisibility(View.GONE);
                                export.setText("Send Attendance Report");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub
                }
            });
            export.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (actionType.getSelectedItem().equals("Export Call Details")) {
                            executeDownloadCallReport();
                        } else {
                            progressDialog = ProgressDialog.show(ExportToExcel.this, "Please wait", "Sending Attendance Report!!!.....", true, false);
                            final String month = monthsSpinner.getSelectedItem().toString();
                            String year = DateUtility.formatDate(new Date().toString()).split("-")[2];
                            final DatabaseReference attendanceData = FirebaseDatabase.getInstance().getReference().child("calender" + "/" + year + "/" + month);

                            ValueEventListener valueEventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final XSSFWorkbook workbook = new XSSFWorkbook();
                                    final XSSFRow[] row = new XSSFRow[1];
                                    XSSFSheet spreadsheet = null;
                                    CellStyle style = null;
                                    CellStyle orangeStyle = null;
                                    ByteArrayOutputStream bos = null;
                                    Map<String, Object[]> activitiesInfo = null;
                                    for (Map.Entry<String, String> entry : engineers.entrySet()) {
                                        paCount = 0;
                                        slCount = 0;
                                        hCount = 0;
                                        hdCount = 0;
                                        elCount = 0;
                                        offCount = 0;
                                        int index = 10;
                                        bos = new ByteArrayOutputStream();
                                        spreadsheet = workbook.createSheet(entry.getValue());
                                        XSSFFont font = workbook.createFont();
                                        spreadsheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));
                                        spreadsheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 3));
                                        spreadsheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));
                                        spreadsheet.addMergedRegion(new CellRangeAddress(35, 35, 3, 4));
                                        spreadsheet.addMergedRegion(new CellRangeAddress(42, 42, 3, 4));
                                        row[0] = spreadsheet.createRow(0);
                                        Cell cell = row[0].createCell((short) 4);
                                        style = workbook.createCellStyle();
                                        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
                                        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
                                        style.setAlignment(CellStyle.ALIGN_CENTER);
                                        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
                                        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
                                        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
                                        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
                                        orangeStyle = workbook.createCellStyle();
                                        orangeStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
                                        orangeStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
                                        orangeStyle.setAlignment(CellStyle.ALIGN_CENTER);
                                        orangeStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
                                        orangeStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
                                        orangeStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
                                        orangeStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
                                        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
                                        style.setFont(font);
                                        row[0] = spreadsheet.createRow(1);
                                        Cell cellOne = row[0].createCell((short) 0);
                                        Cell cellThree = row[0].createCell((short) 1);
                                        Cell cellFive = row[0].createCell((short) 4);
                                        Cell cellSix = row[0].createCell((short) 5);
                                        row[0] = spreadsheet.createRow(2);
                                        Cell cellTwo = row[0].createCell((short) 0);
                                        Cell cellFour = row[0].createCell((short) 1);
                                        Cell cellEight = row[0].createCell((short) 4);
                                        Cell cellNine = row[0].createCell((short) 5);
                                        cell.setCellValue(month + " Attendance Report");
                                        cellOne.setCellValue("Name:");
                                        cellTwo.setCellValue("Location:");
                                        cellThree.setCellValue(entry.getValue());
                                        cellFour.setCellValue("Bangalore");
                                        cellFive.setCellValue("Attendance Month");
                                        cellSix.setCellValue(month);
                                        cellEight.setCellValue("Office Hours");
                                        cellNine.setCellValue("9pm-6pm");
                                        cell.setCellStyle(style);
                                        cellOne.setCellStyle(style);
                                        cellTwo.setCellStyle(style);
                                        cellFive.setCellStyle(style);
                                        cellEight.setCellStyle(style);
                                        activitiesInfo = new TreeMap<String, Object[]>();
                                        activitiesInfo.put(String.valueOf(0), new Object[]{
                                                "Week Day", "Date", "Login-Time", "Login-Location", "Logout-Time", "Logout-Location", "Total-Hours", "Attendance-Abbreviation", "Remark"});

                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            if (ds != null) {
                                                try {
                                                    String type = "-";
                                                    String loginTime = "-";
                                                    String logoutTime = "-";
                                                    String loginLocation = "-";
                                                    String logoutLocation = "-";
                                                    String duration = "-";
                                                    DataSnapshot snapshot1 = ds.child(entry.getKey());
                                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                    String date = ds.getKey();
                                                    String day = days[simpleDateFormat.parse(date).getDay()];
                                                    if (snapshot1 != null) {
                                                        if (simpleDateFormat.parse(date).getDay() == 0 || simpleDateFormat.parse(date).getDay() == 6) {
                                                            type = "OFF";
                                                        } else if (holidays.contains(date)) {
                                                            type = "H";
                                                        } else if (snapshot1.child("type") != null) {
                                                            type = snapshot1.child("type").getValue(String.class);
                                                        } else {
                                                            type = "-";
                                                        }
                                                        if (snapshot1.child("loginDetails") != null) {
                                                            loginTime = snapshot1.child("loginDetails/loginTime").getValue(String.class);
                                                            loginLocation = snapshot1.child("loginDetails/loginLocation").getValue(String.class);
                                                        }
                                                        if (snapshot1.child("logoutDetails") != null) {
                                                            logoutTime = snapshot1.child("logoutDetails/loginTime").getValue(String.class);
                                                            logoutLocation = snapshot1.child("logoutDetails/loginLocation").getValue(String.class);
                                                        }
                                                        if (loginTime != null && logoutTime != null) {
                                                            Map<String, String> map = Commons.getDuration(Long.parseLong(snapshot1.child("loginDetails/loginTimeStamp").getValue(String.class)),
                                                                    Long.parseLong(snapshot1.child("logoutDetails/loginTimeStamp").getValue(String.class)));
                                                            duration = map.get("hours") + "hrs, " + map.get("minutes") + "mins";

                                                        }
                                                        if (type != null)
                                                            setCount(type);
                                                    }
                                                    activitiesInfo.put(String.valueOf(index++), new Object[]{day, date, loginTime, loginLocation
                                                            , logoutTime, logoutLocation, duration, type, ""});
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        Set<String> keyid = activitiesInfo.keySet();
                                        int rowid = 3;
                                        for (String key : keyid) {
                                            row[0] = spreadsheet.createRow(rowid++);
                                            Object[] objectArr = activitiesInfo.get(key);
                                            int cellid = 0;
                                            for (Object obj : objectArr) {
                                                int count = cellid++;
                                                Cell cells = row[0].createCell(count);
                                                spreadsheet.setColumnWidth(count, 20 * 256);
                                                cells.setCellValue((String) obj);
                                                style.setWrapText(true);
                                                if (rowid == 4) {
                                                    cells.setCellStyle(style);
                                                } else if (rowid != 4 && count == 0) {
                                                    cells.setCellStyle(style);
                                                } else {
                                                    cells.setCellStyle(orangeStyle);
                                                    orangeStyle.setWrapText(true);
                                                }
                                            }
                                        }
                                        row[0] = spreadsheet.createRow(35);
                                        Cell list = row[0].createCell((short) 3);
                                        Cell listCount = row[0].createCell((short) 5);
                                        list.setCellValue("Attedance Abbreviation");
                                        listCount.setCellValue("Count");
                                        list.setCellStyle(style);
                                        listCount.setCellStyle(style);
                                        row[0] = spreadsheet.createRow(36);
                                        Cell paCell = row[0].createCell((short) 3);
                                        Cell paSfCell = row[0].createCell((short) 4);
                                        Cell paCountCell = row[0].createCell((short) 5);

                                        row[0] = spreadsheet.createRow(37);
                                        Cell slCell = row[0].createCell((short) 3);
                                        Cell slSfCell = row[0].createCell((short) 4);
                                        Cell slCountCell = row[0].createCell((short) 5);

                                        row[0] = spreadsheet.createRow(38);
                                        Cell offCell = row[0].createCell((short) 3);
                                        Cell offSfCell = row[0].createCell((short) 4);
                                        Cell offCountCell = row[0].createCell((short) 5);

                                        row[0] = spreadsheet.createRow(39);
                                        Cell hdCell = row[0].createCell((short) 3);
                                        Cell hdSfCell = row[0].createCell((short) 4);
                                        Cell hdCountCell = row[0].createCell((short) 5);

                                        row[0] = spreadsheet.createRow(40);
                                        Cell elCell = row[0].createCell((short) 3);
                                        Cell elSfCell = row[0].createCell((short) 4);
                                        Cell elCountCell = row[0].createCell((short) 5);

                                        row[0] = spreadsheet.createRow(41);
                                        Cell hCell = row[0].createCell((short) 3);
                                        Cell hSfCell = row[0].createCell((short) 4);
                                        Cell hCountCell = row[0].createCell((short) 5);

                                        row[0] = spreadsheet.createRow(42);
                                        Cell totalLeaves = row[0].createCell((short) 3);
                                        Cell totalLeavesCell = row[0].createCell((short) 5);

                                        paCell.setCellValue("Present");
                                        paSfCell.setCellValue("PA");
                                        paCountCell.setCellValue(String.valueOf(paCount));
                                        paCell.setCellStyle(orangeStyle);
                                        paSfCell.setCellStyle(orangeStyle);
                                        paCountCell.setCellStyle(orangeStyle);

                                        slCell.setCellValue("Sick Leave");
                                        slSfCell.setCellValue("SL");
                                        slCountCell.setCellValue(String.valueOf(slCount));
                                        slCell.setCellStyle(orangeStyle);
                                        slSfCell.setCellStyle(orangeStyle);
                                        slCountCell.setCellStyle(orangeStyle);

                                        offCell.setCellValue("Week ends");
                                        offSfCell.setCellValue("OFF");
                                        offCountCell.setCellValue(String.valueOf(offCount));
                                        offCell.setCellStyle(orangeStyle);
                                        offSfCell.setCellStyle(orangeStyle);
                                        offCountCell.setCellStyle(orangeStyle);

                                        hdCell.setCellValue("Half Day");
                                        hdSfCell.setCellValue("HD");
                                        hdCountCell.setCellValue(String.valueOf(hdCount));
                                        hdCell.setCellStyle(orangeStyle);
                                        hdSfCell.setCellStyle(orangeStyle);
                                        hdCountCell.setCellStyle(orangeStyle);

                                        elCell.setCellValue("Earned Leave");
                                        elSfCell.setCellValue("EL");
                                        elCountCell.setCellValue(String.valueOf(elCount));
                                        elCell.setCellStyle(orangeStyle);
                                        elSfCell.setCellStyle(orangeStyle);
                                        elCountCell.setCellStyle(orangeStyle);

                                        hCell.setCellValue("Holiday");
                                        hSfCell.setCellValue("H");
                                        hCountCell.setCellValue(String.valueOf(hCount));
                                        hCell.setCellStyle(orangeStyle);
                                        hSfCell.setCellStyle(orangeStyle);
                                        hCountCell.setCellStyle(orangeStyle);

                                        totalLeaves.setCellValue("Balance Leaves");
                                        totalLeavesCell.setCellValue(leaveBalance.get(entry.getKey()));
                                        totalLeaves.setCellStyle(orangeStyle);
                                        totalLeavesCell.setCellStyle(orangeStyle);
                                    }

                                    try {
                                        workbook.write(bos);
                                        byte[] bytes = bos.toByteArray();
                                        MailUtility.sendExcelMail("ujvalsringar@gmail.com", "Attendance Report", "", bytes);
                                        Commons.dismissProgressDialog(progressDialog);
                                        Toast.makeText(ExportToExcel.this, "Report Successfully sent", Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            attendanceData.addValueEventListener(valueEventListener);
                        }

                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
        }


    }

    private void setCount(String type) {
        if (type.equals("PA")) {
            paCount++;
        } else if (type.equals("OFF")) {
            offCount++;
        } else if (type.equals("HD")) {
            hdCount++;
        } else if (type.equals("SL")) {
            slCount++;
        } else if (type.equals("EL")) {
            elCount++;
        } else if (type.equals("H")) {
            hCount++;
        } else {

        }

    }

    private void executeDownloadCallReport() {
        try {
            if (!fromDate.getText().toString().isEmpty() && !toDate.getText().toString().isEmpty()) {
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

    private void addActionType() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, new String[]{"Export Call Details", "Send Attendance Report"});
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        actionType.setAdapter(dataAdapter);
    }

    private void addMonths() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, monthName);
        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        monthsSpinner.setAdapter(dataAdapter);
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

            final CellStyle style = workbook.createCellStyle();
            final CellStyle orangeStyle = workbook.createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            style.setFillPattern(CellStyle.SOLID_FOREGROUND);
            style.setAlignment(CellStyle.ALIGN_CENTER);
            style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
            style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
            style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
            orangeStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.index);
            orangeStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            orangeStyle.setAlignment(CellStyle.ALIGN_CENTER);
            orangeStyle.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
            orangeStyle.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
            orangeStyle.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
            orangeStyle.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference activities = rootRef.child("activities");
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
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
                                    style.setWrapText(true);
                                    orangeStyle.setWrapText(true);
                                    spreadsheet.setColumnWidth(cellid, 20 * 256);
                                    if (rowid == 1) {
                                        cell.setCellStyle(style);
                                    } else {
                                        cell.setCellStyle(orangeStyle);
                                    }
                                }
                            }
                            try {
                                workbook.write(out);
                                Commons.dismissProgressDialog(progressDialog);
                                Toast.makeText(ExportToExcel.this, "Successfully downloaded service report", Toast.LENGTH_SHORT).show();

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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            activities.orderByChild("timeStamp").addValueEventListener(valueEventListener);

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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_pie_chart_24), "Statistics"));
        menu.add(0, 11, 11,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_dashboard_customize_24), "Dashboard"));
        menu.add(0, 12, 12,
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
                Intent intentDasboard = new Intent(ExportToExcel.this, Statistics.class);
                finishAffinity();
                startActivity(intentDasboard);
                return true;
            case 11:
                Intent intentAdminDashboard = new Intent(ExportToExcel.this, Dashboard.class);
                finishAffinity();
                startActivity(intentAdminDashboard);
                return true;
            case 12:
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
