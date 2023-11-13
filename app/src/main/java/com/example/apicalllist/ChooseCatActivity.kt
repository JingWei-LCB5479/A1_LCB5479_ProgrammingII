package com.example.apicalllist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.apicalllist.databinding.ActivityChooseCatBinding

class ChooseCatActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityChooseCatBinding
    private var selectedCategory: String = ""  // Variable to hold the selected category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = binding.sprCategory
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,  // Ensure this array is updated with basketball categories
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        binding.btnShowTeams.setOnClickListener {  // Renamed from btnShowJokes
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("category", selectedCategory)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedCategory = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Implement behavior for when nothing is selected, if necessary
    }
}
