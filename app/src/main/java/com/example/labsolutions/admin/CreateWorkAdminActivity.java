package com.example.labsolutions.admin;

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
import android.widget.Button;
import android.widget.Toast;

import com.example.labsolutions.R;
import com.example.labsolutions.commons.Commons;
import com.example.labsolutions.mailutils.MailUtility;
import com.example.labsolutions.pojos.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CreateWorkAdminActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    TextInputLayout emailId, password, userName;
    FirebaseApp admin;
    Button signUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z-]+\\.+[a-z]+";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_work_admin);
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setTitle("Create Work Admin");
            setSupportActionBar(toolbar);
            admin = FirebaseApp.initializeApp(this);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApiKey("AIzaSyDDozzmjnUAhDwhVmJJisrCk9yFGxobXe4")
                    .setApplicationId("1:421144564713:android:7e74d965040e0b61027178")
                    .setDatabaseUrl("https://labsolutions-8b328.firebaseio.com")
                    .build();
            if (FirebaseApp.getApps(this).size() == 1) {
                admin = FirebaseApp.initializeApp(this, options, "workadmin");
            }
            firebaseAuth = FirebaseAuth.getInstance();
            createWorkAdmin();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createWorkAdmin() {
        try {
            emailId = (TextInputLayout) findViewById(R.id.editTextAdminTextEmailAddress);
            password = (TextInputLayout) findViewById(R.id.editTextAdminTextPassword);
            userName = (TextInputLayout) findViewById(R.id.editTextAdminName);
            signUp = findViewById(R.id.button2);
            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        final String userNameValue = userName.getEditText().getText().toString();
                        final String emailIdValue = emailId.getEditText().getText().toString();
                        final String passwordValue = password.getEditText().getText().toString();
                        if (validateFields(userNameValue, passwordValue, emailIdValue)) {
                            progressDialog = ProgressDialog.show(CreateWorkAdminActivity.this, "Please wait", "Creating work admin....", true, false);
                            final FirebaseAuth secondary = FirebaseAuth.getInstance(FirebaseApp.getInstance("workadmin"));
                            secondary.createUserWithEmailAndPassword(emailIdValue, passwordValue).addOnCompleteListener(CreateWorkAdminActivity.this, new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (!task.isSuccessful()) {
                                        Commons.dismissProgressDialog(progressDialog);
                                        Toast.makeText(CreateWorkAdminActivity.this, "Failed to create User", Toast.LENGTH_SHORT).show();
                                    } else {
                                        UserInfo userInfo = new UserInfo(userNameValue, emailIdValue, null, null, null, null, null);
                                        FirebaseDatabase.getInstance(admin).getReference("workadmin").child(secondary.getCurrentUser().getUid()).setValue(userInfo).addOnCompleteListener(CreateWorkAdminActivity.this, new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                secondary.signOut();
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(CreateWorkAdminActivity.this, "Failed to create work admin", Toast.LENGTH_SHORT).show();
                                                    Commons.dismissProgressDialog(progressDialog);
                                                } else {
                                                    Toast.makeText(CreateWorkAdminActivity.this, "Work admin Successfully created", Toast.LENGTH_SHORT).show();
                                                    Commons.dismissProgressDialog(progressDialog);
                                                    MailUtility.sendMail(emailIdValue, "Login credentials for Labsolutions app", "<head>\n" +
                                                            "<title>Labsolutions</title>\n" +
                                                            "<meta content=\"text/html; charset=utf-8\" http-equiv=\"Content-Type\">\n" +
                                                            "<meta content=\"width=device-width\" name=\"viewport\">\n" +
                                                            "\n" +
                                                            "</head>\n" +
                                                            "<body style=\"background-color: #f4f4f5;\">\n" +
                                                            "<table cellpadding=\"0\" cellspacing=\"0\" style=\"width: 100%; height: 100%; background-color: #f4f4f5; text-align: center;\">\n" +
                                                            "<tbody><tr>\n" +
                                                            "<td style=\"text-align: center;\">\n" +
                                                            "<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" id=\"body\" style=\"background-color: #fff; width: 100%; max-width: 680px; height: 100%;\">\n" +
                                                            "<tbody><tr>\n" +
                                                            "<td>\n" +
                                                            "<table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" class=\"page-center\" style=\"text-align: left; padding-bottom: 88px; width: 100%; padding-left: 120px; padding-right: 120px;\">\n" +
                                                            "<tbody><tr>\n" +
                                                            "<td style=\"padding-top: 24px;\">\n" +
                                                            "<img src=\"http://www.hostgator.co.in/files/writeable/uploads/hostgator166687/image/labsolutionslogo3.png\" style=\"width: auto;\">\n" +
                                                            "</td>\n" +
                                                            "</tr>\n" +
                                                            "<tr>\n" +
                                                            "<td colspan=\"2\" style=\"padding-top: 72px; -ms-text-size-adjust: 100%; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: 100%; color: #000000; font-family: 'Postmates Std', 'Helvetica', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif; font-size: 15px; font-smoothing: always; font-style: normal; font-weight: 600; letter-spacing: -1.6px; line-height: 52px; mso-line-height-rule: exactly; text-decoration: none;\">Thank you for choosing Labsoluntions Instruments & Consultancy pvt ltd</td>\n" +
                                                            "</tr>\n" +
                                                            "<tr>\n" +
                                                            "  <td>Now that you have registered to our App, You will have to keep the below information for future references </td>\n" +
                                                            "  </tr>\n" +
                                                            "<tr>\n" +
                                                            "<td style=\"padding-top: 48px; padding-bottom: 48px;\">\n" +
                                                            "<table cellpadding=\"0\" cellspacing=\"0\" style=\"width: 100%\">\n" +
                                                            "<tbody><tr>\n" +
                                                            "<td style=\"width: 100%; height: 1px; max-height: 1px; background-color: #d9dbe0; opacity: 0.81\"></td>\n" +
                                                            "</tr>\n" +
                                                            "</tbody></table>\n" +
                                                            "</td>\n" +
                                                            "</tr>\n" +
                                                            "<tr>\n" +
                                                            "<tdl̥>\n" +
                                                            " <b>Your UserID:</b>" + emailIdValue + "<br> <b>Password:</b>" + passwordValue + "\n" +
                                                            "                                    </td>\n" +
                                                            "</tr>\n" +
                                                            "</tbody></table>\n" +
                                                            "</td>\n" +
                                                            "</tr>\n" +
                                                            "</tbody></table>\n" +
                                                            "\n" +
                                                            "\n" +
                                                            "\n" +
                                                            "</body>", null);resetForm();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
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
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cloud_download_24), "Export Activities"));
        menu.add(0, 8, 8,
                menuIconWithText(getResources().getDrawable(R.drawable.ic_baseline_cancel_presentation_24), "Sign Out"));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 1:
                Intent intentAdminActivity = new Intent(CreateWorkAdminActivity.this, AdminActivity.class);
                finishAffinity();
                startActivity(intentAdminActivity);
                return true;
            case 2:
                Intent craeteWorkAdmin = new Intent(CreateWorkAdminActivity.this, CreateWorkAdminActivity.class);
                finishAffinity();
                startActivity(craeteWorkAdmin);
                return true;
            case 3:
                Intent intentCreateUserActivity = new Intent(CreateWorkAdminActivity.this, CreateUserActivity.class);
                finishAffinity();
                startActivity(intentCreateUserActivity);
                return true;
            case 4:
                Intent intentAddInstrument = new Intent(CreateWorkAdminActivity.this, AddInstrumentActivity.class);
                finishAffinity();
                startActivity(intentAddInstrument);
                return true;
            case 5:
                Intent intentInstruments = new Intent(CreateWorkAdminActivity.this, InstrumentsActivity.class);
                finishAffinity();
                startActivity(intentInstruments);
                return true;
            case 6:
                Intent intentAllActivities = new Intent(CreateWorkAdminActivity.this, AllActivities.class);
                finishAffinity();
                startActivity(intentAllActivities);
                return true;
            case 7:
                Intent intentExport = new Intent(CreateWorkAdminActivity.this, ExportToExcel.class);
                finishAffinity();
                startActivity(intentExport);
                return true;
            case 8:
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.signOut();
                Intent intentSignOut = new Intent(CreateWorkAdminActivity.this, LoginActivity.class);
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

    private Boolean validateFields(String userNameValue, String passwordValue, String emailIdValue) {
        Boolean isFormValid = true;
        if (userNameValue.isEmpty()) {
            userName.setError("Please enter user name");
            isFormValid = false;
            userName.requestFocus();
        } else {
            userName.setError(null);
        }
        if (passwordValue.isEmpty()) {
            password.setError("Please enter password");
            isFormValid = false;
            password.requestFocus();
        } else {
            password.setError(null);
        }
        if (passwordValue.length() < 6) {
            password.setError("Password should have more than 6 letters");
            isFormValid = false;
            password.requestFocus();
        } else {
            password.setError(null);
        }
        if (emailIdValue.isEmpty()) {
            emailId.setError("Please enter emailid");
            isFormValid = false;
            emailId.requestFocus();
        } else {
            emailId.setError(null);
        }
        if (!emailIdValue.matches(emailPattern)) {
            emailId.setError("Please enter valid emailid");
            isFormValid = false;
            emailId.requestFocus();
        } else {
            emailId.setError(null);
        }

        return isFormValid;
    }

    private void resetForm() {
        userName.getEditText().setText("");
        password.getEditText().setText("");
        emailId.getEditText().setText("");
    }
}