package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.ArrayAdapter

import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import android.widget.TextView
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"
var incr:Int=0

    private var mesArticles: ArrayList<Article>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("BTP")
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)

        val i = intent
        val id_chantier = i.getIntExtra("idChantier",0)


        chantier.setOnClickListener {
            val intent = Intent(this, MonChantier::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }

        employes.setOnClickListener {
            val intent = Intent(this, ListeEmployeActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }

        article.setOnClickListener {
            val intent = Intent(this, ListeArticleActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }

        materiels.setOnClickListener {
            val intent = Intent(this, ListeMaterielsActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }

        avancesEmploye.setOnClickListener {
            val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }

        ordresDeTravail.setOnClickListener {
            val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }
        suivi.setOnClickListener {
            val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }
        dash.setOnClickListener {
            val intent = Intent(this, TableauDeBoardActivity::class.java)
            intent.putExtra("idChantier", id_chantier)
            // start your next activity
            startActivity(intent)
        }



        //Notification
        val Listnotifications = ArrayList<String>()

        val conn = ListeMaterielsActivity.Connexion().execute(id_chantier)
        val list = conn.get()
        val jsonArray = JSONArray(list)



       //récupérer les matériels
        val c: SimpleDateFormat = SimpleDateFormat("yyyy-M-dd")
        var d=c.format((Date()))
        val calendar=Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR,+2)
        val dAvant2= calendar.time
        for (i in 0..(list!!.size) - 1) {
            val dateD = jsonArray.getJSONObject(i).getString("date_debut").toString()

            var typeObj =
                jsonArray.getJSONObject(i).getString("materiel_id").toString()
            var type = typeObj.split("\"")[1]
            var type2 = type.split("\"")[0]
            var etat=jsonArray.getJSONObject(i).getString("state").toString()
            var sdf:SimpleDateFormat= SimpleDateFormat("yyyy-M-dd")
            var d1:Date=sdf.parse(d)
            var d2:Date=sdf.parse(dateD)
            var diff1:Long=Math.abs(d2.getTime()-d1.getTime())
            var diff=TimeUnit.DAYS.convert(diff1,TimeUnit.MILLISECONDS)
            println("********* diff = $diff")

           if(diff.toInt()==2 && etat=="attente" ){

                Listnotifications.add("Le matériel : "+type2+" sera disponible après 2 jours")
             incr++

            }

        }
        //récupérer les articles

        val con= ListeArticleActivity.ListeArticleD().execute(id_chantier)
         mesArticles = con.get() as ArrayList<Article>

        for (i in 0..(mesArticles!!.size-1))
        {
            if (mesArticles!!.get(i).etat=="assigned")
            {
               // notifications.set(incr,"L'Article : "+ mesArticles!!.get(i).nom+" est disponible ")

Listnotifications.add("L'Article : "+ mesArticles!!.get(i).nom+" est disponible ")
                incr++
            }

        }


         // var notifications: Array<String>? = arrayOf(" ")
        var notifications=Listnotifications.toTypedArray()

        cart_badge.setText(incr.toString())
        notification.setOnClickListener {
            val myBuilder = AlertDialog.Builder(this)
            //DATA SOURCE
            //SET PROPERTIES USING METHOD CHAINING
            myBuilder.setTitle("Notifications")
                myBuilder.setItems(notifications) { dialogInterface, position -> Toast.makeText(this, notifications!![position].toString(), Toast.LENGTH_SHORT).show() }
            //CREATE DIALOG
            var myDialog = myBuilder.create()
            cart_badge.setText("0")
            //SHOW DIALOG
            myDialog.show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_accueil, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when {
            item!!.getItemId() == R.id.navigation_disconnect -> {
                val intent = Intent(this, LoginActivity::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
          /*  item!!.getItemId() == R.id.navigation_notifications -> {
                showAlert()
                return true
            }*/
        }
        return super.onOptionsItemSelected(item)
    }



}



