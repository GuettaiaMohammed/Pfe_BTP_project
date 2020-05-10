package com.example.btpproject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_avance_employe.view.*
import kotlinx.android.synthetic.main.activity_ajouter_employe.view.*
import kotlinx.android.synthetic.main.activity_liste_avance_employe.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class ListeAvanceEmployeActivity : AppCompatActivity() {

    private var listAvance: MutableList<AvanceEmploye>? = null
    private var avanceAdapter: AvanceEmployeAdapter? = null
    private var listView: ListView? = null

    //liste spinner
    private val listEmployes = arrayListOf<String>()
    val db = "BTP_pfe"
    val username = "admin"
    val password = "pfe_chantier"
    val url = "http://sogesi.hopto.org:7013"

    var mDatepicker: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_avance_employe)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes avance")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)




        listAvance = ArrayList<AvanceEmploye>()
        avanceAdapter = AvanceEmployeAdapter(applicationContext, 0)
        listView = findViewById(R.id.AvanceEmpListe)
        listEmployes.add("")

        //liste des demandes matériels
        val conn = ListeAvance().execute(url)
        val list = conn.get()

        //recupéré l'objet JSON
        val jsonArray = JSONArray(list)

        //récupéré lles données de l'objet JSON
        for (i in 0..(list!!.size) - 1) {
            val montant = jsonArray.getJSONObject(i).getString("mantant_demande").toString()
            val date = jsonArray.getJSONObject(i).getString("date").toString()
            var empObj =
                jsonArray.getJSONObject(i).getString("employee_id").toString()
            var emp = empObj.split("\"")[1]
            var emp2 = emp.split("\"")[0]

            println("**************************  employé = $emp2")
            println("**************************  montant = $montant")

            listAvance!!.add(AvanceEmploye(emp2, montant, date))
        }

        //liste des Employé spinner
        val connEmp = ListeEmploye().execute(url)
        val listEmp = connEmp.get()
        val jsonArray3 = JSONArray(listEmp)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listEmp!!.size) - 1) {

            val name = jsonArray3.getJSONObject(i).getString("name").toString()


            listEmployes.add(name)

        }


        listView!!.adapter = avanceAdapter
        avanceAdapter!!.addAll(listAvance)

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..listAvance!!.size) {
                if (position == i) {
                    Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show()
                }
            }

        })



        fabAvance.setOnClickListener {

            var date:String=""
            var idCh:Int=2
            var idE:Int=0
            var empl:String=""
            var montant:String=""
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_avance_employe, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                mBuilder.setView(mDialogView)
            //.setTitle("Login Form")



            //Spinner
            val spinnerE = mDialogView.findViewById <Spinner>(R.id.spinnerEmploye)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listEmployes)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.setAdapter(adapter)


            //button valider


            //date Début Picker Btn
            val dateBtn = mDialogView.findViewById<Button>(R.id.dateDAvBtn)
            // affichage de calendrier lors de la clique
            dateBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepicker = DatePickerDialog(
                    this,
                    R.style.DialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        //affichage de la date selectionné
                        dateBtn.setText(
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        )
                        date=dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                        dateBtn.textSize = 12F
                    }, year, month, day
                )

                mDatepicker!!.show()
            }
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,spinnerE.selectedItem.toString()+" "+" Le montant: "+mDialogView.montant.text ,Toast.LENGTH_SHORT).show()

                empl=spinnerE.selectedItem.toString()
                montant=mDialogView.montant.text.toString()

                for (i in 0..(listEmp!!.size) - 1) {

                    val name = jsonArray3.getJSONObject(i).getString("name").toString()

if(name==empl)
{
    idE=jsonArray3.getJSONObject(i).getString("id").toInt()

}


                }
                if(montant != "" && date != "" && empl != "") {
                    val demandeAvance = AjouterDemandeAvance().execute(
                        idCh.toString(),
                        date,
                        idE.toString(),
                        montant
                    )
                    mBuilder.dismiss()
                    val i:Intent=intent
                    finish()
                    overridePendingTransition(0,0)
                    startActivity(i)
                    overridePendingTransition(0,0)
                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }else
                {
                    Toast.makeText(mBuilder.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()

                }


            }

            //show dialog
            mBuilder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_avance,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search_avance)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Rechercher ici"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    avanceAdapter!!.clear()
                    val search = newText.toLowerCase()
                    listAvance!!.forEach {
                        if((it.employe!!.toLowerCase().contains(newText))
                            ||(it.dateAvance!!.toLowerCase().contains(newText))
                            ||(it.prix!!.toLowerCase().contains(newText))){
                            avanceAdapter!!.add(it)
                        }
                    }
                }else{
                    avanceAdapter!!.clear()
                    avanceAdapter!!.addAll(listAvance)
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

    class ListeAvance : AsyncTask<String, Void, List<Any>?>() {
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



                //liste des demandes avance
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "demande.avance", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id", "=", 2)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("employee_id", "mantant_demande", "date")
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

    class ListeEmploye : AsyncTask<String, Void, List<Any>?>() {
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
                            Arrays.asList("id", ">", 1)

                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name","id"))
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



    class AjouterDemandeAvance : AsyncTask<String, Void,List<Any>?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        var idE:Int=0
        var dateD:String=""
        var idCh:Int=0
        var montant:String=""

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): List<Any>? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
                    common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(
                                    String.format(
                                        "%s/xmlrpc/2/object",
                                        "http://sogesi.hopto.org:7013"
                                    )
                                )
                            }
                        })
                    }
                }



                println("************************  datebbb = ${infos[1]}")



               idCh = infos[0].toInt()
                dateD = infos[1]
               idE=infos[2].toInt()
                montant=infos[3]








                var id1: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "demande.avance", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("chantier_id", idCh)
                                put("employee_id", idE)
                                put("date", dateD)
                                put("mantant_demande",montant)
                                put("mantant_valide","0")


                            }
                        })
                    )
                )as Int
                println("************************  liste des données = $id1")
            } catch (e: MalformedURLException) {
                Log.d(
                    "MalformedURLException",
                    "*********************************************************"
                )
                Log.d("MalformedURLException", e.toString())
            } catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }

}
