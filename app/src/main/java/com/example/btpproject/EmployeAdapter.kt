package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import android.widget.TextView


class EmployeAdapter(context: Context, resource: Int) : ArrayAdapter<Employe>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_employe, null)

        val employe= getItem(position)

        val nom = v.findViewById<TextView>(R.id.nom)
        val metier = v.findViewById<TextView>(R.id.lotSuiviTV)


        nom.text = employe.nom
        metier.text = employe.metier



        return v
    }
}

