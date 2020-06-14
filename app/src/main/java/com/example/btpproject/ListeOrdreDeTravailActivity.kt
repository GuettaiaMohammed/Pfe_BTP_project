package com.example.btpproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_liste_ordres_de_travail.*

import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class ListeOrdreDeTravailActivity : AppCompatActivity() {

    private var listOT: MutableList<OrdreDeTravail>? = null
    private var listView: ListView? = null
    private var ordreTravailAdapter: OrdreDeTravailAdapter? = null

    //liste spinner
    private val listlot = arrayListOf<String>()

    lateinit var i: Intent
    var id_chantier:Int = 0

    lateinit var mPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_ordres_de_travail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Ordres de travail")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        i = intent
        id_chantier = i.getIntExtra("idChantier",0)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        listView = findViewById(R.id.ordreDeTravailListe)
        listOT = ArrayList()

        //liste des demandes matériels
        val conn = ListeOrdre().execute(id_chantier.toString(), url, db, username, password)
        val list = conn.get()
        val jsonArray = JSONArray(list)

        //recupéré l'objet JSON
        if(list != null) {


            //récupéré lles données de l'objet JSON
            for (i in 0..(list!!.size) - 1) {
                val nom = jsonArray.getJSONObject(i).getString("name").toString()
                val date = jsonArray.getJSONObject(i).getString("date_debut").toString()
                var lotObj =
                    jsonArray.getJSONObject(i).getString("lot_id").toString()
                var lot = lotObj.split("\"")[1]
                var lot2 = lot.split("\"")[0]

                println("**************************  lot = $lot2")
                println("**************************  nom = $nom")

                listOT!!.add(OrdreDeTravail(nom, lot2, date))
            }



            ordreTravailAdapter = OrdreDeTravailAdapter(applicationContext, 0)
            listView!!.adapter = ordreTravailAdapter
            ordreTravailAdapter!!.addAll(listOT)

            var i: Int = 0
            listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
                for (i in 0..listOT!!.size) {
                    if (position == i) {
                        val intent = Intent(this, DetailOrdreDeTravailActivity::class.java)

                        val id = jsonArray.getJSONObject(i).getString("id").toString()
                        intent.putExtra("id", id.toInt())
                        intent.putExtra("idChantier", id_chantier)
                        // start your next activity
                        startActivity(intent)
                    }
                }

            })
        }
        //button click to show dialog
        fabOrdreDeTravail.setOnClickListener {
            val intent = Intent(this, AjouterOrdreDeTravailActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
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
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_monCh->
            {
                val intent = Intent(this, MonChantier::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_materiel ->
            {
                val intent = Intent(this, ListeMaterielsActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_employe ->
            {
                val intent = Intent(this, ListeEmployeActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_article ->
            {
                val intent = Intent(this, ListeArticleActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_suiviJ ->
            {
                val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_avance ->
            {
                val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
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

    class ListeOrdre : AsyncTask<String, Void, List<Any>?>() {

        override fun doInBackground(vararg v: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", v[1]))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        v[2], v[3], v[4], Collections.emptyMap<Any, Any>()
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
                                serverURL = URL(String.format("%s/xmlrpc/2/object", v[1]))
                            }
                        })
                    }
                }



                //liste des demandes matériels
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[2], uid, v[4],
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id", "=", v[0]!!.toInt())
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("name", "date_debut", "lot_id")
                            )
                        }
                    }
                )) as Array<Any>)
                println("**************************  champs chantier = $list")


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

    class ListeLot : AsyncTask<String, Void, List<Any>?>() {
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
                )
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
                var liste: List<*> = java.util.ArrayList<Any>()

                liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "hr.employee", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "!=", 0)

                        )
                    ),
                    object : java.util.HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name"))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)

                println("************************  liste des champs = $liste")



                return liste

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