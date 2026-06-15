package com.example.amram1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * מסך הוספת משתמש — נגיש רק למנהל.
 * מאפשר להוסיף משתמש חדש למערכת (תלמיד, מדריך, אב בית, או מנהל).
 * סוג המשתמש נבחר מרשימה נפתחת והופך אוטומטית ל-type מספרי.
 */
public class AddStudentActivity extends AppCompatActivity {

    private EditText etStudentName, etStudentId, etStudentYear; // שדות קלט: שם, ת.ז., שכבה
    private Spinner spinnerUserType;  // רשימה נפתחת לבחירת סוג משתמש
    private Button btnAddStudent;     // כפתור הוספה

    private FirebaseFirestore db;     // גישה למסד הנתונים

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        // קישור המשתנים לאלמנטים בעיצוב
        etStudentName = findViewById(R.id.etStudentName);
        etStudentId = findViewById(R.id.etStudentId);
        etStudentYear = findViewById(R.id.etStudentYear);
        spinnerUserType = findViewById(R.id.spinnerUserType);
        btnAddStudent = findViewById(R.id.btnAddStudent);
        db = FirebaseFirestore.getInstance();

        setupSpinner();

        btnAddStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    /**
     * מגדיר את הרשימה הנפתחת עם סוגי המשתמשים
     */
    private void setupSpinner() {
        String[] userTypes = {"תלמיד", "מדריך", "אב בית", "מנהל"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, userTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserType.setAdapter(adapter);
    }

    /**
     * ממיר את הבחירה ב-Spinner למספר type:
     * תלמיד=1, מדריך=2, אב בית=3, מנהל=4
     */
    private int getTypeFromSpinner() {
        int position = spinnerUserType.getSelectedItemPosition();
        switch (position) {
            case 0: return 1; // תלמיד
            case 1: return 2; // מדריך
            case 2: return 3; // אב בית
            case 3: return 4; // מנהל
            default: return 1;
        }
    }

    /**
     * יוצר משתמש חדש ושומר אותו ב-Firestore.
     * ה-document ID הוא תעודת הזהות — כך מונעים כפילויות.
     */
    private void createUser() {
        String name = etStudentName.getText().toString().trim();
        String id = etStudentId.getText().toString().trim();
        String year = etStudentYear.getText().toString().trim();

        // בדיקת תקינות — כל השדות חובה
        if (name.isEmpty() || id.isEmpty() || year.isEmpty()) {
            Toast.makeText(this, "יש למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        int userType = getTypeFromSpinner();
        User newUser = new User(name, id, userType, year);

        // שמירה ב-Firestore — document ID = תעודת זהות
        db.collection("users").document(id)
                .set(newUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddStudentActivity.this, "המשתמש נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                        // ניקוי השדות להוספה הבאה
                        etStudentName.setText("");
                        etStudentId.setText("");
                        etStudentYear.setText("");
                        spinnerUserType.setSelection(0);
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
