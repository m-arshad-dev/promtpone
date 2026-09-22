package com.example.campuscompanion.ui.screens.attendance

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.campuscompanion.domain.model.AttendanceRecord
import com.example.campuscompanion.ui.components.AttendanceProgressBar
import com.example.campuscompanion.ui.components.EmptyState
import com.example.campuscompanion.viewmodel.AttendanceViewModel
import com.example.ui.theme.AttendanceDanger
import com.example.ui.theme.AttendanceGood
import com.example.ui.theme.AttendanceWarning

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    viewModel: AttendanceViewModel,
    onCourseClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Attendance Records",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = Modifier.testTag("attendance_screen")
    ) { paddingValues ->
        if (uiState.records.isEmpty()) {
            EmptyState(
                title = "No Attendance Data",
                description = "Attendance records are currently being synced with university registers.",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Overall Summary Hero Card
                item {
                    OverallAttendanceHeroCard(
                        overallPercentage = uiState.overallPercentage,
                        totalClasses = uiState.totalClasses,
                        totalAttended = uiState.totalAttended,
                        totalAbsent = uiState.totalAbsent
                    )
                }

                // Threshold Warning Alert
                if (uiState.lowAttendanceCount > 0) {
                    item {
                        ThresholdWarningCard(lowCount = uiState.lowAttendanceCount)
                    }
                }

                // Section Title
                item {
                    Text(
                        text = "Course Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Course by course cards
                items(uiState.records, key = { it.id }) { record ->
                    AttendanceCourseCard(
                        record = record,
                        onClick = { onCourseClick(record.courseId) }
                    )
                }
            }
        }
    }
}

@Composable
fun OverallAttendanceHeroCard(
    overallPercentage: Int,
    totalClasses: Int,
    totalAttended: Int,
    totalAbsent: Int,
    modifier: Modifier = Modifier
) {
    val progress = (overallPercentage / 100f).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "overallCircle")

    val overallColor = when {
        overallPercentage >= 80 -> AttendanceGood
        overallPercentage >= 75 -> AttendanceWarning
        else -> AttendanceDanger
    }

    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("overall_attendance_hero")
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular percentage display
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(90.dp)
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        strokeWidth = 9.dp,
                        color = overallColor.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxSize()
                    )
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        strokeWidth = 9.dp,
                        color = overallColor,
                        modifier = Modifier.fillMaxSize()
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$overallPercentage%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = overallColor
                        )
                        Text(
                            text = "Overall",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Stats column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Attendance Summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Target: 75% minimum required by university examination regulations.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stat breakdown boxes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatPill(
                    label = "Total Classes",
                    value = "$totalClasses",
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Attended",
                    value = "$totalAttended",
                    color = AttendanceGood,
                    modifier = Modifier.weight(1f)
                )
                StatPill(
                    label = "Absent",
                    value = "$totalAbsent",
                    color = AttendanceDanger,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ThresholdWarningCard(
    lowCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AttendanceDanger.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("attendance_threshold_warning")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = null,
                tint = AttendanceDanger,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Action Required: Low Attendance Alert",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AttendanceDanger
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "You have $lowCount course(s) below the 75% minimum eligibility requirement. Please ensure regular attendance in upcoming sessions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun AttendanceCourseCard(
    record: AttendanceRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("attendance_course_card_${record.courseCode}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.courseCode,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "${record.attendedClasses}/${record.totalClasses} Classes",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = record.courseName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            AttendanceProgressBar(
                percentage = record.percentage,
                showWarningTag = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Absent: ${record.absentClasses} classes",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (record.isBelowThreshold) AttendanceDanger else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = if (record.isBelowThreshold) "Needs Attention" else "On Track",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (record.isBelowThreshold) AttendanceDanger else AttendanceGood
                )
            }
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
