package com.example.campuscompanion.data.remote.mock

import com.example.campuscompanion.data.remote.api.CampusApiService
import com.example.campuscompanion.data.remote.dto.AnnouncementDto
import com.example.campuscompanion.data.remote.dto.AssignmentDto
import com.example.campuscompanion.data.remote.dto.AttendanceDto
import com.example.campuscompanion.data.remote.dto.CourseDto
import com.example.campuscompanion.data.remote.dto.LoginRequestDto
import com.example.campuscompanion.data.remote.dto.LoginResponseDto
import com.example.campuscompanion.data.remote.dto.StudentDto
import com.example.campuscompanion.data.remote.dto.TimetableDto
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.io.IOException

class MockCampusApiService : CampusApiService {

    var isSimulatedOffline: Boolean = false
    var shouldFail: Boolean = false

    private val student = StudentDto(
        id = "2022-CS-001",
        name = "Arshad",
        email = "arshad.cs@university.edu",
        department = "Computer Science",
        program = "BS Computer Science",
        semester = 8,
        cgpa = 3.78,
        avatarUrl = ""
    )

    private val courses = listOf(
        CourseDto(
            id = "c1",
            code = "CS-401",
            name = "Mobile Application Development",
            instructor = "Dr. Ahmed",
            creditHours = 3,
            attendancePercentage = 86,
            description = "Comprehensive modern Android development with Kotlin, Jetpack Compose, MVVM architecture, Room database, Coroutines, and REST APIs.",
            room = "Lab 3, CS Block",
            scheduleSummary = "Mon, Wed (09:00 - 10:30)"
        ),
        CourseDto(
            id = "c2",
            code = "CS-402",
            name = "Software Engineering",
            instructor = "Prof. Sarah Khan",
            creditHours = 3,
            attendancePercentage = 91,
            description = "Advanced software engineering methodologies, Agile Scrum practices, system modeling with UML, testing architectures, and CI/CD pipelines.",
            room = "Room 204, Academic Complex",
            scheduleSummary = "Mon, Wed (11:00 - 12:30)"
        ),
        CourseDto(
            id = "c3",
            code = "CS-403",
            name = "Compiler Construction",
            instructor = "Dr. Usman Tariq",
            creditHours = 3,
            attendancePercentage = 72,
            description = "Principles of programming language translation: lexical analysis, LR parsing, syntax-directed translation, symbol tables, and code generation.",
            room = "Lecture Hall 1",
            scheduleSummary = "Tue, Thu (14:00 - 15:30)"
        ),
        CourseDto(
            id = "c4",
            code = "CS-404",
            name = "Database Systems II",
            instructor = "Dr. Fatima Noor",
            creditHours = 3,
            attendancePercentage = 88,
            description = "Distributed database architectures, indexing techniques (B-trees, hash indices), concurrency control, recovery schemes, and NoSQL storage.",
            room = "Lab 1, CS Block",
            scheduleSummary = "Tue, Thu (10:00 - 11:30)"
        ),
        CourseDto(
            id = "c5",
            code = "CS-405",
            name = "Artificial Intelligence",
            instructor = "Prof. Bilal Raza",
            creditHours = 4,
            attendancePercentage = 80,
            description = "Foundations of modern AI: informed heuristic search, game playing algorithms, constraint satisfaction, probabilistic reasoning, and neural networks.",
            room = "Auditorium B",
            scheduleSummary = "Wed, Fri (14:00 - 16:00)"
        )
    )

    private val attendance = listOf(
        AttendanceDto(
            id = "att-1",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            totalClasses = 28,
            attendedClasses = 24,
            absentClasses = 4,
            percentage = 86
        ),
        AttendanceDto(
            id = "att-2",
            courseId = "c2",
            courseCode = "CS-402",
            courseName = "Software Engineering",
            totalClasses = 32,
            attendedClasses = 29,
            absentClasses = 3,
            percentage = 91
        ),
        AttendanceDto(
            id = "att-3",
            courseId = "c3",
            courseCode = "CS-403",
            courseName = "Compiler Construction",
            totalClasses = 25,
            attendedClasses = 18,
            absentClasses = 7,
            percentage = 72
        ),
        AttendanceDto(
            id = "att-4",
            courseId = "c4",
            courseCode = "CS-404",
            courseName = "Database Systems II",
            totalClasses = 26,
            attendedClasses = 23,
            absentClasses = 3,
            percentage = 88
        ),
        AttendanceDto(
            id = "att-5",
            courseId = "c5",
            courseCode = "CS-405",
            courseName = "Artificial Intelligence",
            totalClasses = 30,
            attendedClasses = 24,
            absentClasses = 6,
            percentage = 80
        )
    )

    private val assignments = listOf(
        AssignmentDto(
            id = "asg-1",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            title = "Android Project: Compose UI",
            description = "Build a complete student campus application using Jetpack Compose, Room persistence, and MVVM architecture. Implement dark mode and clean Material 3 design.",
            dueDate = "Tomorrow, 11:59 PM",
            status = "PENDING",
            maxMarks = 100
        ),
        AssignmentDto(
            id = "asg-2",
            courseId = "c3",
            courseCode = "CS-403",
            courseName = "Compiler Construction",
            title = "Compiler Lexer & Parser Implementation",
            description = "Implement an LL(1) syntax analyzer and lexical token scanner in Kotlin or C++ for the mini-language specification provided in class.",
            dueDate = "Friday, 5:00 PM",
            status = "PENDING",
            maxMarks = 100
        ),
        AssignmentDto(
            id = "asg-3",
            courseId = "c2",
            courseCode = "CS-402",
            courseName = "Software Engineering",
            title = "Software Requirements Specification (SRS)",
            description = "Draft a formal IEEE 830 compliant SRS document for your semester capstone project, including use cases, mockups, and non-functional requirements.",
            dueDate = "In 4 days",
            status = "PENDING",
            maxMarks = 50
        ),
        AssignmentDto(
            id = "asg-4",
            courseId = "c4",
            courseCode = "CS-404",
            courseName = "Database Systems II",
            title = "SQL Indexing & Query Optimization",
            description = "Analyze query execution plans on a dataset of 500,000 records. Create clustered and non-clustered indices to optimize response times by at least 60%.",
            dueDate = "Completed",
            status = "SUBMITTED",
            maxMarks = 50,
            submissionNotes = "Submitted query profiles and PDF benchmarks report.",
            submittedDate = "Sep 18, 2026"
        ),
        AssignmentDto(
            id = "asg-5",
            courseId = "c5",
            courseCode = "CS-405",
            courseName = "Artificial Intelligence",
            title = "A* Heuristic Search Implementation",
            description = "Program the A* search algorithm to solve the 15-puzzle grid with Manhattan distance and Euclidean distance heuristics.",
            dueDate = "Completed",
            status = "SUBMITTED",
            maxMarks = 100,
            submissionNotes = "Source code repo link submitted with test results.",
            submittedDate = "Sep 15, 2026"
        ),
        AssignmentDto(
            id = "asg-6",
            courseId = "c2",
            courseCode = "CS-402",
            courseName = "Software Engineering",
            title = "Design Patterns Case Study",
            description = "Case study analyzing Repository, Factory, and Observer design patterns in modern enterprise mobile architectures.",
            dueDate = "Completed",
            status = "SUBMITTED",
            maxMarks = 50,
            submissionNotes = "Case study document uploaded.",
            submittedDate = "Sep 10, 2026"
        ),
        AssignmentDto(
            id = "asg-7",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            title = "Mobile UI Wireframes & User Journey",
            description = "Figma wireframe prototypes and user flow diagrams for university portal application.",
            dueDate = "Completed",
            status = "SUBMITTED",
            maxMarks = 50,
            submissionNotes = "Figma link attached.",
            submittedDate = "Sep 05, 2026"
        ),
        AssignmentDto(
            id = "asg-8",
            courseId = "c3",
            courseCode = "CS-403",
            courseName = "Compiler Construction",
            title = "Formal Grammar & Automata Exercise",
            description = "Mathematical conversion of regular expressions to Non-deterministic Finite Automata (NFA) and deterministic minimization.",
            dueDate = "Past Due",
            status = "OVERDUE",
            maxMarks = 50
        )
    )

    private val announcements = listOf(
        AnnouncementDto(
            id = "anc-1",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            title = "New assignment instructions have been posted",
            description = "Please review the Android Project rubric uploaded to the portal. Remember to follow Material 3 design and offline-first Room repository guidelines.",
            date = "Today, 08:30 AM",
            author = "Dr. Ahmed",
            isRead = false,
            isImportant = true
        ),
        AnnouncementDto(
            id = "anc-2",
            courseId = "all",
            courseCode = "ACAD",
            courseName = "Academic Office",
            title = "Midterm Exam Schedule Announced",
            description = "The midterm examination schedule for Semester 8 has been finalized. Exams commence from October 12th. Please verify your exam venues and timing.",
            date = "Yesterday",
            author = "Dean of Academic Affairs",
            isRead = false,
            isImportant = true
        ),
        AnnouncementDto(
            id = "anc-3",
            courseId = "c3",
            courseCode = "CS-403",
            courseName = "Compiler Construction",
            title = "Compiler Lab Session Rescheduled to Thursday",
            description = "The Tuesday practical lab for compiler code generation will be held on Thursday at 3:00 PM in Lab 2 due to department server maintenance.",
            date = "Sep 20, 2026",
            author = "Dr. Usman Tariq",
            isRead = true,
            isImportant = false
        ),
        AnnouncementDto(
            id = "anc-4",
            courseId = "all",
            courseCode = "STUDENT",
            courseName = "Student Council",
            title = "Annual University Hackathon 2026 Registration Open",
            description = "Join over 300 student teams for the 48-hour Campus Innovators Hackathon. Exciting prizes and mentorship from industry tech leaders.",
            date = "Sep 19, 2026",
            author = "Student Affairs Council",
            isRead = true,
            isImportant = false
        ),
        AnnouncementDto(
            id = "anc-5",
            courseId = "c2",
            courseCode = "CS-402",
            courseName = "Software Engineering",
            title = "Project Milestone 1 Architecture Review",
            description = "Each project group will present their high-level architectural diagram and repository structure during next Monday's tutorial session.",
            date = "Sep 17, 2026",
            author = "Prof. Sarah Khan",
            isRead = true,
            isImportant = false
        ),
        AnnouncementDto(
            id = "anc-6",
            courseId = "all",
            courseCode = "LIB",
            courseName = "Campus Library",
            title = "Library Extended Hours During Study Weeks",
            description = "The Central University Library will remain open 24/7 starting next Monday to assist students during midterm prep. Quiet study pods can be booked online.",
            date = "Sep 14, 2026",
            author = "Chief Librarian",
            isRead = true,
            isImportant = false
        )
    )

    private val timetable = listOf(
        // Monday
        TimetableDto(
            id = "tt-1",
            day = "MONDAY",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            startTime = "09:00",
            endTime = "10:30",
            room = "Lab 3, CS Block",
            instructor = "Dr. Ahmed"
        ),
        TimetableDto(
            id = "tt-2",
            day = "MONDAY",
            courseId = "c2",
            courseCode = "CS-402",
            courseName = "Software Engineering",
            startTime = "11:00",
            endTime = "12:30",
            room = "Room 204, Academic Complex",
            instructor = "Prof. Sarah Khan"
        ),
        // Tuesday
        TimetableDto(
            id = "tt-3",
            day = "TUESDAY",
            courseId = "c4",
            courseCode = "CS-404",
            courseName = "Database Systems II",
            startTime = "10:00",
            endTime = "11:30",
            room = "Lab 1, CS Block",
            instructor = "Dr. Fatima Noor"
        ),
        TimetableDto(
            id = "tt-4",
            day = "TUESDAY",
            courseId = "c3",
            courseCode = "CS-403",
            courseName = "Compiler Construction",
            startTime = "14:00",
            endTime = "15:30",
            room = "Lecture Hall 1",
            instructor = "Dr. Usman Tariq"
        ),
        // Wednesday
        TimetableDto(
            id = "tt-5",
            day = "WEDNESDAY",
            courseId = "c1",
            courseCode = "CS-401",
            courseName = "Mobile Application Development",
            startTime = "09:00",
            endTime = "10:30",
            room = "Lab 3, CS Block",
            instructor = "Dr. Ahmed"
        ),
        TimetableDto(
            id = "tt-6",
            day = "WEDNESDAY",
            courseId = "c2",
            courseCode = "CS-402",
            courseName = "Software Engineering",
            startTime = "11:00",
            endTime = "12:30",
            room = "Room 204, Academic Complex",
            instructor = "Prof. Sarah Khan"
        ),
        TimetableDto(
            id = "tt-7",
            day = "WEDNESDAY",
            courseId = "c5",
            courseCode = "CS-405",
            courseName = "Artificial Intelligence",
            startTime = "14:00",
            endTime = "16:00",
            room = "Auditorium B",
            instructor = "Prof. Bilal Raza"
        ),
        // Thursday
        TimetableDto(
            id = "tt-8",
            day = "THURSDAY",
            courseId = "c4",
            courseCode = "CS-404",
            courseName = "Database Systems II",
            startTime = "10:00",
            endTime = "11:30",
            room = "Lab 1, CS Block",
            instructor = "Dr. Fatima Noor"
        ),
        TimetableDto(
            id = "tt-9",
            day = "THURSDAY",
            courseId = "c3",
            courseCode = "CS-403",
            courseName = "Compiler Construction",
            startTime = "14:00",
            endTime = "15:30",
            room = "Lecture Hall 1",
            instructor = "Dr. Usman Tariq"
        ),
        // Friday
        TimetableDto(
            id = "tt-10",
            day = "FRIDAY",
            courseId = "c5",
            courseCode = "CS-405",
            courseName = "Artificial Intelligence",
            startTime = "14:00",
            endTime = "16:00",
            room = "Auditorium B",
            instructor = "Prof. Bilal Raza"
        )
    )

    private suspend fun checkNetwork() {
        delay(250) // Realistic network latency simulation
        if (isSimulatedOffline) {
            throw IOException("Network unavailable (Offline mode active)")
        }
        if (shouldFail) {
            throw IOException("Simulated network connection error")
        }
    }

    override suspend fun login(request: LoginRequestDto): Response<LoginResponseDto> {
        checkNetwork()
        val isValid = (request.studentId == "2022-CS-001" || request.studentId.equals("arshad@campus.edu", ignoreCase = true))
                && request.password == "123456"

        return if (isValid) {
            Response.success(LoginResponseDto(token = "jwt_campus_mock_token_xyz123", student = student))
        } else {
            val errorBody = "{\"error\": \"Invalid Student ID or Password. Try demo: 2022-CS-001 / 123456\"}"
                .toResponseBody("application/json".toMediaTypeOrNull())
            Response.error(401, errorBody)
        }
    }

    override suspend fun getProfile(): Response<StudentDto> {
        checkNetwork()
        return Response.success(student)
    }

    override suspend fun getCourses(): Response<List<CourseDto>> {
        checkNetwork()
        return Response.success(courses)
    }

    override suspend fun getAttendance(): Response<List<AttendanceDto>> {
        checkNetwork()
        return Response.success(attendance)
    }

    override suspend fun getAssignments(): Response<List<AssignmentDto>> {
        checkNetwork()
        return Response.success(assignments)
    }

    override suspend fun getAnnouncements(): Response<List<AnnouncementDto>> {
        checkNetwork()
        return Response.success(announcements)
    }

    override suspend fun getTimetable(): Response<List<TimetableDto>> {
        checkNetwork()
        return Response.success(timetable)
    }
}
