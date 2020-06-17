package com.example.btpproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_welcom.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import android.widget.EditText as EditText1

/*
internal val urlll = "http://sogesi.hopto.org:7013/"
    internal val dbll = "BTP_pfe"
    internal val usernamell = "admin"
    internal val passwordll = "pfe_chantier"
 */

class LoginActivity : AppCompatActivity() {

    lateinit var mPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var utilisateur=findViewById<EditText1>(R.id.utilisateur)
        var mdp=findViewById<EditText1>(R.id.motPass)
        var erreur=findViewById<TextView>(R.id.erreur)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")


        loginImage.animate().scaleX(1.2F).scaleY(1.2F).setDuration(5000).start()
        var conn=Authentification().execute(url, db, username, password)
        var list=conn.get()
        var json=JSONArray(list)



        //
        //











        ///
        loginBtn.setOnClickListener {
            var user:String=utilisateur.text.toString()

            var pass:String=mdp.text.toString()
            var login:String=""
            var motPass:String=""
            var idUser:Int=0

            if(list!=null) {
                for (i in 0..(list.size - 1)) {

                    val util = json.getJSONObject(i).getString("login").toString()
                    val mpass = json.getJSONObject(i).getString("password").toString()
                    val id = json.getJSONObject(i).getString("id").toString().toInt()

                    if (user == util && (pass == mpass || pass == "")) {
                        login=util
                        motPass=mpass
                        idUser=id
                        println("************** login et mdp = $login , $motPass id= $idUser")


                    }
                }}


            if(login!=""&&idUser!=0){
                erreur.setText("Connecté")
                erreur.setTextColor(Color.GREEN)
                val intent = Intent(this, ListeChantierActivity::class.java)
                // start your next activity
                intent.putExtra("idUser", idUser)
                startActivity(intent)


            }else {
                erreur.setText("Nom d'utilisteur ou mot de passe incorrect")
                erreur.setTextColor(Color.RED)
                utilisateur.setText("")
                mdp.setText("")

            }}


        configBtn.setOnClickListener {
            val intent = Intent(this, ConfigurationActivity::class.java)
            // start your next activity
            //intent.putExtra("idUser", idUser)
            startActivity(intent)
        }
    }


    class Authentification: AsyncTask<String, Void, List<Any>?>() {


        override fun doInBackground(vararg url: String?): List<Any>?{
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", url[0]))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        url[1], url[2], url[3], Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", url[0]))
                            }
                        })
                    }
                }

                //liste des chantier
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    url[1], uid, url[3],
                    "res.users", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "!=", 0)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("name","id","login","password")
                            )
                        }
                    }
                )) as Array<Any>)

                println("**************************** list = $list")

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
