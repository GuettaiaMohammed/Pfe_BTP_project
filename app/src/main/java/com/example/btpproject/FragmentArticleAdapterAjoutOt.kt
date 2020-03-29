package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class FragmentArticleAdapterAjoutOt (context: Context, resource: Int) : ArrayAdapter<ArticleOT>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_cellule_article_ajout_ot, null)

        val articleOT = getItem(position)

        val ligne = v.findViewById<TextView>(R.id.ligneLotArtAjtOTTV)
        val article = v.findViewById<TextView>(R.id.nomArlAjtOTTV)
        val unite = v.findViewById<TextView>(R.id.uniteArtAjtOTTV)


        ligne.text = articleOT.ligne
        article.text = articleOT.article
        unite.text = articleOT.unite


        return v
    }
}