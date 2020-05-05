package com.example.btpproject

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*

import kotlinx.android.synthetic.main.activity_liste_articles.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class ListeArticleActivity : AppCompatActivity() {

    //
    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"
    //

    private val listArticles = arrayListOf<String>()
    private val listUnites = arrayListOf<String>()
    //
    private var mesArticles: ArrayList<Article>? = null
    private var listView: ListView? = null
    private var   articleAdapter: ArticleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_articles)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes article")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        listArticles.add("")
        listUnites.add("")




        val conn=Connexion().execute(url)

        val conn4= MonChantier.Article().execute(url)
        val conn5= MonChantier.Unite().execute(url)
        val listArticle=conn4.get()
        //liste des unités
        val listU=conn5.get()

        val jsonArray5 = JSONArray(listArticle)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listArticle!!.size) - 1) {

            val name = jsonArray5.getJSONObject(i).getString("name").toString()


            listArticles.add(name)

        }
        val jsonArray6 = JSONArray(listU)

        //récupéré lles données de l'objet JSON
        for (i in 0..(listU!!.size) - 1) {

            val name = jsonArray6.getJSONObject(i).getString("name").toString()

            listUnites.add(name)

        }

        listView =findViewById(R.id.articleListe)
        articleAdapter = ArticleAdapter(applicationContext, 0)
        mesArticles = conn.get() as ArrayList<Article>


        articleAdapter!!.addAll(mesArticles)
        listView!!.adapter = articleAdapter


        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (_i in 0..mesArticles!!.size) {
                if (position == _i) {
                  val intent = Intent(this, DetailArticleQuantiteActivity::class.java)
                    val article = mesArticles!!.get(_i)
                   val idA=article.id
                    intent.putExtra("id",idA)
                    // start your next activity
                   startActivity(intent)
                }
            }

        })

        //button click to show dialog
        fabArticle.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_article, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")

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

            //show dialog
            mBuilder.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_article,menu)
        var menuItem: MenuItem = menu!!.findItem(R.id.app_bar_search_article)
        var searchView: SearchView = menuItem.actionView as SearchView
        searchView.queryHint = "Rechercher ici"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText!!.isNotEmpty()){
                    articleAdapter!!.clear()
                    val search = newText.toLowerCase()
                    mesArticles!!.forEach {
                        if((it.nom!!.toLowerCase().contains(newText))
                            ||(it.qteDemande!!.toLowerCase().contains(newText))
                            ||(it.date!!.toLowerCase().contains(newText))){
                            articleAdapter!!.add(it)
                        }
                    }
                }else{
                    articleAdapter!!.clear()
                    articleAdapter!!.addAll(mesArticles)
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
                val list1 = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "purchase.order", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("origin", "=", "piscine_semi_olympique_ref")
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
                println("**************************  champs chantier = $list1")
                //liste des chantier
                val jsonArray = JSONArray(list1)
                var listeArticleDem = ArrayList<Article>()

                for(i in 0..(list1.size)-1) {

                    var id = jsonArray.getJSONObject(i).getString("id").toInt()


                    val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                        db, uid, password,
                        "purchase.order.line", "search_read",
                        Arrays.asList(
                            Arrays.asList(
                                Arrays.asList("order_id", "=", id)
                            )
                        ),
                        object : HashMap<Any, Any>() {
                            init {
                                put(
                                    "fields",
                                    Arrays.asList("id","product_id","product_qty","date_planned","product_uom")
                                )
                            }
                        }
                    )) as Array<Any>)
                    println("**************************  champs chantier = $list")
                    val jsonArray2 = JSONArray(list)

                    for(i1 in 0..(list.size)-1) {
                        var date= jsonArray2.getJSONObject(i1).getString("date_planned").toString()
                        var qte= jsonArray2.getJSONObject(i1).getString("product_qty").toString()
                        var id= jsonArray2.getJSONObject(i1).getString("id").toInt()

                        var typeObj =
                        jsonArray2.getJSONObject(i1).getString("product_id").toString()
                    var type = typeObj.split("\"")[1]
                    var type2 = type.split("\"")[0]
                        var typeObj2 =
                            jsonArray2.getJSONObject(i1).getString("product_uom").toString()
                        var u = typeObj2.split("\"")[1]
                        var u2 = u.split("\"")[0]

                   // println("**************************  article = $type2")

                        listeArticleDem!!.add(Article(id,type2,qte+u2,date))
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
}
