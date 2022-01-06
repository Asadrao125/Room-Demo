package com.technado.roomdemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ContactAdapter(var context: Context, var list: List<Contact>) :
    RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = list[position].name
        holder.tvPhone.text = list[position].phone
        holder.btnDelete.setOnClickListener(View.OnClickListener {
            GlobalScope.launch {
                MainActivity.database.contactDao().deleteContact(list.get(position))
            }
        })

        holder.itemView.setOnClickListener(View.OnClickListener {
            SettingsDialog(context as Activity, list.get(position), position)
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView
        var tvPhone: TextView
        var btnDelete: Button

        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvPhone = itemView.findViewById(R.id.tvPhone)
            btnDelete = itemView.findViewById(R.id.btnDelete)
        }
    }

    fun SettingsDialog(activity: Activity, contact: Contact, position: Int) {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.getLayoutInflater()
        val dialogView: View = inflater.inflate(R.layout.edit_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertDialog: AlertDialog = dialogBuilder.create()

        val btnUpdate: Button = dialogView.findViewById(R.id.btnUpdate)
        val edtName: EditText = dialogView.findViewById(R.id.edtName)
        val edtPhone: EditText = dialogView.findViewById(R.id.edtPhone)
        val tvTitle: TextView = dialogView.findViewById(R.id.tvTitle)

        edtName.setText(contact.name)
        tvTitle.text = "Update - " + contact.name
        edtPhone.setText(contact.phone)

        btnUpdate.setOnClickListener(View.OnClickListener {
            val name = edtName.text.toString()
            val phone = edtPhone.text.toString()
            if (!name.isEmpty() && !phone.isEmpty()) {
                GlobalScope.launch {
                    MainActivity.database.contactDao()
                        .updateContact(Contact(contact.id, name, phone))
                }
                list.get(position).name = name
                list.get(position).phone = phone
                alertDialog.dismiss()
            } else {
                Toast.makeText(context, "Name Required", Toast.LENGTH_SHORT).show()
            }
        })
        alertDialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }
}