package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox

import android.widget.TextView


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EmployeAdapter(context: Context, resource: Int) : ArrayAdapter<Employe>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_employe, null)

        val employe= getItem(position)

        val nom = v.findViewById<TextView>(R.id.nom)
        val metier = v.findViewById<TextView>(R.id.lotSuiviTV)
        var check=v.findViewById<CheckBox>(R.id.checkBox)


        nom.text = employe.nom
        metier.text = employe.metier
        check=employe.checkBox



        return v
    }
}

