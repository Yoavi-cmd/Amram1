package com.example.amram1; // ודא שזה שם החבילה שלך

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StudentHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private Button btnRaiseProblem;
    private Button btnPermanentTask;
    private Button btnMyProblems;
    private Button btnLogout;

    // פרטי המשתמש שנשמרו מה-LoginActivity
    private String currentUserId;
    private String currentUserName;
    private String currentUserYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage);
        btnRaiseProblem = findViewById(R.id.btnRaiseProblem);
        btnPermanentTask = findViewById(R.id.btnPermanentTask);
        btnMyProblems = findViewById(R.id.btnMyProblems);
        btnLogout = findViewById(R.id.btnLogout);

        // 1. קבלת פרטי המשתמש שנשלחו מדף הכניסה
        Intent intent = getIntent();
        currentUserId = intent.getStringExtra("USER_ID");
        currentUserName = intent.getStringExtra("USER_NAME");
        currentUserYear = intent.getStringExtra("USER_YEAR");

        // 2. הצגת הודעת קבלת פנים מותאמת אישית
        if (currentUserName != null && currentUserYear != null) {
            tvWelcomeMessage.setText("שלום, " + currentUserName + ".\nשכבת " + currentUserYear);
        } else {
            // אם המידע לא הגיע כראוי (מצב חירום)
            tvWelcomeMessage.setText("שלום תלמיד! נא להתחבר מחדש.");
            Toast.makeText(this, "שגיאה בטעינת פרטי משתמש", Toast.LENGTH_LONG).show();
        }

        // 3. הגדרת כפתור "לעלות תקלה"
        btnRaiseProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // מעבר למסך העלאת התקלה
                Intent problemIntent = new Intent(StudentHomeActivity.this, ReportProblemActivity.class);

                // חשוב: נעביר את פרטי התלמיד גם למסך הדיווח
                // כדי שנוכל לשמור את התקלה עם ה-ID והשכבה הנכונים
                problemIntent.putExtra("USER_ID", currentUserId);
                problemIntent.putExtra("USER_YEAR", currentUserYear);
                problemIntent.putExtra("USER_NAME", currentUserName);
                startActivity(problemIntent);
            }
        });

        btnPermanentTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent taskIntent = new Intent(StudentHomeActivity.this, PermanentTasksActivity.class);
                taskIntent.putExtra("USER_ID", currentUserId);
                startActivity(taskIntent);
            }
        });

        btnMyProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(StudentHomeActivity.this, MyProblemsActivity.class);
                myIntent.putExtra("USER_NAME", currentUserName);
                startActivity(myIntent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(StudentHomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}