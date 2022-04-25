package com.example.weatherappkotlin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "cityName"
private const val ARG_PARAM2 = "wind"
private const val ARG_PARAM3 = "temp"



/**
 * A simple [Fragment] subclass.
 * Use the [weatherCities.newInstance] factory method to
 * create an instance of this fragment.
 */
class weatherCities : Fragment() {
    // TODO: Rename and change types of parameters
    private var cityName: String? = null
    private var windSpeed: String? = null
    private var temp: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            cityName = it.getString(ARG_PARAM1)
            windSpeed = it.getString(ARG_PARAM2)
            temp = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weather_cities, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment weatherCities.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(cityName: String, windSpeed: String, temp: String) =
            weatherCities().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, cityName)
                    putString(ARG_PARAM2, windSpeed)
                    putString(ARG_PARAM3, temp)
                }
            }
    }
}