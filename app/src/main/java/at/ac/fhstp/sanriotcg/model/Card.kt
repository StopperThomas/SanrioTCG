package at.ac.fhstp.sanriotcg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_table")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val drawableRes: Int,
    val rarity: Float
)