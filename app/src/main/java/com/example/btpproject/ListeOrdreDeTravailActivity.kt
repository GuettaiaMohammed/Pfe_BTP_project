package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_liste_ordre_de_travail.*
import java.util.ArrayList

class ListeOrdreDeTravailActivity : AppCompatActivity() {

    private var listOT: MutableList<OrdreDeTravail>? = null
    private var listView: ListView? = null
    private var ordreTravailAdapter: OrdreDeTravailAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_ordre_de_travail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Ordres de travail")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        listView = findViewById(R.id.ordreDeTravailListe)
        listOT = ArrayList()

        listOT!!.add(OrdreDeTravail("Ordre de travail 1", "TERRASSEMENT","24/02/2020"))
        listOT!!.add(OrdreDeTravail("Ordre de travail 2", "TRAVEAUX EN INFRASTRUCTURE","24/02/2020"))
        listOT!!.add(OrdreDeTravail("Ordre de travail 3", "TRAVEAUX EN INFRASTRUCTURE","25/02/2020"))


        ordreTravailAdapter = OrdreDeTravailAdapter(applicationContext, 0)
        listView!!.adapter = ordreTravailAdapter
        ordreTravailAdapter!!.addAll(listOT)

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..listOT!!.size) {
                if (position == i) {
                    val intent = Intent(this, DetailOrdreDeTravailActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                }
            }

        })

        //button click to show dialog
        fabOrdreDeTravail.setOnClickListener {
            val intent = Intent(this, AjouterOrdreDeTravailActivity::class.java)
            // start your next activity
            startActivity(intent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_ordre,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search_ordre)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Rechercher ici"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    ordreTravailAdapter!!.clear()
                    val search = newText.toLowerCase()
                    listOT!!.forEach {
                        if((it.nom!!.toLowerCase().contains(newText))
                            ||(it.lot!!.toLowerCase().contains(newText))
                            ||(it.date!!.toLowerCase().contains(newText))){
                            ordreTravailAdapter!!.add(it)
                        }
                    }
                }else{
                    ordreTravailAdapter!!.clear()
                    ordreTravailAdapter!!.addAll(listOT)
                }
                return true
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when{
            item!!.getItemId() == R.id.navigation_home ->
            {
                val intent = Intent(this, MainActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_monCh->
            {
                val intent = Intent(this, MonChantier::class.java)
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
            item!!.getItemId() == R.id.navigation_avance ->
            {
                val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }

            item!!.getItemId() == R.id.navigation_disco ->
            {
                val intent = Intent(this, LoginActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
