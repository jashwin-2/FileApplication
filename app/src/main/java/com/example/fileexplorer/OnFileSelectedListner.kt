package com.example.fileexplorer

import java.io.File

interface OnFileSelectedListner {
    fun onFileClicked(file : File)
}