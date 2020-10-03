package com.lucidsoftworksllc.sabotcommunity.activities

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.fragments.*
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.PayPalConfig
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import java.lang.Runnable
import java.util.*

@AndroidEntryPoint
class FragmentContainer : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var mDrawerLayout: DrawerLayout? = null
    private var navView: BottomNavigationView? = null
    private var deviceUsername: String? = null
    private var dashContainer: RelativeLayout? = null
    private var requestQueue: RequestQueue? = null
    private lateinit var unreadNotiJob: CompletableJob
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        requestQueue = Volley.newRequestQueue(applicationContext)
        unreadNotificationsHandler(1000)
        loadFragment(DashboardFragment())
        if (getIntent().hasExtra("user_to_id")) {
            val userTo = getIntent().getStringExtra("user_to_id")
            if (userTo != null && userTo.isNotEmpty()) {
                val ldf = FragmentProfile()
                val args = Bundle()
                args.putString("UserId", userTo)
                ldf.arguments = args
                supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, ldf)
                        .commit()
            }
        } else if (getIntent().hasExtra("link") && getIntent().getStringExtra("link") != null) {
            val link = getIntent().getStringExtra("link")
            when {
                link!!.contains("post.php?id=") -> {
                    val linkID = link.replace("post.php?id=", "")
                    val ldf = ProfilePostFragment()
                    val args = Bundle()
                    args.putString("id", linkID)
                    ldf.arguments = args
                    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                link.contains("publics_topic.php?id=") -> {
                    val linkID = link.replace("publics_topic.php?id=", "")
                    val ldf = PublicsTopicFragment()
                    val args = Bundle()
                    args.putString("PublicsId", linkID)
                    ldf.arguments = args
                    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                link.contains("clan=") -> {
                    val linkID = link.replace("clan=", "")
                    val ldf = ClanFragment()
                    val args = Bundle()
                    args.putString("ClanId", linkID)
                    ldf.arguments = args
                    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                link.contains("user=") -> {
                    val linkID = link.replace("user=", "")
                    val ldf = FragmentProfile()
                    val args = Bundle()
                    args.putString("Username", linkID)
                    ldf.arguments = args
                    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                link.contains("ptopic=") -> {
                    val linkID = link.replace("ptopic=", "")
                    val ldf = PublicsTopicFragment()
                    val args = Bundle()
                    args.putString("PublicsId", linkID)
                    ldf.arguments = args
                    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                link.contains("pchatroom=") -> {
                    val linkID = link.replace("pchatroom=", "")
                    val ldf = PublicsChatRoom()
                    val args = Bundle()
                    args.putString("GameId", linkID)
                    ldf.arguments = args
                    this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                link.contains("review") -> {
                    val ldf = FragmentProfile()
                    val args = Bundle()
                    args.putString("UserId", SharedPrefManager.getInstance(this)!!.userID)
                    ldf.arguments = args
                    this.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, ldf)
                            .commit()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_container)
        //setSupportActionBar(findViewById(R.id.fragment_container_toolbar))
        navView = findViewById(R.id.nav_view)
        navView?.setOnNavigationItemSelectedListener(this)
        dashContainer = findViewById(R.id.dashContainer)
        deviceUsername = SharedPrefManager.getInstance(this)!!.username
        mDrawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.side_nav_view)
        if (!SharedPrefManager.getInstance(this)!!.isLoggedIn) {
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task: Task<InstanceIdResult?> ->
            if (!task.isSuccessful) {
                Log.w("FCM", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }
            val token = Objects.requireNonNull(task.result)?.token
            if (token != SharedPrefManager.getInstance(applicationContext)!!.fCMToken) {
                SharedPrefManager.getInstance(applicationContext)!!.updateToken(token!!)
            }
        }
        requestQueue = Volley.newRequestQueue(applicationContext)
        unreadNotificationsHandler(1000)
        loadFragment(DashboardFragment())
        if (intent.hasExtra("user_to_id")) {
            val userTo = intent.getStringExtra("user_to_id")
            if (userTo != null && userTo.isNotEmpty()) {
                val ldf = FragmentProfile()
                val args = Bundle()
                args.putString("UserId", userTo)
                ldf.arguments = args
                supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).commit()
            }
        } else if (intent.hasExtra("link") && intent.getStringExtra("link") != null) {
            val link = intent.getStringExtra("link")
            if (link != null) {
                when {
                    link.contains("post.php?id=") -> {
                        val linkID = link.replace("post.php?id=", "")
                        val ldf = ProfilePostFragment()
                        val args = Bundle()
                        args.putString("id", linkID)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    link.contains("publics_topic.php?id=") -> {
                        val linkID = link.replace("publics_topic.php?id=", "")
                        val ldf = PublicsTopicFragment()
                        val args = Bundle()
                        args.putString("PublicsId", linkID)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    link.contains("clan=") -> {
                        val linkID = link.replace("clan=", "")
                        val ldf = ClanFragment()
                        val args = Bundle()
                        args.putString("ClanId", linkID)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    link.contains("user=") -> {
                        val linkID = link.replace("user=", "")
                        val ldf = FragmentProfile()
                        val args = Bundle()
                        args.putString("Username", linkID)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    link.contains("ptopic=") -> {
                        val linkID = link.replace("ptopic=", "")
                        val ldf = PublicsTopicFragment()
                        val args = Bundle()
                        args.putString("PublicsId", linkID)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    link.contains("pchatroom=") -> {
                        val linkID = link.replace("pchatroom=", "")
                        val ldf = PublicsChatRoom()
                        val args = Bundle()
                        args.putString("GameId", linkID)
                        ldf.arguments = args
                        this.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    link.contains("review") -> {
                        val ldf = FragmentProfile()
                        val args = Bundle()
                        args.putString("UserId", SharedPrefManager.getInstance(this)!!.userID)
                        ldf.arguments = args
                        this.supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, ldf)
                                .commit()
                    }
                }
            }
        }
        if (intent.hasExtra("registered")) {
            LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_action_accept)
                    .setTitle(R.string.registered_title)
                    .setMessage(R.string.registered_desc)
                    .setPositiveButton(R.string.yes) { loadFragment(AccountSettingsFragment()) }
                    .setNegativeButton(R.string.no, null)
                    .show()
        } else {
            CoroutineScope(IO).launch {
                currentUpdate()
            }
        }
        navigationView.bringToFront()
        val toggle = ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mDrawerLayout?.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_messages -> {
                    finish()
                    startActivity(Intent(this, ChatActivity::class.java))
                }
                R.id.nav_settings -> {
                    val asf: Fragment = AccountSettingsFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.nav_profile -> {
                    val asf: Fragment = FragmentProfile()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.nav_publics -> {
                    val asf: Fragment = PublicsFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.nav_clans -> {
                    val asf: Fragment = ClansListFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.nav_about -> {
                    val asf: Fragment = AboutFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.nav_contact_us -> {
                    val asf: Fragment = ContactUsFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                R.id.nav_merch -> {
                    val asf: Fragment = MerchFragment()
                    val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
            mDrawerLayout?.closeDrawer(GravityCompat.START)
            true
        }
        val navHeader = navigationView.getHeaderView(0)
        val navBackground = navHeader.findViewById<ImageView>(R.id.img_header_bg)
        val headerUsername = navHeader.findViewById<TextView>(R.id.headerUsername)
        val headerNickname = navHeader.findViewById<TextView>(R.id.headerNickname)
        val versionNumber = navHeader.findViewById<TextView>(R.id.versionNumber)
        versionNumber.text = Constants.APP_VERSION_FINAL
        headerNickname.text = SharedPrefManager.getInstance(this)!!.nickname
        val headerUsernameText = "@" + SharedPrefManager.getInstance(this)!!.username
        headerUsername.text = headerUsernameText
        Glide.with(this).load(Constants.BASE_URL + SharedPrefManager.getInstance(this)!!.profilePic)
                .thumbnail(0.5f)
                .into(navBackground)
    }

    fun openDrawer() {
        mDrawerLayout!!.openDrawer(GravityCompat.START)
    }

    private suspend fun unreadNotifications() {
            if (!shouldGetNotification(this)) {
                try {
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, UNREAD_NUM, Response.Listener { response: String? ->
                        try {
                            val obj = JSONObject(response!!)
                            if (obj.getString("error") == "false") {
                                if (obj.has("num")) {
                                    unreadNotiSuccess(obj.getString("num"))
                                }
                            } else if (obj.getString("banned") == "yes") {
                                userBanned()
                            } else {
                                unreadNotiFailed(obj.getString("message"))
                            }
                            unreadNotificationsHandler(6000)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { }) {
                        override fun getParams(): Map<String, String> {
                            val params: MutableMap<String, String> = HashMap()
                            params["username"] = deviceUsername!!
                            return params
                        }
                    }
                    addToRequestQueue(stringRequest)
                } catch (ignored: Exception) { }
            }
        }

    private fun unreadNotiSuccess(num: String){
        CoroutineScope(Main).launch {
            BottomMenuHelper.showBadge(applicationContext, navView, R.id.navigation_notifications, num)
        }
    }

    private fun unreadNotiFailed(message: String){
        CoroutineScope(Main).launch {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun userBanned(){
        CoroutineScope(Main).launch {
            Toast.makeText(
                    applicationContext,
                    "User Banned/Closed!",
                    Toast.LENGTH_LONG).show()
            val toLogin = Intent(applicationContext, LoginActivity::class.java)
            toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            SharedPrefManager.getInstance(this@FragmentContainer)!!.logout()
            finish()
            startActivity(toLogin)
        }
    }

    fun addToRequestQueue(stringRequest: StringRequest) {
        stringRequest.setShouldCache(false)
        stringRequest.retryPolicy = DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue!!.add(stringRequest)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager
                    .beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .commit()
            return true
        }
        return false
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        var fragment: Fragment? = null
        when (menuItem.itemId) {
            R.id.navigation_dashboard -> fragment = DashboardFragment()
            R.id.navigation_notifications -> {
                BottomMenuHelper.removeBadge(navView, R.id.navigation_notifications)
                fragment = NotificationsFragment()
            }
            R.id.navigation_publics -> fragment = PublicsFragment()
            R.id.navigation_search -> fragment = SearchFragment()
            R.id.navigation_profile -> fragment = FragmentProfile()
        }
        return loadFragment(fragment)
    }

    fun profilePopup(v: View?) {
        val popup = PopupMenu(this, v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.profile_menu, popup.menu)
        popup.setOnMenuItemClickListener { item: MenuItem ->
            if (item.itemId == R.id.menuLogout) {
                val toLogin = Intent(applicationContext, LoginActivity::class.java)
                toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                SharedPrefManager.getInstance(this@FragmentContainer)!!.logout()
                finish()
                startActivity(toLogin)
            }
            if (item.itemId == R.id.menuSettings) {
                val asf: Fragment = AccountSettingsFragment()
                val fragmentTransaction = supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragmentTransaction.replace(R.id.fragment_container, asf)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            true
        }
        popup.show()
    }

    private object BottomMenuHelper {
        fun showBadge(context: Context?, bottomNavigationView: BottomNavigationView?, @IdRes itemId: Int, value: String) {
            removeBadge(bottomNavigationView, itemId)
            val itemView: BottomNavigationItemView = bottomNavigationView!!.findViewById(itemId)
            val badge = LayoutInflater.from(context).inflate(R.layout.snippet_badge_number, bottomNavigationView, false)
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

    fun unreadNotificationsHandler(delay: Int) {
        val chatHandler = Handler()
        val runnableCode = Runnable {
            if(!::unreadNotiJob.isInitialized){
                unreadNotiJob = Job()
            }
            CoroutineScope(Dispatchers.IO + unreadNotiJob).launch {
                unreadNotifications()
            }
        }
        chatHandler.postDelayed(runnableCode, delay.toLong())
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                val confirm: PaymentConfirmation = data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)!!
                try {
                    val paymentDetails = confirm.toJSONObject().toString(4)
                    val jsonDetails = JSONObject(paymentDetails)
                    val responseDetails = jsonDetails.getJSONObject("response")
                    val paymentId = responseDetails.getString("id")
                    val message = """
                        ${getString(R.string.order_message1)}

                        PayPal order ID (screenshot this for your records): $paymentId
                        """.trimIndent()
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, SEND_PAYMENT_CONFIRMATION, Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            if (jsonObject.getString("error") != "false") {
                                Toast.makeText(applicationContext, "Issue with payment! Please contact an admin!", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { Toast.makeText(applicationContext, "Error on Response, please try again later...", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): Map<String, String> {
                            val parms: MutableMap<String, String> = HashMap()
                            parms["payment_details"] = paymentDetails
                            parms["payment_id"] = paymentId
                            parms["username"] = SharedPrefManager.getInstance(applicationContext)!!.username!!
                            parms["email"] = SharedPrefManager.getInstance(applicationContext)!!.email!!
                            return parms
                        }
                    }
                    val requestQueue = Volley.newRequestQueue(applicationContext)
                    requestQueue.add(stringRequest)
                    LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.green)
                            .setIcon(R.drawable.ic_action_accept)
                            .setTitle(R.string.thank_you_for_your_order)
                            .setMessage(message)
                            .setPositiveButton(R.string.ok, null)
                            .show()
                } catch (e: JSONException) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun currentUpdate(){
            val stringRequest: StringRequest = object : StringRequest(Method.POST, GET_CURRENT_VERSION, Response.Listener { response: String? ->
                try {
                    val obj = JSONObject(response!!)
                    if (obj.getString("error") == "false") {
                        if (obj.getInt("version") > Constants.APP_GRADLE_VERSION) {
                            CoroutineScope(Main).launch {
                                LovelyStandardDialog(this@FragmentContainer, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                        .setTopColorRes(R.color.green)
                                        .setButtonsColorRes(R.color.green)
                                        .setIcon(R.drawable.ic_action_report)
                                        .setTitle(R.string.new_update_available)
                                        .setMessage(R.string.gp_redirect)
                                        .setPositiveButton(R.string.yes) {
                                            val uri = Uri.parse("https://play.google.com/store/apps/details?id=com.lucidsoftworksllc.sabotcommunity")
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            applicationContext.startActivity(intent)
                                        }
                                        .setNegativeButton(R.string.no, null)
                                        .show()
                            }
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["version"] = Constants.APP_VERSION_FINAL
                    params["api"] = "android"
                    return params
                }
            }
            addToRequestQueue(stringRequest)
        }

    companion object {
        //Paypal intent request code to track onActivityResult method
        const val PAYPAL_REQUEST_CODE = 123
        private val config = PayPalConfiguration() // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
                // or live (ENVIRONMENT_PRODUCTION)
                .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                .clientId(PayPalConfig.PAYPAL_CLIENT_ID)
        private const val UNREAD_NUM = Constants.ROOT_URL + "get_notification_unread.php"
        const val SEND_PAYMENT_CONFIRMATION = Constants.ROOT_URL + "merch_payment.php"
        private const val GET_CURRENT_VERSION = Constants.ROOT_URL + "get_current_version.php"

        fun shouldGetNotification(context: Context): Boolean {
            val myProcess = RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(myProcess)
            if (myProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND) return true
            val km = context.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
            return Objects.requireNonNull(km).inKeyguardRestrictedInputMode()
        }
    }
}