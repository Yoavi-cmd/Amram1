package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HouseFatherActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnActiveProblems, btnCreatePermanentTask, btnFixedProblems;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_father);
        tvWelcome = findViewById(R.id.tvFatherWelcome);
        btnActiveProblems = findViewById(R.id.btnActiveProblems);
        btnCreatePermanentTask = findViewById(R.id.btnCreatePermanentTask);
        btnFixedProblems = findViewById(R.id.btnFixedProblems);
        currentUserName = getIntent().getStringExtra("USER_NAME");
        if (currentUserName != null) {
            tvWelcome.setText("שלום אב הבית, " + currentUserName);
        }



        btnActiveProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseFatherActivity.this, ActiveProblemsActivity.class);
                startActivity(intent);
            }
        });


        btnCreatePermanentTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseFatherActivity.this, CreateTaskActivity.class);
                startActivity(intent);
            }
        });


        btnFixedProblems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseFatherActivity.this, FixedProblemsActivity.class);
                startActivity(intent);
            }
        });
    }
}