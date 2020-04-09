package com.example.btpproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import kotlinx.android.synthetic.main.activity_liste_articles.*
import java.util.ArrayList

class ListeArticleActivity : AppCompatActivity() {
    private var mesArticles: ArrayList<Article>? = null
    private var listView: ListView? = null
    private var   articleAdapter: ArticleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_articles)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes article")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        listView = findViewById(R.id.articleListe)
        articleAdapter = ArticleAdapter(applicationContext, 0)
        mesArticles = ArrayList();

        mesArticles!!.add(Article("Béton",  "100 m3", "01/03/2020"))
        mesArticles!!.add(Article("Ciment",  "1000 unités", "01/03/2020"))
        mesArticles!!.add(Article("Brique",  "200 unités","11/03/2020"))
        mesArticles!!.add(Article("Gravier",  "300 unités","21/03/2020"))

        articleAdapter!!.addAll(mesArticles)
        listView!!.adapter = articleAdapter


        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (_i in 0..mesArticles!!.size) {
                if (position == _i) {
                  //val intent = Intent(this, DetailArticleQuantiteActivity::class.java)
                    // start your next activity
                   startActivity(Intent(this, DetailArticleQuantiteActivity::class.java))
                }
            }

        })

        //button click to show dialog
        fabArticle.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_article, null)
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
        inflater.inflate(R.menu.menu_article,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search_article)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Rechercher ici"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    articleAdapter!!.clear()
                    val search = newText.toLowerCase()
                    mesArticles!!.forEach {
                        if((it.nom!!.toLowerCase().contains(newText))
                            ||(it.qteDemande!!.toLowerCase().contains(newText))
                            ||(it.date!!.toLowerCase().contains(newText))){
                            articleAdapter!!.add(it)
                        }
                    }
                }else{
                    articleAdapter!!.clear()
                    articleAdapter!!.addAll(mesArticles)
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
            item!!.getItemId() == R.id.navigation_ordreTravail ->
            {
                val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
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
