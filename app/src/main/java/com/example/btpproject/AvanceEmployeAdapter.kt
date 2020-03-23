package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class AvanceEmployeAdapter(context: Context, resource: Int) :
    ArrayAdapter<AvanceEmploye>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_avance_employe_cellule, null)

        val avance = getItem(position)
        val nom = v.findViewById<TextView>(R.id.nomEmpTV)
        val date = v.findViewById<TextView>(R.id.dateAV)
        val prix = v.findViewById<TextView>(R.id.prixTV)

        nom.text = avance!!.employe
        date.text = avance.dateAvance
        prix.text = avance.prix

        return v
    }
}
