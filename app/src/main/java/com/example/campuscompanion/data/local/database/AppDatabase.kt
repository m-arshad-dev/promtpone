package com.example.campuscompanion.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.campuscompanion.data.local.dao.CampusDao
import com.example.campuscompanion.data.local.entity.AnnouncementEntity
import com.example.campuscompanion.data.local.entity.AssignmentEntity
import com.example.campuscompanion.data.local.entity.AttendanceEntity
import com.example.campuscompanion.data.local.entity.CourseEntity
import com.example.campuscompanion.data.local.entity.StudentEntity
import com.example.campuscompanion.data.local.entity.TimetableEntity

@Database(
    entities = [
        StudentEntity::class,
        CourseEntity::class,
        AttendanceEntity::class,
        AssignmentEntity::class,
        AnnouncementEntity::class,
        TimetableEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun campusDao(): CampusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campus_companion.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
