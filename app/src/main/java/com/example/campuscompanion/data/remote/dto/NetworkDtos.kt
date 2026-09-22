package com.example.campuscompanion.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "studentId") val studentId: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class LoginResponseDto(
    @Json(name = "token") val token: String,
    @Json(name = "student") val student: StudentDto
)

@JsonClass(generateAdapter = true)
data class StudentDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "department") val department: String,
    @Json(name = "program") val program: String,
    @Json(name = "semester") val semester: Int,
    @Json(name = "cgpa") val cgpa: Double,
    @Json(name = "avatarUrl") val avatarUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class CourseDto(
    @Json(name = "id") val id: String,
    @Json(name = "code") val code: String,
    @Json(name = "name") val name: String,
    @Json(name = "instructor") val instructor: String,
    @Json(name = "creditHours") val creditHours: Int,
    @Json(name = "attendancePercentage") val attendancePercentage: Int,
    @Json(name = "description") val description: String = "",
    @Json(name = "room") val room: String = "",
    @Json(name = "scheduleSummary") val scheduleSummary: String = ""
)

@JsonClass(generateAdapter = true)
data class AttendanceDto(
    @Json(name = "id") val id: String,
    @Json(name = "courseId") val courseId: String,
    @Json(name = "courseCode") val courseCode: String,
    @Json(name = "courseName") val courseName: String,
    @Json(name = "totalClasses") val totalClasses: Int,
    @Json(name = "attendedClasses") val attendedClasses: Int,
    @Json(name = "absentClasses") val absentClasses: Int,
    @Json(name = "percentage") val percentage: Int
)

@JsonClass(generateAdapter = true)
data class AssignmentDto(
    @Json(name = "id") val id: String,
    @Json(name = "courseId") val courseId: String,
    @Json(name = "courseCode") val courseCode: String,
    @Json(name = "courseName") val courseName: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "dueDate") val dueDate: String,
    @Json(name = "status") val status: String,
    @Json(name = "maxMarks") val maxMarks: Int = 100,
    @Json(name = "submissionNotes") val submissionNotes: String? = null,
    @Json(name = "submittedDate") val submittedDate: String? = null
)

@JsonClass(generateAdapter = true)
data class AnnouncementDto(
    @Json(name = "id") val id: String,
    @Json(name = "courseId") val courseId: String,
    @Json(name = "courseCode") val courseCode: String,
    @Json(name = "courseName") val courseName: String,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "date") val date: String,
    @Json(name = "author") val author: String,
    @Json(name = "isRead") val isRead: Boolean = false,
    @Json(name = "isImportant") val isImportant: Boolean = false
)

@JsonClass(generateAdapter = true)
data class TimetableDto(
    @Json(name = "id") val id: String,
    @Json(name = "day") val day: String,
    @Json(name = "courseId") val courseId: String,
    @Json(name = "courseCode") val courseCode: String,
    @Json(name = "courseName") val courseName: String,
    @Json(name = "startTime") val startTime: String,
    @Json(name = "endTime") val endTime: String,
    @Json(name = "room") val room: String,
    @Json(name = "instructor") val instructor: String
)
