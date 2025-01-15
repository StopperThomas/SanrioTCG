package at.ac.fhstp.sanriotcg.repository

import at.ac.fhstp.sanriotcg.data.CardDao
import at.ac.fhstp.sanriotcg.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class CardRepository(private val cardDao: CardDao) {

    val allCards: Flow<List<Card>> = cardDao.getAllCards()

    suspend fun insert(card: Card) {
        cardDao.insert(card)
    }

    suspend fun delete(card: Card): Card {
        cardDao.delete(card)
        return card
    }

    suspend fun getCardById(cardId: Int): Card? {
        return allCards.first().find { it.id == cardId }
    }
}