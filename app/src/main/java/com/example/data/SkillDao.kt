package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.model.SkillWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillDao {

    @Transaction
    @Query("SELECT * FROM skills ORDER BY lastPracticedAt DESC, id DESC")
    fun getAllSkillsWithDetails(): Flow<List<SkillWithDetails>>

    @Transaction
    @Query("SELECT * FROM skills WHERE id = :skillId")
    fun getSkillWithDetailsById(skillId: Long): Flow<SkillWithDetails?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: SkillEntity): Long

    @Update
    suspend fun updateSkill(skill: SkillEntity)

    @Delete
    suspend fun deleteSkill(skill: SkillEntity)

    @Query("DELETE FROM skills WHERE id = :skillId")
    suspend fun deleteSkillById(skillId: Long)

    @Query("UPDATE skills SET totalMinutes = totalMinutes + :minutes, lastPracticedAt = :timestamp WHERE id = :skillId")
    suspend fun addPracticeMinutes(skillId: Long, minutes: Int, timestamp: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: MilestoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<MilestoneEntity>)

    @Update
    suspend fun updateMilestone(milestone: MilestoneEntity)

    @Delete
    suspend fun deleteMilestone(milestone: MilestoneEntity)

    @Query("DELETE FROM milestones WHERE id = :milestoneId")
    suspend fun deleteMilestoneById(milestoneId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPracticeSession(session: PracticeSessionEntity): Long

    @Delete
    suspend fun deletePracticeSession(session: PracticeSessionEntity)

    @Query("DELETE FROM practice_sessions WHERE id = :sessionId")
    suspend fun deletePracticeSessionById(sessionId: Long)

    @Query("SELECT * FROM practice_sessions ORDER BY dateMillis DESC")
    fun getAllPracticeSessions(): Flow<List<PracticeSessionEntity>>
}
