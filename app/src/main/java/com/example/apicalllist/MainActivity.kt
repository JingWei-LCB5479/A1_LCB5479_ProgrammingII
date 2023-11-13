package com.example.apicalllist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.apicalllist.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var teamList = mutableListOf<String>()  // Renamed from jokesList to teamList
    private lateinit var arrayAdapter: ArrayAdapter<String>
    val chooseTeamResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> if (result.resultCode == Activity.RESULT_OK) {
        val category = result.data?.getStringExtra("category").toString()
        Log.d("category_intent", category)
        teamList.clear()
        retrieveTeamsWithVolley(category)  // Renamed method
    }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayAdapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, teamList)
        binding.lvTeams.adapter = arrayAdapter  // Renamed from lvJokes to lvTeams
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.menu_category) {

            chooseTeamResult.launch(Intent(this, ChooseCatActivity::class.java))

        }
        return super.onOptionsItemSelected(item)
    }

    private fun retrieveTeamsWithVolley(category: String) {  // Renamed method

        val queue = Volley.newRequestQueue(this)
        val url = "https://www.balldontlie.io/api/v1/teams"  // Updated URL for basketball teams
        Log.d("url", url)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val teamsArray = response.getJSONArray("data")  // Updated for basketball teams

                    for (i in 0 until teamsArray.length()) {

                        val team = teamsArray.getJSONObject(i)
                        val teamName = team.getString("full_name")  // Get team name

                        teamList.add(teamList.size, teamName)
                        arrayAdapter.notifyDataSetChanged()
                        binding.lvTeams.setSelection(0)  // Renamed from lvJokes to lvTeams
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            },
            {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }
}
