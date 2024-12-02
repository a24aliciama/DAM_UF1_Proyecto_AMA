package com.example.proyecto_guion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_guion.databinding.FragmentObrasBinding
import com.google.android.material.navigation.NavigationView
import java.io.File

class ObrasFragment : Fragment() {

    var bindingNull: FragmentObrasBinding? = null
    val binding: FragmentObrasBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingNull = FragmentObrasBinding.inflate(inflater, container, false)
        val vista = binding.root

       (activity as AppCompatActivity).supportActionBar!!.title = "Tus Obras"

        // Inicializamos el RecyclerView con el adapter
        adapter = FolderAdapter(emptyList()) { folder ->  // <-- Cambiado: Pasar función lambda a `onClick`
            Toast.makeText(requireContext(), "Carpeta seleccionada: ${folder.name}", Toast.LENGTH_SHORT).show()
        }


        binding.containerButtons.layoutManager = LinearLayoutManager(requireContext())
        binding.containerButtons.adapter = adapter

        // Observamos el LiveData para la lista de carpetas
        model.folders.observe(viewLifecycleOwner) { folders ->
            adapter.updateFolders(folders) // <-- Cambiado: Usar méodo `updateFolders` del adaptador
        }

        // Cargar las carpetas inicialmente
        loadFolderButtons()


        return vista
    }

    private fun loadFolderButtons() {
        // Obtén las carpetas dentro de "ProyectoGuion"
        val proyectoGuionDir = File(requireContext().filesDir, "ProyectoGuion")
        if (!proyectoGuionDir.exists()) {
            proyectoGuionDir.mkdir()  // Crear el directorio si no existe
        }

        // Obtener la lista de carpetas
        val folders = proyectoGuionDir.listFiles()?.filter { it.isDirectory } ?: emptyList()

        // Actualizar la lista de carpetas en el ViewModel
        model.setFolders(folders)
    }

    // Esta función es llamada después de crear o eliminar una carpeta
    fun refreshFolders() { // <-- Cambiado: Méodo reutilizado para actualizar carpetas
        loadFolderButtons()
    }
}