package com.example.proyecto_guion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_guion.databinding.AnotacionTextBinding
import com.example.proyecto_guion.databinding.ChatTextBinding
import com.example.proyecto_guion.databinding.TuchatTextBinding

class ChatAdapter(private val messages: List<ChatMessage>) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    companion object {
        const val TYPE_PERSONAJE = 1
        const val TYPE_ELENCO = 2
        const val TYPE_ANOTACION = 3
        const val TYPE_DEFAULT = 4
    }
    var isTextViewVisible = true  // Variable para controlar la visibilidad del TextView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // Inflar el layout dependiendo del tipo de mensaje
        val layoutId = when (viewType) {
            TYPE_PERSONAJE -> R.layout.tuchat_text
            TYPE_ELENCO -> R.layout.chat_text
            TYPE_DEFAULT -> R.layout.anotacion_text
            else -> R.layout.chat_text
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatMessage = messages[position]
        holder.bind(chatMessage)
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].type) {
            "personaje" -> TYPE_PERSONAJE
            "elenco" -> TYPE_ELENCO
            "anotacion" -> TYPE_ANOTACION
            else -> TYPE_DEFAULT
        }
    }

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewNombre: TextView = itemView.findViewById(R.id.textViewNombre)
        private val textViewMensaje: TextView = itemView.findViewById(R.id.textViewMensaje)

        fun bind(chatMessage: ChatMessage) {
            textViewNombre.text = chatMessage.character
            textViewMensaje.text = chatMessage.message
            // Si el mensaje es de tipo 'personaje', manejamos la visibilidad del TextView
            if (chatMessage.type == "personaje") {
                textViewMensaje.visibility = if (isTextViewVisible) View.VISIBLE else View.GONE

            }
        }
    }
}