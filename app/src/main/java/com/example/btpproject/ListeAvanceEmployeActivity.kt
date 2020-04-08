package com.example.btpproject

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_ajouter_article.view.*
import kotlinx.android.synthetic.main.activity_ajouter_article.view.button
import kotlinx.android.synthetic.main.activity_ajouter_avance_employe.view.*
import kotlinx.android.synthetic.main.activity_ajouter_employe.view.*
import kotlinx.android.synthetic.main.activity_liste_avance_employe.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import java.util.*

class ListeAvanceEmployeActivity : AppCompatActivity() {

    private var listAvance: MutableList<AvanceEmploye>? = null
    private var avanceAdapter: AvanceEmployeAdapter? = null
    private var listView: ListView? = null

    //liste spinner
    private val listEmployes = arrayListOf<String>()


    var mDatepicker: DatePickerDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_avance_employe)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Demandes avance")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        // replire la liste de spinner
        listEmployes.addAll(listOf("","Architecte","Maçon"))

        listAvance = ArrayList<AvanceEmploye>()
        avanceAdapter = AvanceEmployeAdapter(applicationContext, 0)
        listView = findViewById(R.id.AvanceEmpListe)

        listAvance!!.add(AvanceEmploye("Guettaia Mohammed", "20000.00 DA", "10/01/2020"))
        listAvance!!.add(AvanceEmploye("Benabed Oussama", "5000.00 DA", "12/02/2020"))
        listAvance!!.add(AvanceEmploye("Bensaber Ikram", "15000.00 DA", "21/03/2020"))
        listAvance!!.add(AvanceEmploye("Guettaia Houcine", "10000.00 DA", "21/03/2020"))
        listAvance!!.add(AvanceEmploye("Fakir Abdelkrim", "20000.00 DA", "30/03/2020"))


        listView!!.adapter = avanceAdapter
        avanceAdapter!!.addAll(listAvance)

        var i:Int = 0
        listView!!.setOnItemClickListener(AdapterView.OnItemClickListener { adapterView, view, position, l ->
            for (i in 0..listAvance!!.size) {
                if (position == i) {
                    Toast.makeText(this, "clicked", Toast.LENGTH_LONG).show()
                }
            }

        })


        fabAvance.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.activity_ajouter_avance_employe, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(this)
                .setView(mDialogView)
            //.setTitle("Login Form")



            //Spinner
            val spinnerE = mDialogView.findViewById <Spinner>(R.id.spinnerEmploye)

            //Remplire Spinner
            val  adapter : ArrayAdapter<String> = ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,listEmployes)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerE.setAdapter(adapter)


            //button valider
            mDialogView.button.setOnClickListener{
                Toast.makeText(this,spinnerE.selectedItem.toString()+" "+" Le montant: "+mDialogView.montant.text ,Toast.LENGTH_SHORT).show()
            }

            //date Début Picker Btn
            val dateBtn = mDialogView.findViewById<Button>(R.id.dateDAvBtn)
            // affichage de calendrier lors de la clique
            dateBtn!!.setOnClickListener {
                val cldr = Calendar.getInstance()
                val day = cldr.get(Calendar.DAY_OF_MONTH)
                val month = cldr.get(Calendar.MONTH)
                val year = cldr.get(Calendar.YEAR)
                // date picker dialog
                mDatepicker = DatePickerDialog(
                    this,
                    DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                        //affichage de la date selectionné
                        dateBtn.setText(
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                        )
                        dateBtn.textSize = 12F
                    }, year, month, day
                )
                mDatepicker!!.show()
            }

            //show dialog
            mBuilder.show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_avance,menu)
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
