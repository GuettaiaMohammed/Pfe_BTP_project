package com.example.btpproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_detail_article.*
import kotlinx.android.synthetic.main.activity_detail_materiel.*
import kotlinx.android.synthetic.main.activity_receptioner_materiel.view.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import javax.xml.datatype.DatatypeConstants.DAYS

import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.view.*
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Arrays.asList
import java.util.concurrent.TimeUnit


class DetailMaterielActivity : AppCompatActivity() {


    lateinit var intt: Intent
    var id_chantier:Int = 0
    var id:Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_materiel)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Matériel")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        intt = intent
        id = intt.getIntExtra("id",0)
        id_chantier = intt.getIntExtra("idChantier",0)

        val nom=findViewById<TextView>(R.id.name)
        val date_D=findViewById<TextView>(R.id.dateD)
        val date_debut=findViewById<TextView>(R.id.date_debut)
        val date_fin=findViewById<TextView>(R.id.date_fin)
        val duree=findViewById<TextView>(R.id.duree)
        val detailM=findViewById<TextView>(R.id.detail)
        var statu:String=""



       // Toast.makeText(this, id.toString() , Toast.LENGTH_SHORT).show()

        var conn = DetailMateriel().execute(id)
        var details  = conn.get()
        val jsonArray = JSONArray(details)

        //récupéré lles données de l'objet JSON
        for (i in 0..(details!!.size) - 1) {
            val dateD = jsonArray.getJSONObject(i).getString("date_debut").toString()
            val dateF = jsonArray.getJSONObject(i).getString("date_fin").toString()
            val dure= jsonArray.getJSONObject(i).getString("duree_mat").toString()
            val comnt= jsonArray.getJSONObject(i).getString("commentaire").toString()
            var typeObj =
                jsonArray.getJSONObject(i).getString("materiel_id").toString()
            var type = typeObj.split("\"")[1]
            var type2 = type.split("\"")[0]
            var detail=jsonArray.getJSONObject(i).getString("state").toString()



            nom.setText(type2)

            if (comnt=="false"){
            date_D.setText(" ")}
            else{
                date_D.setText(comnt)
            }
            date_debut.setText(dateD)
            date_fin.setText(dateF)
            duree.setText(dure+" jours")
            //detailM.setText(detail)
statu=detail


        }
        if (statu=="en_cour"){
            detailM.setText("Utilisé")
            receptionMateriel.setText("Libérer")
            receptionMateriel.setOnClickListener {
                val recp =
                    Receptionner().execute(id.toString(), "", "libre")
                detailM.setText("Libéré")
               // receptionMateriel.setText("")
                //receptionMateriel.setBackgroundColor(Color.WHITE)
                receptionMateriel.setVisibility(View.INVISIBLE)
receptionMateriel.setEnabled(false)
            }

        }
        if (statu=="libre"){
            detailM.setText("Libéré")
            //receptionMateriel.setText("")
            //receptionMateriel.setBackgroundColor(Color.WHITE)
            receptionMateriel.setEnabled(false)
            receptionMateriel.setVisibility(View.INVISIBLE)

        }
         if (statu=="attente"){
             detailM.setText("Attente de réception")
        receptionMateriel.setOnClickListener {
            var cmnt:String=""
                  //Inflate the dialog with custom view
                  val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_receptioner_materiel, null)
                  //AlertDialogBuilder
                  val mBuilder = AlertDialog.Builder(this).create()
                      mBuilder.setView(mDialogView)
                  //.setTitle("Login Form")
             //show dialog
             mBuilder.show()
             mDialogView.validerNote.setOnClickListener{
                 cmnt=mDialogView.note.text.toString()
                 if(mDialogView.note.text.toString()!="") {
                     Toast.makeText(this, "Votre note est envoyée !!", Toast.LENGTH_SHORT).show()

                     val recp =
                        Receptionner().execute(id.toString(), cmnt, "en_cour")
                     val i:Intent=intent
                     finish()
                     overridePendingTransition(0,0)
                     startActivity(i)
                     overridePendingTransition(0,0)
                     i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                     mBuilder.dismiss()


                 }
                 mDialogView.annulerNote.setOnClickListener{

                         val recp =
                             Receptionner().execute(id.toString(), "", "en_cour")
                         mBuilder.dismiss()

                     }


             }

            detailM.setText("Utilisé")
             receptionMateriel.setText("Libérer")
              }}

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_materiel,menu)
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



    class DetailMateriel : AsyncTask<Int, Void, List<Any>?>() {
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



                //liste des demandes matériels
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "chantier.materiel", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("chantier_id", "=", 2),
                            Arrays.asList("id", "=", id)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList(
                                    "materiel_id",
                                    "date_debut",
                                    "date_fin",
                                    "state",
                                    "duree_mat",
                                    "commentaire"
                                )
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
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        var idE:Int=0
        var state:String=""

        var comment:String=""

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



                println("************************  datebbb = ${infos[1]}")




                idE=infos[0].toInt()
                comment=infos[1]
                state=infos[2]








/*                var id1: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "demande.avance", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("chantier_id", idCh)
                               put("employee_id", idE)
                                put("date", dateD)
                                put("mantant_demande",montant)
                                put("mantant_valide","0")


                            }
                        })
                    )
                )as Int
                println("************************  liste des données = $id1")*/
                var id2=models.execute("execute_kw", asList(
    db, uid, password,
    "chantier.materiel", "write",
    asList(
        asList(idE),
        object : HashMap<Any, Any>()
        { init


            { put("commentaire", comment)
                put("state", state)


            }}
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
