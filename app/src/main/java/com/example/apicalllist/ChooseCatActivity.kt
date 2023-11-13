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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseCatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = binding.sprCategory
        spinner.onItemSelectedListener = this
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            this,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            val catValue = parent.getItemAtPosition(position)
            binding.btnShowJokes.setOnClickListener(){
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("category", catValue.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}