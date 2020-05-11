package com.example.btpproject

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class FragmentListeArticlesAjoutOt(var idLot: Int) : Fragment() {
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

        var conn = ListeArticleOt().execute(idLot)
        if(conn.get()!!.isNotEmpty() || conn.get() != null) {
            articleOT = conn.get() as ArrayList<ArticleOT>?

            if (articleOT != null) {
                articleAdapter!!.addAll(articleOT)
            }
        }
        listView!!.adapter = articleAdapter

        return view
    }

    fun create(idOT: Int){
        if(articleOT != null) {
            for (i in 0..(articleOT!!.size) - 1) {
                val nomLigne = listView!!.getChildAt(i).findViewById<TextView>(R.id.ligneLotArtAjtOTTV).text.toString()
                val nomArticle = listView!!.getChildAt(i).findViewById<TextView>(R.id.nomArlAjtOTTV)
                    .text.toString()
                val unite =
                    listView!!.getChildAt(i).findViewById<TextView>(R.id.uniteArtAjtOTTV).text.toString()
                val qte = listView!!.getChildAt(i).findViewById<EditText>(R.id.qteConsommeAjtOtET).text.toString()
                val idLL = articleOT!!.get(i).idLigne
                var idP = articleOT!!.get(i).idArticle

                val connAjt = AjouterArticleOT().execute(idOT.toString(), nomArticle, unite, qte, idP, idLL)

            }
        }
    }

    class ListeArticleOt : AsyncTask<Int, Void, List<Any>?>() {
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(Build.VERSION_CODES.KITKAT)
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

                //liste des id des lignes
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "project.lot", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "=", id)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("article_lot_ids")
                            )
                        }
                    }
                )) as Array<Any>)


                val listArticleOt = ArrayList<ArticleOT>()

                if(list.isNotEmpty()) {
                    val jsonArray3 = JSONArray(list)
                    val ligneIds =
                        jsonArray3.getJSONObject(0).toString()

                    val ids = ligneIds.split("[")[1]
                    val ids2 = ids.split("]")[0]
                    //liste final des ids
                    val idd: List<String> = ids2.split(",")

                    if(idd[0] != "") {

                        // recupéré les champ nom unite num des ligne par chaque Id
                        for (i in 0..(idd!!.size) - 1) {
                            val idInt = idd[i].toInt()
                            val listLigne = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                db, uid, password,
                                "article.lot", "search_read",
                                Arrays.asList(
                                    Arrays.asList(
                                        Arrays.asList("id", "=", idInt)
                                    )
                                ),
                                object : HashMap<Any, Any>() {
                                    init {
                                        put(
                                            "fields",
                                            Arrays.asList("product_id", "unite", "ligne_lot_id")
                                        )
                                    }
                                }
                            )) as Array<Any>)

                            if(listLigne.isNotEmpty()) {
                                //liste des champs
                                val jsonArray4 = JSONArray(listLigne)
                                val prod =
                                    jsonArray4.getJSONObject(0).getString("product_id").toString()
                                var prod1 = prod.split("[")[1]
                                var prod2 = prod1.split(",")[0]

                                val ligneObj =
                                    jsonArray4.getJSONObject(0).getString("ligne_lot_id").toString()
                                var ligne1 = ligneObj.split("[")[1]
                                var ligneid = ligne1.split(",")[0]

                                var ligneN = ligneObj.split("\"")[1]
                                var ligneNom = ligneN.split("\"")[0]

                                val liste = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                    db, uid, password,
                                    "product.product", "search_read",
                                    Arrays.asList(
                                        Arrays.asList(
                                            Arrays.asList("id", "=", prod2.toInt())
                                        )
                                    ),
                                    object : HashMap<Any, Any>() {
                                        init {
                                            put("fields", Arrays.asList("name"))
                                            //put("limit", 5);
                                        }
                                    }
                                )) as Array<Any>)

                                val jsonArray5 = JSONArray(liste)

                                val unite =
                                    jsonArray4.getJSONObject(0).getString("unite").toString()
                                var unit = unite.split("\"")[1]
                                var unit2 = unit.split("\"")[0]

                                val name = jsonArray5.getJSONObject(0).getString("name").toString()


                                listArticleOt.add(ArticleOT(ligneid, ligneNom, prod2, name, unit2, "0"))
                            }
                        }

                    }

                }
                return listArticleOt

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }

    class AjouterArticleOT : AsyncTask<String, Void,Int?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"

        var name:String=""
        var qte:String = ""
        var idOT:Int=0
        var idU:Int=0
        var idP:Int=0
        var idLL:Int=0

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


                idOT = infos[0].toInt()
                name= infos[1]
                idU= idUn.toInt()
                qte= infos[3]
                idP = infos[4].toInt()
                idLL = infos[5].toInt()


                var id: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "article.consom", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("ordre_travail_id", idOT)
                                put("ligne_lot_id", idLL)
                                put("product_id", idP)
                                put("name", name)
                                put("unite", idU)
                                put("qte", qte)

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
