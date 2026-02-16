# Public Take-Home Exercise
Please create a local Git repository and commit your work as you go. Send it back to us once you are done. 

## Let's get started
For this exercise we'd like you to create a simple **to-do list** app.

It should be possible to:
- Have an overview of all tasks
- Add a new task (title and description with a 50 and 200 character limit respectively)
- Mark a task as completed
- Delete a task

When the user attempts to add a task, the app should simulate making a network call. Please add a random delay between 0.5 and 2.5 seconds before continuing. After the delay, the app should simulate that a network call can fail. It should have a success rate of 75%. Please add what you'd consider graceful error handling.

It's up to you to decide how you want to implement the user interface, navigation framework, state management, data storage, etc.

You're welcome to expand upon the scope of this exercise if you want to show off your skills / creativity.

Good luck & please feel free to reach out with any questions!


### Key Architectural Decisions

- MVVM for lifecycle-aware, state-driven UI
- StateFlow for reactive UI updates
- Hilt for dependency injection
- Room for persistent local storage
- KSP for annotation processing performance
- Clear separation of concerns across layers

---

# ✨ Features

## Task Overview

- Displays all tasks
- Active tasks appear first
- Completed tasks are sorted below active ones
- Most recently created tasks appear first
- Empty state UI

---

## Add Task

- Title limited to 50 characters
- Description limited to 200 characters
- Inline validation with character counters
- Error messages via string resources
- Loading indicator while saving

### Simulated Network Behavior

When adding a task:

- Random delay between **500ms and 2500ms**
- 75% success rate
- 25% simulated failure
- Graceful error handling via Snackbar

---

## Mark Task as Completed

- Checkbox toggles completion state
- Persisted automatically via Room
- UI updates reactively via Flow

---

## Delete Task

- Delete action per task
- Snackbar with **Undo**
- Undo restores the deleted task

---

# 🗄 Persistence

The app uses **Room** as a local database.

- Entity: `Task`
- DAO: `TaskDao`
- Database: `AppDatabase`
- Repository implementation: `RoomTaskRepository`

Data persists across app restarts.

---

# ⚙️ Tech Stack

- Kotlin 2.x (K2 mode enabled)
- Jetpack Compose (Material3)
- Hilt (Dependency Injection)
- Room (SQLite ORM)
- Coroutines
- StateFlow
- KSP (Kotlin Symbol Processing)
- Android Gradle Plugin 8.x

---

# 📂 Project Structure

<pre> ```text com.publicapp.takehome ├─ data │ ├─ model │ ├─ local │ └─ repository ├─ ui │ ├─ navigation │ ├─ tasklist │ └─ addtask ├─ di └─ TodoApplication.kt ``` </pre>
---

# 🚀 How to Run

1. Open the project in Android Studio (AGP 8.10+ recommended)
2. Sync Gradle
3. Run on emulator or device
4. Minimum SDK: 28

---

# 🧠 Design Considerations

- All UI strings are stored in resources
- Error messages are resource-based (no hardcoded strings)
- Repository abstraction allows easy swapping of data sources
- Sorting logic can be handled in ViewModel or directly in SQL
- Dependency injection is modularized via Hilt modules

---

# 👨‍💻 Author

Damian Peron  
Senior Android Engineer 

