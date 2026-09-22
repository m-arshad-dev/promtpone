package com.example.campuscompanion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.campuscompanion.data.repository.CampusRepository
import com.example.campuscompanion.data.repository.UserPreferencesRepository

class ViewModelFactory(
    private val repository: CampusRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val courseId: String? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, preferencesRepository) as T
            }
            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {
                DashboardViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CoursesViewModel::class.java) -> {
                CoursesViewModel(repository) as T
            }
            modelClass.isAssignableFrom(CourseDetailViewModel::class.java) -> {
                CourseDetailViewModel(repository, courseId ?: "") as T
            }
            modelClass.isAssignableFrom(AttendanceViewModel::class.java) -> {
                AttendanceViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AssignmentsViewModel::class.java) -> {
                AssignmentsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AnnouncementsViewModel::class.java) -> {
                AnnouncementsViewModel(repository) as T
            }
            modelClass.isAssignableFrom(TimetableViewModel::class.java) -> {
                TimetableViewModel(repository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(repository, preferencesRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
