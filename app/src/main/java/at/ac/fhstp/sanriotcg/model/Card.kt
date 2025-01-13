package at.ac.fhstp.sanriotcg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "card_table")
data class Card(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for Room database
    val name: String, // Name of the card
    val drawableRes: Int, // Resource ID for the card image (e.g., R.drawable.cinnamoroll)
    val rarity: Float // Probability of this card being selected
)
