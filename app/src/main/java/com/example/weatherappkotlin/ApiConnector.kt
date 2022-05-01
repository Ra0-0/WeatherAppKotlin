package com.example.weatherappkotlin

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Файл для получение данных с Api

data class WeatherLatLon(
    var lat: String? = null,
    var lon: String? = null
) // Структура ответа для координат города

data class Main(
    var temp: String? = null
)
data class Wind(
    var speed: String? = null
)
data class CityWeather(
    var main : Main,
    var wind : Wind
) // Структура ответа для погоды

interface WeatherApiConncetor {

    @GET("geo/1.0/direct")
    fun directGeocoding(
        @Query("q") query: String?,
        @Query("limit") limit: Int?,
        @Query("appid") appId: String): Call<MutableList<WeatherLatLon>>

    @GET("data/2.5/weather")
    fun cityWeather(
        @Query("lat") lat: String?,
        @Query("lon") lon: String?,
        @Query("units") units: String,
        @Query("appid") appId: String): Call<CityWeather>

    companion object ApiAdapter {
        val baseUrl = "http://api.openweathermap.org/";
        fun apiConnector(): WeatherApiConncetor {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .build()
            return retrofit.create(WeatherApiConncetor::class.java);
        }
    }
}