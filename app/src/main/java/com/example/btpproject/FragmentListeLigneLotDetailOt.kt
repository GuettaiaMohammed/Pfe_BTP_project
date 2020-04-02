package com.example.btpproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentListeLigneLotDetailOt : Fragment() {
    internal lateinit var view: View
    private var lignes: ArrayList<LigneLotOT>? = null
    private var listView: ListView? = null
    private var ligneLotAdapter: FragmentLigneLotAdapterDetailOt? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.activity_fragment_liste_ligne_lot_detail_ot, container, false)

        listView = view.findViewById(R.id.ListLigneLotDetailOT)
        ligneLotAdapter = FragmentLigneLotAdapterDetailOt(view.context,0)

        lignes = ArrayList()

        lignes!!.add(LigneLotOT("N°","Désignation","Unité","Quantité réalisé"))


        lignes!!.add(LigneLotOT("2.01.a","Déblais en tranchée et en puits :a) tranche comprise entre 0,00 et 1,50m","m3","490,00"))
        lignes!!.add(LigneLotOT("2.01.b","Déblais en tranchée et en puits :b) tranche comprise entre 1,50 et 3,00m","m3","490,00"))
        lignes!!.add(LigneLotOT("2.03","Transport des terres à la décharge publique, quelque soit la distance parcourue","m3","1000,00"))
        lignes!!.add(LigneLotOT("2.06.a","Béton armé pour ouvrage en infrastructure :b) Béton dosé à 350Kg/m3 pour a/poteaux et voiles","m3","35,00"))

        ligneLotAdapter!!.addAll(lignes)
        listView!!.adapter = ligneLotAdapter


        return view
    }
}
