package com.example.btpproject



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class QteRealiseAdapter(context: Context, resource: Int) : ArrayAdapter<QuantiteRealise>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_suivi_qte_realise, null)

        val quantite = getItem(position)
        val qte = v.findViewById<TextView>(R.id.qteRealise)
        val date = v.findViewById<TextView>(R.id.date)

        qte.text = quantite.qterealise
        date.text = quantite.date


        return v
    }
}

