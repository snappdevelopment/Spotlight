package com.snad.feature.castdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.snad.feature.castdetails.model.Person
import com.snad.feature.castdetails.repository.PersonRepository
import com.snad.feature.castdetails.repository.PersonResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Clock
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.inject.Inject

internal class CastDetailsViewModel(
    private val personRepository: PersonRepository,
    private val ioDispatcher: CoroutineDispatcher,
    private val clock: Clock,
    private val dateTimeFormatter: DateTimeFormatter
) : ViewModel() {

    private val _state = MutableStateFlow<CastDetailsState>(CastDetailsState.LoadingState)
    val state = _state.asStateFlow()

    fun loadCastDetails(id: Int) {
        updateState(CastDetailsState.LoadingState)
        viewModelScope.launch(ioDispatcher) {
            val result = personRepository.loadPerson(id)
            when(result) {
                is PersonResult.Success -> updateState(prepareDoneState(result))
                is PersonResult.NetworkError -> updateState(CastDetailsState.NetworkErrorState)
                is PersonResult.ConnectionError -> updateState(CastDetailsState.NetworkErrorState)
                is PersonResult.AuthenticationError -> updateState(CastDetailsState.AuthenticationErrorState)
                is PersonResult.ApiError -> updateState(CastDetailsState.ErrorState)
                is PersonResult.Error -> updateState(CastDetailsState.ErrorState)
            }
        }
    }

    private fun prepareDoneState(result: PersonResult.Success): CastDetailsState.DoneState {
        val sortedMovies = result.person.person_credits.cast.sortedByDescending { cast -> cast.release_date }.filterNot { cast ->
            if(cast.release_date != null && cast.release_date.isNotEmpty()) {
                val releaseDate = LocalDate.parse(cast.release_date)
                releaseDate.isAfter(LocalDate.now()) && cast.poster_path == null
            }
            else false
        }

        var birthdayString: String? = null
        var deathdayString: String? = null
        val birthday = result.person.birthday
        val deathday = result.person.deathday
        if(birthday != null && birthday.isNotEmpty()) {
            val birthdayDate = LocalDate.parse(birthday)
            val endDate = if(deathday == null || deathday.isEmpty()) LocalDate.now(clock) else LocalDate.parse(deathday)

            val age = Period.between(
                LocalDate.of(birthdayDate.year, birthdayDate.month, birthdayDate.dayOfMonth),
                LocalDate.of(endDate.year, endDate.month, endDate.dayOfMonth)
            ).years

            birthdayString = LocalDate.parse(birthday).format(
                DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
            )

            if(deathday != null && deathday.isNotEmpty()) {
                deathdayString = LocalDate.parse(deathday).format(
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                ).plus(" (${age})")
            }
            else birthdayString = birthdayString.plus(" (${age})")
        }

        return CastDetailsState.DoneState(
            result.person.copy(
                person_credits = result.person.person_credits.copy(cast = sortedMovies),
                birthday = birthdayString,
                deathday = deathdayString
            )
        )
    }

    private fun updateState(state: CastDetailsState) {
        _state.value = state
    }

    class Factory @Inject constructor(
        private  val personRepository: PersonRepository,
        private val ioDispatcher: CoroutineDispatcher,
        private val clock: Clock,
        private val dateTimeFormatter: DateTimeFormatter
    ): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CastDetailsViewModel(personRepository, ioDispatcher, clock, dateTimeFormatter) as T
        }
    }
}

internal sealed class CastDetailsState {
    data class DoneState(val person: Person): CastDetailsState()
    object LoadingState: CastDetailsState()
    object NetworkErrorState: CastDetailsState()
    object AuthenticationErrorState: CastDetailsState()
    object ErrorState: CastDetailsState()
}