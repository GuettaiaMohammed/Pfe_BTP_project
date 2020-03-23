package com.example.btpproject

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.activity_ordre_de_travail_cellule.*

class OrdreDeTravailAdapter(context: Context, resource: Int) : ArrayAdapter<OrdreDeTravail>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_ordre_de_travail_cellule, null)

        val ordreDeTravail = getItem(position)
        val nom = v.findViewById<TextView>(R.id.nomOTTV)
        val lot = v.findViewById<TextView>(R.id.LotOTTV)

        nom.text = ordreDeTravail!!.nom
        lot.text = ordreDeTravail.lot

        return v
    }
}
