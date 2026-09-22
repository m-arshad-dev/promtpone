package com.example.campuscompanion.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Main : Screen("main")

    // Sub-screens
    object CourseDetail : Screen("course/{courseId}") {
        fun createRoute(courseId: String) = "course/$courseId"
    }

    object AssignmentDetail : Screen("assignment/{assignmentId}") {
        fun createRoute(assignmentId: String) = "assignment/$assignmentId"
    }

    object AnnouncementDetail : Screen("announcement/{announcementId}") {
        fun createRoute(announcementId: String) = "announcement/$announcementId"
    }

    object Announcements : Screen("announcements")
    object Timetable : Screen("timetable")
    object Settings : Screen("settings")
}

enum class BottomNavTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    Dashboard(
        route = "tab_dashboard",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        testTag = "tab_home"
    ),

    Courses(
        route = "tab_courses",
        title = "Courses",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book,
        testTag = "tab_courses"
    ),

    Attendance(
        route = "tab_attendance",
        title = "Attendance",
        selectedIcon = Icons.Filled.CheckCircle,
        unselectedIcon = Icons.Outlined.CheckCircle,
        testTag = "tab_attendance"
    ),

    Assignments(
        route = "tab_assignments",
        title = "Assignments",
        selectedIcon = Icons.Filled.Assignment,
        unselectedIcon = Icons.Outlined.Assignment,
        testTag = "tab_assignments"
    ),

    Profile(
        route = "tab_profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        testTag = "tab_profile"
    );

    companion object {
        val items: List<BottomNavTab> get() = entries
    }
}
