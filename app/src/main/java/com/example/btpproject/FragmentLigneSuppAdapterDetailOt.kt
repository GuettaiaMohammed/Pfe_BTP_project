package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FragmentLigneSuppAdapterDetailOt (context: Context, resource: Int) : ArrayAdapter<LigneSupplementaireOT>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_ligne_supp_detail_ot, null)

        val ligne= getItem(position)

        val description = v.findViewById<TextView>(R.id.descrpitionLigneSuppDetOTTV2)
        val numero = v.findViewById<TextView>(R.id.numLigneSuppDetOTTV2)
        val unite = v.findViewById<TextView>(R.id.uniteLigneSuppDetOTTV2)
        val qte = v.findViewById<TextView>(R.id.qteRealisLigneSuppDetOTTV2)


        description.text = ligne.description
        numero.text = ligne.numero
        unite.text = ligne.unite
        qte.text = ligne.qteRealise


        return v
    }
}