package com.example.btpproject

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_detail_article.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import kotlinx.android.synthetic.main.activity_receptioner_article.view.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.sql.Date
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DetailArticleQuantiteActivity : AppCompatActivity() {

    public var mesQtes: ArrayList<QuantiteArticle>? = null
    public var listView: ListView? = null
    public var qteAdapter: QuantiteArticleAdapter? = null
    var qteRecu : String?=""

    lateinit var i: Intent
    var id_chantier:Int = 0
    var id:Int = 0

    lateinit var mPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)
        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Article")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        val nom=findViewById<TextView>(R.id.name)
        val dateD=findViewById<TextView>(R.id.date)
        val qteD=findViewById<TextView>(R.id.qteDemand)
        val state=findViewById<TextView>(R.id.etat)
        val qteT=findViewById<TextView>(R.id.qteTraite)

        i = intent
        id = i.getIntExtra("id",0)
        id_chantier = i.getIntExtra("idChantier",0)

        // Toast.makeText(this, id.toString() , Toast.LENGTH_SHORT).show()

        var conn = DetailArticle2().execute(id.toString(), url, db, username, password)
        var details  = conn.get()



        var conn1=QuantiteRec().execute(id.toString(), url, db, username, password)
        var listQte=conn1.get()



var qte2:String=""
        var qte:String=""
        val jsonArray = JSONArray(details)
        for (i1 in 0..(details!!.size) - 1) {

        var date= jsonArray.getJSONObject(i1).getString("date").toString()
         qte= jsonArray.getJSONObject(i1).getString("product_uom_qty").toString()

            var etat= jsonArray.getJSONObject(i1).getString("state").toString()
           qte2= jsonArray.getJSONObject(i1).getString("quantity_done").toString()


        var typeObj =
            jsonArray.getJSONObject(i1).getString("product_id").toString()
        var type = typeObj.split("\"")[1]
        var type2 = type.split("\"")[0]
        var typeObj2 =
            jsonArray.getJSONObject(i1).getString("product_uom").toString()
        var u = typeObj2.split("\"")[1]
        var u2 = u.split("\"")[0]

            nom.setText(type2)
            qteD.setText(qte+" "+u2)
            dateD.setText(date)
            if(qte==qte2){
                receptionArticle.setEnabled(false)
            }
            if(etat=="done"){
             state.setText("Fait")

                receptionArticle.setEnabled(false)
            }
            else{
                state.setText("Prêt")
            }
            qteT.setText(qte2+" "+u2)

        }

        val jsonArray1 = JSONArray(listQte)


        listView = this.findViewById(R.id.qteListe)
        qteAdapter = QuantiteArticleAdapter(applicationContext, 0)

        this.mesQtes = ArrayList();



    if(listQte!=null) {
        (mesQtes as ArrayList<QuantiteArticle>).add(QuantiteArticle("Quantité ",  "Date réception"))

        for (i1 in 0..(listQte!!.size) - 1) {

        var date = jsonArray1.getJSONObject(i1).getString("date").toString()


        var qtec2 = jsonArray1.getJSONObject(i1).getString("qty_done").toString()


        var typeObj2 =
            jsonArray1.getJSONObject(i1).getString("product_uom_id").toString()
        var u = typeObj2.split("\"")[1]
        var u2 = u.split("\"")[0]

        if(qtec2!="0.0"){
        mesQtes!!.add(QuantiteArticle(qtec2 + " " + u2, date))}


    }
}



        receptionArticle.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_receptioner_article, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                mBuilder.setView(mDialogView)
            //.setTitle("Login Form")
            mBuilder.show()
            mDialogView.valider.setOnClickListener{
                //pour ajouter la quantité receptionné et la date
                val c: SimpleDateFormat =SimpleDateFormat("dd/M/yyyy")
                var d=c.format((Date()))

                qteRecu=mDialogView.qteRecep.text.toString()
                var qteF:Float=qte.toFloat()
                var qte2F:Float=qte2.toFloat()
                var qteRecuF:Float= qteRecu!!.toFloat()
                var diff:Float=qteF-qte2F
                if (qteRecuF+qte2F>qteF)
                {
                    Toast.makeText(mBuilder.context, "la quantité reçue ne doit pas dépasser :"+diff.toString(), Toast.LENGTH_SHORT).show()

                }
               else if(qteRecu!=""){
                val receptQte =Receptionner()
                    .execute(id.toString(), qteRecu, d, url, db, username, password)
                mBuilder.dismiss()
                if(qteD.getText()==qteRecu)
                    receptionArticle.setEnabled(false)
                val i:Intent=intent
                finish()
                overridePendingTransition(0,0)
                startActivity(i)
                overridePendingTransition(0,0)
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)}
                else{
                    Toast.makeText(mBuilder.context, "Veuillez remplire la quantité", Toast.LENGTH_SHORT).show()

                }
            }
            mDialogView.annuler.setOnClickListener {
                mBuilder.dismiss()
            }
            //pour désactiver le button receptionner



        }
        qteAdapter!!.addAll(mesQtes)
        listView!!.adapter = qteAdapter




    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_article,menu)
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


    class DetailArticle : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg id: Int?): List<Any>? {
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






                    val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        db, uid, password,
                        "purchase.order.line", "search_read",
                        Arrays.asList(
                            Arrays.asList(
                                Arrays.asList("id", "=", id)
                            )
                        ),
                        object : HashMap<Any, Any>() {
                            init {
                                put(
                                    "fields",
                                    Arrays.asList("product_id","product_qty","date_planned","product_uom")
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


    class DetailArticle2 : AsyncTask<String, Void, List<Any>?>() {

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
                                serverURL = URL(String.format("%s/xmlrpc/2/object", v[1]))
                            }
                        })
                    }
                }






                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[2], uid, v[4],
                    "stock.move", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "=", v[0]!!.toInt())
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("id","product_id","product_uom_qty","quantity_done","product_uom","date","state")
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

    class QuantiteRec : AsyncTask<String, Void, List<Any>?>() {

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
                                serverURL = URL(String.format("%s/xmlrpc/2/object", v[1]))
                            }
                        })
                    }
                }






                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    v[2], uid, v[4],
                    "stock.move.line", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("move_id", "=", v[0]!!.toInt())
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("id","qty_done","product_uom_id","date")
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

    class Receptionner : AsyncTask<String, Void,List<Any>?>(){

        var idA:Int=0
        var qte:String=""
        var date:String=""



        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): List<Any>? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", infos[3]))

                val uid: Int = client.execute(
                    common_config, "authenticate", Arrays.asList(
                        infos[4], infos[5], infos[6], Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(
                                    String.format(
                                        "%s/xmlrpc/2/object",
                                        infos[3]
                                    )
                                )
                            }
                        })
                    }
                }



                println("************************  datebbb = ${infos[1]}")




                idA=infos[0].toInt()
                qte=infos[1]
                date=infos[2]


                var q:Float= 0F

         /*       val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "stock.move.line", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("move_id", "=", idA)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("qty_done")
                            )
                        }
                    }
                )) as Array<Any>)
                println("**************************  champs chantier = $list")

                var jsonArray=JSONArray(list)
                for (i1 in 0..(list!!.size) - 1) {



                    var qte2 = jsonArray.getJSONObject(i1).getString("qty_done").toString().toFloat()
                   q=qte2
                    println("**************************  champs chantier = $q")
                }


                    q=q+qte.toFloat()

                println("**************************  q = $q")



var qteDone=q.toString()

               var id2=models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "stock.move.line", "write",
                    Arrays.asList(
                        Arrays.asList(idA),
                        object : HashMap<Any, Any>() { init {
                            put("qty_done", qteDone)
                            put("date",date)



                        }
                        }
                    )
                ))*/



                       val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                           infos[4], uid, infos[6],
                           "stock.move", "search_read",
                           Arrays.asList(
                               Arrays.asList(
                                   Arrays.asList("id", "=", idA)
                               )
                           ),
                           object : HashMap<Any, Any>() {
                               init {
                                   put(
                                       "fields",
                                       Arrays.asList("quantity_done")
                                   )
                               }
                           }
                       )) as Array<Any>)
                       println("**************************  champs chantier = $list")

                       var jsonArray=JSONArray(list)
                  if (list!=null){     for (i1 in 0..(list!!.size) - 1) {



                           var qte2 = jsonArray.getJSONObject(i1).getString("quantity_done").toString().toFloat()
                          q=qte2
                           println("**************************  champs chantier = $q")
                       }}


                           q=q+qte.toFloat()

                       println("**************************  q = $q")



       var qteDone=q.toString()

                      var id2=models.execute("execute_kw", Arrays.asList(
                           infos[4], uid, infos[6],
                           "stock.move", "write",
                           Arrays.asList(
                               Arrays.asList(idA),
                               object : HashMap<Any, Any>() { init {
                                   put("quantity_done", qteDone)
                                   put("date",date)



                               }
                               }
                           )
                       ))
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


