package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ManagerHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private Button btnAddStudent, btnActiveProblems, btnRaiseProblem, btnFixedProblems;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        tvWelcomeMessage = findViewById(R.id.tvManagerWelcomeMessage);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        btnActiveProblems = findViewById(R.id.btnManagerActiveProblems);
        btnRaiseProblem = findViewById(R.id.btnManagerRaiseProblem);
        btnFixedProblems = findViewById(R.id.btnManagerFixedProblems);

        Intent intent = getIntent();
        currentUserName = intent.getStringExtra("USER_NAME");

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
                // במקרה של מנהל, אפשר להעביר פרטים כלליים או לקבל אותם מה-Intent המקורי
                intent.putExtra("USER_NAME", currentUserName);
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
    }
}