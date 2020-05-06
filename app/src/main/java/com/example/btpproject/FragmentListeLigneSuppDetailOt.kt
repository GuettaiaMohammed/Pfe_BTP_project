package com.example.btpproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentListeLigneSuppDetailOt: Fragment() {
    internal lateinit var view: View
    private var ligneSupps: ArrayList<LigneSupplementaireOT>? = null
    private var listView: ListView? = null
    private var ligneSuppAdapter: FragmentLigneSuppAdapterDetailOt? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(
            R.layout.activity_fragment_liste_ligne_supp_detail_ot,
            container,
            false
        )

        listView = view.findViewById(R.id.ListeLigneSuppDetailOT)
        ligneSuppAdapter = FragmentLigneSuppAdapterDetailOt(view.context,0)
        ligneSupps = ArrayList()

        ligneSupps!!.add(LigneSupplementaireOT("Descrption", "N°", "Unité", "Qte réalisé"))
        ligneSupps!!.add(LigneSupplementaireOT("Ligne supplementaire", "0.1.3", "m²", "300"))

        ligneSuppAdapter!!.addAll(ligneSupps)
        listView!!.adapter = ligneSuppAdapter

        return view
    }
}