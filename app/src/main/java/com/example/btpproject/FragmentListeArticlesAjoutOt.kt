package com.example.btpproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class FragmentListeArticlesAjoutOt : Fragment() {
    internal lateinit var view: View
    private var articleOT: ArrayList<ArticleOT>? = null
    private var listView: ListView? = null
    private var articleAdapter: FragmentArticleAdapterAjoutOt? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.activity_fragment_liste_articles_ajout_ot, container, false)

        listView = view.findViewById(R.id.listeArticleAjoutOt)
        articleAdapter = FragmentArticleAdapterAjoutOt(view.context,0)
        articleOT = ArrayList()



        articleOT!!.add(ArticleOT("Remblais de fouilles après exécution des ouvrages en béton armé.","Béton", "m3","300"))

        articleAdapter!!.addAll(articleOT)
        listView!!.adapter = articleAdapter

        return view
    }
}
