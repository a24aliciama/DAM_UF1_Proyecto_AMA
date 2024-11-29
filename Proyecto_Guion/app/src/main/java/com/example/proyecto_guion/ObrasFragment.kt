package com.example.proyecto_guion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.proyecto_guion.databinding.FragmentObrasBinding
import com.google.android.material.appbar.MaterialToolbar

class ObrasFragment : Fragment() {

    var bindingNull: FragmentObrasBinding? = null
    val binding: FragmentObrasBinding
        get() = bindingNull!!

    val model : GuionViewModel by viewModels(
        ownerProducer = {this.requireActivity()}
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        // Inflate the layout for this fragment
        bindingNull = FragmentObrasBinding.inflate(inflater,container,false)
        val vista = binding.root




      //  vista.findNavController().navigate(R.id.obrasFragment)
        return vista
    }
}