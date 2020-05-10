package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView

class FragmentLigneLotAdapterAjoutOt(context: Context, resource: Int) : ArrayAdapter<LigneLotOT>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_ligne_lot_ajout_ot, null)

        val ligne= getItem(position)

        val num = v.findViewById<TextView>(R.id.numLigneLotAjtOTTV)
        val design = v.findViewById<TextView>(R.id.designLigneLotAjtOTTV)
        val unite = v.findViewById<TextView>(R.id.uniteAjtOTTV)


        num.text = ligne.numero
        design.text = ligne.designation
        unite.text = ligne.unite


        return v
    }
}