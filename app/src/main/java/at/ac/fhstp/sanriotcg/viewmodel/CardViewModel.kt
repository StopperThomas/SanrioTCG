package at.ac.fhstp.sanriotcg.viewmodel

import android.app.Application
import androidx.lifecycle.*
import at.ac.fhstp.sanriotcg.model.Card
import at.ac.fhstp.sanriotcg.data.CardDatabase
import at.ac.fhstp.sanriotcg.repository.CardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CardRepository
    private val _cards = MutableStateFlow<List<Card>>(emptyList())
    val cards: StateFlow<List<Card>> = _cards

    init {
        val cardDao = CardDatabase.getDatabase(application).cardDao()
        repository = CardRepository(cardDao)
        loadCards()
    }

    private fun loadCards() {
        viewModelScope.launch {
            repository.allCards.collect { cardsList ->
                _cards.value = cardsList
            }
        }
    }

    fun addCard(card: Card) = viewModelScope.launch {
        repository.insert(card)
        loadCards()
    }

    fun deleteCard(card: Card) = viewModelScope.launch {
        repository.delete(card)
        loadCards()
    }

    fun updateCards(updatedCard: Card) {
        _cards.value = _cards.value.map { card ->
            if (card.id == updatedCard.id) {
                updatedCard
            } else {
                card
            }
        }
    }
}
