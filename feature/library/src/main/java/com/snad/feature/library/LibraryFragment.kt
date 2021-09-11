package com.snad.feature.library

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.snad.core.arch.observeWithLifecycle
import com.snad.feature.library.databinding.FragmentLibraryBinding
import com.snad.core.persistence.models.LibraryMovie
import com.snad.feature.library.repository.LibraryRepository
import javax.inject.Inject

class LibraryFragment : Fragment() {

    private val libraryViewModel: LibraryViewModel by viewModels { viewModelFactory }
    private var binding: FragmentLibraryBinding? = null
    private val viewBinding: FragmentLibraryBinding
        get() = binding!!

    lateinit var libraryComponent: LibraryComponent

    @Inject
    internal lateinit var libraryRepository: LibraryRepository

    @Inject
    internal lateinit var viewModelFactory: LibraryViewModel.Factory

    private var libraryAdapter: LibraryAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activity?.window?.statusBarColor = resources.getColor(R.color.colorPrimaryDark, null)

        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        inject()

        libraryAdapter = LibraryAdapter(
            this::movieClickListener,
            this::movieLongClickListener,
            this::movieWatchedClickListener
        )
        viewBinding.recyclerView.adapter = libraryAdapter

        libraryViewModel.state.observeWithLifecycle(this) { state ->
            when (state) {
                is LibraryState.DoneState -> showDoneState(state.libraryMovies)
                is LibraryState.EmptyState -> showEmptyState()
                is LibraryState.LoadingState -> showLoadingState()
                is LibraryState.ErrorState -> showErrorState()
            }
        }

        libraryViewModel.handleAction(LoadMovies)

        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        libraryAdapter = null
    }

    private fun movieLongClickListener(libraryMovie: LibraryMovie) {
        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_delete_movie_title)
            .setMessage(R.string.dialog_delete_movie_message)
            .setPositiveButton(android.R.string.ok) { dialog, which ->
                libraryViewModel.handleAction(DeleteMovie(libraryMovie))
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
        libraryViewModel.handleAction(UpdateMovie(libraryMovie))
    }

    private fun showDoneState(libraryMovies: List<LibraryMovie>) {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 1f
        viewBinding.emptyLibraryIconImageView.visibility = View.INVISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.INVISIBLE

        libraryAdapter?.submitList(libraryMovies)
    }

    private fun showEmptyState() {
        viewBinding.loadingProgressbar.hide()
        viewBinding.headerBackground.pivotY = 0f
        viewBinding.headerBackground.scaleY = 0.75f
        viewBinding.emptyLibraryIconImageView.visibility = View.VISIBLE
        viewBinding.emptyLibraryTextView.visibility = View.VISIBLE

        libraryAdapter?.submitList(emptyList())
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