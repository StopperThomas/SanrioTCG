package at.ac.fhstp.sanriotcg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_balance")
data class CoinBalance(
    @PrimaryKey val id: Int = 1,
    val balance: Int
)