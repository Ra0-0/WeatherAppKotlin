package com.example.weatherappkotlin

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Entity(tableName = "citiesWeather_table", foreignKeys = [ForeignKey(entity = CityTable::class,
    parentColumns = ["id"], childColumns = ["city_id"], onDelete = ForeignKey.CASCADE
)])
class CityWeatherTable (
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,
    @ColumnInfo(name = "city_id")
    var city_id: Int? = null,
    @ColumnInfo(name = "temp")
    var temp: Int? = null,
    @ColumnInfo(name = "wind")
    var wind: Int? = null
)

@Entity(tableName = "cities_table")
class CityTable (
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,
    @ColumnInfo(name = "name")
    var name: String? = null
)

@Dao
interface CityWeatherTableDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(cityWeather: CityWeatherTable)

    @Query("UPDATE citiesWeather_table SET `wind` = :wind, `temp` = :temp WHERE `city_id` = :city_id")
    fun update(city_id: Int, wind: Int, temp: Int)

    @Query("SELECT * FROM citiesWeather_table INNER JOIN cities_table ON citiesWeather_table.city_id = cities_table.id WHERE citiesWeather_table.id = :id")
    fun getСityWeather(id: Int): CityWeatherTable

    @Query("SELECT * FROM citiesWeather_table")
    fun getAll(): List<CityWeatherTable>

    @Query("DELETE FROM citiesWeather_table")
    fun deleteAll()
}

@Dao
interface CitiesTableDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(сity: CityTable): Long

    @Query("SELECT * FROM cities_table")
    fun getAll(): List<CityTable>

    @Query("SELECT name FROM cities_table WHERE id = :id")
    fun getCityNameById(id: Int): String


    @Query("DELETE FROM cities_table")
    fun deleteAll()
}

@Database(entities = [CityTable::class, CityWeatherTable::class],
    version = 2, exportSchema = false)
abstract class CitiesDatabase : RoomDatabase() {
    abstract fun citiesDao(): CitiesTableDao
    abstract fun cityWeatherDao(): CityWeatherTableDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE `citiesWeather_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `city_id` INTEGER, `temp` INTEGER, `wind` INTEGER," +
                        "FOREIGN KEY(`city_id`) REFERENCES `cities_table`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
            }
        }

        @Volatile
        private var INSTANCE: CitiesDatabase? = null
        fun getDatabase(context: Context): CitiesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CitiesDatabase::class.java,
                    "citiesWeather_database"
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}