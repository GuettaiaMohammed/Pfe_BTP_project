package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ArticleLigneSuppAdapter(context: Context, resource: Int) : ArrayAdapter<ArticleOT>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_article_ligne_supp_ot, null)

        val article = getItem(position)
        val nom = v.findViewById<TextView>(R.id.articleArtLigneSuppTV)
        val unite = v.findViewById<TextView>(R.id.uniteArtLigneSuppTV)
        val qte = v.findViewById<TextView>(R.id.QteConsomArtLigneSuppTV)


        nom.text = article.article
        qte.text = article.qteConsomme
        unite.text= article.unite


        return v
    }
}