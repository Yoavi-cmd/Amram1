package com.example.amram1;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MyProblemsAdapter extends RecyclerView.Adapter<MyProblemsAdapter.MyProblemViewHolder> {

    private List<Problem> problemList;
    private String senderName;
    private SharedPreferences prefs;

    public MyProblemsAdapter(List<Problem> problemList, String senderName, SharedPreferences prefs) {
        this.problemList = problemList;
        this.senderName = senderName;
        this.prefs = prefs;
    }

    @NonNull
    @Override
    public MyProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_problem, parent, false);
        return new MyProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProblemViewHolder holder, int position) {
        Problem problem = problemList.get(position);

        holder.tvTitle.setText(problem.getTypeP() + " | חדר " + problem.getRoomP());
        holder.tvDesc.setText(problem.getDescription());

        long elapsed = System.currentTimeMillis() - problem.getTimestamp();
        holder.tvTime.setText(formatElapsed(elapsed));

        String todayKey = "reminder_" + problem.getId() + "_" + getTodayString();
        boolean alreadySentToday = prefs.getBoolean(todayKey, false);

        if (alreadySentToday) {
            holder.btnReminder.setEnabled(false);
            holder.btnReminder.setText("תזכורת נשלחה היום");
        } else {
            holder.btnReminder.setEnabled(true);
            holder.btnReminder.setText("שלח תזכורת");
        }

        holder.btnReminder.setOnClickListener(v -> {
            if (prefs.getBoolean(todayKey, false)) {
                Toast.makeText(v.getContext(), "כבר שלחת תזכורת היום", Toast.LENGTH_SHORT).show();
                return;
            }

            Reminder reminder = new Reminder(
                    problem.getId(),
                    problem.getTypeP(),
                    problem.getRoomP(),
                    senderName,
                    System.currentTimeMillis()
            );

            FirebaseFirestore.getInstance().collection("reminders").add(reminder)
                    .addOnSuccessListener(doc -> {
                        prefs.edit().putBoolean(todayKey, true).apply();
                        holder.btnReminder.setEnabled(false);
                        holder.btnReminder.setText("תזכורת נשלחה היום");
                        Toast.makeText(v.getContext(), "התזכורת נשלחה", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(v.getContext(), "שגיאה בשליחה", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private String getTodayString() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
        return sdf.format(new java.util.Date());
    }

    private String formatElapsed(long millis) {
        long minutes = millis / 60000;
        if (minutes < 60) return minutes + " דקות";
        long hours = minutes / 60;
        if (hours < 24) return hours + " שעות";
        long days = hours / 24;
        return days + " ימים";
    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }

    public static class MyProblemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvTime;
        Button btnReminder;

        public MyProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMyProblemTitle);
            tvDesc = itemView.findViewById(R.id.tvMyProblemDesc);
            tvTime = itemView.findViewById(R.id.tvMyProblemTime);
            btnReminder = itemView.findViewById(R.id.btnSendReminder);
        }
    }
}
