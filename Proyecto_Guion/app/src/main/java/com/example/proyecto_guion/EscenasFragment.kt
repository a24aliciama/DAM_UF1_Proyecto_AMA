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
import com.example.proyecto_guion.databinding.FragmentObrasBinding
import java.io.File

class EscenasFragment : Fragment() {

    var bindingNull: FragmentEscenasBinding? = null
    val binding: FragmentEscenasBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    private lateinit var adapter: FolderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingNull = FragmentEscenasBinding.inflate(inflater, container, false)

        binding.sideEscena.visibility = View.GONE

        (activity as AppCompatActivity).supportActionBar!!.title = "Tus Escenas"

        adapter = FolderAdapter(
            folders = emptyList(),
            onFolderClick = { folder ->
                Toast.makeText(requireContext(), "Subcarpeta seleccionada: ${folder.name}", Toast.LENGTH_SHORT).show()
            },
            onLongClick = { folder ->
                Toast.makeText(requireContext(), "Opciones para: ${folder.name}", Toast.LENGTH_SHORT).show()
            }
        )

        // Configurar RecyclerView
        binding.containerButtonsEscena.layoutManager = LinearLayoutManager(requireContext())
        binding.containerButtonsEscena.adapter = adapter

        // Observar el ViewModel para obtener la carpeta seleccionada
        model.selectedFolder.observe(viewLifecycleOwner) { folder ->
            folder?.let {
                // Obtener subcarpetas dentro de la carpeta seleccionada
                val subfolders = folder.listFiles()?.filter { it.isDirectory } ?: emptyList()
                adapter.updateFolders(subfolders) // Actualizar el adaptador con las subcarpetas
            }
        }

        return binding.root
    }

}