package com.danyleon.googlemaps

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
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


class MainActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener {

    private lateinit var map: GoogleMap
    private lateinit var addressText: TextView
    private lateinit var relativePin: RelativeLayout
    private lateinit var relativePinShadow: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addressText = findViewById(R.id.addressText)
        relativePin = findViewById(R.id.relativePin)
        relativePinShadow = findViewById(R.id.relativePinShadow)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true

        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraIdleListener(this);

        setUpMap()
    }

    private fun setUpMap() {
        val currentLatLng = LatLng(-12.119956, -77.0312958)
        getAddress(currentLatLng)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
    }

    private fun getAddress(latLng: LatLng) {
        addressText.text = latLng.toString()
    }

    override fun onCameraMoveStarted(p0: Int) {
        relativePinShadow.visibility = View.VISIBLE
        val params = relativePin.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0,0,0,180)
        relativePin.layoutParams = params
    }

    override fun onCameraIdle() {
        relativePinShadow.visibility = View.GONE
        val params = relativePin.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(0,0,0,100)
        relativePin.layoutParams = params

        val center = map.cameraPosition.target
        val currentLatLng = LatLng(center.latitude, center.longitude)
        getAddress(currentLatLng)
    }
}