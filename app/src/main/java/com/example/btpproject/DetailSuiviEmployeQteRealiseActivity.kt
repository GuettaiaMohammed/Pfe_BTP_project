package com.example.btpproject

import android.annotation.SuppressLint
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

    lateinit var i: Intent
    var id_chantier:Int = 0
    var id:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_suivi)

        i = intent
        id = i.getIntExtra("id",0)
        id_chantier = i.getIntExtra("idChantier",0)

        val name=findViewById<TextView>(R.id.name)

        val qtePrev=findViewById<TextView>(R.id.qteP)

        val qteR=findViewById<TextView>(R.id.qteR)

        val nbHprev=findViewById<TextView>(R.id.nbHp)

        val nbHptrav=findViewById<TextView>(R.id.nbHt)
        val unite1=findViewById<TextView>(R.id.unit1)
        val unite2=findViewById<TextView>(R.id.unit2)
        var unite:String=""

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Detail suivi")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        println(" ************** id = $id")
        // Toast.makeText(this, id.toString() , Toast.LENGTH_SHORT).show()
        var conn = Connexion().execute(id)
        var details  = conn.get()
        var conn1 = ListDetails().execute(id)
        var Listdetails  = conn1.get()


        val jsonArray = JSONArray(details)

var idE:String=""
        for (i in 0..(details!!.size) - 1) {
            var Obj =
                jsonArray.getJSONObject(i).getString("employee_id").toString()
            var nom = Obj.split("\"")[1]
            var nom2 = nom.split("\"")[0]
            idE=Obj.get(1).toString()
            var Obj2 =
                jsonArray.getJSONObject(i).getString("unite").toString()
            var u = Obj2.split("\"")[1]
            var u1 = u.split("\"")[0]

            val qtePrevu = jsonArray.getJSONObject(i).getString("qte_prev").toString()
            val qteRealise = jsonArray.getJSONObject(i).getString("qte_realise").toString()
            val nbHprevu = jsonArray.getJSONObject(i).getString("nb_h_prevu").toString()
            val nbHtravail = jsonArray.getJSONObject(i).getString("nb_h_travail").toString()

            name.setText(nom2)
            qtePrev.setText(qtePrevu)
            qteR.setText(qteRealise)
            nbHprev.setText(nbHprevu)
            nbHptrav.setText(nbHtravail)
            unite1.setText(u1)
            unite2.setText(u1)
            unite=u1

        }


        val jsonArray1 = JSONArray(Listdetails)


        listView = findViewById(R.id.qteRealise)
        qteAdapter = QteRealiseAdapter(applicationContext, 0)

        mesQtes = ArrayList();
        (mesQtes as ArrayList<QuantiteRealise>).add(QuantiteRealise("Date",  "Qantitée réalisée", "Nb H Travaillé"))

        for (i in 0..(Listdetails!!.size) - 1) {
            var Obj =
                jsonArray1.getJSONObject(i).getString("currency_id").toString()
            var nom = Obj.split("\"")[1]
            var nom2 = nom.split("\"")[0]

            val date = jsonArray1.getJSONObject(i).getString("date").toString()
            val qteRealise = jsonArray1.getJSONObject(i).getString("qte_realise").toString()
            val montant = jsonArray1.getJSONObject(i).getString("montant_pris").toString()
            val nbHtravail = jsonArray1.getJSONObject(i).getString("nb_h_travail").toString()

            mesQtes!!.add(QuantiteRealise(date,qteRealise+" "+unite,nbHtravail))

        }






        ajouterQteRealiserBtn.setOnClickListener {
            var qteRealise:String=""
            var nbhT:String=""

            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_qte_realise, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this).create()
                mBuilder.setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            mBuilder.show()
          mDialogView.valider.setOnClickListener{
              //pour ajouter la quantité receptionné et la date
              val c: SimpleDateFormat = SimpleDateFormat("dd/M/yyyy")
              var d=c.format((Date()))
              qteRealise=mDialogView.qteR.text.toString()
              nbhT=mDialogView.nbH.text.toString()


             if (nbhT!="" && qteRealise!="" )
              {
                  val ajouterS = AjouterSuivi().execute(id.toString(),qteRealise,nbhT,d,idE)
                  qteAdapter!!.add(QuantiteRealise(d,""+mDialogView.qteR.text.toString()+" "+unite,""+mDialogView.nbH.text.toString()))
//var q:Int=qteR.text.toString().toInt()+qteRealise.toInt()
  //           var n:Int=nbHptrav.text.toString().toInt()+nbhT.toInt()
             // qteR.setText(q.toString())
               //   nbHptrav.setText(n.toString())

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

}class AjouterSuivi : AsyncTask<String, Void,List<Any>?>(){
    val db = "BTP_pfe"
    val username = "admin"
    val password = "pfe_chantier"

    var id:Int=0

    var dateD:String=""
    var qteT:String=""
    var nbH:String=""
    var idE:Int=0


    @SuppressLint("NewApi")
    override fun doInBackground(vararg infos:String): List<Any>? {
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





            id = infos[0].toInt()
            qteT=infos[1]
            nbH=infos[2]
            dateD = infos[3]
            idE=infos[4].toInt()





           var id1: Int = models.execute(
                "execute_kw", Arrays.asList(
                    db, uid, password,
                    "reg.employe", "create",

                    Arrays.asList(object : HashMap<Any, Any>() {
                        init {

                            put("reglement_employe_id", id)

                            put("employee_id",idE)
                            put("chantier_id",2)

                            put("date",dateD)
                            put("qte_realise", qteT)
                            put("nb_h_travail", nbH)
                            put("employee_id", idE)




                        }
                    })
                )
            )as Int
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
