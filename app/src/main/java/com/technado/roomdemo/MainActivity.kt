package com.technado.roomdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.technado.roomdemo.databinding.MainActivityBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    var binding: MainActivityBinding? = null
    lateinit var recyclerViewContact: RecyclerView

    companion object {
        lateinit var database: ContactDatabase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        database = Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contactDB"
        ).build()

        recyclerViewContact = binding!!.recyclerViewContact
        recyclerViewContact.layoutManager =
            LinearLayoutManager(this)
        recyclerViewContact.setHasFixedSize(true)

        database.contactDao().getAllContact().observe(this, {
            recyclerViewContact.adapter = ContactAdapter(this, it)
        })

        binding?.btnSave?.setOnClickListener(View.OnClickListener {
            val name = binding?.edtName?.text.toString()
            val phone = binding?.edtPhone?.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(applicationContext, "Name Required", Toast.LENGTH_SHORT).show()
            } else if (phone.isEmpty()) {
                Toast.makeText(applicationContext, "Phone Required", Toast.LENGTH_SHORT).show()
            } else {
                GlobalScope.launch {
                    database.contactDao().insertContact(Contact(0, name, phone))
                }
                binding?.edtName?.text = null
                binding?.edtPhone?.text = null

                database.contactDao().getAllContact().observe(this, {
                    recyclerViewContact.adapter = ContactAdapter(this, it)
                })
            }
        })
    }
}