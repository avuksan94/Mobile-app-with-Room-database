package hr.algebra.moviemanager.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MovieDao {
    @Query("select * from movies")
    fun getMovies(): MutableList<Movie>

    @Query("select * from movies where _id=:id")
    fun getMovie(id: Long) : Movie?

    @Insert
    fun insert(movie: Movie)

    @Update
    fun update(movie: Movie)

    @Delete
    fun delete(movie: Movie)
}