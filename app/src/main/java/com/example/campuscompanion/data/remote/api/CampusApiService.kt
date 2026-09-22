package com.example.campuscompanion.data.remote.api

import com.example.campuscompanion.data.remote.dto.AnnouncementDto
import com.example.campuscompanion.data.remote.dto.AssignmentDto
import com.example.campuscompanion.data.remote.dto.AttendanceDto
import com.example.campuscompanion.data.remote.dto.CourseDto
import com.example.campuscompanion.data.remote.dto.LoginRequestDto
import com.example.campuscompanion.data.remote.dto.LoginResponseDto
import com.example.campuscompanion.data.remote.dto.StudentDto
import com.example.campuscompanion.data.remote.dto.TimetableDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CampusApiService {

    @POST("login")
    suspend fun login(@Body request: LoginRequestDto): Response<LoginResponseDto>

    @GET("profile")
    suspend fun getProfile(): Response<StudentDto>

    @GET("courses")
    suspend fun getCourses(): Response<List<CourseDto>>

    @GET("attendance")
    suspend fun getAttendance(): Response<List<AttendanceDto>>

    @GET("assignments")
    suspend fun getAssignments(): Response<List<AssignmentDto>>

    @GET("announcements")
    suspend fun getAnnouncements(): Response<List<AnnouncementDto>>

    @GET("timetable")
    suspend fun getTimetable(): Response<List<TimetableDto>>
}
