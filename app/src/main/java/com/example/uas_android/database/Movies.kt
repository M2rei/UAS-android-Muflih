package com.example.uas_android.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class Movies(

    @PrimaryKey var id: String = "",
    @ColumnInfo(name = "title")
    val title: String? = null,
    @ColumnInfo(name = "description")
    val description: String = "",
    @ColumnInfo(name = "image")
    val image: String = ""
)
