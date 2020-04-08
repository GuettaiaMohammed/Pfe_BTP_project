package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("BTP")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        chantier.setOnClickListener {
            val intent = Intent(this, MonChantier::class.java)
            // start your next activity
            startActivity(intent)
        }

        employes.setOnClickListener {
            val intent = Intent(this, ListeEmployeActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        article.setOnClickListener {
            val intent = Intent(this, ListeArticleActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        materiels.setOnClickListener {
            val intent = Intent(this, ListeMaterielsActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        avancesEmploye.setOnClickListener {
            val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        ordresDeTravail.setOnClickListener {
            val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
        suivi.setOnClickListener {
            val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
            // start your next activity
            startActivity(intent)
        }


        notification.setOnClickListener {
            val myBuilder = AlertDialog.Builder(this)
            //DATA SOURCE
            val notifications = arrayOf<CharSequence>("Notification 1", "Notification 2", "Notification 3", "Notification 4", "Notification 5", "Notification 6", "Notification 7")
            //SET PROPERTIES USING METHOD CHAINING
            myBuilder.setTitle("Notifications").setItems(notifications) { dialogInterface, position -> Toast.makeText(this, notifications[position].toString(), Toast.LENGTH_SHORT).show() }
            //CREATE DIALOG
            var myDialog = myBuilder.create()
            //SHOW DIALOG
            myDialog.show()
        }
        cart_badge.setText("2")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_accueil, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item!!.getItemId() == R.id.navigation_disconnect -> {
                val intent = Intent(this, LoginActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
          /*  item!!.getItemId() == R.id.navigation_notifications -> {
                showAlert()
                return true
            }*/
        }
        return super.onOptionsItemSelected(item)
    }

}



