package com.example.btpproject

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.activity_ajouter_article.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_employe.view.*
import kotlinx.android.synthetic.main.activity_ajouter_materiel.view.*
import kotlinx.android.synthetic.main.activity_mon_chantier2.*
import java.util.ArrayList

class MonChantier : AppCompatActivity() {

    private var mesLots: ArrayList<Lot>? = null
    private var listView: ListView? = null
    private var lotAdapter: LotAdapter? = null

//les listes des spinner
    private val listArticles = arrayListOf<String>()
    private val listUnites = arrayListOf<String>()
    private val listMetiers = arrayListOf<String>()
    private val listMateriels = arrayListOf<String>()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mon_chantier2)

        val toolbar = findViewById<Toolbar>(R.id.toolbar2)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Mon chantier")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //

        //remplire les listes de spinner
        listArticles.addAll(listOf("","Article 1", "Article 2", "Article 3"))
        listUnites.addAll(listOf("","m3","m2","Kg"))
        listMetiers.addAll(listOf("","Architecte","Maçon"))
        listMateriels.addAll(listOf("","Gru","Mini Pele"))
        //
        //button click to show dialog ajout d'article
        ajoutArticle.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_article, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")
            //Spinner
            val spinnerA = mDialogView.findViewById <Spinner>(R.id.spinnerA)
            val spinnerU = mDialogView.findViewById <Spinner>(R.id.spinnerUniteDMesure)
            //Remplire Spinner
           val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listArticles)
            val  adapter1 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listUnites)
             adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerA.setAdapter(adapter)
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerU.setAdapter(adapter1)

            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,spinnerA.selectedItem.toString()+" "+" La quantité : "+mDialogView.qte.text+" "+spinnerU.selectedItem.toString() ,Toast.LENGTH_SHORT).show()


            }


            //show dialog
            val  mAlertDialog = mBuilder.show()

        }

        //button click to show dialog ajout de matériel
        ajoutEmploye.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")


            //Spinner
            val spinnerE = mDialogView.findViewById <Spinner>(R.id.spinnerE)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listMetiers)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.setAdapter(adapter)


            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,spinnerE.selectedItem.toString()+" "+" Le nombre : "+mDialogView.nbr.text ,Toast.LENGTH_SHORT).show()


            }





            //show dialog
            val  mAlertDialog = mBuilder.show()
        }

        //button click to show dialog
        ajoutMateriel.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_materiel, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")





            //Spinner
            val spinnerM = mDialogView.findViewById <Spinner>(R.id.spinnerM)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listMateriels)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerM.setAdapter(adapter)


            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,spinnerM.selectedItem.toString()+" "+" De : "+mDialogView.dateD.text +" Au "+mDialogView.dateF.text ,Toast.LENGTH_SHORT).show()


            }







            //show dialog
            val  mAlertDialog = mBuilder.show()
        }


        listView = findViewById(R.id.listeLot)
        lotAdapter = LotAdapter(applicationContext, 0)

        mesLots = ArrayList();


        (mesLots as ArrayList<Lot>).add(Lot("Numero ","Lot","Etat"))

        (mesLots as ArrayList<Lot>).add(Lot("1",  "TERRASSEMENT","En cours"))
        (mesLots as ArrayList<Lot>).add(Lot("2",  "TRAVAUX EN INFRASTRUCTURE","Terminé"))
        (mesLots as ArrayList<Lot>).add(Lot("3",  "TRAVAUX EN INFRASTRUCTURE","Brouillon"))

        lotAdapter!!.addAll(mesLots)
        listView!!.adapter = lotAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.navigation_menu,menu)
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
