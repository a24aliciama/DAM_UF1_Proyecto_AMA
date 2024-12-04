package com.example.proyecto_guion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView

class PersonajeAdapter : RecyclerView.Adapter<PersonajeAdapter.PersonajeViewHolder>(){

        private var personajesCount: Int = 0
        private val personajesNames = mutableListOf<String>()

        fun setPersonajeCount(count: Int) {
            personajesCount = count
            personajesNames.clear()
            notifyDataSetChanged()  // Notify adapter to refresh the view with new inputs
        }

        fun getPersonajeNameAt(position: Int): String {
            return personajesNames.getOrNull(position).orEmpty()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonajeViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.personaje_input, parent, false)
            return PersonajeViewHolder(view)
        }

        override fun onBindViewHolder(holder: PersonajeViewHolder, position: Int) {
            holder.bind(position)
        }

        override fun getItemCount(): Int {
            return personajesCount
        }

        inner class PersonajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val inputPersonaje: EditText = itemView.findViewById(R.id.InputText_personaje)

            fun bind(position: Int) {
                inputPersonaje.hint = "Personaje ${position + 1}"
                inputPersonaje.addTextChangedListener {
                    if (position < personajesNames.size) {
                        personajesNames[position] = it.toString()
                    } else {
                        personajesNames.add(it.toString())
                    }
                }
            }
        }

}