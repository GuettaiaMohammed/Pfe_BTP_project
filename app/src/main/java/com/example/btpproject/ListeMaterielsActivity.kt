package com.example.btpproject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import java.util.*
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.IntegerRes
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_materiel.view.*

import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import java.net.MalformedURLException
import java.net.URL
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import java.util.Collections.emptyMap
import org.json.JSONArray
import java.io.Serializable

import java.lang.reflect.Type
import kotlin.collections.HashMap


class ListeMaterielsActivity : AppCompatActivity() {

    private var mesMateriels: ArrayList<Materiel>? = null
    private var listView: ListView? = null
    private var materielAdapter: MaterielAdapter? = null

    //liste de spinner
    private val listMateriels = arrayListOf<String>()

    var mDatepickerD: DatePickerDialog? = null
    var mDatepickerF: DatePickerDialog? = null

    lateinit var intt: Intent
    var id_chantier:Int = 0
    lateinit var mPreferences : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_materiels)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes matériel")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        intt = intent
        id_chantier = intt.getIntExtra("idChantier",0)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        listMateriels.add("")

        listView = findViewById(R.id.materielsListe)
        materielAdapter = MaterielAdapter(applicationContext, 0)
        mesMateriels = ArrayList()
        //Faire la connexion avec le serveur

        //liste des demandes matériels
        val conn = Connexion().execute(id_chantier.toString(), url, db, username, password)
        val list = conn.get()

        val conn2= MonChantier.Materiel().execute(url, db, username, password)
        //liste type matériels
        val listM =conn2.get()


        val jsonArray3 = JSONArray(listM)

        //récupéré lles données de l'objet JSON
        if(listM!=null){ for (i in 0..(listM!!.size) - 1) {

            val name = jsonArray3.getJSONObject(i).getString("name").toString()


            listMateriels.add(name)

        }}

        //recupéré l'objet JSON
        val jsonArray = JSONArray(list)

        //récupéré lles données de l'objet JSON
   if(list!=null){     for (i in 0..(list!!.size) - 1) {
            val dateD = jsonArray.getJSONObject(i).getString("date_debut").toString()
            val dateF = jsonArray.getJSONObject(i).getString("date_fin").toString()
            var typeObj =
                jsonArray.getJSONObject(i).getString("materiel_id").toString()
            var type = typeObj.split("\"")[1]
            var type2 = type.split("\"")[0]
            var detail=jsonArray.getJSONObject(i).getString("state").toString()
            println("**************************  type = $type2")
            println("**************************  Date debut = $dateD")

            mesMateriels!!.add(Materiel(type2,detail, dateD, dateF))
        }}

        materielAdapter!!.addAll(mesMateriels)
        listView!!.adapter = materielAdapter
        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..mesMateriels!!.size) {
                if (position == i) {
                    val intent = Intent(this, DetailMaterielActivity::class.java)
                    val id = jsonArray.getJSONObject(i).getString("id").toString()

                    intent.putExtra("id",id.toInt())
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                }
            }

        })


        //button click to show dialog
        fabMateriel.setOnClickListener {
            var dateD:String=""
            var dateF:String=""
            var type:String=""
            var detail:String=""
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_materiel, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                mBuilder.setView(mDialogView)
            //.setTitle("Login Form")

            //date Début Picker Btn
            val dateDBtn = mDialogView.findViewById<Button>(R.id.dateDMatBtn)
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
                        dateD = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                        dateDBtn.textSize = 12F
                    }, year, month, day
                )
                mDatepickerD!!.show()
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
                mDatepickerF = DatePickerDialog(
                    this,
                        R.style.DialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        //affichage de la date selectionné
                        dateFBtn.setText(
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        )
                        dateF = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                        dateFBtn.textSize = 12F
                    }, year, month, day
                )
                mDatepickerF!!.show()
            }




            //Spinner
            val spinnerM = mDialogView.findViewById <Spinner>(R.id.spinnerM)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listMateriels)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerM.setAdapter(adapter)


            //button valider
            mDialogView.button.setOnClickListener{

                type=spinnerM.selectedItem.toString()
                detail= mDialogView.comnt.text.toString()


                var id:Int=0
       if (list!=null){         for (i in 0..(listM!!.size) - 1) {

                    val name = jsonArray3.getJSONObject(i).getString("name").toString()

                    if(name==type)
                    {
                        id=jsonArray3.getJSONObject(i).getString("id").toInt()
                        // Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()

                    }

                }}

                if(type != "" && detail != "" && dateD != "" && dateF != "") {

                    val dateDT = dateDBtn.text
                    val dateFT = dateFBtn.text

                    val jDateDT = dateDT.split("/")[0]
                    val mDateDT = dateDT.split("/")[1]
                    val aDateDT = dateDT.split("/")[2]

                    val jDateFT = dateFT.split("/")[0]
                    val mDateFT = dateFT.split("/")[1]
                    val aDateFT = dateFT.split("/")[2]

                    val dateDC: Calendar = Calendar.getInstance()
                    dateDC.set(Calendar.DATE, jDateDT.toInt())
                    dateDC.set(Calendar.MONTH, mDateDT.toInt())
                    dateDC.set(Calendar.YEAR, aDateDT.toInt())

                    val debutDate: Date = dateDC.time

                    val dateFC: Calendar = Calendar.getInstance()
                    dateFC.set(Calendar.DATE, jDateFT.toInt())
                    dateFC.set(Calendar.MONTH, mDateFT.toInt())
                    dateFC.set(Calendar.YEAR, aDateFT.toInt())

                    val finDate: Date = dateFC.time

                    if(debutDate.compareTo(finDate) >=0){

                        Toast.makeText(mBuilder.context, "La date fin  doit etre supérieur à la date début", Toast.LENGTH_SHORT).show()

                    }else {
                        val demandeM = MonChantier.AjouterMateriel()
                            .execute(id_chantier.toString(), id.toString(), dateD, dateF, detail, url, db, username, password)
                        mBuilder.dismiss()
                        val i: Intent = intent
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(i)
                        overridePendingTransition(0, 0)
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    }
                }else{


                    Toast.makeText(mBuilder.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
                }

            }



            //show dialog
            mBuilder.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_materiel,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search_materiel)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Rechercher ici"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    materielAdapter!!.clear()
                    val search = newText.toLowerCase()
                    mesMateriels!!.forEach {
                        if((it.type!!.toLowerCase().contains(newText))
                            ||(it.type!!.toLowerCase().contains(newText))
                            ||(it.dateD!!.toLowerCase().contains(newText))
                            ||(it.dateF!!.toLowerCase().contains(newText))){
                            materielAdapter!!.add(it)
                        }
                    }
                }else{
                    materielAdapter!!.clear()
                    materielAdapter!!.addAll(mesMateriels)
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

    class Connexion : AsyncTask<String, Void, List<Any>?>() {

        override fun doInBackground(vararg v: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", v[1]))

                val uid: Int=  client.execute(
                        common_config, "authenticate", asList(
                v[2], v[3], v[4], emptyMap<Any, Any>()
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



                //liste des demandes matériels
                val list = asList(*models.execute("execute_kw", asList(
                    v[2], uid, v[4],
                    "chantier.materiel", "search_read",
                    asList(
                        asList(
                            asList("chantier_id", "=", v[0]!!.toInt())
                        )
                    ),
                    object : HashMap<Any,Any>() {
                        init {
                            put(
                                "fields",
                                asList("id","materiel_id", "date_debut", "date_fin","state","duree_mat")
                            )
                        }
                    }
                )) as Array<Any>)

                println("*************** list = $list")

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

    class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }

}
