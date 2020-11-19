package com.lucidsoftworksllc.sabotcommunity.others

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import javax.inject.Inject

class SharedPrefManager private constructor(private val mCtx: Context) {
    fun userLogin(userid: String?, username: String?, nickname: String?, email: String?, profilepic: String?, usersfollowed: String?, gamesfollowed: String?, usersfriends: String?, blockarray: String?) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_USER_ID, userid)
        editor.putString(KEY_USER_EMAIL, email)
        editor.putString(KEY_USERNAME, username)
        editor.putString(KEY_NICKNAME, nickname)
        editor.putString(KEY_PROFILE_PIC, profilepic)
        editor.putString(KEY_BLOCKED_ARRAY, blockarray)
        editor.apply()
    }

    fun isLoggedIn() : Boolean {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        return sharedPreferences.getString(KEY_USERNAME, null) != null
    }

    fun isUserBlocked(username: String): Boolean {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val usersBlockedArray = sharedPreferences.getString(KEY_BLOCKED_ARRAY, null)!!
        val blockedarray = usersBlockedArray.split(",".toRegex()).toTypedArray()
        return listOf(*blockedarray).contains(username)
    }

    fun logout() {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val token = sharedPreferences.getString(KEY_FCM_TOKEN, null).toString()
        val userId = sharedPreferences.getString(KEY_USER_ID, null).toString()
        val username = sharedPreferences.getString(KEY_USERNAME, null).toString()
        removefcmtoken(token, userId, username)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
        CoroutineScope(IO).launch { SabotDatabase(mCtx).clearAllTables() }
    }

    private fun removefcmtoken(token : String, userid: String, username: String){
        val stringRequest: StringRequest = object : StringRequest(Method.POST, REMOVE_FCM_TOKEN, Response.Listener {
            println("FCM token revoked $token")
        }, Response.ErrorListener { error: VolleyError -> Log.d("update_token", "onErrorResponse: $error") }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["token"] = token
                params["user_id"] = userid
                params["username"] = username
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mCtx)
        requestQueue.add(stringRequest)
    }

    fun updateToken(fcm_token: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(KEY_FCM_TOKEN, fcm_token)
        editor.apply()
        println("Updating FCM token $fcm_token")
        val stringRequest: StringRequest = object : StringRequest(Method.POST, STORE_FCM_TOKEN, Response.Listener {
            Log.d("SHAREDPREFS", "updateToken: $it")
        }, Response.ErrorListener { error: VolleyError -> Log.d("update_token", "onErrorResponse: $error") }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["token"] = fcm_token
                params["old_token"] = Objects.requireNonNull(sharedPreferences.getString(KEY_FCM_TOKEN, null)!!)
                params["user_id"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)!!)
                params["username"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)!!)
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mCtx)
        requestQueue.add(stringRequest)
    }

    /*private fun networkUpdateToken(){
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, STORE_FCM_TOKEN, Response.Listener {
            println("FCM token updated")
        }, Response.ErrorListener { error: VolleyError -> Log.d("update_token", "onErrorResponse: $error") }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["token"] = Objects.requireNonNull(sharedPreferences.getString(KEY_FCM_TOKEN, null)!!)
                params["user_id"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)!!)
                params["username"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)!!)
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mCtx)
        requestQueue.add(stringRequest)
    }*/

    fun blockUser(username: String) {
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val newBlockKey = sharedPreferences.getString(KEY_BLOCKED_ARRAY, null) + username + ","
        val stringRequest: StringRequest = object : StringRequest(Method.POST, BLOCK_USER_URL, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    val editor = sharedPreferences.edit()
                    editor.putString(KEY_BLOCKED_ARRAY, newBlockKey)
                    editor.apply()
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mCtx, "Could not block, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["new_block_key"] = newBlockKey
                params["user_blocked"] = username
                params["user_id"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)!!)
                params["username"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)!!)
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mCtx)
        requestQueue.add(stringRequest)
    }

    /*void getFriendArray(){
        final SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GET_USER_FRIENDS, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERS_FRIENDS, obj.getString("connections"));
                    editor.apply();
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx,"Could not get connections, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("user_id", Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)));
                parms.put("username", Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)));
                return parms;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
    }*/

    var profilePic: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_PROFILE_PIC, null)
        }
        set(profilePic) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_PROFILE_PIC, profilePic)
            editor.apply()
        }
    val username: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USERNAME, null)
        }
    val userID: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USER_ID, null)
        }
    val nickname: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_NICKNAME, null)
        }
    val fCMToken: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_FCM_TOKEN, null)
        }

    /*public String getUsersFriends(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERS_FRIENDS, null);
    }*/

    var firstPublicsFragment: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_FIRST_PUBLICS_FRAG, null) == null) {
                val editor = sharedPreferences.edit()
                editor.putString(KEY_FIRST_PUBLICS_FRAG, "show")
                editor.apply()
            }
            return sharedPreferences.getString(KEY_FIRST_PUBLICS_FRAG, null)
        }
        set(show) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_FIRST_PUBLICS_FRAG, show)
            editor.apply()
            sharedPreferences.getString(KEY_FIRST_PUBLICS_FRAG, null)
        }

    val email: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            return sharedPreferences.getString(KEY_USER_EMAIL, null)
        }
    var currentPublics: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_DASH_CURRENT_PUBLICS, null) == null) {
                val editor = sharedPreferences.edit()
                editor.putString(KEY_DASH_CURRENT_PUBLICS, "All")
                editor.apply()
            }
            return sharedPreferences.getString(KEY_DASH_CURRENT_PUBLICS, null)
        }
        set(filter) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_DASH_CURRENT_PUBLICS, filter)
            editor.apply()
            sharedPreferences.getString(KEY_DASH_CURRENT_PUBLICS, null)
        }
    var publicsSortBy: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_PUBLICS_SORT_BY, null) == null) {
                val editor = sharedPreferences.edit()
                editor.putString(KEY_PUBLICS_SORT_BY, "Followers")
                editor.apply()
            }
            return sharedPreferences.getString(KEY_PUBLICS_SORT_BY, null)
        }
        set(sort) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_PUBLICS_SORT_BY, sort)
            editor.apply()
            sharedPreferences.getString(KEY_PUBLICS_SORT_BY, null)
        }
    var notificationFrequency: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_NOTI_FREQUENCY, null) == null) {
                val editor = sharedPreferences.edit()
                editor.putString(KEY_NOTI_FREQUENCY, "Always")
                editor.apply()
            }
            return sharedPreferences.getString(KEY_NOTI_FREQUENCY, null)
        }
        set(frequency) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_NOTI_FREQUENCY, frequency)
            editor.apply()
            sharedPreferences.getString(KEY_NOTI_FREQUENCY, null)
        }
    var lastNoti: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_LAST_NOTI, null) == null) {
                val currentTime = Calendar.getInstance().time
                val editor = sharedPreferences.edit()
                editor.putString(KEY_LAST_NOTI, currentTime.toString())
                editor.apply()
            }
            return sharedPreferences.getString(KEY_LAST_NOTI, null)
        }
        set(now) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_LAST_NOTI, now)
            editor.apply()
            sharedPreferences.getString(KEY_LAST_NOTI, null)
        }
    var notiPlatforms: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_NOTI_PLATFORMS, null) == null) {
                val platformList = ArrayList<String>()
                platformList.add("PlayStation")
                platformList.add("Xbox")
                platformList.add("Steam")
                platformList.add("PC")
                platformList.add("Mobile")
                platformList.add("Switch")
                platformList.add("Cross-Platform")
                platformList.add("Other")
                val editor = sharedPreferences.edit()
                editor.putString(KEY_NOTI_PLATFORMS, platformList.toString())
                editor.apply()
            }
            return sharedPreferences.getString(KEY_NOTI_PLATFORMS, null)
        }
        set(platformarray) {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(KEY_NOTI_PLATFORMS, platformarray)
            editor.apply()
        }

    @get:Throws(JSONException::class)
    val shipmentInfo: String?
        get() {
            val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
            if (sharedPreferences.getString(KEY_SHIPMENT_INFO, null) == null) {
                val shipmentInfoObject = JSONObject()
                val shipmentData = JSONObject()
                shipmentData.put("fullname", "")
                shipmentData.put("street", "")
                shipmentData.put("city", "")
                shipmentData.put("state", "")
                shipmentData.put("apt", "")
                shipmentData.put("zip", "")
                shipmentData.put("country", "")
                shipmentInfoObject.put("shipmentInfo", shipmentData)
                val editor = sharedPreferences.edit()
                editor.putString(KEY_SHIPMENT_INFO, shipmentInfoObject.toString())
                editor.apply()
            }
            return sharedPreferences.getString(KEY_SHIPMENT_INFO, null)
        }

    @Throws(JSONException::class)
    fun setShipmentInfo(fullname: String, street: String, city: String, state: String, apt: String, zip: String, country: String): Boolean {
        val result = arrayOfNulls<String>(1)
        result[0] = "True"
        val sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE)
        val stringRequest: StringRequest = object : StringRequest(Method.POST, SET_SHIPMENT_INFO, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (!obj.getBoolean("error")) {
                    val shipmentInfoObject = JSONObject()
                    val shipmentData = JSONObject()
                    shipmentData.put("fullname", fullname)
                    shipmentData.put("street", street)
                    shipmentData.put("city", city)
                    shipmentData.put("state", state)
                    shipmentData.put("apt", apt)
                    shipmentData.put("zip", zip)
                    shipmentData.put("country", country)
                    shipmentInfoObject.put("shipmentInfo", shipmentData)
                    val editor = sharedPreferences.edit()
                    editor.putString(KEY_SHIPMENT_INFO, shipmentInfoObject.toString())
                    editor.apply()
                    result[0] = "True"
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                    result[0] = "yes"
                }
            } catch (e: JSONException) {
                e.printStackTrace()
                result[0] = "yes"
            }
        }, Response.ErrorListener {
            Toast.makeText(mCtx, "Could not save shipment info! Error #3", Toast.LENGTH_LONG).show()
            result[0] = "yes"
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["user_id"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)!!)
                params["username"] = Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)!!)
                params["fullname"] = fullname
                params["street"] = street
                params["city"] = city
                params["state"] = state
                params["apt"] = apt
                params["zip"] = zip
                params["country"] = country
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mCtx)
        requestQueue.add(stringRequest)
        return java.lang.Boolean.parseBoolean(result[0])
    }

    companion object {
        private var mInstance: SharedPrefManager? = null
        private const val BLOCK_USER_URL = Constants.ROOT_URL + "user_block.php"
        private const val SET_SHIPMENT_INFO = Constants.ROOT_URL + "sharedprefs_saveshipmentinfo.php"
        private const val STORE_FCM_TOKEN = Constants.ROOT_URL + "messages.php/storefcmtoken"
        private const val REMOVE_FCM_TOKEN = Constants.ROOT_URL + "messages.php/removefcmtoken"
        private const val SHARED_PREF_USER = "userpref"
        private const val KEY_USERNAME = "username"
        private const val KEY_NICKNAME = "nickname"
        private const val KEY_USER_EMAIL = "useremail"
        private const val KEY_PROFILE_PIC = "profilepic"
        private const val KEY_USER_ID = "userid"
        private const val KEY_FCM_TOKEN = "fcmtoken"
        private const val KEY_BLOCKED_ARRAY = "blockedarray"
        private const val KEY_DASH_CURRENT_PUBLICS = "currentpublics"
        private const val KEY_FIRST_PUBLICS_FRAG = "firstpublics"
        private const val KEY_NOTI_FREQUENCY = "notifrequency"
        private const val KEY_LAST_NOTI = "lastnoti"
        private const val KEY_SHIPMENT_INFO = "shipmentinfo"
        private const val KEY_PUBLICS_SORT_BY = "publicssortby"
        private const val KEY_NOTI_PLATFORMS = "notiplatforms"
        @JvmStatic
        @Synchronized
        fun getInstance(context: Context): SharedPrefManager? {
            if (mInstance == null) {
                mInstance = SharedPrefManager(context)
            }
            return mInstance
        }
    }
}