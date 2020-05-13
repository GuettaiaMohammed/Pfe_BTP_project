package com.example.btpproject

import android.annotation.TargetApi
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
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

class FragmentListeLigneLotDetailOt(var idOT: Int): Fragment() {
    internal lateinit var view: View
    private var lignes: ArrayList<LigneLotOT>? = null
    private var listView: ListView? = null
    private var ligneLotAdapter: FragmentLigneLotAdapterDetailOt? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.activity_fragment_liste_ligne_lot_detail_ot, container, false)

        listView = view.findViewById(R.id.ListLigneLotDetailOT)
        ligneLotAdapter = FragmentLigneLotAdapterDetailOt(view.context,0)

        val conn = ListeLigneOtt().execute(idOT)
        lignes = conn.get() as ArrayList<LigneLotOT>?

        if(lignes != null) {
            ligneLotAdapter!!.addAll(lignes)
        }

        listView!!.adapter = ligneLotAdapter

        return view
    }

    class ListeLigneOtt : AsyncTask<Int, Void, List<Any>?>() {
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
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("id", "=", id)
                        )
                    ),
                    object : HashMap<Any, Any>() {
                        init {
                            put(
                                "fields",
                                Arrays.asList("ligne_ordre_travail_ids")
                            )
                        }
                    }
                )) as Array<Any>)


                val listLigneOt = ArrayList<LigneLotOT>()
                listLigneOt!!.add(LigneLotOT("","N°", "Designation", "Unité", "Quantité réalisée"))

                if(list.isNotEmpty()) {
                    val jsonArray3 = JSONArray(list)
                    val ligneIds =
                        jsonArray3.getJSONObject(0).toString()

                    val ids = ligneIds.split("[")[1]
                    val ids2 = ids.split("]")[0]
                    //liste final des ids
                    val idd: List<String> = ids2.split(",")


                    if (idd[0] != "") {


                        // recupéré les champ nom unite num des ligne par chaque Id
                        for (i in 0..(idd!!.size) - 1) {
                            val idInt = idd[i].toInt()
                            println("************************ id int= ${idInt}")
                            val listLigne =
                                Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                    db, uid, password,
                                    "ligne.ordre.travail", "search_read",
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


                                    listLigneOt.add(LigneLotOT("", num, name, unit2, qte))
                                }

                            }

                        }
                    }

                return listLigneOt

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
