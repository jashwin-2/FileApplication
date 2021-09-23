package com.example.fileexplorer

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.file_container.view.*

class FileViewHolder(view : View) : RecyclerView.ViewHolder(view) {
    val textName: TextView = view.fileName
    val textSize: TextView = view.size
    val imgType: ImageView = view.fileType
    val cardView = view.container
    val lastModified = view.last_modified
}