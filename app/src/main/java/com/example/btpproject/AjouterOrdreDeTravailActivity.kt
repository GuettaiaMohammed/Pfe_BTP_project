package com.example.btpproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.*
import java.text.SimpleDateFormat


class AjouterOrdreDeTravailActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var viewPager: ViewPager
    lateinit var nameOT: TextInputEditText
    private val listLot = arrayListOf<String>()

    private var articleAdapter: ArticleLigneSuppAdapter? = null
    private var listViewArticle: ListView? = null
    private var listArticles: ArrayList<ArticleOT>? = null

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
        nameOT = findViewById(R.id.NomOTET)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = dateFormat.format(Date()).toString()
        nameOT.setText(date)
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

        val adapter = ViewPagerAdapter(supportFragmentManager)

        lateinit var fragmentLigneLot: FragmentListeLigneLotAjouteOt
        lateinit var fragmentArticle: FragmentListeArticlesAjoutOt
        lateinit var fragmentLigneSupp: FragmentListeLigneSuppAjoutOt
        spinnerE.onItemSelectedListener = object:  AdapterView.OnItemSelectedListener{

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val nomLot: String = parent!!.getItemAtPosition(position).toString()
                adapter.clear()

                if(nomLot != ""){

                    val idLot = jsonArray3.getJSONObject(position-1).getString("id").toString()
                    fragmentLigneLot = FragmentListeLigneLotAjouteOt(idLot.toInt())
                    fragmentArticle = FragmentListeArticlesAjoutOt(idLot.toInt())
                    fragmentLigneSupp = FragmentListeLigneSuppAjoutOt(idLot.toInt())
                    //Ajouter les fragments
                    adapter.addFragment(fragmentLigneLot, "Lignes")
                    adapter.addFragment(fragmentArticle, "Articles")
                    adapter.addFragment(fragmentLigneSupp, "Lignes Supp")

                }
                //Adapter setup

                viewPager.setAdapter(adapter)
                viewPager.setSaveFromParentEnabled(false)
                tabLayout.setupWithViewPager(viewPager)

            }
        }

        //Coté ajoute
        val connRefMax = Reference().execute(url)
        val ref= connRefMax.get().toInt()
        val refMax = ref+1

        val numOt = findViewById<TextInputEditText>(R.id.refOTET)
        numOt.setText(refMax.toString())


        val validerBtn = findViewById<Button>(R.id.validerAjtOtBtn)
        validerBtn.setOnClickListener {
            // Id lot
            val positionLot: Int = spinnerE.selectedItemPosition
            val nomLot: String = spinnerE.selectedItem.toString()
            var idLot = ""
            if(positionLot > 0) {
                idLot = jsonArray3.getJSONObject(positionLot - 1).getString("id").toString()
            }
            val nom = nameOT.text.toString()
            val num = numOt.text.toString()

            //get projet Id
            val connIdp = ProjetId().execute(url)
            val projectID = connIdp.get()

            //get emplacement chantier id
            val connIdE = EmplacementId().execute(url)
            val emplacementID = connIdE.get()
            if(nom!="" && num!="" && nomLot!=""){
                val connAjt = AjouterOT().execute(nom, num, projectID.toString(), idLot, emplacementID.toString(), nomLot, date)

                if(connAjt.get() != 0) {
                    val idNvOT = connAjt.get()
                    fragmentLigneLot.create(idNvOT!!.toInt())
                    fragmentArticle.create(idNvOT.toInt())
                    fragmentLigneSupp.createLigneSupp(idNvOT.toInt())

                    val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
                     //start your next activity
                    startActivity(intent)
                }


            }else{
                Toast.makeText(this, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
            }

        }
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
                            Arrays.asList("chantier_id", "=", 2),
                            Arrays.asList("state", "=", "en_cour")
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

    class ProjetId : AsyncTask<String, Void, Int>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): Int {
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
                            Arrays.asList("id", "=", 2)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("project_id")
                            )
                        }
                    }
                )) as Array<Any>)

                if(list.isNotEmpty()){
                    val jsonArray = JSONArray(list)
                    val idP= jsonArray.getJSONObject(0).getString("project_id").toString()
                    val idp1 = idP.split("[")[1]
                    val idp2 = idp1.split(",")[0]
                    println("**************************** projet = ${idP.toString()}")
                    println("**************************** id projet = ${idp2.toString()}")
                    return idp2.toInt()
                }


            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    class EmplacementId : AsyncTask<String, Void, Int>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): Int {
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
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id", "=", 2)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("property_stock_chantier")
                            )
                        }
                    }
                )) as Array<Any>)

                if(list.isNotEmpty()){
                    val jsonArray = JSONArray(list)
                    val idE= jsonArray.getJSONObject(0).getString("id").toInt()
                    println("**************************** id emplacement = ${idE.toString()}")
                    return idE
                }


            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    class Reference : AsyncTask<String, Void, Int>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): Int {
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
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id", "=", 2)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("reference")
                            )
                        }
                    }
                )) as Array<Any>)

                var listeRef = ArrayList<Int>()
                if(list.isNotEmpty()){
                    for(i in 0..(list.size)-1) {
                        val jsonArray = JSONArray(list)
                        val ref = jsonArray.getJSONObject(i).getString("reference").toInt()
                        println("**************************** id emplacement = ${ref.toString()}")
                        listeRef.add(ref)
                    }
                    var refMax: Int = 0
                    if(listeRef.size == 1 ){
                        refMax = listeRef[0]
                        return refMax
                    }else {
                        for (i in 0..(listeRef.size) - 1) {
                            if (refMax <= listeRef[i]){
                                refMax = listeRef[i]
                            }
                        }
                        return refMax
                    }
                }


            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    class AjouterOT : AsyncTask<String, Void,Int?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"


        var nom:String=""
        var ref:Int=0
        var nomLot:String = ""
        var idL:Int=0
        var idP:Int=0
        var idE:Int=0
        var date = ""

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): Int? {
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


                nom = infos[0]
                ref = infos[1].toInt()
                idP= infos[2].toInt()
                idL= infos[3].toInt()
                idE= infos[4].toInt()
                nomLot = infos[5]
                date = infos[6]

                println("**************** nom : ${nom}")
                println("**************** ref : ${ref}")
                println("**************** projet : ${idP}")
                println("**************** lot : ${idL}")
                println("**************** emplac : ${idE}")
                println("**************** nomLot : ${nomLot}")
                println("**************** date : ${date}")


                var id: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "ordre.travail", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("name", nom)
                                put("chantier_id", 2)
                                put("project_id", idP)
                                put("lot_id", idL)
                                put("reference", Integer.valueOf(ref))
                                put("date_debut", date)
                                put("property_stock_chantier", idE)

                            }
                        })
                    )
                ) as Int
                println("************************  liste des données = $id")
                return id

            } catch (e: MalformedURLException) {
                Log.d(
                    "MalformedURLException",
                    "*********************************************************"
                )
                Log.d("MalformedURLException", e.toString())
            } catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return 0
        }
    }

}
