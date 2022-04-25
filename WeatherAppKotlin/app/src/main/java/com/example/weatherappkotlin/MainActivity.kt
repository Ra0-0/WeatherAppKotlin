package com.example.weatherappkotlin

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.AccessController.getContext
import java.util.concurrent.Flow


class MainActivity : AppCompatActivity() {

    private var db: CitiesDatabase? = null
    private var cityWeatherDao: CityWeatherTableDao? = null
    private var citiesDao: CitiesTableDao? = null

    fun getCityWeatherByApi(cityName: String): CityWeather{

        var citiesLatLon = WeatherApiConncetor.apiConnector().directGeocoding(cityName,
            1, "360cd16dd45ed62190214332afb02a73").execute().body()

        var citiesLat = citiesLatLon?.first()?.lat.toString()
        var citiesLon = citiesLatLon?.first()?.lon.toString()

        var citiesWeather = WeatherApiConncetor.apiConnector().cityWeather(citiesLat, citiesLon,
            "metric", "360cd16dd45ed62190214332afb02a73").execute().body()
        return citiesWeather!!
    }

    fun getAllCitiesByBd(): List<CityTable>?{
        var citiesList = citiesDao?.getAll()
        return citiesList
    }

    fun insertCityToBd(cityName: String){
        var city = CityTable(name = cityName)
        var city_id = citiesDao?.insert(city)
        var weather = getCityWeatherByApi(cityName)
        var cityWeather = CityWeatherTable(city_id = city_id?.toInt(), temp = weather.main.temp?.toDouble()?.toInt(),
            wind = weather.wind.speed?.toDouble()?.toInt())
        cityWeatherDao!!.insert(cityWeather)
    }

    fun insertCityWeatherToBd(cityWeather: CityWeatherTable){
        with(cityWeatherDao){
            this?.insert(cityWeather)
        }
    }

    fun upateCityWeatherInDb(citiesList :List<CityTable>) {
        for(city in citiesList!!){
            var weather = getCityWeatherByApi(city.name!!)
            cityWeatherDao!!.update(city.id!!, weather.wind.speed!!.toDouble()!!.toInt(),
                weather.main.temp!!.toDouble()!!.toInt())
        }
    }

    fun createAllWeatherFragments() {
        val container = R.id.weatherFragmentsBox
        upateCityWeatherInDb(getAllCitiesByBd()!!)
        var cityWeather = cityWeatherDao?.getAll()
        for(weather in cityWeather!!){
            var cityName = citiesDao!!.getCityNameById(weather.city_id!!)
            var weatherCities = weatherCities.newInstance(cityName, weather.wind.toString(), weather.temp.toString())
            supportFragmentManager.beginTransaction().add(container, weatherCities).commit()
        }
    }

    fun insertNewCity() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {
            db = CitiesDatabase.getDatabase(context = applicationContext)
            cityWeatherDao = db?.cityWeatherDao()
            citiesDao = db?.citiesDao()

            createAllWeatherFragments()

//            Log.i("ApiCon", getAllCitiesByBd(db).toString())


//
//            var city1 = CityTable(name = "s")
//            var cityWeather1 = CityWeatherTable(city_id = 21, wind = 1, temp = 1)
//

//            citiesWeatherDao = db?.cityWeatherDao()
//            with(citiesWeatherDao){
//                this?.insert(city)
//            }
//
//            var list1 = db?.citiesDao()?.getAllCities()

        }



    }

    fun search(view: View) {
        var cityName = findViewById(R.id.cityName) as EditText
        insertCityToBd(cityName.text.toString())
    }


}