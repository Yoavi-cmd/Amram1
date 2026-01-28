package com.example.amram1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
        db.collection("users")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            navigateBasedOnType(user);

                        }
                    } else {
                        Toast.makeText(this,
                                "Error: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateBasedOnType(User user) {
        Intent intent = null;
        int userType = user.getType();

        // נשתמש במספרים ב-case labels
        switch (userType) {
            case 1: // 1 = תלמיד
                intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
                break;
            case 2: // 2 = מדריך
                intent = new Intent(LoginActivity.this, GuideHomeActivity.class);
                break;
            case 3: // 3 = אב בית
                intent = new Intent(LoginActivity.this, HouseFatherActivity.class);
                break;
            case 4: // 4 = מנהל
                intent = new Intent(LoginActivity.this, ManagerHomeActivity.class);
                break;
            default:
                Toast.makeText(this, "שגיאה: סוג משתמש לא תקין", Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            intent.putExtra("USER_ID", user.getId());
            intent.putExtra("USER_NAME", user.getName());
            intent.putExtra("USER_YEAR", user.getYear());

            startActivity(intent);
            finish();
        }
    }
}