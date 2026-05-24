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

/**
 * מסך תקלות שטופלו — מציג היסטוריה של תקלות שסומנו כ-"fixed".
 * תומך בסינון לפי שכבה (עבור מדריך).
 */
public class FixedProblemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;          // רשימה גלילה של תקלות שטופלו
    private FixedProblemsAdapter adapter;        // אדפטר שמחבר נתונים לתצוגה
    private List<Problem> fixedList;             // רשימת התקלות שטופלו
    private FirebaseFirestore db;                // גישה למסד הנתונים
    private String filterYear;                   // סינון לפי שכבה (null = הכל)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_problems);

        db = FirebaseFirestore.getInstance();
        fixedList = new ArrayList<>();

        // בדיקה אם יש סינון לפי שכבה
        filterYear = getIntent().getStringExtra("FILTER_YEAR");

        recyclerView = findViewById(R.id.recyclerViewFixed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FixedProblemsAdapter(fixedList);
        recyclerView.setAdapter(adapter);

        loadFixedProblems();
    }

    /**
     * טוען מחדש כל פעם שחוזרים למסך — מציג עדכונים אחרונים
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadFixedProblems();
    }

    /**
     * טוען תקלות שטופלו מ-Firestore (status="fixed").
     * מסנן לפי שכבה אם רלוונטי.
     */
    private void loadFixedProblems() {
        Query query = db.collection("problems").whereEqualTo("status", "fixed");

        if (filterYear != null) {
            query = query.whereEqualTo("year", filterYear);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fixedList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Problem p = doc.toObject(Problem.class);
                            if (p != null) {
                                fixedList.add(p);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "אין תקלות שטופלו בהיסטוריה", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show()
                );
    }
}
