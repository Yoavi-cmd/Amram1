package com.example.amram1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * אדפטר שמציג רשימת תקלות שטופלו ב-RecyclerView.
 * כל פריט מציג: סוג+חדר, תיאור, ותמונה (אם קיימת).
 */
public class FixedProblemsAdapter extends RecyclerView.Adapter<FixedProblemsAdapter.FixedViewHolder> {

    private List<Problem> problemList; // רשימת התקלות שטופלו

    public FixedProblemsAdapter(List<Problem> problemList) {
        this.problemList = problemList;
    }

    @NonNull
    @Override
    public FixedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fixed_problem, parent, false);
        return new FixedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FixedViewHolder holder, int position) {
        Problem problem = problemList.get(position);

        // הצגת סוג התקלה ומספר החדר
        String title = problem.getTypeP() + " | חדר " + problem.getRoomP();
        holder.tvTitle.setText(title);
        holder.tvDesc.setText(problem.getDescription());

        // פיענוח Base64 והצגת התמונה אם קיימת
        String imageBase64 = problem.getImageBase64();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            holder.ivImage.setImageBitmap(bitmap);
            holder.ivImage.setVisibility(View.VISIBLE);
        } else {
            holder.ivImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }

    /**
     * ViewHolder — מחזיק את האלמנטים של פריט תקלה שטופלה
     */
    public static class FixedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        ImageView ivImage;

        public FixedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProblemTitle);
            tvDesc = itemView.findViewById(R.id.tvProblemDesc);
            ivImage = itemView.findViewById(R.id.ivProblemImage);
        }
    }
}
