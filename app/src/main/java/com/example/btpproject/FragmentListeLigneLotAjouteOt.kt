package com.example.btpproject

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_cellule_lot.*
import org.apache.xmlrpc.XmlRpcException
import org.apache.xmlrpc.client.XmlRpcClient
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl
import org.json.JSONArray
import org.w3c.dom.Text
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class FragmentListeLigneLotAjouteOt(var idLot: Int) : Fragment() {
    internal lateinit var view: View
    private var lignes: ArrayList<LigneLotOT>? = null
    private var listView: ListView? = null
    private var ligneLotAdapter: FragmentLigneLotAdapterAjoutOt? = null

    private var linearLayout: LinearLayout? = null
    private var numero: TextView? = null
    private var designation: TextView?= null
    private var unite: TextView?= null
    private var qteRealise: TextView?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = inflater.inflate(R.layout.activity_fragment_liste_ligne_lot_ajout_ot, container, false)




        //ajout de la liste des lignes
        listView = view.findViewById(R.id.listeLigneAjoutOt)
        ligneLotAdapter = FragmentLigneLotAdapterAjoutOt(view.context,0)

        var conn = ListeLigneOt().execute(idLot)
        lignes = conn.get() as ArrayList<LigneLotOT>?

        ligneLotAdapter!!.addAll(lignes)
        listView!!.adapter = ligneLotAdapter


        return view
    }

    class ListeLigneOt : AsyncTask<Int, Void, List<Any>?>() {
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

                //liste des id des lignes
                val list = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                    db, uid, password,
                    "ordre.travail", "search_read",
                    Arrays.asList(
                        Arrays.asList(
                            Arrays.asList("lot_id", "=", id)
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

                val jsonArray3 = JSONArray(list)
                val ligneIds =
                    jsonArray3.getJSONObject(0).toString()

                val ids = ligneIds.split("[")[1]
                val ids2 = ids.split("]")[0]
                //liste final des ids
                val id: List<String> = ids2.split(",")


                if(id != null) {

                    val listLigneOt = ArrayList<LigneLotOT>()
                    // recupéré les champ nom unite num des ligne par chaque Id
                    for (i in 0..(id!!.size) - 1) {
                        val idInt = id[i].toInt()
                        val listLigne = Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                            db, uid, password,
                            "ligne.ordre.travail", "search_read",
                            Arrays.asList(
                                Arrays.asList(
                                    Arrays.asList("ligne_lot_id", "=", idInt)
                                )
                            ),
                            object : HashMap<Any, Any>() {
                                init {
                                    put(
                                        "fields",
                                        Arrays.asList("name","num", "unite", "qte_realise")
                                    )
                                }
                            }
                        )) as Array<Any>)

                        //liste des champs
                        val jsonArray4 = JSONArray(listLigne)
                        val unite =
                            jsonArray4.getJSONObject(0).getString("unite").toString()
                        var unit = unite.split("\"")[1]
                        var unit2 = unit.split("\"")[0]

                        val num = jsonArray4.getJSONObject(0).getString("num").toString()

                        val name = jsonArray4.getJSONObject(0).getString("name").toString()

                        listLigneOt.add(LigneLotOT(num, name, unit2, "0"))

                    }
                    return listLigneOt
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
}


