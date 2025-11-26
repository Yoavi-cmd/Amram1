package com.example.amram1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etLoginId;
    private Button btnLogin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etLoginId = findViewById(R.id.etLoginId);
        btnLogin = findViewById(R.id.btnLogin);
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idInput = etLoginId.getText().toString().trim();

                if (idInput.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "נא להזין תעודת זהות", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser(idInput);
            }
        });
    }

    private void loginUser(String id) {
        // חיפוש המסמך באוסף users לפי ה-ID
        db.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // המשתמש נמצא! נהפוך אותו לאובייקט User
                            User user = documentSnapshot.toObject(User.class);

                            if (user != null) {
                                navigateBasedOnType(user);
                            }
                        } else {
                            // המשתמש לא נמצא ב-Database
                            Toast.makeText(LoginActivity.this, "משתמש לא קיים, פנה למנהל", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "שגיאה בהתחברות: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateBasedOnType(User user) {
        Intent intent = null;

        // נקבל את ה-type כ-int (בהתאם למחלקה User)
        int userType = user.getType();

        // נשתמש במספרים ב-case labels
        switch (userType) {
            case 1: // 1 = תלמיד (StudentHomeActivity)
                intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
                break;
            case 2: // 2 = מדריך (GuideHomeActivity)
                intent = new Intent(LoginActivity.this, GuideHomeActivity.class);
                break;
            case 3: // 3 = אב בית (HouseFatherActivity)
                intent = new Intent(LoginActivity.this, HouseFatherActivity.class);
                break;
            case 4: // 4 = מנהל (ManagerHomeActivity)
                intent = new Intent(LoginActivity.this, ManagerHomeActivity.class);
                break;
            default:
                Toast.makeText(this, "שגיאה: סוג משתמש לא תקין", Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            // מעבירים את המידע על המשתמש לדף הבא (אופציונלי, אך מומלץ)
            intent.putExtra("USER_ID", user.getId());
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_YEAR", user.getYear());

            startActivity(intent);
            finish(); // סוגר את דף הכניסה כדי שלא יחזרו אליו בלחיצה על "חזור"
        }
    }
}