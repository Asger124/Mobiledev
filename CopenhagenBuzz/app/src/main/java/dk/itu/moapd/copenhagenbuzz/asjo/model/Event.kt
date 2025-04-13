package dk.itu.moapd.copenhagenbuzz.asjo.model

import java.time.LocalDate

data class Event(
    var eventName: String,
    var eventLocation: String,
    var eventDateRange: Pair<LocalDate,LocalDate>,
    var eventType: String,
    var eventDescription: String,
    var isFavorite: Boolean = false

)



