package com.example.amram1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PermanentTasksAdapter extends RecyclerView.Adapter<PermanentTasksAdapter.TaskViewHolder> {

    private List<PermanentTask> taskList;
    private OnTaskClickListener listener;

    // ממשק ללחיצה (כדי שנטפל בלוגיקה ב-Activity)
    public interface OnTaskClickListener {
        void onMarkDoneClick(PermanentTask task);
    }

    public PermanentTasksAdapter(List<PermanentTask> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_permanent_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        PermanentTask task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvDesc.setText(task.getDescription());

        holder.btnDone.setOnClickListener(v -> listener.onMarkDoneClick(task));
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        Button btnDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvDesc = itemView.findViewById(R.id.tvTaskDesc);
            btnDone = itemView.findViewById(R.id.btnMarkDone);
        }
    }
}
