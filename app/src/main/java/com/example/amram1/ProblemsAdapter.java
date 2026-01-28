package com.example.amram1;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProblemsAdapter extends RecyclerView.Adapter<ProblemsAdapter.ProblemViewHolder> {

    private List<Problem> problemList;
    private OnProblemClickListener listener;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_problem, parent, false);
        return new ProblemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProblemViewHolder holder, int position) {
        Problem problem = problemList.get(position);
        holder.tvTypeAndRoom.setText(problem.getTypeP() + " | חדר " + problem.getRoomP());
        holder.tvDesc.setText(problem.getDescription());

        int severity = problem.getSeverity();
        if (severity >= 3) {
            holder.tvSeverity.setText("דחיפות: גבוהה ");
            holder.tvSeverity.setTextColor(Color.RED);
        } else if (severity == 2) {
            holder.tvSeverity.setText("דחיפות: בינונית");
            holder.tvSeverity.setTextColor(Color.parseColor("#FFA000"));
        } else {
            holder.tvSeverity.setText("דחיפות: רגילה");
            holder.tvSeverity.setTextColor(Color.parseColor("#388E3C"));
        }

        holder.btnFixed.setOnClickListener(v -> {
            if (listener != null) {
                // תיקון: עכשיו אנחנו שולחים את ה-ID האמיתי
                listener.onMarkFixedClick(problem, problem.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return problemList.size();
    }

    public static class ProblemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTypeAndRoom, tvSeverity, tvDesc;
        Button btnFixed;

        public ProblemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTypeAndRoom = itemView.findViewById(R.id.tvProblemTypeAndRoom);
            tvSeverity = itemView.findViewById(R.id.tvSeverity);
            tvDesc = itemView.findViewById(R.id.tvProblemDesc);
            btnFixed = itemView.findViewById(R.id.btnMarkFixed);
        }
    }
}