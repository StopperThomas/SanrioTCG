package at.ac.fhstp.sanriotcg.data

import androidx.room.*
import at.ac.fhstp.sanriotcg.model.Card
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM card_table")
    fun getAllCards(): Flow<List<Card>>

    @Query("SELECT * FROM card_table WHERE id = :id LIMIT 1")
    fun getCardById(id: Int): Card

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: Card)

    @Delete
    suspend fun delete(card: Card)
}