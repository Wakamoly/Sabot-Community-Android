package com.lucidsoftworksllc.sabotcommunity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class ProfilePostFragment : Fragment() {
    private var mProgressBar: ProgressBar? = null
    private var likeProgress: ProgressBar? = null
    private var urlProgress: ProgressBar? = null
    private var thisUserID: String? = null
    private var thisUsername: String? = null
    private var tvEdited: TextView? = null
    private var urlTitle: TextView? = null
    private var urlDesc: TextView? = null
    private var postToUser: TextView? = null
    private var profilepostpostsnicknameTop: TextView? = null
    private var profilepostpostsusernameTop: TextView? = null
    private var profilepostpostbodyTop: TextView? = null
    private var profilepostpostsdatetimeTop: TextView? = null
    private var profilecommentslikesTop: TextView? = null
    private var textViewNumComments: TextView? = null
    private var profilePostImage: ImageView? = null
    private var likeView: ImageView? = null
    private var likedView: ImageView? = null
    private var platformType2: ImageView? = null
    private var urlImage: ImageView? = null
    private var profileTopicMenu: ImageView? = null
    private var profilepostpostsprofileImageTop: CircleImageView? = null
    private var onlineView: CircleImageView? = null
    private var verifiedView: CircleImageView? = null
    private var commentLayout: LinearLayout? = null
    private var urlPreview: LinearLayout? = null
    private var urlBits: LinearLayout? = null
    private var likesLayout: LinearLayout? = null
    private var submitComment: MaterialRippleLayout? = null
    private var commentEditText: EditText? = null
    private var mCtx: Context? = null
    private var profilePostView: RecyclerView? = null
    private var adapter: PostCommentsAdapter? = null
    private var profilePostsSwipe: SwipeRefreshLayout? = null
    private var profilePostPostList: MutableList<PostCommentRecycler>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val profilePostRootView = inflater.inflate(R.layout.fragment_profile_post, null)
        urlBits = profilePostRootView.findViewById(R.id.urlBits)
        urlDesc = profilePostRootView.findViewById(R.id.urlDesc)
        urlTitle = profilePostRootView.findViewById(R.id.urlTitle)
        urlPreview = profilePostRootView.findViewById(R.id.urlPreview)
        urlImage = profilePostRootView.findViewById(R.id.urlImage)
        urlProgress = profilePostRootView.findViewById(R.id.urlProgress)
        onlineView = profilePostRootView.findViewById(R.id.online)
        verifiedView = profilePostRootView.findViewById(R.id.verified)
        likeView = profilePostRootView.findViewById(R.id.like)
        likedView = profilePostRootView.findViewById(R.id.liked)
        likeProgress = profilePostRootView.findViewById(R.id.likeProgress)
        mProgressBar = profilePostRootView.findViewById(R.id.postProgressBar)
        profilePostImage = profilePostRootView.findViewById(R.id.profilePostImage)
        commentLayout = profilePostRootView.findViewById(R.id.commentLayout)
        submitComment = profilePostRootView.findViewById(R.id.submitComment)
        commentEditText = profilePostRootView.findViewById(R.id.commentEditText)
        textViewNumComments = profilePostRootView.findViewById(R.id.textViewNumComments)
        postToUser = profilePostRootView.findViewById(R.id.postToUser)
        profilepostpostsnicknameTop = profilePostRootView.findViewById(R.id.profileCommentsNickname_top)
        profilepostpostsusernameTop = profilePostRootView.findViewById(R.id.profileCommentsUsername_top)
        profilepostpostbodyTop = profilePostRootView.findViewById(R.id.profileCommentsBody_top)
        profilepostpostsdatetimeTop = profilePostRootView.findViewById(R.id.profileCommentsDateTime_top)
        profilepostpostsprofileImageTop = profilePostRootView.findViewById(R.id.profileCommentsProfile_image_top)
        profilecommentslikesTop = profilePostRootView.findViewById(R.id.profileCommentsLikes_top)
        platformType2 = profilePostRootView.findViewById(R.id.platformType2)
        likesLayout = profilePostRootView.findViewById(R.id.likesLayout)
        profileTopicMenu = profilePostRootView.findViewById(R.id.profileTopicMenu)
        tvEdited = profilePostRootView.findViewById(R.id.tvEdited)
        mCtx = activity
        thisUserID = SharedPrefManager.getInstance(mCtx!!)!!.userID
        thisUsername = SharedPrefManager.getInstance(mCtx!!)!!.username
        profilePostPostList = ArrayList()
        profilePostView = profilePostRootView.findViewById(R.id.profileCommentsPostRecyclerView)
        profilePostView?.setHasFixedSize(true)
        profilePostView?.layoutManager = LinearLayoutManager(activity)
        commentEditText?.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isEmpty()) {
                    submitComment?.isEnabled = false
                    submitComment?.visibility = View.GONE
                } else {
                    submitComment?.isEnabled = true
                    submitComment?.visibility = View.VISIBLE
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {}
        })
        if (!TextUtils.isEmpty(commentEditText?.text)) {
            submitComment?.visibility = View.VISIBLE
        }
        loadprofilePostTopicTop()
        loadprofilePostTopic()
        profilePostsSwipe = profilePostRootView.findViewById(R.id.profilePostsSwipe)
        profilePostsSwipe?.setOnRefreshListener {
            val currentFragment = (mCtx as FragmentActivity?)!!.supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is ProfilePostFragment) {
                val fragTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction()
                fragTransaction.detach(currentFragment)
                fragTransaction.attach(currentFragment)
                fragTransaction.commit()
            }
            profilePostsSwipe?.isRefreshing = false
        }
        profilepostpostsnicknameTop?.requestFocus()
        return profilePostRootView
    }

    private fun submitComment(body: String, added_by: String, user_to: String, image: String, post_id: String?) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, CommentPost_URL, Response.Listener {
            val currentFragment = (mCtx as FragmentActivity?)!!.supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is ProfilePostFragment) {
                val fragTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction()
                fragTransaction.detach(currentFragment)
                fragTransaction.attach(currentFragment)
                fragTransaction.commit()
            }
            profilePostsSwipe!!.isRefreshing = false
            profilepostpostsnicknameTop!!.requestFocus()
        }, Response.ErrorListener {
            mProgressBar!!.visibility = View.GONE
            Toast.makeText(mCtx, "Error on Response, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["added_by"] = added_by
                params["user_to"] = user_to
                params["image"] = image
                params["post_id"] = post_id!!
                params["user_id"] = thisUserID!!
                return params
            }
        }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadprofilePostTopicTop() {
        val postId = requireArguments().getString("id")
        val stringRequest = StringRequest(Request.Method.GET, "$ProfilePost_URL?postID=$postId&username=$thisUsername", { response: String? ->
            try {
                val profilepost = JSONArray(response)
                val profilepostObject = profilepost.getJSONObject(0)
                val id = profilepostObject.getInt("id")
                val type = profilepostObject.getString("type")
                val likes = profilepostObject.getString("likes")
                val body = profilepostObject.getString("body")
                //String added_by = profilepostObject.getString("added_by");
                val userTo = profilepostObject.getString("user_to")
                val dateAdded = profilepostObject.getString("date_added")
                //String user_closed = profilepostObject.getString("user_closed");
                //String deleted = profilepostObject.getString("deleted");
                val image = profilepostObject.getString("image")
                val userId = profilepostObject.getString("user_id")
                val profilePic = profilepostObject.getString("profile_pic")
                val nickname = profilepostObject.getString("nickname")
                val username = profilepostObject.getString("username")
                val commentcount = profilepostObject.getString("commentcount")
                val likedbyuserYes = profilepostObject.getString("likedbyuseryes")
                val online = profilepostObject.getString("online")
                val verified = profilepostObject.getString("verified")
                val usertoId = profilepostObject.getString("userto_id")
                val form = profilepostObject.getString("form")
                val isEdited = profilepostObject.getString("edited")
                if (isEdited == "yes") {
                    tvEdited!!.visibility = View.VISIBLE
                } else {
                    tvEdited!!.visibility = View.GONE
                }
                profileTopicMenu!!.setOnClickListener { v: View? ->
                    val popup = PopupMenu(mCtx, v)
                    val inflater = popup.menuInflater
                    if (username == thisUsername) {
                        inflater.inflate(R.menu.profile_post_owner, popup.menu)
                        popup.setOnMenuItemClickListener { item: MenuItem ->
                            if (item.itemId == R.id.menuDelete) {
                                deletePost(postId)
                            }
                            if (item.itemId == R.id.menuEdit) {
                                val ldf = ProfilePostEditFragment()
                                val args = Bundle()
                                args.putString("id", id.toString())
                                ldf.arguments = args
                                (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                            if (item.itemId == R.id.menuReport) {
                                val ldf = ReportFragment()
                                val args = Bundle()
                                args.putString("context", body)
                                args.putString("type", "post")
                                args.putString("id", id.toString())
                                ldf.arguments = args
                                (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                            true
                        }
                    } else {
                        inflater.inflate(R.menu.profile_post_nonowner, popup.menu)
                        popup.setOnMenuItemClickListener { item: MenuItem ->
                            if (item.itemId == R.id.menuReport) {
                                val ldf = ReportFragment()
                                val args = Bundle()
                                args.putString("context", body)
                                args.putString("type", "post")
                                args.putString("id", id.toString())
                                ldf.arguments = args
                                (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                            true
                        }
                    }
                    popup.show()
                }
                if (userTo != "none") {
                    postToUser!!.visibility = View.VISIBLE
                    when (form) {
                        "user" -> {
                            postToUser!!.text = String.format("to @%s", userTo)
                            postToUser!!.setOnClickListener {
                                val ldf = FragmentProfile()
                                val args = Bundle()
                                args.putString("UserId", usertoId)
                                ldf.arguments = args
                                (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        }
                        "clan" -> {
                            postToUser!!.text = String.format("to [%s]", userTo)
                            postToUser!!.setTextColor(ContextCompat.getColor(mCtx!!, R.color.pin))
                            postToUser!!.setOnClickListener {
                                val ldf = ClanFragment()
                                val args = Bundle()
                                args.putString("ClanId", usertoId)
                                ldf.arguments = args
                                (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        }
                        "event" -> {
                        }
                    }
                }
                when (type) {
                    "Xbox" -> {
                        platformType2!!.setImageResource(R.drawable.icons8_xbox_50)
                        platformType2!!.visibility = View.VISIBLE
                    }
                    "PlayStation" -> {
                        platformType2!!.setImageResource(R.drawable.icons8_playstation_50)
                        platformType2!!.visibility = View.VISIBLE
                    }
                    "Steam" -> {
                        platformType2!!.setImageResource(R.drawable.icons8_steam_48)
                        platformType2!!.visibility = View.VISIBLE
                    }
                    "PC" -> {
                        platformType2!!.setImageResource(R.drawable.icons8_workstation_48)
                        platformType2!!.visibility = View.VISIBLE
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
                            mCtx!!.startActivity(intent)
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
                            Glide.with(mCtx!!)
                                    .load(imageUrl[0])
                                    .error(R.drawable.ic_error)
                                    .into(urlImage!!)
                            if (title[0] != null) {
                                urlTitle!!.text = title[0]
                            } else {
                                urlTitle!!.text = mCtx!!.getString(R.string.no_content)
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
                profilepostpostsnicknameTop!!.text = nickname
                profilepostpostsusernameTop!!.text = String.format("@%s", username)
                profilepostpostbodyTop!!.text = body
                profilepostpostsdatetimeTop!!.text = dateAdded
                profilecommentslikesTop!!.text = likes
                textViewNumComments!!.text = commentcount
                likesLayout!!.setOnClickListener {
                    val asf: Fragment = UserListFragment()
                    val args = Bundle()
                    args.putString("query", "post")
                    args.putString("queryID", id.toString())
                    asf.arguments = args
                    val fragmentTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction()
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
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
                profilepostpostsnicknameTop!!.setOnClickListener {
                    val ldf = FragmentProfile()
                    val args = Bundle()
                    args.putString("UserId", userId)
                    ldf.arguments = args
                    (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                likeView!!.setOnClickListener {
                    likeView!!.visibility = View.GONE
                    likeProgress!!.visibility = View.VISIBLE
                    val stringRequest1: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response1: String? ->
                        val obj: JSONObject
                        try {
                            obj = JSONObject(response1!!)
                            if (!obj.getBoolean("error")) {
                                val newValue = (profilecommentslikesTop!!.text.toString().toInt() + 1).toString()
                                profilecommentslikesTop!!.text = newValue
                                likeProgress!!.visibility = View.GONE
                                likedView!!.visibility = View.VISIBLE
                                likedView!!.isEnabled = false
                                Handler().postDelayed({ likedView!!.isEnabled = true }, 3500)
                            } else {
                                Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                                likeProgress!!.visibility = View.GONE
                                likeView!!.visibility = View.VISIBLE
                                likeView!!.isEnabled = false
                                Handler().postDelayed({ likeView!!.isEnabled = true }, 3000)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(mCtx, "Could not like, please try again later...", Toast.LENGTH_LONG).show()
                        likeProgress!!.visibility = View.GONE
                        likeView!!.visibility = View.VISIBLE
                        likeView!!.isEnabled = false
                        Handler().postDelayed({ likeView!!.isEnabled = true }, 3000)
                    }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["post_id"] = Objects.requireNonNull(postId)
                            params["method"] = "like"
                            params["user_to"] = username
                            params["user_id"] = thisUserID
                            params["username"] = thisUsername
                            return params
                        }
                    }
                    (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest1)
                }
                likedView!!.setOnClickListener {
                    likedView!!.visibility = View.GONE
                    likeProgress!!.visibility = View.VISIBLE
                    val stringRequest1: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response12: String? ->
                        val obj: JSONObject
                        try {
                            obj = JSONObject(response12!!)
                            if (!obj.getBoolean("error")) {
                                val newValue = (profilecommentslikesTop!!.text.toString().toInt() - 1).toString()
                                profilecommentslikesTop!!.text = newValue
                                likeProgress!!.visibility = View.GONE
                                likeView!!.visibility = View.VISIBLE
                                likeView!!.isEnabled = false
                                Handler().postDelayed({ likeView!!.isEnabled = true }, 3500)
                            } else {
                                Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                                likeProgress!!.visibility = View.GONE
                                likedView!!.visibility = View.VISIBLE
                                likedView!!.isEnabled = false
                                Handler().postDelayed({ likedView!!.isEnabled = true }, 3000)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(mCtx, "Could not remove like, please try again later...", Toast.LENGTH_LONG).show()
                        likeProgress!!.visibility = View.GONE
                        likedView!!.visibility = View.VISIBLE
                        likedView!!.isEnabled = false
                        Handler().postDelayed({ likeView!!.isEnabled = true }, 3000)
                    }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["post_id"] = postId
                            params["method"] = "unlike"
                            params["user_to"] = username
                            params["user_id"] = thisUserID
                            params["username"] = thisUsername
                            return params
                        }
                    }
                    (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest1)
                }
                submitComment!!.setOnClickListener { view: View ->
                    val body1 = commentEditText!!.text.toString()
                    val addedBy1 = SharedPrefManager.getInstance(mCtx!!)!!.username
                    val image1 = ""
                    if (body1.isNotEmpty()) {
                        commentLayout!!.visibility = View.GONE
                        mProgressBar!!.visibility = View.VISIBLE
                        submitComment(body1, addedBy1!!, username, image1, postId)
                        commentEditText!!.text.clear()
                        hideKeyboardFrom(mCtx, view)
                    } else {
                        Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                    }
                }
                val profilePic2 = profilePic.substring(0, profilePic.length - 4) + "_r.JPG"
                Glide.with(mCtx!!)
                        .load(Constants.BASE_URL + profilePic2)
                        .into(profilepostpostsprofileImageTop!!)
                if (image.isNotEmpty()) {
                    Glide.with(mCtx!!)
                            .load(Constants.BASE_URL + image).override(1000)
                            .into(profilePostImage!!)
                    profilePostImage!!.setOnClickListener {
                        val asf: Fragment = PhotoViewFragment()
                        val args = Bundle()
                        args.putString("image", image)
                        asf.arguments = args
                        val fragmentTransaction = (mCtx as FragmentActivity?)!!.supportFragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, asf)
                        fragmentTransaction.addToBackStack(null)
                        fragmentTransaction.commit()
                    }
                }
                mProgressBar!!.visibility = View.GONE
                commentLayout!!.visibility = View.VISIBLE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mCtx, "Error on Response: Dashboard Feed", Toast.LENGTH_SHORT).show() }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadprofilePostTopic() {
        val profilePostID = requireArguments().getString("id")
        val stringRequest = StringRequest(Request.Method.GET, "$ProfileComment_URL?userid=$thisUserID&username=$thisUsername&postid=$profilePostID", { response: String? ->
            try {
                val profilePost = JSONArray(response)
                for (i in 0 until profilePost.length()) {
                    val profilePostObject = profilePost.getJSONObject(i)
                    val id = profilePostObject.getString("id")
                    val body = profilePostObject.getString("body")
                    val postedBy = profilePostObject.getString("posted_by")
                    val postedTo = profilePostObject.getString("posted_to")
                    val time = profilePostObject.getString("time")
                    val postId = profilePostObject.getString("post_id")
                    val likes = profilePostObject.getString("likes")
                    val likedBy = profilePostObject.getString("liked_by")
                    val userId = profilePostObject.getString("userid")
                    val profilePic = profilePostObject.getString("profile_pic")
                    val nickname = profilePostObject.getString("nickname")
                    val username = profilePostObject.getString("username")
                    val likedbyuser = profilePostObject.getString("likedbyuseryes")
                    val online = profilePostObject.getString("online")
                    val verified = profilePostObject.getString("verified")
                    val edited = profilePostObject.getString("edited")
                    val profilePostPostResult = PostCommentRecycler(id, body, postedBy, postedTo, time, postId, likes, likedBy, userId, profilePic, nickname, username, likedbyuser, online, verified, edited)
                    profilePostPostList!!.add(profilePostPostResult)
                }
                adapter = PostCommentsAdapter(mCtx!!, profilePostPostList!!)
                profilePostView!!.adapter = adapter
                profilePostsSwipe!!.isRefreshing = false
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error: VolleyError -> Toast.makeText(mCtx, error.message, Toast.LENGTH_SHORT).show() }
        (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun deletePost(postID: String?) {
        LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_error)
                .setTitle(R.string.delete_post_string)
                .setMessage(R.string.confirm)
                .setPositiveButton(R.string.yes) {
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, POST_DELETE, Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            if (jsonObject.getString("error") == "false") {
                                Toast.makeText(mCtx, "Post deleted!", Toast.LENGTH_LONG).show()
                                requireActivity().supportFragmentManager.popBackStackImmediate()
                            } else {
                                Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { Toast.makeText(mCtx, "Network error, please try again later...", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["postid"] = postID
                            params["username"] = thisUsername
                            params["userid"] = thisUserID
                            return params
                        }
                    }
                    (mCtx as FragmentContainer?)!!.addToRequestQueue(stringRequest)
                }
                .setNegativeButton(R.string.no, null)
                .show()
    }

    companion object {
        private const val ProfileComment_URL = Constants.ROOT_URL + "profilePostComments_api.php"
        private const val CommentPost_URL = Constants.ROOT_URL + "profilePostCommentSubmit_api.php"
        private const val LIKE_URL = Constants.ROOT_URL + "post_like.php"
        private const val ProfilePost_URL = Constants.ROOT_URL + "profilePost.php"
        private const val POST_DELETE = Constants.ROOT_URL + "profile_post_action.php/post_delete"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}