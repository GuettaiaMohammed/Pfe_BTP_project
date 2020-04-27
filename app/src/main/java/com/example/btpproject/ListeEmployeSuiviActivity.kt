package com.example.btpproject


import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_employe_suivi.view.*

import kotlinx.android.synthetic.main.activity_liste_employes_suivi.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class ListeEmployeSuiviActivity : AppCompatActivity() {

    //
    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"





    private var mesEmployes: ArrayList<Employe>? = null
    private var listView: ListView? = null
    private var employeAdapter: EmployeSuiviAdapter? = null


    //les listes des spinner
    private val listEmployes = arrayListOf<String>()
    private val listUnites = arrayListOf<String>()
    private val listLots = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_employes_suivi)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Suivi Employés")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        //remplire les listes de spinner
        listEmployes.add("")
        listUnites.add("")

        listLots.add("")


        //
        val conn = Connexion().execute(url)
        val conn1=Employee().execute(url)
        val conn2= MonChantier.Unite().execute(url)
        val conn3=LignesLots().execute(url)

        //liste des employés
        val listE=conn1.get()
        val jsonArray1 = JSONArray(listE)
        for (i in 0..(listE!!.size) - 1) {

            val name = jsonArray1.getJSONObject(i).getString("name").toString()


            listEmployes.add(name)

        }
        //liste des unités
        val listU=conn2.get()
        val jsonArray6 = JSONArray(listU)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listU!!.size) - 1) {

            val name = jsonArray6.getJSONObject(i).getString("name").toString()

            listUnites.add(name)

        }


///liste lignes lots
        val listLigne=conn3.get()
        val jsonArray2 = JSONArray(listLigne)
        for (i in 0..(listLigne!!.size) - 1){
        var Obj1 =
            jsonArray2.getJSONObject(i).getString("name").toString()
      //  var lot = Obj1.split("\"")[1]
        //var lot2 = lot.split("\"")[0]

        listLots.add(Obj1)

        }




        listView = findViewById(R.id.empl)
        employeAdapter = EmployeSuiviAdapter(applicationContext, 0)

        mesEmployes= ArrayList();
        val list = conn.get()
        //recupéré l'objet JSON
        val jsonArray = JSONArray(list)

        //récupéré lles données de l'objet JSON
        for (i in 0..(list!!.size) - 1) {
            var Obj =
                    jsonArray.getJSONObject(i).getString("employee_id").toString()
            var nom = Obj.split("\"")[1]
            var nom2 = nom.split("\"")[0]

            var Obj1 =
                    jsonArray.getJSONObject(i).getString("ligne_lot_id").toString()
            var lot = Obj1.split("\"")[1]
            var lot2 = lot.split("\"")[0]


            mesEmployes!!.add(Employe(nom2, lot2))
        }



        employeAdapter!!.addAll(mesEmployes)
        listView!!.adapter = employeAdapter

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..mesEmployes!!.size) {
                if (position == i) {
                    val intent = Intent(this, DetailSuiviEmployeQteRealiseActivity::class.java)
                    val id = jsonArray.getJSONObject(i).getString("id").toString()

                    intent.putExtra("id",id.toInt())
                    // start your next activity
                    startActivity(intent)
                }
            }

        })

        //button click to show dialog
        fabEmpl.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe_suivi, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            mBuilder.show()


            //Spinner
            val spinnerE = mDialogView.findViewById <Spinner>(R.id.spinnerEmploye)
            val spinnerU = mDialogView.findViewById <Spinner>(R.id.spinnerUnite)

            val spinnerL = mDialogView.findViewById <Spinner>(R.id.spinnerLigneLot)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listEmployes)
            val  adapter1 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listUnites)
            val  adapter3 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listLots)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.setAdapter(adapter)
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerU.setAdapter(adapter1)


            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerL.setAdapter(adapter3)


            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,"ajout de : "+mDialogView.qteP.text.toString()+" , "+ mDialogView.nbH.text.toString()+" , "+mDialogView.pu.text.toString(), Toast.LENGTH_SHORT).show()


            }




        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_suivi,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search_suivi)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Rechercher ici"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    employeAdapter!!.clear()
                    val search = newText.toLowerCase()
                    mesEmployes!!.forEach {
                        if((it.nom!!.toLowerCase().contains(newText))
                            ||(it.metier!!.toLowerCase().contains(newText))){
                            employeAdapter!!.add(it)
                        }
                    }
                }else{
                    employeAdapter!!.clear()
                    employeAdapter!!.addAll(mesEmployes)
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
                        "reglement.employe", "search_read",
                        Arrays.asList(
                                Arrays.asList(
                                        Arrays.asList("chantier_id","=",2)
                                )
                        ),
                        object : HashMap<Any, Any>() {
                            init {
                                put(
                                        "fields",
                                        Arrays.asList("id","employee_id","ligne_lot_id","qte_prev","qte_realise","nb_h_prevu","nb_h_travail")
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

    class Employee : AsyncTask<String, Void, List<Any>?>() {
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
                            Arrays.asList("id", ">", 1)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name","job_title"))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)




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
    class LignesLots : AsyncTask<String, Void, List<Any>?>() {
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
                    "ligne.lot", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id","!=",0)
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
