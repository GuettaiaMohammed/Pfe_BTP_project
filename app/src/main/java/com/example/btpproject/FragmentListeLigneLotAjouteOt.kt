package com.example.btpproject

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        if(conn.get()!!.isNotEmpty() || conn.get() != null) {

            lignes = conn.get() as ArrayList<LigneLotOT>?
            if (lignes != null) {
                ligneLotAdapter!!.addAll(lignes)
            }
        }
        listView!!.adapter = ligneLotAdapter



        return view
    }

    fun create(idOT: Int){
        if(lignes != null) {
            for (i in 0..(lignes!!.size) - 1) {
                val num =
                    listView!!.getChildAt(i).findViewById<TextView>(R.id.numLigneLotAjtOTTV).text.toString()
                val name = listView!!.getChildAt(i).findViewById<TextView>(R.id.designLigneLotAjtOTTV)
                    .text.toString()
                val unite =
                    listView!!.getChildAt(i).findViewById<TextView>(R.id.uniteAjtOTTV).text.toString()
                val qte = listView!!.getChildAt(i).findViewById<EditText>(R.id.QteRealiseAjoutOtET)
                    .text.toString()
                val idLL = lignes!!.get(i).id
                val connAjt =
                    AjouterLigneOT().execute(idOT.toString(), num, name, unite, qte, idLot.toString(), idLL)

            }
        }
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
                                Arrays.asList("ligne_lot_ids")
                            )
                        }
                    }
                )) as Array<Any>)


                val listLigneOt = ArrayList<LigneLotOT>()
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
                            val listLigne =
                                Arrays.asList(*models.execute("execute_kw", Arrays.asList(
                                    db, uid, password,
                                    "ligne.lot", "search_read",
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
                                                    "unite"
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

                                val num = jsonArray4.getJSONObject(0).getString("num").toString()

                                val name = jsonArray4.getJSONObject(0).getString("name").toString()


                                listLigneOt.add(LigneLotOT(idInt.toString(), num, name, unit2, "0"))
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

    class AjouterLigneOT : AsyncTask<String, Void,Int?>(){
        val db = "BTP_pfe"
        val username = "admin"
        val password = "pfe_chantier"


        var num:String=""
        var name:String=""
        var qteRealis:String = ""
        var idOT:Int=0
        var idU:Int=0
        var idL:Int=0
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
                idL = infos[5].toInt()
                idLL = infos[6].toInt()

                var id: Int = models.execute(
                    "execute_kw", Arrays.asList(
                        db, uid, password,
                        "ligne.ordre.travail", "create",
                        Arrays.asList(object : HashMap<Any, Any>() {
                            init {
                                put("ordre_travail_id", idOT)
                                put("ligne_lot_id", idLL)
                                put("lot_id", idL)
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
}


