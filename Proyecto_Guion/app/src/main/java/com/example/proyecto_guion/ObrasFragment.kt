package com.example.proyecto_guion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_guion.databinding.FragmentObrasBinding
import java.io.File

class ObrasFragment : Fragment() {

    var bindingNull: FragmentObrasBinding? = null
    val binding: FragmentObrasBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    private lateinit var adapter: FolderAdapter
    private var selectedFolder: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingNull = FragmentObrasBinding.inflate(inflater, container, false)
        val vista = binding.root

        binding.sideObras.visibility = View.GONE

       (activity as AppCompatActivity).supportActionBar!!.title = "Tus Obras"

        // Inicializar el adapter
        adapter = FolderAdapter(
            folders = emptyList(),
            onFolderClick = { folder ->
                Toast.makeText(requireContext(), "Carpeta seleccionada: ${folder.name}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { folder ->
                selectedFolder = folder
                toggleSideObrasVisibility()
                Toast.makeText(requireContext(), "Drawer abierto para: ${folder.name}", Toast.LENGTH_SHORT).show()
            }
        )

        // Configurar RecyclerView
        binding.containerButtons.layoutManager = LinearLayoutManager(requireContext())
        binding.containerButtons.adapter = adapter

        // Observar el LiveData para las carpetas
        model.folders.observe(viewLifecycleOwner) { folders ->
            adapter.updateFolders(folders ?: emptyList()) // Asegurar lista no nula
        }

        binding.ButtonEliminar.setOnClickListener {
            selectedFolder?.let { folder ->
                // Eliminar la carpeta seleccionada
                if (folder.exists() && folder.isDirectory) {
                    val success = folder.deleteRecursively() // Eliminar la carpeta y su contenido
                    if (success) {
                        Toast.makeText(requireContext(), "Carpeta eliminada: ${folder.name}", Toast.LENGTH_SHORT).show()
                        refreshFolders() // Refrescar la lista de carpetas
                    } else {
                        Toast.makeText(requireContext(), "No se pudo eliminar la carpeta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            binding.sideObras.visibility = View.GONE // Cerrar el panel lateral después de la eliminación
        }

        // Configurar el FAB para abrir el AddObrasFragment
        binding.botonfloatID.setOnClickListener {
            val navController = activity?.findNavController(R.id.container_fragment)
            if (navController != null) {
                navController.navigate(R.id.action_obrasFragment_to_addObrasFragment)
            }
        }

        // Cargar carpetas al inicio
        loadFolderButtons()

        return vista
    }

    private fun toggleSideObrasVisibility() {
        // Si 'side_obras' está visible, lo ocultamos
        if (binding.sideObras.visibility == View.VISIBLE) {
            binding.sideObras.visibility = View.GONE
        } else {
            // Si 'side_obras' está oculto, lo mostramos
            binding.sideObras.visibility = View.VISIBLE
        }
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