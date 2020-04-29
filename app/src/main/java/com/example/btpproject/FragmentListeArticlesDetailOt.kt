package com.example.btpproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentListeArticlesDetailOt(var idLot: Int) : Fragment() {
    internal lateinit var view: View
    private var articleOT: ArrayList<ArticleOT>? = null
    private var listView: ListView? = null
    private var articleAdapter: FragmentArticleAdapterDetailOt? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.activity_fragment_liste_articles_detail_ot, container, false)

        listView = view.run { findViewById(R.id.listArticlesDetailOT) }
        articleAdapter = FragmentArticleAdapterDetailOt(view.context,0)
        articleOT = ArrayList()

        articleOT!!.add(ArticleOT("Ligne","Article", "Unité","Quantité consommé"))


        articleOT!!.add(ArticleOT("Béton armé pour ouvrage en infrastructure :b) Béton dosé à 350Kg/m3 pour a/poteaux et voiles.","Béton", "m²","20"))


        articleAdapter!!.addAll(articleOT)
        listView!!.adapter = articleAdapter

        return view
    }
}
