package com.example.btpproject

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.EventLogTags
import android.util.Log
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tableau_de_board)

        i = intent
        id_chantier = i.getIntExtra("idChantier",0)

        val conn=ListeArticleD().execute(id_chantier)
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

        for (i in 0..(mesArticles.size) - 1){
            val nameA:String = mesArticles.get(i).nom.toString()
            val qteD: Float = mesArticles.get(i).qteDemande.toString().toFloat()
            if(values.size > 0) {
                for (j in 0..values.size - 1) {
                    val nameV = values.get(j).label
                    if (values.get(j).label.equals(nameA)) {
                        var qteV = values.get(j).value
                        var qte = qteV + qteD
                        values.remove(values.get(j))
                        values.add(PieEntry(qte, nameA))
                    }
                    if(j == values.size-1 && !(values.get(j).label.equals(nameA))){
                        values.add(PieEntry(qteD, nameA))
                    }
                }
            }else{
                values.add(PieEntry(qteD, nameA))
            }
        }


        pie.animateY(1000)


        var pieDataSet: PieDataSet = PieDataSet(values, "Articles")
        pieDataSet.sliceSpace = 3f
        pieDataSet.selectionShift = 5f
        pieDataSet.setColors(Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53))

        var pieData: PieData = PieData(pieDataSet)
        pieData.setValueTextSize(10f)
        pieData.setValueTextColor(Color.YELLOW)

        val description1 = Description()
        description1.text = "Articles"

        pie.setDescription(description1)
        pie.data = pieData


        // BAR
        val conn2=Metiers().execute(id_chantier)
        mesMetiers = conn2.get() as ArrayList<Employe>

        bar = findViewById(R.id.barChart)
        var barEntries: ArrayList<BarEntry> = ArrayList()
        var metiers: ArrayList<String> = ArrayList()

        val conn3= MonChantier.Metier().execute("")
        val listMetier=conn3.get()

        val jsonArray4 = JSONArray(listMetier)

        //récupéré lles données de l'objet JSON
        if(listMetier!!.isNotEmpty()) {
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


        var barDataSet: BarDataSet = BarDataSet(barEntries , "Nombre employés")
        barDataSet.setColors(Color.rgb(193, 37, 82), Color.rgb(255, 102, 0), Color.rgb(245, 199, 0),
            Color.rgb(106, 150, 31), Color.rgb(179, 100, 53))

        val description = Description()
        description.text = "Métiers"

        var barData = BarData(barDataSet)
        //barData.barWidth = 0.9f
        bar.setDescription(description)
        bar.data = barData


        val xAxis: XAxis = bar.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(metiers)
        xAxis.position = XAxis.XAxisPosition.BOTH_SIDED
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = metiers.size
        xAxis.labelRotationAngle = 270f

        bar.animateY(2000)
        bar.invalidate()
    }

    class ListeArticleD : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg idCh: Int?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
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
                val record= Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "project.chantier", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id","=",idCh)
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
                val json = JSONArray(record)
                var  name:String= ""
                name=json.getJSONObject(0).getString("reference").toString()


                println("********* namer =$name")
                val list1 = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
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
                val jsonArray = JSONArray(list1)
                var listeArticleDem = java.util.ArrayList<Article>()

                for (i in 0..(list1.size) - 1) {

                    var name = jsonArray.getJSONObject(i).getString("name").toString()


                    val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        db, uid, password,
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

                            val list2 = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                db, uid, password,
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

                            for (i1 in 0..(list2.size) - 1) {
                                var state =
                                    jsonArray3.getJSONObject(i1).getString("state").toString()
                                var qte = jsonArray3.getJSONObject(i1).getString("product_uom_qty")
                                    .toString()
                                var idA = jsonArray3.getJSONObject(i1).getString("id").toInt()

                                var typeObj =
                                    jsonArray3.getJSONObject(i1).getString("product_id").toString()
                                var type = typeObj.split("\"")[1]
                                var type2 = type.split("\"")[0]

                                var date= jsonArray3.getJSONObject(i1).getString("date_expected").toString()

                                // println("**************************  article = $type2")

                                listeArticleDem!!.add(Article(idA, type2, qte ,date, state))
                            }
                        }
                    }


                }



                //  println("**************************  article = $listeArticleDem ")
                return listeArticleDem

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }


    class Metiers : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"
        val ids:ArrayList<Int> =ArrayList<Int>()
        var listeEmp = ArrayList<Employe>()

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
                    db, uid, password,
                    "demande.appro_personnel", "search_read",
                    Arrays.asList(
                        Arrays.asList(

                            Arrays.asList("chantier_id", "=", idCh)

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

                for(i in 0..(listId.size)-1) {
                    var idD = jsonArray1.getJSONObject(i).getString("id").toInt()
//



                    val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        db, uid, password,
                        "ligne.demande.appro_personnel", "search_read",
                        Arrays.asList(
                            Arrays.asList(
                                Arrays.asList(
                                    "demande_appro_personnel_id",
                                    "=",idD)

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


                    val jsonArray = JSONArray(list)


                    for(i in 0..(list.size)-1){
                        var metierObj = jsonArray.getJSONObject(i).getString("job_id").toString()
                        var qte = jsonArray.getJSONObject(i).getString("qte").toString()

                        var met = metierObj.split("\"")[1]
                        var met2 = met.split("\"")[0]


                        println("**************************  metier = $met2")
                        println("**************************  qte = $qte")

                        listeEmp!!.add(Employe(qte, met2))

                    }}
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
}
