package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FragmentArticleAdapter (context: Context, resource: Int) : ArrayAdapter<ArticleOT>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_article_detail_ot, null)

        val articleOT = getItem(position)

        val ligne = v.findViewById<TextView>(R.id.ligneLotArtDetOTTV)
        val article = v.findViewById<TextView>(R.id.nomArlDetOTTV)
        val unite = v.findViewById<TextView>(R.id.uniteArtDetOTTV)
        val qte = v.findViewById<TextView>(R.id.qteArtConsomeDetOTTV)


        ligne.text = articleOT.ligne
        article.text = articleOT.article
        unite.text = articleOT.unite
        qte.text = articleOT.qteConsomme


        return v
    }
}