package com.example.amram1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FixedProblemsAdapter extends RecyclerView.Adapter<FixedProblemsAdapter.FixedViewHolder> {

    private List<Problem> problemList;

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

        // שימוש בגטרים הקיימים שלך
        String title = problem.getTypeP() + " | חדר " + problem.getRoomP();
        holder.tvTitle.setText(title);
        holder.tvDesc.setText(problem.getDescription());
    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }

    public static class FixedViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;

        public FixedViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvProblemTitle);
            tvDesc = itemView.findViewById(R.id.tvProblemDesc);
        }
    }
}