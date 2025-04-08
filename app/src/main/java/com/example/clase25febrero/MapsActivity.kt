package com.example.clase25febrero

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.clase25febrero.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import java.io.IOException

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //Crear proveedor de localizacion
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    //Suscribirno a cambios
    lateinit var mLocationRequest: LocationRequest

    //callback a localizacion
    private lateinit var mLocationCallback: LocationCallback

    private  var mMap: GoogleMap?=null
    private lateinit var binding: ActivityMapsBinding
    var gpsub = LatLng(4.628721, -74.0636458129701)

    //Configuracion del sensor
    private lateinit var sensorManager: SensorManager
    private lateinit var lightSensor: Sensor
    private lateinit var lightSensorListener: SensorEventListener

    //Inicializar el Geocoder
    private lateinit var mGeocoder: Geocoder

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(lightSensorListener, lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL)
    }
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(lightSensorListener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Inicializar Sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)!!

        lightSensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (mMap != null) {
                    if (event.values[0] < 10000) { //Este es el lumbral de luz
                        Log.i("MAPS", "DARK MAP " + event.values[0])
                        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(baseContext, R.raw.noche))
                    } else {
                        Log.i("MAPS", "LIGHT MAP " + event.values[0])
                        mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(baseContext, R.raw.retro))
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
        }

        //Geocoder
        mGeocoder = Geocoder(baseContext)
        binding.editTextText.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEND){
                val mGeocoder = Geocoder(baseContext)
                //Cuando se realice la busqueda
                val addressString = binding.editTextText.text.toString()
                if (addressString.isNotEmpty()) {
                    try {
                        val addresses: List<Address>? = mGeocoder.getFromLocationName(
                            addressString, 2, MIscelanius.lowerLeftLatitude, MIscelanius.lowerLeftLongitude,
                            MIscelanius.upperRightLatitude, MIscelanius.upperRightLongitude)
                        if (addresses != null && addresses.isNotEmpty()) {
                            val addressResult = addresses[0]
                            val position = LatLng(addressResult.latitude, addressResult.longitude)
                            if (mMap != null) {
                                //Agregar Marcador al mapa
                                mMap?.addMarker(MarkerOptions().position(position).title(addressResult.featureName))
                                Toast.makeText(this, "Latitud" + position.latitude + "Longitud" + position.longitude,
                                    Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "Dirección no encontrada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this, "La dirección esta vacía", Toast.LENGTH_SHORT).show()
                }
            }
            return@setOnEditorActionListener true
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.uiSettings.isZoomGesturesEnabled = true
        mMap!!.uiSettings.isZoomControlsEnabled = true
        mMap!!.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.noche)) //Cambiar el estilo del mapa
        mMap!!.setOnMapClickListener { latLng ->
            try {
                val addresses = mGeocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressLine = address.getAddressLine(0) // Dirección completa

                    mMap?.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title(addressLine)
                    )
                } else {
                    // Si no encuentra dirección
                    mMap?.addMarker(
                        MarkerOptions()
                            .position(latLng)
                            .title("Ubicación sin dirección")
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al obtener dirección", Toast.LENGTH_SHORT).show()
            }
        }

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Metodo propio
        mLocationRequest = createLocationRequest()


        // Add a marker in Sydney and move the camera
        val javeriana = LatLng(4.628721, -74.0636458129701)
        val plazaBolivar = LatLng(4.59806, -74.0758)
        val titan = LatLng(4.69478, -74.08635)
        val movistar = LatLng(4.6492837, -74.0772872950584)

        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        Log.i("LOCATION", "Location update in the callback: $location")
                        if (location != null) {
                            gpsub = LatLng(location.latitude,location.longitude)
                            val gps = mMap?.addMarker(MarkerOptions().position(gpsub).title("Ubicacion actual"))
                            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                        }
                    }
                }
                startLocationUpdates()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                pedirPermiso(this, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    "", MIscelanius.PERMISSION_UBI)
            }
            else -> {
                pedirPermiso(this, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    "", MIscelanius.PERMISSION_UBI)
            }
        }

        val mrkJaveriana = mMap?.addMarker(MarkerOptions().position(javeriana).title("Universidad Javeriana").snippet("La segunda mejor universidad privada").alpha(1F)
            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
            .icon(bitmapDescriptorFromVector(this, R.drawable.baseline_book_24)))
        val mrkPlazaBolivar = mMap?.addMarker(MarkerOptions().position(plazaBolivar).title("Plaza de Bolivar"))
            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        val mrkTitan = mMap?.addMarker(MarkerOptions().position(titan).title("C.C TItan"))
            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        val mrkMovistar = mMap?.addMarker(MarkerOptions().position(movistar).title("Movistar Arena"))
            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
        mMap?.moveCamera(CameraUpdateFactory.zoomTo(15F))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(gpsub))

        mrkJaveriana?.isVisible = true //Mostrar un marcador
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setBounds(0, 0, vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val bitmap = Bitmap.createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
        }
    }


    //Constructor de peticiones para cambios en localizacion
    private fun createLocationRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,10000).apply { setMinUpdateIntervalMillis(5000) }.build()



    private fun pedirPermiso(context: Activity, permiso: String, justificacion: String, idCode: Int){
        if (ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(permiso), idCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MIscelanius.PERMISSION_UBI -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mLocationCallback = object : LocationCallback() {
                        override fun onLocationResult(locationResult: LocationResult) {
                            val location = locationResult.lastLocation
                            Log.i("LOCATION", "Location update in the callback: $location")
                            if (location != null) {
                                gpsub = LatLng(location.latitude,location.longitude)
                                val gps = mMap!!.addMarker(MarkerOptions().position(gpsub).title("Ubicacion actual"))
                                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))
                            }
                        }
                    }
                    startLocationUpdates()

                } else {
                    Toast.makeText(baseContext, "Experiencia de usuario diminuida.",Toast.LENGTH_SHORT).show()

                }

                return
            }

            MIscelanius.PERMISSION_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    Toast.makeText(baseContext, "Experiencia de usuario diminuida.",Toast.LENGTH_SHORT).show()
                }

                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

}
