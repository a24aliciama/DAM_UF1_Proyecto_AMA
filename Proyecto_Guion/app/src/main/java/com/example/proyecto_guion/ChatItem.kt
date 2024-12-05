package com.example.proyecto_guion

data class ChatItem(
    val type: String,       // Tipo de mensaje: "personaje", "elenco" o "anotacion"
    val name: String?,      // Nombre del personaje o elenco (nulo si es "anotacion")
    val text: String        // El contenido del mensaje o anotación
)