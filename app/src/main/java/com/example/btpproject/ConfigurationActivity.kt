package com.example.btpproject

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class ConfigurationActivity : AppCompatActivity() {

    lateinit var mPreferences : SharedPreferences
    lateinit var  mEditor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuration)

        var url = findViewById<EditText>(R.id.url)
        var bdd = findViewById<EditText>(R.id.bdd)
        var username = findViewById<EditText>(R.id.username)
        var passBdd = findViewById<EditText>(R.id.passbdd)
        var valid = findViewById<Button>(R.id.valideConfigBtn)
        var error = findViewById<TextView>(R.id.erreurConfig)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mEditor = mPreferences.edit()

        url.setText(mPreferences.getString("url", "http://sogesi.hopto.org:7013"))
        bdd.setText(mPreferences.getString("bdd", "BTP_pfe"))
        username.setText(mPreferences.getString("username", "admin"))
        passBdd.setText(mPreferences.getString("passBdd", "pfe_chantier"))

        valid.setOnClickListener {
            var urlS = url.text.toString()
            var bddS = bdd.text.toString()
            var usernameS = username.text.toString()
            var passBddS = passBdd.text.toString()

            if (urlS == "" || bddS == "" || usernameS == "" || passBddS == "") {
                Toast.makeText(this, "Veuillez remplir toutes les cases ", Toast.LENGTH_SHORT).show()
            } else {
                if (Connexion().execute(urlS, bddS, usernameS, passBddS).get() == null
                    || Connexion().execute(urlS, bddS, usernameS, passBddS).get() == 0
                ) {
                    error.setText("veuillez verifier vos information")
                    error.setTextColor(Color.RED)
                } else {

                    error.setText("Connexion avec success")
                    error.setTextColor(Color.GREEN)

                    mEditor.putString("url", urlS)
                    mEditor.commit()
                    mEditor.putString("bdd", bddS)
                    mEditor.commit()
                    mEditor.putString("username", usernameS)
                    mEditor.commit()
                    mEditor.putString("passBdd", passBddS)
                    mEditor.commit()

                    val intent = Intent(this, LoginActivity::class.java)
                    // start your next activity
                    //intent.putExtra("idUser", idUser)
                    startActivity(intent)

                }
            }
        }
    }

    class Connexion: AsyncTask<String, Void, Int?>() {


        override fun doInBackground(vararg url: String?): Int?{
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

                return uid

            }catch (e: MalformedURLException) {
                return null
            }  catch (e: XmlRpcException) {
                return null
            }catch (e : Exception){
                return null
            }
            return null
        }
    }
}
