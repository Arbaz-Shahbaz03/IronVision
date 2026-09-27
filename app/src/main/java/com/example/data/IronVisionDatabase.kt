package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.WorkoutDao
import com.example.data.model.ExerciseSet
import com.example.data.model.RepMetric
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession

@Database(
    entities = [
        WorkoutSession::class,
        ExerciseSet::class,
        RepMetric::class,
        UserProfile::class
    ],
    version = 2,
    exportSchema = false
)
abstract class IronVisionDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: IronVisionDatabase? = null

        fun getDatabase(context: Context): IronVisionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IronVisionDatabase::class.java,
                    "ironvision_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
