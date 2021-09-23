package com.example.fileexplorer.fragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fileexplorer.FileAdapter
import com.example.fileexplorer.OnFileSelectedListner
import com.example.fileexplorer.R
import kotlinx.android.synthetic.main.fragment_internal.view.*
import java.io.File
import java.util.*


class InternalFragment : Fragment(),OnFileSelectedListner {
    private lateinit var recyclerView: RecyclerView
    private lateinit var fileAdapter: FileAdapter
    private lateinit var internalStorage: File
    lateinit var path: String

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_internal, container, false)
        val storagePath = Environment.getExternalStorageDirectory().absolutePath
        recyclerView = view.recycler_internal
        this.internalStorage = File(storagePath)

        val bundle = this.arguments
        if (bundle != null)
            this.internalStorage  = File(bundle.getString("path"))

        view.tv_pathHolder.text = this.internalStorage.absolutePath

        if (isEmpty(internalStorage)) {
            view.recycler_internal.visibility = View.GONE
            view.empty_view.visibility = View.VISIBLE
        } else
            requestPermission()
        return view
    }


    companion object{
        const val REQUEST_CODE = 100
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        if (!Environment.isExternalStorageManager()) {
            Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).also {
                startActivity(it)
            }
        }
        displayFiles()
    }

    private fun findFiles(file: File): List<File> {
        val list: MutableList<File> = file.listFiles().filter {
            it.isDirectory && !it.isHidden
      }.toList().toMutableList()

        val files = file.listFiles().filter {
            it.name.lowercase(Locale.getDefault()).endsWith(".jpeg") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".png") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".wav") ||
                    it.name.lowercase().endsWith(".pdf") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".apk") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".jpg") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".mp3") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".mp4") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".docx") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".mkv") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".ini") ||
                    it.name.lowercase(Locale.getDefault()).endsWith(".txt")
        }.toMutableList()
        list.addAll(files)
        return list
    }

    private fun displayFiles() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        var fileList = mutableListOf<File>()
        fileList.addAll(findFiles(internalStorage))
        fileAdapter = FileAdapter(context, fileList, this)
        recyclerView.adapter = fileAdapter
    }

    override fun onFileClicked(file: File) {
        if(file.isDirectory) {
            var bundle = Bundle()
            bundle.putString("path", file.absolutePath)
            var frag = InternalFragment()
            frag.arguments = bundle
            fragmentManager?.beginTransaction()!!.replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }
        else
        {
            val target=Intent(Intent.ACTION_VIEW).also{
                it.setDataAndType(Uri.fromFile(file),"application/pdf")
                it.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

            }
            var int = Intent.createChooser(target,"Open File")
            context?.startActivity(int)
        }

    }

    private fun isEmpty(internalStorage: File): Boolean {
        var items = 0
        internalStorage.listFiles().forEach { if(!it.isHidden) items++ }
        return items==0
    }

}