package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val exerciseType: String = "SQUAT", // "SQUAT" or "DEADLIFT"
    val totalReps: Int = 0,
    val avgDepthAngleDeg: Float = 0f,
    val maxBarDeviationCm: Float = 0f,
    val durationSeconds: Long = 0L,
    val rpe: Float = 8.0f,
    val isProSession: Boolean = false
)

@Entity(tableName = "exercise_sets")
data class ExerciseSet(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: Long,
    val setNumber: Int = 1,
    val exerciseType: String = "SQUAT",
    val weightKg: Float = 100f,
    val completedReps: Int = 5,
    val targetReps: Int = 5
)

@Entity(tableName = "rep_metrics")
data class RepMetric(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val setId: Long,
    val repIndex: Int,
    val minKneeAngleDeg: Float,
    val minHipAngleDeg: Float,
    val maxBarDeviationCm: Float,
    val eccentricDurationSec: Float,
    val concentricDurationSec: Float,
    val formScore: Int, // 0 to 100
    val formBreakdownWarning: String,
    val isCleanRep: Boolean = true,
    val barPathPointsJson: String = ""
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Long = 1L,
    val name: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val fitnessGoal: String = "Strength & Form",
    val experienceLevel: String = "Intermediate",
    val targetMuscle: String = "All Muscle Groups",
    val registeredTimestamp: Long = System.currentTimeMillis()
)
