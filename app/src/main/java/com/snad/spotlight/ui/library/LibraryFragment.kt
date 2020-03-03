package com.snad.spotlight.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var binding: FragmentLibraryBinding? = null
    private val viewBinding: FragmentLibraryBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        libraryViewModel = ViewModelProvider(this).get(LibraryViewModel::class.java)
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        val textView: TextView = viewBinding.textDashboard
        libraryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}