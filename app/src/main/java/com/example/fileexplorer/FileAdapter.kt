package com.example.fileexplorer

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileAdapter(private val context: Context?, private val files: List<File>, private val listner: OnFileSelectedListner) : RecyclerView.Adapter<FileViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container,parent,false))
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.textName.text = files[position].name
        holder.textName.isSelected=true
        var items =0
        if(files[position].isDirectory){
            files[position].listFiles().forEach { if (!it.isHidden) {
                items++
            }
            }
            holder.textSize.text = items.toString() + "Files"
            holder.imgType.setImageResource(R.drawable.ic_folder)
        }
        else{
            holder.textSize.text= Formatter.formatFileSize(context,files[position].length())
            setIcon(files[position],holder)
        }
        holder.cardView.setOnClickListener{
            listner.onFileClicked(files[position])
        }
        val date = SimpleDateFormat("MM-dd-yyyy  HH-mm-ss")
        holder.lastModified.text="last modified : "+date.format(files[position].lastModified())

    }

    private fun setIcon(file : File, holder : FileViewHolder){
        val name = file.name.toLowerCase()

        if (name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpj"))
            holder.imgType.setImageResource(R.drawable.ic_image)
        else if(name.endsWith(".pdf"))
            holder.imgType.setImageResource(R.drawable.ic_pdf)
        else if(name.endsWith(".mp4")||name.endsWith("mkv"))
            holder.imgType.setImageResource(R.drawable.ic_play)
        else if(name.endsWith("doc") || name.endsWith("docx") || name.endsWith("txt"))
            holder.imgType.setImageResource(R.drawable.ic_docs)
        else if(name.endsWith(".mp3"))
            holder.imgType.setImageResource(R.drawable.ic_music)
        else
            holder.imgType.setImageResource(R.drawable.ic_android)
    }
    override fun getItemCount(): Int {
        return files.size
    }
}