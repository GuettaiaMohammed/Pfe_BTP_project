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
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


class DetailMaterielActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_materiel)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Matériel")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val nom=findViewById<TextView>(R.id.name)
       val date_D=findViewById<TextView>(R.id.dateD)
        val date_debut=findViewById<TextView>(R.id.date_debut)
        val date_fin=findViewById<TextView>(R.id.date_fin)
        val duree=findViewById<TextView>(R.id.duree)



val i=intent
        val id=i.getIntExtra("id",0)
       // Toast.makeText(this, id.toString() , Toast.LENGTH_SHORT).show()

        var conn = DetailMateriel().execute(id)
        var details  = conn.get()
        val jsonArray = JSONArray(details)

        //récupéré lles données de l'objet JSON
        for (i in 0..(details!!.size) - 1) {
            val dateD = jsonArray.getJSONObject(i).getString("date_debut").toString()
            val dateF = jsonArray.getJSONObject(i).getString("date_fin").toString()
            val dateCreation= jsonArray.getJSONObject(i).getString("create_date").toString()
            var typeObj =
                jsonArray.getJSONObject(i).getString("type_materiel_id").toString()
            var type = typeObj.split("\"")[1]
            var type2 = type.split("\"")[0]

            val date = LocalDate.parse(dateD, DateTimeFormatter.ISO_DATE)
            val date1 = LocalDate.parse(dateF, DateTimeFormatter.ISO_DATE)


             val CONST_DURATION_OF_DAY = 1000L * 60 * 60 * 24
            val  calendar1 =  GregorianCalendar()
		calendar1.set(Calendar.YEAR, date.year);
		calendar1.set(Calendar.MONTH, date.monthValue);
		calendar1.set(Calendar.DAY_OF_MONTH, date.dayOfMonth);
		val date2 = calendar1.getTime();
		//  2006-08-15
		val calendar2 = GregorianCalendar()
		calendar2.set(Calendar.YEAR, date1.year);
		calendar2.set(Calendar.MONTH, date1.monthValue);
		calendar2.set(Calendar.DAY_OF_MONTH, date1.dayOfMonth);
		val date3  = calendar2.getTime();
		// Différence
		val diff = Math.abs(date3.getTime() - date2.getTime());
		val numberOfDay =diff/CONST_DURATION_OF_DAY


            nom.setText(type2)
            date_D.setText(dateCreation)
            date_debut.setText(dateD)
            date_fin.setText(dateF)
            duree.setText(numberOfDay.toString()+" jours")



        }

        receptionMateriel.setOnClickListener {
                  //Inflate the dialog with custom view
                  val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_receptioner_materiel, null)
                  //AlertDialogBuilder
                  val mBuilder = AlertDialog.Builder(this)
                      .setView(mDialogView)
                  //.setTitle("Login Form")
             //show dialog
             mBuilder.show()
             mDialogView.validerNote.setOnClickListener{
                 if(mDialogView.note.text.toString()!="") {
                     Toast.makeText(this, "Votre note est envoyée !!", Toast.LENGTH_SHORT).show()
                 }
mBuilder.setCancelable(true)
             }


             receptionMateriel.setEnabled(false)
              }

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
                    "demande.appro_mat", "search_read",
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
                                    "type_materiel_id",
                                    "date_debut",
                                    "date_fin",
                                    "create_date"
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
}
