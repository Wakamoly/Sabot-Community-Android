package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.models.ClanTagModel
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.activities.LoginActivity
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.RequestHandler
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.yarolegovich.lovelydialog.LovelyCustomDialog
import com.yarolegovich.lovelydialog.LovelyInfoDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AccountSettingsFragment : Fragment() {
    var imageViewProfilePic: CircleImageView? = null
    var saveChanges: CircleImageView? = null
    private var progressDialog: ProgressDialog? = null
    var editProfileLayout: RelativeLayout? = null
    var setProfilePhotoButton: RelativeLayout? = null
    private var clanTagLayout: LinearLayout? = null
    private val websiteInputET: EditText? = null
    private var nintendoIDInputET: EditText? = null
    private var twitterInputET: EditText? = null
    private var discordUserET: EditText? = null
    private var discordInputET: EditText? = null
    private var textViewEmail: EditText? = null
    private var textViewNickname: EditText? = null
    private var textViewDescription: EditText? = null
    private var editTextPasswordNew: EditText? = null
    private var editTextPasswordOld: EditText? = null
    private var editTextPasswordOldVerify: EditText? = null
    private var twitchInputET: EditText? = null
    private var mixerInputET: EditText? = null
    private var psnInputET: EditText? = null
    private var xboxInputET: EditText? = null
    private var steamInputET: EditText? = null
    private var instagramInputET: EditText? = null
    private var youtubeInputET: EditText? = null
    private var textViewUsername: TextView? = null
    private var lastNotiText: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var spinnerProgress: ProgressBar? = null
    var userID: String? = null
    var username: String? = null
    var currentTag: String? = null
    var newFrequency: String? = null
    private var closeAccount: Button? = null
    private var notiPlatformButton: Button? = null
    private var frequencyInfoButton: ImageView? = null
    private var clanTagModelArrayList: ArrayList<ClanTagModel>? = null
    private val tags = ArrayList<String>()
    private var clanTagSpinner: Spinner? = null
    private var frequencySpinner: Spinner? = null
    private var mCtx: Context? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val editProfileRootView = inflater.inflate(R.layout.fragment_editprofile, null)
        val backArrow = editProfileRootView.findViewById<ImageView>(R.id.backArrow)
        twitchInputET = editProfileRootView.findViewById(R.id.twitchInputET)
        mixerInputET = editProfileRootView.findViewById(R.id.mixerInputET)
        psnInputET = editProfileRootView.findViewById(R.id.psnInputET)
        xboxInputET = editProfileRootView.findViewById(R.id.xboxInputET)
        steamInputET = editProfileRootView.findViewById(R.id.steamInputET)
        saveChanges = editProfileRootView.findViewById(R.id.saveChanges)
        editTextPasswordNew = editProfileRootView.findViewById(R.id.passwordNew)
        editTextPasswordOld = editProfileRootView.findViewById(R.id.password1)
        editTextPasswordOldVerify = editProfileRootView.findViewById(R.id.password2)
        editProfileLayout = editProfileRootView.findViewById(R.id.editProfileLayout)
        setProfilePhotoButton = editProfileRootView.findViewById(R.id.changeProfilePhoto)
        mProgressBar = editProfileRootView.findViewById(R.id.profileEditProgressBar)
        textViewNickname = editProfileRootView.findViewById(R.id.edit_nickname)
        textViewUsername = editProfileRootView.findViewById(R.id.editTVusername)
        textViewDescription = editProfileRootView.findViewById(R.id.description)
        imageViewProfilePic = editProfileRootView.findViewById(R.id.profile_photo)
        textViewEmail = editProfileRootView.findViewById(R.id.email)
        clanTagSpinner = editProfileRootView.findViewById(R.id.clanTagSpinner)
        spinnerProgress = editProfileRootView.findViewById(R.id.spinnerProgress)
        clanTagLayout = editProfileRootView.findViewById(R.id.clanTagLayout)
        closeAccount = editProfileRootView.findViewById(R.id.closeAccount)
        discordInputET = editProfileRootView.findViewById(R.id.discordInputET)
        frequencyInfoButton = editProfileRootView.findViewById(R.id.frequencyInfoButton)
        frequencySpinner = editProfileRootView.findViewById(R.id.frequencySpinner)
        lastNotiText = editProfileRootView.findViewById(R.id.lastNotiText)
        youtubeInputET = editProfileRootView.findViewById(R.id.youtubeInputET)
        instagramInputET = editProfileRootView.findViewById(R.id.instagramInputET)
        notiPlatformButton = editProfileRootView.findViewById(R.id.notiPlatformButton)
        discordUserET = editProfileRootView.findViewById(R.id.discordUserET)
        twitterInputET = editProfileRootView.findViewById(R.id.twitterInputET)
        nintendoIDInputET = editProfileRootView.findViewById(R.id.nintendoIDInputET)
        //websiteInputET = editProfileRootView.findViewById(R.id.websiteInputET);
        mCtx = activity
        progressDialog = ProgressDialog(mCtx)
        userID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        username = SharedPrefManager.getInstance(mCtx!!)!!.username
        currentTag = ""
        closeAccount?.setOnClickListener {
            val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> if (mCtx is FragmentContainer) { closeAccountAction() }
                    DialogInterface.BUTTON_NEGATIVE -> { }
                }
            }
            val builder = AlertDialog.Builder(mCtx, R.style.AlertDialogStyle)
            builder.setMessage(R.string.closing_account).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
        }
        notiPlatformButton?.setOnClickListener {
            val li = LayoutInflater.from(mCtx)
            val dialogView = li.inflate(R.layout.dialog_noti_platforms, null)
            val playstationcb = dialogView.findViewById<CheckBox>(R.id.playstation)
            val xboxcb = dialogView.findViewById<CheckBox>(R.id.xbox)
            val steamcb = dialogView.findViewById<CheckBox>(R.id.steam)
            val pccb = dialogView.findViewById<CheckBox>(R.id.pc)
            val mobilecb = dialogView.findViewById<CheckBox>(R.id.mobile)
            val nswitchcb = dialogView.findViewById<CheckBox>(R.id.nswitch)
            val crossplatformcb = dialogView.findViewById<CheckBox>(R.id.crossplatform)
            val othercb = dialogView.findViewById<CheckBox>(R.id.other)
            try {
                val notiPlatformsData = JSONArray(SharedPrefManager.getInstance(mCtx!!)!!.notiPlatforms)
                val objectName = arrayOfNulls<String>(notiPlatformsData.length())
                for (i in 0 until notiPlatformsData.length()) {
                    objectName[i] = notiPlatformsData.getString(i)
                    when (objectName[i]) {
                        "PlayStation" -> {
                            playstationcb.isChecked = true
                            continue
                        }
                        "Xbox" -> {
                            xboxcb.isChecked = true
                            continue
                        }
                        "Steam" -> {
                            steamcb.isChecked = true
                            continue
                        }
                        "PC" -> {
                            pccb.isChecked = true
                            continue
                        }
                        "Mobile" -> {
                            mobilecb.isChecked = true
                            continue
                        }
                        "Switch" -> {
                            nswitchcb.isChecked = true
                            continue
                        }
                        "Cross-Platform" -> {
                            crossplatformcb.isChecked = true
                            continue
                        }
                        "Other" -> othercb.isChecked = true
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val dialog = LovelyCustomDialog(mCtx)
            dialog.setView(dialogView)
                    .setTopColorRes(R.color.green)
                    .setTitle(R.string.get_notifications_for_platforms)
                    .setIcon(R.drawable.ic_friend_add)
                    .setMessage(R.string.noti_platform_message)
                    .setListener(R.id.saveBtn) {
                        val platformListSave = ArrayList<String>()
                        if (playstationcb.isChecked) {
                            platformListSave.add("PlayStation")
                        }
                        if (xboxcb.isChecked) {
                            platformListSave.add("Xbox")
                        }
                        if (steamcb.isChecked) {
                            platformListSave.add("Steam")
                        }
                        if (pccb.isChecked) {
                            platformListSave.add("PC")
                        }
                        if (mobilecb.isChecked) {
                            platformListSave.add("Mobile")
                        }
                        if (nswitchcb.isChecked) {
                            platformListSave.add("Switch")
                        }
                        if (crossplatformcb.isChecked) {
                            platformListSave.add("Cross-Platform")
                        }
                        if (othercb.isChecked) {
                            platformListSave.add("Other")
                        }
                        SharedPrefManager.getInstance(mCtx!!)?.notiPlatforms = platformListSave.toString()
                        dialog.dismiss()
                    }
                    .show()
        }
        frequencySpinner?.setSelection(getIndex(frequencySpinner, SharedPrefManager.getInstance(mCtx!!)!!.notificationFrequency!!))
        lastNotiText?.text = SharedPrefManager.getInstance(mCtx!!)!!.lastNoti
        saveChanges?.setOnClickListener { saveChangesClick("toProfile") }
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        setProfilePhotoButton?.setOnClickListener {
            LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.grey)
                    .setIcon(R.drawable.ic_check)
                    .setTitle(R.string.save_settings_query)
                    .setMessage(R.string.leaving_account_settings)
                    .setPositiveButton(R.string.yes) { saveChangesClick("toProfilePicture") }
                    .setNegativeButton(R.string.no) {
                        val asf: Fragment = UploadProfilePhotoFragment()
                        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                        fragmentTransaction.replace(R.id.fragment_container, asf)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                    .show()
        }
        loadEditProfile()
        return editProfileRootView
    }

    private fun loadEditProfile() {
        val stringRequest = StringRequest(Request.Method.GET, "$ProfileEdit_URL?userid=$userID&username=$username", { response: String? ->
            try {
                val profiletop = JSONArray(response)
                val profiletopObject = profiletop.getJSONObject(0)
                val nickname = profiletopObject.getString("nickname")
                val username = profiletopObject.getString("username")
                val description = profiletopObject.getString("description")
                //String signup_date = profiletopObject.getString("signup_date");
                val profilePic = profiletopObject.getString("profile_pic")
                val email = profiletopObject.getString("email")
                val twitch = profiletopObject.getString("twitch")
                val mixer = profiletopObject.getString("mixer")
                val psn = profiletopObject.getString("psn")
                val xbox = profiletopObject.getString("xbox")
                val steam = profiletopObject.getString("steam")
                val discord = profiletopObject.getString("discord")
                //TODO finish this
                val instagram = profiletopObject.getString("instagram")
                val youtube = profiletopObject.getString("youtube")
                //String website = profiletopObject.getString("website");
                val twitter = profiletopObject.getString("twitter")
                val nintendo = profiletopObject.getString("nintendo")
                val discordUser = profiletopObject.getString("discord_user")
                currentTag = profiletopObject.getString("clantag")
                frequencySpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                        newFrequency = frequencySpinner!!.selectedItem.toString()
                        SharedPrefManager.getInstance(mCtx!!)!!.notificationFrequency = newFrequency
                        Toast.makeText(mCtx, "Publics notification frequency changed to " + SharedPrefManager.getInstance(mCtx!!)!!.notificationFrequency + "!", Toast.LENGTH_SHORT).show()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                frequencyInfoButton!!.setOnClickListener {
                    LovelyInfoDialog(mCtx)
                            .setTopColorRes(R.color.colorPrimary)
                            .setIcon(R.drawable.ic_info_grey_24dp)
                            .setTitle(R.string.publics_frequency_text)
                            .setMessage(R.string.publics_noti_info_message)
                            .show()
                }
                loadClantags()
                twitchInputET!!.setText(twitch)
                mixerInputET!!.setText(mixer)
                psnInputET!!.setText(psn)
                xboxInputET!!.setText(xbox)
                steamInputET!!.setText(steam)
                textViewDescription!!.setText(description)
                val usernameText = "@$username"
                textViewUsername!!.text = usernameText
                textViewNickname!!.setText(nickname)
                textViewEmail!!.setText(email)
                discordInputET!!.setText(discord)
                instagramInputET!!.setText(instagram)
                youtubeInputET!!.setText(youtube)
                discordUserET!!.setText(discordUser)
                //websiteInputET.setText(website);
                twitterInputET!!.setText(twitter)
                nintendoIDInputET!!.setText(nintendo)
                Glide.with(mCtx!!)
                        .load(Constants.BASE_URL + profilePic)
                        .error(R.mipmap.ic_launcher)
                        .into(imageViewProfilePic!!)
                mProgressBar!!.visibility = View.GONE
                editProfileLayout!!.visibility = View.VISIBLE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mCtx, "Network Error!", Toast.LENGTH_SHORT).show() }
        Volley.newRequestQueue(mCtx).add(stringRequest)
    }

    private fun saveChangesClick(action: String) {
        progressDialog!!.setMessage("Saving your settings...")
        progressDialog!!.show()
        val instagram = instagramInputET!!.text.toString().trim { it <= ' ' }
        val youtube = youtubeInputET!!.text.toString().trim { it <= ' ' }
        val discord = discordInputET!!.text.toString().trim { it <= ' ' }
        val discordUser = discordUserET!!.text.toString().trim { it <= ' ' }
        val twitter = twitterInputET!!.text.toString().trim { it <= ' ' }
        val nintendoID = nintendoIDInputET!!.text.toString().trim { it <= ' ' }
        val twitchNew = twitchInputET!!.text.toString().trim { it <= ' ' }
        val mixerNew = mixerInputET!!.text.toString().trim { it <= ' ' }
        val psnNew = psnInputET!!.text.toString().trim { it <= ' ' }
        val xboxNew = xboxInputET!!.text.toString().trim { it <= ' ' }
        val steamNew = steamInputET!!.text.toString().trim { it <= ' ' }
        val email = textViewEmail!!.text.toString().trim { it <= ' ' }
        val bio = textViewDescription!!.text.toString().trim { it <= ' ' }
        val nickname = textViewNickname!!.text.toString().trim { it <= ' ' }
        val username = SharedPrefManager.getInstance(mCtx!!)!!.username
        val spinnertext: String = clanTagSpinner!!.selectedItem.toString()
        val finalspinnertext: String
        finalspinnertext = if (spinnertext != "None" && spinnertext != "null" && spinnertext.isNotEmpty()) {
            spinnertext.substring(1, spinnertext.length - 1)
        } else {
            ""
        }
        var errorText = ""
        var passwordNew = ""
        var passwordOld = ""
        var passwordOldVerify = ""
        if (editTextPasswordNew!!.text.toString().isNotEmpty() && editTextPasswordOld!!.text.toString().isNotEmpty() && editTextPasswordOldVerify!!.text.toString().isNotEmpty()) {
            if (editTextPasswordOld!!.text.toString().isNotEmpty() || editTextPasswordNew!!.text.toString().isNotEmpty() || editTextPasswordOldVerify!!.text.toString().isNotEmpty()) {
                if (editTextPasswordOldVerify!!.text.toString() == editTextPasswordOld!!.text.toString()) {
                    passwordNew = editTextPasswordNew!!.text.toString().trim { it <= ' ' }
                    passwordOld = editTextPasswordOld!!.text.toString().trim { it <= ' ' }
                    passwordOldVerify = editTextPasswordOldVerify!!.text.toString().trim { it <= ' ' }
                } else {
                    progressDialog!!.dismiss()
                    errorText = "1"
                    Toast.makeText(mCtx, "Password verify does not match old password input", Toast.LENGTH_LONG).show()
                }
            } else {
                progressDialog!!.dismiss()
                errorText = "2"
                Toast.makeText(mCtx, "Please fill in all 3 password fields", Toast.LENGTH_LONG).show()
            }
        }
        val finalPasswordNew = passwordNew
        val finalPasswordOld = passwordOld
        val finalPasswordOldVerify = passwordOldVerify
        if (errorText.isEmpty()) {
            val stringRequest: StringRequest = object : StringRequest(Method.POST,
                    ProfileEditSave_URL,
                    Response.Listener { response: String? ->
                        progressDialog!!.dismiss()
                        try {
                            val jsonObject = JSONObject(response!!)
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                            if (jsonObject.getString("error") == "false") {
                                if (action == "toProfile") {
                                    val asf: Fragment = FragmentProfile()
                                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                    fragmentTransaction.replace(R.id.fragment_container, asf)
                                    fragmentTransaction.commit()
                                } else if (action == "toProfilePicture") {
                                    val asf: Fragment = UploadProfilePhotoFragment()
                                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                    fragmentTransaction.replace(R.id.fragment_container, asf)
                                    fragmentTransaction.commit()
                                }
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    },
                    Response.ErrorListener { error: VolleyError ->
                        progressDialog!!.hide()
                        Toast.makeText(mCtx, error.message, Toast.LENGTH_LONG).show()
                    }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["username"] = username!!
                    params["nickname"] = nickname
                    params["email"] = email
                    params["bio"] = bio
                    params["passwordNew"] = finalPasswordNew
                    params["passwordOld"] = finalPasswordOld
                    params["passwordVerify"] = finalPasswordOldVerify
                    params["twitch"] = twitchNew
                    params["mixer"] = mixerNew
                    params["youtube"] = youtube
                    params["discord_server"] = discord
                    params["nintendoid"] = nintendoID
                    params["twitter"] = twitter
                    params["discord_user"] = discordUser
                    params["instagram"] = instagram
                    params["psn"] = psnNew
                    params["xbox"] = xboxNew
                    params["steam"] = steamNew
                    params["clantag"] = finalspinnertext
                    return params
                }
            }
            RequestHandler.getInstance(mCtx!!)!!.addToRequestQueue(stringRequest)
        }
    }

    private fun loadClantags() {
        val stringRequest = StringRequest(Request.Method.GET, "$LOAD_CLAN_TAGS?userid=$userID&username=$username",
                { response: String? ->
                    try {
                        val obj = JSONObject(response!!)
                        println(response)
                        if (obj.optString("error") == "false") {
                            clanTagModelArrayList = ArrayList()
                            if (!obj.has("tags")) {
                                clanTagLayout!!.visibility = View.GONE
                            } else {
                                val dataArray = obj.getJSONArray("tags")
                                for (i in 0 until dataArray.length()) {
                                    val clanTagModel = ClanTagModel()
                                    val dataobj = dataArray.getJSONObject(i)
                                    if (dataobj.getString("tag") != "") {
                                        clanTagModel.tag = "[" + dataobj.getString("tag") + "]"
                                        clanTagModelArrayList?.add(clanTagModel)
                                    }
                                }
                                for (i in clanTagModelArrayList?.indices!!) {
                                    tags.add(clanTagModelArrayList!![i].tag!!)
                                    //println("TAG ADDED: ${clanTagModelArrayList!![i].tag}")
                                }
                                tags.add("None")
                                tags.add("[$currentTag]")
                                val spinnerArrayAdapter = ArrayAdapter(mCtx!!, R.layout.spinner_item, tags)
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
                                clanTagSpinner?.adapter = spinnerArrayAdapter
                                clanTagSpinner?.setSelection(getIndex(clanTagSpinner, "[$currentTag]"))
                                spinnerProgress?.visibility = View.GONE
                                clanTagSpinner?.visibility = View.VISIBLE
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { error: VolleyError -> Toast.makeText(mCtx, error.message, Toast.LENGTH_SHORT).show() }
        val requestQueue = Volley.newRequestQueue(mCtx)
        requestQueue.add(stringRequest)
    }

    private fun closeAccountAction() {
        progressDialog!!.setMessage("Saving your settings...")
        progressDialog!!.show()
        val stringRequest: StringRequest = object : StringRequest(Method.POST,
                CLOSE_ACCOUNT,
                Response.Listener { response: String? ->
                    progressDialog!!.dismiss()
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") == "false") {
                            Toast.makeText(mCtx, R.string.user_closed, Toast.LENGTH_SHORT).show()
                            SharedPrefManager.getInstance(mCtx!!)!!.logout()
                            val toLogin = Intent(mCtx, LoginActivity::class.java)
                            toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            requireActivity().finish()
                            startActivity(toLogin)
                        } else {
                            Toast.makeText(mCtx, R.string.could_not_close_account, Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener { error: VolleyError ->
                    progressDialog!!.hide()
                    Toast.makeText(mCtx, error.message, Toast.LENGTH_LONG).show()
                }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = username!!
                params["user_id"] = userID!!
                params["noti_token"] = SharedPrefManager.getInstance(mCtx!!)!!.fCMToken!!
                return params
            }
        }
        RequestHandler.getInstance(mCtx!!)!!.addToRequestQueue(stringRequest)
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
        private const val CLOSE_ACCOUNT = Constants.ROOT_URL + "user_close_account.php"
        private const val ProfileEdit_URL = Constants.ROOT_URL + "profileedit_api.php"
        private const val ProfileEditSave_URL = Constants.ROOT_URL + "profileeditsave_api.php"
        private const val LOAD_CLAN_TAGS = Constants.ROOT_URL + "load_editprofile_clan_tags.php"
    }
}