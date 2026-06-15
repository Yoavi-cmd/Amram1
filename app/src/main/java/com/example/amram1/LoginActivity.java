package com.example.amram1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * מסך ההתחברות — המסך הראשון שנפתח באפליקציה.
 * בודק אם יש התחברות שמורה (SharedPreferences),
 * ואם כן — עובר ישר למסך הבית המתאים בלי צורך להזין ת.ז.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etLoginId;       // שדה קלט לתעודת זהות
    private Button btnLogin;          // כפתור התחברות
    private FirebaseFirestore db;     // גישה למסד הנתונים Firestore

    // משגר לבקשת הרשאות מהמשתמש (מצלמה, התראות)
    private ActivityResultLauncher<String[]> permissionsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // רישום ה-launcher לבקשת הרשאות — חייב להיות לפני כל קוד אחר
        permissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                results -> {
                    // ההרשאות התבקשו — ממשיכים לאפליקציה
                    continueToApp();
                });

        // בדיקה אם כבר ביקשנו הרשאות בעבר
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean permissionsRequested = prefs.getBoolean("PERMISSIONS_REQUESTED", false);

        if (!permissionsRequested) {
            // פעם ראשונה — מבקשים הרשאות ושומרים שביקשנו
            prefs.edit().putBoolean("PERMISSIONS_REQUESTED", true).apply();
            requestAllPermissions();
        } else {
            // כבר ביקשנו — ממשיכים ישר
            continueToApp();
        }
    }

    /**
     * מבקש הרשאות מצלמה והתראות מהמשתמש
     */
    private void requestAllPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);

        // התראות דורשות הרשאה רק מאנדרואיד 13 ומעלה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        permissionsLauncher.launch(permissions.toArray(new String[0]));
    }

    /**
     * בודק אם יש התחברות שמורה ב-SharedPreferences.
     * אם כן — עובר ישר למסך הבית. אם לא — מציג את מסך ההתחברות.
     */
    private void continueToApp() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedId = prefs.getString("USER_ID", null);

        // בדיקה אם יש משתמש שמור — התחברות אוטומטית
        if (savedId != null) {
            String savedName = prefs.getString("USER_NAME", "");
            String savedYear = prefs.getString("USER_YEAR", "");
            int savedType = prefs.getInt("USER_TYPE", -1);

            if (savedType != -1) {
                navigateToHome(savedId, savedName, savedYear, savedType);
                return; // יוצא מהפונקציה — לא מציג את מסך ההתחברות
            }
        }

        // אין משתמש שמור — מציגים את מסך ההתחברות
        setContentView(R.layout.activity_login);

        etLoginId = findViewById(R.id.etLoginId);
        btnLogin = findViewById(R.id.btnLogin);
        db = FirebaseFirestore.getInstance();

        // לחיצה על כפתור התחברות — שולח את הת.ז. לבדיקה
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

    /**
     * מחפש את המשתמש ב-Firestore לפי תעודת זהות.
     * אם נמצא — שומר אותו ב-SharedPreferences ומעביר למסך הבית.
     */
    private void loginUser(String id) {
        db.collection("users")
                .whereEqualTo("id", id)  // חיפוש לפי שדה id
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            Toast.makeText(this, "משתמש לא נמצא", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // המשתמש נמצא — ממירים למחלקת User ושומרים
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            saveUserToPrefs(user);  // שמירת פרטי המשתמש לזיכרון המכשיר
                            navigateToHome(user.getId(), user.getName(), user.getYear(), user.getType());
                        }
                    } else {
                        Toast.makeText(this,
                                "שגיאה: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * שומר את פרטי המשתמש ב-SharedPreferences לצורך התחברות אוטומטית
     */
    private void saveUserToPrefs(User user) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("USER_ID", user.getId());
        editor.putString("USER_NAME", user.getName());
        editor.putString("USER_YEAR", user.getYear());
        editor.putInt("USER_TYPE", user.getType());
        editor.apply(); // שמירה אסינכרונית לזיכרון המכשיר
    }

    /**
     * מעביר את המשתמש למסך הבית המתאים לפי הסוג שלו.
     * type 1=תלמיד, 2=מדריך, 3=אב בית, 4=מנהל
     */
    private void navigateToHome(String id, String name, String year, int type) {
        Intent intent = null;

        switch (type) {
            case 1: // תלמיד
                intent = new Intent(this, StudentHomeActivity.class);
                break;
            case 2: // מדריך
                intent = new Intent(this, GuideHomeActivity.class);
                break;
            case 3: // אב בית
                intent = new Intent(this, HouseFatherActivity.class);
                break;
            case 4: // מנהל
                intent = new Intent(this, ManagerHomeActivity.class);
                break;
            default:
                Toast.makeText(this, "שגיאה: סוג משתמש לא תקין", Toast.LENGTH_SHORT).show();
                return;
        }

        // מעביר את פרטי המשתמש למסך הבא דרך Intent extras
        intent.putExtra("USER_ID", id);
        intent.putExtra("USER_NAME", name);
        intent.putExtra("USER_YEAR", year);
        startActivity(intent);
        finish(); // סוגר את מסך ההתחברות כדי שלא יחזרו אליו בלחיצת חזור
    }
}
