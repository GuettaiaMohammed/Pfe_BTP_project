package com.example.btpproject

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
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

class AjouterOrdreDeTravailActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    private val listLot = arrayListOf<String>()

    val db = "BTP_pfe"
    val username = "admin"
    val password = "pfe_chantier"
    val url = "http://sogesi.hopto.org:7013"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ajouter_ordre_de_travail)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Ordre de travail")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        tabLayout = findViewById(R.id.ajoutOTTabLayout)
        viewPager = findViewById(R.id.ajoutOTViewPager)
        listLot.add("")

        //Spinner
        val spinnerE = findViewById <Spinner>(R.id.listeLotSpinner)

        //liste des Employé spinner
        val connLot = ListeLotSpinner().execute(url)
        val listLott = connLot.get()
        val jsonArray3 = JSONArray(listLott)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listLott!!.size) - 1) {

            val name = jsonArray3.getJSONObject(i).getString("name").toString()
            listLot.add(name)

        }

        //Remplire Spinner
        val  adapter2 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listLot)

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerE.setAdapter(adapter2)

        val adapter: ViewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        //Ajouter les fragments
        adapter.addFragment(FragmentListeLigneLotAjouteOt(), "Lignes")
        adapter.addFragment(FragmentListeArticlesAjoutOt(), "Articles")

        //Adapter setup
        viewPager.setAdapter(adapter)
        tabLayout.setupWithViewPager(viewPager)
    }

    class ListeLotSpinner : AsyncTask<String, Void, List<Any>?>() {
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
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "project.lot", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id", "=", 2)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("name")
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

}
