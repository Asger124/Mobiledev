package dk.itu.moapd.copenhagenbuzz.asjo.view

import com.google.android.material.color.DynamicColors
import android.app.Application

class MyApplication: Application() {


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

}