package com.example  .btpproject



import android. app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_employe.view.*

import kotlinx.android.synthetic.main.activity_liste_employe.*
import java.util.*
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.util.Log
import android.view.*
import android.widget.Toast
import android.widget.DatePicker
import android.widget.EditText
import androidx.core.view.get
import com.example.btpproject.R.layout.activity_cellule_employe
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.Arrays.asList


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ListeEmployeActivity : AppCompatActivity() {

    //
    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"

    private var mesEmployes: ArrayList<Employe>? = null
    private var listView: ListView? = null
    private var employeAdapter: EmployeAdapter? = null


    private var  check: CheckBox? = null

    private val listMetiers = arrayListOf<String>()

    var mDatepickerD: DatePickerDialog? = null
    var mDatepickerF: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_employe)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes personnel")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

  Connexion().execute(url)

        listMetiers.addAll(listOf("", "Architecte", "Maçon"))


        listView = findViewById(R.id.empl)
        employeAdapter = EmployeAdapter(applicationContext, 0)

        mesEmployes = ArrayList()


        (mesEmployes as ArrayList<Employe>).add(Employe( "Employé1", "Architecte"))
        (mesEmployes as ArrayList<Employe>).add(Employe("Employé2", "Maçon"))
        (mesEmployes as ArrayList<Employe>).add(Employe( "Employé3", "Maçon"))

        employeAdapter!!.addAll(mesEmployes)
        listView!!.adapter = employeAdapter


        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..mesEmployes!!.size) {
                if (position == i) {
                    var text:TextView = view.findViewById(R.id.receptioner)
                    text.setText("Employé Receptionné")
                    text.setTextColor(Color.GRAY)
                    text.setBackgroundColor(Color.WHITE)


                  //  employeAdapter!!.remove(employeAdapter!!.getItem(i))
                }
            }

        })



        //button click to show dialog
        fabEmploye.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
            //.setTitle("Login Form")

            //Spinner
            val spinnerE = mDialogView.findViewById<Spinner>(R.id.spinnerE)

            //Remplire Spinner
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listMetiers)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.setAdapter(adapter)

            //button valider
            mDialogView.button.setOnClickListener {
                Toast.makeText(this, spinnerE.selectedItem.toString() + " " + " Le nombre : " + mDialogView.nbr.text,
                        Toast.LENGTH_SHORT).show()
            }

            //date Début Picker Btn
            val dateDBtn = mDialogView.findViewById<Button>(R.id.dateDEmpBtn)
            // affichage de calendrier lors de la clique
            dateDBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepickerD = DatePickerDialog(
                        this,
                        R.style.DialogTheme,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            //affichage de la date selectionné
                            dateDBtn.setText(
                                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            )
                            dateDBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerD!!.show()
            }

            //date fin Picker Btn
            val dateFBtn = mDialogView.findViewById<Button>(R.id.dateFEmpBtn)
            // affichage de calendrier lors de la clique
            dateFBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepickerF = DatePickerDialog(
                        this,
                        R.style.DialogTheme,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            //affichage de la date selectionné
                            dateFBtn.setText(
                                    dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            )
                            dateFBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerF!!.show()
            }

            //show dialog
            mBuilder.show()
        }




           }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_personel,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search)
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
                        if((it.metier!!.toLowerCase().contains(newText))|| (it.nom!!.toLowerCase().contains(newText))){
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
                        "ligne.demande.appro_personnel", "search_read",
                        Arrays.asList(
                                Arrays.asList(
                                        Arrays.asList("demande_appro_personnel_id", "=", "PISCINE SEMI OLYMPIQUE REGHAIA")
                                )
                        ),
                        object : HashMap<Any, Any>() {
                            init {
                                put(
                                        "fields",
                                        Arrays.asList("job_id","employee_ids")

                                )
                            }
                        }
                )) as Array<Any>)
                println("**************************  champs chantier = $list")


                val jsonArray = JSONArray(list)

                for(i in 0..(list.size)-1){
                    var typeObj = jsonArray.getJSONObject(i).getString("job_id").toString()
                  var typeObj1 = jsonArray.getJSONObject(i).getString("employee_ids").toString()
                    var type = typeObj.split("\"")[1]
                    var type2 = type.split("\"")[0]




//
                    println("**************************  metier = $type2")
                   println("**************************  nom employé = $typeObj1")

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
