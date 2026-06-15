package com.example.amram1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * אדפטר שמציג רשימת משתמשים ב-RecyclerView עם כותרות קבוצה.
 * תומך בשני סוגי פריטים: כותרת (String) ומשתמש (User).
 * הרשימה מכילה אובייקטים מסוג Object — כל אחד הוא String (כותרת) או User (משתמש).
 */
public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // סוג פריט: כותרת קבוצה
    private static final int TYPE_USER = 1;    // סוג פריט: משתמש

    private List<Object> items;                // רשימה משולבת של כותרות ומשתמשים
    private OnUserDeleteListener listener;     // מאזין ללחיצה על כפתור מחיקה

    /**
     * ממשק שמאפשר ל-Activity לטפל בלחיצה על כפתור מחיקה
     */
    public interface OnUserDeleteListener {
        void onDeleteClick(User user, int position);
    }

    // בנאי — מקבל רשימה משולבת ומאזין מחיקה
    public UsersAdapter(List<Object> items, OnUserDeleteListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /**
     * קובע את סוג הפריט לפי המיקום ברשימה.
     * אם האובייקט הוא String — זו כותרת. אם User — זה משתמש.
     */
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return TYPE_HEADER; // כותרת קבוצה
        } else {
            return TYPE_USER;   // משתמש
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            // יצירת תצוגה לכותרת קבוצה
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            // יצירת תצוגה למשתמש
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            // מילוי כותרת קבוצה
            String headerText = (String) items.get(position);
            ((HeaderViewHolder) holder).tvHeaderTitle.setText(headerText);
        } else if (holder instanceof UserViewHolder) {
            // מילוי פרטי משתמש
            User user = (User) items.get(position);
            UserViewHolder userHolder = (UserViewHolder) holder;

            // הצגת שם המשתמש
            userHolder.tvUserName.setText(user.getName());

            // הצגת תעודת זהות ושכבה
            String details = "ת.ז.: " + user.getId() + " | שכבה: " + user.getYear();
            userHolder.tvUserDetails.setText(details);

            // הצגת סוג המשתמש בעברית
            userHolder.tvUserType.setText(getTypeText(user.getType()));

            // לחיצה על כפתור מחיקה — שולח ל-Activity דרך ה-listener
            userHolder.btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(user, userHolder.getAdapterPosition());
                }
            });
        }
    }

    /**
     * ממיר מספר type לטקסט בעברית
     */
    private String getTypeText(int type) {
        switch (type) {
            case 1: return "תלמיד";
            case 2: return "מדריך";
            case 3: return "אב בית";
            case 4: return "מנהל";
            default: return "לא ידוע";
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder לכותרת קבוצה — מחזיק את טקסט הכותרת
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeaderTitle; // כותרת הקבוצה

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeaderTitle = itemView.findViewById(R.id.tvHeaderTitle);
        }
    }

    /**
     * ViewHolder למשתמש — מחזיק את כל האלמנטים של פריט משתמש בודד
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserDetails, tvUserType; // שם, פרטים, סוג
        Button btnDelete;                                // כפתור מחיקה

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserDetails = itemView.findViewById(R.id.tvUserDetails);
            tvUserType = itemView.findViewById(R.id.tvUserType);
            btnDelete = itemView.findViewById(R.id.btnDeleteUser);
        }
    }
}
