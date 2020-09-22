package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class ProfilePostEditFragment : Fragment() {
    private var verifiedView: CircleImageView? = null
    private var onlineView: CircleImageView? = null
    private var urlBits: LinearLayout? = null
    private var urlPreview: LinearLayout? = null
    private var publicsTopicList: RelativeLayout? = null
    private var contentLayout: MaterialRippleLayout? = null
    private var postProgressBar: ProgressBar? = null
    private var urlProgress: ProgressBar? = null
    private var imageProfilenewsView: ImageView? = null
    private var imageViewProfilenewsPic: ImageView? = null
    private var notiType: ImageView? = null
    private var likeView: ImageView? = null
    private var likedView: ImageView? = null
    private var urlImage: ImageView? = null
    private var saveChanges: ImageView? = null
    private var backArrow: ImageView? = null
    private var tvEdited: TextView? = null
    private var textviewaddedBy: TextView? = null
    private var textviewdateAdded: TextView? = null
    private var textviewuserTo: TextView? = null
    private var textViewLikes: TextView? = null
    private var postusernameTop: TextView? = null
    private var textViewNumComments: TextView? = null
    private var urlTitle: TextView? = null
    private var urlDesc: TextView? = null
    private var textViewComments: TextView? = null
    private var textViewLikesText: TextView? = null
    private var userID: String? = null
    private var username: String? = null
    private var textViewBody: EditText? = null
    private var mContext: Context? = null
    private var postID: String? = null
    private var saveButton: Button? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val editProfilePostRootView = inflater.inflate(R.layout.fragment_editprofilepost, null)
        verifiedView = editProfilePostRootView.findViewById(R.id.verified)
        onlineView = editProfilePostRootView.findViewById(R.id.online)
        postProgressBar = editProfilePostRootView.findViewById(R.id.postProgressBar)
        likeView = editProfilePostRootView.findViewById(R.id.like)
        likedView = editProfilePostRootView.findViewById(R.id.liked)
        notiType = editProfilePostRootView.findViewById(R.id.platformType)
        publicsTopicList = editProfilePostRootView.findViewById(R.id.publicsTopicList)
        textviewaddedBy = editProfilePostRootView.findViewById(R.id.textViewProfileName)
        postusernameTop = editProfilePostRootView.findViewById(R.id.postUsername_top)
        textviewuserTo = editProfilePostRootView.findViewById(R.id.textViewToUserName)
        imageViewProfilenewsPic = editProfilePostRootView.findViewById(R.id.imageViewProfilenewsPic)
        imageProfilenewsView = editProfilePostRootView.findViewById(R.id.profileNewsImage)
        textViewBody = editProfilePostRootView.findViewById(R.id.textViewBody)
        textViewLikes = editProfilePostRootView.findViewById(R.id.textViewNumLikes)
        textviewdateAdded = editProfilePostRootView.findViewById(R.id.profileCommentsDateTime_top)
        textViewNumComments = editProfilePostRootView.findViewById(R.id.textViewNumComments)
        urlPreview = editProfilePostRootView.findViewById(R.id.urlPreview)
        urlProgress = editProfilePostRootView.findViewById(R.id.urlProgress)
        urlImage = editProfilePostRootView.findViewById(R.id.urlImage)
        urlTitle = editProfilePostRootView.findViewById(R.id.urlTitle)
        urlDesc = editProfilePostRootView.findViewById(R.id.urlDesc)
        urlBits = editProfilePostRootView.findViewById(R.id.urlBits)
        textViewComments = editProfilePostRootView.findViewById(R.id.textViewComments)
        textViewLikesText = editProfilePostRootView.findViewById(R.id.textViewLikes)
        contentLayout = editProfilePostRootView.findViewById(R.id.contentLayout)
        tvEdited = editProfilePostRootView.findViewById(R.id.tvEdited)
        saveButton = editProfilePostRootView.findViewById(R.id.saveButton)
        saveChanges = editProfilePostRootView.findViewById(R.id.saveChanges)
        backArrow = editProfilePostRootView.findViewById(R.id.backArrow)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        postID = requireArguments().getString("id")
        textViewBody?.requestFocus()
        if (textViewBody?.hasFocus()!!) {
            val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
        saveChanges?.setOnClickListener { savePost() }
        saveButton?.setOnClickListener { savePost() }
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        loadprofilePost()
        return editProfilePostRootView
    }

    private fun savePost() {
        saveChanges!!.visibility = View.GONE
        saveButton!!.visibility = View.GONE
        postProgressBar!!.visibility = View.VISIBLE
        val view = requireActivity().currentFocus
        if (view != null) {
            val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        val body = textViewBody!!.text.toString()
        val stringRequest: StringRequest = object : StringRequest(Method.POST, POST_EDIT_SAVE, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    Toast.makeText(mContext, "Saved!", Toast.LENGTH_LONG).show()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                    saveChanges!!.visibility = View.VISIBLE
                    saveButton!!.visibility = View.VISIBLE
                    postProgressBar!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            saveChanges!!.visibility = View.VISIBLE
            saveButton!!.visibility = View.VISIBLE
            postProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Network error, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["postid"] = postID!!
                params["username"] = username!!
                params["userid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadprofilePost() {
        val stringRequest = StringRequest(Request.Method.GET, "$ProfilePost_URL?postID=$postID&username=$username&userid=$userID", { response: String? ->
            try {
                val profilepost = JSONArray(response)
                val profilepostObject = profilepost.getJSONObject(0)

                //int id = profilepostObject.getInt("id");
                val type = profilepostObject.getString("type")
                val likes = profilepostObject.getString("likes")
                val body = profilepostObject.getString("body")
                val userTo = profilepostObject.getString("user_to")
                val dateAdded = profilepostObject.getString("date_added")
                val image = profilepostObject.getString("image")
                val profilePic = profilepostObject.getString("profile_pic")
                val nickname = profilepostObject.getString("nickname")
                val username = profilepostObject.getString("username")
                val commentcount = profilepostObject.getString("commentcount")
                val likedbyuserYes = profilepostObject.getString("likedbyuseryes")
                val online = profilepostObject.getString("online")
                val verified = profilepostObject.getString("verified")
                val form = profilepostObject.getString("form")
                if (userTo != "none") {
                    textviewuserTo!!.visibility = View.VISIBLE
                    when (form) {
                        "user" -> textviewuserTo!!.text = String.format("to @%s", userTo)
                        "clan" -> {
                            textviewuserTo!!.text = String.format("to [%s]", userTo)
                            textviewuserTo!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.pin))
                        }
                        "event" -> {
                        }
                    }
                }
                when (type) {
                    "Xbox" -> {
                        notiType!!.setImageResource(R.drawable.icons8_xbox_50)
                        notiType!!.visibility = View.VISIBLE
                    }
                    "PlayStation" -> {
                        notiType!!.setImageResource(R.drawable.icons8_playstation_50)
                        notiType!!.visibility = View.VISIBLE
                    }
                    "Steam" -> {
                        notiType!!.setImageResource(R.drawable.icons8_steam_48)
                        notiType!!.visibility = View.VISIBLE
                    }
                    "PC" -> {
                        notiType!!.setImageResource(R.drawable.icons8_workstation_48)
                        notiType!!.visibility = View.VISIBLE
                    }
                }
                val bodybits = body.split("\\s+".toRegex()).toTypedArray()
                for (item in bodybits) {
                    if (Patterns.WEB_URL.matcher(item).matches()) {
                        val finalItem: String = if (!item.contains("http://") && !item.contains("https://")) {
                            "https://$item"
                        } else {
                            item
                        }
                        val imageUrl = arrayOf<String?>(null)
                        val title = arrayOfNulls<String>(1)
                        val desc = arrayOf<String?>(null)
                        urlPreview!!.visibility = View.VISIBLE
                        urlImage!!.setOnClickListener {
                            val uri = Uri.parse(finalItem)
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            mContext!!.startActivity(intent)
                        }
                        Thread( Runnable runnable@ {
                            try {
                                val doc = Jsoup.connect(finalItem).get()
                                val ogTags = doc.select("meta[property^=og:]")
                                if (ogTags.size <= 0) {
                                    return@runnable
                                }
                                val metaOgTitle = doc.select("meta[property=og:title]")
                                if (metaOgTitle != null) {
                                    title[0] = metaOgTitle.attr("content")
                                } else {
                                    title[0] = doc.title()
                                }
                                val metaOgDesc = doc.select("meta[property=og:description]")
                                if (metaOgDesc != null) {
                                    desc[0] = metaOgDesc.attr("content")
                                }
                                val metaOgImage = doc.select("meta[property=og:image]")
                                if (metaOgImage != null) {
                                    imageUrl[0] = metaOgImage.attr("content")
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }).start()
                        //Fucking code wouldn't work any other way than I'm currently capable. Fuck it, have a delay
                        val handler = Handler()
                        handler.postDelayed({
                            Glide.with(mContext!!)
                                    .load(imageUrl[0])
                                    .error(R.drawable.ic_error)
                                    .into(urlImage!!)
                            if (title[0] != null) {
                                urlTitle!!.text = title[0]
                            } else {
                                urlTitle!!.text = mContext!!.getString(R.string.no_content)
                            }
                            if (desc[0] != null) {
                                urlDesc!!.text = desc[0]
                            }
                            urlProgress!!.visibility = View.GONE
                            urlBits!!.visibility = View.VISIBLE
                        }, 5000)
                        break
                    }
                }
                textviewaddedBy!!.text = nickname
                postusernameTop!!.text = String.format("@%s", username)
                textViewBody!!.setText(body)
                textviewdateAdded!!.text = dateAdded
                textViewLikes!!.text = likes
                textViewNumComments!!.text = commentcount
                if (online == "yes") {
                    onlineView!!.visibility = View.VISIBLE
                }
                if (verified == "yes") {
                    verifiedView!!.visibility = View.VISIBLE
                }
                if (likedbyuserYes == "yes") {
                    likeView!!.visibility = View.GONE
                    likedView!!.visibility = View.VISIBLE
                }
                val profilePic2 = profilePic.substring(0, profilePic.length - 4) + "_r.JPG"
                Glide.with(mContext!!)
                        .load(Constants.BASE_URL + profilePic2)
                        .into(imageViewProfilenewsPic!!)
                if (image.isNotEmpty()) {
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + image).override(1000)
                            .into(imageProfilenewsView!!)
                }
                postProgressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Error on Response: Dashboard Feed", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val ProfilePost_URL = Constants.ROOT_URL + "profilePostEdit.php"
        private const val POST_EDIT_SAVE = Constants.ROOT_URL + "profile_post_action.php/post_save"
    }
}