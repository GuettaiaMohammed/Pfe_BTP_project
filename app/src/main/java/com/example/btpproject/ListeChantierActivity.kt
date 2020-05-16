package com.example.btpproject

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class ListeChantierActivity : AppCompatActivity(), ChantierAdapter.OnNoteListener {


    private var listeCh: ArrayList<Chantier>? = null
    private var recyclerView: RecyclerView? = null
    private var chantierAdapter: ChantierAdapter? = null
    lateinit var intt: Intent
    var idUser:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_chantier)

        recyclerView = findViewById(R.id.listeChantier)
        listeCh = ArrayList()
        intt = intent
        idUser = intt.getIntExtra("idUser",0)

        val conn = Chantiers().execute(idUser)
        val list = conn.get() as List<Any>

        if(list.isNotEmpty()){
            for(i in 0..(list.size)-1) {
                val jsonArray = JSONArray(list)
                val id = jsonArray.getJSONObject(i).getString("id")
                    .toString()

                val nom = jsonArray.getJSONObject(i).getString("name")
                    .toString()

                val taux = jsonArray.getJSONObject(i).getString("taux")
                    .toString()
                val tauxx = taux.split(".")[0]
                val empObj = jsonArray.getJSONObject(i).getString("property_stock_chantier")
                    .toString()
                val emp1 = empObj.split("\"")[1]
                val emp2 = emp1.split("\"")[0]

                println("**************************** id emplacement = ${list.toString()}")
                listeCh!!.add(Chantier(id, nom, tauxx, emp2))

            }
            chantierAdapter = ChantierAdapter(this.applicationContext, listeCh!!, this)
        }


        recyclerView!!.adapter = chantierAdapter
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))


    }

    override fun onNoteClick(position: Int) {
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        val id_chantier = listeCh!!.get(position).id
        intent.putExtra("idChantier", id_chantier!!.toInt())
        startActivity(intent)
    }

    class Chantiers : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg idUser: Int?): List<Any>? {
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
                    "project.chantier", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("user_id", "=", idUser),
                            Arrays.asList("state", "=", "en_cour")
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("id", "name", "taux", "property_stock_chantier")
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

}
