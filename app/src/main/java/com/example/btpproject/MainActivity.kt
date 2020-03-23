package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
    }
}
