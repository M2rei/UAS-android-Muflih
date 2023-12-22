    package com.example.uas_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import com.example.uas_android.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore

    class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val userCollectionRef = firestore.collection("user")
    private var updateId = ""
    private val userListLiveData: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        with(binding){
            viewPager.adapter = TabAdapter(this@MainActivity)
            TabLayoutMediator(tabLayout,viewPager){tab, position ->
                tab.text = when (position){
                    0 -> "Login"
                    1 -> "Register"
                    else -> ""
                }
            }.attach()
        }
    }

    private fun observeUsers(){
        userListLiveData.observe(this){user ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                user.toMutableList()
            )
        }
    }

}