package com.example.campuscompanion.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.campuscompanion.data.repository.CampusRepository
import com.example.campuscompanion.data.repository.UserPreferencesRepository
import com.example.campuscompanion.ui.screens.announcements.AnnouncementDetailScreen
import com.example.campuscompanion.ui.screens.announcements.AnnouncementsScreen
import com.example.campuscompanion.ui.screens.assignments.AssignmentDetailScreen
import com.example.campuscompanion.ui.screens.courses.CourseDetailScreen
import com.example.campuscompanion.ui.screens.login.LoginScreen
import com.example.campuscompanion.ui.screens.main.MainScaffoldScreen
import com.example.campuscompanion.ui.screens.settings.SettingsScreen
import com.example.campuscompanion.ui.screens.splash.SplashScreen
import com.example.campuscompanion.ui.screens.timetable.TimetableScreen
import com.example.campuscompanion.viewmodel.AnnouncementsViewModel
import com.example.campuscompanion.viewmodel.AssignmentsViewModel
import com.example.campuscompanion.viewmodel.AttendanceViewModel
import com.example.campuscompanion.viewmodel.AuthViewModel
import com.example.campuscompanion.viewmodel.CourseDetailViewModel
import com.example.campuscompanion.viewmodel.CoursesViewModel
import com.example.campuscompanion.viewmodel.DashboardViewModel
import com.example.campuscompanion.viewmodel.ProfileViewModel
import com.example.campuscompanion.viewmodel.SettingsViewModel
import com.example.campuscompanion.viewmodel.TimetableViewModel
import com.example.campuscompanion.viewmodel.ViewModelFactory

@Composable
fun AppNavigation(
    repository: CampusRepository,
    preferencesRepository: UserPreferencesRepository,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val factory = ViewModelFactory(repository, preferencesRepository)

    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
    val coursesViewModel: CoursesViewModel = viewModel(factory = factory)
    val attendanceViewModel: AttendanceViewModel = viewModel(factory = factory)
    val assignmentsViewModel: AssignmentsViewModel = viewModel(factory = factory)
    val announcementsViewModel: AnnouncementsViewModel = viewModel(factory = factory)
    val timetableViewModel: TimetableViewModel = viewModel(factory = factory)
    val profileViewModel: ProfileViewModel = viewModel(factory = factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        // Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                isLoggedIn = isLoggedIn,
                onNavigateNext = { loggedIn ->
                    val destination = if (loggedIn) Screen.Main.route else Screen.Login.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Main App Scaffold (Tabs: Home, Courses, Attendance, Assignments, Profile)
        composable(Screen.Main.route) {
            MainScaffoldScreen(
                dashboardViewModel = dashboardViewModel,
                coursesViewModel = coursesViewModel,
                attendanceViewModel = attendanceViewModel,
                assignmentsViewModel = assignmentsViewModel,
                profileViewModel = profileViewModel,
                onNavigateToCourseDetail = { courseId ->
                    navController.navigate(Screen.CourseDetail.createRoute(courseId))
                },
                onNavigateToAssignmentDetail = { assignmentId ->
                    navController.navigate(Screen.AssignmentDetail.createRoute(assignmentId))
                },
                onNavigateToAnnouncements = {
                    navController.navigate(Screen.Announcements.route)
                },
                onNavigateToAnnouncementDetail = { announcementId ->
                    navController.navigate(Screen.AnnouncementDetail.createRoute(announcementId))
                },
                onNavigateToTimetable = {
                    navController.navigate(Screen.Timetable.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        // Course Detail Screen
        composable(
            route = Screen.CourseDetail.route,
            arguments = listOf(navArgument("courseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId") ?: ""
            val courseDetailViewModel: CourseDetailViewModel = viewModel(
                key = "course_$courseId",
                factory = ViewModelFactory(repository, preferencesRepository, courseId)
            )
            CourseDetailScreen(
                viewModel = courseDetailViewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAssignmentDetail = { assignmentId ->
                    navController.navigate(Screen.AssignmentDetail.createRoute(assignmentId))
                }
            )
        }

        // Assignment Detail Screen
        composable(
            route = Screen.AssignmentDetail.route,
            arguments = listOf(navArgument("assignmentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val assignmentId = backStackEntry.arguments?.getString("assignmentId") ?: ""
            AssignmentDetailScreen(
                assignmentId = assignmentId,
                viewModel = assignmentsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Announcements List Screen
        composable(Screen.Announcements.route) {
            AnnouncementsScreen(
                viewModel = announcementsViewModel,
                onAnnouncementClick = { announcementId ->
                    navController.navigate(Screen.AnnouncementDetail.createRoute(announcementId))
                }
            )
        }

        // Announcement Detail Screen
        composable(
            route = Screen.AnnouncementDetail.route,
            arguments = listOf(navArgument("announcementId") { type = NavType.StringType })
        ) { backStackEntry ->
            val announcementId = backStackEntry.arguments?.getString("announcementId") ?: ""
            AnnouncementDetailScreen(
                announcementId = announcementId,
                viewModel = announcementsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Weekly Timetable Screen
        composable(Screen.Timetable.route) {
            TimetableScreen(
                viewModel = timetableViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
