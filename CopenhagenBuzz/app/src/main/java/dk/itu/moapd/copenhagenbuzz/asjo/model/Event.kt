package dk.itu.moapd.copenhagenbuzz.asjo.model

import com.google.firebase.database.PropertyName
import java.time.LocalDate

data class Event(
    var userId : String ? = null,
    var eventName: String? = null,
    var eventLocation: EventLocation? = null,
    var startDate : Long? = null,
    var endDate : Long? = null,
    var eventType: String? = null,
    var eventDescription: String? = null,
    var eventPhotoURL: String? = null,
    @get:PropertyName("isFavorite")
    @set:PropertyName("isFavorite")
    var isFavorite: Boolean = false
)



