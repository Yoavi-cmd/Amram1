package com.example.amram1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateTaskActivity extends AppCompatActivity {

    private EditText etTitle, etDesc;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        etTitle = findViewById(R.id.etTaskTitle);
        etDesc = findViewById(R.id.etTaskDesc);
        btnSave = findViewById(R.id.btnSaveTask);

        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTask();
            }
        });
    }

    private void saveTask() {
        String title = etTitle.getText().toString().trim();
        String description = etDesc.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "נא למלא כותרת ותיאור", Toast.LENGTH_SHORT).show();
            return;
        }

        // יצירת האובייקט (השתמשנו ב-PermanentTask שיצרנו קודם)
        PermanentTask newTask = new PermanentTask(title, description);

        // שמירה ב-Firestore
        // הפקודה .add() יוצרת מסמך עם ID אוטומטי (כמו ה-Auto-ID שעשית ידנית)
        db.collection("permanent_tasks")
                .add(newTask)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CreateTaskActivity.this, "המשימה נוצרה בהצלחה!", Toast.LENGTH_SHORT).show();
                        finish(); // סוגר את המסך וחוזר למסך הקודם (בית אב הבית)
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CreateTaskActivity.this, "שגיאה בשמירה: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}