package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ChantierAdapter(mcontext: Context,mdata: ArrayList<Chantier>): RecyclerView.Adapter<ChantierAdapter.ChantierAdapterHolder>() {

     var mcontext: Context = mcontext
     var mdata: ArrayList<Chantier> = mdata


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChantierAdapterHolder {
        var layout: View?
        layout = LayoutInflater.from(mcontext).inflate(R.layout.activity_cellule_chantier, viewGroup, false)
        return ChantierAdapterHolder(layout)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    override fun onBindViewHolder(chantierAdapterHolder: ChantierAdapterHolder, position: Int) {
        chantierAdapterHolder.nom!!.text = mdata.get(position).nom
        chantierAdapterHolder.etat!!.text = mdata.get(position).etat
        chantierAdapterHolder.avancement!!.text = mdata.get(position).avancement.toString()
    }


     class ChantierAdapterHolder: RecyclerView.ViewHolder{
        var nom: TextView? = null
        var etat: TextView? = null
        var avancement: TextView? = null

        constructor(itemView: View) : super(itemView){
            nom = itemView.findViewById(R.id.nomChantierListCh)
            etat = itemView.findViewById(R.id.etatChantierListeTV)
            avancement = itemView.findViewById(R.id.avancementChantierListeTV)
        }
    }
}
