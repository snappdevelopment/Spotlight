package com.snad.spotlight.ui.castDetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.snad.spotlight.R
import com.snad.spotlight.network.models.Person
import com.snad.spotlight.repository.PersonRepository
import com.snad.spotlight.repository.PersonResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
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
                    is PersonResult.Success -> state.value = prepareDoneState(result)
                    is PersonResult.NetworkError -> state.value = CastDetailsState.NetworkErrorState
                    is PersonResult.ConnectionError -> state.value = CastDetailsState.NetworkErrorState
                    is PersonResult.AuthenticationError -> state.value = CastDetailsState.AuthenticationErrorState
                    is PersonResult.ApiError -> state.value = CastDetailsState.ErrorState
                    is PersonResult.Error -> state.value = CastDetailsState.ErrorState
                }
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
            val endDate = if(deathday == null || deathday.isEmpty()) LocalDate.now() else LocalDate.parse(deathday)

            val age = Period.between(
                LocalDate.of(birthdayDate.year, birthdayDate.month, birthdayDate.dayOfMonth),
                LocalDate.of(endDate.year, endDate.month, endDate.dayOfMonth)
            ).years

            birthdayString = LocalDate.parse(birthday).format(DateTimeFormatter.ofLocalizedDate(
                FormatStyle.SHORT))

            if(deathday != null && deathday.isNotEmpty()) {
                deathdayString = LocalDate.parse(deathday).format(DateTimeFormatter.ofLocalizedDate(
                    FormatStyle.SHORT)).plus(" (${age})")
            }
            else birthdayString = birthdayString.plus(" (${age})")
        }

        return CastDetailsState.DoneState(result.person.copy(
            person_credits = result.person.person_credits.copy(cast = sortedMovies),
            birthday = birthdayString,
            deathday = deathdayString
        ))
    }
}

sealed class CastDetailsState {
    class DoneState(val person: Person): CastDetailsState()
    object LoadingState: CastDetailsState()
    object NetworkErrorState: CastDetailsState()
    object AuthenticationErrorState: CastDetailsState()
    object ErrorState: CastDetailsState()
}