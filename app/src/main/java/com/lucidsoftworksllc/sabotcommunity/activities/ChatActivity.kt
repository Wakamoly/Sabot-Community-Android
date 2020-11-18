package com.lucidsoftworksllc.sabotcommunity.activities

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.fragments.*
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import java.lang.Runnable
import java.util.*

@AndroidEntryPoint
class ChatActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var deviceUserID: String? = null
    private var deviceUsername: String? = null
    private var requestQueue: RequestQueue? = null
    private var navView: BottomNavigationView? = null
    private lateinit var unreadMessageJob: CompletableJob
    private var canUpdate = false
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        requestQueue = Volley.newRequestQueue(applicationContext)
        canUpdate = true
        if (getIntent().hasExtra("user_to")) {
            val userTo: String? = getIntent().getStringExtra("user_to")
            if (userTo!!.isNotEmpty()) {
                val ldf = MessageFragment()
                val args = Bundle()
                args.putString("user_to", userTo)
                ldf.arguments = args
                this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
            }
        } else if (getIntent().hasExtra("link") && getIntent().getStringExtra("link") != null) {
            val link = getIntent().getStringExtra("link")
            if (link != null && link.isNotEmpty()) {
                if (link.contains("group=")) {
                    val linkfinal = link.replace("group=", "")
                    if (linkfinal.isNotEmpty()) {
                        val ldf = MessageGroupFragment()
                        val args = Bundle()
                        args.putString("group_id", linkfinal)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                    }
                } else if (link.contains("user=")) {
                    val linkfinal = link.replace("user=", "")
                    if (linkfinal.isNotEmpty()) {
                        val ldf = MessageFragment()
                        val args = Bundle()
                        args.putString("user_to", linkfinal)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat_fragment_container)
        //setSupportActionBar(findViewById(R.id.chat_fragment_container))
        navView = findViewById(R.id.chat_nav_view)
        navView!!.setOnNavigationItemSelectedListener(this)
        deviceUsername = SharedPrefManager.getInstance(this)!!.username
        deviceUserID = SharedPrefManager.getInstance(this)!!.userID
        if (!SharedPrefManager.getInstance(this)!!.isLoggedIn()) {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        requestQueue = Volley.newRequestQueue(applicationContext)
        canUpdate = true
        getUnreadMessagesHandler(4000)
        loadFragment(ConvosFragment())
        if (intent.hasExtra("user_to")) {
            val userTo: String? = intent.getStringExtra("user_to")
            if (userTo!!.isNotEmpty()) {
                val ldf = MessageFragment()
                val args = Bundle()
                args.putString("user_to", userTo)
                ldf.arguments = args
                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
            }
        } else if (intent.hasExtra("link") && intent.getStringExtra("link") != null) {
            val link = intent.getStringExtra("link")
            if (link != null && link.isNotEmpty()) {
                if (link.contains("group=")) {
                    val linkfinal = link.replace("group=", "")
                    if (linkfinal.isNotEmpty()) {
                        val ldf = MessageGroupFragment()
                        val args = Bundle()
                        args.putString("group_id", linkfinal)
                        ldf.arguments = args
                        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                    }
                } else if (link.contains("user=")) {
                    val linkfinal = link.replace("user=", "")
                    if (linkfinal.isNotEmpty()) {
                        val ldf = MessageFragment()
                        val args = Bundle()
                        args.putString("user_to", linkfinal)
                        ldf.arguments = args
                        supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                    }
                } else if (link.contains("requests")) {
                    val ldf = MessageRequestsFragment()
                    val args = Bundle()
                    ldf.arguments = args
                    supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
                }
            }
        }


        //TODO: Make on backpressed load convos
        /*else{
            loadFragment(new ConvosFragment());
        }*/

        //TODO: Make unreadmessages() return new messages or just badge?
        /*Timer _Request_Trip_Timer = new Timer();
        _Request_Trip_Timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getUnreadMessages();
            }
        }, 30000, 30000); // First time start after 5 mili second and repeat after 30 seconds*/
    }

    override fun onPause() {
        super.onPause()
        canUpdate = false
    }

    override fun onResume() {
        super.onResume()
        canUpdate = true
    }

    private fun shouldGetNotification(): Boolean {
        return canUpdate
    }

    fun getUnreadMessagesHandler(delay: Int) {
        val chatHandler = Handler()
        val runnableCode = Runnable {
            if(!::unreadMessageJob.isInitialized){
                unreadMessageJob = Job()
            }
            CoroutineScope(Dispatchers.IO + unreadMessageJob).launch {
                unreadMessages()
            }
        }
        chatHandler.postDelayed(runnableCode, delay.toLong())
    }

    fun addToRequestQueue(stringRequest: StringRequest) {
        stringRequest.setShouldCache(false) // no caching url...
        stringRequest.retryPolicy = DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue!!.add(stringRequest)
    }

    private fun unreadMessages() {
            if (shouldGetNotification()) {
                val stringRequest: StringRequest = object : StringRequest(Method.POST, UNREAD_NUM, Response.Listener { response: String? ->
                    try {
                        val obj = JSONObject(response!!)
                        if (obj.getString("error") == "false") {
                            if (obj.has("num")) {
                                CoroutineScope(Main).launch {
                                    BottomMenuHelper.showBadge(applicationContext, navView, R.id.chat_navigation_inbox, obj.getString("num"))
                                }
                            }
                            if (obj.has("requests")) {
                                CoroutineScope(Main).launch {
                                    BottomMenuHelper.showBadge(applicationContext, navView, R.id.chat_navigation_requests, obj.getString("requests"))
                                }
                            }
                        } else if (obj.getString("banned") == "yes") {
                            CoroutineScope(Main).launch {
                                Toast.makeText(
                                        applicationContext,
                                        "User Banned/Closed!",
                                        Toast.LENGTH_LONG).show()
                                val toLogin = Intent(applicationContext, LoginActivity::class.java)
                                toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                SharedPrefManager.getInstance(this@ChatActivity)!!.logout()
                                finish()
                                startActivity(toLogin)
                            }
                        } else {
                            CoroutineScope(Main).launch {
                                Toast.makeText(applicationContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        }
                        getUnreadMessagesHandler(10000)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { Toast.makeText(applicationContext, "Could not get messages, please try again later...", Toast.LENGTH_LONG).show() }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["username"] = deviceUsername!!
                        return params
                    }
                }
                addToRequestQueue(stringRequest)
            }
        }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out).replace(R.id.chat_fragment_container, fragment).commit()
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (menuItem.itemId) {
            R.id.chat_navigation_inbox -> fragment = ConvosFragment()
            R.id.chat_navigation_search -> fragment = NewMessageFragment()
            R.id.chat_navigation_requests -> fragment = MessageRequestsFragment()
        }
        return loadFragment(fragment)
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            finish()
            startActivity(Intent(this, FragmentContainer::class.java))
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private object BottomMenuHelper {
        fun showBadge(context: Context?, bottomNavigationView: BottomNavigationView?, @IdRes itemId: Int, value: String) {
            removeBadge(bottomNavigationView, itemId)
            val itemView: BottomNavigationItemView = bottomNavigationView!!.findViewById(itemId)
            val badge = LayoutInflater.from(context).inflate(R.layout.snippet_badge_message_number, bottomNavigationView, false)
            if (value != "0") {
                val text = badge.findViewById<TextView>(R.id.badge_text_view)
                text.text = value
                itemView.addView(badge)
            }
        }

        fun removeBadge(bottomNavigationView: BottomNavigationView?, @IdRes itemId: Int) {
            val itemView: BottomNavigationItemView = bottomNavigationView!!.findViewById(itemId)
            if (itemView.childCount == 3) {
                itemView.removeViewAt(2)
            }
        }
    }

    companion object {
        private const val UNREAD_NUM = Constants.ROOT_URL + "get_messages_unread.php"
    }
}