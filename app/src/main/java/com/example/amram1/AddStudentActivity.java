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
        db = FirebaseFirestore.getInstance();

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // קריאה לפונקציה שיוצרת את המשתמש
                createStudentUser();
            }
        });
    }

    private void createStudentUser() {
        String name = etStudentName.getText().toString().trim();
        String id = etStudentId.getText().toString().trim();
        String year = etStudentYear.getText().toString().trim();

        if (name.isEmpty() || id.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }


        int userType = 1;
        User newStudent = new User(name, id, userType, year);


        db.collection("users").document(id)
                .set(newStudent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddStudentActivity.this, "התלמיד נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                        etStudentName.setText("");
                        etStudentId.setText("");
                        etStudentYear.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddStudentActivity.this, "שגיאה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}