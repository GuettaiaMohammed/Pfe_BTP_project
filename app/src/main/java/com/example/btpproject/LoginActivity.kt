package com.example.btpproject

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var utilisateur=findViewById<EditText>(R.id.utilisateur)
        var mdp=findViewById<EditText>(R.id.motPass)
        var erreur=findViewById<TextView>(R.id.erreur)

        loginImage.animate().scaleX(1.2F).scaleY(1.2F).setDuration(5000).start()
        var conn=Connexion().execute("")
        var list=conn.get()
        var json=JSONArray(list)


        loginBtn.setOnClickListener {
            var user:String=utilisateur.text.toString()

            var pass:String=mdp.text.toString()
            var idUser:Int=0
            if(list!=null){
                for(i in 0..(list.size-1)){
                    val name = json.getJSONObject(i).getString("name").toString()
                    val login=json.getJSONObject(i).getString("login").toString()
                    val password=json.getJSONObject(i).getString("password").toString()
                    idUser=json.getJSONObject(i).getString("id").toString().toInt()
                    if(user==""&&pass=="")
                    {
                        Toast.makeText(this, "Veuillez entrer votre nom d'utilisateur et mot de passe ", Toast.LENGTH_SHORT).show()

                    }else{
                    if(user!=name&&pass!=login){
                        //  erreur.setText("Nom d'utilisteur ou mot de passe incorrect")
                        //   erreur.setTextColor(Color.RED)
                        utilisateur.setText("")
                        mdp.setText("")
                    } else{


                        erreur.setText("Connecté")
                        erreur.setTextColor(Color.GREEN)
                        val intent = Intent(this, ListeChantierActivity::class.java)
                        // start your next activity
                        intent.putExtra("idUser", idUser)
                        startActivity(intent)
                    }}

                }}


        }
    }

    class Connexion: AsyncTask<String, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): List<Any>?{
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
