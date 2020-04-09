package com.example.btpproject

import android.app.DatePickerDialog
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
import kotlinx.android.synthetic.main.activity_cellule_materiel.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import java.util.*


class ListeMaterielsActivity : AppCompatActivity() {

    private var mesMateriels: ArrayList<Materiel>? = null
    private var listView: ListView? = null
    private var materielAdapter: MaterielAdapter? = null

    //liste de spinner
    private val listMateriels = arrayListOf<String>()

    var mDatepickerD: DatePickerDialog? = null
    var mDatepickerF: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_materiels)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes matériel")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        listMateriels.addAll(listOf("","Gru","Mini Pele"))

        listView = findViewById(R.id.materielsListe)
        materielAdapter = MaterielAdapter(applicationContext, 0)

        mesMateriels = ArrayList();

        (mesMateriels as ArrayList<Materiel>).add(Materiel("Grue", "Interne", "21/03/2020"))
        (mesMateriels as ArrayList<Materiel>).add(Materiel("JCB/Mini-pelles", "Interne", "17/03/2020"))
        (mesMateriels as ArrayList<Materiel>).add(Materiel("JCB/Chargeuse-pelleteuse 3CX ECO", "Interne", "11/02/2020"))
        (mesMateriels as ArrayList<Materiel>).add(Materiel("BELL/tombereau articulé B25E", "Interne", "01/01/2020"))

        materielAdapter!!.addAll(mesMateriels)
        listView!!.adapter = materielAdapter

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..mesMateriels!!.size) {
                if (position == i) {
                    val intent = Intent(this, DetailMaterielActivity::class.java)
                    // start your next activity
                    startActivity(intent)
                }
            }

        })


        //button click to show dialog
        fabMateriel.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_materiel, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")

            //date Début Picker Btn
            val dateDBtn = mDialogView.findViewById<Button>(R.id.dateDMatBtn)
            // affichage de calendrier lors de la clique
            dateDBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepickerD = DatePickerDialog(
                    this,
                        R.style.DialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        //affichage de la date selectionné
                        dateDBtn.setText(
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        )
                        dateDBtn.textSize = 12F
                    }, year, month, day
                )
                mDatepickerD!!.show()
            }

            //date fin Picker Btn
            val dateFBtn = mDialogView.findViewById<Button>(R.id.dateFMatBtn)
            // affichage de calendrier lors de la clique
            dateFBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepickerF = DatePickerDialog(
                    this,
                        R.style.DialogTheme,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        //affichage de la date selectionné
                        dateFBtn.setText(
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        )
                        dateFBtn.textSize = 12F
                    }, year, month, day
                )
                mDatepickerF!!.show()
            }




            //Spinner
            val spinnerM = mDialogView.findViewById <Spinner>(R.id.spinnerM)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listMateriels)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerM.setAdapter(adapter)


            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,spinnerM.selectedItem.toString() , Toast.LENGTH_SHORT).show()
            }



            //show dialog
            mBuilder.show()
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
