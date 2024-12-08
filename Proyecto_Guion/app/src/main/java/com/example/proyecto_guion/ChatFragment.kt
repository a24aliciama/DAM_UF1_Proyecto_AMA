package com.example.proyecto_guion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto_guion.databinding.FragmentChatBinding
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class ChatFragment : Fragment() {

    var bindingNull: FragmentChatBinding? = null
    val binding: FragmentChatBinding
        get() = bindingNull!!

    val model: GuionViewModel by viewModels(ownerProducer = { this.requireActivity() })

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingNull = FragmentChatBinding.inflate(inflater, container, false)

        loadMessagesFromJson()
        // Llamar a la función que carga los chips
        loadChipsFromJson()

        // Configuramos el FAB para alternar la visibilidad del TextView
        binding.botonfloatID.setOnClickListener {
            chatAdapter.isTextViewVisible = !chatAdapter.isTextViewVisible  // Alternamos la visibilidad
            chatAdapter.notifyDataSetChanged()  // Notificamos que los datos han cambiado para que el RecyclerView se actualice
        }

        binding.sendButton.setOnClickListener {

            val selectedChipId = binding.chipGroup.checkedChipId
            if (selectedChipId == View.NO_ID) {
                // Ningún chip está seleccionado
                Toast.makeText(requireContext(), "Por favor, selecciona un personaje antes de enviar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                // Obtener el texto del chip seleccionado
                val selectedChip = binding.chipGroup.findViewById<Chip>(selectedChipId)
                val characterName = selectedChip.text.toString()
                val messageText = binding.messageEditText.text.toString().trim()
                val messageType = getMessageTypeFromChip(selectedChipId)

                if (messageText.isNotEmpty()) {
                    // Guardar el mensaje junto con el personaje seleccionado
                    saveMessageToJsonFile(messageText, characterName, messageType)
                    binding.messageEditText.text.clear() // Limpiar el campo de texto
                    Toast.makeText(requireContext(), "Mensaje enviado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "El mensaje está vacío", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            loadMessagesFromJson()
        }
        return binding.root
    }

    //cargar mensajes
    private fun loadMessagesFromJson() {
        val chatFile = File(model.selectedFolderEscena.value, "chat.json")
        val messages: MutableList<ChatMessage> = if (chatFile.exists()) {
            try {
                FileReader(chatFile).use { reader ->
                    val type = object : TypeToken<List<ChatMessage>>() {}.type
                    Gson().fromJson<List<ChatMessage>>(reader, type)
                }.toMutableList()
            } catch (e: Exception) {
                mutableListOf() // Si hay algún error, inicializamos una nueva lista vacía
            }
        } else {
            mutableListOf() // Si el archivo no existe, inicializamos una nueva lista vacía
        }

        // Configurar el RecyclerView
        chatAdapter = ChatAdapter(messages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerView.adapter = chatAdapter
    }

    //JSON
    private fun saveMessageToJsonFile(message: String, characterName: String, messageType: String) {
        val chatFile = File(model.selectedFolderEscena.value, "chat.json")

        val newMessageData = mapOf(
            "message" to message,
            "character" to characterName,
            "type" to messageType
        )

        // Crear una lista para almacenar los mensajes
        val messages: MutableList<Map<String, String>> = if (chatFile.exists()) {
            try {
                FileReader(chatFile).use { reader ->
                    val type = object : TypeToken<MutableList<Map<String, String>>>() {}.type
                    Gson().fromJson(reader, type)
                }
            } catch (e: Exception) {
                mutableListOf() // Si hay algún error, inicializamos una nueva lista
            }
        } else {
            mutableListOf()
        }

        // Añadir el nuevo mensaje
        messages.add(newMessageData)

        try {
            // Verificar si el archivo existe, si no, lo creamos
            if (!chatFile.exists()) {
                chatFile.createNewFile() // Crea el archivo si no existe
            }

            // Escribir la lista de mensajes actualizada en el archivo JSON
            FileWriter(chatFile).use { writer ->
                Gson().toJson(messages, writer)
            }

            // Mostrar un mensaje de éxito
            Toast.makeText(requireContext(), "Mensaje guardado en chat.json", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // Manejar cualquier error al guardar el archivo
            Toast.makeText(requireContext(), "Error al guardar el mensaje", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMessageTypeFromChip(chipId: Int): String {
        return when (chipId) {
            R.id.chipPersonaje -> "personaje"
            R.layout.anotacion_chip -> "anotacion"
            else -> {
                // Si no es ninguno de los anteriores, verificamos el tag del chip (para chips dinámicos de elenco)
                val chip = binding.chipGroup.findViewById<Chip>(chipId)
                when (chip?.tag) {
                    "elenco" -> "elenco"  // Si el tag es "elenco", lo tratamos como un chip de tipo elenco
                    else -> "desconocido"  // Si no tiene tag o es de otro tipo, lo marcamos como desconocido
                }
            }
        }
    }



    //CHIP

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
                "personaje" -> R.layout.personaje_chip
                "elenco" -> R.layout.elenco_chip
                "anotacion" -> R.layout.anotacion_chip
                else -> throw IllegalArgumentException("Tipo de chip desconocido")
            }, binding.chipGroup, false) as Chip

        // Asignar un ID único al chip solo si es de tipo "elenco"
        if (type == "elenco") {
            chip.id = View.generateViewId()  // Esto genera un ID único dinámicamente solo para los chips de elenco
            chip.tag = "elenco"  // Etiqueta personalizada para identificarlo como "elenco"
        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )
    }
}