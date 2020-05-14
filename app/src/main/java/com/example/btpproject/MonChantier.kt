package com.example.btpproject

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.AsyncTask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.indexOf
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
import kotlinx.android.synthetic.main.activity_ajouter_article.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_employe.view.*
import kotlinx.android.synthetic.main.activity_ajouter_materiel.view.*
import kotlinx.android.synthetic.main.activity_detail_materiel.view.*
import kotlinx.android.synthetic.main.activity_mon_chantier2.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.indexOf as indexOf1

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

    lateinit var i: Intent
    var id_chantier: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mon_chantier2)

        i = intent
        id_chantier = i.getIntExtra("idChantier",0)

        val nomChantier = findViewById<TextView>(R.id.nomChantier)
        val date_debut = findViewById<TextView>(R.id.date_debut)
        val date_fin_prev = findViewById<TextView>(R.id.date_fin_prev)
        val date_fin_reel = findViewById<TextView>(R.id.date_fin_reel)


        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Mon chantier"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //botton de documents
        docBtn.setOnClickListener {
            //val connPDf = PDFUrl().execute(id_chantier)
            //val attach = connPDf.get() as String
            //Toast.makeText(this, "Attach: $attach", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, PDFActivity::class.java)
            // start your next activity
            startActivity(intent)

        }

        //
        //remplire les listes de spinner
        listArticles.add("")
        listUnites.add("")
        listMetiers.add("")
        listMateriels.add("")



        listView = findViewById(R.id.listeLot)
        lotAdapter = LotAdapter(applicationContext, 0)
        mesLots = ArrayList()
        (mesLots as ArrayList<Lot>).add(Lot("Numero ","Lot","Etat"))
        //
        val conn = Connexion().execute(id_chantier)
        val conn1 = DetailChantier().execute(id_chantier)
        val conn2=Materiel().execute(url)
        val conn3=Metier().execute(url)
        val conn4=Article().execute(url)
        val conn5=Unite().execute(url)

        //liste type matériels
       val listM =conn2.get()
        //liste métiers
        val listMetier=conn3.get()
        //liste articles
        val listArticle=conn4.get()
        //liste des unités
        val listU=conn5.get()
        //liste de lots
        val list = conn.get()
        //liste info chantier
        val list2 = conn1.get()


        val jsonArray3 = JSONArray(listM)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listM!!.size) - 1) {

            val name = jsonArray3.getJSONObject(i).getString("name").toString()


            listMateriels.add(name)

        }


        val jsonArray4 = JSONArray(listMetier)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listMetier!!.size) - 1) {

            val name = jsonArray4.getJSONObject(i).getString("name").toString()


            listMetiers.add(name)

        }
        val jsonArray8: JSONArray
        jsonArray8 = JSONArray(listArticle)

        //récupéré lles données de l'objet JSON
       for (i in 0..(listArticle!!.size) - 1) {

            val name = jsonArray8.getJSONObject(i).getString("name").toString()


            listArticles.add(name)

        }
        val jsonArray6 = JSONArray(listU)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listU!!.size) - 1) {

            val name = jsonArray6.getJSONObject(i).getString("name").toString()

            listUnites.add(name)

        }

        //recupéré l'objet JSON
        val jsonArray = JSONArray(list)

        //récupéré lles données de l'objet JSON
        for (i in 0..(list!!.size) - 1) {
            val num = jsonArray.getJSONObject(i).getString("num").toString()
            val name = jsonArray.getJSONObject(i).getString("name").toString()
            val state = jsonArray.getJSONObject(i).getString("state").toString()




            mesLots!!.add(Lot(num, name, state))
        }
        lotAdapter!!.addAll(mesLots)
        listView!!.adapter = lotAdapter


        val jsonArray2 = JSONArray(list2)

        for (i in 0..(list2!!.size) - 1) {

            val name = jsonArray2.getJSONObject(i).getString("name").toString()
            val dateD = jsonArray2.getJSONObject(i).getString("date_debut").toString()
            val dateFprev = jsonArray2.getJSONObject(i).getString("date_fin_prev").toString()
            val dateFreel = jsonArray2.getJSONObject(i).getString("date_fin_reel").toString()

            nomChantier.setText(name)
            date_debut.setText(dateD)
            date_fin_prev.setText(dateFprev)
            date_fin_reel.setText(dateFreel)


        }







            //button click to show dialog ajout d'article
        ajoutArticle.setOnClickListener {
            var article:String=""
            var nameA:String=""
            var qte:String=""
            var prix:String=""
            var idA:Int=0
            var idU:Int=0
            var ref:String="piscine_semi_olympique_ref"
            var unite:String=""
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_article, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                    mBuilder.setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            val mAlertDialog = mBuilder.show()
            //Spinner
            val spinnerA = mDialogView.findViewById<Spinner>(R.id.spinnerA)

            //Remplire Spinner
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listArticles)
            val adapter1: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listUnites)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerA.adapter = adapter


            //button valider
            mDialogView.button.setOnClickListener {
                //Toast.makeText(this, spinnerA.selectedItem.toString() + " " + " La quantité : " + mDialogView.qte.text + " " + spinnerU.selectedItem.toString(), Toast.LENGTH_SHORT).show()
                article=spinnerA.selectedItem.toString()

                qte= mDialogView.qte.text.toString()




                for (i in 0..(listArticle!!.size) - 1) {

                    val name = jsonArray8.getJSONObject(i).getString("name").toString()


                    if(name==article)
                    {

                        idA=jsonArray8.getJSONObject(i).getString("id").toInt()
                        prix=jsonArray8.getJSONObject(i).getString("standard_price").toString()


                        var typeObj =
                            jsonArray8.getJSONObject(i).getString("uom_id").toString()

                     var type2 = typeObj.get(1).toString()

                        var type = typeObj.split("\"")[1]
                        unite= type.split("\"")[0]


                       idU=type2.toInt()

                        nameA=name

                    }

                }
                val c: SimpleDateFormat = SimpleDateFormat("dd/M/yyyy")
                var d=c.format((Date()))


if (qte != "" && article !="") {
   /// mDialogView.qte.setText( mDialogView.qte.text.toString()+unite)
    val demandeA =
        AjouterArticle().execute(ref, idA.toString(), idU.toString(), qte, d, nameA, prix)
    Toast.makeText(mBuilder.context, "Demande envoyée : \n"+"Article :"+nameA+" Quantité :"+qte+unite, Toast.LENGTH_SHORT).show()

    mBuilder.dismiss()
}else
{

    Toast.makeText(mBuilder.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
}
            }

        }

        //button click to show dialog ajout de matériel
        ajoutEmploye.setOnClickListener {
            var dateD:String=""
            var dateF:String=""
            var nbr:Int=0
            var n:String=""
            var metier:String=""

            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                    mBuilder.setView(mDialogView)
            //.setTitle("Login Form")



            //Spinner
            val spinnerE = mDialogView.findViewById<Spinner>(R.id.spinnerE)

            //Remplire Spinner
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listMetiers)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.adapter = adapter





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
                            dateD = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
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
                            dateF= dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            dateFBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerFEmp!!.show()
            }


            //button valider
            mDialogView.button.setOnClickListener {

                //récupérer les données saisis
                n= mDialogView.nbr.text.toString()
                nbr=n.toInt()
                metier=spinnerE.selectedItem.toString()
                // récupérer l 'id de métier
                var id:Int=0
                for (i in 0..(listMetier!!.size) - 1) {

                    val name = jsonArray4.getJSONObject(i).getString("name").toString()
                    if(name==metier)
                    {
                        id=jsonArray4.getJSONObject(i).getString("id").toInt()
                       // Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()

                    }

                }
                if (dateD != ""&& dateF != "" && metier != "" && n != "") {

                    val demandeE =
                        AjouterEmploye().execute(id.toString(), dateD, dateF, nbr.toString())
                    mBuilder.dismiss()
                }else
                {

                    Toast.makeText(mBuilder.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
                }

            }






            //show dialog
            val mAlertDialog = mBuilder.run { show() }

        }

        //button click to show dialog
        ajoutMateriel.setOnClickListener {
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


            //Spinner
            val spinnerM = mDialogView.findViewById<Spinner>(R.id.spinnerM)


            //Remplire Spinner
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listMateriels)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerM.adapter = adapter




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
                            dateD = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
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
                            dateF = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                            dateFBtn.textSize = 12F
                        }, year, month, day
                )
                mDatepickerFMat!!.show()
            }



            //button valider
            mDialogView.button.setOnClickListener {
                type=spinnerM.selectedItem.toString()
                detail= mDialogView.comnt.text.toString()


                var id:Int=0
                for (i in 0..(listM!!.size) - 1) {

                    val name = jsonArray3.getJSONObject(i).getString("name").toString()

                    if(name==type)
                    {
                        id=jsonArray3.getJSONObject(i).getString("id").toInt()
                        // Toast.makeText(this, id.toString(), Toast.LENGTH_SHORT).show()

                    }

                }

                if(type != "" && detail != "" && dateD != "" && dateF != "") {
                    val demandeM = AjouterMateriel().execute(id.toString(), dateD, dateF, detail)
              mBuilder.dismiss()
                }else{


                    Toast.makeText(mBuilder.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
                }

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
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_monCh->
            {
                val intent = Intent(this, MonChantier::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_materiel ->
            {
                val intent = Intent(this, ListeMaterielsActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_employe ->
            {
                val intent = Intent(this, ListeEmployeActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_article ->
            {
                val intent = Intent(this, ListeArticleActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_suiviJ ->
            {
                val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_avance ->
            {
                val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
                // start your next activity
                startActivity(intent)
                return true
            }
            item.itemId == R.id.navigation_ordreTravail ->
            {
                val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
                intent.putExtra("idChantier", id_chantier)
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



















    class Connexion : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg idCh: Int?): List<Any>? {
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
                                        asList("chantier_id", "=", idCh)
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

    class DetailChantier : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg idCh: Int?): List<Any>? {
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

                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", "http://sogesi.hopto.org:7013"))
                            }
                        })
                    }
                }
                // récupérer détails de chantier
                val record = (models.execute(
                        "execute_kw", asList(
                        *arrayOf(db, uid, password, "project.chantier", "read", asList(idCh),
                                object : java.util.HashMap<Any,Any>() {
                            init {
                                put("fields", asList("name", "date_debut", "date_fin_prev", "date_fin_reel"))
                            }
                        }))
                ) as Array<Any>)[0] as Map<Any, String>

                return listOf(record)







            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }


    }

    class Materiel : AsyncTask<String, Void, List<Any>?>() {
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

                liste = asList(*models.execute("execute_kw", asList(
                        db, uid, password,
                        "type.materiel", "search_read",
                        asList(asList(
                                asList("id", "!=", 0)

                        )
                        ),
                        object : java.util.HashMap<Any,Any>() {
                            init {
                                put("fields", asList("name","id"))
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


    class Metier : AsyncTask<String, Void, List<Any>?>() {
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

                liste = asList(*models.execute("execute_kw", asList(
                        db, uid, password,
                        "hr.job", "search_read",
                        asList(asList(
                                asList("id", "!=", 0)
                        )
                        ),
                        object : java.util.HashMap<Any,Any>() {
                            init {
                                put("fields", asList("name","id"))
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



    class Article : AsyncTask<String, Void, List<Any>?>() {
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

                liste = asList(*models.execute("execute_kw", asList(
                        db, uid, password,
                        "product.product", "search_read",
                        asList(asList(
                                asList("id", "!=", 0)
                        )
                        ),
                        object : java.util.HashMap<Any,Any>() {
                            init {
                                put("fields", asList("name","id","standard_price","uom_id"))
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



    class Unite : AsyncTask<String, Void, List<Any>?>() {
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

                liste = asList(*models.execute("execute_kw", asList(
                        db, uid, password,
                        "uom.uom", "search_read",
                        asList(asList(
                                asList("id", "!=", 0)
                        )
                        ),
                        object : java.util.HashMap<Any,Any>() {
                            init {
                                put("fields", asList("name","id"))
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



    class AjouterEmploye : AsyncTask<String, Void,List<Any>?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"


        var dateD:String=""
        var dateF:String=""
        var nbr:Int=0
        var idJob:Int=0

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): List<Any>? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
                    common_config, "authenticate", asList(
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



                idJob = infos[0].toInt()
                dateD = infos[1]
                dateF = infos[2]
                nbr =infos[3].toInt()




                var id: Int = models.execute(
                    "execute_kw", asList(
                        db, uid, password,
                        "demande.appro_personnel", "create",
                        asList(object : java.util.HashMap<Any, Any>() {
                            init {
                                put("chantier_id", 2)
                                put("date_debut", dateD)
                                put("date_fin", dateF)

                            }
                        })
                    )
                ) as Int
                println("************************  liste des données = $id")

                var id1: Int = models.execute(
                    "execute_kw", asList(
                        db, uid, password,
                        "ligne.demande.appro_personnel", "create",
                        asList(object : java.util.HashMap<Any, Any>() {
                            init {
                                put("chantier_id", 2)
                                put("demande_appro_personnel_id",id)
                                put("qte", nbr)
                                put("job_id",idJob)

                            }
                        })
                    ))as Int
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


    class AjouterMateriel : AsyncTask<String, Void,List<Any>?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"


        var dateD:String=""
        var dateF:String=""
        var detail:String=""
        var idM:Int=0

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): List<Any>? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
                    common_config, "authenticate", asList(
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



                idM = infos[0].toInt()
                dateD = infos[1]
                dateF = infos[2]
                detail =infos[3]




                var id: Int = models.execute(
                    "execute_kw", asList(
                        db, uid, password,
                        "demande.appro_mat", "create",
                        asList(object : java.util.HashMap<Any, Any>() {
                            init {
                                put("chantier_id", 2)
                                put("type_materiel_id", idM)
                                put("date_debut", dateD)
                                put("date_fin", dateF)
                                put("detail_mat", detail)

                            }
                        })
                    )
                ) as Int
                println("************************  liste des données = $id")


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


    class AjouterArticle : AsyncTask<String, Void,List<Any>?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

var idDemnd:Int=0
        var ref:String=""
        var dateD:String=""
        var nomA:String=""
        var prix:String=""
        var idA:Int=0
        var idU:Int=0
        var qte:Int=0

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): List<Any>? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
                    common_config, "authenticate", asList(
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



              ref = infos[0]
                idA= infos[1].toInt()
                idU= infos[2].toInt()
                qte= infos[3].toInt()
                dateD = infos[4]
                nomA=infos[5]
                prix=infos[6]







                var liste: List<*> = java.util.ArrayList<Any>()

                liste = asList(*models.execute("execute_kw", asList(
                    db, uid, password,
                    "purchase.order", "search_read",
                    asList(asList(
                        asList("origin", "=", ref),
                        asList("state","=","draft")
                    )
                    ),
                    object : java.util.HashMap<Any,Any>() {
                        init {
                            put("fields", asList("id"))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)
                println("************************  liste des données = $liste")
                val json=JSONArray(liste)
                for (i in 0..(liste.size)-1)
                {
                    idDemnd=json.getJSONObject(i).getString("id").toInt()

                }


                var id1: Int = models.execute(
                    "execute_kw", asList(
                        db, uid, password,
                        "purchase.order.line", "create",
                        asList(object : java.util.HashMap<Any, Any>() {
                            init {
                                put("order_id", idDemnd)
                                put("product_id", idA)
                                put("name",nomA)
                                put("product_uom",idU)
                                put("product_qty",qte)
                                put("date_planned",dateD)
                                put("price_unit",prix)


                            }
                        })
                    ))as Int
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

    class PDFUrl : AsyncTask<Int, Void, String?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg idCh: Int?): String? {
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
                            asList("chantier_id", "=", idCh)
                        )
                    ),
                    object : java.util.HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                asList("attachment_pdf")
                            )
                        }
                    }
                )) as Array<Any>)

                if(list.isNotEmpty()){
                    println("**************************  champs chantier = $list")
                    val json=JSONArray(list)
                    val attach =  json.getJSONObject(0).getString("attachment_pdf").toString()
                    println("**************************  champs chantier = $attach")
                    return attach
                }


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





