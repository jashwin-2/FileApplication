package com.example.fileexplorer.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fileexplorer.FileAdapter
import com.example.fileexplorer.OnFileSelectedListner
import com.example.fileexplorer.R
import kotlinx.android.synthetic.main.fragment_internal.view.*
import java.io.File


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

        view.tv_pathHolder.text = internalStorage.absolutePath.replace('/', '>',false)
        if (isEmpty(internalStorage)) {
            view.recycler_internal.visibility = View.GONE
            view.empty_view.visibility = View.VISIBLE
        }

        val dia = AlertDialog.Builder(context)
            .setTitle("Attention")
            .setMessage("Couldn't access files.Allow File Explorer to access files")
            .setIcon(R.drawable.ic_file_explorer)
            .setPositiveButton("Ok") { _, _ ->
                Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).also {
                    startActivity(it)
                }
            }.create()

        if(!Environment.isExternalStorageManager()) {
            dia.show()
            displayFiles()
        }
        requestPermission()
        view.tv_pathHolder.setOnClickListener {
            it.isSelected = true
            view.tv_pathHolder.setHorizontallyScrolling(true)
        }
        displayFiles()
        return view
    }

    companion object{
        const val REQUEST_CODE = 1
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun requestPermission() {
        //TODO better permission handling
        var requests = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED ) {
            requests.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_DENIED )
            requests.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (requests.isNotEmpty())
            requestPermissions(requests.toTypedArray(), REQUEST_CODE)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == REQUEST_CODE)
        {

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(activity,"${permissions[0]} Access Granted",Toast.LENGTH_LONG).show()
            else
                Toast.makeText(activity,"${permissions[0]} Access Denied",Toast.LENGTH_LONG).show()
            if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(activity,"${permissions[1]} Access Granted",Toast.LENGTH_LONG).show()
            else
                Toast.makeText(activity,"${permissions[1]} Access Denied",Toast.LENGTH_LONG).show()
        }
    }

    private fun findFiles(file: File): List<File> {
        val list: MutableList<File> = file.listFiles().filter {
            !it.isHidden
        }.toList().toMutableList()

        //TODO repeated processing. name.lowercase is called multiple times but the output will be the same. such repeated processing needs to be avoided. applies to Locale.getDefault() too
        return list.sortedBy { it.name }
    }


    private fun displayFiles() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        //TODO

        //TODO why create a new list and copy items ?
        fileAdapter = FileAdapter(context, findFiles(internalStorage), this)
        recyclerView.adapter = fileAdapter
    }

    override fun onFileClicked(file: File) {
        if(file.isDirectory) {
            var bundle = Bundle()
            bundle.putString("path", file.absolutePath)
            var frag = InternalFragment()
            frag.arguments = bundle
            //TODO commit vs commitAllowingStateloss, addToBackStack
            fragmentManager?.beginTransaction()!!.replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit()
        }
        else
        {
            //TODO file mime type, FileUriExposedException
            val uri = FileProvider.getUriForFile(requireContext(),
                requireContext().applicationContext.packageName+".provider",file)
            val intent  = Intent(Intent.ACTION_VIEW)
            val name = uri.toString().toLowerCase()

            if(name.contains(".doc"))
                intent.setDataAndType(uri,"application/msword")
            else if (name.contains(".pdf"))
                intent.setDataAndType(uri,"application/pdf")
            else if (name.contains(".mp3") || name.contains(".wav"))
                intent.setDataAndType(uri,"application/x-wav")
            else if (name.contains(".jpeg") || name.contains(".jpj") || name.contains(".png") || name.contains(".pdf"))
                intent.setDataAndType(uri,"image/jpeg")
            else if (name.contains(".mp4") || name.contains(".mkv"))
                intent.setDataAndType(uri,"video/*")
            else
                intent.setDataAndType(uri,"*/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            requireContext().startActivity(intent)

        }
    }

    private fun isEmpty(internalStorage: File): Boolean {
        //TODO Only need to find at least one file which is not hidden. No need to count all files
        internalStorage.listFiles().forEach { if(!it.isHidden) return false }
        return true
    }

}