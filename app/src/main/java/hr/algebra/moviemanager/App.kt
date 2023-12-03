package hr.algebra.moviemanager

import android.app.Application
import hr.algebra.moviemanager.dao.MovieDao
import hr.algebra.moviemanager.dao.MovieDatabase

class App : Application() {

    private lateinit var movieDao: MovieDao

    override fun onCreate() {
        super.onCreate()
        var db = MovieDatabase.getInstance(this)
        movieDao = db.movieDao()
    }

    fun getMovieDao() = movieDao
}