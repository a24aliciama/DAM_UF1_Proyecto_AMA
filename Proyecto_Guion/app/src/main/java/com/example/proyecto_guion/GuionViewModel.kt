package com.example.proyecto_guion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class GuionViewModel: ViewModel() {
    // LiveData para almacenar el nombre
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _folders = MutableLiveData<List<File>>()
    val folders: LiveData<List<File>> get() = _folders

    private val _selectedFolder = MutableLiveData<File?>()
    val selectedFolder: LiveData<File?> get() = _selectedFolder

    // Méodo para actualizar el nombre
    fun setName(name: String) {
        _name.value = name
    }

    fun setFolders(folders: List<File>) {
        _folders.value = folders
    }

    fun selectFolder(folder: File) {
        _selectedFolder.value = folder
    }




}