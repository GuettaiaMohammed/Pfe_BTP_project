package com.example.btpproject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.example.btpproject.FonctionsXmlRPC.Companion.getMany2One
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_employe.view.*
import kotlinx.android.synthetic.main.activity_mon_chantier2.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import java.util.HashMap as HashMap1

class MonChantier : AppCompatActivity() {
    //
    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"

    //

    private var mesLots: ArrayList<Lot>? = null
    private var listView: ListView? = null
    private var lotAdapter: LotAdapter? = null

    //les listes des spinner
    private val listArticles = arrayListOf<String>()
    private val listUnites = arrayListOf<String>()
    private val listMetiers = arrayListOf<String>()
    private val listMateriels = arrayListOf<String>()

    //datePicker Dialog
    var mDatepickerDEmp: DatePickerDialog? = null
    var mDatepickerFEmp: DatePickerDialog? = null
    var mDatepickerDMat: DatePickerDialog? = null
    var mDatepickerFMat: DatePickerDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mon_chantier2)




        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Mon chantier"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //


        //remplire les listes de spinner
        listArticles.addAll(listOf("", "Article 1", "Article 2", "Article 3"))
        listUnites.addAll(listOf("", "m3", "m2", "Kg"))
        listMetiers.addAll(listOf("", "Architecte", "Maçon"))
        listMateriels.addAll(listOf("", "Gru", "Mini Pele"))



        listView = findViewById(R.id.listeLot)
        lotAdapter = LotAdapter(applicationContext, 0)
        mesLots = ArrayList()
        (mesLots as ArrayList<Lot>).add(Lot("Numero ","Lot","Etat"))
        //
        val conn = Connexion().execute(url)
        val conn1 = DetailChantier().execute(url)
        val list = conn.get()

        //recupéré l'objet JSON
        val jsonArray = JSONArray(list)

        //récupéré lles données de l'objet JSON
        for (i in 0..(list!!.size) - 1) {
            val num = jsonArray.getJSONObject(i).getString("num").toString()
            val name = jsonArray.getJSONObject(i).getString("name").toString()
            var state = jsonArray.getJSONObject(i).getString("state").toString()




            mesLots!!.add(Lot(num, name, state))
        }





        lotAdapter!!.addAll(mesLots)
        listView!!.adapter = lotAdapter



        //button click to show dialog ajout d'article
        ajoutArticle.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_article, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            val mAlertDialog = mBuilder.show()
            //Spinner
            val spinnerA = mDialogView.findViewById<Spinner>(R.id.spinnerA)
            val spinnerU = mDialogView.findViewById<Spinner>(R.id.spinnerUniteDMesure)
            //Remplire Spinner
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listArticles)
            val adapter1: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listUnites)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerA.adapter = adapter
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerU.adapter = adapter1

            //button valider
            mDialogView.button.setOnClickListener {
                Toast.makeText(this, spinnerA.selectedItem.toString() + " " + " La quantité : " + mDialogView.qte.text + " " + spinnerU.selectedItem.toString(), Toast.LENGTH_SHORT).show()


            }


        }

        //button click to show dialog ajout de matériel
        ajoutEmploye.setOnClickListener {
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
            spinnerE.adapter = adapter


            //button valider
            mDialogView.button.setOnClickListener {
                Toast.makeText(this, spinnerE.selectedItem.toString() + " " + " Le nombre : " + mDialogView.nbr.text, Toast.LENGTH_SHORT).show()
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
                mDatepickerDEmp = DatePickerDialog(
                        this,
                        R.style.DialogTheme,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            //affichage de la date selectionné
                            dateDBtn.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            dateDBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerDEmp!!.show()
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
                mDatepickerFEmp = DatePickerDialog(
                        this,
                        R.style.DialogTheme,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            //affichage de la date selectionné
                            dateFBtn.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            dateFBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerFEmp!!.show()
            }

            //show dialog
            val mAlertDialog = mBuilder.show()

        }

        //button click to show dialog
        ajoutMateriel.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_materiel, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                    .setView(mDialogView)
            //.setTitle("Login Form")


            //Spinner
            val spinnerM = mDialogView.findViewById<Spinner>(R.id.spinnerM)

            //Remplire Spinner
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listMateriels)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerM.adapter = adapter


            //button valider
            mDialogView.button.setOnClickListener {
                Toast.makeText(this, spinnerM.selectedItem.toString(), Toast.LENGTH_SHORT).show()
            }

            //date Début Picker Btn
            val dateDBtn = mDialogView.findViewById<Button>(R.id.dateDMatBtn)
            // affichage de calendrier lors de la clique
            dateDBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepickerDMat = DatePickerDialog(
                        this,
                        R.style.DialogTheme,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            //affichage de la date selectionné
                            dateDBtn.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            dateDBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerDMat!!.show()
            }

            //date fin Picker Btn
            val dateFBtn = mDialogView.findViewById<Button>(R.id.dateFMatBtn)
            // affichage de calendrier lors de la clique
            dateFBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepickerFMat = DatePickerDialog(
                        this,
                        R.style.DialogTheme,
                        DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            //affichage de la date selectionné
                            dateFBtn.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            dateFBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerFMat!!.show()
            }

            //show dialog
            val mAlertDialog = mBuilder.show()
        }




    }


    ///////////











    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when{
            item!!.itemId == R.id.navigation_home ->
            {
                val intent = Intent(this, MainActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_monCh->
            {
                val intent = Intent(this, MonChantier::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_materiel ->
            {
                val intent = Intent(this, ListeMaterielsActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_employe ->
            {
                val intent = Intent(this, ListeEmployeActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_article ->
            {
                val intent = Intent(this, ListeArticleActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_suiviJ ->
            {
                val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_avance ->
            {
                val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_ordreTravail ->
            {
                val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_disco ->
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
                        common_config, "authenticate", asList(
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
                val list = asList(*models.execute("execute_kw", asList(
                        db, uid, password,
                        "project.lot", "search_read",
                        asList(
                                asList(
                                        asList("chantier_id", "=", 2)
                                )
                        ),
                        object : java.util.HashMap<Any, Any>() {
                            init {
                                put(
                                        "fields",
                                        asList("num","name", "state")
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

    class DetailChantier : AsyncTask<String, Void, List<Any>?>() {
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
                        common_config, "authenticate", asList(
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





                //   println("**************************  champs chantier = $record")
               // return

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
