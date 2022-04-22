package com.example.weatherappkotlin

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.room.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.security.AccessController.getContext
import java.util.concurrent.Flow


class MainActivity : AppCompatActivity() {

    @Database(entities = [City::class], version = 1, exportSchema = false)
    abstract class CitiesDatabase : RoomDatabase() {
        abstract fun citiesDao(): CitiesDao

        companion object {
            @Volatile
            private var INSTANCE: CitiesDatabase? = null
            fun getDatabase(context: Context): CitiesDatabase {
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        CitiesDatabase::class.java,
                        "citiesWeather_database"
                    ).build()
                    INSTANCE = instance
                    // return instance
                    instance
                }
            }
        }
    }

    @Entity(tableName = "cities_table")
    class City (
        @PrimaryKey(autoGenerate = true)
        var id : Int? = null,
        @ColumnInfo(name = "name")
        var name: String? = null
    )

    @Dao
    interface CitiesDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        fun insert(сity: City)

        @Query("SELECT * FROM cities_table")
        fun getAllCities(): List<City>
    }

    private var db: CitiesDatabase? = null
    private var context = this
    private var citiesDao: CitiesDao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var citiesLat : String? = null
//        var citiesLon : String? = null

        GlobalScope.launch {
//            val citiesLatLon = WeatherApiConncetor.apiConnector().directGeocoding("Набережные Челны",
//                1, "360cd16dd45ed62190214332afb02a73").execute().body()
//            citiesLat = citiesLatLon?.first()?.lat.toString()
//            citiesLon = citiesLatLon?.first()?.lon.toString()
//            val citiesWeather = WeatherApiConncetor.apiConnector().cityWeather(citiesLat, citiesLon,
//                "metric", "360cd16dd45ed62190214332afb02a73").execute().body()
//            Log.i("ApiCon", citiesWeather.toString())
            db = CitiesDatabase.getDatabase(context = context)
            citiesDao = db?.citiesDao()

            var city1 = City(name = "s")

            with(citiesDao){
                this?.insert(city1)
            }
            var list = db?.citiesDao()?.getAllCities()
            Log.i("ListApp", list.toString())
            Log.i("ListApp", list?.get(1)?.name.toString())
        }

    }




}