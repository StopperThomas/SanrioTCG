package at.ac.fhstp.sanriotcg.repository

import at.ac.fhstp.sanriotcg.data.ChallengeDao
import at.ac.fhstp.sanriotcg.model.Challenge
import kotlinx.coroutines.flow.Flow

class ChallengeRepository(private val challengeDao: ChallengeDao) {

    fun getChallenges(): Flow<List<Challenge>> = challengeDao.getChallenges()

    suspend fun insertOrUpdate(challenge: Challenge) {
        challengeDao.insertOrUpdate(challenge)
    }

    suspend fun updateProgress(challengeId: Int, progress: Int) {
        challengeDao.updateProgress(challengeId, progress)
    }

    suspend fun markAsClaimed(challengeId: Int) {
        challengeDao.markAsClaimed(challengeId)
    }
}