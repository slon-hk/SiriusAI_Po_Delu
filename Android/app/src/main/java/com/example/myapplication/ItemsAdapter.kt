package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

// Класс данных для хранения информации о книгах
data class Item(val nameBooks: String, val imageResId: Bitmap)

class ItemsAdapter(private val items: List<Item>, private val context: Context): RecyclerView.Adapter<ItemsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = items[position]
        holder.nameBook.text = item.nameBooks

        // Устанавливаем битмап в ImageView
        holder.image.setImageBitmap(item.imageResId)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){
        val image: ImageView = view.findViewById(R.id.imageBook)
        val nameBook: TextView = view.findViewById(R.id.nameBook)
    }
}