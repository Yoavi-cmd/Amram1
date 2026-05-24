package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ManagerHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private Button btnAddStudent, btnActiveProblems, btnRaiseProblem, btnFixedProblems, btnManageUsers, btnLogout;
    private String currentUserName;
    private String currentUserYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        tvWelcomeMessage = findViewById(R.id.tvManagerWelcomeMessage);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnActiveProblems = findViewById(R.id.btnManagerActiveProblems);
        btnRaiseProblem = findViewById(R.id.btnManagerRaiseProblem);
        btnFixedProblems = findViewById(R.id.btnManagerFixedProblems);
        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnLogout = findViewById(R.id.btnLogout);

        Intent intent = getIntent();
        currentUserName = intent.getStringExtra("USER_NAME");
        currentUserYear = intent.getStringExtra("USER_YEAR");

        if (currentUserName != null) {
            tvWelcomeMessage.setText("שלום מנהל, " + currentUserName);
        } else {
            tvWelcomeMessage.setText("שלום מנהל!");
        }

        // כפתור הוספת תלמיד
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(ManagerHomeActivity.this, AddStudentActivity.class);
                startActivity(addIntent);
            }
        });

        // כפתור תקלות פעילות
        btnActiveProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, ActiveProblemsActivity.class);
                startActivity(intent);
            }
        });

        // כפתור העלאת תקלה
        btnRaiseProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, ReportProblemActivity.class);
                intent.putExtra("USER_NAME", currentUserName);
                intent.putExtra("USER_YEAR", currentUserYear);
                startActivity(intent);
            }
        });

        // כפתור תקלות שתוקנו
        btnFixedProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, FixedProblemsActivity.class);
                startActivity(intent);
            }
        });

        // כפתור ניהול משתמשים — מעבר למסך צפייה ומחיקה של משתמשים
        btnManageUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerHomeActivity.this, ManageUsersActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(ManagerHomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}