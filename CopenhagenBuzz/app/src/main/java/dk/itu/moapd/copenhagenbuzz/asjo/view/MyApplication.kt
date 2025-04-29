package dk.itu.moapd.copenhagenbuzz.asjo.view

import com.google.android.material.color.DynamicColors
import android.app.Application
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.cdimascio.dotenv.dotenv

inline fun <reified T> T.TAG(): String = T::class.java.simpleName

val dotenv = dotenv {
    directory = "/assets"
    filename = "env"
}["DATABASE_URL"]


class MyApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        Firebase.database(dotenv).setPersistenceEnabled(true)
        Firebase.database(dotenv).reference.keepSynced(true)
    }

}