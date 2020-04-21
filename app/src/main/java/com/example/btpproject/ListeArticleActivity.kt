package com.example.btpproject

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
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
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class ListeArticleActivity : AppCompatActivity() {

    //
    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"

    //
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


        Connexion().execute(url)

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




    class Connexion : AsyncTask<String, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int=  client.execute(
                        common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                )
                ) as Int
                Log.d(
                        "result",
                        "*******************************************************************"
                );
                Log.d("uid = ", Integer.toString(uid))
                System.out.println("************************************    UID = " + uid)

                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", "http://sogesi.hopto.org:7013"))
                            }
                        })
                    }
                }

                //liste des chantier
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        db, uid, password,
                        "demande.appro.article", "search_read",
                        Arrays.asList(
                                Arrays.asList(
                                        Arrays.asList("chantier_id", "=", 2)
                                )
                        ),
                        object : HashMap<Any, Any>() {
                            init {
                                put(
                                        "fields",
                                        Arrays.asList("ligne_demande_appro_article_ids")
                                )
                            }
                        }
                )) as Array<Any>)
                println("**************************  champs chantier = $list")

                val jsonArray = JSONArray(list)

                for(i in 0..(list.size)-1) {
                    var typeObj = jsonArray.getJSONObject(i).getString("ligne_demande_appro_article_ids").toString()
                    // var typeObj1 = jsonArray.getJSONObject(i).getString("employee_ids").toString()
                  //  var type = typeObj.split("\"")[1]
                  //  var type2 = type.split("\"")[0]

                    println("**************************  type = $typeObj")
                    // println("**************************  Date debut = $typeObj1")
                }

                    return list

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }


    }
}
