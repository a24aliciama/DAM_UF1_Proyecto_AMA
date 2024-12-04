package com.example.proyecto_guion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        (activity as AppCompatActivity).supportActionBar!!.title = "Tus Escenas"

        // Detectar toques  de sideObras
        binding.sideEscena.setOnClickListener {
            if (binding.sideEscena.visibility == View.VISIBLE) {
                binding.sideEscena.visibility = View.GONE // Ocultar el panel lateral

            }
        }

        adapter = FolderAdapter(
            folders = emptyList(),
            onFolderClick = { folder ->
                Toast.makeText(requireContext(), "Subcarpeta seleccionada: ${folder.name}", Toast.LENGTH_SHORT).show()
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

        // Configurar el FAB para abrir el AddEscenaFragment
        binding.botonfloatIDEscena.setOnClickListener {
           /* val navController = activity?.findNavController(R.id.container_fragment)
            if (navController != null) {
                navController.navigate(R.id.action_escenasFragment_to_addEscenaFragment)
            }*/
            val dialog = AddEscenaFragment()
            dialog.show(parentFragmentManager, "AddEscenaDialog")
        }

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

}