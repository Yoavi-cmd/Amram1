package com.example.amram1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermanentTasksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PermanentTasksAdapter adapter;
    private List<PermanentTask> taskList;
    private FirebaseFirestore db;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permanent_tasks);

        // קבלת מזהה המשתמש מהמסך הקודם
        currentUserId = getIntent().getStringExtra("USER_ID");

        db = FirebaseFirestore.getInstance();
        taskList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // הגדרת האדפטר
        adapter = new PermanentTasksAdapter(taskList, new PermanentTasksAdapter.OnTaskClickListener() {
            @Override
            public void onMarkDoneClick(PermanentTask task) {
                markTaskAsDone(task);
            }
        });
        recyclerView.setAdapter(adapter);

        loadTasks();
    }

    private void loadTasks() {

        db.collection("permanent_tasks")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        taskList.clear();
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            PermanentTask task = document.toObject(PermanentTask.class);
                            if (task != null) {
                                task.setId(document.getId()); // שמירת ה-ID לצורך עדכון
                                taskList.add(task);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "אין מטלות קבועות כרגע", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בטעינה", Toast.LENGTH_SHORT).show());
    }

    private void markTaskAsDone(PermanentTask task) {
        Map<String, Object> completionData = new HashMap<>();
        completionData.put("taskId", task.getId());
        completionData.put("taskTitle", task.getTitle());
        completionData.put("userId", currentUserId);
        completionData.put("timestamp", System.currentTimeMillis());


        db.collection("task_completions")
                .add(completionData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "הדיווח נשמר בהצלחה! כל הכבוד", Toast.LENGTH_LONG).show();
                    int position = taskList.indexOf(task);
                    if (position != -1) {
                        taskList.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בשמירה, נסה שוב", Toast.LENGTH_SHORT).show());
    }
}
