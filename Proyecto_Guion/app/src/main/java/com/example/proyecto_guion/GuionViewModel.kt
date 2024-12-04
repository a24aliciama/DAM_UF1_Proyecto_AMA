package com.example.proyecto_guion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class GuionViewModel: ViewModel() {
    // LiveData para almacenar el nombre
    private val _name = MutableLiveData<String>()
    val name: LiveData<String> get() = _name

    private val _foldersObras = MutableLiveData<List<File>>()
    val folders: LiveData<List<File>> get() = _foldersObras

    private val _selectedFolderObra = MutableLiveData<File?>()
    val selectedFolderObra: LiveData<File?> get() = _selectedFolderObra

    // Méodo para actualizar el nombre
    fun setName(name: String) {
        _name.value = name
    }

    fun setFoldersObras(folders: List<File>) {
        _foldersObras.value = folders
    }

    fun selectFolderObra(folder: File) {
        _selectedFolderObra.value = folder
    }

    // Variables para almacenar los personajes
    private val _personajes = MutableLiveData<List<String>>()
    val personajes: LiveData<List<String>> get() = _personajes

    // Méodo para actualizar la lista de personajes
    fun setPersonajes(personajesList: List<String>) {
        _personajes.value = personajesList
    }




}