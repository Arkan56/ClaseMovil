package com.example.clase25febrero

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clase25febrero.databinding.ActivityOsmactivityBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

class OSMActivity : AppCompatActivity() {

    lateinit var binding: ActivityOsmactivityBinding
    val latitude = 4.62
    val longitude = -74.07
    val startPoint = GeoPoint(latitude, longitude)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOsmactivityBinding.inflate(layoutInflater)
        Configuration.getInstance().load(applicationContext, getSharedPreferences("osmdroid", MODE_PRIVATE))
        setContentView(binding.root)
        binding.osmMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.osmMap.setMultiTouchControls(true)
    }

    override fun onResume() {
        super.onResume()
        binding.osmMap.onResume()
        val mapController: IMapController = binding.osmMap.controller
        mapController.setZoom(18.0)
        mapController.setCenter(this.startPoint)
    }
    override fun onPause() {
        super.onPause()
        binding.osmMap.onPause()
    }

}