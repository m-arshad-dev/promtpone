package com.example.campuscompanion.domain.model

enum class AssignmentStatus {
    PENDING,
    SUBMITTED,
    OVERDUE
}

enum class WeekDay(val displayName: String, val shortName: String) {
    MONDAY("Monday", "Mon"),
    TUESDAY("Tuesday", "Tue"),
    WEDNESDAY("Wednesday", "Wed"),
    THURSDAY("Thursday", "Thu"),
    FRIDAY("Friday", "Fri")
}

data class Student(
    val id: String,
    val name: String,
    val email: String,
    val department: String,
    val program: String,
    val semester: Int,
    val cgpa: Double,
    val avatarUrl: String = ""
)

data class Course(
    val id: String,
    val code: String,
    val name: String,
    val instructor: String,
    val creditHours: Int,
    val attendancePercentage: Int,
    val description: String = "",
    val room: String = "",
    val scheduleSummary: String = ""
)

data class AttendanceRecord(
    val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val totalClasses: Int,
    val attendedClasses: Int,
    val absentClasses: Int,
    val percentage: Int,
    val isBelowThreshold: Boolean = percentage < 75
)

data class Assignment(
    val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val status: AssignmentStatus,
    val maxMarks: Int = 100,
    val submissionNotes: String? = null,
    val submittedDate: String? = null
)

data class Announcement(
    val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val title: String,
    val description: String,
    val date: String,
    val author: String,
    val isRead: Boolean = false,
    val isImportant: Boolean = false
)

data class TimetableEntry(
    val id: String,
    val day: WeekDay,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val instructor: String
)
