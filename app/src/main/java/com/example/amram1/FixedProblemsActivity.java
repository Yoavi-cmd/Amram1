package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FixedProblemsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FixedProblemsAdapter adapter;
    private List<Problem> fixedList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fixed_problems);

        db = FirebaseFirestore.getInstance();
        fixedList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewFixed);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FixedProblemsAdapter(fixedList);
        recyclerView.setAdapter(adapter);

        loadFixedProblems();
    }

    private void loadFixedProblems() {
        db.collection("problems")
                .whereEqualTo("status", "fixed")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        fixedList.clear();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Problem p = doc.toObject(Problem.class);
                            if (p != null) {
                                fixedList.add(p);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "אין תקלות שטופלו בהיסטוריה", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show()
                );
    }
}