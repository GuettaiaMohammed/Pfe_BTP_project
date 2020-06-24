package com.example.btpproject

import android.annotation.TargetApi
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
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

class FragmentListeLigneSuppDetailOt(var idOT: Int): Fragment() {
    internal lateinit var view: View
    private var ligneSupps: ArrayList<LigneSupplementaireOT>? = null
    private var listView: ListView? = null
    private var ligneSuppAdapter: FragmentLigneSuppAdapterDetailOt? = null

    lateinit var mPreferences : SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(
            R.layout.activity_fragment_liste_ligne_supp_detail_ot,
            container,
            false
        )

        listView = view.findViewById(R.id.ListeLigneSuppDetailOT)
        ligneSuppAdapter = FragmentLigneSuppAdapterDetailOt(view.context,0)

        mPreferences = PreferenceManager.getDefaultSharedPreferences(view.context)
        val url = mPreferences.getString("url", "http://sogesi.hopto.org:7013")
        val db = mPreferences.getString("bdd", "BTP_pfe")
        val username= mPreferences.getString("username", "admin")
        val password = mPreferences.getString("passBdd", "pfe_chantier")

        val connLignsupp = ListeLigneSupp().execute(idOT.toString(), url, db, username, password)
        ligneSupps = connLignsupp.get() as ArrayList<LigneSupplementaireOT>?
if(ligneSupps!=null){
        ligneSuppAdapter!!.addAll(ligneSupps)}
        listView!!.adapter = ligneSuppAdapter

        return view
    }

    class ListeLigneSupp : AsyncTask<String, Void, List<Any>?>() {

        @TargetApi(Build.VERSION_CODES.KITKAT)
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        override fun doInBackground(vararg v: String?): List<Any>? {
            var client =  XmlRpcClient()
            var common_config  =  XmlRpcClientConfigImpl()
            try {
                //Testé l'authentification
                common_config.serverURL = URL(String.format("%s/xmlrpc/2/common", v[1]))

                val uid: Int=  client.execute(
                    common_config, "authenticate", Arrays.asList(
                        v[2], v[3], v[4], Collections.emptyMap<Any, Any>()
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
                    v[2], uid, v[4],
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "=", v[0]!!.toInt())
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("sous_ligne_suplementaire_ids")
                            )
                        }
                    }
                )) as Array<Any>)


                val listLigneSupp = ArrayList<LigneSupplementaireOT>()
                listLigneSupp!!.add(LigneSupplementaireOT("Description", "N°", "Ligne Parente","Unité", "Quantité réalisée"))

                if(list.isNotEmpty()) {
                    val jsonArray3 = JSONArray(list)
                    val ligneIds =
                        jsonArray3.getJSONObject(0).toString()

                    val ids = ligneIds.split("[")[1]
                    val ids2 = ids.split("]")[0]
                    //liste final des ids
                    val idd: List<String> = ids2.split(",")

                    println("${idd[0]}")
                    if (idd[0] != "") {


                        // recupéré les champ nom unite num des ligne par chaque Id
                        for (i in 0..(idd!!.size) - 1) {
                            val idInt = idd[i].toInt()
                            val listLigne =
                                Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                    v[2], uid, v[4],
                                    "sous_ligne.suplementaire", "search_read",
                                    Arrays.asList(
                                        Arrays.asList(
                                            Arrays.asList("id", "=", idInt)
                                        )
                                    ),
                                    object : HashMap<Any, Any>() {
                                        init {
                                            put(
                                                "fields",
                                                Arrays.asList(
                                                    "ligne_supp_lot_id",
                                                    "name",
                                                    "num",
                                                    "unite",
                                                    "qte_realis"
                                                )
                                            )
                                        }
                                    }
                                )) as Array<Any>)

                            if(listLigne.isNotEmpty()) {
                                //liste des champs
                                val jsonArray4 = JSONArray(listLigne)

                              /*  val ligneObj =
                                    jsonArray4.getJSONObject(0).getString("ligne_supp_lot_id").toString()
                                var ligne = ligneObj.split("\"")[1]
                                var ligneParente = ligne.split("\"")[0]*/

                                val unite =
                                    jsonArray4.getJSONObject(0).getString("unite").toString()
                                var unit = unite.split("\"")[1]
                                var unit2 = unit.split("\"")[0]

                                val num =
                                    jsonArray4.getJSONObject(0).getString("num").toString()
                                val name =
                                    jsonArray4.getJSONObject(0).getString("name").toString()
                                val qte =
                                    jsonArray4.getJSONObject(0).getString("qte_realis")
                                        .toString()

                                listLigneSupp.add(LigneSupplementaireOT(name, num, "", unit2, qte))
                            }

                        }

                    }
                }

                return listLigneSupp

            }catch (e: MalformedURLException) {
                Log.d("MalformedURLException", "*********************************************************")
                Log.d("MalformedURLException", e.toString())
            }  catch (e: XmlRpcException) {
                e.printStackTrace()
            }
            return null
        }
    }

}