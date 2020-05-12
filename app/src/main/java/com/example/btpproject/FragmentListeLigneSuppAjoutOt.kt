package com.example.btpproject

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList


class FragmentListeLigneSuppAjoutOt(var idLot: Int) : Fragment() {
    internal val url = "http://sogesi.hopto.org:7013/"
    internal val db = "BTP_pfe"
    internal val username = "admin"
    internal val password = "pfe_chantier"


    internal lateinit var view: View
    private var ligneSupps: ArrayList<LigneSupplementaireOT>? = null
    private var listView: ListView? = null
    private var ligneSuppAdapter: FragmentLigneSuppAdapterAjoutOt? = null

    private var articleAdapter: ArticleLigneSuppAdapter? = null
    private var listViewArticle: ListView? = null
    private var listArticles: ArrayList<ArticleOT>? = null

    //les listes des spinner
    private val listArticlesS = arrayListOf<String>()
    private val listUnitesS = arrayListOf<String>()
    private val listLigneLotS = arrayListOf<String>()

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
        ligneSuppAdapter!!.add(LigneSupplementaireOT("Description", "N°", "Ligne parente", "Unité", "Qte réalisé"))

        listArticlesS.add("")
        listUnitesS.add("")
        listLigneLotS.add("")

        val conn4 = Article().execute(url)
        val conn5 = Unite().execute(url)
        val connLigneLot = LignesLots().execute(idLot)


        //liste articles
        val listArticle=conn4.get()
        //liste des unités
        val listU=conn5.get()
        //liste des lignes lot
        val listLigneLot = connLigneLot.get()

        //récupéré lles données de l'objet JSON
        val jsonArray8: JSONArray
        jsonArray8 = JSONArray(listArticle)
        for (i in 0..(listArticle!!.size) - 1) {

            val name = jsonArray8.getJSONObject(i).getString("name").toString()
            listArticlesS.add(name)

        }

        //récupéré lles données de l'objet JSON
        val jsonArray6 = JSONArray(listU)
        for (i in 0..(listU!!.size) - 1) {

            val name = jsonArray6.getJSONObject(i).getString("name").toString()
            listUnitesS.add(name)
        }

        //récupéré lles données de l'objet JSON
        val jsonLigneLot = JSONArray(listLigneLot)
        for (i in 0..(listLigneLot!!.size) - 1) {

            val name = jsonLigneLot.getJSONObject(i).getString("name").toString()
            listLigneLotS.add(name)
        }

        // Page Ajout Ligne supplémentaire
        val ajoutLigneSupp: Button = view.findViewById(R.id.ajoutLigneSuppBtn)
        ajoutLigneSupp.setOnClickListener {
            //Inflate the dialog with custom view
            val mDialogView = LayoutInflater.from(view.context).inflate(R.layout.activity_ajouter_ligne_supp_ot, null)
            //AlertDialogBuilder
            val mBuilder: AlertDialog = AlertDialog.Builder(view.context).create()
                mBuilder.setView(mDialogView)

            //spinner Unité
            val spinnerU = mDialogView.findViewById<Spinner>(R.id.spinnerUniteDMesureLigneSupp)
            val adapter1: ArrayAdapter<String> = ArrayAdapter<String>(mBuilder.context, android.R.layout.simple_spinner_item, listUnitesS)
            spinnerU.adapter = adapter1

            //spinner LigneLot
            val spinnerLL = mDialogView.findViewById<Spinner>(R.id.spinnerLigneParentLigneSupp)
            val adapterLL: ArrayAdapter<String> = ArrayAdapter<String>(mBuilder.context, android.R.layout.simple_spinner_item, listLigneLotS)
            spinnerLL.adapter = adapterLL

            listViewArticle = mDialogView.findViewById(R.id.listeArtConsomLigneSuppOt)
            articleAdapter = ArticleLigneSuppAdapter(mBuilder.context, 0)
            articleAdapter!!.add(ArticleOT("","","Ligne","","Article", "Unité", "Qte consomé"))

            val numLS = mDialogView.findViewById<TextInputEditText>(R.id.numLigneSuppET)
            val descLS = mDialogView.findViewById<TextInputEditText>(R.id.descLigneSuppET)
            val qteRealLS = mDialogView.findViewById<TextInputEditText>(R.id.qteRealiseLigneSuppET)

            //valider l'ajout d'un ligne supplémentaire
            val btnValid = mDialogView.findViewById<Button>(R.id.validAjtLigneSuppBtn)
            btnValid.setOnClickListener {

                val numeroLigne = numLS.text.toString()
                val descLigne = descLS.text.toString()
                val uniteLigne = spinnerU.selectedItem.toString()
                val ligneParente = spinnerLL.selectedItem.toString()
                val ligneParentePosition = spinnerLL.selectedItemPosition
                val ligneParenteId =  jsonLigneLot.getJSONObject(ligneParentePosition-1).getString("id").toString()
                val qteLigne = qteRealLS.text.toString()

                if(numeroLigne != "" && descLigne != "" && uniteLigne != "" && ligneParente != "" && qteLigne != ""){
                    ligneSuppAdapter!!.add(LigneSupplementaireOT(descLigne, numeroLigne, ligneParenteId, uniteLigne, qteLigne))
                    mBuilder.dismiss()
                }else{
                    Toast.makeText(mBuilder.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
                }


            }

                //Page ajouter un article consommé pour une ligen supplémentaire
                val articleConsomBtn = mDialogView.findViewById<Button>(R.id.ajouterArticleConsomLigneSuppBtn)
                articleConsomBtn.setOnClickListener {
                    // afficher l'ajout de l'article consommé
                    val mDialogView2 = LayoutInflater.from(mBuilder.context).inflate(R.layout.activity_ajouter_article_consom_ligne_supp, null)
                    //AlertDialogBuilder
                    val mBuilder2: AlertDialog = AlertDialog.Builder(mBuilder.context).create()
                        mBuilder2.setView(mDialogView2)


                    //quantité consommé Edit text
                    val qteConsET = mDialogView2.findViewById<TextInputEditText>(R.id.qteConsomArtLigneSuppEt)

                    //Spinner Ajouté article consommé
                    val spinnerA = mDialogView2.findViewById<Spinner>(R.id.spinnerALigneSupp)
                    //Remplire Spinner
                    val adapter: ArrayAdapter<String> = ArrayAdapter<String>(mBuilder2.context, android.R.layout.simple_spinner_item, listArticlesS)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerA.adapter = adapter


                        //Boutton valider Ajout d'un Article pour une ligne supplémentaire
                        val validAjtArticleBtn = mDialogView2.findViewById<Button>(R.id.validAjtArticleLigneSuppBtn)
                        validAjtArticleBtn.setOnClickListener {
                            val article = spinnerA.selectedItem.toString()
                                val artPos = spinnerA.selectedItemPosition
                                val articleId = jsonArray8.getJSONObject(artPos-1).getString("id").toString()

                            val unitePos = spinnerA.selectedItemPosition
                                val unite = jsonArray8.getJSONObject(unitePos-1).getString("uom_id").toString()
                                var unit = unite.split("\"")[1]
                                var unit2 = unit.split("\"")[0]

                            val qteConsom = qteConsET.text.toString()

                            if(article != "" && unite != "" && qteConsom != "") {
                                articleAdapter!!.add(ArticleOT("","","",articleId,article, unit2, qteConsom))
                                mBuilder2.dismiss()
                            }else{
                                Toast.makeText(mBuilder2.context, "Veuillez remplire tout les cases", Toast.LENGTH_SHORT).show()
                            }
                        }

                    mBuilder2.show()

                }


            listViewArticle!!.adapter = articleAdapter

            val mAlertDialog = mBuilder.show()

        }


        listView!!.adapter = ligneSuppAdapter


        return view
    }

    fun createLigneSupp(idOT: Int){
        if(ligneSuppAdapter != null) {
            for (i in 1..(ligneSuppAdapter!!.count) - 1) {
                val num =
                    listView!!.getChildAt(i).findViewById<TextView>(R.id.numLigneSuppAjtOTTV2).text.toString()
                val name = listView!!.getChildAt(i).findViewById<TextView>(R.id.descriptionLigneSuppAjtOTTV2)
                    .text.toString()
                val unite =
                    listView!!.getChildAt(i).findViewById<TextView>(R.id.uniteLigneSuppAjtOTTV2).text.toString()
                val qte = listView!!.getChildAt(i).findViewById<TextView>(R.id.qteRealisLigneSuppAjtOtTV)
                    .text.toString()
                val ligneParentId = ligneSuppAdapter!!.getItem(i).ligneParenteId

                val connAjt =
                    AjouterLigneSuppOT().execute(idOT.toString(), num, name, unite, qte, ligneParentId)
                val idLigneS = connAjt.get()
                createArticleLignesupp(idLigneS!!.toInt())

            }
        }
    }

    fun createArticleLignesupp(idLigneSupp: Int){
        if(articleAdapter != null) {
            for (i in 1..(articleAdapter!!.count) - 1) {
                val unite =
                    listViewArticle!!.getChildAt(i).findViewById<TextView>(R.id.uniteArtLigneSuppTV).text.toString()
                val qte = listViewArticle!!.getChildAt(i).findViewById<TextView>(R.id.QteConsomArtLigneSuppTV)
                    .text.toString()
                val articleId = articleAdapter!!.getItem(i).idArticle

                val connAjt =
                    AjouterArticleLigneSuppOT().execute(idLigneSupp.toString(), articleId, unite, qte)

            }
        }
    }

    class Article : AsyncTask<String, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                    )
                ) as Int

                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", "http://sogesi.hopto.org:7013"))
                            }
                        })
                    }
                }

                //liste des chantier
                var liste: List<*> = java.util.ArrayList<Any>()

                liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "product.product", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "!=", 0)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields",
                                Arrays.asList(
                                    "name",
                                    "uom_id"
                                ))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)




                return liste

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }


    }



    class Unite : AsyncTask<String, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg url: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", "http://sogesi.hopto.org:7013"))
                            }
                        })
                    }
                }

                //liste des chantier
                var liste: List<*> = java.util.ArrayList<Any>()

                liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "uom.uom", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "!=", 0)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name"))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)





                return liste

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }

    class LignesLots : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        override fun doInBackground(vararg id: Int?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                    )
                ) as Int



                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(String.format("%s/xmlrpc/2/object", "http://sogesi.hopto.org:7013"))
                            }
                        })
                    }
                }

                //liste des chantier
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "ligne.lot", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("lot_id", "=", id)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("name")
                            )
                        }
                    }
                )) as Array<Any>)

                if(list.isNotEmpty()) {
                    return list
                }

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }

    class AjouterLigneSuppOT : AsyncTask<String, Void,Int?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"


        var num:String=""
        var name:String=""
        var qteRealis:String = ""
        var idOT:Int=0
        var idU:Int=0
        var idLP:Int=0
        var idL:Int=0

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): Int? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
                    common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(
                                    String.format(
                                        "%s/xmlrpc/2/object",
                                        "http://sogesi.hopto.org:7013"
                                    )
                                )
                            }
                        })
                    }
                }

                var liste: List<*> = java.util.ArrayList<Any>()

                liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "uom.uom", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("name", "=", infos[3])
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name"))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)

                val jsonArray4 = JSONArray(liste)
                val idUn =
                    jsonArray4.getJSONObject(0).getString("id").toString()


                idOT = infos[0].toInt()
                num = infos[1]
                name= infos[2]
                idU= idUn.toInt()
                qteRealis= infos[4]
                idLP = infos[5].toInt()

                var id: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "sous_ligne.suplementaire", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("ordre_travail_id", idOT)
                                put("ligne_lot_id", idLP)
                                put("num", num)
                                put("name", name)
                                put("unite", idU)
                                put("qte_realis", qteRealis)

                            }
                        })
                    )
                ) as Int
                println("************************  liste des données = $id")
                return id

            } catch (e: MalformedURLException) {
                Log.d(
                    "MalformedURLException",
                    "*********************************************************"
                )
                Log.d("MalformedURLException", e.toString())
            } catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    class AjouterArticleLigneSuppOT : AsyncTask<String, Void,Int?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"



        var qte:String = ""
        var idArt:Int=0
        var idU:Int=0
        var idLP:Int=0

        @SuppressLint("NewApi")
        override fun doInBackground(vararg infos:String): Int? {
            var client = XmlRpcClient()
            var common_config = XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL =
                    URL(String.format("%s/xmlrpc/2/common", "http://sogesi.hopto.org:7013"))

                val uid: Int = client.execute(
                    common_config, "authenticate", Arrays.asList(
                        db, username, password, Collections.emptyMap<Any, Any>()
                    )
                ) as Int


                val models = object : XmlRpcClient() {
                    init {
                        setConfig(object : XmlRpcClientConfigImpl() {
                            init {
                                serverURL = URL(
                                    String.format(
                                        "%s/xmlrpc/2/object",
                                        "http://sogesi.hopto.org:7013"
                                    )
                                )
                            }
                        })
                    }
                }

                var liste: List<*> = java.util.ArrayList<Any>()

                liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "uom.uom", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("name", "=", infos[2])
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put("fields", Arrays.asList("name"))
                            //put("limit", 5);
                        }
                    }
                )) as Array<Any>)

                val jsonArray4 = JSONArray(liste)
                val idUn =
                    jsonArray4.getJSONObject(0).getString("id").toString()


                idLP = infos[0].toInt()
                idArt = infos[1].toInt()
                idU= idUn.toInt()
                qte= infos[3]

                var id: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "article.sous_line", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("sous_ligne_suplementaire_id", idLP)
                                put("product_id", idArt)
                                put("unite", idU)
                                put("qte_consom", qte)

                            }
                        })
                    )
                ) as Int
                println("************************  liste des données = $id")
                return id

            } catch (e: MalformedURLException) {
                Log.d(
                    "MalformedURLException",
                    "*********************************************************"
                )
                Log.d("MalformedURLException", e.toString())
            } catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return 0
        }
    }
}