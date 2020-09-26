package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.models.PlatformModel
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class EditPublicsTopic : Fragment() {
    private var gamename: String? = null
    private var platform: String? = null
    private var finalPlatform: String? = null
    private var numPlayers: String? = null
    private var gameimage: String? = null
    private var userID: String? = null
    private var username: String? = null
    private var topicId: String? = null
    private var content: String? = null
    private var gameid: String? = null
    private var title: String? = null
    private var mContext: Context? = null
    private var btnSubmit: Button? = null
    private var platformSpinner: Spinner? = null
    private var numPlayersSpinner: Spinner? = null
    private var etOther: EditText? = null
    private var etSubject: EditText? = null
    private var etDescription: EditText? = null
    private var newPublicsTopicProgressBar: ProgressBar? = null
    private var spinnerProgress: ProgressBar? = null
    private var textViewGame: TextView? = null
    private var backArrow: ImageView? = null
    private var newTopicImage: ImageView? = null
    private var submitDetails: LinearLayout? = null
    private var platformArrayList: ArrayList<PlatformModel>? = null
    private val platforms = ArrayList<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newPublicsRootView = inflater.inflate(R.layout.fragment_editpublicstopic, null)
        mContext = activity
        btnSubmit = newPublicsRootView.findViewById(R.id.btnSubmit)
        textViewGame = newPublicsRootView.findViewById(R.id.textViewGame)
        platformSpinner = newPublicsRootView.findViewById(R.id.platformSpinner)
        etOther = newPublicsRootView.findViewById(R.id.etOther)
        etSubject = newPublicsRootView.findViewById(R.id.etSubject)
        etDescription = newPublicsRootView.findViewById(R.id.etDescription)
        numPlayersSpinner = newPublicsRootView.findViewById(R.id.numPlayersSpinner)
        newPublicsTopicProgressBar = newPublicsRootView.findViewById(R.id.newPublicsTopicProgressBar)
        submitDetails = newPublicsRootView.findViewById(R.id.submitDetails)
        newTopicImage = newPublicsRootView.findViewById(R.id.newTopicImage)
        spinnerProgress = newPublicsRootView.findViewById(R.id.spinnerProgress)
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        gamename = requireArguments().getString("gamename")
        gameimage = requireArguments().getString("gameimage")
        gameid = requireArguments().getString("gameid")
        topicId = requireArguments().getString("topic_id")
        content = requireArguments().getString("content")
        title = requireArguments().getString("title")
        numPlayers = requireArguments().getString("num_players")
        textViewGame?.text = gamename
        etDescription?.setText(content)
        etSubject?.setText(title)
        backArrow = newPublicsRootView.findViewById(R.id.backArrow)
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        platform = platformSpinner?.selectedItem.toString()

        numPlayersSpinner?.setSelection(getIndex(numPlayersSpinner, numPlayers!!))

        Glide.with(mContext!!)
                .load(Constants.BASE_URL + gameimage)
                .error(R.mipmap.ic_launcher)
                .into(newTopicImage!!)
        numPlayersSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                numPlayers = numPlayersSpinner?.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        platformSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                platform = platformSpinner?.selectedItem.toString()
                if (platform == "Other") etOther?.visibility = View.VISIBLE
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        btnSubmit?.setOnClickListener {
            val body = etDescription?.text.toString()
            val subject = etSubject?.text.toString()
            finalPlatform = if (platform == "Other") {
                etOther?.text.toString()
            } else {
                platform
            }
            if (body != "" && subject != "" && finalPlatform != "") {
                submitEditPublicsTopic(finalPlatform, numPlayers, topicId, body, subject, userID, username)
            } else {
                Toast.makeText(mContext, "Please fill in each field!", Toast.LENGTH_SHORT).show()
            }
        }
        loadPlatforms()
        return newPublicsRootView
    }

    private fun submitEditPublicsTopic(finalPlatform: String?, numPlayers: String?, topic_id: String?, body: String, subject: String, submitted_by_id: String?, submitted_by: String?) {
        submitDetails!!.visibility = View.GONE
        newPublicsTopicProgressBar!!.visibility = View.VISIBLE
        val stringRequest: StringRequest = object : StringRequest(Method.POST, EDIT_TOPIC, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    Toast.makeText(mContext, "Post edited!", Toast.LENGTH_LONG).show()
                    if (jsonObject.has("topicid")) {
                        if (mContext is FragmentContainer) {
                            val ldf = PublicsTopicFragment()
                            val args = Bundle()
                            args.putString("PublicsId", jsonObject.getString("topicid"))
                            ldf.arguments = args
                            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).commit()
                        }
                    } else {
                        requireActivity().supportFragmentManager.popBackStackImmediate()
                    }
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    newPublicsTopicProgressBar!!.visibility = View.GONE
                    submitDetails!!.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            newPublicsTopicProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Error, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val parms: MutableMap<String, String> = HashMap()
                parms["platform"] = finalPlatform!!
                parms["numplayers"] = numPlayers!!
                parms["topic_id"] = topic_id!!
                parms["submitted_by_id"] = submitted_by_id!!
                parms["body"] = body
                parms["subject"] = subject
                parms["submitted_by"] = submitted_by!!
                parms["gamename"] = gamename!!
                return parms
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadPlatforms() {
        val stringRequest = StringRequest(Request.Method.GET, "$LOAD_GAME_PLATFORMS?userid=$userID&username=$username&gameid=$gameid",
                { response: String? ->
                    try {
                        val obj = JSONObject(response!!)
                        if (obj.optString("error") == "false") {
                            platformArrayList = ArrayList()
                            val dataArray = obj.getJSONArray("platforms")
                            for (i in 0 until dataArray.length()) {
                                val platformModel = PlatformModel()
                                val dataobj = dataArray.getJSONObject(i)
                                if (dataobj.getString("platform") != "") {
                                    platformModel.platform = dataobj.getString("platform")
                                    platformArrayList!!.add(platformModel)
                                }
                            }
                            for (i in platformArrayList!!.indices) {
                                platforms.add(platformArrayList!![i].platform!!)
                            }
                            platforms.add("Other")
                            val spinnerArrayAdapter = ArrayAdapter(mContext!!, R.layout.spinner_item, platforms)
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            platformSpinner!!.adapter = spinnerArrayAdapter
                            platformSpinner!!.setSelection(0)
                            spinnerProgress!!.visibility = View.GONE
                            platformSpinner!!.visibility = View.VISIBLE
                        } else {
                            platformSpinner!!.visibility = View.GONE
                            spinnerProgress!!.visibility = View.GONE
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { error: VolleyError -> Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun getIndex(spinner: Spinner?, myString: String): Int {
        var index = 0
        for (i in 0 until spinner!!.count) {
            if (spinner.getItemAtPosition(i) == myString) {
                index = i
            }
        }
        return index
    }

    companion object {
        private const val EDIT_TOPIC = Constants.ROOT_URL + "publics_topic_edit_submit.php"
        private const val LOAD_GAME_PLATFORMS = Constants.ROOT_URL + "load_game_platforms.php"
    }
}