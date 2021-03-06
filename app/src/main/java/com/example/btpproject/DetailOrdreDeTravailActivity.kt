package com.example.btpproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.HashMap

class DetailOrdreDeTravailActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager

    lateinit var intt: Intent
    var id_chantier:Int = 0
    var id:Int = 0

    lateinit var mPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_ordre_de_travail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Ordre de travail")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        intt = intent
        id = intt.getIntExtra("id",0)
        id_chantier = intt.getIntExtra("idChantier",0)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        tabLayout = findViewById(R.id.detailOTTabLayout)
        viewPager = findViewById(R.id.detailOTViewPager)

        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        val nom = findViewById<TextView>(R.id.nomOTDeTOTTV)
        val refOT = findViewById<TextView>(R.id.refOTDetOTTV)
        val refLot = findViewById<TextView>(R.id.refLotDetOTTV)
        val date = findViewById<TextView>(R.id.DateOTDetOTTV)


        val conn = DetailOrdre().execute(id.toString(), url, db, username, password)
        val ordre = conn.get()

        val jsonArray = JSONArray(ordre)

        //récupéré lles données de l'objet JSON
        for (i in 0..(ordre!!.size) - 1) {
            val name = jsonArray.getJSONObject(i).getString("name").toString()
            val ref = jsonArray.getJSONObject(i).getString("reference").toString()
            val datte = jsonArray.getJSONObject(i).getString("date_debut").toString()
            var lotObj =
                jsonArray.getJSONObject(i).getString("lot_id").toString()
            var idLot = lotObj.split(",")[0]
            var id2 = idLot.split("[")[1]

            val conn2 = RefLot().execute(id2, url, db, username, password)
            val refLott = conn2.get()

            nom.text = name
            refOT.text = ref
            refLot.text = refLott
            date.text = datte


            //Ajouter les fragments
            adapter.addFragment(FragmentListeLigneLotDetailOt(id), "Lignes")
            adapter.addFragment(FragmentListeArticlesDetailOt(id), "Articles")
            adapter.addFragment(FragmentListeLigneSuppDetailOt(id), "Lignes Supp")

        }

        //Adapter setup
        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_ordre, menu)
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
            item!!.getItemId() == R.id.navigation_ordreTravail ->
            {
                val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
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

    class DetailOrdre : AsyncTask<String, Void, List<Any>?>() {

        @RequiresApi(Build.VERSION_CODES.KITKAT)
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


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", v[1]))
                            }
                        })
                    }
                }

                /*
                //lecture des champs
                val record = (models.execute(
                    "execute_kw", asList(
                        db, uid, password,
                        "ordre.travail", "read",
                        asList(id)
                    )
                ) as Array<Any>)[0] as Map<*, *>

                println("**************************  nom = ${record.get("name")}")
                println("**************************  lot id =" +
                        " ${FonctionsXmlRPC.getMany2One(record as Map<String, Objects>, "lot_id").id}")
                println("**************************  lot name =" +
                        " ${FonctionsXmlRPC.getMany2One(record as Map<String, Objects>, "lot_id").name}")4

                 */




                //liste des demandes matériels
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[2], uid, v[4],
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "=", v[0]!!.toInt())
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("name", "date_debut", "lot_id", "reference")
                            )
                        }
                    }
                )) as Array<Any>)



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

    class RefLot : AsyncTask<String, Void, String?>() {

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun doInBackground(vararg v: String?): String? {
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


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", v[1]))
                            }
                        })
                    }
                }


                //lecture des champs
                val record = (models.execute(
                    "execute_kw", Arrays.asList(
                        v[2], uid, v[4],
                        "project.lot", "read",
                        Arrays.asList(v[0]!!.toInt())
                    )
                ) as Array<Any>)[0] as Map<*, *>

                val ref = record.get("reference").toString()

                return ref

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
