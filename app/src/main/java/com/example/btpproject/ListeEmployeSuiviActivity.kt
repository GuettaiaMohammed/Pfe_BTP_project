package com.example.btpproject


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_employe_suivi.view.*

import kotlinx.android.synthetic.main.activity_liste_employes_suivi.*

import java.util.ArrayList


class ListeEmployeSuiviActivity : AppCompatActivity() {

    private var mesEmployes: ArrayList<Employe>? = null
    private var listView: ListView? = null
    private var employeAdapter: EmployeSuiviAdapter? = null

    private var  check: CheckBox? = null

    //les listes des spinner
    private val listEmployes = arrayListOf<String>()
    private val listUnites = arrayListOf<String>()
    private val listTypes = arrayListOf<String>()
    private val listLots = arrayListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_employes_suivi)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Suivi Employés")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        //remplire les listes de spinner
        listEmployes.addAll(listOf("","Employé 1", "Employé 2", "Employé 3"))
        listUnites.addAll(listOf("","m3","m2"))
        listTypes.addAll(listOf("","type1","type2"))
        listLots.addAll(listOf("","Ligne1","Ligne2"))

        listView = findViewById(R.id.empl)
        employeAdapter = EmployeSuiviAdapter(applicationContext, 0)

        mesEmployes= ArrayList();

        (mesEmployes as ArrayList<Employe>).add(Employe("Employé1", "Transport à la décharge publique de l’EPIC ASROUT"))

        employeAdapter!!.addAll(mesEmployes)
        listView!!.adapter = employeAdapter

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..mesEmployes!!.size) {
                if (position == i) {
                    val intent = Intent(this, DetailSuiviEmployeQteRealiseActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                }
            }

        })

        //button click to show dialog
        fabEmpl.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_employe_suivi, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")
            //show dialog
            mBuilder.show()


            //Spinner
            val spinnerE = mDialogView.findViewById <Spinner>(R.id.spinnerEmploye)
            val spinnerU = mDialogView.findViewById <Spinner>(R.id.spinnerUnite)
            val spinnerT = mDialogView.findViewById <Spinner>(R.id.spinnerTypeInt)
            val spinnerL = mDialogView.findViewById <Spinner>(R.id.spinnerLigneLot)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listEmployes)
            val  adapter1 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listUnites)
            val  adapter2 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listTypes)
            val  adapter3 : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listLots)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.setAdapter(adapter)
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerU.setAdapter(adapter1)

            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerT.setAdapter(adapter2)
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerL.setAdapter(adapter3)


            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,"ajout de : "+mDialogView.qteP.text.toString()+" , "+ mDialogView.nbH.text.toString()+" , "+mDialogView.pu.text.toString(), Toast.LENGTH_SHORT).show()


            }




        }
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
