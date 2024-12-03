package com.example.proyecto_guion


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_guion.databinding.BotonObrasBinding // Asegúrate de usar el nombre correcto
import java.io.File

class FolderAdapter(
    private var folders: List<File>, // Lista de carpetas
    private val onFolderClick: (File) -> Unit, /*Función que se llama cuando se hace clic en una carpeta*/
    private val onLongClick: (File) -> Unit
) : RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    private var selectionEnabled = false
    private var onSelect: ((File) -> Unit)? = null
    private var selectedFolder: File? = null

    fun enableSelection(onSelect: (File) -> Unit) {
        selectionEnabled = true
        this.onSelect = onSelect
        notifyDataSetChanged()
    }

    fun updateFolders(newFolders: List<File>) {
        folders = newFolders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val binding = BotonObrasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folders[position]
        holder.bind(folder, onFolderClick, onLongClick, selectionEnabled, onSelect, folder == selectedFolder)
    }

    override fun getItemCount() = folders.size

    inner class FolderViewHolder(private val binding: BotonObrasBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            folder: File,
            onClick: (File) -> Unit,
            onLongClick: (File) -> Unit,
            selectionEnabled: Boolean,
            onSelect: ((File) -> Unit)?,
            isSelected: Boolean
        ) {
            binding.folderButtonObras.text = folder.name

            // Configurar click según el modo
            binding.folderButtonObras.setOnClickListener {
                if (selectionEnabled) {
                    onSelect?.invoke(folder)
                } else {
                    onClick(folder)
                }
            }

            // Configurar pulsación larga
            binding.folderButtonObras.setOnLongClickListener {
                onLongClick(folder) // Llamar a la función pasada como callback
                true // Indicar que el evento fue manejado
            }

            // Cambiar estilo según el estado de selección
            val backgroundResource = if (selectionEnabled && isSelected) {
                R.drawable.boton_custom_rojo
            } else {
                R.drawable.boton_custom
            }
            binding.folderButtonObras.setBackgroundResource(backgroundResource)
        }
    }
}