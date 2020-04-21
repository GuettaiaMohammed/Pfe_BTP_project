package com.example.btpproject

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log


import java.net.URL
import java.util.ArrayList
import java.util.HashMap

import de.timroes.axmlrpc.XMLRPCCallback
import de.timroes.axmlrpc.XMLRPCClient

class FonctionsXmlRPC
/********************************************************************************************
 *
 * @param serverAddress
 */
internal constructor() {
    private var url: URL? = null
    private var client: XMLRPCClient? = null

    init {
        try {
            url = URL("http://sogesi.hopto.org:7013/xmlrpc/2/common")
            client = XMLRPCClient(url)
        } catch (ex: Exception) {
            Log.e("ODOO UTILITY: ", ex.message)
        }

    }

    /*********************************************************************************************
     *
     * @param listener
     * @param db
     * @param username
     * @param password
     * @return
     */

    fun login(
        listener: XMLRPCCallback,
        db: String,
        username: String,
        password: String
    ): Long {

        val emptyMap = HashMap<String, Any>()
        return client!!.callAsync(listener, "authenticate", db, username, password, emptyMap)
    }

    /*********************************************************************************************
     * search_read() search data from Odoo and read the records
     * @param listener XMLRPCCallback to receive response from the server asyncly
     * @param db database name
     * @param uid user id integer reteurned from login
     * @param password
     * @param object the object  model name
     * @param conditions search condition
     * @param fields field to fetch
     * @return task id
     */

    fun search_read(
        listener: XMLRPCCallback,
        db: String,
        uid: String,
        password: String,
        `object`: String,
        conditions: List<*>,
        fields: Map<String, List<*>>
    ): Long {
        return client!!.callAsync(
            listener, "execute_kw", db, Integer.parseInt(uid), password,
            `object`, "search_read", conditions, fields
        )
    }

    /*********************************************************************************************
     *
     * @param listener
     * @param db
     * @param uid
     * @param password
     * @param object
     * @param data
     * @return
     */
    fun create(
        listener: XMLRPCCallback,
        db: String,
        uid: String,
        password: String,
        `object`: String,
        data: List<*>
    ): Long {
        return client!!.callAsync(
            listener, "execute_kw", db, Integer.parseInt(uid), password,
            `object`, "create", data
        )
    }

    /*********************************************************************************************
     *
     * @param listener
     * @param db
     * @param uid
     * @param password
     * @param object
     * @param method
     * @param data
     * @return
     */
    fun exec(
        listener: XMLRPCCallback,
        db: String,
        uid: String,
        password: String,
        `object`: String,
        method: String,
        data: List<*>
    ): Long {
        return client!!.callAsync(
            listener, "execute_kw", db, Integer.parseInt(uid), password,
            `object`, method, data
        )
    }

    /*********************************************************************************************
     *
     * @param listener
     * @param db
     * @param uid
     * @param password
     * @param object
     * @param data
     * @return
     */
    fun update(
        listener: XMLRPCCallback,
        db: String,
        uid: String,
        password: String,
        `object`: String,
        data: List<*>
    ): Long {
        return client!!.callAsync(
            listener, "execute_kw", db, Integer.parseInt(uid), password,
            `object`, "write", data
        )
    }

    companion object {

        /*********************************************************************************************
         *
         * @param classObj
         * @param fieldName
         * @return
         */

        fun getMany2One(classObj: Map<String, Any>, fieldName: String): M2OField {

            var fieldId: Int? = 0
            var fieldValue = ""
            val res = M2OField()
            if (classObj[fieldName] is Array<*>) {
                val field = classObj[fieldName] as Array<Any>?
                if (field!!.size > 0) {
                    fieldId = field[0] as Int
                    fieldValue = field[1] as String
                }
            }
            res.id = fieldId
            res.name = fieldValue

            return res
        }

        /*********************************************************************************************
         *
         * @param classObj
         * @param fieldName
         * @return
         */

        fun getOne2Many(classObj: Map<String, Any>, fieldName: String): List<*> {
            val res = ArrayList<Any>()
            val field = classObj[fieldName] as Array<Any>?

            for (i in field!!.indices) {
                res.add(i, field[i])
            }
            return res
        }

        /*********************************************************************************************
         *
         * @param classObj
         * @param fieldName
         * @return
         */
        fun getString(classObj: Map<String, Any>, fieldName: String): String {
            var res = ""
            if (classObj[fieldName] is String) {
                res = classObj[fieldName] as String
            }
            return res
        }

        /*********************************************************************************************
         *
         * @param classObj
         * @param fieldName
         * @return
         */
        fun getDouble(classObj: Map<String, Any>, fieldName: String): Double? {
            var res: Double? = 0.0
            if (classObj[fieldName] is Double) {
                res = classObj[fieldName] as Double?
            }
            return res
        }

        /*********************************************************************************************
         *
         * @param classObj
         * @param fieldName
         * @return
         */
        fun getInteger(classObj: Map<String, Any>, fieldName: String): Int? {
            var res: Int? = 0
            if (classObj[fieldName] is Double) {
                res = classObj[fieldName] as Int?
            }
            return res
        }

        fun MessageDialog(context: Context, msg: String) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, which -> dialog.dismiss() }
                .create().show()
        }
    }
    class M2OField {
        var id: Int? = null
        var name: String? = null
    }
}




