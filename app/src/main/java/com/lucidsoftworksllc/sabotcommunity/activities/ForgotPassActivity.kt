package com.lucidsoftworksllc.sabotcommunity.activities

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.google.android.material.textfield.TextInputEditText
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.RequestHandler
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class ForgotPassActivity : FragmentActivity() {
    private var emailEditText: TextInputEditText? = null
    private var usernameEditText: TextInputEditText? = null
    private var progressDialog: ProgressDialog? = null
    private var forgotButton: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_forgot_pass)
        progressDialog = ProgressDialog(this)
        emailEditText = findViewById(R.id.emailForgotEditText)
        usernameEditText = findViewById(R.id.usernameForgotEditText)
        forgotButton = findViewById(R.id.forgotButton)
        forgotButton?.setOnClickListener {
            val email = Objects.requireNonNull(emailEditText?.text).toString().trim { it <= ' ' }
            val username = Objects.requireNonNull(usernameEditText?.text).toString().trim { it <= ' ' }
            forgotPassword(email, username)
        }
    }

    private fun forgotPassword(email: String, username: String) {
        if (username.length < 4 || username.length > 20) {
            Toast.makeText(applicationContext, "Username must be 4 to 20 characters long!", Toast.LENGTH_LONG).show()
            progressDialog!!.dismiss()
        } else {
            if (isValidEmail(email)) {
                progressDialog!!.setMessage("Checking credentials...")
                progressDialog!!.show()
                val stringRequest: StringRequest = object : StringRequest(Method.POST,
                        URL_FORGOT_PASS,
                        Response.Listener { response: String? ->
                            progressDialog!!.dismiss()
                            try {
                                val jsonObject = JSONObject(response!!)
                                if (!jsonObject.getBoolean("error")) {
                                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                                    finish()
                                    Toast.makeText(applicationContext, "Success, check your emails!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(applicationContext, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }, Response.ErrorListener { error: VolleyError ->
                    progressDialog!!.hide()
                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_LONG).show()
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["username"] = username
                        params["email"] = email
                        return params
                    }
                }
                RequestHandler.getInstance(this)!!.addToRequestQueue(stringRequest)
            } else {
                Toast.makeText(applicationContext, "E-mail not valid!", Toast.LENGTH_LONG).show()
                progressDialog!!.dismiss()
            }
        }
    }

    companion object {
        const val URL_FORGOT_PASS: String = Constants.ROOT_URL + "password_recovery.php"
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