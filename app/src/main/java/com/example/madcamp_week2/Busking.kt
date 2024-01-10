package com.example.madcamp_week2

data class Busking(
    val id: Int,
    val title: String,
    val team: String,
    val image_url: String,
    val date: String,
    val location: String,
    val setlist: String,
    val start_time: String,
    val end_time: String,
    var isFavorite: String?
)