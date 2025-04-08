package com.example.clase25febrero

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clase25febrero.databinding.ActivityGpsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlin.math.roundToInt

class Gps : AppCompatActivity() {

    lateinit var binding: ActivityGpsBinding

    //Crear proveedor de localizacion
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    //Suscribirno a cambios
    lateinit var mLocationRequest: LocationRequest

    //callback a localizacion
    private lateinit var mLocationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGpsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicializar el proveedor
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //Metodo propio
        mLocationRequest = createLocationRequest()

        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        Log.i("LOCATION", "Location update in the callback: $location")
                        if (location != null) {
                            binding.elevacion.text = location.altitude.toString()
                            binding.latitud.text = location.latitude.toString()
                            binding.longitud.text = location.longitude.toString()
                            binding.distancia.text = distance(location.latitude, location.longitude, 4.70145,-74.14606).toString()
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

        //Ultima localizacion obtenida

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
                                binding.elevacion.text = location.altitude.toString()
                                binding.latitud.text = location.latitude.toString()
                                binding.longitud.text = location.longitude.toString()
                                binding.distancia.text = distance(location.latitude, location.longitude, 4.70145,-74.14606).toString()
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

    fun distance(lat1: Double, long1: Double, lat2: Double, long2: Double): Double {
        val latDistance = Math.toRadians(lat1 - lat2)
        val lngDistance = Math.toRadians(long1 - long2)
        val a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2))
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val result = MIscelanius.RADIUS_OF_EARTH_KM * c
        return (result * 100.0).roundToInt() / 100.0
    }
}