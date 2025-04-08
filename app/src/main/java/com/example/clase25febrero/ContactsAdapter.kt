package com.example.clase25febrero

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter
import com.example.clase25febrero.databinding.ContactsAdapterBinding

class ContactsAdapter(context: Context?, c: Cursor?, flags: Int) : CursorAdapter(context, c, flags) {

    lateinit var  binding: ContactsAdapterBinding

    private val CONTACT_ID_INDEX = 0
    private val DISPLAY_NAME_INDEX = 1

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context)
            .inflate(R.layout.contacts_adapter, parent, false)
    }
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val tvIdContacto = view?.findViewById<TextView>(R.id.textView4)
        val tvNombre = view?.findViewById<TextView>(R.id.textView5)
        val idnum = cursor?.getInt(CONTACT_ID_INDEX)
        val nombre = cursor?.getString(DISPLAY_NAME_INDEX)
        tvIdContacto?.text = idnum?.toString()
        tvNombre?.text = nombre
    }


}