package dk.itu.moapd.copenhagenbuzz.asjo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import com.github.javafaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Date

//
//class DataViewModel(
//    private val savedStateHandle: SavedStateHandle) : ViewModel() {
//
//    private val _eventList = MutableLiveData<List<Event>>()
//
//    private val _favoriteList = MutableLiveData<List<Event>>()
//
//    val favorites: LiveData<List<Event>> get() = _favoriteList
//
//    val events: LiveData<List<Event>> get() = _eventList
//
//    private val faker = Faker()
//
//    init {
//        fetchEvents()
//    }

//    private fun generateFakeEvents(): List<Event> {
//        val date1 = LocalDate.of(
//            faker.number().numberBetween(2020, 2025),
//            faker.number().numberBetween(1, 12),
//            faker.number().numberBetween(1, 31)
//        )
//        val date2 = LocalDate.of(
//            faker.number().numberBetween(2020, 2025),
//            faker.number().numberBetween(1, 12),
//            faker.number().numberBetween(1, 31)
//        )
//
//        return List(15) {
//            Event(
//                userId = faker.number().toString(),
//                eventName = faker.company().name(),
//                eventLocation = faker.country().capital(),
//                startDate = faker.number().randomNumber(),
//                endDate = faker.number().randomNumber(),
//                eventType = faker.hipster().word(),
//                eventDescription = faker.lorem().sentence()
//            )
//        }
//    }


//    private fun generateRandomFavorites(events: List<Event>): List<Event> {
//        val shuffledIndices = (events.indices).shuffled().take(10)
//        return shuffledIndices.map { index -> events[index] }
//    }
//
//
//    private fun fetchEvents() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val eventList = generateFakeEvents()
//            _eventList.postValue(eventList)
//            val favorites = generateRandomFavorites(eventList)
//            favorites.forEach { it.isFavorite = true }
//            _favoriteList.postValue(favorites)
//
//        }
//    }
//}

//    private fun fetchFavorites() {
//        viewModelScope.launch(Dispatchers.IO) {
//             val favorites = generateRandomFavorites(generateFakeEvents())
//             favorites.forEach { it.isFavorite=true }
//            _favoriteList.postValue(favorites)
//        }
//    }





