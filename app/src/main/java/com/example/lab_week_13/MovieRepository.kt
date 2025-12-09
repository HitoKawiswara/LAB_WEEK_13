package com.example.lab_week_13

import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDao
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val movieService: MovieService,
    private val movieDao: MovieDao
) {

    // API key kamu
    private val apiKey = "4ebb92bb9df6dd74c5c162ab29810a81"

    // fetch movies from the API / DB
    // 1. cek DB dulu
    // 2. kalau kosong → fetch dari API, simpan ke DB, lalu emit
    // 3. kalau ada → langsung emit dari DB
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            // Ambil data dari DB dulu
            val savedMovies = movieDao.getMovies()

            if (savedMovies.isEmpty()) {
                // Tidak ada di DB → fetch dari API
                val movies = movieService.getPopularMovies(apiKey).results

                // Simpan hasil API ke DB
                movieDao.addMovies(movies)

                // Emit data dari API
                emit(movies)
            } else {
                // Ada di DB → langsung emit dari DB
                emit(savedMovies)
            }
        }.flowOn(Dispatchers.IO)
    }
}