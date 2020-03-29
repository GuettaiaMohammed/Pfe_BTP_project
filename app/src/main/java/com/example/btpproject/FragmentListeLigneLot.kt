package com.example.btpproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentListeLigneLot : Fragment() {
    internal lateinit var view: View
    private var lignes: ArrayList<LigneLotOT>? = null
    private var listView: ListView? = null
    private var ligneLotAdapter: FragmentLigneLotAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.activity_fragment_liste_ligne_lot, container, false)

        listView = view.findViewById(R.id.ListLigneLotDetailOT)
        ligneLotAdapter = FragmentLigneLotAdapter(view.context,0)

        lignes = ArrayList()

        lignes!!.add(LigneLotOT("N°","Désignation","Unité","Quantité réalisé"))


        lignes!!.add(LigneLotOT("2.0.1a","Déblais en tranchée et en puits :a) tranche comprise entre 0,00 et 1,50m","m²","3050,20"))
        lignes!!.add(LigneLotOT("2.0.1a","Déblais en tranchée et en puits :a) tranche comprise entre 0,00 et 1,50m","m²","3050,20"))
        lignes!!.add(LigneLotOT("2.0.1a","Déblais en tranchée et en puits :a) tranche comprise entre 0,00 et 1,50m","m²","3050,20"))
        lignes!!.add(LigneLotOT("2.0.1a","Déblais en tranchée et en puits :a) tranche comprise entre 0,00 et 1,50m","m²","3050,20"))

        ligneLotAdapter!!.addAll(lignes)
        listView!!.adapter = ligneLotAdapter


        return view
    }
}
