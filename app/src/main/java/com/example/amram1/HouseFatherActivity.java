package com.example.amram1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class HouseFatherActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private Button btnActiveProblems, btnCreatePermanentTask, btnFixedProblems, btnLogout;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_father);
        tvWelcome = findViewById(R.id.tvFatherWelcome);
        btnActiveProblems = findViewById(R.id.btnActiveProblems);
        btnCreatePermanentTask = findViewById(R.id.btnCreatePermanentTask);
        btnFixedProblems = findViewById(R.id.btnFixedProblems);
        btnLogout = findViewById(R.id.btnLogout);
        currentUserName = getIntent().getStringExtra("USER_NAME");
        if (currentUserName != null) {
            tvWelcome.setText("שלום אב הבית, " + currentUserName);
        }

        checkReminders();
        scheduleReminderAlarm();

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

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(HouseFatherActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void scheduleReminderAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderCheckReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 9);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY,
                pendingIntent);
    }

    private void checkReminders() {
        long todayStart = getTodayStartMillis();

        FirebaseFirestore.getInstance().collection("reminders")
                .whereGreaterThanOrEqualTo("timestamp", todayStart)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    if (count > 0) {
                        StringBuilder message = new StringBuilder();
                        message.append("יש לך ").append(count).append(" תזכורות היום:\n\n");

                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            Reminder r = doc.toObject(Reminder.class);
                            message.append("• ").append(r.getProblemType())
                                    .append(" בחדר ").append(r.getRoomP())
                                    .append(" (מאת ").append(r.getSenderName()).append(")\n");
                        }

                        new AlertDialog.Builder(HouseFatherActivity.this)
                                .setTitle("תזכורות חדשות")
                                .setMessage(message.toString())
                                .setPositiveButton("הבנתי", null)
                                .show();
                    }
                });
    }

    private long getTodayStartMillis() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        cal.set(java.util.Calendar.MINUTE, 0);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
