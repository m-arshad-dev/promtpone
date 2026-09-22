package com.example.campuscompanion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.campuscompanion.data.local.entity.AnnouncementEntity
import com.example.campuscompanion.data.local.entity.AssignmentEntity
import com.example.campuscompanion.data.local.entity.AttendanceEntity
import com.example.campuscompanion.data.local.entity.CourseEntity
import com.example.campuscompanion.data.local.entity.StudentEntity
import com.example.campuscompanion.data.local.entity.TimetableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampusDao {

    // --- Student ---
    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    fun getStudent(id: String): Flow<StudentEntity?>

    @Query("SELECT * FROM students LIMIT 1")
    fun getFirstStudent(): Flow<StudentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    // --- Courses ---
    @Query("SELECT * FROM courses ORDER BY code ASC")
    fun getAllCourses(): Flow<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    fun getCourseById(id: String): Flow<CourseEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    // --- Attendance ---
    @Query("SELECT * FROM attendance ORDER BY courseCode ASC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance WHERE courseId = :courseId LIMIT 1")
    fun getAttendanceByCourse(courseId: String): Flow<AttendanceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(records: List<AttendanceEntity>)

    // --- Assignments ---
    @Query("SELECT * FROM assignments ORDER BY dueDate ASC")
    fun getAllAssignments(): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE id = :id LIMIT 1")
    fun getAssignmentById(id: String): Flow<AssignmentEntity?>

    @Query("SELECT * FROM assignments WHERE courseId = :courseId ORDER BY dueDate ASC")
    fun getAssignmentsByCourse(courseId: String): Flow<List<AssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<AssignmentEntity>)

    @Update
    suspend fun updateAssignment(assignment: AssignmentEntity)

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY date DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE id = :id LIMIT 1")
    fun getAnnouncementById(id: String): Flow<AnnouncementEntity?>

    @Query("SELECT * FROM announcements WHERE courseId = :courseId ORDER BY date DESC")
    fun getAnnouncementsByCourse(courseId: String): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    @Query("UPDATE announcements SET isRead = 1 WHERE id = :id")
    suspend fun markAnnouncementAsRead(id: String)

    // --- Timetable ---
    @Query("SELECT * FROM timetable ORDER BY startTime ASC")
    fun getAllTimetable(): Flow<List<TimetableEntity>>

    @Query("SELECT * FROM timetable WHERE day = :day ORDER BY startTime ASC")
    fun getTimetableForDay(day: String): Flow<List<TimetableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(entries: List<TimetableEntity>)

    // --- Clear tables helper ---
    @Query("DELETE FROM students")
    suspend fun clearStudents()

    @Query("DELETE FROM courses")
    suspend fun clearCourses()

    @Query("DELETE FROM attendance")
    suspend fun clearAttendance()

    @Query("DELETE FROM assignments")
    suspend fun clearAssignments()

    @Query("DELETE FROM announcements")
    suspend fun clearAnnouncements()

    @Query("DELETE FROM timetable")
    suspend fun clearTimetable()
}
