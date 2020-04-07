package com.example.btpproject

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_cellule_lot.*
import org.w3c.dom.Text

class FragmentListeLigneLotAjouteOt : Fragment() {
    internal lateinit var view: View
    private var lignes: ArrayList<LigneLotOT>? = null
    private var listView: ListView? = null
    private var ligneLotAdapter: FragmentLigneLotAdapterAjoutOt? = null

    private var linearLayout: LinearLayout? = null
    private var numero: TextView? = null
    private var designation: TextView?= null
    private var unite: TextView?= null
    private var qteRealise: TextView?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = inflater.inflate(R.layout.activity_fragment_liste_ligne_lot_ajout_ot, container, false)




        //ajout de la liste des lignes
        listView = view.findViewById(R.id.listeLigneAjoutOt)
        ligneLotAdapter = FragmentLigneLotAdapterAjoutOt(view.context,0)

        lignes = ArrayList()

        lignes!!.add(LigneLotOT("2.0.1a","Déblais en tranchée et en puits :a) tranche comprise entre 0,00 et 1,50m","m²","3050,20"))
        lignes!!.add(LigneLotOT("2.01.a","Déblais en tranchée et en puits :a) tranche comprise entre 1,50 et 3,00m","m²","3050,20"))

        ligneLotAdapter!!.addAll(lignes)
        listView!!.adapter = ligneLotAdapter


        return view
    }
}
