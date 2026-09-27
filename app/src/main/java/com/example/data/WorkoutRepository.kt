package com.example.data

import com.example.data.dao.WorkoutDao
import com.example.data.model.ExerciseSet
import com.example.data.model.RepMetric
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val dao: WorkoutDao) {
    val allSessions: Flow<List<WorkoutSession>> = dao.getAllSessions()
    val userProfile: Flow<UserProfile?> = dao.getUserProfile()

    suspend fun isUserRegistered(): Boolean = dao.getUserProfileCount() > 0

    suspend fun saveUserProfile(profile: UserProfile) {
        dao.saveUserProfile(profile)
    }

    suspend fun clearUserProfile() {
        dao.clearUserProfile()
    }

    fun getSession(id: Long): Flow<WorkoutSession?> = dao.getSessionById(id)

    fun getSetsForSession(sessionId: Long): Flow<List<ExerciseSet>> = dao.getSetsForSession(sessionId)

    fun getRepsForSet(setId: Long): Flow<List<RepMetric>> = dao.getRepsForSet(setId)

    fun getAllRepsForSession(sessionId: Long): Flow<List<RepMetric>> = dao.getAllRepsForSession(sessionId)

    suspend fun saveCompletedWorkout(
        exerciseType: String,
        weightKg: Float,
        reps: List<RepMetric>,
        durationSeconds: Long,
        rpe: Float
    ): Long {
        val totalReps = reps.size
        val avgDepth = if (reps.isNotEmpty()) reps.map { it.minKneeAngleDeg }.average().toFloat() else 90f
        val maxDev = if (reps.isNotEmpty()) reps.maxOfOrNull { it.maxBarDeviationCm } ?: 0f else 0f

        val session = WorkoutSession(
            timestamp = System.currentTimeMillis(),
            exerciseType = exerciseType,
            totalReps = totalReps,
            avgDepthAngleDeg = avgDepth,
            maxBarDeviationCm = maxDev,
            durationSeconds = durationSeconds,
            rpe = rpe
        )
        val sessionId = dao.insertSession(session)

        val set = ExerciseSet(
            sessionId = sessionId,
            setNumber = 1,
            exerciseType = exerciseType,
            weightKg = weightKg,
            completedReps = totalReps,
            targetReps = totalReps
        )
        val setId = dao.insertSet(set)

        val updatedReps = reps.map { it.copy(setId = setId) }
        dao.insertRepMetrics(updatedReps)

        return sessionId
    }

    suspend fun seedSampleDataIfEmpty() {
        if (dao.getSessionCount() > 0) return

        val now = System.currentTimeMillis()
        val oneDayAgo = now - 86400000L
        val threeDaysAgo = now - (86400000L * 3)

        // Session 1: Squats
        val session1 = WorkoutSession(
            timestamp = oneDayAgo,
            exerciseType = "SQUAT",
            totalReps = 5,
            avgDepthAngleDeg = 84.6f,
            maxBarDeviationCm = 3.2f,
            durationSeconds = 48L,
            rpe = 8.5f,
            isProSession = false
        )
        val s1Id = dao.insertSession(session1)
        val set1 = ExerciseSet(
            sessionId = s1Id,
            setNumber = 1,
            exerciseType = "SQUAT",
            weightKg = 140f,
            completedReps = 5,
            targetReps = 5
        )
        val set1Id = dao.insertSet(set1)
        val reps1 = listOf(
            RepMetric(0, set1Id, 1, 86f, 74f, 2.1f, 2.3f, 1.1f, 96, "PRISTINE_FORM", true),
            RepMetric(0, set1Id, 2, 84f, 72f, 2.4f, 2.1f, 1.2f, 94, "PRISTINE_FORM", true),
            RepMetric(0, set1Id, 3, 85f, 73f, 2.8f, 2.2f, 1.4f, 92, "PRISTINE_FORM", true),
            RepMetric(0, set1Id, 4, 83f, 70f, 3.2f, 2.4f, 1.8f, 88, "SLIGHT_FORWARD_DRIFT", true),
            RepMetric(0, set1Id, 5, 85f, 69f, 3.1f, 2.5f, 2.1f, 85, "FATIGUE_BAR_SLOW", true)
        )
        dao.insertRepMetrics(reps1)

        // Session 2: Deadlift
        val session2 = WorkoutSession(
            timestamp = threeDaysAgo,
            exerciseType = "DEADLIFT",
            totalReps = 5,
            avgDepthAngleDeg = 92.0f,
            maxBarDeviationCm = 2.8f,
            durationSeconds = 42L,
            rpe = 9.0f,
            isProSession = false
        )
        val s2Id = dao.insertSession(session2)
        val set2 = ExerciseSet(
            sessionId = s2Id,
            setNumber = 1,
            exerciseType = "DEADLIFT",
            weightKg = 180f,
            completedReps = 5,
            targetReps = 5
        )
        val set2Id = dao.insertSet(set2)
        val reps2 = listOf(
            RepMetric(0, set2Id, 1, 95f, 68f, 1.8f, 1.8f, 1.0f, 98, "LOCKOUT_SOLID", true),
            RepMetric(0, set2Id, 2, 94f, 66f, 2.1f, 1.9f, 1.1f, 95, "LOCKOUT_SOLID", true),
            RepMetric(0, set2Id, 3, 92f, 65f, 2.3f, 2.0f, 1.3f, 93, "LOCKOUT_SOLID", true),
            RepMetric(0, set2Id, 4, 90f, 62f, 2.8f, 2.2f, 1.7f, 89, "HIPS_ROSE_EARLY", true),
            RepMetric(0, set2Id, 5, 89f, 60f, 2.6f, 2.4f, 2.3f, 86, "HIPS_ROSE_EARLY", true)
        )
        dao.insertRepMetrics(reps2)
    }
}
