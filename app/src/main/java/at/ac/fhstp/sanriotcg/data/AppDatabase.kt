package at.ac.fhstp.sanriotcg.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import at.ac.fhstp.sanriotcg.model.Card
import at.ac.fhstp.sanriotcg.model.Album
import at.ac.fhstp.sanriotcg.model.CoinBalance
import at.ac.fhstp.sanriotcg.model.Challenge

@Database(entities = [Card::class, Album::class, CoinBalance::class, Challenge::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun cardDao(): CardDao
    abstract fun albumDao(): AlbumDao
    abstract fun coinBalanceDao(): CoinBalanceDao
    abstract fun challengeDao(): ChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}