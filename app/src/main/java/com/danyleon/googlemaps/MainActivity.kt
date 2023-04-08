package com.danyleon.googlemaps

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener,
    GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener {

    private lateinit var map: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapPin: ImageView
    private lateinit var addressText: TextView

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addressText = findViewById(R.id.addressText)
        mapPin = findViewById(R.id.mapPin)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this)

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                getAddress(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean = false

    private fun getAddress(latLng: LatLng) {

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            Geocoder(this).getFromLocation(latLng.latitude, latLng.longitude, 1) { addressList ->
                if (addressList.size > 0) {
                    addressText.text = addressList[0].getAddressLine(0)
                }
            }
        } else {
            val addresses = Geocoder(this).getFromLocation(latLng.latitude, latLng.longitude, 1)

            if (addresses!!.size > 0) {
                addressText.text = addresses[0].getAddressLine(0)
            }
        }
    }

    override fun onCameraMoveStarted(p0: Int) {
        println("start")
    }

    override fun onCameraMove() {
        println("moving")
    }

    override fun onCameraIdle() {
        println("end")
        val center = map.cameraPosition.target
        val currentLatLng = LatLng(center.latitude, center.longitude)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
        getAddress(currentLatLng)
    }

    override fun onCameraMoveCanceled() {
        println("cancel")
    }
}