package com.example.btpproject


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
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

    internal lateinit var view: View
    private var mesEmployes: ArrayList<Employe>? = null
    private var listView: ListView? = null
    private var employeAdapter: EmployeSuiviAdapter? = null


    //les listes des spinner
    private val listEmployes = arrayListOf<String>()
    private val listUnites = arrayListOf<String>()
    private val listLots = arrayListOf<String>()

    lateinit var intt: Intent
    var id_chantier:Int = 0

    lateinit var mPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_employes_suivi)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Suivi Employés")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        intt = intent
        id_chantier = intt.getIntExtra("idChantier",0)

        //remplire les listes de spinner
        listEmployes.add("")
        listUnites.add("")

        listLots.add("")


        //
        val conn = Connexion().execute(id_chantier.toString(), url, db, username, password)
        val conn1=Employee().execute(url, db, username, password)
        val conn2= MonChantier.Unite().execute(url, db, username, password)
        val conn4=LigneLots2().execute(id_chantier.toString(), url, db, username, password)
        //liste des employés
        val listE=conn1.get()
        val jsonArray1 = JSONArray(listE)
     if(listE!=null){   for (i in 0..(listE!!.size) - 1) {

            val name = jsonArray1.getJSONObject(i).getString("name").toString()


            listEmployes.add(name)

        }}
        //liste des unités
        val listU=conn2.get()
        val jsonArray6 = JSONArray(listU)

        //récupéré lles données de l'objet JSON
     if(listU!=null){   for (i in 0..(listU!!.size) - 1) {

            val name = jsonArray6.getJSONObject(i).getString("name").toString()

            listUnites.add(name)

        }}


///liste lignes lots
        val listLigne=conn4.get()
       // val jsonArray2 = JSONArray(listLigne)
   if(listLigne!=null){    for (i in 0..(listLigne!!.size) - 1){
       /* var Obj1 =
            jsonArray2.getJSONObject(i).getString("name").toString()
      // var lot = Obj1.split("\"")[1]
       // var lot2 = lot.split("\"")[0]*/
       println("************ ${listLigne[i]}")

        listLots.add(listLigne[i].designation.toString())

        }}




        listView = findViewById(R.id.empl)
        employeAdapter = EmployeSuiviAdapter(applicationContext, 0)

        mesEmployes= ArrayList();
        val list = conn.get()
        //recupéré l'objet JSON
        val jsonArray = JSONArray(list)

        //récupéré lles données de l'objet JSON
       if(list!=null){ for (i in 0..(list!!.size) - 1) {
            var Obj =
                    jsonArray.getJSONObject(i).getString("employee_id").toString()
            var nom = Obj.split("\"")[1]
            var nom2 = nom.split("\"")[0]

            var Obj1 =
                    jsonArray.getJSONObject(i).getString("ligne_lot_id").toString()
            var lot = Obj1.split("\"")[1]
           var lot2 = lot.split("\"")[0]


            mesEmployes!!.add(Employe(nom2, lot2))
        }}



        employeAdapter!!.addAll(mesEmployes)
        listView!!.adapter = employeAdapter

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..mesEmployes!!.size) {
                if (position == i) {
                    val intent = Intent(this, DetailSuiviEmployeQteRealiseActivity::class.java)
                    val id = jsonArray.getJSONObject(i).getString("id").toString()


                    intent.putExtra("id", id.toInt())
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                }
            }

        })

        //button click to show dialog
        fabEmpl.setOnClickListener {
            var qteP:String=""
            var nbHP:String=""
            var pu:String=""
            var empl:String=""
            var unite:String=""
            var lLot:String=""
            var idE:Int=0
            var idU:Int=0
            var idlLot:Int=0

            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe_suivi, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                mBuilder.setView(mDialogView)
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
               // Toast.makeText(this,"ajout de : "+mDialogView.qteP.text.toString()+" , "+ mDialogView.nbH.text.toString()+" , "+mDialogView.pu.text.toString(), Toast.LENGTH_SHORT).show()
                qteP=mDialogView.qteP.text.toString()
                nbHP=mDialogView.nbH.text.toString()
                pu=mDialogView.pu.text.toString()

                empl=spinnerE.selectedItem.toString()
                unite=spinnerU.selectedItem.toString()
                lLot=spinnerL.selectedItem.toString()

                for (i in 0..(listLigne!!.size) - 1){
                    var name =listLigne[i].designation
                        //jsonArray2.getJSONObject(i).getString("name").toString()


                  if(name==lLot)
                  {
                      idlLot= listLigne[i].id!!.toInt()
                          //jsonArray2.getJSONObject(i).getString("id").toInt()
                  }

                }

                for (i in 0..(listU!!.size) - 1) {

                    val name = jsonArray6.getJSONObject(i).getString("name").toString()

                   if(name==unite)
                   {

                       idU= jsonArray6.getJSONObject(i).getString("id").toInt()
                   }

                }
                for (i in 0..(listE!!.size) - 1) {

                    val name = jsonArray1.getJSONObject(i).getString("name").toString()

                    if(name==empl)
                     {
                         idE=jsonArray1.getJSONObject(i).getString("id").toInt()
                     }

                }
                if (nbHP!="" && pu!="" && qteP!="" && empl!="" && lLot!="" && unite!="")
                {
                    val demandeES = AjouterEmployeSuivi().execute(idE.toString(),idU.toString(),idlLot.toString(),qteP,pu,nbHP, url, db, username, password)
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

                //liste des chantier
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        v[2], uid, v[4],
                        "reglement.employe", "search_read",
                        Arrays.asList(
                                Arrays.asList(
                                        Arrays.asList("chantier_id","=",v[0]!!.toInt())
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

        override fun doInBackground(vararg v: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", v[0]))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        v[1], v[2], v[3], Collections.emptyMap<Any, Any>()
                    )
                ) as Int



                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", v[0]))
                            }
                        })
                    }
                }

                //liste des chantier
                var liste: List<*> = java.util.ArrayList<Any>()

                liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[1], uid, v[3],
                    "hr.employee", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", ">", 1)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name","job_title","id"))
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
    class LignesLots : AsyncTask<Int, Void, List<Any>?>() {
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

             /*   val listLot = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "project.lot", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id","=",idCh),
                            Arrays.asList("state","=","en_cour")

                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("ligne_lot_ids")
                            )
                        }
                    }
                )) as Array<Any>)

println("**************** $listLot")

var json=JSONArray(listLot)
                for(i in 0..(listLot.size -1))
                {
                    var idL=json.getJSONObject(i).getString("ligne_lot_ids").toInt()
                    println("**************** $idL")
                }


*/
































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
                                Arrays.asList("name","id")
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

    class AjouterEmployeSuivi : AsyncTask<String, Void,List<Any>?>(){

        var qteP:String=""
        var pu:String=""
        var nbH:String=""
        var idE:Int=0
        var idLl:Int=0
        var idU:Int=0

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): List<Any>? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", infos[6]))

                val uid: Int = client.execute(
                    common_config, "authenticate", Arrays.asList(
                        infos[7], infos[8], infos[9], Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(
                                    String.format(
                                        "%s/xmlrpc/2/object",
                                        infos[6]
                                    )
                                )
                            }
                        })
                    }
                }



                println("************************  datebbb = ${infos[1]}")



                idE = infos[0].toInt()
                idU = infos[1].toInt()
                idLl= infos[2].toInt()

                qteP =infos[3]
                pu =infos[4]
                nbH =infos[5]




                var id: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        infos[7], uid, infos[9],
                        "reglement.employe", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("chantier_id", 2)
                                put("employee_id", idE)
                                put("ligne_lot_id", idLl)
                                put("unite", idU)
                                put("unit_price", pu)
                                put("qte_prev", qteP)
                                put("nb_h_prevu", nbH)

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

class LigneLots2:AsyncTask<String,Void,List<LigneLot>?>(){
    val listLigneLot = ArrayList<LigneLot>()

    override fun doInBackground(vararg v: String?): List<LigneLot>? {
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

            val listLot = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                v[2], uid, v[4],
                "project.lot", "search_read",
                Arrays.asList(
                    Arrays.asList(
                        Arrays.asList("chantier_id","=",v[0]!!.toInt()),
                        Arrays.asList("state","=","en_cour")

                    )
                ),
                object : HashMap<Any, Any>() {
                    init {
                        put(
                            "fields",
                            Arrays.asList("id")
                        )
                    }
                }
            )) as Array<Any>)

            println("**************** $listLot")

            var json=JSONArray(listLot)


            for(i in 0..(listLot.size -1)) {
                var idL = json.getJSONObject(i).getString("id").toInt()
                println("**************** $idL")


                   //liste des id des lignes
                   val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                       v[2], uid, v[4],
                       "project.lot", "search_read",
                       Arrays.asList(
                           Arrays.asList(
                               Arrays.asList("id", "=", idL)
                           )
                       ),
                       object : HashMap<Any, Any>() {
                           init {
                               put(
                                   "fields",
                                   Arrays.asList("ligne_lot_ids")
                               )
                           }
                       }
                   )) as Array<Any>)


                   if(list.isNotEmpty()) {

                       val jsonArray3 = JSONArray(list)
                       val ligneIds =
                           jsonArray3.getJSONObject(0).toString()

                       var idd: List<String> = emptyList()
                       if(ligneIds.indexOf(",") >= 0) {

                           val ids = ligneIds.split("[")[1]
                           val ids2 = ids.split("]")[0]
                           //liste final des ids
                           idd = ids2.split(",")

                           if (idd[0] != "") {

                               // recupéré les champ nom unite num des ligne par chaque Id
                               for (i in 0..(idd!!.size) - 1) {
                                   val idInt = idd[i].toInt()
                                   val listLigne =
                                       Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                           v[2], uid, v[4],
                                           "ligne.lot", "search_read",
                                           Arrays.asList(
                                               Arrays.asList(
                                                   Arrays.asList("id", "=", idInt)
                                               )
                                           ),
                                           object : HashMap<Any, Any>() {
                                               init {
                                                   put(
                                                       "fields",
                                                       Arrays.asList(
                                                           "name",
                                                           "num",
                                                           "unite"
                                                       )
                                                   )
                                               }
                                           }
                                       )) as Array<Any>)

                                   if(listLigne.isNotEmpty()) {
                                       //liste des champs
                                       val jsonArray4 = JSONArray(listLigne)




                                       val num = jsonArray4.getJSONObject(0).getString("num").toString()
                                       val name = jsonArray4.getJSONObject(0).getString("name").toString()
                                       println("*************************$idInt  ,  $num , $name")
                                       listLigneLot.add(LigneLot(idInt.toString(),name))
                                   }
                               }
                           }

                       }else{

                           val ids = ligneIds.split("[")[1]
                           val id = ids.split("]")[0]

                           if (id != "") {

                               // recupéré les champ nom unite num des ligne par chaque Id

                                   val idInt = id.toInt()
                                   val listLigne =
                                       Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                           v[2], uid, v[4],
                                           "ligne.lot", "search_read",
                                           Arrays.asList(
                                               Arrays.asList(
                                                   Arrays.asList("id", "=", idInt)
                                               )
                                           ),
                                           object : HashMap<Any, Any>() {
                                               init {
                                                   put(
                                                       "fields",
                                                       Arrays.asList(
                                                           "name",
                                                           "num",
                                                           "unite"
                                                       )
                                                   )
                                               }
                                           }
                                       )) as Array<Any>)

                                   if(listLigne.isNotEmpty()) {
                                       //liste des champs
                                       val jsonArray4 = JSONArray(listLigne)

                                       val num = jsonArray4.getJSONObject(0).getString("num").toString()
                                       val name = jsonArray4.getJSONObject(0).getString("name").toString()
                                       println("*************************$idInt  ,  $num , $name")
                                       listLigneLot.add(LigneLot(idInt.toString(), name))
                                   }
                               }
                       }

                   }



            }

            return listLigneLot
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
