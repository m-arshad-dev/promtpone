package com.example.campuscompanion.data.repository

import android.content.Context
import com.example.campuscompanion.data.local.dao.CampusDao
import com.example.campuscompanion.data.local.entity.toEntity
import com.example.campuscompanion.data.remote.api.CampusApiService
import com.example.campuscompanion.data.remote.dto.LoginRequestDto
import com.example.campuscompanion.data.remote.mock.MockCampusApiService
import com.example.campuscompanion.domain.model.Announcement
import com.example.campuscompanion.domain.model.Assignment
import com.example.campuscompanion.domain.model.AssignmentStatus
import com.example.campuscompanion.domain.model.AttendanceRecord
import com.example.campuscompanion.domain.model.Course
import com.example.campuscompanion.domain.model.Student
import com.example.campuscompanion.domain.model.TimetableEntry
import com.example.campuscompanion.domain.model.WeekDay
import com.example.campuscompanion.notifications.NotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CampusRepository(
    private val context: Context,
    private val dao: CampusDao,
    private val apiService: CampusApiService,
    private val preferencesRepository: UserPreferencesRepository
) {

    val isSimulatedOfflineFlow: Flow<Boolean> = preferencesRepository.simulateOfflineFlow

    suspend fun setSimulateOffline(offline: Boolean) {
        preferencesRepository.setSimulateOffline(offline)
        if (apiService is MockCampusApiService) {
            apiService.isSimulatedOffline = offline
        }
    }

    // --- Authentication ---
    suspend fun login(studentId: String, password: String): Result<Student> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequestDto(studentId.trim(), password))
            if (response.isSuccessful && response.body() != null) {
                val studentDto = response.body()!!.student
                val student = Student(
                    id = studentDto.id,
                    name = studentDto.name,
                    email = studentDto.email,
                    department = studentDto.department,
                    program = studentDto.program,
                    semester = studentDto.semester,
                    cgpa = studentDto.cgpa,
                    avatarUrl = studentDto.avatarUrl
                )
                dao.insertStudent(student.toEntity())
                preferencesRepository.setLoggedIn(true, student.id)
                // Seed initial data from API to Room
                refreshAllData()
                Result.success(student)
            } else {
                val err = response.errorBody()?.string() ?: "Login failed"
                Result.failure(Exception(if (err.contains("error")) "Invalid student ID or password" else err))
            }
        } catch (e: Exception) {
            // Check if student exists locally in Room cache for offline login
            val localStudent = dao.getFirstStudent().firstOrNull()?.toDomain()
            if (localStudent != null && (studentId == localStudent.id || studentId.contains(localStudent.name, ignoreCase = true))) {
                preferencesRepository.setLoggedIn(true, localStudent.id)
                Result.success(localStudent)
            } else {
                Result.failure(Exception("Network error: ${e.localizedMessage ?: "Unable to connect"}. Use demo: 2022-CS-001 / 123456"))
            }
        }
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        preferencesRepository.setLoggedIn(false)
    }

    // --- Student Profile ---
    fun getStudent(): Flow<Student?> = dao.getFirstStudent().map { it?.toDomain() }

    suspend fun updateStudentProfile(
        name: String,
        email: String,
        department: String,
        program: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val current = dao.getFirstStudent().firstOrNull()?.toDomain()
            if (current != null) {
                val updated = current.copy(
                    name = name,
                    email = email,
                    department = department,
                    program = program
                )
                dao.updateStudent(updated.toEntity())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Student record not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Courses ---
    fun getCourses(): Flow<List<Course>> = dao.getAllCourses().map { list ->
        list.map { it.toDomain() }
    }

    fun getCourseById(courseId: String): Flow<Course?> = dao.getCourseById(courseId).map { it?.toDomain() }

    // --- Attendance ---
    fun getAttendance(): Flow<List<AttendanceRecord>> = dao.getAllAttendance().map { list ->
        list.map { it.toDomain() }
    }

    fun getAttendanceByCourse(courseId: String): Flow<AttendanceRecord?> =
        dao.getAttendanceByCourse(courseId).map { it?.toDomain() }

    // --- Assignments ---
    fun getAssignments(): Flow<List<Assignment>> = dao.getAllAssignments().map { list ->
        list.map { it.toDomain() }
    }

    fun getAssignmentById(id: String): Flow<Assignment?> = dao.getAssignmentById(id).map { it?.toDomain() }

    fun getAssignmentsByCourse(courseId: String): Flow<List<Assignment>> =
        dao.getAssignmentsByCourse(courseId).map { list -> list.map { it.toDomain() } }

    suspend fun submitAssignment(id: String, notes: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = dao.getAssignmentById(id).firstOrNull()?.toDomain()
            if (existing != null) {
                val updated = existing.copy(
                    status = AssignmentStatus.SUBMITTED,
                    submissionNotes = notes,
                    submittedDate = "Just now"
                )
                dao.updateAssignment(updated.toEntity())
                Result.success(Unit)
            } else {
                Result.failure(Exception("Assignment not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Announcements ---
    fun getAnnouncements(): Flow<List<Announcement>> = dao.getAllAnnouncements().map { list ->
        list.map { it.toDomain() }
    }

    fun getAnnouncementById(id: String): Flow<Announcement?> = dao.getAnnouncementById(id).map { it?.toDomain() }

    fun getAnnouncementsByCourse(courseId: String): Flow<List<Announcement>> =
        dao.getAnnouncementsByCourse(courseId).map { list -> list.map { it.toDomain() } }

    suspend fun markAnnouncementAsRead(id: String) = withContext(Dispatchers.IO) {
        dao.markAnnouncementAsRead(id)
    }

    // --- Timetable ---
    fun getTimetable(): Flow<List<TimetableEntry>> = dao.getAllTimetable().map { list ->
        list.map { it.toDomain() }
    }

    fun getTimetableForDay(day: WeekDay): Flow<List<TimetableEntry>> =
        dao.getTimetableForDay(day.name).map { list -> list.map { it.toDomain() } }

    // --- Offline-First Sync / Refresh ---
    suspend fun refreshAllData(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch from Network / API
            val coursesResp = apiService.getCourses()
            val attendanceResp = apiService.getAttendance()
            val assignmentsResp = apiService.getAssignments()
            val announcementsResp = apiService.getAnnouncements()
            val timetableResp = apiService.getTimetable()
            val profileResp = apiService.getProfile()

            // 2. Persist to Room
            if (profileResp.isSuccessful && profileResp.body() != null) {
                val p = profileResp.body()!!
                dao.insertStudent(
                    Student(
                        id = p.id,
                        name = p.name,
                        email = p.email,
                        department = p.department,
                        program = p.program,
                        semester = p.semester,
                        cgpa = p.cgpa,
                        avatarUrl = p.avatarUrl
                    ).toEntity()
                )
            }

            if (coursesResp.isSuccessful && coursesResp.body() != null) {
                val courses = coursesResp.body()!!.map {
                    Course(
                        id = it.id,
                        code = it.code,
                        name = it.name,
                        instructor = it.instructor,
                        creditHours = it.creditHours,
                        attendancePercentage = it.attendancePercentage,
                        description = it.description,
                        room = it.room,
                        scheduleSummary = it.scheduleSummary
                    ).toEntity()
                }
                dao.insertCourses(courses)
            }

            if (attendanceResp.isSuccessful && attendanceResp.body() != null) {
                val records = attendanceResp.body()!!.map {
                    AttendanceRecord(
                        id = it.id,
                        courseId = it.courseId,
                        courseCode = it.courseCode,
                        courseName = it.courseName,
                        totalClasses = it.totalClasses,
                        attendedClasses = it.attendedClasses,
                        absentClasses = it.absentClasses,
                        percentage = it.percentage
                    ).toEntity()
                }
                dao.insertAttendance(records)
            }

            if (assignmentsResp.isSuccessful && assignmentsResp.body() != null) {
                // Keep submitted states if user already submitted locally
                val localMap = dao.getAllAssignments().firstOrNull()?.associateBy { it.id } ?: emptyMap()
                val assignments = assignmentsResp.body()!!.map { remote ->
                    val local = localMap[remote.id]
                    if (local != null && local.status == AssignmentStatus.SUBMITTED.name) {
                        local
                    } else {
                        Assignment(
                            id = remote.id,
                            courseId = remote.courseId,
                            courseCode = remote.courseCode,
                            courseName = remote.courseName,
                            title = remote.title,
                            description = remote.description,
                            dueDate = remote.dueDate,
                            status = runCatching { AssignmentStatus.valueOf(remote.status) }.getOrDefault(AssignmentStatus.PENDING),
                            maxMarks = remote.maxMarks,
                            submissionNotes = remote.submissionNotes,
                            submittedDate = remote.submittedDate
                        ).toEntity()
                    }
                }
                dao.insertAssignments(assignments)
            }

            if (announcementsResp.isSuccessful && announcementsResp.body() != null) {
                // Preserve local isRead state
                val localMap = dao.getAllAnnouncements().firstOrNull()?.associateBy { it.id } ?: emptyMap()
                val announcements = announcementsResp.body()!!.map { remote ->
                    val local = localMap[remote.id]
                    Announcement(
                        id = remote.id,
                        courseId = remote.courseId,
                        courseCode = remote.courseCode,
                        courseName = remote.courseName,
                        title = remote.title,
                        description = remote.description,
                        date = remote.date,
                        author = remote.author,
                        isRead = local?.isRead ?: remote.isRead,
                        isImportant = remote.isImportant
                    ).toEntity()
                }
                dao.insertAnnouncements(announcements)
            }

            if (timetableResp.isSuccessful && timetableResp.body() != null) {
                val timetable = timetableResp.body()!!.map {
                    TimetableEntry(
                        id = it.id,
                        day = runCatching { WeekDay.valueOf(it.day) }.getOrDefault(WeekDay.MONDAY),
                        courseId = it.courseId,
                        courseCode = it.courseCode,
                        courseName = it.courseName,
                        startTime = it.startTime,
                        endTime = it.endTime,
                        room = it.room,
                        instructor = it.instructor
                    ).toEntity()
                }
                dao.insertTimetable(timetable)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            // Offline fallback: room data remains fully intact!
            Result.failure(e)
        }
    }

    fun triggerAnnouncementNotification(announcement: Announcement) {
        NotificationHelper.showAnnouncementNotification(
            context = context,
            title = announcement.title,
            message = "${announcement.courseName}: ${announcement.description}",
            courseName = announcement.courseName
        )
    }
}
