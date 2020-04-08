package com.example.btpproject

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import java.text.DateFormat
import java.util.*

class AjouterEmployeActivity : AppCompatActivity() {
    var dateDBtn: Button? = null
    var dateFBtn: Button? = null
    var mDateSetListener: DatePickerDialog.OnDateSetListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_employe)
    }
}
