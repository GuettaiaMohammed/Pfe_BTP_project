package com.example.btpproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListeChantierActivity : AppCompatActivity() {

    private var listeCh: ArrayList<Chantier>? = null
    private var recyclerView: RecyclerView? = null
    private var chantierAdapter: ChantierAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_liste_chantier)

        recyclerView = findViewById(R.id.listeChantier)

        listeCh = ArrayList()
        listeCh!!.add(Chantier("PISCINE SEMI OLYMPIQUE REGHAIA", 90, "En coure"))
        listeCh!!.add(Chantier("MODIFCATION DE LYCEE BOUAZZA MILOUD", 100, "Terminé"))

        chantierAdapter = ChantierAdapter(this.applicationContext, listeCh!!)
        recyclerView!!.adapter = chantierAdapter
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))


    }
}
