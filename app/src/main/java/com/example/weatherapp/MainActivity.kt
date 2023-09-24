package com.example.weatherapp

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
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
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCancelable(false)

        fetchWeatherData("India")
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
         progressDialog.show()
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
                 progressDialog.dismiss()
                 val responseBody = response.body()
                 if(response.isSuccessful && responseBody != null){

                     val temperature = responseBody.main.temp.toString()
                     val humidity = responseBody.main.humidity
                     val windSpeed = responseBody.wind.speed
                     val sunRise = responseBody.sys.sunrise.toLong()
                     val sunSet = responseBody.sys.sunset.toLong()
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
                     binding.textSunrise.text = "${time(sunRise)}"
                     binding.textSunset.text = "${time(sunSet)}"
                     binding.textSea.text = "$seaLevel hPa"
                     binding.textCondition.text = condition
                     binding.city.text = "$CityName"
                     binding.day.text =  dayName()
                     binding.date.text = date()

                     changeBackgroundImagesAcctoCondition(condition)
                 }
             }

             override fun onFailure(call: Call<WeatherAppData>, t: Throwable) {
                 Log.d("Main Activity", "onFailure: " + t.message)
             }
         })
    }

    private fun changeBackgroundImagesAcctoCondition(conditions: String) {
        when(conditions){
            "clear Sky", "Sunny", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny3)
                binding.lottieAnimation.setAnimation(R.raw.sun)
            }

            "Partly Clouds", "Clouds", "OverCast", "Mist", "Foggy", "Cloud" ->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimation.setAnimation(R.raw.cloudy_weather)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimation.setAnimation(R.raw.rainy_weather)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimation.setAnimation(R.raw.snow_weather)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny3)
                binding.lottieAnimation.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimation.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return  sdf.format((Date()))
    }

    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return  sdf.format((Date(timestamp*1000)))
    }

    fun dayName():String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format((Date()))
    }
}