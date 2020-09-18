package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ReportFragment : Fragment() {
    private var type: String? = null
    private var idOfReported: String? = null
    private var reason: String? = null
    private var reportedBy: String? = null
    private var contextReported: String? = null
    private var reportedById: String? = null
    private var finalReason: String? = null
    private var mContext: Context? = null
    private var btnSubmit: Button? = null
    private var reportSpinner: Spinner? = null
    private var etSubject: EditText? = null
    private var etDescription: EditText? = null
    private var etOther: EditText? = null
    private var reportProgressBar: ProgressBar? = null
    private var reportDetails: LinearLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contactUsRootView = inflater.inflate(R.layout.fragment_report, null)
        mContext = activity
        btnSubmit = contactUsRootView.findViewById(R.id.btnSubmit)
        reportSpinner = contactUsRootView.findViewById(R.id.reportSpinner)
        etSubject = contactUsRootView.findViewById(R.id.etSubject)
        etDescription = contactUsRootView.findViewById(R.id.etDescription)
        reportProgressBar = contactUsRootView.findViewById(R.id.reportProgressBar)
        reportDetails = contactUsRootView.findViewById(R.id.reportDetails)
        etOther = contactUsRootView.findViewById(R.id.etOther)
        contextReported = requireArguments().getString("context")
        type = requireArguments().getString("type")
        idOfReported = requireArguments().getString("id")
        reportSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                reason = reportSpinner?.selectedItem.toString()
                if (reason == "Other") etOther?.visibility = View.VISIBLE
                btnSubmit?.setOnClickListener {
                    val body = etDescription?.text.toString()
                    reportedBy = SharedPrefManager.getInstance(mContext!!)!!.username
                    reportedById = SharedPrefManager.getInstance(mContext!!)!!.userID
                    val subject = etSubject?.text.toString()
                    finalReason = if (reason == "Other") {
                        etOther?.text.toString()
                    } else {
                        reason
                    }
                    if (body != "" && subject != "" && finalReason != "") {
                        submitReportRequest(type, idOfReported, contextReported, finalReason, reportedBy, body, subject, reportedById)
                    } else {
                        Toast.makeText(mContext, "Please fill in each field!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        return contactUsRootView
    }

    private fun submitReportRequest(type: String?, id_of_reported: String?, context_reported: String?, finalReason: String?, reported_by: String?, body: String, subject: String, reported_by_id: String?) {
        reportDetails!!.visibility = View.GONE
        reportProgressBar!!.visibility = View.VISIBLE
        val stringRequest: StringRequest = object : StringRequest(Method.POST, SUBMIT_REPORT_REQ, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    Toast.makeText(mContext, "Thank you for your concern! We will look into your request very soon.", Toast.LENGTH_LONG).show()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    reportProgressBar!!.visibility = View.GONE
                    reportDetails!!.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            reportProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Error on Response, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["type"] = type!!
                params["id_of_reported"] = id_of_reported!!
                params["context_reported"] = context_reported!!
                params["finalReason"] = finalReason!!
                params["reported_by"] = reported_by!!
                params["body"] = body
                params["subject"] = subject
                params["reported_by_id"] = reported_by_id!!
                params["email"] = SharedPrefManager.getInstance(mContext!!)!!.email!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val SUBMIT_REPORT_REQ = Constants.ROOT_URL + "report_submit.php"
    }
}