package com.example.campuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.campuscompanion.data.repository.CampusRepository
import com.example.campuscompanion.data.repository.ThemeMode
import com.example.campuscompanion.data.repository.UserPreferencesRepository
import com.example.campuscompanion.domain.model.Announcement
import com.example.campuscompanion.domain.model.Assignment
import com.example.campuscompanion.domain.model.AssignmentStatus
import com.example.campuscompanion.domain.model.AttendanceRecord
import com.example.campuscompanion.domain.model.Course
import com.example.campuscompanion.domain.model.Student
import com.example.campuscompanion.domain.model.TimetableEntry
import com.example.campuscompanion.domain.model.WeekDay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

// --- Auth ViewModel ---
sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val student: Student) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class AuthViewModel(
    private val repository: CampusRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = preferencesRepository.isLoggedInFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun login(studentId: String, password: String) {
        if (studentId.isBlank() || password.isBlank()) {
            _loginState.value = LoginUiState.Error("Please enter both Student ID and Password.")
            return
        }

        _loginState.value = LoginUiState.Loading
        viewModelScope.launch {
            val result = repository.login(studentId, password)
            result.onSuccess { student ->
                _loginState.value = LoginUiState.Success(student)
            }.onFailure { error ->
                _loginState.value = LoginUiState.Error(error.localizedMessage ?: "Authentication failed")
            }
        }
    }

    fun resetState() {
        _loginState.value = LoginUiState.Idle
    }
}

// --- Dashboard ViewModel ---
data class DashboardUiState(
    val student: Student? = null,
    val overallAttendance: Int = 0,
    val hasLowAttendanceWarning: Boolean = false,
    val todayClasses: List<TimetableEntry> = emptyList(),
    val upcomingAssignments: List<Assignment> = emptyList(),
    val latestAnnouncements: List<Announcement> = emptyList(),
    val unreadAnnouncementsCount: Int = 0,
    val isRefreshing: Boolean = false,
    val isSimulatedOffline: Boolean = false,
    val errorMessage: String? = null
)

private data class DashboardData(
    val student: Student?,
    val overallAttendance: Int,
    val hasLowAttendanceWarning: Boolean,
    val todayClasses: List<TimetableEntry>,
    val upcomingAssignments: List<Assignment>,
    val latestAnnouncements: List<Announcement>,
    val unreadAnnouncementsCount: Int
)

class DashboardViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)
    private val _errorMessage = MutableStateFlow<String?>(null)

    private val currentDayOfWeek: WeekDay = getCurrentWeekDay()

    private val dataFlow = combine(
        repository.getStudent(),
        repository.getAttendance(),
        repository.getTimetableForDay(currentDayOfWeek),
        repository.getAssignments(),
        repository.getAnnouncements()
    ) { student, attendance, todayClasses, assignments, announcements ->
        val overallAttendance = if (attendance.isNotEmpty()) {
            attendance.map { it.percentage }.average().toInt()
        } else {
            0
        }
        val pendingAssignments = assignments.filter { it.status == AssignmentStatus.PENDING }
        val unreadCount = announcements.count { !it.isRead }

        DashboardData(
            student = student,
            overallAttendance = overallAttendance,
            hasLowAttendanceWarning = overallAttendance < 75 && attendance.isNotEmpty(),
            todayClasses = todayClasses,
            upcomingAssignments = pendingAssignments.take(3),
            latestAnnouncements = announcements.take(3),
            unreadAnnouncementsCount = unreadCount
        )
    }

    private val statusFlow = combine(
        repository.isSimulatedOfflineFlow,
        _isRefreshing,
        _errorMessage
    ) { isOffline, refreshing, error ->
        Triple(isOffline, refreshing, error)
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        dataFlow,
        statusFlow
    ) { data, status ->
        DashboardUiState(
            student = data.student,
            overallAttendance = data.overallAttendance,
            hasLowAttendanceWarning = data.hasLowAttendanceWarning,
            todayClasses = data.todayClasses,
            upcomingAssignments = data.upcomingAssignments,
            latestAnnouncements = data.latestAnnouncements,
            unreadAnnouncementsCount = data.unreadAnnouncementsCount,
            isSimulatedOffline = status.first,
            isRefreshing = status.second,
            errorMessage = status.third
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _errorMessage.value = null
            val res = repository.refreshAllData()
            res.onFailure {
                _errorMessage.value = "Offline mode: Showing cached data (${it.localizedMessage})"
            }
            _isRefreshing.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    private fun getCurrentWeekDay(): WeekDay {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> WeekDay.MONDAY
            Calendar.TUESDAY -> WeekDay.TUESDAY
            Calendar.WEDNESDAY -> WeekDay.WEDNESDAY
            Calendar.THURSDAY -> WeekDay.THURSDAY
            Calendar.FRIDAY -> WeekDay.FRIDAY
            else -> WeekDay.MONDAY // Fallback for weekends
        }
    }
}

// --- Courses ViewModel ---
data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val errorMessage: String? = null
)

class CoursesViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    val coursesState: StateFlow<CoursesUiState> = repository.getCourses()
        .combine(repository.isSimulatedOfflineFlow) { courses, _ ->
            CoursesUiState(isLoading = false, courses = courses)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CoursesUiState(isLoading = true))

    fun refresh() {
        viewModelScope.launch {
            repository.refreshAllData()
        }
    }
}

// --- Course Detail ViewModel ---
data class CourseDetailUiState(
    val course: Course? = null,
    val assignments: List<Assignment> = emptyList(),
    val attendance: AttendanceRecord? = null,
    val timetable: List<TimetableEntry> = emptyList(),
    val selectedTab: Int = 0
)

class CourseDetailViewModel(
    private val repository: CampusRepository,
    val courseId: String
) : ViewModel() {

    private val _selectedTab = MutableStateFlow(0)

    val uiState: StateFlow<CourseDetailUiState> = combine(
        repository.getCourseById(courseId),
        repository.getAssignmentsByCourse(courseId),
        repository.getAttendanceByCourse(courseId),
        repository.getTimetable(),
        _selectedTab
    ) { course, assignments, attendance, allTimetable, tab ->
        val courseTimetable = allTimetable.filter { it.courseId == courseId }
        CourseDetailUiState(
            course = course,
            assignments = assignments,
            attendance = attendance,
            timetable = courseTimetable,
            selectedTab = tab
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CourseDetailUiState())

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }
}

// --- Attendance ViewModel ---
data class AttendanceUiState(
    val records: List<AttendanceRecord> = emptyList(),
    val overallPercentage: Int = 0,
    val totalClasses: Int = 0,
    val totalAttended: Int = 0,
    val totalAbsent: Int = 0,
    val lowAttendanceCount: Int = 0
)

class AttendanceViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    val uiState: StateFlow<AttendanceUiState> = repository.getAttendance()
        .combine(repository.isSimulatedOfflineFlow) { records, _ ->
            val overall = if (records.isNotEmpty()) records.map { it.percentage }.average().toInt() else 0
            val total = records.sumOf { it.totalClasses }
            val attended = records.sumOf { it.attendedClasses }
            val absent = records.sumOf { it.absentClasses }
            val lowCount = records.count { it.isBelowThreshold }

            AttendanceUiState(
                records = records,
                overallPercentage = overall,
                totalClasses = total,
                totalAttended = attended,
                totalAbsent = absent,
                lowAttendanceCount = lowCount
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AttendanceUiState())
}

// --- Assignments ViewModel ---
enum class AssignmentFilter { ALL, PENDING, SUBMITTED, OVERDUE }

data class AssignmentsUiState(
    val assignments: List<Assignment> = emptyList(),
    val filteredAssignments: List<Assignment> = emptyList(),
    val currentFilter: AssignmentFilter = AssignmentFilter.ALL,
    val selectedAssignment: Assignment? = null
)

class AssignmentsViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _currentFilter = MutableStateFlow(AssignmentFilter.ALL)
    private val _selectedAssignment = MutableStateFlow<Assignment?>(null)

    val uiState: StateFlow<AssignmentsUiState> = combine(
        repository.getAssignments(),
        _currentFilter,
        _selectedAssignment
    ) { list, filter, selected ->
        val filtered = when (filter) {
            AssignmentFilter.ALL -> list
            AssignmentFilter.PENDING -> list.filter { it.status == AssignmentStatus.PENDING }
            AssignmentFilter.SUBMITTED -> list.filter { it.status == AssignmentStatus.SUBMITTED }
            AssignmentFilter.OVERDUE -> list.filter { it.status == AssignmentStatus.OVERDUE }
        }
        AssignmentsUiState(
            assignments = list,
            filteredAssignments = filtered,
            currentFilter = filter,
            selectedAssignment = selected
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AssignmentsUiState())

    fun setFilter(filter: AssignmentFilter) {
        _currentFilter.value = filter
    }

    fun selectAssignment(assignment: Assignment?) {
        _selectedAssignment.value = assignment
    }

    fun submitAssignment(id: String, notes: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.submitAssignment(id, notes)
            onComplete()
        }
    }
}

// --- Announcements ViewModel ---
data class AnnouncementsUiState(
    val announcements: List<Announcement> = emptyList(),
    val unreadCount: Int = 0
)

class AnnouncementsViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    val uiState: StateFlow<AnnouncementsUiState> = repository.getAnnouncements()
        .combine(repository.isSimulatedOfflineFlow) { list, _ ->
            AnnouncementsUiState(
                announcements = list,
                unreadCount = list.count { !it.isRead }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AnnouncementsUiState())

    fun markAsRead(id: String) {
        viewModelScope.launch {
            repository.markAnnouncementAsRead(id)
        }
    }

    fun sendTestNotification(announcement: Announcement) {
        repository.triggerAnnouncementNotification(announcement)
    }
}

// --- Timetable ViewModel ---
data class TimetableUiState(
    val selectedDay: WeekDay = WeekDay.MONDAY,
    val classes: List<TimetableEntry> = emptyList(),
    val allDayClassesCount: Map<WeekDay, Int> = emptyMap()
)

class TimetableViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(WeekDay.MONDAY)

    val uiState: StateFlow<TimetableUiState> = combine(
        repository.getTimetable(),
        _selectedDay
    ) { allEntries, day ->
        val filtered = allEntries.filter { it.day == day }
        val counts = WeekDay.entries.associateWith { d -> allEntries.count { it.day == d } }
        TimetableUiState(
            selectedDay = day,
            classes = filtered,
            allDayClassesCount = counts
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TimetableUiState())

    fun selectDay(day: WeekDay) {
        _selectedDay.value = day
    }
}

// --- Profile ViewModel ---
data class ProfileUiState(
    val student: Student? = null,
    val isEditing: Boolean = false,
    val isSavedSuccess: Boolean = false
)

class ProfileViewModel(
    private val repository: CampusRepository
) : ViewModel() {

    private val _isEditing = MutableStateFlow(false)

    val uiState: StateFlow<ProfileUiState> = combine(
        repository.getStudent(),
        _isEditing
    ) { student, isEditing ->
        ProfileUiState(student = student, isEditing = isEditing)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    fun setEditing(editing: Boolean) {
        _isEditing.value = editing
    }

    fun updateProfile(name: String, email: String, department: String, program: String, onDone: () -> Unit) {
        viewModelScope.launch {
            repository.updateStudentProfile(name, email, department, program)
            _isEditing.value = false
            onDone()
        }
    }

    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onLoggedOut()
        }
    }
}

// --- Settings ViewModel ---
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val notificationsEnabled: Boolean = true,
    val isSimulatedOffline: Boolean = false
)

class SettingsViewModel(
    private val repository: CampusRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        preferencesRepository.themeModeFlow,
        preferencesRepository.notificationsEnabledFlow,
        preferencesRepository.simulateOfflineFlow
    ) { mode, notifEnabled, offline ->
        SettingsUiState(
            themeMode = mode,
            notificationsEnabled = notifEnabled,
            isSimulatedOffline = offline
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsUiState())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
        }
    }

    fun setSimulateOffline(offline: Boolean) {
        viewModelScope.launch {
            repository.setSimulateOffline(offline)
        }
    }

    fun triggerTestNotification() {
        val testAnnouncement = Announcement(
            id = "test-${System.currentTimeMillis()}",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            title = "Campus Companion Announcement",
            description = "Your attendance and assignments have been synchronized successfully!",
            date = "Now",
            author = "Academic Portal",
            isRead = false,
            isImportant = true
        )
        repository.triggerAnnouncementNotification(testAnnouncement)
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            onDone()
        }
    }
}
