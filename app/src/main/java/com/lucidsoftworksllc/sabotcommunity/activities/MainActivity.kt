package com.lucidsoftworksllc.sabotcommunity.activities

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.RequestHandler
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private var editTextUsername: EditText? = null
    private var editTextEmail: EditText? = null
    private var editTextPassword: EditText? = null
    private var editTextPassword2: EditText? = null
    private var howdidyoufindus: EditText? = null
    private var progressDialog: ProgressDialog? = null
    private var textViewLogin: TextView? = null
    private var buttonRegister: TextView? = null
    private var termsAndConditionsLinkTV: TextView? = null
    private var privacyLinkTV: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SharedPrefManager.getInstance(this)!!.isLoggedIn()) {
            finish()
            startActivity(Intent(this, FragmentContainer::class.java))
            return
        }
        setContentView(R.layout.activity_main)
        editTextEmail = findViewById(R.id.emailEditText)
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextPassword = findViewById(R.id.passwordEditText)
        editTextPassword2 = findViewById(R.id.password2EditText)
        howdidyoufindus = findViewById(R.id.howdidyoufindus)
        textViewLogin = findViewById(R.id.goToLoginButton)
        termsAndConditionsLinkTV = findViewById(R.id.termsAndConditionsLinkTV)
        privacyLinkTV = findViewById(R.id.privacyLinkTV)
        buttonRegister = findViewById(R.id.buttonRegister)
        progressDialog = ProgressDialog(this)
        buttonRegister?.setOnClickListener(this)
        textViewLogin?.setOnClickListener(this)
        termsAndConditionsLinkTV?.setOnClickListener(this)
        privacyLinkTV?.setOnClickListener(this)
    }

    private fun registerUser() {
        val email = editTextEmail!!.text.toString().trim { it <= ' ' }
        val username = editTextUsername!!.text.toString().trim { it <= ' ' }
        val password = editTextPassword!!.text.toString().trim { it <= ' ' }
        val password2 = editTextPassword2!!.text.toString().trim { it <= ' ' }
        val howdidyoufindustext = howdidyoufindus!!.text.toString().trim { it <= ' ' }
        if (username.length < 4 || username.length > 20) {
            Toast.makeText(applicationContext, "Username must be 4 to 20 characters long!", Toast.LENGTH_LONG).show()
            progressDialog!!.dismiss()
        } else {
            if (password == password2 && password.length >= 3) {
                if (isValidEmail(email)) {
                    progressDialog!!.setMessage("Registering user...")
                    progressDialog!!.show()
                    val stringRequest: StringRequest = object : StringRequest(Method.POST,
                            Constants.URL_REGISTER,
                            Response.Listener { response: String? ->
                                progressDialog!!.dismiss()
                                try {
                                    val jsonObject = JSONObject(response!!)
                                    if (jsonObject.getString("error") == "false") {
                                        sendToLogin(email, password)
                                    } else {
                                        Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            },
                            Response.ErrorListener { error: VolleyError ->
                                progressDialog!!.hide()
                                Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                            }) {
                        override fun getParams(): Map<String, String> {
                            val params: MutableMap<String, String> = HashMap()
                            params["username"] = username
                            params["email"] = email
                            params["password"] = password
                            params["howdidyoufindus"] = howdidyoufindustext
                            return params
                        }
                    }
                    RequestHandler.getInstance(this)!!.addToRequestQueue(stringRequest)
                } else {
                    Toast.makeText(applicationContext, "E-mail not valid!", Toast.LENGTH_LONG).show()
                    progressDialog!!.dismiss()
                }
            } else {
                Toast.makeText(applicationContext, "Passwords do not match or not long enough!", Toast.LENGTH_LONG).show()
                progressDialog!!.dismiss()
            }
        }
    }

    override fun onClick(view: View) {
        if (view === privacyLinkTV) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://app.termly.io/document/privacy-policy/96bf5c01-d39b-496c-a30e-57353b49877c")))
        if (view === buttonRegister) registerUser()
        if (view === textViewLogin) startActivity(Intent(this, LoginActivity::class.java))
        if (view === termsAndConditionsLinkTV) startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sabotcommunity.com/termsandconditions.php")))
    }

    fun sendToLogin(email: String?, password: String?) {
        startActivity(Intent(this, LoginActivity::class.java).putExtra("email", email).putExtra("password", password).putExtra("registered", "yes"))
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