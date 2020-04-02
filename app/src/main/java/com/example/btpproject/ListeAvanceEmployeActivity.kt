package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_liste_avance_employe.*
import kotlinx.android.synthetic.main.activity_liste_materiels.*
import java.util.ArrayList

class ListeAvanceEmployeActivity : AppCompatActivity() {

    private var listAvance: MutableList<AvanceEmploye>? = null
    private var avanceAdapter: AvanceEmployeAdapter? = null
    private var listView: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_avance_employe)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setTitle("Avances Employé")
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

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
            //show dialog
            mBuilder.show()
        }
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
                val intent = Intent(this, MonChantier::class.java)
                // start your next activity
                startActivity(intent)
                return true
            }
            item!!.getItemId() == R.id.navigation_dashboard ->
            {
                val intent = Intent(this, MainActivity::class.java)
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
        }
        return super.onOptionsItemSelected(item)
    }
}
