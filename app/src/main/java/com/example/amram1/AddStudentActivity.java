package com.example.amram1; // ודא שזה שם החבילה שלך

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddStudentActivity extends AppCompatActivity {

    private EditText etStudentName, etStudentId, etStudentYear;
    private Button btnAddStudent;

    // 1. קבלת גישה לבסיס הנתונים של Firestore
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student); // קשר לקובץ ה-XML

        // 2. קישור המשתנים לעיצוב
        etStudentName = findViewById(R.id.etStudentName);
        etStudentId = findViewById(R.id.etStudentId);
        etStudentYear = findViewById(R.id.etStudentYear);
        btnAddStudent = findViewById(R.id.btnAddStudent);

        // 3. אתחול הגישה ל-Firestore
        db = FirebaseFirestore.getInstance();

        // 4. הגדרת מאזין ללחיצה על כפתור "הוספה"
        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // קריאה לפונקציה שיוצרת את המשתמש
                createStudentUser();
            }
        });
    }

    private void createStudentUser() {
        // 5. איסוף הנתונים מהשדות
        String name = etStudentName.getText().toString().trim();
        String id = etStudentId.getText().toString().trim();
        String year = etStudentYear.getText().toString().trim();

        // בדיקה בסיסית שהשדות אינם ריקים
        if (name.isEmpty() || id.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        // 6. יצירת אובייקט User חדש
        // סוג משתמש 1 = תלמיד (כפי שהגדרת)
        int userType = 1;
        User newStudent = new User(name, id, userType, year);

        // 7. שמירת האובייקט ב-Firestore
        // אנחנו ניצור אוסף (collection) בשם "users"
        // ונשתמש בתעודת הזהות של התלמיד בתור ה-ID של המסמך (Document)
        db.collection("users").document(id)
                .set(newStudent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // הצלחה!
                        Toast.makeText(AddStudentActivity.this, "התלמיד נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                        // אפשר לנקות את השדות או לסגור את המסך
                        etStudentName.setText("");
                        etStudentId.setText("");
                        etStudentYear.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // כישלון!
                        Toast.makeText(AddStudentActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}