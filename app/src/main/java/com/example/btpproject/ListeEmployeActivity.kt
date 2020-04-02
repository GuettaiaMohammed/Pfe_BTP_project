package com.example.btpproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar

import kotlinx.android.synthetic.main.activity_liste_employe.*

import java.util.ArrayList


class ListeEmployeActivity : AppCompatActivity() {

    private var mesEmployes: ArrayList<Employe>? = null
    private var listView: ListView? = null
    private var employeAdapter: EmployeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_employe)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Employés demandé")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        listView = findViewById(R.id.empl)
        employeAdapter = EmployeAdapter(applicationContext, 0)

        mesEmployes= ArrayList();

        (mesEmployes as ArrayList<Employe>).add(Employe("Employé1", "Architecte"))
        (mesEmployes as ArrayList<Employe>).add(Employe("Employé2", "Maçon"))
        (mesEmployes as ArrayList<Employe>).add(Employe("Employé3", "Maçon"))

        employeAdapter!!.addAll(mesEmployes)
        listView!!.adapter = employeAdapter



        //button click to show dialog
        fabEmploye.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            mBuilder.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when{
            item!!.getItemId() == R.id.navigation_home ->
            {
                val intent = Intent(this, MonChantier::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_dashboard ->
            {
                val intent = Intent(this, MainActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_materiel ->
            {
                val intent = Intent(this, ListeMaterielsActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_employe ->
            {
                val intent = Intent(this, ListeEmployeActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_article ->
            {
                val intent = Intent(this, ListeArticleActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_suiviJ ->
            {
                val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
