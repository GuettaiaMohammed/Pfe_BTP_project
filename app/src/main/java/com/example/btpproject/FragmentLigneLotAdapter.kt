package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FragmentLigneLotAdapter(context: Context, resource: Int) : ArrayAdapter<LigneLotOT>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_ligne_lot_detail_ot, null)

        val ligne= getItem(position)

        val num = v.findViewById<TextView>(R.id.numLigneLotDetOTTV)
        val design = v.findViewById<TextView>(R.id.designLigneLotDetOTTV)
        val unite = v.findViewById<TextView>(R.id.uniteDetOTTV)
        val qte = v.findViewById<TextView>(R.id.qteLigneRealiseDetOTTV)


        num.text = ligne.numero
        design.text = ligne.designation
        unite.text = ligne.unite
        qte.text = ligne.qteRealise


        return v
    }
}