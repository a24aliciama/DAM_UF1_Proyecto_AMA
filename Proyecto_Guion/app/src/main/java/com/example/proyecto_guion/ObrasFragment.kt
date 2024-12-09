package com.example.proyecto_guion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_guion.databinding.FragmentObrasBinding
import java.io.File

class ObrasFragment : Fragment() {

    var bindingNull: FragmentObrasBinding? = null
    val binding: FragmentObrasBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    private lateinit var adapter: FolderAdapter
    private var selectedFolderObras: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingNull = FragmentObrasBinding.inflate(inflater, container, false)
        val vista = binding.root

        binding.sideObras.visibility = View.GONE

        (activity as AppCompatActivity).supportActionBar!!.title = getString(R.string.TusObras)

        // Detectar toques  de sideObras
        binding.sideObras.setOnClickListener {
            if (binding.sideObras.visibility == View.VISIBLE) {
                binding.sideObras.visibility = View.GONE // Ocultar el panel lateral

            }
        }

        // Inicializar el adapter
        adapter = FolderAdapter(
            folders = emptyList(),
            onFolderClick = { folder ->
                model.selectFolderObra(folder) // Guardar la carpeta seleccionada en el ViewModel
                val navController = activity?.findNavController(R.id.container_fragment)
                navController?.navigate(R.id.action_obrasFragment_to_escenasFragment) // Navegar al fragmento Escenas
                Toast.makeText(requireContext(), "Carpeta seleccionada: ${folder.name}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { folder ->
                selectedFolderObras = folder
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
            selectedFolderObras?.let { folder ->
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

        binding.ButtonRenombrar.setOnClickListener {
            selectedFolderObras?.let { folder ->
                // Mostrar un cuadro de diálogo para introducir el nuevo nombre
                renombrar(folder)
            } ?: run {
                Toast.makeText(requireContext(), "No hay carpeta seleccionada para renombrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ButtonEliminarTodo.setOnClickListener {
            val rootFolder = File(requireContext().filesDir, "ProyectoGuion") // Carpeta raíz

            if (rootFolder.exists() && rootFolder.isDirectory) {
                rootFolder.listFiles()?.forEach { folder ->
                    folder.deleteRecursively() // Eliminar cada carpeta y su contenido
                }
                Toast.makeText(requireContext(), "Todas las carpetas se eliminaron correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "La carpeta raíz no existe o no es válida", Toast.LENGTH_SHORT).show()
            }

            refreshFolders() // Actualizar la lista de carpetas
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
        model.setFoldersObras(folders)
    }

    // Esta función es llamada después de crear o eliminar una carpeta
    fun refreshFolders() { // <-- Cambiado: Méodo reutilizado para actualizar carpetas
        loadFolderButtons()
    }

    private fun renombrar(folder: File) {
        val dialogView = layoutInflater.inflate(R.layout.renombrar_dialogo, null)

        val input = dialogView.findViewById<EditText>(R.id.edtNewName)
        val renameButton = dialogView.findViewById<Button>(R.id.ButtonRenombrar)
        val cancelButton = dialogView.findViewById<Button>(R.id.ButtonCancelar)

        // Configurar el diálogo
        val builder = AlertDialog.Builder(requireContext())
            .setView(dialogView)  // Establecer el diseño personalizado
            .setCancelable(true)

        val dialog = builder.create()

        // Configurar botones del diálogo
        // Configurar el botón "Renombrar"
        renameButton.setOnClickListener {
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                renombrarObra(folder, newName)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el botón "Cancelar"
        cancelButton.setOnClickListener {
            dialog.cancel() // Cerrar el diálogo
        }

        // Mostrar el diálogo
        dialog.show()
    }

    private fun renombrarObra(folder: File, newName: String) {
        val parentDir = folder.parentFile
        val newFolder = File(parentDir, newName)

        if (newFolder.exists()) {
            Toast.makeText(requireContext(), "Ya existe una carpeta con ese nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val success = folder.renameTo(newFolder)
        if (success) {
            Toast.makeText(requireContext(), "Carpeta renombrada a: $newName", Toast.LENGTH_SHORT).show()
            refreshFolders() // Refrescar la lista de carpetas
        } else {
            Toast.makeText(requireContext(), "No se pudo renombrar la carpeta", Toast.LENGTH_SHORT).show()
        }
    }
}