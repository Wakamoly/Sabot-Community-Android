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
import com.lucidsoftworksllc.sabotcommunity.models.PlatformModel
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class NewPublicsTopic : Fragment() {
    private var gameid: String? = null
    private var gamename: String? = null
    private var platform: String? = null
    private var finalPlatform: String? = null
    private var numPlayers: String? = null
    private var whenText: String? = null
    private var gameimage: String? = null
    private var userID: String? = null
    private var username: String? = null
    private var mContext: Context? = null
    private var playingNowCheck: CheckBox? = null
    private var btnSubmit: Button? = null
    private var platformSpinner: Spinner? = null
    private var whenSpinner: Spinner? = null
    private var numPlayersSpinner: Spinner? = null
    private var etOther: EditText? = null
    private var etSubject: EditText? = null
    private var etDescription: EditText? = null
    private var newPublicsTopicProgressBar: ProgressBar? = null
    private var spinnerProgress: ProgressBar? = null
    private var textViewGame: TextView? = null
    private var howTo: TextView? = null
    private var whenTextView: TextView? = null
    private var backArrow: ImageView? = null
    private var newTopicImage: ImageView? = null
    private var platformInfo: ImageView? = null
    private var whenInfo: ImageView? = null
    private var playersInfo: ImageView? = null
    private var subjectInfo: ImageView? = null
    private var descInfo: ImageView? = null
    private var submitDetails: LinearLayout? = null
    private var platformArrayList: ArrayList<PlatformModel>? = null
    private val platforms = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newPublicsRootView = inflater.inflate(R.layout.fragment_newpublicstopic, null)
        mContext = activity
        btnSubmit = newPublicsRootView.findViewById(R.id.btnSubmit)
        textViewGame = newPublicsRootView.findViewById(R.id.textViewGame)
        platformSpinner = newPublicsRootView.findViewById(R.id.platformSpinner)
        etOther = newPublicsRootView.findViewById(R.id.etOther)
        etSubject = newPublicsRootView.findViewById(R.id.etSubject)
        whenSpinner = newPublicsRootView.findViewById(R.id.whenSpinner)
        etDescription = newPublicsRootView.findViewById(R.id.etDescription)
        numPlayersSpinner = newPublicsRootView.findViewById(R.id.numPlayersSpinner)
        newPublicsTopicProgressBar = newPublicsRootView.findViewById(R.id.newPublicsTopicProgressBar)
        submitDetails = newPublicsRootView.findViewById(R.id.submitDetails)
        newTopicImage = newPublicsRootView.findViewById(R.id.newTopicImage)
        spinnerProgress = newPublicsRootView.findViewById(R.id.spinnerProgress)
        platformInfo = newPublicsRootView.findViewById(R.id.platformInfo)
        whenInfo = newPublicsRootView.findViewById(R.id.whenInfo)
        playersInfo = newPublicsRootView.findViewById(R.id.playersInfo)
        subjectInfo = newPublicsRootView.findViewById(R.id.subjectInfo)
        descInfo = newPublicsRootView.findViewById(R.id.descInfo)
        howTo = newPublicsRootView.findViewById(R.id.howTo)
        playingNowCheck = newPublicsRootView.findViewById(R.id.playingNowCheck)
        whenTextView = newPublicsRootView.findViewById(R.id.whenTextView)
        userID = SharedPrefManager.getInstance(requireActivity())!!.userID
        username = SharedPrefManager.getInstance(requireActivity())!!.username
        gameid = requireArguments().getString("gameid")
        gamename = requireArguments().getString("gamename")
        gameimage = requireArguments().getString("gameimage")
        textViewGame?.text = gamename
        backArrow = newPublicsRootView.findViewById(R.id.backArrow)
        playingNowCheck?.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                whenTextView?.setText(R.string.keep_post_open)
            } else {
                whenTextView?.setText(R.string.`when`)
            }
        }
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        platform = platformSpinner?.selectedItem.toString()
        numPlayers = numPlayersSpinner?.selectedItem.toString()
        whenText = whenSpinner?.selectedItem.toString()
        Glide.with(mContext!!)
                .load(Constants.BASE_URL + gameimage)
                .error(R.mipmap.ic_launcher)
                .into(newTopicImage!!)
        howTo?.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_info_grey_24dp)
                    .setTitle(R.string.how_does_this_work)
                    .setMessage(R.string.how_it_works)
                    .setPositiveButton(android.R.string.ok) { }
                    .show()
        }
        platformInfo?.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_info_grey_24dp)
                    .setTitle(R.string.platform)
                    .setMessage(R.string.platform_how_it_works)
                    .setPositiveButton(android.R.string.ok) { }
                    .show()
        }
        whenInfo?.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_info_grey_24dp)
                    .setTitle(R.string.`when`)
                    .setMessage(R.string.when_how_it_works)
                    .setPositiveButton(android.R.string.ok) { }
                    .show()
        }
        playersInfo?.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_info_grey_24dp)
                    .setTitle(R.string.players_needed)
                    .setMessage(R.string.players_how_it_works)
                    .setPositiveButton(android.R.string.ok) { }
                    .show()
        }
        subjectInfo?.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_info_grey_24dp)
                    .setTitle(R.string.subject_text)
                    .setMessage(R.string.subject_how_it_works)
                    .setPositiveButton(android.R.string.ok) { }
                    .show()
        }
        descInfo?.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_info_grey_24dp)
                    .setTitle(R.string.description_text)
                    .setMessage(R.string.desc_how_it_works)
                    .setPositiveButton(android.R.string.ok) { }
                    .show()
        }
        numPlayersSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                numPlayers = numPlayersSpinner?.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        whenSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                whenText = whenSpinner?.selectedItem.toString()
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
                submitPublicsTopic(finalPlatform, whenText!!, numPlayers!!, gameid, body, subject, userID, username)
            } else {
                Toast.makeText(mContext, "Please fill in each field!", Toast.LENGTH_SHORT).show()
            }
        }
        loadPlatforms()
        return newPublicsRootView
    }

    private fun submitPublicsTopic(finalPlatform: String?, whenText: String, numPlayers: String, gameid: String?, body: String, subject: String, submitted_by_id: String?, submitted_by: String?) {
        submitDetails!!.visibility = View.GONE
        newPublicsTopicProgressBar!!.visibility = View.VISIBLE
        var isPlayingNow = "no"
        if (playingNowCheck!!.isChecked) {
            isPlayingNow = "yes"
        }
        val finalIsPlayingNow = isPlayingNow
        val stringRequest: StringRequest = object : StringRequest(Method.POST, SUBMIT_TOPIC, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    Toast.makeText(mContext, "New Publics posted!", Toast.LENGTH_LONG).show()
                    if (jsonObject.has("topicid")) {
                        sendToTopic(jsonObject.getString("topicid"))
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
                val params: MutableMap<String, String> = HashMap()
                params["platform"] = finalPlatform!!
                params["whentext"] = whenText
                params["numplayers"] = numPlayers
                params["gameid"] = gameid!!
                params["submitted_by_id"] = submitted_by_id!!
                params["body"] = body
                params["subject"] = subject
                params["submitted_by"] = submitted_by!!
                params["gamename"] = gamename!!
                params["isplayingnow"] = finalIsPlayingNow
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun sendToTopic(topicID: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, TOPIC_NOTIFY, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    Toast.makeText(mContext, "Players notified!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            newPublicsTopicProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Error on notifying!", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topicID"] = topicID
                params["username"] = username!!
                params["gameid"] = gameid!!
                params["gamename"] = gamename!!
                params["platform"] = finalPlatform!!
                params["numplayers"] = numPlayers!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
        if (mContext is FragmentContainer) {
            val ldf = PublicsTopicFragment()
            val args = Bundle()
            args.putString("PublicsId", topicID)
            ldf.arguments = args
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).commit()
        }
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

    companion object {
        private const val SUBMIT_TOPIC = Constants.ROOT_URL + "publics_topic_submit.php"
        private const val TOPIC_NOTIFY = Constants.ROOT_URL + "publics_action.php/post_notify"
        private const val LOAD_GAME_PLATFORMS = Constants.ROOT_URL + "load_game_platforms.php"
    }
}