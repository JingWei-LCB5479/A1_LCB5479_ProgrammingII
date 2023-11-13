package com.example.apicalllist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.apicalllist.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var teamsList = mutableListOf<Team>() // List to hold team data
    private lateinit var teamAdapter: TeamAdapter // Adapter for RecyclerView

    private val chooseTeamResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val category = result.data?.getStringExtra("category").toString()
            filterTeams(category) // Filter teams based on the selected category
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSpinner()
        retrieveTeamsWithVolley() // Retrieve all teams on start
    }

    private fun setupRecyclerView() {
        teamAdapter = TeamAdapter(teamsList)
        binding.recyclerView.apply {
            adapter = teamAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun setupSpinner() {
        // Assuming you have a Spinner with ID 'spinner' in your layout
        val spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent?.getItemAtPosition(position).toString()
                filterTeams(selectedCategory) // Filter teams based on the selected category
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun filterTeams(category: String) {
        val filteredList = if (category == "All") teamsList else teamsList.filter { it.conference == category || it.division == category }
        teamAdapter.updateTeams(filteredList)
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

    private fun retrieveTeamsWithVolley() {
        val queue = Volley.newRequestQueue(this)
        val url = "https://www.balldontlie.io/api/v1/teams"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val teamsArray = response.getJSONArray("data")
                    teamsList.clear()

                    for (i in 0 until teamsArray.length()) {
                        val teamJson = teamsArray.getJSONObject(i)
                        val team = Team(
                            id = teamJson.getInt("id"),
                            abbreviation = teamJson.getString("abbreviation"),
                            city = teamJson.getString("city"),
                            conference = teamJson.getString("conference"),
                            division = teamJson.getString("division"),
                            full_name = teamJson.getString("full_name"),
                            name = teamJson.getString("name")
                        )
                        teamsList.add(team)
                    }

                    teamAdapter.updateTeams(teamsList) // Update RecyclerView with the fetched teams

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Error retrieving data", Toast.LENGTH_SHORT).show()
                }
            },
            { _ ->
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonObjectRequest)
    }
}
