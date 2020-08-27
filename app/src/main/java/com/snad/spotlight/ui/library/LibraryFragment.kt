package com.snad.spotlight.ui.library

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.snad.spotlight.*
import com.snad.spotlight.databinding.FragmentLibraryBinding
import com.snad.spotlight.persistence.LibraryDb
import com.snad.spotlight.persistence.models.LibraryMovie
import com.snad.spotlight.repository.LibraryRepository

class LibraryFragment : Fragment() {

    private lateinit var libraryViewModel: LibraryViewModel
    private var binding: FragmentLibraryBinding? = null
    private val viewBinding: FragmentLibraryBinding
        get() = binding!!
    private val recyclerViewAdapter = LibraryAdapter(
        this::movieLongClickListener,
        this::movieClickListener,
        this::movieWatchedClickListener
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)

        binding = FragmentLibraryBinding.inflate(inflater, container, false)

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

    private fun movieClickListener(id: Int, coverCardView: CardView) {
        val extras = FragmentNavigatorExtras(
            coverCardView to "cover${id}"
        )
        val action = LibraryFragmentDirections.actionNavigationLibraryToNavigationMovieDetails(id)
        findNavController().navigate(action, extras)
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

        recyclerViewAdapter.submitList(libraryMovies)
    }

    private fun showEmptyState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.emptyLibraryIconImageView.visibility = View.VISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.VISIBLE

        recyclerViewAdapter.submitList(emptyList())
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