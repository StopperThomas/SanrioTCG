package at.ac.fhstp.sanriotcg.repository

import at.ac.fhstp.sanriotcg.data.AlbumDao
import at.ac.fhstp.sanriotcg.model.Album
import kotlinx.coroutines.flow.Flow

class AlbumRepository(private val albumDao: AlbumDao) {

    val allAlbums: Flow<List<Album>> = albumDao.getAllAlbums()

    suspend fun insert(album: Album) {
        albumDao.insert(album)
    }

    suspend fun delete(album: Album) {
        albumDao.delete(album)
    }

    suspend fun update(album: Album) {
        albumDao.update(album)
    }
}