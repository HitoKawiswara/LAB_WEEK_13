import android.util.Log
import com.example.lab_week_13.api.MovieService
import com.example.lab_week_13.database.MovieDao
import com.example.lab_week_13.database.MovieDatabase
import com.example.lab_week_13.model.Movie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class MovieRepository(
    private val movieService: MovieService,
    private val movieDatabase: MovieDatabase
) {

    // API key kamu
    private val apiKey = "4ebb92bb9df6dd74c5c162ab29810a81"

    // Cek DB dulu; kalau kosong baru fetch dari API dan simpan ke DB
    fun fetchMovies(): Flow<List<Movie>> {
        return flow {
            val movieDao: MovieDao = movieDatabase.movieDao()
            val savedMovies = movieDao.getMovies()

            if (savedMovies.isEmpty()) {
                val movies = movieService.getPopularMovies(apiKey).results
                movieDao.addMovies(movies)
                emit(movies)
            } else {
                emit(savedMovies)
            }
        }.flowOn(Dispatchers.IO)
    }

    // fetch movies dari API lalu simpan ke database
    // dipakai WorkManager untuk refresh data berkala
    suspend fun fetchMoviesFromNetwork() {
        val movieDao: MovieDao = movieDatabase.movieDao()
        try {
            val popularMovies = movieService.getPopularMovies(apiKey)
            val moviesFetched = popularMovies.results
            movieDao.addMovies(moviesFetched)
        } catch (exception: Exception) {
            Log.d(
                "MovieRepository",
                "An error occurred: ${exception.message}"
            )
        }
    }
}