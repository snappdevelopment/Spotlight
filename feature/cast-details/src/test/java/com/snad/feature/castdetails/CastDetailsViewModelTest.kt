package com.snad.feature.castdetails

import app.cash.turbine.test
import com.snad.feature.castdetails.model.Cast
import com.snad.feature.castdetails.model.Person
import com.snad.feature.castdetails.model.PersonCredits
import com.snad.feature.castdetails.repository.PersonRepository
import com.snad.feature.castdetails.repository.PersonResult
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalCoroutinesApi
internal class CastDetailsViewModelTest {

    private val cast = Cast(
        character = "",
        credit_id = "",
        release_date = null,
        vote_count = 0,
        video = false,
        adult = false,
        vote_average = 0.0,
        title = "",
        genre_ids = listOf(),
        original_language = "",
        original_title = "",
        popularity = 1.0,
        id = 0,
        backdrop_path = null,
        overview = "",
        poster_path = null
    )

    private val person = Person(
        adult = false,
        also_known_as = listOf(),
        biography = "",
        birthday = "2010-01-01T07:54:15Z",
        deathday = null,
        gender = 0,
        homepage = null,
        id = 0,
        imdb_id = "",
        known_for_department = "",
        name = "",
        place_of_birth = null,
        popularity = 0.0,
        profile_path = null,
        person_credits = PersonCredits(
            cast = listOf(cast, cast.copy(popularity = 2.0)),
            crew = listOf()
        )
    )

    private val repository = TestPersonRepository()
    private val clock = Clock.fixed(Instant.parse("2020-06-06T10:15:30.00Z"), ZoneId.of("Europe/Paris"))

    private val underTest = CastDetailsViewModel(
        personRepository = repository,
        ioDispatcher = TestCoroutineDispatcher(),
        clock = clock,
        dateTimeFormatter = DateTimeFormatter.ISO_DATE
    )

    @Test
    fun `loading cast details succeeds with birthday`() = runBlocking {
        val person = person.copy(
            person_credits = person.person_credits.copy(
                cast = listOf(cast.copy(popularity = 2.0), cast)
            ),
            birthday = "01/01/10 (10)"
        )
        val expectedState = CastDetailsState.DoneState(person)

        underTest.state.test {
            underTest.loadCastDetails(id = 0)
            repository.setResult(PersonResult.Success(person.copy(birthday = "2010-01-01")))

            assertEquals(CastDetailsState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading cast details succeeds with deathday`() = runBlocking {
        val person = person.copy(
            person_credits = person.person_credits.copy(
                cast = listOf(cast.copy(popularity = 2.0), cast)
            ),
            birthday = "01/01/10",
            deathday = "01/01/11 (1)"
        )
        val expectedState = CastDetailsState.DoneState(person)

        underTest.state.test {
            underTest.loadCastDetails(id = 0)
            repository.setResult(PersonResult.Success(person.copy(birthday = "2010-01-01", deathday = "2011-01-01")))

            assertEquals(CastDetailsState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading cast details fails with network error`() = runBlocking {
        val expectedState = CastDetailsState.NetworkErrorState

        underTest.state.test {
            underTest.loadCastDetails(id = 0)
            repository.setResult(PersonResult.NetworkError)

            assertEquals(CastDetailsState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading cast details fails with authentication error`() = runBlocking {
        val expectedState = CastDetailsState.AuthenticationErrorState

        underTest.state.test {
            underTest.loadCastDetails(id = 0)
            repository.setResult(PersonResult.AuthenticationError)

            assertEquals(CastDetailsState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }

    @Test
    fun `loading cast details fails with api error`() = runBlocking {
        val expectedState = CastDetailsState.ErrorState

        underTest.state.test {
            underTest.loadCastDetails(id = 0)
            repository.setResult(PersonResult.ApiError)

            assertEquals(CastDetailsState.LoadingState, expectItem())
            assertEquals(expectedState, expectItem())
            expectNoEvents()
        }
    }
}

private class TestPersonRepository: PersonRepository {

    private val result = MutableSharedFlow<PersonResult>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    suspend fun setResult(newResult: PersonResult) {
        result.emit(newResult)
    }

    override suspend fun loadPerson(id: Int): PersonResult {
        return result.first()
    }
}