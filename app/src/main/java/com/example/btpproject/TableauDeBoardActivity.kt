package com.example.btpproject

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.EventLogTags
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.android.synthetic.main.activity_liste_ordres_de_travail.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class TableauDeBoardActivity : AppCompatActivity() {

    lateinit var i: Intent
    var id_chantier:Int = 0
    lateinit var mesArticles: ArrayList<Article>
    lateinit var mesMetiers: ArrayList<Employe>

    lateinit var pie: PieChart
    lateinit var bar: BarChart
    lateinit var barG: BarChart
    lateinit var barMult: BarChart

    lateinit var mPreferences : SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tableau_de_board)
        val toolbar = findViewById<Toolbar>(R.id.toolbar3)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Statistiques")

        i = intent
        id_chantier = i.getIntExtra("idChantier", 0)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        val conn = ListeArticleD().execute(id_chantier.toString(), url, db, username, password)
        if(conn.get() != null) {
            mesArticles = conn.get() as ArrayList<Article>

            // PIE
            pie = findViewById(R.id.pieChart)
            pie.setUsePercentValues(true)
            pie.description.isEnabled = true
            pie.setExtraOffsets(5f, 10f, 5f, 5f)
            pie.dragDecelerationFrictionCoef = 0.99f
            pie.isDrawHoleEnabled = true
            pie.setHoleColor(Color.WHITE)
            pie.transparentCircleRadius = 60f

            val values: ArrayList<PieEntry> = ArrayList()
            if (mesArticles.isNotEmpty() || mesArticles != null) {
                for (i in 0..(mesArticles.size) - 1) {
                    val nameA: String = mesArticles.get(i).nom.toString()
                    val qteD: Float = mesArticles.get(i).qteDemande.toString().toFloat()
                    if (values.size > 0) {
                        for (j in 0..values.size - 1) {
                            val nameV = values.get(j).label
                            if (values.get(j).label.equals(nameA)) {
                                var qteV = values.get(j).value
                                var qte = qteV + qteD
                                values.remove(values.get(j))
                                values.add(PieEntry(qte, nameA))
                            }
                            if (j == values.size - 1 && !(values.get(j).label.equals(nameA))) {
                                values.add(PieEntry(qteD, nameA))
                            }
                        }
                    } else {
                        values.add(PieEntry(qteD, nameA))
                    }
                }
            }


            pie.animateY(1000)


            var pieDataSet: PieDataSet = PieDataSet(values, "Articles")
            pieDataSet.sliceSpace = 3f
            pieDataSet.selectionShift = 5f
            pieDataSet.setColors(
                Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
            )

            var pieData: PieData = PieData(pieDataSet)
            pieData.setValueTextSize(10f)
            pieData.setValueTextColor(Color.YELLOW)

            val description1 = Description()
            description1.text = "Articles"

            pie.setDescription(description1)
            pie.data = pieData

        }
        // BAR
        val conn2 = Metiers().execute(id_chantier.toString(), url, db, username, password)
        if(conn2.get() != null) {
            mesMetiers = conn2.get() as ArrayList<Employe>
            bar = findViewById(R.id.barChart)
            var barEntries: ArrayList<BarEntry> = ArrayList()
            var metiers: ArrayList<String> = ArrayList()

            val conn3 = MonChantier.Metier().execute(url, db, username, password)
            val listMetier = conn3.get()

            val jsonArray4 = JSONArray(listMetier)

            //récupéré lles données de l'objet JSON
            if (listMetier!!.isNotEmpty() || mesMetiers.isNotEmpty()) {
                for (i in 0..(listMetier!!.size) - 1) {

                    val name = jsonArray4.getJSONObject(i).getString("name").toString()
                    metiers.add(name)
                    barEntries.add(BarEntry(i.toFloat(), 0f))

                }

                for (i in 0..mesMetiers.size - 1) {
                    val nameM: String = mesMetiers.get(i).metier.toString()
                    val qteMD: Float = mesMetiers.get(i).nom.toString().toFloat()
                    if (metiers.size > 0) {
                        for (j in 0..metiers.size - 1) {
                            val nomM = metiers.get(j)
                            if (nameM.equals(nomM)) {
                                val barVal = barEntries.get(j).y
                                barEntries.get(j).y = qteMD + barVal
                            }
                        }
                    }
                }
            }


            var barDataSet: BarDataSet = BarDataSet(barEntries, "Nombre employés")
            barDataSet.setColors(
                Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
                Color.rgb(106, 150, 31), Color.rgb(179, 100, 53)
            )

            val description = Description()
            description.text = "Métiers"

            var barData = BarData(barDataSet)

            //barData.barWidth = 0.9f
            bar.setDescription(description)
            bar.data = barData


            val xAxis: XAxis = bar.xAxis
            xAxis.valueFormatter = IndexAxisValueFormatter(metiers)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = metiers.size
            xAxis.labelRotationAngle = 270f

            bar.animateY(2000)
            bar.invalidate()
        }

        // Group bar
        val listEmpQte: ArrayList<EmpQte>
        val conn4 = QteEmploye().execute(id_chantier.toString(), url, db, username, password)
        if(conn4.get() != null){
        listEmpQte = conn4.get() as ArrayList<EmpQte>

        barG = findViewById(R.id.barChartG)

        val barEntriesQteP: ArrayList<BarEntry> = ArrayList()
        val barEntriesQteR: ArrayList<BarEntry> = ArrayList()
        val nameEmp: ArrayList<String> = ArrayList()

        if(listEmpQte.isNotEmpty()) {
            for (i in 0..listEmpQte.size - 1) {
                val name = listEmpQte.get(i).nom
                val qteP = listEmpQte.get(i).qteP
                val qteR = listEmpQte.get(i).qteR

                barEntriesQteP.add(BarEntry(i.toFloat(), qteP.toFloat()))
                barEntriesQteR.add(BarEntry(i.toFloat(), qteR.toFloat()))
                nameEmp.add(name)
            }
        }


        var barDataSetQteP: BarDataSet = BarDataSet(barEntriesQteP, "Quantité prévue")
        barDataSetQteP.setColors(Color.GREEN)

        var barDataSetQteR: BarDataSet = BarDataSet(barEntriesQteR, "Quantité réalisé")
        barDataSetQteR.setColors(Color.YELLOW)

        var barDataGr = BarData(barDataSetQteP, barDataSetQteR)
        barDataGr.barWidth = 0.16f

        barG.data = barDataGr

        val xAxisG: XAxis = barG.xAxis
        xAxisG.valueFormatter = IndexAxisValueFormatter(nameEmp)
        xAxisG.setCenterAxisLabels(true)
        xAxisG.position = XAxis.XAxisPosition.BOTH_SIDED
        xAxisG.granularity = 1f
        xAxisG.isGranularityEnabled = true

        barG.isDragEnabled = true
        barG.setVisibleXRangeMaximum(4f)
        barG.xAxis.axisMinimum = 0f
        barG.groupBars(0f, 0.58f, 0.05f)

        barG.animateY(2000)
        barG.invalidate()

        // Multi Bar

        barMult = findViewById(R.id.barChartMult)
        var barEntriesMult: ArrayList<BarEntry> = ArrayList()
        var ligneLot: ArrayList<String> = ArrayList()
        if (listEmpQte.isNotEmpty()) {

            for (i in 0..(listEmpQte!!.size) - 1) {

                val ligne = listEmpQte.get(i).ligne
                ligneLot.add(ligne)
                barEntriesMult.add(BarEntry(i.toFloat(), floatArrayOf(0f, 0f)))

            }
            for (i in 0..listEmpQte.size - 1) {
                val ligne = listEmpQte.get(i).ligne
                val qteP = listEmpQte.get(i).qteP
                val qteR = listEmpQte.get(i).qteR
                if (ligneLot.size - 1 > 0) {
                    for (j in 0..ligneLot.size - 1) {
                        if (ligne.equals(ligneLot.get(j))) {
                            val qteR0 = barEntriesMult.get(i).yVals[1]
                            val qte = qteR.toFloat() + qteR0
                            barEntriesMult.get(i).setVals(floatArrayOf(qteP.toFloat(), qte))

                        }
                    }
                }

            }

            var barDataSetMulti: BarDataSet = BarDataSet(barEntriesMult, "Qte prévue/Qte réalisé")
            barDataSetMulti.setColors(Color.CYAN, Color.GREEN)


            var barDataMulti = BarData(barDataSetMulti)
            //barData.barWidth = 0.9f
            barMult.data = barDataMulti


            val xAxisM: XAxis = barMult.xAxis
            xAxisM.valueFormatter = IndexAxisValueFormatter(ligneLot)
            xAxisM.position = XAxis.XAxisPosition.BOTTOM
            xAxisM.setDrawAxisLine(false)
            xAxisM.setDrawGridLines(false)
            xAxisM.granularity = 1f
            xAxisM.labelCount = ligneLot.size
            xAxisM.labelRotationAngle = 270f

            barMult.animateY(2000)
            barMult.invalidate()

        }
        }
    }

        override fun onCreateOptionsMenu(menu: Menu?): Boolean {
            val inflater: MenuInflater = menuInflater
            inflater.inflate(R.menu.navigation_menu, menu)
            return true
        }

        override fun onOptionsItemSelected(item: MenuItem?): Boolean {
            when {
                item!!.getItemId() == R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }
                item!!.getItemId() == R.id.navigation_monCh -> {
                    val intent = Intent(this, MonChantier::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }
                item!!.getItemId() == R.id.navigation_materiel -> {
                    val intent = Intent(this, ListeMaterielsActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }
                item!!.getItemId() == R.id.navigation_employe -> {
                    val intent = Intent(this, ListeEmployeActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }
                item!!.getItemId() == R.id.navigation_article -> {
                    val intent = Intent(this, ListeArticleActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }

                item!!.getItemId() == R.id.navigation_avance -> {
                    val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }
                item!!.getItemId() == R.id.navigation_ordreTravail -> {
                    val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }

                item!!.getItemId() == R.id.navigation_suiviJ -> {
                    val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
                    intent.putExtra("idChantier", id_chantier)
                    // start your next activity
                    startActivity(intent)
                    return true
                }

                item!!.getItemId() == R.id.navigation_disco -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }


    class ListeArticleD : AsyncTask<String, Void, List<Any>?>() {

        override fun doInBackground(vararg v: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", v[1]))

                val uid: Int = client.execute(
                    common_config, "authenticate", Arrays.asList(
                        v[2], v[3], v[4], Collections.emptyMap<Any, Any>()
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
                                serverURL = URL(
                                    String.format(
                                        "%s/xmlrpc/2/object",
                                        v[1]
                                    )
                                )
                            }
                        })
                    }
                }
                val record= Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[2], uid, v[4],
                    "project.chantier", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id","=",v[0]!!.toInt())
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
                println("********* chantier =$record")
                if(record != null) {
                    val json = JSONArray(record)
                    var name: String = ""
                    name = json.getJSONObject(0).getString("reference").toString()


                    println("********* namer =$name")
                    val list1 = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        v[2], uid, v[4],
                        "purchase.order", "search_read",
                        Arrays.asList(
                            Arrays.asList(
                                Arrays.asList("origin", "=", name)
                            )
                        ),
                        object : HashMap<Any, Any>() {
                            init {
                                put(
                                    "fields",
                                    Arrays.asList("id", "name")
                                )
                            }
                        }
                    )) as Array<Any>)
                    println("**************************  champs chantier = $list1")
                    //liste des chantier
                    if (list1 != null) {
                        val jsonArray = JSONArray(list1)
                        var listeArticleDem = java.util.ArrayList<Article>()

                        for (i in 0..(list1.size) - 1) {

                            var name = jsonArray.getJSONObject(i).getString("name").toString()


                            val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                v[2], uid, v[4],
                                "stock.picking", "search_read",
                                Arrays.asList(
                                    Arrays.asList(
                                        Arrays.asList("origin", "=", name)
                                    )
                                ),
                                object : HashMap<Any, Any>() {
                                    init {
                                        put(
                                            "fields",
                                            Arrays.asList("id", "state", "scheduled_date")
                                        )
                                    }
                                }
                            )) as Array<Any>)
                            println("**************************  champs chantier = $list")
                            val jsonArray2 = JSONArray(list)
                            if (list != null) {
                                for (i2 in 0..(list.size) - 1) {

                                    var idP = jsonArray2.getJSONObject(i2).getString("id").toInt()

                                    val list2 =
                                        Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                            v[2], uid, v[4],
                                            "stock.move", "search_read",
                                            Arrays.asList(
                                                Arrays.asList(
                                                    Arrays.asList("picking_id", "=", idP)
                                                )
                                            ),
                                            object : HashMap<Any, Any>() {
                                                init {
                                                    put(
                                                        "fields",
                                                        Arrays.asList(
                                                            "id",
                                                            "product_id",
                                                            "product_uom_qty",
                                                            "state",
                                                            "date_expected"
                                                        )
                                                    )
                                                }
                                            }
                                        )) as Array<Any>)

                                    println("**************************  champs chantier = $list")
                                    val jsonArray3 = JSONArray(list2)
                                    if (list2 != null) {
                                        for (i1 in 0..(list2.size) - 1) {
                                            var state =
                                                jsonArray3.getJSONObject(i1).getString("state")
                                                    .toString()
                                            var qte =
                                                jsonArray3.getJSONObject(i1)
                                                    .getString("product_uom_qty")
                                                    .toString()
                                            var idA =
                                                jsonArray3.getJSONObject(i1).getString("id").toInt()

                                            var typeObj =
                                                jsonArray3.getJSONObject(i1).getString("product_id")
                                                    .toString()
                                            var type = typeObj.split("\"")[1]
                                            var type2 = type.split("\"")[0]

                                            var date =
                                                jsonArray3.getJSONObject(i1)
                                                    .getString("date_expected")
                                                    .toString()

                                            // println("**************************  article = $type2")

                                            listeArticleDem!!.add(
                                                Article(
                                                    idA,
                                                    type2,
                                                    qte,
                                                    date,
                                                    state
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                        }

                        return listeArticleDem
                    }
                }
                //  println("**************************  article = $listeArticleDem ")


            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }


    class Metiers : AsyncTask<String, Void, List<Any>?>() {

        val ids:ArrayList<Int> =ArrayList<Int>()
        var listeEmp = ArrayList<Employe>()

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


                val listId = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[2], uid, v[4],
                    "demande.appro_personnel", "search_read",
                    Arrays.asList(
                        Arrays.asList(

                            Arrays.asList("chantier_id", "=", v[0]!!.toInt())

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
                println("**************************  champs chantier = $listId")
                val jsonArray1 = JSONArray(listId)
                if(listId != null) {
                    for (i in 0..(listId.size) - 1) {
                        var idD = jsonArray1.getJSONObject(i).getString("id").toInt()
//
                        val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                            v[2], uid, v[4],
                            "ligne.demande.appro_personnel", "search_read",
                            Arrays.asList(
                                Arrays.asList(
                                    Arrays.asList(
                                        "demande_appro_personnel_id",
                                        "=", idD
                                    )

                                )
                            ),
                            object : HashMap<Any, Any>() {
                                init {
                                    put(
                                        "fields",
                                        Arrays.asList("job_id", "qte")

                                    )
                                }
                            }
                        )) as Array<Any>)
                        println("**************************  champs chantier = $list")

                        if (list != null) {
                            val jsonArray = JSONArray(list)


                            for (i in 0..(list.size) - 1) {
                                var metierObj =
                                    jsonArray.getJSONObject(i).getString("job_id").toString()
                                var qte = jsonArray.getJSONObject(i).getString("qte").toString()

                                var met = metierObj.split("\"")[1]
                                var met2 = met.split("\"")[0]


                                println("**************************  metier = $met2")
                                println("**************************  qte = $qte")

                                listeEmp!!.add(Employe(qte, met2))

                            }
                        }
                    }
                }
                return listeEmp


            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }


    }

    class QteEmploye : AsyncTask<String, Void, ArrayList<EmpQte>?>() {

        override fun doInBackground(vararg v: String?):ArrayList<EmpQte>?{
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

                if(list.isNotEmpty()) {
                    var listEmp: ArrayList<EmpQte> =  ArrayList()
                    val jsonArray = JSONArray(list)

                    for (i in 0..(list.size) - 1) {

                        var qteP = jsonArray.getJSONObject(i).getString("qte_prev").toString()
                        var qteR = jsonArray.getJSONObject(i).getString("qte_realise").toString()

                        var nomObj = jsonArray.getJSONObject(i).getString("employee_id").toString()
                        var emp = nomObj.split("\"")[1]
                        var emp2 = emp.split("\"")[0]

                        var ligneObj = jsonArray.getJSONObject(i).getString("ligne_lot_id").toString()
                        var ligne = ligneObj.split("\"")[1]
                        var ligne2 = ligne.split("\"")[0]



                        listEmp!!.add(EmpQte(emp2, ligne2, qteP, qteR))

                    }
                    return listEmp
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
    class EmpQte(var nom: String, var ligne: String, var qteP: String, var qteR: String)


}
