package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GuideHomeActivity extends AppCompatActivity {

    private TextView tvGuideWelcome;
    private Button btnActiveProblems, btnRaiseProblem, btnFixedProblems, btnLogout;
    private String currentUserName;
    private String currentUserYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_home);

        tvGuideWelcome = findViewById(R.id.tvGuideWelcome);
        btnActiveProblems = findViewById(R.id.btnGuideActiveProblems);
        btnRaiseProblem = findViewById(R.id.btnGuideRaiseProblem);
        btnFixedProblems = findViewById(R.id.btnGuideFixedProblems);
        btnLogout = findViewById(R.id.btnLogout);

        Intent intent = getIntent();
        currentUserName = intent.getStringExtra("USER_NAME");
        currentUserYear = intent.getStringExtra("USER_YEAR");

        if (currentUserName != null) {
            tvGuideWelcome.setText("שלום מדריך, " + currentUserName);
        }

        btnActiveProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideHomeActivity.this, ActiveProblemsActivity.class);
                intent.putExtra("FILTER_YEAR", currentUserYear);
                startActivity(intent);
            }
        });

        btnRaiseProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideHomeActivity.this, ReportProblemActivity.class);
                intent.putExtra("USER_NAME", currentUserName);
                intent.putExtra("USER_YEAR", currentUserYear);
                startActivity(intent);
            }
        });

        btnFixedProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideHomeActivity.this, FixedProblemsActivity.class);
                intent.putExtra("FILTER_YEAR", currentUserYear);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(GuideHomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
