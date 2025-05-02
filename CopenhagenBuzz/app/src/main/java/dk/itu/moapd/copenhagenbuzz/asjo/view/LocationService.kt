package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.common.util.SharedPreferencesUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dk.itu.moapd.copenhagenbuzz.asjo.R

class LocationService : Service() {

    inner class LocalBinder : Binder() {
        internal val service: LocationService
            get() = this@LocationService
    }

    private val localBinder = LocalBinder()

    companion object {
        private const val PACKAGE_NAME = "dk.itu.moapd.copenhagenbuzz.asjo"
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    }


    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var locationCallback: LocationCallback


    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize the `LocationCallback`.
        locationCallback = object : LocationCallback() {

            /**
             * This method will be executed when `FusedLocationProviderClient` has a new location.
             *
             * @param locationResult The last known location.
             */
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                // Get the current user's location.
                val currentLocation = locationResult.lastLocation

                // Notify our Activity that a new location was added.
                val intent = Intent(ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
                intent.putExtra(EXTRA_LOCATION, currentLocation)
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder {
        // Return the communication channel to the service.
        return localBinder
    }

    fun subscribeToLocationUpdates() {


        // Save the location tracking preference.
        SharedPreferenceUtil.saveLocationTrackingPref(this, true)

        // Sets the accuracy and desired interval for active location updates.
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateIntervalMillis(30_000L)
            .setMaxUpdateDelayMillis(120_000L)
            .build()

        // Subscribe to location changes.
        try {
            fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        }
    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        // Create a notification channel (if needed) and build a notification.
//        val channelId = "location_channel"
//        val channelName = "Location Service Channel"
//        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//        notificationManager.createNotificationChannel(channel)
//
//        val notification = NotificationCompat.Builder(this, "location_channel")
//            .setContentTitle("Location Service")
//            .setContentText("Tracking location in background")
//            .setSmallIcon(R.drawable.baseline_firebase_24)
//            .build()
//
//        // Start the service in the foreground
//        startForeground(1, notification)
//
//        subscribeToLocationUpdates()
//
//        return START_STICKY
//    }
    fun unsubscribeToLocationUpdates() {
        // Unsubscribe to location changes.
        try {
            fusedLocationProviderClient
                .removeLocationUpdates(locationCallback)
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
        }
    }

}

