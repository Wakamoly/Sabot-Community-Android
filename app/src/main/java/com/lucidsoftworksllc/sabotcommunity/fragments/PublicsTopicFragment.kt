package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
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
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.R.drawable
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsPostAdapter
import com.lucidsoftworksllc.sabotcommunity.models.PublicsPostRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PublicsTopicFragment : Fragment() {
    private var mProgressBar: ProgressBar? = null
    private var requestProgressBar: ProgressBar? = null
    private var voteProgress: ProgressBar? = null
    private var userID: String? = null
    private var publicsID: String? = null
    private var deviceusername: String? = null
    private var topicTitle: TextView? = null
    private var clantag: TextView? = null
    private var publicsPostTitle: TextView? = null
    private var publicspostsnicknameTop: TextView? = null
    private var publicspostsusernameTop: TextView? = null
    private var publicspostbodyTop: TextView? = null
    private var publicspostsdatetimeTop: TextView? = null
    private var tvWhen: TextView? = null
    private var numPlayersNeeded: TextView? = null
    private var numPlayersAdded: TextView? = null
    private var textviewnumpublicspointsTop: TextView? = null
    private var publicspostsprofileImageTop: CircleImageView? = null
    private var online: CircleImageView? = null
    private var verified: CircleImageView? = null
    private var whenLayout: LinearLayout? = null
    private var topicLayout: LinearLayout? = null
    private var postDeletedScreenContent: LinearLayout? = null
    private var nicknameLayout: LinearLayout? = null
    private var playingNowLayout: LinearLayout? = null
    private var mContext: Context? = null
    private var submitComment: MaterialRippleLayout? = null
    private var publicsImageView: ImageView? = null
    private var platformType: ImageView? = null
    private var publicspostsupvotewhiteTop: ImageView? = null
    private var publicsPostsDownvoteWhite: ImageView? = null
    private var publicspostsupvotegreenTop: ImageView? = null
    private var publicspostsdownvoteredTop: ImageView? = null
    private var publicsTopicMenu: ImageView? = null
    private var noRequests: Button? = null
    private var joinRequests: Button? = null
    private var requestToJoin: Button? = null
    private var requestedToJoin: Button? = null
    private var topicJoined: Button? = null
    private var deleteTopic: Button? = null
    private var topicMembers: Button? = null
    private var topicEdit: Button? = null
    private var newComment: Button? = null
    private var commentEditText: EditText? = null
    private var newCommentLayout: RelativeLayout? = null
    private var publicsView: RecyclerView? = null
    private var adapter: PublicsPostAdapter? = null
    private var publicsSwipe: SwipeRefreshLayout? = null
    private var publicsPostList: MutableList<PublicsPostRecycler>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val publicsRootView = inflater.inflate(R.layout.fragment_publics_topic, null)
        mProgressBar = publicsRootView.findViewById(R.id.topicProgressBar)
        publicsPostTitle = publicsRootView.findViewById(R.id.publicsPostTitle)
        publicspostsnicknameTop = publicsRootView.findViewById(R.id.publicsPostsNickname_top)
        publicspostsusernameTop = publicsRootView.findViewById(R.id.publicsPostsUsername_top)
        publicspostbodyTop = publicsRootView.findViewById(R.id.publicsPostBody_top)
        publicspostsdatetimeTop = publicsRootView.findViewById(R.id.publicsPostsDateTime_top)
        publicspostsprofileImageTop = publicsRootView.findViewById(R.id.publicsPostsProfile_image_top)
        numPlayersNeeded = publicsRootView.findViewById(R.id.numPlayersNeeded)
        numPlayersAdded = publicsRootView.findViewById(R.id.numPlayersAdded)
        topicLayout = publicsRootView.findViewById(R.id.topicLayout)
        tvWhen = publicsRootView.findViewById(R.id.tvWhen)
        whenLayout = publicsRootView.findViewById(R.id.whenLayout)
        platformType = publicsRootView.findViewById(R.id.platformType)
        noRequests = publicsRootView.findViewById(R.id.noRequests)
        joinRequests = publicsRootView.findViewById(R.id.joinRequests)
        requestToJoin = publicsRootView.findViewById(R.id.requestToJoin)
        requestedToJoin = publicsRootView.findViewById(R.id.requestedToJoin)
        topicJoined = publicsRootView.findViewById(R.id.topicJoined)
        textviewnumpublicspointsTop = publicsRootView.findViewById(R.id.textViewNumPublicsPoints_top)
        requestProgressBar = publicsRootView.findViewById(R.id.requestProgressBar)
        submitComment = publicsRootView.findViewById(R.id.submitComment)
        commentEditText = publicsRootView.findViewById(R.id.commentEditText)
        publicspostsdownvoteredTop = publicsRootView.findViewById(R.id.publicsPostsDownvoteRed_top)
        publicspostsupvotegreenTop = publicsRootView.findViewById(R.id.publicsPostsUpvoteGreen_top)
        publicsPostsDownvoteWhite = publicsRootView.findViewById(R.id.publicsPostsDownvoteWhite)
        publicspostsupvotewhiteTop = publicsRootView.findViewById(R.id.publicsPostsUpvoteWhite_top)
        voteProgress = publicsRootView.findViewById(R.id.voteProgress)
        deleteTopic = publicsRootView.findViewById(R.id.deleteTopic)
        postDeletedScreenContent = publicsRootView.findViewById(R.id.postDeletedScreenContent)
        newCommentLayout = publicsRootView.findViewById(R.id.newCommentLayout)
        publicsTopicMenu = publicsRootView.findViewById(R.id.publicsTopicMenu)
        publicsImageView = publicsRootView.findViewById(R.id.publicsImageView)
        nicknameLayout = publicsRootView.findViewById(R.id.nicknameLayout)
        clantag = publicsRootView.findViewById(R.id.clantag)
        topicMembers = publicsRootView.findViewById(R.id.topicMembers)
        online = publicsRootView.findViewById(R.id.online)
        verified = publicsRootView.findViewById(R.id.verified)
        topicTitle = publicsRootView.findViewById(R.id.topicTitle)
        topicEdit = publicsRootView.findViewById(R.id.topicEdit)
        newComment = publicsRootView.findViewById(R.id.newComment)
        playingNowLayout = publicsRootView.findViewById(R.id.playingNowLayout)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        deviceusername = SharedPrefManager.getInstance(mContext!!)!!.username
        publicsID = requireArguments().getString("PublicsId")
        publicsPostList = ArrayList()
        publicsView = publicsRootView.findViewById(R.id.recyclerPublicsTopic)
        publicsView?.setHasFixedSize(true)
        publicsView?.layoutManager = LinearLayoutManager(mContext)
        loadPublicsTopicTop()
        loadPublicsTopic()
        publicsSwipe = publicsRootView.findViewById(R.id.publicsPostsSwipe)
        publicsSwipe?.setOnRefreshListener {
            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is PublicsTopicFragment) {
                val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragTransaction.detach(currentFragment)
                fragTransaction.attach(currentFragment)
                fragTransaction.commit()
            }
            publicsSwipe?.isRefreshing = false
        }
        newComment?.setOnClickListener {
            newCommentLayout?.visibility = View.VISIBLE
            submitComment?.visibility = View.VISIBLE
            commentEditText?.requestFocus()
            if (commentEditText?.hasFocus()!!) {
                val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }
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
        publicsPostTitle?.requestFocus()
        return publicsRootView
    }

    private fun loadPublicsTopicTop() {
        val stringRequest = StringRequest(Request.Method.GET, "$PublicsTopicTop_URL?userid=$userID&username=$deviceusername&topicid=$publicsID", { response: String? ->
            try {
                val publicstop = JSONArray(response)
                val publicsObject = publicstop.getJSONObject(0)
                val deleted = publicsObject.getString("deleted")
                if (deleted == "yes") {
                    postDeletedScreenContent!!.visibility = View.VISIBLE
                } else {
                    val postId = publicsObject.getString("post_id")
                    val postTopic = publicsObject.getString("post_topic")
                    val postContent = publicsObject.getString("post_content")
                    val postDate = publicsObject.getString("post_date")
                    val userId = publicsObject.getString("user_id")
                    val profilePic = publicsObject.getString("profile_pic")
                    val nickname = publicsObject.getString("nickname")
                    val username = publicsObject.getString("username")
                    val numPlayers = publicsObject.getString("num_players")
                    val numAdded = publicsObject.getInt("num_added")
                    val eventDate = publicsObject.getString("event_date")
                    val type = publicsObject.getString("type")
                    val votes = publicsObject.getString("votes")
                    val acceptedArray = publicsObject.getString("accepted_array")
                    val requests = publicsObject.getInt("requests")
                    val vote = publicsObject.getString("vote")
                    val backimage = publicsObject.getString("back_image")
                    val clanTag = publicsObject.getString("clantag")
                    val isOnline = publicsObject.getString("online")
                    val isVerified = publicsObject.getString("verified")
                    val gameName = publicsObject.getString("game_name")
                    val catId = publicsObject.getString("cat_id")
                    val playingNow = publicsObject.getString("playing_now")
                    clantag!!.text = clanTag
                    topicTitle!!.text = postTopic
                    if (playingNow == "yes") {
                        playingNowLayout!!.visibility = View.VISIBLE
                    } else {
                        playingNowLayout!!.visibility = View.GONE
                    }
                    if (isOnline == "yes") {
                        online!!.visibility = View.VISIBLE
                    } else {
                        online!!.visibility = View.GONE
                    }
                    if (isVerified == "yes") {
                        verified!!.visibility = View.VISIBLE
                    } else {
                        verified!!.visibility = View.GONE
                    }
                    if (vote == "up") {
                        publicspostsupvotewhiteTop!!.visibility = View.GONE
                        publicspostsupvotegreenTop!!.visibility = View.VISIBLE
                    } else if (vote == "down") {
                        publicsPostsDownvoteWhite!!.visibility = View.GONE
                        publicspostsdownvoteredTop!!.visibility = View.VISIBLE
                    }
                    val accepted = acceptedArray.split(",".toRegex()).toTypedArray()
                    //if user is topic owner
                    if (username == deviceusername) {
                        topicEdit!!.visibility = View.VISIBLE
                        topicEdit!!.setOnClickListener {
                            val ldf = EditPublicsTopic()
                            val args = Bundle()
                            args.putString("topic_id", postId)
                            args.putString("gamename", gameName)
                            args.putString("gameimage", backimage)
                            args.putString("content", postContent)
                            args.putString("title", postTopic)
                            args.putString("num_players", numPlayers)
                            args.putString("gameid", catId)
                            ldf.arguments = args
                            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        deleteTopic!!.visibility = View.VISIBLE
                        deleteTopic!!.setOnClickListener {
                            val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> if (mContext is FragmentContainer) {
                                        deleteTopic(postId)
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }
                            }
                            val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                            builder.setMessage("Delete Publics?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show()
                        }
                        requestProgressBar!!.visibility = View.GONE
                        if (requests > 0) {
                            joinRequests!!.visibility = View.VISIBLE
                            joinRequests!!.setOnClickListener {
                                val ldf = TopicManagePlayers()
                                val args = Bundle()
                                args.putString("topic_id", postId)
                                args.putString("permission", "admin")
                                ldf.arguments = args
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        } else {
                            noRequests!!.visibility = View.VISIBLE
                        }
                    } else if (listOf(*accepted).contains(deviceusername)) {
                        requestProgressBar!!.visibility = View.GONE
                        if (numAdded > 0) {
                            topicMembers!!.visibility = View.VISIBLE
                            topicMembers!!.setOnClickListener {
                                val ldf = TopicManagePlayers()
                                val args = Bundle()
                                args.putString("topic_id", postId)
                                args.putString("permission", "user")
                                ldf.arguments = args
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        }
                        topicJoined!!.visibility = View.VISIBLE
                        topicJoined!!.setOnClickListener {
                            val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> if (mContext is FragmentContainer) {
                                        topicJoined!!.visibility = View.GONE
                                        requestProgressBar!!.visibility = View.VISIBLE
                                        leaveTopic(postId)
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }
                            }
                            val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                            builder.setMessage(R.string.leave_publics_topic).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show()
                        }
                    } else {
                        isJoinRequested(postId, eventDate)
                        if (numAdded > 0) {
                            topicMembers!!.visibility = View.VISIBLE
                            topicMembers!!.setOnClickListener {
                                val ldf = TopicManagePlayers()
                                val args = Bundle()
                                args.putString("topic_id", postId)
                                args.putString("permission", "user")
                                ldf.arguments = args
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        }
                    }
                    if (eventDate == "now") {
                        whenLayout!!.setBackgroundResource(drawable.details_button)
                    } else if (eventDate == "ended") {
                        whenLayout!!.setBackgroundResource(drawable.red_button)
                    }
                    textviewnumpublicspointsTop!!.text = votes
                    nicknameLayout!!.setOnClickListener {
                        val ldf = FragmentProfile()
                        val args = Bundle()
                        args.putString("UserId", userId)
                        ldf.arguments = args
                        (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    submitComment!!.setOnClickListener { view: View ->
                        val body = commentEditText!!.text.toString()
                        val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
                        val image = ""
                        if (body.isNotEmpty()) {
                            topicLayout!!.visibility = View.GONE
                            newCommentLayout!!.visibility = View.GONE
                            submitComment!!.visibility = View.GONE
                            mProgressBar!!.visibility = View.VISIBLE
                            submitComment(body, addedBy!!, username, image, postId)
                            commentEditText!!.text.clear()
                            hideKeyboardFrom(mContext, view)
                        } else {
                            Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                        }
                    }
                    when (type) {
                        "Xbox" -> {
                            platformType!!.setImageResource(drawable.icons8_xbox_50)
                            platformType!!.visibility = View.VISIBLE
                        }
                        "PlayStation" -> {
                            platformType!!.setImageResource(drawable.icons8_playstation_50)
                            platformType!!.visibility = View.VISIBLE
                        }
                        "Steam" -> {
                            platformType!!.setImageResource(drawable.icons8_steam_48)
                            platformType!!.visibility = View.VISIBLE
                        }
                        "PC" -> {
                            platformType!!.setImageResource(drawable.icons8_workstation_48)
                            platformType!!.visibility = View.VISIBLE
                        }
                        "Mobile" -> {
                            platformType!!.setImageResource(drawable.icons8_mobile_48)
                            platformType!!.visibility = View.VISIBLE
                        }
                        "Switch" -> {
                            platformType!!.setImageResource(drawable.icons8_nintendo_switch_48)
                            platformType!!.visibility = View.VISIBLE
                        }
                        "Cross-Platform" -> {
                            platformType!!.setImageResource(drawable.icons8_collect_40)
                            platformType!!.visibility = View.VISIBLE
                        }
                        else -> {
                            platformType!!.setImageResource(drawable.icons8_question_mark_64)
                            platformType!!.visibility = View.VISIBLE
                        }
                    }
                    publicspostsupvotewhiteTop!!.setOnClickListener {
                        publicspostsdownvoteredTop!!.visibility = View.GONE
                        publicsPostsDownvoteWhite!!.visibility = View.GONE
                        publicspostsupvotegreenTop!!.visibility = View.GONE
                        publicspostsupvotewhiteTop!!.visibility = View.GONE
                        voteProgress!!.visibility = View.VISIBLE
                        topicVote("up", postId, username)
                    }
                    publicspostsupvotegreenTop!!.setOnClickListener {
                        publicspostsdownvoteredTop!!.visibility = View.GONE
                        publicsPostsDownvoteWhite!!.visibility = View.GONE
                        publicspostsupvotegreenTop!!.visibility = View.GONE
                        publicspostsupvotewhiteTop!!.visibility = View.GONE
                        voteProgress!!.visibility = View.VISIBLE
                        topicVote("remove", postId, username)
                    }
                    publicsPostsDownvoteWhite!!.setOnClickListener {
                        publicspostsdownvoteredTop!!.visibility = View.GONE
                        publicsPostsDownvoteWhite!!.visibility = View.GONE
                        publicspostsupvotegreenTop!!.visibility = View.GONE
                        publicspostsupvotewhiteTop!!.visibility = View.GONE
                        voteProgress!!.visibility = View.VISIBLE
                        topicVote("down", postId, username)
                    }
                    publicspostsdownvoteredTop!!.setOnClickListener {
                        publicspostsdownvoteredTop!!.visibility = View.GONE
                        publicsPostsDownvoteWhite!!.visibility = View.GONE
                        publicspostsupvotegreenTop!!.visibility = View.GONE
                        publicspostsupvotewhiteTop!!.visibility = View.GONE
                        voteProgress!!.visibility = View.VISIBLE
                        topicVote("remove", postId, username)
                    }
                    numPlayersAdded!!.text = numAdded.toString()
                    numPlayersNeeded!!.text = numPlayers
                    publicsPostTitle!!.text = gameName
                    publicspostsnicknameTop!!.text = nickname
                    val usernameText = "@$username"
                    publicspostsusernameTop!!.text = usernameText
                    publicspostbodyTop!!.text = postContent
                    publicspostsdatetimeTop!!.text = postDate
                    tvWhen!!.text = eventDate
                    val profilePic2 = profilePic.substring(0, profilePic.length - 4) + "_r.JPG"
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + profilePic2)
                            .into(publicspostsprofileImageTop!!)
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + backimage)
                            .into(publicsImageView!!)
                    topicLayout!!.visibility = View.VISIBLE
                    mProgressBar!!.visibility = View.GONE
                    publicsTopicMenu!!.setOnClickListener { view: View? ->
                        val popup = PopupMenu(mContext, view)
                        val inflater = popup.menuInflater
                        inflater.inflate(R.menu.publics_topic_menu, popup.menu)
                        popup.setOnMenuItemClickListener { item: MenuItem ->
                            if (item.itemId == R.id.menuTopicReport) {
                                val ldf = ReportFragment()
                                val args = Bundle()
                                args.putString("context", "publics_topic")
                                args.putString("type", "topic")
                                args.putString("id", postId)
                                ldf.arguments = args
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                            if (item.itemId == R.id.menuTopicPlayer) {
                                if (mContext is FragmentContainer) {
                                    val ldf = FragmentProfile()
                                    val args = Bundle()
                                    args.putString("UserId", userId)
                                    ldf.arguments = args
                                    (mContext as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                                }
                            }
                            true
                        }
                        popup.show()
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadPublicsTopic() {
        val stringRequest = StringRequest(Request.Method.GET, "$PublicsTopic_URL?userid=$userID&username=$deviceusername&publicsid=$publicsID", { response: String? ->
            try {
                val publics = JSONArray(response)
                for (i in 0 until publics.length()) {
                    val publicsObject = publics.getJSONObject(i)
                    val id = publicsObject.getString("id")
                    val subject = publicsObject.getString("subject")
                    val date = publicsObject.getString("date")
                    val cat = publicsObject.getString("cat")
                    val topicBy = publicsObject.getString("topic_by")
                    val postId = publicsObject.getString("post_id")
                    val postTopic = publicsObject.getString("post_topic")
                    val postContent = publicsObject.getString("post_content")
                    val postDate = publicsObject.getString("post_date")
                    val postBy = publicsObject.getString("post_by")
                    val userId = publicsObject.getString("user_id")
                    val profilePic = publicsObject.getString("profile_pic")
                    val nickname = publicsObject.getString("nickname")
                    val username = publicsObject.getString("username")
                    val voted = publicsObject.getString("voted")
                    val votes = publicsObject.getString("votes")
                    val replies = publicsObject.getString("replies")
                    val online = publicsObject.getString("online")
                    val verified = publicsObject.getString("verified")
                    val clantag = publicsObject.getString("clantag")
                    val publicsPostResult = PublicsPostRecycler(id, subject, date, cat, topicBy, postId, postTopic, postContent, postDate, postBy, userId, profilePic, nickname, username, voted, votes, replies, online, verified, clantag)
                    publicsPostList!!.add(publicsPostResult)
                }
                adapter = PublicsPostAdapter(mContext!!, publicsPostList!!)
                publicsView!!.adapter = adapter
                mProgressBar!!.visibility = View.GONE
                publicsSwipe!!.visibility = View.VISIBLE
                publicsSwipe!!.isRefreshing = false
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { error: VolleyError -> Toast.makeText(mContext, error.message, Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun isJoinRequested(post_id: String, time: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, GET_REQUESTS, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    if (obj.has("request_sent") && obj.getString("request_sent") == "yes") {
                        requestedToJoin!!.visibility = View.VISIBLE
                        requestProgressBar!!.visibility = View.GONE
                        requestedToJoin!!.setOnClickListener {
                            val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> {
                                        requestedToJoin!!.visibility = View.GONE
                                        requestProgressBar!!.visibility = View.VISIBLE
                                        if (mContext is FragmentContainer) {
                                            leaveTopic(post_id)
                                        }
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> { }
                                }
                            }
                            val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                            builder.setMessage(R.string.leave_publics_topic_requested).setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show()
                        }
                    } else if (obj.has("request_sent") && obj.getString("request_sent") == "no") {
                        requestToJoin!!.visibility = View.VISIBLE
                        requestProgressBar!!.visibility = View.GONE
                        if (time == "ended") {
                            requestToJoin!!.setBackgroundResource(drawable.grey_button)
                            requestToJoin!!.setOnClickListener { }
                        } else {
                            requestToJoin!!.setOnClickListener {
                                requestToJoin!!.visibility = View.GONE
                                requestProgressBar!!.visibility = View.VISIBLE
                                isConnectOrReceivedConnect(post_id)
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                            mContext,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not get requests, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topic_id"] = post_id
                params["thisusername"] = deviceusername!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    fun requestToJoin(topic_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, JOIN_TOPIC, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("result") == "success") {
                    requestedToJoin!!.visibility = View.VISIBLE
                    requestProgressBar!!.visibility = View.GONE
                    Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not send request, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topic_id"] = topic_id
                params["thisusername"] = deviceusername!!
                params["thisuserid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    fun requestToJoinNonConnection(topic_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, JOIN_TOPIC_NON_CONNECTION, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("result") == "success") {
                    requestedToJoin!!.visibility = View.VISIBLE
                    requestProgressBar!!.visibility = View.GONE
                    Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not send request, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topic_id"] = topic_id
                params["thisusername"] = deviceusername!!
                params["thisuserid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    fun requestToJoinNewConnection(topic_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, JOIN_TOPIC_NEW_CONNECTION, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("result") == "success") {
                    requestedToJoin!!.visibility = View.VISIBLE
                    requestProgressBar!!.visibility = View.GONE
                    Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not send request, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topic_id"] = topic_id
                params["thisusername"] = deviceusername!!
                params["thisuserid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    fun isConnectOrReceivedConnect(topic_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, IS_CONNECTED, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("result") == "success") {
                    when {
                        obj["isFriend"] == "yes" -> {
                            requestToJoin(topic_id)
                        }
                        obj["isFriend"] == "received" -> {
                            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(drawable.ic_friend_add)
                                    .setTitle(R.string.accept_connection_request_publics)
                                    .setMessage(R.string.accept_connection_request_publics_message)
                                    .setPositiveButton(R.string.yes) { requestToJoinNewConnection(topic_id) }
                                    .setNegativeButton(R.string.no, null)
                                    .show()
                        }
                        else -> {
                            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(drawable.ic_friend_add)
                                    .setTitle(R.string.send_connection_request)
                                    .setMessage(R.string.send_connection_request_message)
                                    .setPositiveButton(R.string.yes) { requestToJoinNonConnection(topic_id) }
                                    .setNegativeButton(R.string.no, null)
                                    .show()
                        }
                    }
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not send request, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topic_id"] = topic_id
                params["thisusername"] = deviceusername!!
                params["thisuserid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    fun leaveTopic(topic_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, LEAVE_TOPIC, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    if (obj.getString("result") == "yes") {
                        resetFragment()
                    }
                } else {
                    Toast.makeText(
                            mContext,
                            obj.getString("message"),
                            Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not send request, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["topic_id"] = topic_id
                params["thisusername"] = deviceusername!!
                params["thisuserid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun resetFragment() {
        val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is PublicsTopicFragment) {
            val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            fragTransaction.detach(currentFragment)
            fragTransaction.attach(currentFragment)
            fragTransaction.commit()
            publicsSwipe!!.isRefreshing = false
            publicspostsnicknameTop!!.requestFocus()
        }
        //refresh.setRefreshing(false);
    }

    private fun submitComment(body: String, added_by: String, user_to: String, image: String, post_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, PUBLICS_POST_SUBMIT, Response.Listener { resetFragment() }, Response.ErrorListener {
            mProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Network error, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["added_by"] = added_by
                params["user_to"] = user_to
                params["image"] = image
                params["post_id"] = post_id
                params["user_id"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun topicVote(method: String, layout_id: String, user_to: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, VOTE_URL, Response.Listener { response: String? ->
            val obj: JSONObject
            try {
                obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    if (obj.getString("layout") == "topic") {
                        when {
                            obj.getString("method") == "up" -> {
                                val newValue = (textviewnumpublicspointsTop!!.text.toString().toInt() + 1).toString()
                                textviewnumpublicspointsTop!!.text = newValue
                                voteProgress!!.visibility = View.GONE
                                publicspostsupvotegreenTop!!.visibility = View.VISIBLE
                                publicsPostsDownvoteWhite!!.visibility = View.VISIBLE
                                publicspostsupvotegreenTop!!.isEnabled = false
                                publicsPostsDownvoteWhite!!.isEnabled = false
                                Handler().postDelayed({
                                    publicspostsupvotegreenTop!!.isEnabled = true
                                    publicsPostsDownvoteWhite!!.isEnabled = true
                                }, 3000)
                            }
                            obj.getString("method") == "down" -> {
                                val newValue = textviewnumpublicspointsTop!!.text.toString().toInt().toString()
                                textviewnumpublicspointsTop!!.text = newValue
                                voteProgress!!.visibility = View.GONE
                                publicspostsupvotewhiteTop!!.visibility = View.VISIBLE
                                publicspostsdownvoteredTop!!.visibility = View.VISIBLE
                                publicspostsupvotewhiteTop!!.isEnabled = false
                                publicspostsdownvoteredTop!!.isEnabled = false
                                Handler().postDelayed({
                                    publicspostsupvotewhiteTop!!.isEnabled = true
                                    publicspostsdownvoteredTop!!.isEnabled = true
                                }, 3000)
                            }
                            else -> {
                                var finalValue = ""
                                if (obj.getString("result") == "-1") {
                                    finalValue = (textviewnumpublicspointsTop!!.text.toString().toInt() - 1).toString()
                                } else if (obj.getString("result") == "+1") {
                                    finalValue = (textviewnumpublicspointsTop!!.text.toString().toInt() + 1).toString()
                                }
                                textviewnumpublicspointsTop!!.text = finalValue
                                publicspostsupvotewhiteTop!!.visibility = View.VISIBLE
                                publicsPostsDownvoteWhite!!.visibility = View.VISIBLE
                                voteProgress!!.visibility = View.GONE
                                publicspostsupvotewhiteTop!!.isEnabled = false
                                publicsPostsDownvoteWhite!!.isEnabled = false
                                Handler().postDelayed({
                                    publicspostsupvotewhiteTop!!.isEnabled = true
                                    publicsPostsDownvoteWhite!!.isEnabled = true
                                }, 3000)
                            }
                        }
                    }
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                    voteProgress!!.visibility = View.GONE
                    publicspostsupvotewhiteTop!!.visibility = View.VISIBLE
                    publicsPostsDownvoteWhite!!.visibility = View.VISIBLE
                    publicspostsupvotewhiteTop!!.isEnabled = false
                    publicsPostsDownvoteWhite!!.isEnabled = false
                    Handler().postDelayed({
                        publicspostsupvotewhiteTop!!.isEnabled = true
                        publicsPostsDownvoteWhite!!.isEnabled = true
                    }, 3000)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            Toast.makeText(mContext, "Could not vote, please try again later...", Toast.LENGTH_LONG).show()
            voteProgress!!.visibility = View.GONE
            publicspostsupvotewhiteTop!!.visibility = View.VISIBLE
            publicsPostsDownvoteWhite!!.visibility = View.VISIBLE
            publicspostsupvotewhiteTop!!.isEnabled = false
            publicsPostsDownvoteWhite!!.isEnabled = false
            Handler().postDelayed({
                publicspostsupvotewhiteTop!!.isEnabled = true
                publicsPostsDownvoteWhite!!.isEnabled = true
            }, 3000)
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["post_id"] = layout_id
                params["method"] = method
                params["layout"] = "topic"
                params["user_to"] = user_to
                params["user_id"] = userID!!
                params["username"] = deviceusername!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun deleteTopic(post_id: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, PUBLICS_DELETE, Response.Listener { resetFragment() }, Response.ErrorListener {
            mProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Network error, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = deviceusername!!
                params["topic_id"] = post_id
                params["user_id"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val PublicsTopicTop_URL = Constants.ROOT_URL + "publicsTopicTop_api.php"
        private const val PublicsTopic_URL = Constants.ROOT_URL + "publicsTopic_api.php"
        private const val LEAVE_TOPIC = Constants.ROOT_URL + "publics_leave_topic.php"
        private const val JOIN_TOPIC = Constants.ROOT_URL + "publics_join_topic.php"
        private const val JOIN_TOPIC_NON_CONNECTION = Constants.ROOT_URL + "publics_join_topic_non_connection.php"
        private const val JOIN_TOPIC_NEW_CONNECTION = Constants.ROOT_URL + "publics_join_topic_new_connection.php"
        private const val IS_CONNECTED = Constants.ROOT_URL + "publics_join_topic_is_connected.php"
        private const val GET_REQUESTS = Constants.ROOT_URL + "publics_get_requests.php"
        private const val PUBLICS_POST_SUBMIT = Constants.ROOT_URL + "publics_comment_submit.php"
        private const val PUBLICS_DELETE = Constants.ROOT_URL + "publics_topic_delete.php"
        private const val VOTE_URL = Constants.ROOT_URL + "publics_topic_vote.php"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}