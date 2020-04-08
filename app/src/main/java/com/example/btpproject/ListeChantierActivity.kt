package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListeChantierActivity : AppCompatActivity(), ChantierAdapter.OnNoteListener {


    private var listeCh: ArrayList<Chantier>? = null
    private var recyclerView: RecyclerView? = null
    private var chantierAdapter: ChantierAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_chantier)

        recyclerView = findViewById(R.id.listeChantier)

        listeCh = ArrayList()
        listeCh!!.add(Chantier("PISCINE SEMI OLYMPIQUE REGHAIA", 90, "En cours"))
        listeCh!!.add(Chantier("MODIFCATION DE LYCEE BOUAZZA MILOUD", 100, "Terminé"))

        chantierAdapter = ChantierAdapter(this.applicationContext, listeCh!!, this)
        recyclerView!!.adapter = chantierAdapter
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))


    }

    override fun onNoteClick(position: Int) {
        val intent = Intent(this, MainActivity::class.java)
        // start your next activity
        startActivity(intent)
    }
}
