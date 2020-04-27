package com.example.btpproject

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
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
import kotlinx.android.synthetic.main.activity_ajouter_qte_realise.view.*
import kotlinx.android.synthetic.main.activity_detail_suivi.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import kotlinx.android.synthetic.main.activity_liste_employes_suivi.*
import kotlinx.android.synthetic.main.activity_receptioner_article.view.*
import kotlinx.android.synthetic.main.activity_receptioner_article.view.valider
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class DetailSuiviEmployeQteRealiseActivity : AppCompatActivity() {

    private var mesQtes: ArrayList<QuantiteRealise>? = null
    private var listView: ListView? = null
    private var qteAdapter: QteRealiseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_suivi)


        val name=findViewById<TextView>(R.id.name)

        val qtePrev=findViewById<TextView>(R.id.qteP)

        val qteR=findViewById<TextView>(R.id.qteR)

        val nbHprev=findViewById<TextView>(R.id.nbHp)

        val nbHptrav=findViewById<TextView>(R.id.nbHt)
        var unite:String=""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Detail suivi")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        val i=intent
        val id=i.getIntExtra("id",0)
        // Toast.makeText(this, id.toString() , Toast.LENGTH_SHORT).show()
        var conn = Connexion().execute(id)
        var details  = conn.get()
        var conn1 = ListDetails().execute(id)
        var Listdetails  = conn1.get()


        val jsonArray = JSONArray(details)


        for (i in 0..(details!!.size) - 1) {
            var Obj =
                jsonArray.getJSONObject(i).getString("employee_id").toString()
            var nom = Obj.split("\"")[1]
            var nom2 = nom.split("\"")[0]
            var Obj2 =
                jsonArray.getJSONObject(i).getString("unite").toString()
            var u = Obj2.split("\"")[1]
            var u1 = u.split("\"")[0]

            val qtePrevu = jsonArray.getJSONObject(i).getString("qte_prev").toString()
            val qteRealise = jsonArray.getJSONObject(i).getString("qte_realise").toString()
            val nbHprevu = jsonArray.getJSONObject(i).getString("nb_h_prevu").toString()
            val nbHtravail = jsonArray.getJSONObject(i).getString("nb_h_travail").toString()

            name.setText(nom2)
            qtePrev.setText(qtePrevu+" "+u1)
            qteR.setText(qteRealise+" "+u1)
            nbHprev.setText(nbHprevu)
            nbHptrav.setText(nbHtravail)
            unite=u1

        }


        val jsonArray1 = JSONArray(Listdetails)


        listView = findViewById(R.id.qteRealise)
        qteAdapter = QteRealiseAdapter(applicationContext, 0)

        mesQtes = ArrayList();
        (mesQtes as ArrayList<QuantiteRealise>).add(QuantiteRealise("Date",  "Qantité réalisé", "Nb H Travaillé", "Montant pris"))

        for (i in 0..(Listdetails!!.size) - 1) {
            var Obj =
                jsonArray1.getJSONObject(i).getString("currency_id").toString()
            var nom = Obj.split("\"")[1]
            var nom2 = nom.split("\"")[0]

            val date = jsonArray1.getJSONObject(i).getString("date").toString()
            val qteRealise = jsonArray1.getJSONObject(i).getString("qte_realise").toString()
            val montant = jsonArray1.getJSONObject(i).getString("montant_pris").toString()
            val nbHtravail = jsonArray1.getJSONObject(i).getString("nb_h_travail").toString()

            mesQtes!!.add(QuantiteRealise(date,qteRealise+" "+unite,nbHtravail,montant+" "+nom2))

        }






        ajouterQteRealiserBtn.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_qte_realise, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            mBuilder.show()
          mDialogView.valider.setOnClickListener{
              //pour ajouter la quantité receptionné et la date
              val c: SimpleDateFormat = SimpleDateFormat("dd/M/yyyy")
              var d=c.format((Date()))
              qteAdapter!!.add(QuantiteRealise(d,""+mDialogView.qteR.text.toString(),""+mDialogView.nbH.text.toString(),""+mDialogView.pu.text.toString()))


          }


        }
        qteAdapter!!.addAll(mesQtes)
        listView!!.adapter = qteAdapter



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_suivi,menu)
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




    class Connexion : AsyncTask<Int, Void, List<Any>?>() {
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
                    "reglement.employe", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id","=",2)
                            ,Arrays.asList("id","=",id)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("employee_id","ligne_lot_id","qte_prev","qte_realise","nb_h_prevu","nb_h_travail","unite")
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
    class ListDetails : AsyncTask<Int, Void, List<Any>?>() {
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
                    "reg.employe", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id","=",2)
                            ,Arrays.asList("reglement_employe_id","=",id)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("date","montant_pris","qte_prev","qte_realise","nb_h_travail","currency_id")
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

}