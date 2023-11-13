package com.example.apicalllist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.apicalllist.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var jokesList = mutableListOf<String>()
    private lateinit var arrayAdapter: ArrayAdapter<String>
    val chooseCatResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if (result.resultCode == Activity.RESULT_OK) {
                val category = result.data?.getStringExtra("category").toString()
        Log.d("category_intent", category)
        jokesList.clear()
        handleRetrieveQuoteWithVolley(category)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, jokesList)
        binding.lvJokes.adapter = arrayAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_category) {

            chooseCatResult.launch(Intent(this, ChooseCatActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }

    private fun handleRetrieveQuoteWithVolley(category: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "https://api.chucknorris.io/jokes/search?query=" + category
        Log.d("url", url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                        val rootJSONObject = JSONObject(response.toString());

                        val resultSet = JSONArray(rootJSONObject.getString("result"))

                        for (i in 0 until resultSet.length()) {

                            //Get result JSON object node
                            val oneSet = resultSet.getJSONObject(i)

                            //Get result details
                            val value = oneSet.getString("value")

                            jokesList.add(jokesList.size, value)
                            arrayAdapter.notifyDataSetChanged()
                            binding.lvJokes.setSelection(0)
                        }

                }catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Response error: " + e.printStackTrace(), Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this@MainActivity, "Fail to get response", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }
}