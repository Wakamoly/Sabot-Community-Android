package com.lucidsoftworksllc.sabotcommunity.activities

import android.animation.Animator
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.RequestHandler
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class LoginActivity : FragmentActivity(), View.OnClickListener {
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var progressDialog: ProgressDialog? = null
    private var bookIconImageView: ImageView? = null
    private var bookITextView: TextView? = null
    private var buttonLogin: TextView? = null
    private var registerButton: TextView? = null
    private var forgotPassButton: TextView? = null
    private var loadingProgressBar: ProgressBar? = null
    private var rootView: RelativeLayout? = null
    private var threeminutewalkthrough: LinearLayout? = null
    private var afterAnimationView: LinearLayout? = null
    private lateinit var logInJob: CompletableJob

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SharedPrefManager.getInstance(applicationContext)!!.isLoggedIn) {
            finish()
            startActivity(Intent(this, FragmentContainer::class.java))
            return
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)
        initViews()
        if (intent.hasExtra("email") && intent.hasExtra("password") && intent.hasExtra("registered")) {
            val email: String = intent.getStringExtra("email")!!
            val password: String = intent.getStringExtra("password")!!
            val registered: String = intent.getStringExtra("registered")!!
            if (email.isNotEmpty() && password.isNotEmpty() && registered == "yes") {
                progressDialog!!.show()
                if(!::logInJob.isInitialized){
                    logInJob = Job()
                    CoroutineScope(IO + logInJob).launch {
                        loginUser(email, password, "yes")
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            object : CountDownTimer(10000, 5000) {
                override fun onTick(millisUntilFinished: Long) {
                    bookITextView!!.visibility = View.GONE
                    loadingProgressBar!!.visibility = View.GONE
                    rootView!!.setBackgroundColor(ContextCompat.getColor(this@LoginActivity, R.color.colorPrimary))
                    // logoImageView.setImageResource(R.drawable.logo);
                    startAnimation()
                }

                override fun onFinish() {}
            }.start()
        }
    }

    private fun initViews() {
        bookIconImageView = findViewById(R.id.logoImageView)
        bookITextView = findViewById(R.id.loadingText)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        rootView = findViewById(R.id.activity_login_view)
        afterAnimationView = findViewById(R.id.afterAnimationView)
        forgotPassButton = findViewById(R.id.forgotPassButton)
        editTextEmail = findViewById(R.id.emailEditText)
        editTextPassword = findViewById(R.id.passwordEditText)
        buttonLogin = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait...")
        threeminutewalkthrough = findViewById(R.id.threeminutewalkthrough)
        threeminutewalkthrough?.visibility = View.GONE
        buttonLogin?.setOnClickListener(this)
        registerButton?.setOnClickListener(this)
        forgotPassButton?.setOnClickListener(this)
    }

    private fun startAnimation() {
        val viewPropertyAnimator = bookIconImageView!!.animate()
        viewPropertyAnimator.x(50f)
        viewPropertyAnimator.y(100f)
        viewPropertyAnimator.duration = 1500
        viewPropertyAnimator.setListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                afterAnimationView!!.visibility = View.VISIBLE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private suspend fun loginUser(email: String, password: String, isNewUser: String){
        if (isValidEmail(email)) {
            val stringRequest: StringRequest = object : StringRequest(
                    Method.POST,
                    Constants.URL_LOGIN,
                    Response.Listener { response: String? ->
                        try {
                            val obj = JSONObject(response!!)
                            if (!obj.getBoolean("error")) {
                                loginSuccess(obj, isNewUser)
                            } else {
                                loginFailed(obj.getString("message"))
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            loginFailed("Login Failed!")
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        loginFailed(error.message!!)
                    }
            ) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["email"] = email
                    params["password"] = password
                    return params
                }
            }
            RequestHandler.getInstance(this)!!.addToRequestQueue(stringRequest)
        } else {
            loginFailed("E-mail not valid!")
        }
    }

    private fun loginFailed(error: String){
        if(logInJob.isActive || logInJob.isCompleted){
            logInJob.cancel(CancellationException("Cancelling job"))
        }
        CoroutineScope(Main).launch {
            progressDialog!!.dismiss()
            Toast.makeText(applicationContext, error, Toast.LENGTH_LONG).show()
        }
    }

    private fun loginSuccess(obj: JSONObject, isNewUser: String){
        CoroutineScope(Main).launch {
            progressDialog!!.dismiss()
            SharedPrefManager.getInstance(applicationContext)!!
                    .userLogin(
                            obj.getString("id"),
                            obj.getString("username"),
                            obj.getString("nickname"),
                            obj.getString("email"),
                            obj.getString("profilepic"),
                            obj.getString("usersfollowed"),
                            obj.getString("gamesfollowed"),
                            obj.getString("usersfriends"),
                            obj.getString("blockedarray")
                    )
            val toDash: Intent = if (isNewUser == "yes"){
                Intent(this@LoginActivity, FragmentContainer::class.java).putExtra("registered", "yes")
            }else{
                Intent(this@LoginActivity, FragmentContainer::class.java)
            }
            toDash.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            finish()
            startActivity(toDash)
        }
    }

    override fun onClick(view: View) {
        if (view === buttonLogin) {
            val email = editTextEmail!!.text.toString().trim { it <= ' ' }
            val pass = editTextPassword!!.text.toString().trim { it <= ' ' }
            if (email.length > 1 || pass.length > 1) {
                progressDialog!!.show()
                if(!::logInJob.isInitialized){
                    logInJob = Job()
                    CoroutineScope(IO + logInJob).launch {
                        loginUser(email, pass, "no")
                    }
                }
            } else {
                Toast.makeText(applicationContext, "E-mail or pass not valid!", Toast.LENGTH_LONG).show()
            }
        }
        /*if (view == threeminutewalkthrough) {
            startActivity(new Intent(this, Walkthrough.class));
        }*/
        if (view === registerButton) {
            startActivity(Intent(this, MainActivity::class.java))
        }
        if (view === forgotPassButton) {
            startActivity(Intent(this, ForgotPassActivity::class.java))
        }
    }

    companion object {
        fun isValidEmail(target: CharSequence?): Boolean {
            return !TextUtils.isEmpty(target!!) && Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(target).matches()
        }
    }
}