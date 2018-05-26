package com.hfad.weathermap

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.URL
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    companion object {
        val API_KEY = "&appid=b4b2db2a8e326a36948f1c1ed387d275"
        val BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q="
        lateinit var task : DownloadJSON
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun getWeather(view : View){
        try {
            task = DownloadJSON()
            val encodedCityName = URLEncoder.encode(et_city_name.text.toString(), "UTF-8")
            task.execute(BASE_URL + encodedCityName + API_KEY)
            val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(et_city_name.windowToken, 0)
        }catch (e : Exception){
            Toast.makeText(applicationContext, "Could not find weather", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class DownloadJSON : AsyncTask<String, Void, String>(){
        override fun doInBackground(vararg params: String?): String? {

            try {
                var result = ""
                val url = URL(params[0])
                val connection = url.openConnection()
                connection.connect()
                val inputStream = connection.getInputStream()
                val reader = InputStreamReader(inputStream)
                var data = reader.read()

                while (data != -1){
                    val current : Char = data.toChar()
                    result += current
                    data = reader.read()
                }

                return result

            } catch (e : Exception){
                e.printStackTrace()
                Toast.makeText(applicationContext, "Could not find weather", Toast.LENGTH_SHORT).show()
                return null
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            try {

                val jsonObject = JSONObject(result)
                val weatherInfo: String = jsonObject.getString("weather")
                val array = JSONArray(weatherInfo)
                var message_description = ""
                var message_main = ""
                var i = 0;
                while (i < array.length()) {
                    val jsonPart: JSONObject = array.getJSONObject(i)
                    val main = jsonPart.getString("main")
                    val description = jsonPart.getString("description")

                    if (main != "" && description != "") {
                        if (array.length() > 1 && i == 0) {
                            message_main += main + "\r\n"
                            message_description += description + "\r\n"
                        }else{
                            message_main += main
                            message_description += description
                        }
                    }
                    i++
                }
                if (message_main != "" && message_description != "") {
                    tv_main_weather.text = message_main
                    tv_description.text = message_description
                }

                val temp = jsonObject.getJSONObject("main")
                val temperature = temp.getString("temp")
                val temperature_min = temp.getString("temp_min")
                val temperature_max = temp.getString("temp_max")

                tv_temp.text = temperature
                tv_temp_min.text = temperature_min
                tv_temp_max.text = temperature_max

            }catch (e : Exception){
                Toast.makeText(applicationContext, "Could not find weather", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

    }
}
