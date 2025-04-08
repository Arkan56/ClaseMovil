package com.example.clase25febrero

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clase25febrero.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    AdapterView.OnItemSelectedListener {
    lateinit var binding: ActivityMainBinding
    var item = "0"

    var mProjection: Array<String>? = null
    var mCursor: Cursor? = null
    var mContactsAdapter: ContactsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        binding.spinner.onItemSelectedListener = this

        val json = JSONObject(MIscelanius.loadJSONFromAsset(baseContext,"paises.json"))
        val paisesJsonArray = json.getJSONArray("paises")
        val nombresPaises = arrayOfNulls<String>(paisesJsonArray.length())
        for (i in 0 until paisesJsonArray.length()) {
            val jsonObject = paisesJsonArray.getJSONObject(i)
            nombresPaises[i] = jsonObject.getString("nombre_pais")
        }

        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, nombresPaises)
        binding.lista.adapter = adapter*/


        binding.button2.setOnClickListener{
            startActivity(Intent(this,PantallaWeb::class.java))
        }
        binding.button.setOnClickListener{
            if(item != "0") {
                Toast.makeText(baseContext, item, Toast.LENGTH_LONG).show()
                startActivity(Intent(this,PantallaFrame::class.java))
            }
            else{
                Toast.makeText(baseContext, "No se ha seleccionado una opcion", Toast.LENGTH_LONG).show()
            }
        }

        binding.button5.setOnClickListener {
            startActivity(Intent(this, OSMActivity::class.java))
        }

        //Leer contactos
        val mProjection = arrayOf(ContactsContract.Profile._ID, ContactsContract.Profile.DISPLAY_NAME_PRIMARY)
        mContactsAdapter = ContactsAdapter(this, null, 0)
        binding.lista.adapter = mContactsAdapter

        when {
            ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                mCursor = contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI, mProjection,
                    null,
                    null,
                    null
                )
                mContactsAdapter?.changeCursor(mCursor)
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this, android.Manifest.permission.READ_CONTACTS) -> {
                pedirPermiso(this, android.Manifest.permission.READ_CONTACTS,
                    "", MIscelanius.PERMISSION_READ_CONTACTS)
            }
            else -> {
                pedirPermiso(this, android.Manifest.permission.READ_CONTACTS,
                    "", MIscelanius.PERMISSION_READ_CONTACTS)
            }
        }
    }

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
            MIscelanius.PERMISSION_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    mCursor = contentResolver.query(
                        ContactsContract.Contacts.CONTENT_URI, mProjection,
                        null,
                        null,
                        null
                    )
                    mContactsAdapter?.changeCursor(mCursor)
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

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(baseContext, "Seleccion: " + parent?.selectedItemId + "," + parent?.selectedItem
            , Toast.LENGTH_LONG).show()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Toast.makeText(baseContext, "Seleccion: " + parent?.selectedItemId + "," + parent?.selectedItem
            , Toast.LENGTH_LONG).show()
        item = parent?.selectedItem.toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }


}