package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ContactUsFragment : Fragment() {
    private var deviceId: String? = null
    private var version: String? = null
    private var model: String? = null
    private var product: String? = null
    private var mContext: Context? = null
    private var btnSubmit: Button? = null
    private var contactUsSpinner: Spinner? = null
    private var etSubject: EditText? = null
    private var etDescription: EditText? = null
    private var contactProgressBar: ProgressBar? = null
    private var contactUsDetails: LinearLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contactUsRootView = inflater.inflate(R.layout.fragment_contact_us, null)
        mContext = activity
        btnSubmit = contactUsRootView.findViewById(R.id.btnSubmit)
        contactUsSpinner = contactUsRootView.findViewById(R.id.contactUsSpinner)
        etSubject = contactUsRootView.findViewById(R.id.etSubject)
        etDescription = contactUsRootView.findViewById(R.id.etDescription)
        contactProgressBar = contactUsRootView.findViewById(R.id.contactProgressBar)
        contactUsDetails = contactUsRootView.findViewById(R.id.contactUsDetails)
        if (arguments != null) {
            if (requireArguments().getString("newpublics") != null) {
                contactUsSpinner?.setSelection(2)
                etSubject?.setText(requireArguments().getString("newpublics"))
                etSubject?.requestFocus()
                if (etSubject?.hasFocus()!!) {
                    val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
            }
        }
        btnSubmit?.setOnClickListener {
            version = Build.VERSION.RELEASE // API Level
            deviceId = Build.DEVICE // Device
            model = Build.MODEL // Model
            product = Build.PRODUCT // Product
            val spinnerText = contactUsSpinner?.selectedItem.toString()
            val body = etDescription?.text.toString()
            val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
            val subject = etSubject?.text.toString()
            val addedById = SharedPrefManager.getInstance(mContext!!)!!.userID
            if (body != "" || subject != "") {
                submitContactRequest(deviceId, version, model, product, spinnerText, body, addedBy!!, subject, addedById!!)
            } else {
                Toast.makeText(mContext, "Please fill in each field!", Toast.LENGTH_SHORT).show()
            }
        }
        return contactUsRootView
    }

    private fun submitContactRequest(deviceId: String?, version: String?, model: String?, product: String?, spinnerText: String, body: String, added_by: String, subject: String, added_by_id: String) {
        contactUsDetails!!.visibility = View.GONE
        contactProgressBar!!.visibility = View.VISIBLE
        val stringRequest: StringRequest = object : StringRequest(Method.POST, SUBMIT_CONTACT_REQ, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    Toast.makeText(mContext, "Thank you for your concern! We will look into your request very soon.", Toast.LENGTH_LONG).show()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    contactProgressBar!!.visibility = View.GONE
                    contactUsDetails!!.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            contactProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Error on Response, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["deviceId"] = deviceId!!
                params["version"] = version!!
                params["model"] = model!!
                params["product"] = product!!
                params["spinnerText"] = spinnerText
                params["body"] = body
                params["added_by"] = added_by
                params["subject"] = subject
                params["added_by_id"] = added_by_id
                params["email"] = SharedPrefManager.getInstance(mContext!!)!!.email!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val SUBMIT_CONTACT_REQ = Constants.ROOT_URL + "contact_us_submit.php"
    }
}