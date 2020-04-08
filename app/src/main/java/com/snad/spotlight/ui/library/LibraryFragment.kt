package com.snad.spotlight.ui.library

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.snad.spotlight.*
import com.snad.spotlight.databinding.FragmentLibraryBinding
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.util.*

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var binding: FragmentLibraryBinding? = null
    private val viewBinding: FragmentLibraryBinding
        get() = binding!!

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

        val recyclerViewAdapter = LibraryAdapter(
            movies,
            this::movieLongClickListener,
            this::movieClickListener,
            this::movieWatchedClickListener)
        viewBinding.recyclerView.adapter = recyclerViewAdapter

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
                is LibraryState.EmptyState -> showEmptyState()
                is LibraryState.LoadingState -> showLoadingState()
                is LibraryState.ErrorState -> showErrorState()
            }
        })

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

    private fun movieClickListener(
        id: Int,
        coverImageView: ImageView
    ) {
        val extras = FragmentNavigatorExtras(
            coverImageView to "cover${id}"
        )
//        val bundle = Bundle()
//        bundle.putInt("id", id)
        val action = LibraryFragmentDirections.actionNavigationLibraryToNavigationMovieDetails(id)
        findNavController().navigate(action, extras)
//        findNavController().navigate(R.id.action_navigation_library_to_navigation_movie_details, bundle)
    }

    private fun movieWatchedClickListener(libraryMovie: LibraryMovie) {
        libraryViewModel.updateLibraryMovie(libraryMovie)
    }

    private fun showDoneState(libraryMovies: List<LibraryMovie>) {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 1f
        viewBinding.emptyLibraryIconImageView.visibility = View.INVISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.INVISIBLE

        movies.clear()
        movies.addAll(libraryMovies)
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showEmptyState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.emptyLibraryIconImageView.visibility = View.VISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.VISIBLE

        movies.clear()
        viewBinding.recyclerView.adapter?.notifyDataSetChanged()
    }

    private fun showLoadingState() {
        viewBinding.loadingProgressbar.show()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.emptyLibraryIconImageView.visibility = View.INVISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.INVISIBLE
    }

    private fun showErrorState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.emptyLibraryIconImageView.visibility = View.INVISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.INVISIBLE

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