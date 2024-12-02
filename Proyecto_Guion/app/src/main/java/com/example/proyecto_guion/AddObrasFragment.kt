package com.example.proyecto_guion

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.proyecto_guion.databinding.FragmentAddObrasBinding
import com.example.proyecto_guion.databinding.FragmentObrasBinding
import java.io.File

class AddObrasFragment : DialogFragment(){

    var bindingNull: FragmentAddObrasBinding? = null
    val binding: FragmentAddObrasBinding
    get() = bindingNull!!

    val model : GuionViewModel by viewModels(
        ownerProducer = {this.requireActivity()}
    )

    private var listener: OnNameEnteredListener? = null
    interface OnNameEnteredListener {
        fun onNameEntered(name: String)
    }

    fun setOnNameEnteredListener(listener: OnNameEnteredListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        bindingNull = FragmentAddObrasBinding.inflate(inflater,container,false)
        val vista = binding.root

        binding.ButtonCrearObras.setOnClickListener {
            val folderName = binding.inputNameObras.text.toString()

            if (folderName.isNotEmpty()) {
                // Crear la carpeta en ProyectoGuion
                val proyectoGuionDir = File(requireContext().filesDir, "ProyectoGuion")
                val newFolder = File(proyectoGuionDir, folderName)

                if (!newFolder.exists()) {
                    newFolder.mkdir()  // Crea la carpeta

                    // Notificar al usuario
                    Toast.makeText(
                        requireContext(),
                        "Carpeta '$folderName' creada",
                        Toast.LENGTH_SHORT
                    ).show()

                    val navController = activity?.findNavController(R.id.container_fragment)
                    if (navController?.currentDestination?.id != R.id.obrasFragment) {
                        if (navController != null) {
                            navController.navigate(R.id.action_addObrasFragment_to_obrasFragment)
                        }
                    }
                    // Cerrar el diálogo
                    dismiss()
                } else {
                    // La carpeta ya existe
                    Toast.makeText(requireContext(), "La carpeta ya existe", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                // Si el nombre está vacío
                Toast.makeText(requireContext(), "Por favor, ingrese un nombre", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        return vista
    }

    private fun createFolder(name: String) {
        // Obtener la ruta del almacenamiento interno donde queremos crear las carpetas
        val proyectoGuionFolder = File(requireContext().filesDir, "ProyectoGuion")

        // Verificar si la carpeta principal "ProyectoGuion" existe, si no, crearla
        if (!proyectoGuionFolder.exists()) {
            proyectoGuionFolder.mkdir()  // Crear la carpeta ProyectoGuion
        }

        // Crear una nueva carpeta dentro de "ProyectoGuion" con el nombre que el usuario ingresó
        val newFolder = File(proyectoGuionFolder, name)
        if (!newFolder.exists()) {
            val folderCreated = newFolder.mkdir()  // Crear la carpeta
            if (folderCreated) {
                Log.d("AddObrasFragment", "Carpeta '$name' creada con éxito en $proyectoGuionFolder")
            } else {
                Log.e("AddObrasFragment", "No se pudo crear la carpeta '$name'")
            }
        } else {
            Log.d("AddObrasFragment", "La carpeta '$name' ya existe.")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (dialog?.window != null) {
            dialog?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_DIM_BEHIND,
                WindowManager.LayoutParams.FLAG_DIM_BEHIND
            )
        }
    }
}