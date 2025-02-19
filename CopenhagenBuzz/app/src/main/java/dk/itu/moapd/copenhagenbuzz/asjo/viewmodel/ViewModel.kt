package dk.itu.moapd.copenhagenbuzz.asjo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    private var _text = MutableLiveData<String>()

    val textState: LiveData<String>
        get() = _text

    fun onTextChanged(text:String) {
        _text.value = text
    }

    private var isLoggedIn = false

    fun toggleLogin() {
        isLoggedIn = !isLoggedIn
    }

    fun isLogged() = isLoggedIn

}