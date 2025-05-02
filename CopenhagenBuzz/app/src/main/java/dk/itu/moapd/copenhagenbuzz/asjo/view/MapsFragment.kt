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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dk.itu.moapd.copenhagenbuzz.asjo.R
import dk.itu.moapd.copenhagenbuzz.asjo.databinding.FragmentMapsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MapsFragment : Fragment(), OnMapReadyCallback, SharedPreferences.OnSharedPreferenceChangeListener {

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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMapsBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(binding.map.id) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        sharedPreferences = requireActivity()
            .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        // Initialize the broadcast receiver.
        locationBroadcastReceiver = LocationBroadcastReceiver()
    }


    override fun onMapReady(map: GoogleMap) {

        googleMap = map
        val itu = LatLng(55.6596, 12.5910)
        googleMap.addMarker(MarkerOptions().position(itu).title("IT University of Copenhagen"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(itu))
        // Move the Google Maps UI buttons under the OS top bar.
        googleMap.setPadding(0, 100, 0, 0)

        // Enable the location layer. Request the permission if it is not granted.

        if (checkPermission()) {
            googleMap.isMyLocationEnabled = true

        } else {
            requestUserPermissions()
        }
    }

    override fun onStart() {
        super.onStart()

        ContextCompat.startForegroundService(
            requireContext(),
            Intent(requireContext(), LocationService::class.java)
        )

        // Register the shared preference change listener.
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onResume() {
        super.onResume()

        // Register the broadcast receiver.
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(LocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }
    override fun onPause() {
        // Unregister the broadcast receiver.
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(
            locationBroadcastReceiver
        )
        super.onPause()
    }
    override fun onStop() {

        // Unregister the shared preference change listener.
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun checkPermission() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestUserPermissions() {
        if (!checkPermission())
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
    }
    private fun updateLocationDetails(location: Location) {
        val user = LatLng(location.latitude, location.longitude)
        googleMap.addMarker(
            MarkerOptions().position(user).title("You are here")
        )
        // 2) Show lat/lng in your overlay
        with(binding) {
            coordOverlay.text = String.format(
                Locale.getDefault(),
                "Lat: %.6f\nLng: %.6f",
                location.latitude,
                location.longitude
            )
        }

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
//        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
//        val trackingEnabled =
//            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
//        lastKnownLocation?.let {
//            updateTrackingview(trackingEnabled, it)
//        }
    }
//    }


}