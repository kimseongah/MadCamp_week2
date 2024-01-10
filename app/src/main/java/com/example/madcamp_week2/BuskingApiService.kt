package com.example.madcamp_week2

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface BuskingApiService {
    @POST("/isitfavorite")
    fun isItFavorite(
        @Body requestData: FavoriteRequest
    ): Call<FavoriteResponse>
    @POST("/add_to_favorites")
    fun addToFavorites(@Body buskingData: Busking): Call<ResponseBody>

    @POST("/remove_from_favorites")
    fun removeFromFavorites(@Body buskingData: Busking): Call<ResponseBody>
}

data class FavoriteRequest(val id: String, val title: String, val team: String)
data class FavoriteResponse(val message: String)