package at.ac.fhstp.sanriotcg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.ac.fhstp.sanriotcg.model.Challenge
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(challenge: Challenge)

    @Query("SELECT * FROM challenges")
    fun getChallenges(): Flow<List<Challenge>>

    @Query("UPDATE challenges SET progress = :progress WHERE id = :challengeId")
    suspend fun updateProgress(challengeId: Int, progress: Int)

    @Query("UPDATE challenges SET claimed = 1 WHERE id = :challengeId")
    suspend fun markAsClaimed(challengeId: Int)
}