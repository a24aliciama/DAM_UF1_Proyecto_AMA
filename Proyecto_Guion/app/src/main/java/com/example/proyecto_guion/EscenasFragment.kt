package com.example.proyecto_guion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_guion.databinding.FragmentEscenasBinding
import java.io.File

class EscenasFragment : Fragment() {

    var bindingNull: FragmentEscenasBinding? = null
    val binding: FragmentEscenasBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    private lateinit var adapter: FolderAdapter
    private var selectedFolderEscenas: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingNull = FragmentEscenasBinding.inflate(inflater, container, false)

        binding.sideEscena.visibility = View.GONE

        (activity as AppCompatActivity).supportActionBar!!.title = (model.selectedFolderObra.value?.name
            ?: "") + " " + getString(R.string.TusEscenas)

                // Detectar toques  de sideObras
        binding.sideEscena.setOnClickListener {
            if (binding.sideEscena.visibility == View.VISIBLE) {
                binding.sideEscena.visibility = View.GONE // Ocultar el panel lateral

            }
        }

        adapter = FolderAdapter(
            folders = emptyList(),
            onFolderClick = { folder ->
                // Guardar la carpeta seleccionada en el ViewModel
                model.selectFolderEscena(folder)

                // Navegar al fragmento de Escenas o cualquier otro fragmento relacionado
                val navController = activity?.findNavController(R.id.container_fragment)
                navController?.navigate(R.id.action_escenasFragment_to_chatFragment) // Cambia este ID según tu necesidad
                Toast.makeText(requireContext(), "Carpeta seleccionada: ${folder.name}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { folder ->
                selectedFolderEscenas = folder
                toggleSideEscenasVisibility()
                Toast.makeText(requireContext(), "Opciones para: ${folder.name}", Toast.LENGTH_SHORT).show()
            }
        )

        // Configurar RecyclerView
        binding.containerButtonsEscena.layoutManager = LinearLayoutManager(requireContext())
        binding.containerButtonsEscena.adapter = adapter

        // Observar el ViewModel para obtener la carpeta seleccionada
        model.selectedFolderObra.observe(viewLifecycleOwner) { folder ->
            folder?.let {
                // Obtener subcarpetas dentro de la carpeta seleccionada
                val subfolders = folder.listFiles()?.filter { it.isDirectory } ?: emptyList()
                adapter.updateFolders(subfolders) // Actualizar el adaptador con las subcarpetas
            }
        }

        binding.ButtonEliminarEscena.setOnClickListener {
            selectedFolderEscenas?.let { folder ->
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
            binding.sideEscena.visibility = View.GONE // Cerrar el panel lateral después de la eliminación
        }

        binding.ButtonRenombrarEscena.setOnClickListener {
            selectedFolderEscenas?.let { folder ->
                // Mostrar un cuadro de diálogo para introducir el nuevo nombre
                renombrar(folder)
            } ?: run {
                Toast.makeText(requireContext(), "No hay carpeta seleccionada para renombrar", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ButtonEliminarTodoEscena.setOnClickListener {
            val rootFolder = model.selectedFolderObra.value// Carpeta raíz

            if (rootFolder != null) {
                if (rootFolder.exists() && rootFolder.isDirectory) {
                    rootFolder.listFiles()?.forEach { folder ->
                        folder.deleteRecursively() // Eliminar cada carpeta y su contenido
                    }
                    Toast.makeText(requireContext(), "Todas las carpetas se eliminaron correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "La carpeta raíz no existe o no es válida", Toast.LENGTH_SHORT).show()
                }
            }

            refreshFolders() // Actualizar la lista de carpetas
            binding.sideEscena.visibility = View.GONE // Cerrar el panel lateral después de la eliminación
        }

        // Configurar el FAB para abrir el AddEscenaFragment
        binding.botonfloatIDEscena.setOnClickListener {
           /* val navController = activity?.findNavController(R.id.container_fragment)
            if (navController != null) {
                navController.navigate(R.id.action_escenasFragment_to_addEscenaFragment)
            }*/
            val dialog = AddEscenaFragment()
            dialog.show(parentFragmentManager, "AddEscenaDialog")
        }

        refreshFolders()

        return binding.root
    }

    private fun toggleSideEscenasVisibility() {
        // Si  está visible, lo ocultamos
        if (binding.sideEscena.visibility == View.VISIBLE) {
            binding.sideEscena.visibility = View.GONE
        } else {
            // Si está oculto, lo mostramos
            binding.sideEscena.visibility = View.VISIBLE
        }
    }

    private fun refreshFolders() {
        val folder = model.selectedFolderObra.value
        folder?.let {
            val subfolders = it.listFiles()?.filter { it.isDirectory } ?: emptyList()
            adapter.updateFolders(subfolders) // Actualizar adaptador con la lista de subcarpetas actualizada
        }
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
                renombrarEscena(folder, newName)
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

    private fun renombrarEscena(folder: File, newName: String) {
        val parentDir = folder.parentFile
        val newFolder = File(parentDir, newName)

        if (newFolder.exists()) {
            Toast.makeText(requireContext(), "Ya existe una carpeta con ese nombre", Toast.LENGTH_SHORT).show()
            return
        }

        val success = folder.renameTo(newFolder)
        if (success) {
            Toast.makeText(requireContext(), "Escena renombrada a: $newName", Toast.LENGTH_SHORT).show()
            refreshFolders() // Refrescar la lista de subcarpetas
        } else {
            Toast.makeText(requireContext(), "No se pudo renombrar la escena", Toast.LENGTH_SHORT).show()
        }
    }
}