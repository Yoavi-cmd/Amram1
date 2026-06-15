package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyProblemsActivity extends AppCompatActivity {

    private RecyclerView rvMyProblems;
    private TextView tvEmpty;
    private List<Problem> problemList;
    private MyProblemsAdapter adapter;
    private FirebaseFirestore db;
    private String currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_problems);

        rvMyProblems = findViewById(R.id.rvMyProblems);
        tvEmpty = findViewById(R.id.tvEmpty);
        db = FirebaseFirestore.getInstance();

        currentUserName = getIntent().getStringExtra("USER_NAME");

        problemList = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences("ReminderPrefs", MODE_PRIVATE);
        adapter = new MyProblemsAdapter(problemList, currentUserName, prefs);

        rvMyProblems.setLayoutManager(new LinearLayoutManager(this));
        rvMyProblems.setAdapter(adapter);

        loadMyProblems();
    }

    private void loadMyProblems() {
        db.collection("problems")
                .whereEqualTo("reporterName", currentUserName)
                .whereEqualTo("status", "active")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    problemList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Problem problem = doc.toObject(Problem.class);
                        problem.setId(doc.getId());
                        problemList.add(problem);
                    }
                    adapter.notifyDataSetChanged();

                    if (problemList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvMyProblems.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvMyProblems.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show();
                });
    }
}
