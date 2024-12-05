package com.example.proyecto_guion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_guion.databinding.AnotacionTextBinding
import com.example.proyecto_guion.databinding.ChatTextBinding
import com.example.proyecto_guion.databinding.TuchatTextBinding

class ChatAdapter(private val chatItems: List<ChatItem>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PERSONAJE -> {
                val binding = TuchatTextBinding.inflate(inflater, parent, false)
                PersonajeViewHolder(binding)
            }
            TYPE_ELENCO -> {
                val binding = ChatTextBinding.inflate(inflater, parent, false)
                ElencoViewHolder(binding)
            }
            TYPE_ANOTACION -> {
                val binding = AnotacionTextBinding.inflate(inflater, parent, false)
                AnotacionViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = chatItems[position]
        when (holder) {
            is PersonajeViewHolder -> holder.bind(item)
            is ElencoViewHolder -> holder.bind(item)
            is AnotacionViewHolder -> holder.bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (chatItems[position].type) {
            "personaje" -> TYPE_PERSONAJE
            "elenco" -> TYPE_ELENCO
            "anotacion" -> TYPE_ANOTACION
            else -> throw IllegalArgumentException("Unknown item type")
        }
    }

    override fun getItemCount(): Int = chatItems.size

    companion object {
        const val TYPE_PERSONAJE = 1
        const val TYPE_ELENCO = 2
        const val TYPE_ANOTACION = 3
    }

    // ViewHolder para "personaje"
    inner class PersonajeViewHolder(private val binding: TuchatTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatItem) {
            binding.textViewTuNombre.text = item.name
            binding.textViewTuDialogo.text = item.text
        }
    }

    // ViewHolder para "elenco"
    inner class ElencoViewHolder(private val binding: ChatTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatItem) {
            binding.textViewNombre.text = item.name
            binding.textViewDialogo.text = item.text
        }
    }

    // ViewHolder para "anotacion"
    inner class AnotacionViewHolder(private val binding: AnotacionTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatItem) {
            binding.textViewAnotacion.text = item.text
        }
    }
}