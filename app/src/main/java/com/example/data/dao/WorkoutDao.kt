package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ExerciseSet
import com.example.data.model.RepMetric
import com.example.data.model.UserProfile
import com.example.data.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<WorkoutSession>>

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    fun getSessionById(sessionId: Long): Flow<WorkoutSession?>

    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId ORDER BY setNumber ASC")
    fun getSetsForSession(sessionId: Long): Flow<List<ExerciseSet>>

    @Query("SELECT * FROM rep_metrics WHERE setId = :setId ORDER BY repIndex ASC")
    fun getRepsForSet(setId: Long): Flow<List<RepMetric>>

    @Query("SELECT * FROM rep_metrics WHERE setId IN (SELECT id FROM exercise_sets WHERE sessionId = :sessionId) ORDER BY setId ASC, repIndex ASC")
    fun getAllRepsForSession(sessionId: Long): Flow<List<RepMetric>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: ExerciseSet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepMetrics(metrics: List<RepMetric>)

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSessionById(sessionId: Long)

    @Query("SELECT COUNT(*) FROM workout_sessions")
    suspend fun getSessionCount(): Int

    // User Profile Queries
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun getUserProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("DELETE FROM user_profile")
    suspend fun clearUserProfile()
}
