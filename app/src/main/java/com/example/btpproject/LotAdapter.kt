package com.example.btpproject



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog


class LotAdapter(context: Context, resource: Int) : ArrayAdapter<Lot>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_lot, null)

        val lots = getItem(position)
        val num = v.findViewById<TextView>(R.id.num)

        val lot = v.findViewById<TextView>(R.id.lot)
        val etat = v.findViewById<TextView>(R.id.etat)

        num.text = lots.num
        lot.text = lots.lot
        etat.text = lots.etat


        return v
    }
}

