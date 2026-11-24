# 🎓 Campus Placement Management System

A robust, console-based Java application designed to streamline the campus recruitment process. It allows administrators to post jobs and track applications, while students can register, view their profiles, and apply for eligible positions.

All data is persistently stored in local text files, ensuring records survive application restarts.

## ✨ Features

### 👨‍🎓 Student Portal
- **Registration**: Register with personal details, 8-semester academic marks, and unique technical skills.
- **Profile Viewer**: View personal details, calculated average percentage, and a deduplicated list of skills.
- **Job Board**: View all available job listings.
- **Apply**: Apply for jobs based on eligibility (Average % must meet the job's minimum requirement).
- **Application Tracking**: View the real-time status of all submitted applications (PENDING, SELECTED, REJECTED).

### 🛡️ Admin Portal
- **Student Directory**: View all registered students and their profiles.
- **Job Management**: Post new job openings with specific minimum percentage cutoffs.
- **Application Management**: View all student applications and update their status (Select/Reject).
- **Reporting**: Generate summary reports (total students, jobs, applications, and status breakdowns) and save them to a file.

## 🏗️ Architecture & OOP Concepts

This project heavily demonstrates core Java and Object-Oriented Programming principles:

- **Inheritance & Abstraction**: `User` is an abstract base class; `Admin` and `Student` extend it.
- **Polymorphism**: The `showProfile()` method is overridden by subclasses.
- **Encapsulation**: Model classes use `private` fields with getters/setters. Defensive copying is used for arrays (`marks.clone()`).
- **Arrays**: Used to store exactly 8 semester marks (`int[] marks`).
- **Collections**: `ArrayList` for dynamic record storage, `HashSet` to ensure skills are unique (e.g., "java" and "JAVA" become one).
- **Enums**: `ApplicationStatus` ensures type-safe status tracking.
- **File I/O**: `FileManager` handles UTF-8 file operations using `BufferedReader`/`BufferedWriter`.

## 📁 Project Structure

```text
Campus Placement Management System/
├── data/
│   ├── students.txt       # id|name|dept|marks(8)|skills
│   ├── jobs.txt           # id|company|title|minPercentage
│   ├── applications.txt   # id|studentId|jobId|status
│   └── report.txt         # Generated summary report
├── src/
│   └── com/placement/
│       ├── Main.java                 # Entry point and Console UI
│       ├── model/
│       │   ├── ApplicationStatus.java
│       │   ├── User.java
│       │   ├── Admin.java
│       │   ├── Student.java
│       │   ├── Job.java
│       │   └── JobApplication.java
│       ├── service/
│       │   └── PlacementService.java # Business logic
│       └── util/
│           └── FileManager.java      # File handling
└── README.md