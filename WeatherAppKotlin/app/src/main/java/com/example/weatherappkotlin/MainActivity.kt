package com.example.weatherappkotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


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
        var cityWeather_id = cityWeatherDao!!.insert(cityWeather)
        createOneWeatherFragment(cityWeather_id.toInt())
    }

    fun upateCityWeatherInDb(citiesList :List<CityTable>) {
        for(city in citiesList!!){
            var weather = getCityWeatherByApi(city.name!!)
            cityWeatherDao!!.update(city.id!!, weather.wind.speed!!.toDouble()!!.toInt(),
                weather.main.temp!!.toDouble()!!.toInt())
        }
    }

    fun createAllWeatherFragments() {
        upateCityWeatherInDb(getAllCitiesByBd()!!)
        var cityWeather = cityWeatherDao?.getAll()
        for(weather in cityWeather!!){
            var cityName = citiesDao!!.getCityNameById(weather.city_id!!)
            createNewFragment(cityName, weather)
        }
    }

    fun createOneWeatherFragment(id: Int) {
        var weather = cityWeatherDao!!.getCityWeatherById(id)
        var cityName = citiesDao!!.getCityNameById(weather.city_id!!)
        createNewFragment(cityName, weather)
    }

    fun createNewFragment(cityName: String, weather: CityWeatherTable) {
        Log.i("get", cityName)
        val container = R.id.weatherFragmentsBox
        var weatherCities = weatherCities.newInstance(cityName, weather.wind.toString(),
            weather.temp.toString())
        supportFragmentManager.beginTransaction().add(container, weatherCities).commit()
    }

    fun setDate(){
        val todaysDate = Date()
        val day = SimpleDateFormat("dd.MM.yyyy").format(todaysDate).toString()
        val dayOfWeek = SimpleDateFormat("EEEE").format(todaysDate).toString()
        findViewById<TextView>(R.id.date).text = dayOfWeek + " " + day
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        GlobalScope.launch {
            db = CitiesDatabase.getDatabase(context = applicationContext, GlobalScope)
            cityWeatherDao = db?.cityWeatherDao()
            citiesDao = db?.citiesDao()

            setDate()

            if (savedInstanceState == null) {
                createAllWeatherFragments()
            }
        }
    }

    fun search(view: View) {
        GlobalScope.launch {
            var cityName = findViewById(R.id.cityName) as EditText
            insertCityToBd(cityName.text.toString())
        }
    }


}