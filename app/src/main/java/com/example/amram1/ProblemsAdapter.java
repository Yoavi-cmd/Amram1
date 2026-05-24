package com.example.amram1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * אדפטר שמציג רשימת תקלות פעילות ב-RecyclerView.
 * כל פריט מציג: סוג+חדר, דחיפות, תיאור, זמן פתוח, תמונה, וכפתור "סמן כטופל".
 */
public class ProblemsAdapter extends RecyclerView.Adapter<ProblemsAdapter.ProblemViewHolder> {

    private List<Problem> problemList;          // רשימת התקלות להצגה
    private OnProblemClickListener listener;    // מאזין ללחיצה על כפתור "סמן כטופל"

    /**
     * ממשק שמאפשר ל-Activity לטפל בלחיצה על כפתור "סמן כטופל"
     */
    public interface OnProblemClickListener {
        void onMarkFixedClick(Problem problem, String docId);
    }

    public ProblemsAdapter(List<Problem> problemList, OnProblemClickListener listener) {
        this.problemList = problemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProblemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // יצירת תצוגה חדשה מקובץ ה-XML של פריט תקלה
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_problem, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        Problem problem = problemList.get(position);

        // הצגת סוג התקלה ומספר החדר
        holder.tvTypeAndRoom.setText(problem.getTypeP() + " | חדר " + problem.getRoomP());
        holder.tvDesc.setText(problem.getDescription());

        // חישוב כמה זמן התקלה פתוחה
        long elapsed = System.currentTimeMillis() - problem.getTimestamp();
        holder.tvTimeOpen.setText("פתוח: " + formatElapsed(elapsed));

        // הצגת רמת הדחיפות בצבע מתאים
        int severity = problem.getSeverity();
        if (severity == 1) {
            holder.tvSeverity.setText("דחיפות: גבוהה");
            holder.tvSeverity.setTextColor(Color.RED);
        } else if (severity == 2) {
            holder.tvSeverity.setText("דחיפות: בינונית");
            holder.tvSeverity.setTextColor(Color.parseColor("#FFA000"));
        } else {
            holder.tvSeverity.setText("דחיפות: נמוכה");
            holder.tvSeverity.setTextColor(Color.parseColor("#388E3C"));
        }

        // אם יש תמונה — מפענחים את ה-Base64 ומציגים
        String imageBase64 = problem.getImageBase64();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivImage.setImageBitmap(bitmap);
            holder.ivImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }

        // לחיצה על "סמן כטופל" — שולח ל-Activity דרך ה-listener
        holder.btnFixed.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMarkFixedClick(problem, problem.getId());
            }
        });
    }

    /**
     * ממיר זמן במילישניות לטקסט קריא (דקות/שעות/ימים)
     */
    private String formatElapsed(long millis) {
        long minutes = millis / 60000;
        if (minutes < 60) {
            return minutes + " דקות";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " שעות";
        }
        long days = hours / 24;
        return days + " ימים";
    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }

    /**
     * ViewHolder — מחזיק את כל האלמנטים של פריט תקלה בודד
     */
    public static class ProblemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeAndRoom, tvSeverity, tvDesc, tvTimeOpen;
        ImageView ivImage;
        Button btnFixed;

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeAndRoom = itemView.findViewById(R.id.tvProblemTypeAndRoom);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);
            tvDesc = itemView.findViewById(R.id.tvProblemDesc);
            tvTimeOpen = itemView.findViewById(R.id.tvTimeOpen);
            ivImage = itemView.findViewById(R.id.ivProblemImage);
            btnFixed = itemView.findViewById(R.id.btnMarkFixed);
        }
    }
}
