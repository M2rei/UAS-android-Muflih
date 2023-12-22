package com.example.uas_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uas_android.database.Movies
import com.example.uas_android.database.MoviesDao
import com.example.uas_android.database.MoviesRoomDatabase
import com.example.uas_android.databinding.ActivityMain3Binding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity3 : AppCompatActivity() {
    private lateinit var binding: ActivityMain3Binding
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollection = firestore.collection("movie")
    private val movieListLiveData: MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }
    private lateinit var moviedb: MoviesRoomDatabase
    private lateinit var executorService: ExecutorService
    private lateinit var mdao: MoviesDao
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMain3Binding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        moviedb = MoviesRoomDatabase.getInstance(applicationContext)
        executorService = Executors.newSingleThreadExecutor()
        mdao = moviedb.moviesDao()

        with(binding){
            fab.setOnClickListener{
                val  intentToAdd_movie_Activity = Intent(this@MainActivity3, Add_movie_Activity::class.java)
                startActivity(intentToAdd_movie_Activity)
            }
        }
        getAllMovie()
        observeMovie()
        gelAllMovies()
    }
    private fun getAllMovie() {
        observeMovieChanges()
        observeLocalMovies()
    }

    private fun observeMovie() {
        movieListLiveData.observe(this) { movies ->
            val adapter = MoviesAdapter(movies)
            binding.recyclerViewadmin.adapter = adapter
            binding.recyclerViewadmin.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun observeMovieChanges() {
        movieCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d("MainActivity3", "Error listening for movie changes: ", error)
                return@addSnapshotListener
            }

            val movies = snapshot?.toObjects(Movies::class.java)

            if (movies != null) {
                executorService.execute {
                    for (movie in movies) {
                        mdao.insert(movie)
                    }
                }
            }
        }
    }

    private fun gelAllMovies() {
        mdao.allMovies.observe(this) { movies ->
            val adapter = MoviesAdapter(movies)
            binding.recyclerViewadmin.adapter = adapter
            binding.recyclerViewadmin.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun observeLocalMovies() {
        mdao.allMovies.observe(this) { localMovies ->
            if (localMovies.isNotEmpty()) {
                movieListLiveData.postValue(localMovies)
            }
        }
    }
}