package com.example.btpproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_ajouter_article.*

class AjouterArticleActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        var languages = arrayOf("English", "French", "Spanish", "Hindi", "Russian", "Telugu", "Chinese", "German", "Portuguese", "Arabic", "Dutch", "Urdu", "Italian", "Tamil", "Persian", "Turkish", "Other")

        var spinner: Spinner? = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_article)



        //var textView_msg:TextView? = null



           // textView_msg = this.msg

            spinner = this.spinnerA
            spinner!!.setOnItemSelectedListener(this)

            // Create an ArrayAdapter using a simple spinner layout and languages array
            val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
            // Set layout to use when the list of choices appear
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Set Adapter to Spinner
            spinner!!.setAdapter(aa)

        }

        override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
           // textView_msg!!.text = "Selected : "+languages[position]
        }

        override fun onNothingSelected(arg0: AdapterView<*>) {

        }

    }
