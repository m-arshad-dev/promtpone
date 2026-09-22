package com.example.campuscompanion.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.campuscompanion.domain.model.Announcement
import com.example.campuscompanion.domain.model.Assignment
import com.example.campuscompanion.domain.model.AssignmentStatus
import com.example.campuscompanion.domain.model.AttendanceRecord
import com.example.campuscompanion.domain.model.Course
import com.example.campuscompanion.domain.model.Student
import com.example.campuscompanion.domain.model.TimetableEntry
import com.example.campuscompanion.domain.model.WeekDay

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val department: String,
    val program: String,
    val semester: Int,
    val cgpa: Double,
    val avatarUrl: String = ""
) {
    fun toDomain(): Student = Student(
        id = id,
        name = name,
        email = email,
        department = department,
        program = program,
        semester = semester,
        cgpa = cgpa,
        avatarUrl = avatarUrl
    )
}

fun Student.toEntity(): StudentEntity = StudentEntity(
    id = id,
    name = name,
    email = email,
    department = department,
    program = program,
    semester = semester,
    cgpa = cgpa,
    avatarUrl = avatarUrl
)

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey val id: String,
    val code: String,
    val name: String,
    val instructor: String,
    val creditHours: Int,
    val attendancePercentage: Int,
    val description: String,
    val room: String,
    val scheduleSummary: String
) {
    fun toDomain(): Course = Course(
        id = id,
        code = code,
        name = name,
        instructor = instructor,
        creditHours = creditHours,
        attendancePercentage = attendancePercentage,
        description = description,
        room = room,
        scheduleSummary = scheduleSummary
    )
}

fun Course.toEntity(): CourseEntity = CourseEntity(
    id = id,
    code = code,
    name = name,
    instructor = instructor,
    creditHours = creditHours,
    attendancePercentage = attendancePercentage,
    description = description,
    room = room,
    scheduleSummary = scheduleSummary
)

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val totalClasses: Int,
    val attendedClasses: Int,
    val absentClasses: Int,
    val percentage: Int
) {
    fun toDomain(): AttendanceRecord = AttendanceRecord(
        id = id,
        courseId = courseId,
        courseCode = courseCode,
        courseName = courseName,
        totalClasses = totalClasses,
        attendedClasses = attendedClasses,
        absentClasses = absentClasses,
        percentage = percentage,
        isBelowThreshold = percentage < 75
    )
}

fun AttendanceRecord.toEntity(): AttendanceEntity = AttendanceEntity(
    id = id,
    courseId = courseId,
    courseCode = courseCode,
    courseName = courseName,
    totalClasses = totalClasses,
    attendedClasses = attendedClasses,
    absentClasses = absentClasses,
    percentage = percentage
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val status: String,
    val maxMarks: Int = 100,
    val submissionNotes: String? = null,
    val submittedDate: String? = null
) {
    fun toDomain(): Assignment = Assignment(
        id = id,
        courseId = courseId,
        courseCode = courseCode,
        courseName = courseName,
        title = title,
        description = description,
        dueDate = dueDate,
        status = runCatching { AssignmentStatus.valueOf(status) }.getOrDefault(AssignmentStatus.PENDING),
        maxMarks = maxMarks,
        submissionNotes = submissionNotes,
        submittedDate = submittedDate
    )
}

fun Assignment.toEntity(): AssignmentEntity = AssignmentEntity(
    id = id,
    courseId = courseId,
    courseCode = courseCode,
    courseName = courseName,
    title = title,
    description = description,
    dueDate = dueDate,
    status = status.name,
    maxMarks = maxMarks,
    submissionNotes = submissionNotes,
    submittedDate = submittedDate
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val title: String,
    val description: String,
    val date: String,
    val author: String,
    val isRead: Boolean,
    val isImportant: Boolean
) {
    fun toDomain(): Announcement = Announcement(
        id = id,
        courseId = courseId,
        courseCode = courseCode,
        courseName = courseName,
        title = title,
        description = description,
        date = date,
        author = author,
        isRead = isRead,
        isImportant = isImportant
    )
}

fun Announcement.toEntity(): AnnouncementEntity = AnnouncementEntity(
    id = id,
    courseId = courseId,
    courseCode = courseCode,
    courseName = courseName,
    title = title,
    description = description,
    date = date,
    author = author,
    isRead = isRead,
    isImportant = isImportant
)

@Entity(tableName = "timetable")
data class TimetableEntity(
    @PrimaryKey val id: String,
    val day: String,
    val courseId: String,
    val courseCode: String,
    val courseName: String,
    val startTime: String,
    val endTime: String,
    val room: String,
    val instructor: String
) {
    fun toDomain(): TimetableEntry = TimetableEntry(
        id = id,
        day = runCatching { WeekDay.valueOf(day) }.getOrDefault(WeekDay.MONDAY),
        courseId = courseId,
        courseCode = courseCode,
        courseName = courseName,
        startTime = startTime,
        endTime = endTime,
        room = room,
        instructor = instructor
    )
}

fun TimetableEntry.toEntity(): TimetableEntity = TimetableEntity(
    id = id,
    day = day.name,
    courseId = courseId,
    courseCode = courseCode,
    courseName = courseName,
    startTime = startTime,
    endTime = endTime,
    room = room,
    instructor = instructor
)
