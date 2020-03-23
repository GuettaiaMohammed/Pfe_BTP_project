package com.example.btpproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ArticleAdapter(context: Context, resource: Int) : ArrayAdapter<Article>(context, resource) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v: View

        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        v = layoutInflater.inflate(R.layout.activity_article_cellule, null)

        val article = getItem(position)
        val nom = v.findViewById<TextView>(R.id.articleNom)
        val qte = v.findViewById<TextView>(R.id.qteDemande)
        val date = v.findViewById<TextView>(R.id.date)


        nom.text = article.nom
        qte.text = article.qteDemande
        date.text= article.date



        return v
    }
}
