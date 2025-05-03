package dk.itu.moapd.copenhagenbuzz.asjo.model

data class EventLocation (
    var address : String? = null,
    var latitude: Double = 0.0,
    var longitude : Double = 0.0,
)