package dk.itu.moapd.copenhagenbuzz.asjo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import com.github.javafaker.Faker




class DataViewModel(
    private val savedStateHandle: SavedStateHandle) : ViewModel(){

        private val _eventList = MutableLiveData<ArrayList<Event>>()

    val events: LiveData<ArrayList<Event>> get() = _eventList


}