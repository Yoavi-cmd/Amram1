# Application Class Documentation - Amram1

This document provides a detailed description of the main classes used in the application, including their properties and primary functions.

---

### Class: User
This is an object class representing a user in the system (Student, Guide, House Father, or Manager). It stores user identification and permission details.

**Table 1: User**

| Attribute/Action | Description |
| :--- | :--- |
| `private String name` | The user's full name. |
| `private String id` | The user's ID number (used as a unique identifier and login username). |
| `private int type` | User type: 1=Student, 2=Guide, 3=House Father, 4=Manager. |
| `private String year` | The user's school grade (e.g., 10th, 11th, etc.). |
| `public User(...)` | Constructor used to initialize a new user object. |
| `get` and `set` | Access and update methods for all user attributes. |

---

### Class: Problem
This is an object class representing a reported technical issue in the boarding school. it contains all necessary information for tracking and handling the issue.

**Table 2: Problem**

| Attribute/Action | Description |
| :--- | :--- |
| `private String typeP` | Type of problem (e.g., Electricity, Plumbing, Internet, Furniture, Other). |
| `private String id` | Unique document ID in the Firestore database. |
| `private int severity` | Urgency level (1=High, 2=Medium, 3=Low). |
| `private int roomP` | The room number where the problem is located. |
| `private String description` | Detailed description of the problem. |
| `private String imageBase64` | Base64 encoded image of the problem for display. |
| `private String reporterName` | Name of the user who reported the problem. |
| `private String year` | Grade level of the student who reported the problem. |
| `private String status` | Problem status: "active" for open issues or "fixed" for resolved ones. |
| `private long timestamp` | Timestamp indicating when the problem was reported. |
| `public Problem(...)` | Constructor used to create a new problem object. |
| `get` and `set` | Access and update methods for all problem attributes. |

---

### Class: Reminder
This class represents a reminder sent by a student regarding a problem that has not yet been addressed.

**Table 3: Reminder**

| Attribute/Action | Description |
| :--- | :--- |
| `private String problemId` | The ID of the problem the reminder refers to. |
| `private String problemType` | The type of the problem. |
| `private int roomP` | The room number associated with the problem. |
| `private String senderName` | The name of the user who sent the reminder. |
| `private long timestamp` | Timestamp of when the reminder was sent. |
| `public Reminder(...)` | Constructor to initialize a new reminder. |
| `get` and `set` | Access and update methods for all attributes. |

---

### Class: PermanentTask
This class represents a recurring task in the boarding school, created by the House Father.

**Table 4: PermanentTask**

| Attribute/Action | Description |
| :--- | :--- |
| `private String id` | Unique identifier in the database. |
| `private String title` | Task title (e.g., "Room Cleaning"). |
| `private String description` | Detailed description of the task requirements. |
| `public PermanentTask(...)` | Constructor for initializing a new permanent task. |
| `get` and `set` | Access and update methods for the attributes. |

---

### Class: LoginActivity
The main entry point activity responsible for user authentication.

**Table 5: LoginActivity**

| Attribute/Action | Description |
| :--- | :--- |
| `private void loginUser(String id)` | Searches for the user in Firestore by ID and performs login. |
| `private void saveUserToPrefs(...)` | Saves user details in SharedPreferences for automatic login. |
| `private void navigateToHome(...)` | Navigates to the appropriate home screen based on user type. |
| `private void requestAllPermissions()` | Requests Camera and Notification permissions from the user. |

---

### Class: StudentHomeActivity
The main dashboard for students, allowing them to report problems, view their issues, and see tasks.

**Table 6: StudentHomeActivity**

| Attribute/Action | Description |
| :--- | :--- |
| `btnRaiseProblem` | Button to navigate to the problem reporting screen. |
| `btnMyProblems` | Button to view problems reported by the current student. |
| `btnPermanentTask` | Button to view tasks assigned by the staff. |
| `btnLogout` | Clears saved user data and returns to the login screen. |

---

### Class: MyProblemsAdapter
A RecyclerView adapter responsible for displaying the student's personal reported problems.

**Table 7: MyProblemsAdapter**

| Attribute/Action | Description |
| :--- | :--- |
| `public void onBindViewHolder(...)` | Binds problem data to the UI and sets up the reminder button. |
| `private String formatElapsed(...)` | Helper method to calculate and format time elapsed since reporting. |
| `btnReminder.setOnClickListener` | Listener that sends a new reminder to Firestore and updates the UI. |

---

### Class: ReminderCheckReceiver
A BroadcastReceiver that sends push notifications to users if there are open problems.

**Table 8: ReminderCheckReceiver**

| Attribute/Action | Description |
| :--- | :--- |
| `public void onReceive(...)` | Triggered by the system to check the day of the week and send notifications. |
| `private void createNotificationChannel` | Creates a notification channel for Android O and above. |

---

### Class: ReportProblemActivity
Activity that allows users to fill out a form, take a photo, and report a new problem.

**Table 9: ReportProblemActivity**

| Attribute/Action | Description |
| :--- | :--- |
| `private void dispatchTakePictureIntent()` | Opens the camera to capture an image of the problem. |
| `private void saveProblemToFirestore()` | Validates input and uploads the problem details to the database. |

---

### Class: ProblemsAdapter
Generic adapter used to display lists of active or fixed problems for staff members.

**Table 10: ProblemsAdapter**

| Attribute/Action | Description |
| :--- | :--- |
| `btnStatus.setOnClickListener` | Allows staff to change the problem status from "active" to "fixed". |
| `ivProblemImage` | Displays the problem image decoded from Base64 string. |
