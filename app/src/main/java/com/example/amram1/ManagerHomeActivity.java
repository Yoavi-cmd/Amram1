package com.example.amram1; // ודא שזה שם החבילה שלך

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ManagerHomeActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private Button btnAddStudent;

    // אופציונלי: משתנים לשמירת שם המנהל
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_home);

        tvWelcomeMessage = findViewById(R.id.tvManagerWelcomeMessage);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        // יש לקשר גם את הכפתורים האחרים כאן (אפשרי לעשות זאת בהמשך)

        // 1. קבלת פרטי המנהל מדף הכניסה
        Intent intent = getIntent();
        currentUserName = intent.getStringExtra("USER_NAME");

        // הצגת הודעת קבלת פנים מותאמת אישית
        if (currentUserName != null) {
            tvWelcomeMessage.setText("שלום מנהל, " + currentUserName);
        } else {
            tvWelcomeMessage.setText("שלום מנהל!");
        }

        // 2. הגדרת כפתור "להוסיף תלמיד" לניתוב למסך שכבר בנינו
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // המעבר אל AddStudentActivity
                Intent addIntent = new Intent(ManagerHomeActivity.this, AddStudentActivity.class);
                startActivity(addIntent);
            }
        });

        // נותב למסכים אחרים ייבנה כשתממש אותם...
    }
}