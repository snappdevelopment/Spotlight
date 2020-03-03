package com.snad.spotlight.ui.newMovies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.snad.spotlight.R
import com.snad.spotlight.databinding.FragmentNewMoviesBinding

class NewMoviesFragment : Fragment() {

    private lateinit var newMoviesViewModel: NewMoviesViewModel
    private var binding: FragmentNewMoviesBinding? = null
    private val viewBinding: FragmentNewMoviesBinding
        get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newMoviesViewModel = ViewModelProvider(this).get(NewMoviesViewModel::class.java)
        binding = FragmentNewMoviesBinding.inflate(inflater, container, false)

        val textView: TextView = viewBinding.textHome
        newMoviesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}