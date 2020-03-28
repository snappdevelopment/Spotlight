package com.snad.spotlight.ui.library

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.snad.spotlight.*
import com.snad.spotlight.databinding.FragmentLibraryBinding
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var binding: FragmentLibraryBinding? = null
    private val viewBinding: FragmentLibraryBinding
        get() = binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewAdapter: LibraryAdapter
    private lateinit var loadingProgressBar: ContentLoadingProgressBar
    private val movies = mutableListOf<LibraryMovie>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        activity?.transparentStatusBarEnabled(false)
//        activity?.setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false)
//        activity?.setWindowFlag(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, false)
//        activity?.setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)

        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        loadingProgressBar = viewBinding.loadingProgressbar
        recyclerView = viewBinding.recyclerView
        recyclerViewAdapter = LibraryAdapter(
            movies,
            this::movieLongClickListener,
            this::movieClickListener,
            this::movieWatchedClickListener)
        recyclerView.adapter = recyclerViewAdapter

        val app = context!!.applicationContext as App
        val libraryDb = LibraryDb(app.appDb)
        val libraryRepository = LibraryRepository(libraryDb)

        libraryViewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return LibraryViewModel(libraryRepository) as T
            }
        }).get(LibraryViewModel::class.java)

        libraryViewModel.state.observe(viewLifecycleOwner, Observer { state ->
            when(state) {
                is LibraryState.DoneState -> showDoneState(state.libraryMovies)
                is LibraryState.LoadingState -> showLoadingState()
                is LibraryState.ErrorState -> showErrorState()
            }
        })

//        val fakeMovie = LibraryMovie(
//            1112,
//            Calendar.getInstance(),
//            false,
//            false,
//            "/pCUdYAaarKqY2AAUtV6xXYO8UGY.jpg",
//            100000,
//            "Drama, Comedy",
//            null,
//            "A ticking-time-bomb insomniac and a slippery soap salesman channel primal male aggression into a shocking new form of therapy. Their concept catches on, with underground \"fight clubs\" forming in every town, until an eccentric gets in the way and ignites an out-of-control spiral toward oblivion.",
//            5.2,
//            "/4GpwvwDjgwiShr1UBJIn5fk1gwT.jpg",
//            "2020.02.14",
//            2000000,
//            180,
//            "How much can you know about yourself if you've never been in a fight?",
//            "Fight Club",
//            false,
//            7.8,
//            5)
//        lifecycleScope.launch(Dispatchers.IO) {
//            libraryDb.insertMovie(fakeMovie)
//        }

        libraryViewModel.loadLibraryMovies()



        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun movieLongClickListener(libraryMovie: LibraryMovie) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_delete_movie_title)
            .setMessage(R.string.dialog_delete_movie_message)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                libraryViewModel.deleteLibraryMovie(libraryMovie)
            }
            .setNegativeButton(android.R.string.cancel) { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun movieClickListener(id: Int) {
        val bundle = Bundle()
        bundle.putInt("id", id)
        findNavController().navigate(R.id.action_navigation_library_to_navigation_movie_details, bundle)
    }

    private fun movieWatchedClickListener(libraryMovie: LibraryMovie) {
        libraryViewModel.updateLibraryMovie(libraryMovie)
    }

    private fun showDoneState(libraryMovies: List<LibraryMovie>) {
        loadingProgressBar.hide()

        movies.clear()
        movies.addAll(libraryMovies)
        recyclerViewAdapter.notifyDataSetChanged()
    }

    private fun showLoadingState() {
        loadingProgressBar.show()
    }

    private fun showErrorState() {
        loadingProgressBar.hide()

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_wrong_operation_title)
            .setMessage(R.string.dialog_wrong_operation_message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                activity?.finishAndRemoveTask()
            }
            .create()
            .show()
    }
}