package at.ac.fhstp.sanriotcg.data

import androidx.room.*
import at.ac.fhstp.sanriotcg.model.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Query("SELECT * FROM album_table")
    fun getAllAlbums(): Flow<List<Album>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(album: Album)

    @Delete
    suspend fun delete(album: Album)

    @Update
    suspend fun update(album: Album)
}
