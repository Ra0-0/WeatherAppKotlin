package com.example.weatherappkotlin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private var db: CitiesDatabase? = null // База данных
    private var cityWeatherDao: CityWeatherTableDao? = null // Обращение к Таблице WeatherTable
    private var citiesDao: CitiesTableDao? = null // Обращение к Таблице CitiesTable
    private var progressBar: ProgressBar? = null // Загрузка

    fun getCityWeatherByApi(cityName: String): CityWeather{

        var citiesLatLon = WeatherApiConncetor.apiConnector().directGeocoding(cityName,
            1, "360cd16dd45ed62190214332afb02a73").execute().body()

        var citiesLat = citiesLatLon?.first()?.lat.toString()
        var citiesLon = citiesLatLon?.first()?.lon.toString()

        var citiesWeather = WeatherApiConncetor.apiConnector().cityWeather(citiesLat, citiesLon,
            "metric", "360cd16dd45ed62190214332afb02a73").execute().body()
        return citiesWeather!!
    } // Получение данных о температуре и скорости ветра по имени города

    fun getAllCitiesByBd(): List<CityTable>?{
        var citiesList = citiesDao?.getAll()
        return citiesList
    } // Получение всех городов из БД

    fun insertCityToBd(cityName: String){
        var city = CityTable(name = cityName)
        var city_id = citiesDao?.insert(city)
        var weather = getCityWeatherByApi(cityName)
        var cityWeather = CityWeatherTable(city_id = city_id?.toInt(), temp = weather.main.temp?.toDouble()?.toInt(),
            wind = weather.wind.speed?.toDouble()?.toInt())
        var cityWeather_id = cityWeatherDao!!.insert(cityWeather)
        createOneWeatherFragment(cityWeather_id.toInt())
    } // Ввод нового города в Бд (одноверменное и в CityTble и WeatherTable)

    fun upateCityWeatherInDb(citiesList :List<CityTable>) {
        for(city in citiesList!!){
            var weather = getCityWeatherByApi(city.name!!)
            cityWeatherDao!!.update(city.id!!, weather.wind.speed!!.toDouble()!!.toInt(),
                weather.main.temp!!.toDouble()!!.toInt())
        }
    } // Обновление погоды в таблице WeatherTable

    fun createAllWeatherFragments() {
        upateCityWeatherInDb(getAllCitiesByBd()!!)
        var cityWeather = cityWeatherDao?.getAll()
        for(weather in cityWeather!!){
            var cityName = citiesDao!!.getCityNameById(weather.city_id!!)
            createNewFragment(cityName, weather)
        }
    } // Создание всех городов из WeatherTable

    fun createOneWeatherFragment(id: Int) {
        var weather = cityWeatherDao!!.getCityWeatherById(id)
        var cityName = citiesDao!!.getCityNameById(weather.city_id!!)
        createNewFragment(cityName, weather)
    } // Создание 1 города из WeatherTable

    fun createNewFragment(cityName: String, weather: CityWeatherTable) {
        val container = R.id.weatherFragmentsBox
        var weatherCities = weatherCities.newInstance(cityName, weather.wind.toString(),
            weather.temp.toString())
        supportFragmentManager.beginTransaction().add(container, weatherCities).commit()
    } // Создание фрагмента в MainActivity для помещения в него данных о погоде

    fun setDate(){
        val todaysDate = Date()
        val day = SimpleDateFormat("dd.MM.yyyy").format(todaysDate).toString()
        val dayOfWeek = SimpleDateFormat("EEEE").format(todaysDate).toString()
        findViewById<TextView>(R.id.date).text = dayOfWeek + " " + day
    } // Установка дня недели и даты

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        progressBar!!.setVisibility(ProgressBar.VISIBLE) // Видимость загрузки
        setDate() // Дата

        GlobalScope.launch {
            db = CitiesDatabase.getDatabase(context = applicationContext, GlobalScope)
            cityWeatherDao = db?.cityWeatherDao()
            citiesDao = db?.citiesDao()

            if (savedInstanceState == null) {
                createAllWeatherFragments()
            }
            progressBar!!.setVisibility(ProgressBar.INVISIBLE)
        } // При первом включении создание всех экзмепляров из WeatherTable

    }

    fun search(view: View) {
        progressBar!!.setVisibility(ProgressBar.VISIBLE)
        GlobalScope.launch {
            var cityName = findViewById(R.id.cityName) as EditText
            insertCityToBd(cityName.text.toString())
            progressBar!!.setVisibility(ProgressBar.INVISIBLE)
        } // Добавление и вывод нового города
    }
}