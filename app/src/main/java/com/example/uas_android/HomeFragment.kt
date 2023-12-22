package com.example.uas_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uas_android.database.Movies
import com.example.uas_android.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val movieCollection = firestore.collection("movie")
    private var updateId = ""
    private val movieListLiveData: MutableLiveData<List<Movies>> by lazy {
        MutableLiveData<List<Movies>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Mendapatkan Intent dari aktivitas yang memuat fragment
        val username = activity?.intent?.getStringExtra("USERNAME")
        val notelepon = activity?.intent?.getStringExtra("PHONE_NUMBER")
        val email = activity?.intent?.getStringExtra("EMAIL")

        with(binding) {
            // Menampilkan username di tkshome2
            tkshome2.text = username

        }

        getAllMovie()
        observeMovie()

        return binding.root
    }
    private fun getAllMovie(){
        observeMovieChanges()
    }

    private fun observeMovie(){
        movieListLiveData.observe(viewLifecycleOwner) { movies ->
            val adapter = MoviesAdapterPublic(movies)
            binding.recyclerViewpublic.adapter = adapter
            binding.recyclerViewpublic.layoutManager = LinearLayoutManager(requireContext())
        }
    }
    private fun observeMovieChanges(){
        movieCollection.addSnapshotListener{snapshot, error->
            if (error != null){
                Log.d("HomeFragment", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val movie = snapshot?.toObjects(Movies::class.java)
            if (movie != null){
                movieListLiveData.postValue(movie)
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}