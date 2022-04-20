package com.example.weatherappkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    data class Weather(
        var lat: String? = null,
        var lon: String? = null
    )

    interface WeatherApiService {

        @GET("geo/1.0/direct")
        fun directGeocoding(
            @Query("q") query: String?,
            @Query("limit") limit: Int?,
            @Query("appid") appId: String): Call<MutableList<Weather>>

        @GET("data/2.5/weather")
        fun cityWeather(
            @Query("lat") lat: String?,
            @Query("lon") lon: String?,
            @Query("appid") appId: String): Call<MutableList<Weather>>

        companion object ApiAdapter {
            val baseUrl = "http://api.openweathermap.org/";
            fun apiConnector(): WeatherApiService {
                val retrofit = Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(baseUrl)
                    .build()
                return retrofit.create(WeatherApiService::class.java);
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var citiesLatLonLat : String? = null;
        var citiesLatLonLon : String? = null;
        
        WeatherApiService.apiConnector().directGeocoding("Лондон",
            1, "360cd16dd45ed62190214332afb02a73").enqueue(object : Callback<MutableList<Weather>> {
            override fun onFailure(call: Call<MutableList<Weather>>, t: Throwable) {
                Log.i("ApiConectorCall", t.toString())
            }

            override fun onResponse(call: Call<MutableList<Weather>>, response: Response<MutableList<Weather>>) {
                val citiesLatLon = response.body();
                citiesLatLonLat = citiesLatLon?.first()?.lat!!.toString()
                citiesLatLonLon = citiesLatLon?.first()?.lat!!.toString()
                Log.i("ApiConectorCall", citiesLatLonLat.toString())
                Log.i("ApiConectorCall", citiesLatLonLon.toString())
            }
        })
        WeatherApiService.apiConnector().cityWeather(citiesLatLonLat, citiesLatLonLon,
            "360cd16dd45ed62190214332afb02a73").enqueue(object : Callback<MutableList<Weather>> {
            override fun onFailure(call: Call<MutableList<Weather>>, t: Throwable) {

            }
            override fun onResponse(call: Call<MutableList<Weather>>,
                                    response: Response<MutableList<Weather>>) {
                Log.i("ApiConectorCall", response.body().toString())
            }})

    }




}