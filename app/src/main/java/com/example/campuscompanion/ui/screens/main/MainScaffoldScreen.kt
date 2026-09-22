package com.example.campuscompanion.ui.screens.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.campuscompanion.ui.components.CampusBottomBar
import com.example.campuscompanion.ui.navigation.BottomNavTab
import com.example.campuscompanion.ui.screens.assignments.AssignmentsScreen
import com.example.campuscompanion.ui.screens.attendance.AttendanceScreen
import com.example.campuscompanion.ui.screens.courses.CoursesScreen
import com.example.campuscompanion.ui.screens.dashboard.DashboardScreen
import com.example.campuscompanion.ui.screens.profile.ProfileScreen
import com.example.campuscompanion.viewmodel.AssignmentsViewModel
import com.example.campuscompanion.viewmodel.AttendanceViewModel
import com.example.campuscompanion.viewmodel.CoursesViewModel
import com.example.campuscompanion.viewmodel.DashboardViewModel
import com.example.campuscompanion.viewmodel.ProfileViewModel

@Composable
fun MainScaffoldScreen(
    dashboardViewModel: DashboardViewModel,
    coursesViewModel: CoursesViewModel,
    attendanceViewModel: AttendanceViewModel,
    assignmentsViewModel: AssignmentsViewModel,
    profileViewModel: ProfileViewModel,
    onNavigateToCourseDetail: (String) -> Unit,
    onNavigateToAssignmentDetail: (String) -> Unit,
    onNavigateToAnnouncements: () -> Unit,
    onNavigateToAnnouncementDetail: (String) -> Unit,
    onNavigateToTimetable: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLoggedOut: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.Dashboard.route) }

    Scaffold(
        bottomBar = {
            CampusBottomBar(
                currentRoute = selectedTab,
                onTabSelected = { tab ->
                    selectedTab = tab.route
                }
            )
        },
        modifier = Modifier.testTag("main_scaffold_screen")
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = selectedTab, label = "tabCrossfade") { tabRoute ->
                when (tabRoute) {
                    BottomNavTab.Dashboard.route -> {
                        DashboardScreen(
                            viewModel = dashboardViewModel,
                            onNavigateToAttendance = { selectedTab = BottomNavTab.Attendance.route },
                            onNavigateToCourses = { selectedTab = BottomNavTab.Courses.route },
                            onNavigateToAssignments = { selectedTab = BottomNavTab.Assignments.route },
                            onNavigateToAssignmentDetail = onNavigateToAssignmentDetail,
                            onNavigateToAnnouncements = onNavigateToAnnouncements,
                            onNavigateToAnnouncementDetail = onNavigateToAnnouncementDetail,
                            onNavigateToTimetable = onNavigateToTimetable,
                            onNavigateToSettings = onNavigateToSettings
                        )
                    }
                    BottomNavTab.Courses.route -> {
                        CoursesScreen(
                            viewModel = coursesViewModel,
                            onCourseClick = onNavigateToCourseDetail
                        )
                    }
                    BottomNavTab.Attendance.route -> {
                        AttendanceScreen(
                            viewModel = attendanceViewModel,
                            onCourseClick = onNavigateToCourseDetail
                        )
                    }
                    BottomNavTab.Assignments.route -> {
                        AssignmentsScreen(
                            viewModel = assignmentsViewModel,
                            onAssignmentClick = onNavigateToAssignmentDetail
                        )
                    }
                    BottomNavTab.Profile.route -> {
                        ProfileScreen(
                            viewModel = profileViewModel,
                            onNavigateToSettings = onNavigateToSettings,
                            onLoggedOut = onLoggedOut
                        )
                    }
                }
            }
        }
    }
}
