package com.example.amram1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * מסך ניהול משתמשים — מציג את כל המשתמשים הרשומים במערכת.
 * נגיש למנהל בלבד.
 * תומך בחיפוש לפי שם/ת.ז., תצוגה מקובצת לפי תפקיד ושכבה,
 * ומחיקת משתמשים עם אישור דיאלוג.
 *
 * סדר התצוגה: מנהלים -> אבות בית -> מדריכים -> תלמידים (לפי שכבה)
 */
public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView recyclerView;      // רשימה גלילה של משתמשים
    private UsersAdapter adapter;           // אדפטר שמחבר נתונים לתצוגה
    private List<Object> displayItems;      // רשימה משולבת (כותרות + משתמשים) להצגה
    private List<User> allUsers;            // רשימת כל המשתמשים (לפני סינון)
    private EditText etSearchUsers;         // שדה חיפוש
    private TextView tvUserCount;           // מציג את מספר המשתמשים
    private FirebaseFirestore db;           // גישה למסד הנתונים

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // קישור המשתנים לאלמנטים בעיצוב
        recyclerView = findViewById(R.id.recyclerViewUsers);
        tvUserCount = findViewById(R.id.tvUserCount);
        etSearchUsers = findViewById(R.id.etSearchUsers);
        db = FirebaseFirestore.getInstance();
        allUsers = new ArrayList<>();
        displayItems = new ArrayList<>();

        // הגדרת RecyclerView עם אדפטר ו-LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(displayItems, new UsersAdapter.OnUserDeleteListener() {
            @Override
            public void onDeleteClick(User user, int position) {
                // הצגת דיאלוג אישור לפני מחיקה
                showDeleteConfirmation(user, position);
            }
        });
        recyclerView.setAdapter(adapter);

        // מאזין לשינויים בשדה החיפוש — מסנן את הרשימה בזמן אמת
        etSearchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // כל פעם שהטקסט משתנה — מסנן ומעדכן את הרשימה
                filterAndDisplay(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // טעינת כל המשתמשים מ-Firestore
        loadUsers();
    }

    /**
     * טוען מחדש כל פעם שחוזרים למסך — מציג משתמשים שנוספו
     */
    @Override
    protected void onResume() {
        super.onResume();
        loadUsers();
    }

    /**
     * טוען את כל המשתמשים מאוסף "users" ב-Firestore.
     * לאחר הטעינה ממיין ומקבץ אותם לפי סוג ושכבה.
     */
    private void loadUsers() {
        db.collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    allUsers.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        // אין משתמשים במערכת
                        tvUserCount.setText("אין משתמשים רשומים");
                        displayItems.clear();
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    // ממיר כל document למשתמש
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            allUsers.add(user);
                        }
                    }

                    // עדכון מונה וסינון לפי טקסט החיפוש הנוכחי
                    tvUserCount.setText("סה\"כ " + allUsers.size() + " משתמשים");
                    String currentSearch = etSearchUsers.getText().toString().trim();
                    filterAndDisplay(currentSearch);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "שגיאה בטעינת משתמשים: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    /**
     * מסנן את רשימת המשתמשים לפי טקסט חיפוש,
     * ואז מקבץ ומציג אותם עם כותרות לפי סוג ושכבה.
     *
     * סדר התצוגה:
     * 1. מנהלים (type=4) — תמיד ראשונים
     * 2. אבות בית (type=3)
     * 3. מדריכים (type=2)
     * 4. תלמידים (type=1) — מחולקים לפי שכבה
     */
    private void filterAndDisplay(String searchText) {
        // שלב 1: סינון לפי טקסט חיפוש (שם או ת.ז.)
        List<User> filtered = new ArrayList<>();
        for (User user : allUsers) {
            if (searchText.isEmpty()) {
                // אין חיפוש — מציג הכל
                filtered.add(user);
            } else {
                // בודק אם השם או הת.ז. מכילים את טקסט החיפוש
                String lowerSearch = searchText.toLowerCase();
                boolean nameMatch = user.getName() != null && user.getName().toLowerCase().contains(lowerSearch);
                boolean idMatch = user.getId() != null && user.getId().contains(searchText);
                if (nameMatch || idMatch) {
                    filtered.add(user);
                }
            }
        }

        // שלב 2: מיון — לפי סוג (4 ראשון, 1 אחרון), ובתוך כל סוג — לפי שכבה ושם
        Collections.sort(filtered, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                // מיון ראשי: לפי type בסדר יורד (4 ראשון, 1 אחרון)
                int typeCompare = Integer.compare(u2.getType(), u1.getType());
                if (typeCompare != 0) return typeCompare;

                // מיון משני: לפי שכבה (סדר עולה)
                String year1 = u1.getYear() != null ? u1.getYear() : "";
                String year2 = u2.getYear() != null ? u2.getYear() : "";
                int yearCompare = year1.compareTo(year2);
                if (yearCompare != 0) return yearCompare;

                // מיון שלישי: לפי שם (סדר אלפביתי)
                String name1 = u1.getName() != null ? u1.getName() : "";
                String name2 = u2.getName() != null ? u2.getName() : "";
                return name1.compareTo(name2);
            }
        });

        // שלב 3: קיבוץ — יצירת רשימה משולבת עם כותרות קבוצה
        displayItems.clear();

        // מפה מסודרת שמקבצת משתמשים לפי כותרת קבוצה
        LinkedHashMap<String, List<User>> groups = new LinkedHashMap<>();
        for (User user : filtered) {
            String groupName = getGroupName(user); // מחזיר שם קבוצה לפי type ושכבה
            if (!groups.containsKey(groupName)) {
                groups.put(groupName, new ArrayList<>());
            }
            groups.get(groupName).add(user);
        }

        // מוסיף לרשימת התצוגה: כותרת ואז את כל המשתמשים בקבוצה
        for (Map.Entry<String, List<User>> entry : groups.entrySet()) {
            displayItems.add(entry.getKey());       // כותרת קבוצה (String)
            displayItems.addAll(entry.getValue());  // משתמשים בקבוצה (User)
        }

        // עדכון האדפטר והמונה
        adapter.notifyDataSetChanged();

        // עדכון מונה — מראה כמה נמצאו בחיפוש
        if (!searchText.isEmpty()) {
            tvUserCount.setText("נמצאו " + filtered.size() + " מתוך " + allUsers.size());
        } else {
            tvUserCount.setText("סה\"כ " + allUsers.size() + " משתמשים");
        }
    }

    /**
     * מחזיר שם קבוצה לפי סוג המשתמש ושכבתו.
     * מנהלים/צוות מקובצים לפי תפקיד, תלמידים לפי שכבה.
     */
    private String getGroupName(User user) {
        switch (user.getType()) {
            case 4: return "הנהלה";
            case 3: return "אבות בית";
            case 2: return "מדריכים";
            case 1:
                // תלמידים מקובצים לפי שכבה
                String year = user.getYear();
                if (year != null && !year.isEmpty()) {
                    return "תלמידים — שכבת " + year;
                } else {
                    return "תלמידים";
                }
            default: return "אחר";
        }
    }

    /**
     * מציג דיאלוג אישור לפני מחיקת משתמש.
     * מונע מחיקה בטעות — המשתמש צריך ללחוץ "מחק" כדי לאשר.
     */
    private void showDeleteConfirmation(User user, int position) {
        new AlertDialog.Builder(this)
                .setTitle("מחיקת משתמש")
                .setMessage("האם אתה בטוח שברצונך למחוק את " + user.getName() + "?")
                .setPositiveButton("מחק", (dialog, which) -> {
                    // המשתמש אישר — מוחק מ-Firestore
                    deleteUser(user, position);
                })
                .setNegativeButton("ביטול", null) // סגירת הדיאלוג ללא פעולה
                .show();
    }

    /**
     * מוחק משתמש מ-Firestore לפי תעודת הזהות שלו (document ID).
     * לאחר הצלחה — מסיר מהרשימה המקורית ומרענן את התצוגה.
     */
    private void deleteUser(User user, int position) {
        db.collection("users").document(user.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, user.getName() + " נמחק בהצלחה", Toast.LENGTH_SHORT).show();

                    // הסרה מרשימת כל המשתמשים
                    allUsers.remove(user);

                    // ריענון התצוגה המקובצת
                    String currentSearch = etSearchUsers.getText().toString().trim();
                    filterAndDisplay(currentSearch);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "שגיאה במחיקה: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
