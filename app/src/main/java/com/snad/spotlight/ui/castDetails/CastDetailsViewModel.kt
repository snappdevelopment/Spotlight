package com.snad.spotlight.ui.castDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.network.models.Person
import com.snad.spotlight.repository.PersonRepository
import com.snad.spotlight.repository.PersonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.util.*

class CastDetailsViewModel(
    private val personRepository: PersonRepository
) : ViewModel() {

    val state = MutableLiveData<CastDetailsState>()

    fun loadCastDetails(id: Int) {
        state.value = CastDetailsState.LoadingState
        viewModelScope.launch(Dispatchers.IO) {
            val result = personRepository.loadPerson(id)
            withContext(Dispatchers.Main) {
                when(result) {
                    is PersonResult.Success -> {
                        val sortedMovies = result.person.person_credits.cast.sortedByDescending { cast -> cast.release_date }.dropWhile { cast ->
                            val releaseDate = SimpleDateFormat("yyyy-MM-dd").parse(cast.release_date)
                            if(releaseDate != null)
                                releaseDate.after(Date()) && cast.poster_path == null
                            else
                                false
                        }
                        state.value = CastDetailsState.DoneState(result.person.copy(person_credits = result.person.person_credits.copy(cast = sortedMovies)))
                    }
                    is PersonResult.NetworkError -> state.value = CastDetailsState.NetworkErrorState
                    is PersonResult.ConnectionError -> state.value = CastDetailsState.NetworkErrorState
                    is PersonResult.AuthenticationError -> state.value = CastDetailsState.AuthenticationErrorState
                    is PersonResult.ApiError -> state.value = CastDetailsState.ErrorState
                    is PersonResult.Error -> state.value = CastDetailsState.ErrorState
                }
            }
        }
    }
}

sealed class CastDetailsState {
    class DoneState(val person: Person): CastDetailsState()
    object LoadingState: CastDetailsState()
    object NetworkErrorState: CastDetailsState()
    object AuthenticationErrorState: CastDetailsState()
    object ErrorState: CastDetailsState()
}