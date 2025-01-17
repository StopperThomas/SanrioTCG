package at.ac.fhstp.sanriotcg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val target: Int,
    var progress: Int = 0,
    val reward: Int,
    var claimed: Boolean = false
)