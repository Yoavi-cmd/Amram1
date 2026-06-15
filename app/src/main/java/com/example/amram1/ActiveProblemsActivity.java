package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * מסך תקלות פעילות — מציג את כל התקלות שעדיין לא טופלו.
 * תומך בסינון לפי שכבה (עבור מדריך) ומיון לפי דחיפות או חדר.
 * כולל כפתור "סמן כטופל" שמעדכן את הסטטוס ב-Firestore לכל המשתמשים.
 */
public class ActiveProblemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;        // רשימה גלילה של תקלות
    private ProblemsAdapter adapter;          // אדפטר שמחבר את הנתונים לתצוגה
    private List<Problem> problemList;        // הרשימה המוצגת (אחרי מיון)
    private List<Problem> allProblems;        // הרשימה המקורית (לפני מיון)
    private Spinner spinnerFilter;            // רשימת מיון (לפי דחיפות/חדר)
    private FirebaseFirestore db;             // גישה למסד הנתונים
    private String filterYear;                // סינון לפי שכבה (null = הכל)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_problems);

        db = FirebaseFirestore.getInstance();
        problemList = new ArrayList<>();
        allProblems = new ArrayList<>();

        // בדיקה אם יש סינון לפי שכבה (רלוונטי למדריך)
        filterYear = getIntent().getStringExtra("FILTER_YEAR");

        spinnerFilter = findViewById(R.id.spinnerFilter);
        recyclerView = findViewById(R.id.recyclerViewActiveProblems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // יצירת האדפטר עם מאזין ללחיצה על "סמן כטופל"
        adapter = new ProblemsAdapter(problemList, new ProblemsAdapter.OnProblemClickListener() {
            @Override
            public void onMarkFixedClick(Problem problem, String docId) {
                int position = problemList.indexOf(problem);
                markProblemAsFixed(docId, position);
            }
        });
        recyclerView.setAdapter(adapter);

        // מציג Spinner מיון רק אם יש סינון שכבה (מדריך)
        if (filterYear != null) {
            setupFilterSpinner();
        }

        loadActiveProblems();
    }

    /**
     * כל פעם שחוזרים למסך — טוענים מחדש מ-Firestore
     * כדי לראות עדכונים שבוצעו על ידי משתמשים אחרים
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadActiveProblems();
    }

    /**
     * מגדיר Spinner למיון — לפי דחיפות או לפי חדר
     */
    private void setupFilterSpinner() {
        spinnerFilter.setVisibility(View.VISIBLE);
        String[] filterOptions = {"לפי דחיפות", "לפי חדר"};
        ArrayAdapter<String> filterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, filterOptions);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(filterAdapter);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applySorting(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * ממיין את הרשימה לפי הבחירה:
     * 0 = לפי דחיפות (הכי דחוף ראשון)
     * 1 = לפי מספר חדר (סדר עולה)
     */
    private void applySorting(int filterType) {
        problemList.clear();
        problemList.addAll(allProblems);

        if (filterType == 0) {
            // מיון לפי דחיפות — severity 1 (גבוהה) ראשון
            Collections.sort(problemList, new Comparator<Problem>() {
                @Override
                public int compare(Problem p1, Problem p2) {
                    return Integer.compare(p1.getSeverity(), p2.getSeverity());
                }
            });
        } else {
            // מיון לפי מספר חדר
            Collections.sort(problemList, new Comparator<Problem>() {
                @Override
                public int compare(Problem p1, Problem p2) {
                    return Integer.compare(p1.getRoomP(), p2.getRoomP());
                }
            });
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * טוען תקלות פעילות מ-Firestore.
     * מסנן לפי status="active" ואופציונלית לפי שכבה.
     */
    private void loadActiveProblems() {
        Query query = db.collection("problems").whereEqualTo("status", "active");

        // סינון לפי שכבה (רק למדריך)
        if (filterYear != null) {
            query = query.whereEqualTo("year", filterYear);
        }

        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    problemList.clear();
                    allProblems.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this, "אין תקלות פעילות", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // ממיר כל document ל-Problem ושומר את ה-ID
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Problem p = doc.toObject(Problem.class);
                        if (p != null) {
                            p.setId(doc.getId()); // שמירת ה-ID לצורך עדכון בהמשך
                            problemList.add(p);
                            allProblems.add(p);
                        }
                    }

                    // מיון ברירת מחדל — לפי דחיפות (1=גבוהה ראשון)
                    Collections.sort(problemList, new Comparator<Problem>() {
                        @Override
                        public int compare(Problem p1, Problem p2) {
                            return Integer.compare(p1.getSeverity(), p2.getSeverity());
                        }
                    });
                    Collections.sort(allProblems, new Comparator<Problem>() {
                        @Override
                        public int compare(Problem p1, Problem p2) {
                            return Integer.compare(p1.getSeverity(), p2.getSeverity());
                        }
                    });

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בטעינה: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    /**
     * מסמן תקלה כטופלה ב-Firestore — העדכון הוא גלובלי.
     * כל המשתמשים יראו את השינוי כי כולם קוראים מאותו מסד נתונים.
     */
    private void markProblemAsFixed(String docId, int position) {
        db.collection("problems").document(docId)
                .update("status", "fixed") // משנה את הסטטוס ל-"fixed"
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "התקלה סומנה כטופלה!", Toast.LENGTH_SHORT).show();
                    // הסרה מהרשימה המוצגת מיד (בלי לחכות לריענון)
                    if (position != -1 && position < problemList.size()) {
                        Problem removed = problemList.remove(position);
                        allProblems.remove(removed);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בעדכון: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
