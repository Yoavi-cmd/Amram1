package com.example.amram1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * מסך דיווח תקלה — מאפשר למשתמש לדווח על בעיה בפנימייה.
 * המשתמש ממלא מספר חדר, בוחר סוג תקלה, כותב תיאור ומצרף תמונה (מצלמה או גלריה).
 * רמת הדחיפות נקבעת אוטומטית לפי סוג התקלה.
 * התמונה נשמרת כ-Base64 ישירות ב-Firestore.
 */
public class ReportProblemActivity extends AppCompatActivity {

    private EditText etRoomNumber, etDescription;  // שדות קלט: מספר חדר ותיאור
    private Spinner spinnerProblemType;             // רשימה נפתחת לבחירת סוג תקלה
    private Button btnPickImage, btnSubmit;         // כפתורי בחירת תמונה ושליחה
    private ImageView ivProblemImage;               // תצוגה מקדימה של התמונה שנבחרה
    private ProgressBar progressBar;                // סרגל טעינה בזמן שליחה

    private Uri imageUri;                           // כתובת התמונה שנבחרה
    private ActivityResultLauncher<Intent> pickImageLauncher;   // משגר לבחירה מגלריה
    private ActivityResultLauncher<Uri> takePictureLauncher;    // משגר לצילום מצלמה
    private ActivityResultLauncher<String> cameraPermissionLauncher; // משגר לבקשת הרשאת מצלמה
    private Uri cameraImageUri;                     // כתובת הקובץ הזמני לצילום

    private FirebaseFirestore db;                   // גישה למסד הנתונים

    private String currentUserName;                 // שם המשתמש המדווח
    private String currentUserYear;                 // שכבת המשתמש המדווח

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        // קישור המשתנים לאלמנטים בעיצוב
        etRoomNumber = findViewById(R.id.etRoomNumber);
        spinnerProblemType = findViewById(R.id.spinnerProblemType);
        etDescription = findViewById(R.id.etDescription);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivProblemImage = findViewById(R.id.ivProblemImage);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        // קבלת פרטי המשתמש מהמסך הקודם
        currentUserName = getIntent().getStringExtra("USER_NAME");
        currentUserYear = getIntent().getStringExtra("USER_YEAR");

        setupSpinner();

        // רישום ה-launcher לבחירת תמונה מהגלריה
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        ivProblemImage.setImageURI(imageUri);
                        ivProblemImage.setVisibility(View.VISIBLE);
                    }
                });

        // רישום ה-launcher לצילום תמונה מהמצלמה
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        imageUri = cameraImageUri;
                        ivProblemImage.setImageURI(imageUri);
                        ivProblemImage.setVisibility(View.VISIBLE);
                    }
                });

        // רישום ה-launcher לבקשת הרשאת מצלמה
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        launchCamera();
                    } else {
                        Toast.makeText(this, "צריך הרשאת מצלמה כדי לצלם", Toast.LENGTH_SHORT).show();
                    }
                });

        // לחיצה על כפתור תמונה — מציג דיאלוג בחירה (מצלמה/גלריה)
        btnPickImage.setOnClickListener(v -> showImageSourceDialog());
        btnSubmit.setOnClickListener(v -> submitProblem());
    }

    /**
     * מציג דיאלוג שמאפשר למשתמש לבחור בין מצלמה לגלריה
     */
    private void showImageSourceDialog() {
        String[] options = {"מצלמה", "גלריה"};
        new AlertDialog.Builder(this)
                .setTitle("בחר מקור תמונה")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        openCamera();
                    } else {
                        openGallery();
                    }
                })
                .show();
    }

    /**
     * בודק הרשאת מצלמה — אם יש, פותח. אם אין, מבקש.
     */
    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * פותח את המצלמה — יוצר קובץ זמני לשמירת התמונה דרך FileProvider
     */
    private void launchCamera() {
        File imageFile = new File(getCacheDir(), "camera_photo.jpg");
        cameraImageUri = FileProvider.getUriForFile(this,
                getPackageName() + ".fileprovider", imageFile);
        takePictureLauncher.launch(cameraImageUri);
    }

    /**
     * פותח את הגלריה לבחירת תמונה קיימת
     */
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);
    }

    /**
     * מגדיר את הרשימה הנפתחת עם סוגי התקלות
     */
    private void setupSpinner() {
        String[] problemTypes = {"חשמל", "אינסטלציה", "אינטרנט", "ריהוט", "אחר"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, problemTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProblemType.setAdapter(adapter);
    }

    /**
     * קובע רמת דחיפות אוטומטית לפי סוג התקלה:
     * 1 = גבוהה (חשמל, אינסטלציה)
     * 2 = בינונית (אינטרנט, אחר)
     * 3 = נמוכה (ריהוט)
     */
    private int getSeverityByType(String problemType) {
        switch (problemType) {
            case "חשמל":
            case "אינסטלציה":
                return 1;
            case "אינטרנט":
                return 2;
            case "ריהוט":
                return 3;
            default:
                return 2;
        }
    }

    /**
     * אוסף נתונים מהטופס, מוודא תקינות, ממיר תמונה ושולח ל-Firestore
     */
    private void submitProblem() {
        String roomNumberStr = etRoomNumber.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String problemType = spinnerProblemType.getSelectedItem().toString();

        if (TextUtils.isEmpty(roomNumberStr)) {
            etRoomNumber.setError("חובה להזין מספר חדר");
            return;
        }
        if (TextUtils.isEmpty(description)) {
            etDescription.setError("חובה להזין תיאור");
            return;
        }

        int roomNumber = Integer.parseInt(roomNumberStr);
        int severity = getSeverityByType(problemType);
        setLoading(true);

        String imageBase64 = null;
        if (imageUri != null) {
            imageBase64 = convertImageToBase64(imageUri);
        }

        saveProblem(roomNumber, description, problemType, severity, imageBase64);
    }

    /**
     * ממיר תמונה למחרוזת Base64.
     * מקטין ל-400px רוחב ודוחס ב-JPEG 50% כדי לחסוך מקום ב-Firestore.
     */
    private String convertImageToBase64(Uri uri) {
        try {
            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) inputStream.close();

            if (bitmap == null) {
                Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
                return null;
            }

            int maxWidth = 400;
            if (bitmap.getWidth() > maxWidth) {
                float ratio = (float) maxWidth / bitmap.getWidth();
                int newHeight = (int) (bitmap.getHeight() * ratio);
                bitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, newHeight, true);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            Toast.makeText(this, "שגיאה בטעינת התמונה", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * שומר את התקלה ב-Firestore
     */
    private void saveProblem(int roomNumber, String description, String problemType, int severity, String imageBase64) {
        long timestamp = System.currentTimeMillis();
        String status = "active";

        Problem problem = new Problem(problemType, severity, roomNumber, description, imageBase64, currentUserName, currentUserYear, status, timestamp);

        db.collection("problems").add(problem)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(ReportProblemActivity.this, "הדיווח נשלח בהצלחה", Toast.LENGTH_SHORT).show();
                    setLoading(false);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ReportProblemActivity.this, "שגיאה בשליחת הדיווח: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    setLoading(false);
                });
    }

    /**
     * מציג/מסתיר טעינה ומשבית/מפעיל את כל הקלטים
     */
    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!isLoading);
        btnPickImage.setEnabled(!isLoading);
        etRoomNumber.setEnabled(!isLoading);
        etDescription.setEnabled(!isLoading);
        spinnerProblemType.setEnabled(!isLoading);
    }
}
