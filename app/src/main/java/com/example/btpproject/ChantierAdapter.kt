package com.example.btpproject

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ChantierAdapter(mcontext: Context,mdata: ArrayList<Chantier>, onNoteListener: OnNoteListener): RecyclerView.Adapter<ChantierAdapter.ChantierAdapterHolder>() {

     var mcontext: Context = mcontext
     var mdata: ArrayList<Chantier> = mdata
     var mOnNoteListener:OnNoteListener = onNoteListener


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ChantierAdapterHolder {
        var layout: View?
        layout = LayoutInflater.from(mcontext).inflate(R.layout.activity_cellule_chantier, viewGroup, false)
        return ChantierAdapterHolder(layout, mOnNoteListener)
    }

    override fun getItemCount(): Int {
        return mdata.size
    }

    override fun onBindViewHolder(chantierAdapterHolder: ChantierAdapterHolder, position: Int) {
        chantierAdapterHolder.nom!!.text = mdata.get(position).nom
        chantierAdapterHolder.emplac!!.text = mdata.get(position).emplacement
        chantierAdapterHolder.avancement!!.text = mdata.get(position).avancement
    }


    class ChantierAdapterHolder: RecyclerView.ViewHolder, View.OnClickListener{

        var nom: TextView? = null
        var emplac: TextView? = null
        var avancement: TextView? = null
        var onNoteListener: OnNoteListener? = null

        constructor(itemView: View, onNoteListener: OnNoteListener) : super(itemView){
            this
            nom = itemView.findViewById(R.id.nomChantierListCh)
            emplac = itemView.findViewById(R.id.emplacChantierListeTV)
            avancement = itemView.findViewById(R.id.avancementChantierListeTV)
            this.onNoteListener = onNoteListener

            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            onNoteListener!!.onNoteClick(adapterPosition)
        }

    }

    interface OnNoteListener{
        fun onNoteClick(position: Int)
    }
}
