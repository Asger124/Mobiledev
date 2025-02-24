package dk.itu.moapd.copenhagenbuzz.asjo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private var _isLoggedIn = MutableLiveData<Boolean>()

    val isLoggedin: LiveData<Boolean>
        get() = _isLoggedIn


    private var IsLoggedIn = false
    fun toggleLogin() {
        IsLoggedIn = !IsLoggedIn
    }

    fun IsLogged() = IsLoggedIn

}