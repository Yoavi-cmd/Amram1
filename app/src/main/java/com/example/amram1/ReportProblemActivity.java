package com.example.amram1;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.UUID;

public class ReportProblemActivity extends AppCompatActivity {

    private EditText etRoomNumber, etDescription;
    private Spinner spinnerProblemType;
    private Button btnPickImage, btnSubmit;
    private ImageView ivProblemImage;
    private ProgressBar progressBar;

    private Uri imageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    private FirebaseFirestore db;
    private StorageReference storageReference;

    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        etRoomNumber = findViewById(R.id.etRoomNumber);
        spinnerProblemType = findViewById(R.id.spinnerProblemType);
        etDescription = findViewById(R.id.etDescription);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivProblemImage = findViewById(R.id.ivProblemImage);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("problem_images");

        currentUserName = getIntent().getStringExtra("USER_NAME");

        setupSpinner();

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null && result.getData().getData() != null) {
                        imageUri = result.getData().getData();
                        ivProblemImage.setImageURI(imageUri);
                        ivProblemImage.setVisibility(View.VISIBLE);
                    }
                });

        btnPickImage.setOnClickListener(v -> openFileChooser());
        btnSubmit.setOnClickListener(v -> submitProblem());
    }

    private void setupSpinner() {
        String[] problemTypes = {"חשמל", "אינסטלציה", "אינטרנט", "ריהוט", "אחר"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, problemTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProblemType.setAdapter(adapter);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);
    }

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
        setLoading(true);
        if (imageUri != null) {
            uploadImageAndSaveProblem(roomNumber, description, problemType);
        } else {
            saveProblem(roomNumber, description, problemType, null);
        }
    }

    private void uploadImageAndSaveProblem(int roomNumber, String description, String problemType) {
        final StorageReference fileReference = storageReference.child(UUID.randomUUID().toString() + "." + getFileExtension(imageUri));

        fileReference.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        saveProblem(roomNumber, description, problemType, downloadUri.toString());
                    } else {
                        Toast.makeText(ReportProblemActivity.this, "העלאת התמונה נכשלה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        setLoading(false);
                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void saveProblem(int roomNumber, String description, String problemType, String imageUrl) {
        long timestamp = System.currentTimeMillis();
        int severity = 1;
        String status = "active";
        Problem problem = new Problem(problemType, severity, roomNumber, description, imageUrl, currentUserName, status, timestamp);
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

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!isLoading);
        btnPickImage.setEnabled(!isLoading);
        etRoomNumber.setEnabled(!isLoading);
        etDescription.setEnabled(!isLoading);
        spinnerProblemType.setEnabled(!isLoading);
    }
}