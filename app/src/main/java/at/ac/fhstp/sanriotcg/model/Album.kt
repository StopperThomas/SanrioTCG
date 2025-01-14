package at.ac.fhstp.sanriotcg.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album_table")
data class Album(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val cardIds: List<Int>
)