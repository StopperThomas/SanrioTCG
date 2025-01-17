package at.ac.fhstp.sanriotcg.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.ac.fhstp.sanriotcg.model.CoinBalance
import kotlinx.coroutines.flow.Flow

@Dao
interface CoinBalanceDao {
    @Query("SELECT * FROM coin_balance WHERE id = 1")
    fun getCoinBalance(): Flow<CoinBalance?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(coinBalance: CoinBalance)
}