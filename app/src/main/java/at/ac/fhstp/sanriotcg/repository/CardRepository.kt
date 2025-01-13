package at.ac.fhstp.sanriotcg.repository
import at.ac.fhstp.sanriotcg.data.CardDao
import at.ac.fhstp.sanriotcg.model.Card
import kotlinx.coroutines.flow.Flow

class CardRepository(private val cardDao: CardDao) {

    val allCards: Flow<List<Card>> = cardDao.getAllCards()

    suspend fun insert(card: Card) {
        cardDao.insert(card)
    }

    suspend fun delete(card: Card) {
        cardDao.delete(card)
    }
}