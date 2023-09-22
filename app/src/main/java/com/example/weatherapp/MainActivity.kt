package com.example.weatherapp

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding :ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Nagpur")
        searchCity()

    }

    private fun searchCity(){
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query != null){
                 fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(CityName :String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

          val response = retrofit.getWeatherData(CityName, "b0429937d3b8cd1f196f94f26dbc62d1" , "metric")

         response.enqueue(object: Callback<WeatherAppData>{
             override fun onResponse(
                 call: Call<WeatherAppData>,
                 response: Response<WeatherAppData>
             ) {
                 val responseBody = response.body()
                 if(response.isSuccessful && responseBody != null){

                     val temperature = responseBody.main.temp.toString()
                     val humidity = responseBody.main.humidity
                     val windSpeed = responseBody.wind.speed
                     val sunRise = responseBody.sys.sunrise
                     val sunSet = responseBody.sys.sunset
                     val seaLevel = responseBody.main.pressure
                     val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                     val minTemp = responseBody.main.temp_min
                     val maxTemp = responseBody.main.temp_max

                     binding.temp.text = "$temperature ℃"
                     binding.weatherType.text = condition
                     binding.maxTemp.text = "Max Temp: $maxTemp ℃"
                     binding.minTemp.text = "MIn Temp: $minTemp ℃"
                     binding.textHumidity.text = "$humidity ℃"
                     binding.textWind.text = "$windSpeed m/s"
                     binding.textSunrise.text = "$sunRise"
                     binding.textSunset.text = "$sunSet"
                     binding.textSea.text = "$seaLevel hPa"
                     binding.textCondition.text = condition
                     binding.city.text = "$CityName"
                     binding.day.text =  dayName(System.currentTimeMillis())
                     binding.date.text = date()

                     changeBackgroundImagesAcctoCondition(condition)
                 }
             }

             override fun onFailure(call: Call<WeatherAppData>, t: Throwable) {
                 TODO("Not yet implemented")
             }
         })
    }

    private fun changeBackgroundImagesAcctoCondition(conditions: String) {
        when(conditions){
            "Haze" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimation.setAnimation(R.raw.rainy)
            }
        }
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return  sdf.format((Date()))
    }

    fun dayName(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
    }
}