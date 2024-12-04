package com.example.proyecto_guion

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_guion.databinding.FragmentAddEscenaBinding
import com.example.proyecto_guion.databinding.FragmentAddObrasBinding
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.IOException

class AddEscenaFragment : DialogFragment() {
    var bindingNull: FragmentAddEscenaBinding? = null
    val binding: FragmentAddEscenaBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(
        ownerProducer = { this.requireActivity() }
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
        bindingNull = FragmentAddEscenaBinding.inflate(inflater, container, false)
        val vista = binding.root

        val adapter = PersonajeAdapter()

        binding.ButtonCrearEscena.setOnClickListener {
            val escenaName = binding.inputNameEscena.text.toString().trim()
            val personaje = binding.inputPersonaje.text.toString().trim()
            val elencoStr = binding.inputElenco.text.toString().trim()

            if (escenaName.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, ingrese un nombre para la escena.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (personaje.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, ingrese el nombre del personaje.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val elenco = elencoStr.toIntOrNull()
            if (elenco == null || elenco < 1 || elenco > 10) {
                Toast.makeText(requireContext(), "El número de personajes debe ser entre 1 y 10.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar que todos los campos de personaje estén llenos
            if (!AllPersonajes(adapter, elenco)) {
                Toast.makeText(requireContext(), "Por favor, complete todos los campos de personajes.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear la carpeta para la escena
            val selectedFolder = model.selectedFolderObra.value
            selectedFolder?.let { folder ->
                val newSceneFolder = File(folder, escenaName)

                if (!newSceneFolder.exists()) {
                    val success = newSceneFolder.mkdir() // Crear la carpeta de la escena
                    if (success) {
                        Toast.makeText(requireContext(), "Escena creada: $escenaName", Toast.LENGTH_SHORT).show()

                        // Aquí podemos guardar los nombres en un archivo .txt y .json

                        val personajesList = mutableListOf<String>()
                        for (i in 0 until elenco) {
                            val inputName = adapter.getPersonajeNameAt(i)
                            personajesList.add(inputName)
                        }

                        // Guardar en el archivo .txt
                        val txtFile = File(newSceneFolder, "elenco.txt")
                        try {
                            FileWriter(txtFile).use { writer ->
                                writer.write("$personaje\n") // Primero escribe el nombre del personaje
                                personajesList.forEach { name ->
                                    writer.write("$name\n") // Luego los nombres del elenco
                                }
                            }
                            Toast.makeText(requireContext(), "Archivo.txt guardado con éxito.", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            Toast.makeText(requireContext(), "Error al guardar el archivo .txt.", Toast.LENGTH_SHORT).show()
                        }

                        // Guardar en el archivo .json
                        val jsonFile = File(newSceneFolder, "elenco.json")
                        val jsonContent = mapOf(
                            "personaje" to personaje,  // El nombre del personaje principal
                            "elenco" to personajesList  // Lista de nombres del elenco
                        )
                        val gson = Gson()
                        val jsonData = gson.toJson(jsonContent) // Convertir el mapa a JSON
                        try {
                            FileWriter(jsonFile).use { writer ->
                                writer.write(jsonData) // Escribir el JSON en el archivo
                            }
                            Toast.makeText(requireContext(), "Archivo .json guardado con éxito.", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            Toast.makeText(requireContext(), "Error al guardar el archivo .json.", Toast.LENGTH_SHORT).show()
                        }

                        // Guardar los personajes en el ViewModel
                        model.setPersonajes(personajesList)

                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "No se pudo crear la carpeta para la escena.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ya existe una escena con ese nombre.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.containerElenco.layoutManager = LinearLayoutManager(requireContext())
        binding.containerElenco.adapter = adapter

        // When the 'elenco' button is clicked, generate the number of input fields based on the number entered
        binding.botonElenco.setOnClickListener {
            val elencoStr = binding.inputElenco.text.toString().trim()
            val elenco = elencoStr.toIntOrNull()

            if (elenco != null && elenco in 1..10) {
                adapter.setPersonajeCount(elenco)  // Update the number of character inputs dynamically
            } else {
                Toast.makeText(requireContext(), "El número de personajes debe ser entre 1 y 10.", Toast.LENGTH_SHORT).show()
            }
        }

        return vista
    }

    // Función para verificar si todos los campos de personaje están llenos
    private fun AllPersonajes(adapter: PersonajeAdapter, elenco: Int): Boolean {
        for (i in 0 until elenco) {
            // Obtener el nombre del personaje en la posición i
            val inputName = adapter.getPersonajeNameAt(i)
            // Verificar si el inputName es vacío o si es igual al hint (esto indica que el campo está vacío)
            if (inputName.isEmpty() || inputName.contains("Personaje")) {
                return false  // Si el campo está vacío o contiene solo el hint, devolvemos false
            }
        }
        return true  // Si todos los campos están llenos, devolvemos true
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