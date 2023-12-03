package hr.algebra.moviemanager

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import hr.algebra.moviemanager.dao.Movie
import hr.algebra.moviemanager.dao.MovieDao
import hr.algebra.moviemanager.dao.MovieDatabase
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class MovieUnitTest {
    private lateinit var database: MovieDatabase
    private lateinit var movieDao: MovieDao

    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, MovieDatabase::class.java).build()
        movieDao = database.movieDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndReadMovie() {
        // Create a new movie object
        val newMovie = Movie(
            title = "Test Movie",
            description = "This is a test movie",
            picturePath = "/path/to/image.jpg",
            addedDate = LocalDate.now()
        )

        // Insert the movie into the database
        movieDao.insert(newMovie)

        // Retrieve all movies from the database
        val movies = movieDao.getMovies()

        val retrievedMovie = movies.lastOrNull { it.title == newMovie.title && it.description == newMovie.description }
            ?: throw AssertionError("Retrieved movie should not be null")

        assertEquals(retrievedMovie.title, newMovie.title)
        assertEquals(retrievedMovie.description, newMovie.description)
        assertEquals(retrievedMovie.picturePath, newMovie.picturePath)
        assertEquals(retrievedMovie.addedDate, newMovie.addedDate)
    }


}