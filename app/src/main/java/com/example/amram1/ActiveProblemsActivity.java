package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.List;

public class ActiveProblemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProblemsAdapter adapter;
    private List<Problem> problemList;
    // מחקנו את רשימת ה-IDs הנפרדת, כבר לא צריך אותה!
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_problems);
        db = FirebaseFirestore.getInstance();
        problemList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewActiveProblems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProblemsAdapter(problemList, new ProblemsAdapter.OnProblemClickListener() {
            @Override
            public void onMarkFixedClick(Problem problem, String docId) { // docId מגיע עכשיו נכון
                int position = problemList.indexOf(problem);
                markProblemAsFixed(docId, position);
            }
        });

        recyclerView.setAdapter(adapter);
        loadActiveProblems();
    }

    private void loadActiveProblems() {
        // הדפסה 1: בדיקה שהפונקציה בכלל מתחילה
        android.util.Log.d("CHECK_DATA", "מתחיל לנסות למשוך נתונים...");

        db.collection("problems")
                .whereEqualTo("status", "active")
                // שים לב: הורדתי זמנית את ה-orderBy כדי לבודד את הבעיה
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // הדפסה 2: האם הגענו לפה? וכמה מסמכים מצאנו?
                    android.util.Log.d("CHECK_DATA", "הצלחה! מספר מסמכים שנמצאו: " + queryDocumentSnapshots.size());

                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "הרשימה ריקה (לא נמצאו תקלות active)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    problemList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        try {
                            // הדפסה 3: מנסה להמיר מסמך ספציפי
                            android.util.Log.d("CHECK_DATA", "בודק מסמך ID: " + doc.getId());

                            Problem p = doc.toObject(Problem.class);
                            if (p != null) {
                                p.setId(doc.getId());
                                problemList.add(p);
                                android.util.Log.d("CHECK_DATA", "הוספנו לרשימה: " + p.getDescription());
                            } else {
                                android.util.Log.e("CHECK_DATA", "המרת הנתונים נכשלה (p is null)");
                            }
                        } catch (Exception e) {
                            // הדפסה 4: אם יש קריסה בגלל סוג משתנה לא תואם
                            android.util.Log.e("CHECK_DATA", "שגיאה בקריאת שדות המסמך: " + e.getMessage());
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // הדפסה 5: שגיאת רשת או הרשאות
                    android.util.Log.e("CHECK_DATA", "כישלון קריטי בטעינה: " + e.getMessage());
                    Toast.makeText(this, "שגיאה בטעינה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void markProblemAsFixed(String docId, int position) {
        db.collection("problems").document(docId)
                .update("status", "fixed")
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "התקלה סומנה כטופלה!", Toast.LENGTH_SHORT).show();
                    if (position != -1 && position < problemList.size()) {
                        problemList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בעדכון: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}