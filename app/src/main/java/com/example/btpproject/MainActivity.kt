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
import android.util.Log
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import java.net.MalformedURLException
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("BTP")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        chantier.setOnClickListener {
            val intent = Intent(this, MonChantier::class.java)
            // start your next activity
            startActivity(intent)
        }

        employes.setOnClickListener {
            val intent = Intent(this, ListeEmployeActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        article.setOnClickListener {
            val intent = Intent(this, ListeArticleActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        materiels.setOnClickListener {
            val intent = Intent(this, ListeMaterielsActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        avancesEmploye.setOnClickListener {
            val intent = Intent(this, ListeAvanceEmployeActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

        ordresDeTravail.setOnClickListener {
            val intent = Intent(this, ListeOrdreDeTravailActivity::class.java)
            // start your next activity
            startActivity(intent)
        }
        suivi.setOnClickListener {
            val intent = Intent(this, ListeEmployeSuiviActivity::class.java)
            // start your next activity
            startActivity(intent)
        }

       // val conn = Connexion().execute(url)

        notification.setOnClickListener {
            val myBuilder = AlertDialog.Builder(this)
            //DATA SOURCE
            val notifications = arrayOf<CharSequence>("Notification 1", "Notification 2", "Notification 3", "Notification 4", "Notification 5", "Notification 6", "Notification 7")
            //SET PROPERTIES USING METHOD CHAINING
            myBuilder.setTitle("Notifications").setItems(notifications) { dialogInterface, position -> Toast.makeText(this, notifications[position].toString(), Toast.LENGTH_SHORT).show() }
            //CREATE DIALOG
            var myDialog = myBuilder.create()
            //SHOW DIALOG
            myDialog.show()
        }
        cart_badge.setText("2")
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


  /*  class Connexion : AsyncTask<String, Void, List<Any>?>() {
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
                )
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

                //liste des chantier
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "mail.activity", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "!=", 0)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("activity_type_id","name","state")
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


    }*/

}



