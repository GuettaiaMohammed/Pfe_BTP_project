package com.example.btpproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.widget.Button



class FragmentListeLigneSuppAjoutOt : Fragment() {
    internal lateinit var view: View
    private var ligneSupps: ArrayList<LigneSupplementaireOT>? = null
    private var listView: ListView? = null
    private var ligneSuppAdapter: FragmentLigneSuppAdapterAjoutOt? = null

    private var articleAdapter: ArticleLigneSuppAdapter? = null
    private var listViewArticle: ListView? = null
    private var listArticles: ArrayList<ArticleOT>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(
            R.layout.activity_fragment_liste_ligne_supp_ajout_ot,
            container,
            false
        )

        listView = view.findViewById(R.id.ListeLigneSuppAjoutOT)
        ligneSuppAdapter = FragmentLigneSuppAdapterAjoutOt(view.context,0)

        ligneSupps = ArrayList()
        ligneSupps!!.add(LigneSupplementaireOT("Ligne supplementaire", "0.1.3", "m²", "0"))

        val ajoutLigneSupp: Button = view.findViewById(R.id.ajoutLigneSuppBtn)
        ajoutLigneSupp.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(view.context).inflate(R.layout.activity_ajouter_ligne_supp_ot, null)
            //AlertDialogBuilder
            val mBuilder = AlertDialog.Builder(view.context)
                .setView(mDialogView)

            listViewArticle = mDialogView.findViewById(R.id.listeArtConsomLigneSuppOt)
            articleAdapter = ArticleLigneSuppAdapter(mBuilder.context, 0)

            listArticles = ArrayList()
            listArticles!!.add(ArticleOT("Ligne","Article", "Unité", "Qte consomé"))
            listArticles!!.add(ArticleOT("","béton", "m²", "30 m²"))

            articleAdapter!!.addAll(listArticles)
            listViewArticle!!.adapter = articleAdapter

            //Boutton ajouter un article consommé pour une ligen supplémentaire
            val articleConsomBtn = mDialogView.findViewById<Button>(R.id.ajouterArticleConsomLigneSuppBtn)
            articleConsomBtn.setOnClickListener {
                // afficher l'ajout de l'article consommé
                val mDialogView2 = LayoutInflater.from(mBuilder.context).inflate(R.layout.activity_ajouter_article_consom_ligne_supp, null)
                //AlertDialogBuilder
                val mBuilder2 = AlertDialog.Builder(mBuilder.context)
                    .setView(mDialogView2)

                mBuilder2.show()

            }


            val mAlertDialog = mBuilder.show()

        }

        ligneSuppAdapter!!.addAll(ligneSupps)
        listView!!.adapter = ligneSuppAdapter


        return view
    }
}