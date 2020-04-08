package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_qte_realise.view.*
import kotlinx.android.synthetic.main.activity_detail_suivi.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import kotlinx.android.synthetic.main.activity_liste_employes_suivi.*
import kotlinx.android.synthetic.main.activity_receptioner_article.view.*
import kotlinx.android.synthetic.main.activity_receptioner_article.view.valider
import java.text.SimpleDateFormat
import java.util.*


class DetailSuiviEmployeQteRealiseActivity : AppCompatActivity() {

    private var mesQtes: ArrayList<QuantiteRealise>? = null
    private var listView: ListView? = null
    private var qteAdapter: QteRealiseAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_suivi)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Detail suivi")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        listView = findViewById(R.id.qteRealise)
        qteAdapter = QteRealiseAdapter(applicationContext, 0)

        mesQtes = ArrayList();


        (mesQtes as ArrayList<QuantiteRealise>).add(QuantiteRealise("Date",  "Qantité réalisé", "Nb H Travaillé", "Montant pris"))

        (mesQtes as ArrayList<QuantiteRealise>).add(QuantiteRealise("13/03/2020", "50", "20:00", "5000.00" ))




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

}