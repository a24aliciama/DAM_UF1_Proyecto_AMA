package com.example.proyecto_guion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.proyecto_guion.databinding.FragmentChatBinding
import com.example.proyecto_guion.databinding.FragmentEscenasBinding
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class ChatFragment : Fragment() {

    var bindingNull: FragmentChatBinding? = null
    val binding: FragmentChatBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    // Lista que mantendrá los elementos del chat
    private val chatItems = mutableListOf<ChatItem>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingNull = FragmentChatBinding.inflate(inflater, container, false)

        // Cargar los mensajes existentes desde el archivo JSON al iniciar la aplicación
        loadExistingChat()

        // Configurar el RecyclerView y el Adapter
        chatAdapter = ChatAdapter(chatItems)
        binding.chatRecyclerView.adapter = chatAdapter


        // Llamar a la función que carga los chips
        loadChipsFromJson()

        binding.sendButton.setOnClickListener {
            val messageText = binding.messageEditText.text.toString().trim()

            // Verificar que se haya seleccionado un chip
            val selectedChipText = getSelectedChipText()

            if (selectedChipText.isEmpty()) {
                // Mostrar un mensaje de error si no se selecciona un chip
                Toast.makeText(requireContext(), "Debe seleccionar un chip para enviar el mensaje", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar si el mensaje no está vacío
            if (messageText.isNotEmpty()) {
                val chatItem = when (selectedChipText) {
                    "personaje" -> ChatItem("personaje", "Nombre del personaje", messageText)
                    "elenco" -> ChatItem("elenco", "Nombre del elenco", messageText)
                    "anotacion" -> ChatItem("anotacion", null, messageText)
                    else -> return@setOnClickListener  // No se seleccionó un chip válido
                }

                // Agregar el nuevo ítem al RecyclerView
                chatItems.add(chatItem)

                // Notificar al adapter que se ha insertado un nuevo ítem
                chatAdapter.notifyItemInserted(chatItems.size - 1)

                // Desplazar el RecyclerView hasta el último mensaje
                binding.chatRecyclerView.scrollToPosition(chatItems.size - 1)

                // Limpiar el campo de texto
                binding.messageEditText.text.clear()

                // Guardar el chat actualizado en el archivo JSON
                saveChatToJson(chatItems, model.selectedFolderEscena.value!!)
            }
        }

        return binding.root

    }

    private fun loadExistingChat() {
        val selectedFolder = model.selectedFolderEscena.value
        if (selectedFolder == null) {
            Toast.makeText(requireContext(), "No se ha seleccionado una carpeta para la escena", Toast.LENGTH_SHORT).show()
            return
        }

        // Archivo chat.json dentro de la carpeta seleccionada
        val chatFile = File(selectedFolder, "chat.json")
        if (chatFile.exists()) {
            // Leer datos del archivo y agregarlos a la lista
            try {
                val loadedChatItems = loadChatFromJson(chatFile)
                chatItems.clear() // Limpiar la lista actual antes de añadir nuevos datos
                chatItems.addAll(loadedChatItems)

                // Notificar al adapter que los datos han cambiado
                chatAdapter.notifyDataSetChanged()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al cargar el chat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "El archivo chat.json no existe en la carpeta seleccionada", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadChatFromJson(folder: File): List<ChatItem> {
        val chatFile = File(folder, "chat.json")
        return if (chatFile.exists()) {
            try {
                val gson = Gson()
                val fileReader = FileReader(chatFile)
                gson.fromJson(fileReader, Array<ChatItem>::class.java).toList()
            } catch (e: IOException) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun saveChatToJson(chatItems: List<ChatItem>, folder: File) {
        val chatFile = File(folder, "chat.json")
        try {
            val gson = Gson()
            val json = gson.toJson(chatItems)
            FileWriter(chatFile).use { writer ->
                writer.write(json)
            }
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Error al guardar el chat", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedChipText(): String {
        // Aquí debes implementar la lógica para obtener el texto del chip seleccionado.
        // Este es un ejemplo simple, y deberías adaptarlo según tu diseño de chips.
        return binding.chipGroup.checkedChipId.takeIf { it != View.NO_ID }
            ?.let { binding.chipGroup.findViewById<Chip>(it)?.text.toString() }
            ?: ""
    }

    private fun loadChipsFromJson() {
        // Obtener la carpeta seleccionada desde el ViewModel
        val selectedFolder = model.selectedFolderEscena.value
        val elencoFile = selectedFolder?.let { File(it, "elenco.json") }

        // Verificar si el archivo existe
        if (elencoFile != null && elencoFile.exists()) {
            try {
                // Leer y parsear el archivo JSON
                val gson = Gson()
                val fileReader = FileReader(elencoFile)
                val jsonData = gson.fromJson(fileReader, ElencoData::class.java)

                // Agregar el chip de anotación
                addChip("//", "anotacion")

                // Agregar el chip del personaje
                jsonData.personaje?.let {
                    // Agregar el chip de anotación
                    addChip(it, "personaje")  // Chip para el personaje principal
                }

                // Agregar chips para cada personaje en el elenco
                jsonData.elenco?.forEach { personaje ->
                    addChip(personaje, "elenco")  // Chips para los personajes del elenco
                }

            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Error al leer el archivo JSON", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Archivo elenco.json no encontrado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addChip(label: String, type: String) {
        val chip = LayoutInflater.from(requireContext()).inflate(
            when (type) {
                "personaje" -> R.layout.personaje_chip // Reemplaza con el ID correcto del XML de chip
                "elenco" -> R.layout.elenco_chip // Reemplaza con el ID correcto del XML de chip
                "anotacion" -> R.layout.anotacion_chip // Reemplaza con el ID correcto del XML de chip
                else -> throw IllegalArgumentException("Tipo de chip desconocido")
            }, binding.chipGroup, false) as Chip

        chip.text = label
        chip.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Desmarcar otros chips cuando se seleccione este
                uncheckOtherChips(chip)
            }
        }
        binding.chipGroup.addView(chip)
    }

    private fun uncheckOtherChips(selectedChip: Chip) {
        // Desmarcar todos los demás chips
        for (i in 0 until binding.chipGroup.childCount) {
            val chip = binding.chipGroup.getChildAt(i) as Chip
            if (chip != selectedChip) {
                chip.isChecked = false
            }
        }
    }


    // Clase de datos para parsear el JSON
    data class ElencoData(
        val personaje: String?,
        val elenco: List<String>?
    )

}