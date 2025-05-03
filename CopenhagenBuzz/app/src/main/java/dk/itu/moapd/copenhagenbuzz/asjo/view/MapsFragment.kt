package dk.itu.moapd.copenhagenbuzz.asjo.view

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.SharedPreferences

import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentMapsBinding
import dk.itu.moapd.copenhagenbuzz.asjo.model.Event
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback {

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            Log.d("MapsFragment", "BroadcastReceiver got an intent!")
            Toast.makeText(context, "Loc update received", Toast.LENGTH_SHORT).show()
            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION, Location::class.java)
            else
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(LocationService.EXTRA_LOCATION)
            location?.let {
                updateLocationDetails(it)
            }
        }

    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission was just granted, enable location features
            if (::googleMap.isInitialized) {
                try {
                    googleMap.isMyLocationEnabled = true
                } catch (e: SecurityException) {
                    Log.e("MapsFragment", "Error enabling location: ${e.message}")
                }
            }

            // Start location updates
            if (locationServiceBound) {
                locationService.subscribeToLocationUpdates()
            } else {
                // If service is not bound yet, bind it
                Intent(requireContext(), LocationService::class.java).let { serviceIntent ->
                    requireActivity().bindService(
                        serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
                }
            }
        } else {
            // Permission denied
            Toast.makeText(
                requireContext(),
                "Location permission is required for location features",
                Toast.LENGTH_LONG
            ).show()
        }
    }


    private lateinit var googleMap: GoogleMap

    private var _binding: FragmentMapsBinding? = null

    private val binding
        get() = requireNotNull(_binding){
            "Cannot access this"
        }

    companion object {
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    private lateinit var locationService: LocationService
    private var locationServiceBound = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocalBinder
            locationService = binder.service
            locationServiceBound = true
            locationService.subscribeToLocationUpdates()
        }
        override fun onServiceDisconnected(name: ComponentName) {
            locationServiceBound = false
        }
    }


    private fun requestUserPermissions() {
        if (!checkPermission())
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        // Initialize the broadcast receiver.
        locationBroadcastReceiver = LocationBroadcastReceiver()

        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }


    override fun onMapReady(map: GoogleMap) {
            val db = Firebase.database(dotenv)
            val eventsRef = db.reference.child("events")

            googleMap = map

            googleMap.setPadding(0, 100, 0, 0)

            // Store a reference from Marker to Event
            val markerEventMap = mutableMapOf<Marker, Event>()

            // Fetch events and add markers
            eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (eventSnapshot in snapshot.children) {
                        val event = eventSnapshot.getValue(Event::class.java)
                        val location = event?.eventLocation
                        if (event != null && location != null) {
                            val latLng = LatLng(location.latitude, location.longitude)
                            val marker = googleMap.addMarker(
                                MarkerOptions()
                                    .position(latLng)
                                    .title(event.eventName)
                            )
                            if (marker != null) {
                                markerEventMap[marker] = event
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Map", "Failed to load events: ${error.message}")
                }
            })

            // Handle marker click events
            googleMap.setOnMarkerClickListener { marker ->
                val event = markerEventMap[marker]
                event?.let {
                    val message = buildString {
                        appendLine("Type: ${it.eventType}")
                        appendLine("Description: ${it.eventDescription}")
                        appendLine("Start: ${it.startDate}")
                        appendLine("End: ${it.endDate}")
                        appendLine("Location: ${it.eventLocation?.address}")
                    }

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(it.eventName)
                        .setMessage(message)
                        .setPositiveButton("OK", null)
                        .show()
                }
                true // Return true to consume the event
            }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true
        } else {
            requestUserPermissions()
        }
    }

    override fun onStart() {
        super.onStart()

        Intent(requireContext(), LocationService::class.java).let { serviceIntent ->
            requireActivity().bindService(
                serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST))

        // Register the shared preference change listener.

    }


    override fun onPause() {
        // Unregister the broadcast receiver.
        super.onPause()

        if (::locationService.isInitialized) {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationBroadcastReceiver)

            locationService.unsubscribeToLocationUpdates()
        }

    }
    override fun onStop() {

        if (locationServiceBound) {
            requireActivity().unbindService(serviceConnection)
            locationServiceBound = false
        }
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationBroadcastReceiver)

        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




    private fun updateLocationDetails(location: Location) {

        if(checkPermission()) {
                    val user = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(user))
                    // 2) Show lat/lng in your overlay
                    with(binding) {
                        coordOverlay.text = String.format(
                            Locale.getDefault(),
                            "Lat: %.6f\nLng: %.6f",
                            location.latitude,
                            location.longitude
                        )
            }
        }else {
            requestUserPermissions()
        }

    }


}