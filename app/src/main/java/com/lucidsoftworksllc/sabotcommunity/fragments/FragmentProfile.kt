package com.lucidsoftworksllc.sabotcommunity.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
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
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.others.Constants.ROOT_URL
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.JoinedClansAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.ProfilenewsAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsTopicAdapter
import com.lucidsoftworksllc.sabotcommunity.models.ClansRecycler
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.PublicsTopicRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.others.toastLong
import com.theartofdev.edmodo.cropper.CropImage
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class FragmentProfile : Fragment() {
    private var profilenewsView: RecyclerView? = null
    private var newsadapter: ProfilenewsAdapter? = null
    private var publicsnewsadapter: PublicsTopicAdapter? = null
    private var clansAdapter: JoinedClansAdapter? = null
    private var profilenewsRecyclerList: MutableList<ProfilenewsRecycler>? = null
    private var profilepublicsnewsRecyclerList: MutableList<PublicsTopicRecycler>? = null
    private var clans: MutableList<ClansRecycler>? = null
    private var userSwitch: TextView? = null
    private var userTwitter: TextView? = null
    private var userDiscordProfile: TextView? = null
    private var profileWebsite: TextView? = null
    private var profileItemsLabel: TextView? = null
    private var followingTV: TextView? = null
    private var connectionsTV: TextView? = null
    private var followersTV: TextView? = null
    private var postsNoPosts: TextView? = null
    private var textViewClanTag: TextView? = null
    private var textViewTvPosts: TextView? = null
    private val textViewTvLikes: TextView? = null
    private val editProfile: TextView? = null
    private var textViewUsername: TextView? = null
    private var textViewNickname: TextView? = null
    private var textViewDescription: TextView? = null
    private var reviewCount: TextView? = null
    private var userTwitch: TextView? = null
    private var userMixer: TextView? = null
    private var userPSN: TextView? = null
    private var userXbox: TextView? = null
    private var userDiscord: TextView? = null
    private var userSteam: TextView? = null
    private var userInstagram: TextView? = null
    private var userYoutube: TextView? = null
    private var followersCount: TextView? = null
    private var friendsCount: TextView? = null
    private var followingCount: TextView? = null
    private var starIcon: ImageView? = null
    private var imageViewProfilePic: ImageView? = null
    private var imageViewProfileCover: ImageView? = null
    private var profileOnlineIcon: ImageView? = null
    private var imageUploadBtn: ImageView? = null
    private var verifiedIcon: CircleImageView? = null
    private var mContext: Context? = null
    private var submitStatusButton: Button? = null
    private var profileNewsMoreBtn: Button? = null
    private var profilePostsButton: Button? = null
    private var publicsPostsButton: Button? = null
    private var profileClansButton: Button? = null
    private var userSwitchDetails: RelativeLayout? = null
    private var userTwitterDetails: RelativeLayout? = null
    private var userDiscordProfileDetails: RelativeLayout? = null
    private var mProgressBar: RelativeLayout? = null
    private var profileErrorScreen: RelativeLayout? = null
    private var setProfileCoverButton: RelativeLayout? = null
    private var setProfilePhotoButton: RelativeLayout? = null
    private var profileDisabledScreen: RelativeLayout? = null
    private var userTwitchDetails: RelativeLayout? = null
    private var userInstagramDetails: RelativeLayout? = null
    private var userYoutubeDetails: RelativeLayout? = null
    private var userMixerDetails: RelativeLayout? = null
    private var userPSNDetails: RelativeLayout? = null
    private var userXboxDetails: RelativeLayout? = null
    private var userDiscordDetails: RelativeLayout? = null
    private var userSteamDetails: RelativeLayout? = null
    private var profileWebsiteContainer: LinearLayout? = null
    private var profileLayout: LinearLayout? = null
    private var addPostLayout: LinearLayout? = null
    private var playerratingLayout: LinearLayout? = null
    private var moreButtonLayout: LinearLayout? = null
    private var profileStatusContainer: LinearLayout? = null
    private var addMessageButton: MaterialRippleLayout? = null
    private var requestSentFriendButton: MaterialRippleLayout? = null
    private var addFriendProgress: MaterialRippleLayout? = null
    private var editProfileButton: MaterialRippleLayout? = null
    private var followProfileButton: MaterialRippleLayout? = null
    private var followedProfileButton: MaterialRippleLayout? = null
    private var addFriendButton: MaterialRippleLayout? = null
    private var addedFriendButton: MaterialRippleLayout? = null
    private var requestedFriendButton: MaterialRippleLayout? = null
    private var addItemButton: MaterialRippleLayout? = null
    private var profileRating: SimpleRatingBar? = null
    private var profileRefreshLayout: SwipeRefreshLayout? = null
    private var postTypeSpinner: Spinner? = null
    private var imageToUpload: Bitmap? = null
    private var profileUsername: String? = null
    private var rQueue: RequestQueue? = null
    private var jsonObject: JSONObject? = null
    private var userID: String? = null
    private var userProfileID: String? = null
    private var deviceUsername: String? = null
    private var statusUpdate: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val userProfileRootView = inflater.inflate(R.layout.fragment_profile, container, false)
        mContext = activity
        mProgressBar = userProfileRootView.findViewById(R.id.profileLoadingScreen)
        addFriendProgress = userProfileRootView.findViewById(R.id.addFriendProgress)
        followingCount = userProfileRootView.findViewById(R.id.followingCount)
        followersCount = userProfileRootView.findViewById(R.id.followersCount)
        friendsCount = userProfileRootView.findViewById(R.id.friendsCount)
        userTwitch = userProfileRootView.findViewById(R.id.userTwitch)
        userMixer = userProfileRootView.findViewById(R.id.userMixer)
        userPSN = userProfileRootView.findViewById(R.id.userPSN)
        userXbox = userProfileRootView.findViewById(R.id.userXbox)
        userDiscord = userProfileRootView.findViewById(R.id.userDiscord)
        userSteam = userProfileRootView.findViewById(R.id.userSteam)
        userTwitchDetails = userProfileRootView.findViewById(R.id.userTwitchDetails)
        userInstagram = userProfileRootView.findViewById(R.id.userInstagram)
        userInstagramDetails = userProfileRootView.findViewById(R.id.userInstagramDetails)
        userYoutube = userProfileRootView.findViewById(R.id.userYoutube)
        userYoutubeDetails = userProfileRootView.findViewById(R.id.userYoutubeDetails)
        userMixerDetails = userProfileRootView.findViewById(R.id.userMixerDetails)
        userPSNDetails = userProfileRootView.findViewById(R.id.userPSNDetails)
        userXboxDetails = userProfileRootView.findViewById(R.id.userXboxDetails)
        userDiscordDetails = userProfileRootView.findViewById(R.id.userDiscordDetails)
        userSteamDetails = userProfileRootView.findViewById(R.id.userSteamDetails)
        profileDisabledScreen = userProfileRootView.findViewById(R.id.profileDisabledScreen)
        textViewNickname = userProfileRootView.findViewById(R.id.textViewNickname)
        textViewUsername = userProfileRootView.findViewById(R.id.textViewUsername)
        textViewTvPosts = userProfileRootView.findViewById(R.id.tvPosts)
        statusUpdate = userProfileRootView.findViewById(R.id.statusUpdate)
        submitStatusButton = userProfileRootView.findViewById(R.id.submitStatusButton)
        postsNoPosts = userProfileRootView.findViewById(R.id.postsNoPosts)
        //textViewTvLikes = userProfileRootView.findViewById(R.id.tvFollowers);
        addPostLayout = userProfileRootView.findViewById(R.id.addPostLayout)
        playerratingLayout = userProfileRootView.findViewById(R.id.profileRatingContainer)
        textViewDescription = userProfileRootView.findViewById<View>(R.id.textViewDescription) as TextView
        verifiedIcon = userProfileRootView.findViewById(R.id.verifiedIcon)
        addItemButton = userProfileRootView.findViewById(R.id.addItemButton)
        profileOnlineIcon = userProfileRootView.findViewById(R.id.profileOnlineIcon)
        setProfilePhotoButton = userProfileRootView.findViewById(R.id.setProfilePhotoButton)
        setProfileCoverButton = userProfileRootView.findViewById(R.id.setProfileCoverButton)
        imageViewProfilePic = userProfileRootView.findViewById(R.id.imageViewProfilePic)
        imageViewProfileCover = userProfileRootView.findViewById(R.id.profileCover)
        editProfileButton = userProfileRootView.findViewById(R.id.editProfileButton)
        followProfileButton = userProfileRootView.findViewById(R.id.followProfileButton)
        followedProfileButton = userProfileRootView.findViewById(R.id.followedProfileButton)
        requestedFriendButton = userProfileRootView.findViewById(R.id.requestedFriendButton)
        addedFriendButton = userProfileRootView.findViewById(R.id.addedFriendButton)
        addFriendButton = userProfileRootView.findViewById(R.id.addFriendButton)
        addMessageButton = userProfileRootView.findViewById(R.id.addMessageButton)
        starIcon = userProfileRootView.findViewById(R.id.starIcon)
        profileRating = userProfileRootView.findViewById(R.id.profileRating)
        //editProfile = (TextView) userProfileRootView.findViewById(R.id.textEditProfile);
        postTypeSpinner = userProfileRootView.findViewById(R.id.postTypeSpinner)
        profileLayout = userProfileRootView.findViewById(R.id.profileLayout)
        reviewCount = userProfileRootView.findViewById(R.id.reviewCount)
        profileRefreshLayout = userProfileRootView.findViewById(R.id.profileRefreshLayout)
        requestSentFriendButton = userProfileRootView.findViewById(R.id.requestSentFriendButton)
        addMessageButton = userProfileRootView.findViewById(R.id.addMessageButton)
        moreButtonLayout = userProfileRootView.findViewById(R.id.moreButtonLayout)
        imageUploadBtn = userProfileRootView.findViewById(R.id.imageUploadBtn)
        textViewClanTag = userProfileRootView.findViewById(R.id.textViewClanTag)
        profileErrorScreen = userProfileRootView.findViewById(R.id.profileErrorScreen)
        profileStatusContainer = userProfileRootView.findViewById(R.id.profileStatusContainer)
        profileNewsMoreBtn = userProfileRootView.findViewById(R.id.profileNewsMoreBtn)
        followersTV = userProfileRootView.findViewById(R.id.followers)
        followingTV = userProfileRootView.findViewById(R.id.following)
        connectionsTV = userProfileRootView.findViewById(R.id.connections)
        profileClansButton = userProfileRootView.findViewById(R.id.profileClansButtons)
        publicsPostsButton = userProfileRootView.findViewById(R.id.publicsPostsButtons)
        profilePostsButton = userProfileRootView.findViewById(R.id.profilePostsButton)
        profileItemsLabel = userProfileRootView.findViewById(R.id.profileItemsLabel)
        profileWebsite = userProfileRootView.findViewById(R.id.profileWebsite)
        profileWebsiteContainer = userProfileRootView.findViewById(R.id.profileWebsiteContainer)
        userDiscordProfileDetails = userProfileRootView.findViewById(R.id.userDiscordProfileDetails)
        userDiscordProfile = userProfileRootView.findViewById(R.id.userDiscordProfile)
        userTwitterDetails = userProfileRootView.findViewById(R.id.userTwitterDetails)
        userTwitter = userProfileRootView.findViewById(R.id.userTwitter)
        userSwitchDetails = userProfileRootView.findViewById(R.id.userSwitchDetails)
        userSwitch = userProfileRootView.findViewById(R.id.userSwitch)
        imageToUpload = null
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        deviceUsername = SharedPrefManager.getInstance(mContext!!)!!.username
        profilenewsRecyclerList = ArrayList()
        profilepublicsnewsRecyclerList = ArrayList()
        clans = ArrayList()
        profilenewsView = userProfileRootView.findViewById(R.id.recyclerProfilenews)
        profilenewsView?.setHasFixedSize(true)
        profilenewsView?.layoutManager = LinearLayoutManager(mContext)
        if (arguments != null) {
            if (requireArguments().getString("UserId") != null) {
                userProfileID = requireArguments().getString("UserId")
                loadProfileTop()
            } else if (requireArguments().getString("Username") != null && requireArguments().getString("UserId") == null) {
                getUserID(requireArguments().getString("Username"))
            } else {
                userProfileID = userID
                loadProfileTop()
            }
        } else {
            userProfileID = userID
            loadProfileTop()
        }
        profileRefreshLayout?.setOnRefreshListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
            } else {
                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
            }
            profileRefreshLayout?.isRefreshing = false
            imageViewProfileCover?.requestFocus()
        }
        addItemButton?.setOnClickListener {
            if (addPostLayout?.visibility != View.GONE) {
                addPostLayout?.visibility = View.GONE
            } else {
                addPostLayout?.visibility = View.VISIBLE
                statusUpdate?.requestFocus()
                if (statusUpdate?.hasFocus()!!) {
                    val imm = mContext!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                }
                imageUploadBtn?.setOnClickListener {
                    requestMultiplePermissions()
                    openCropper()
                }
            }
        }
        editProfileButton?.setOnClickListener {
            val asf: Fragment = AccountSettingsFragment()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            fragmentTransaction.replace(R.id.fragment_container, asf)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        setProfileCoverButton?.setOnClickListener {
            val asf: Fragment = UploadCoverFragment()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            fragmentTransaction.replace(R.id.fragment_container, asf)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        setProfilePhotoButton?.setOnClickListener {
            val asf: Fragment = UploadProfilePhotoFragment()
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            fragmentTransaction.replace(R.id.fragment_container, asf)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        return userProfileRootView
    }

    private fun submitStatus(body: String, added_by: String, user_to: String, type: String, form: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, RG, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    if (imageToUpload != null) {
                        val postId = jsonObject.getString("postid")
                        postImageUpload(imageToUpload!!, postId, deviceUsername)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
                        (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
                    } else {
                        (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
                    }
                    statusUpdate!!.setText("")
                    imageViewProfileCover!!.requestFocus()
                } else {
                    mProgressBar!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            mProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Error on Response, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["added_by"] = added_by
                params["user_to"] = user_to
                params["user_id"] = userID!!
                params["type"] = type
                params["form"] = form
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(mContext)
        requestQueue.add(stringRequest)
    }

    private fun postImageUpload(bitmap: Bitmap, post_id: String, username: String?) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            jsonObject = JSONObject()
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject!!.put("name", imgname)
            jsonObject!!.put("post_id", post_id)
            jsonObject!!.put("image", encodedImage)
            jsonObject!!.put("added_by", username)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, UPLOAD_IMAGE_URL, jsonObject,
                { jsonObject: JSONObject ->
                    rQueue!!.cache.clear()
                    try {
                        if (jsonObject.getString("error") == "true") {
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }) { }
        rQueue = Volley.newRequestQueue(mContext)
        rQueue?.add(jsonObjectRequest)
    }

    private fun loadProfileTop() {
        //final String users_friends_array = SharedPrefManager.getInstance(mContext).getUsersFriends();
        val stringRequest = StringRequest(Request.Method.GET, "$ProfileTop_URL?userid=$userID&userid2=$userProfileID&deviceusername=$deviceUsername", { response: String? ->
            try {
                val profiletop = JSONArray(response)
                val profiletopObject = profiletop.getJSONObject(0)
                val id = profiletopObject.getString("id")
                val nickname = profiletopObject.getString("nickname")
                val username = profiletopObject.getString("username")
                val description = profiletopObject.getString("description")
                val verified = profiletopObject.getString("verified")
                //String signup_date = profiletopObject.getString("signup_date");
                val profilePic = profiletopObject.getString("profile_pic")
                val coverPic = profiletopObject.getString("cover_pic")
                val numPosts = profiletopObject.getString("num_posts")
                //String num_likes = profiletopObject.getString("num_likes");
                val userClosed = profiletopObject.getString("user_closed")
                val userBanned = profiletopObject.getString("user_banned")
                val numFriends = profiletopObject.getString("num_friends")
                val followings = profiletopObject.getString("followings")
                val followers = profiletopObject.getString("followers")
                val twitch = profiletopObject.getString("twitch")
                val mixer = profiletopObject.getString("mixer")
                val psn = profiletopObject.getString("psn")
                val xbox = profiletopObject.getString("xbox")
                val discord = profiletopObject.getString("discord")
                val steam = profiletopObject.getString("steam")
                val instagram = profiletopObject.getString("instagram")
                val youtube = profiletopObject.getString("youtube")
                val lastOnline = profiletopObject.getString("last_online")
                val count = profiletopObject.getString("count")
                val average = profiletopObject.getString("average")
                val clantag = profiletopObject.getString("clantag")
                val blocked = profiletopObject.getString("blocked")
                val supporter = profiletopObject.getString("supporter")
                val discordUser = profiletopObject.getString("discord_user")
                val twitter = profiletopObject.getString("twitter")
                val website = profiletopObject.getString("website")
                val nintendo = profiletopObject.getString("nintendo")
                val isFollowing = profiletopObject.getString("isFollowing")
                val isConnected = profiletopObject.getString("isConnected")
                val connections = profiletopObject.getString("connections")
                profileUsername = username
                if (userID == userProfileID) {
                    addMessageButton!!.visibility = View.GONE
                    addFriendButton!!.visibility = View.GONE
                    addFriendProgress!!.visibility = View.GONE
                    followProfileButton!!.visibility = View.GONE
                    editProfileButton!!.visibility = View.VISIBLE
                    setProfileCoverButton!!.visibility = View.VISIBLE
                    setProfilePhotoButton!!.visibility = View.VISIBLE
                }
                if (blocked == "yes" || SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(username)) {
                    profileErrorScreen!!.visibility = View.VISIBLE
                    profileLayout!!.visibility = View.GONE
                    mProgressBar!!.visibility = View.GONE
                } else if (userClosed == "yes" || userBanned == "yes") {
                    profileDisabledScreen!!.visibility = View.VISIBLE
                    profileLayout!!.visibility = View.GONE
                    mProgressBar!!.visibility = View.GONE
                } else {
                    postsQueryButtonClicked(profilePostsButton)
                }
                if (clantag != "") textViewClanTag!!.text = String.format("[%s]", clantag)
                if (supporter == "yes") {
                    starIcon!!.visibility = View.VISIBLE
                }
                if (discordUser.isNotEmpty()) {
                    userDiscordProfileDetails!!.visibility = View.VISIBLE
                    userDiscordProfile!!.text = discordUser
                }
                if (twitter.isNotEmpty()) {
                    userTwitterDetails!!.visibility = View.VISIBLE
                    userTwitter!!.text = twitter
                    userTwitterDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$twitter")))
                        }
                    }
                }
                if (website.isNotEmpty()) {
                    profileWebsiteContainer!!.visibility = View.VISIBLE
                    profileWebsite!!.text = website
                    profileWebsiteContainer!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website)))
                        }
                    }
                }
                if (nintendo.isNotEmpty()) {
                    userSwitchDetails!!.visibility = View.VISIBLE
                    userSwitch!!.text = nintendo
                }
                imageViewProfileCover!!.setOnClickListener {
                    val asf: Fragment = PhotoViewFragment()
                    val args = Bundle()
                    args.putString("image", coverPic)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                imageViewProfilePic!!.setOnClickListener {
                    val asf: Fragment = PhotoViewFragment()
                    val args = Bundle()
                    args.putString("image", profilePic)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                addMessageButton!!.setOnClickListener { startActivity(Intent(mContext, ChatActivity::class.java).putExtra("user_to", username)) }
                followersCount!!.text = followers
                followingCount!!.text = followings
                friendsCount!!.text = numFriends
                if (twitch.isNotEmpty()) {
                    userTwitch!!.text = twitch
                    userTwitchDetails!!.visibility = View.VISIBLE
                    userTwitchDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/$twitch")))
                        }
                    }
                }
                if (mixer.isNotEmpty()) {
                    userMixer!!.text = mixer
                    userMixerDetails!!.visibility = View.VISIBLE
                    userMixerDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://mixer.com/$mixer")))
                        }
                    }
                }
                if (psn.isNotEmpty()) {
                    userPSN!!.text = psn
                    userPSNDetails!!.visibility = View.VISIBLE
                    userPSNDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://psnprofiles.com/$psn")))
                        }
                    }
                }
                if (xbox.isNotEmpty()) {
                    userXbox!!.text = xbox
                    userXboxDetails!!.visibility = View.VISIBLE
                    userXboxDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://account.xbox.com/en-us/profile?gamertag=$xbox")))
                        }
                    }
                }
                if (discord.isNotEmpty()) {
                    userDiscord!!.setText(R.string.discord_server_text)
                    userDiscordDetails!!.visibility = View.VISIBLE
                    userDiscordDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discord.gg/$discord")))
                        }
                    }
                }
                if (steam.isNotEmpty()) {
                    userSteam!!.text = steam
                    userSteamDetails!!.visibility = View.VISIBLE
                    userSteamDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/id/$steam")))
                        }
                    }
                }
                if (youtube.isNotEmpty()) {
                    userYoutubeDetails!!.visibility = View.VISIBLE
                    userYoutubeDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/$youtube")))
                        }
                    }
                }
                if (instagram.isNotEmpty()) {
                    userInstagram!!.text = instagram
                    userInstagramDetails!!.visibility = View.VISIBLE
                    userInstagramDetails!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/$instagram")))
                        }
                    }
                }
                val averageFloat = average.toFloat()
                profileRating!!.rating = averageFloat
                reviewCount!!.text = count
                val usernameTo: String
                if (SharedPrefManager.getInstance(mContext!!)!!.username != username) {
                    usernameTo = username
                    if (isFollowing == "yes") {
                        followProfileButton!!.visibility = View.GONE
                        followedProfileButton!!.visibility = View.VISIBLE
                    }
                    if (isConnected == "yes") {
                        addedFriendButton!!.visibility = View.VISIBLE
                        addFriendProgress!!.visibility = View.GONE
                        addItemButton!!.visibility = View.VISIBLE
                    } else {
                        connectionRequest(username)
                    }
                    if (isConnected != "yes" && userProfileID != userID) {
                        addItemButton!!.visibility = View.GONE
                    }
                } else {
                    // profile is device user's
                    usernameTo = "none"
                    if (SharedPrefManager.getInstance(mContext!!)!!.profilePic != profilePic) {
                        SharedPrefManager.getInstance(mContext!!)!!.profilePic = profilePic
                    }
                    /*if(!connections.equals(users_friends_array)){
                        SharedPrefManager.getInstance(mContext).getFriendArray();
                    }*/
                }
                submitStatusButton!!.setOnClickListener { view: View ->
                    val body = statusUpdate!!.text.toString()
                    val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
                    val spinnerText = postTypeSpinner!!.selectedItem.toString()
                    val form = "user"
                    if (statusUpdate!!.text.toString().isNotEmpty() && spinnerText.isNotEmpty()) {
                        profileLayout!!.visibility = View.GONE
                        mProgressBar!!.visibility = View.VISIBLE
                        submitStatus(body, addedBy!!, usernameTo, spinnerText, form)
                        hideKeyboardFrom(mContext, view)
                    } else {
                        Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                    }
                }
                playerratingLayout!!.setOnClickListener {
                    val ldf = PlayerReviewFragment()
                    val args = Bundle()
                    args.putString("UserId", id)
                    args.putString("username", username)
                    args.putString("nickname", nickname)
                    args.putString("verified", verified)
                    args.putString("profile_pic", profilePic)
                    args.putString("all_friend_array", connections)
                    args.putString("last_online", lastOnline)
                    ldf.arguments = args
                    requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                moreButtonLayout!!.setOnClickListener { view: View? ->
                    val popup = PopupMenu(mContext, view)
                    val inflater = popup.menuInflater
                    inflater.inflate(R.menu.profile_more_menu, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem ->
                        if (item.itemId == R.id.menuPlayerReview) {
                            val ldf = PlayerReviewFragment()
                            val args = Bundle()
                            args.putString("UserId", id)
                            args.putString("username", username)
                            args.putString("nickname", nickname)
                            args.putString("verified", verified)
                            args.putString("profile_pic", profilePic)
                            args.putString("all_friend_array", connections)
                            args.putString("last_online", lastOnline)
                            ldf.arguments = args
                            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        if (item.itemId == R.id.menuPlayerReport) {
                            val ldf = ReportFragment()
                            val args = Bundle()
                            args.putString("context", "player")
                            args.putString("type", "user")
                            args.putString("id", id)
                            ldf.arguments = args
                            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        if (item.itemId == R.id.menuPlayerBlock) {
                            if (deviceUsername != profileUsername) {
                                val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                                    when (which) {
                                        DialogInterface.BUTTON_POSITIVE -> {
                                            SharedPrefManager.getInstance(mContext!!)!!.blockUser(username)
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
                                                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
                                            } else {
                                                (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
                                            }
                                        }
                                        DialogInterface.BUTTON_NEGATIVE -> { }
                                    }
                                }
                                val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                                builder.setMessage("Block user?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show()
                            } else {
                                Toast.makeText(mContext, "Are you really trying to block yourself?", Toast.LENGTH_SHORT).show()
                            }
                        }
                        true
                    }
                    popup.show()
                }
                followProfileButton!!.setOnClickListener { followUser(username, followers, "follow") }
                followedProfileButton!!.setOnClickListener {
                    LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.green)
                            .setIcon(R.drawable.ic_friend_add)
                            .setTitle("Unfollow @$username?")
                            .setPositiveButton(R.string.yes) { followUser(username, followers, "unfollow") }
                            .setNegativeButton(R.string.no, null)
                            .show()
                }
                addFriendButton!!.setOnClickListener {
                    LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.green)
                            .setIcon(R.drawable.ic_friend_add)
                            .setTitle("Connection")
                            .setMessage("Connecting is PERMANENT as this user will be able to review you at any time if accepted.\n\nConnect with @$username?")
                            .setPositiveButton(R.string.yes) {
                                addFriendButton!!.visibility = View.GONE
                                requestSentFriendButton!!.visibility = View.VISIBLE
                                addConnection(username)
                                followUser(username, followers, "follow")
                            }
                            .setNegativeButton(R.string.no, null)
                            .show()
                }
                requestedFriendButton!!.setOnClickListener {
                    LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.green)
                            .setIcon(R.drawable.ic_friend_add)
                            .setTitle("Connection")
                            .setMessage("Connecting is PERMANENT as this user will be able to review you at any time if accepted.\n\nConnect with @$username?")
                            .setPositiveButton(R.string.yes) {
                                addFriendButton!!.visibility = View.GONE
                                requestedFriendButton!!.visibility = View.VISIBLE
                                acceptConnection(username)
                                followUser(username, followers, "follow")
                            }
                            .setNegativeButton(R.string.no, null)
                            .show()
                }
                requestSentFriendButton!!.setOnClickListener {
                    LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.green)
                            .setIcon(R.drawable.ic_friend_add)
                            .setTitle("Connection")
                            .setMessage("You are in the process of being permanently connected! If you're having trouble with this user, consider blocking or reporting.")
                            .show()
                }
                addedFriendButton!!.setOnClickListener {
                    LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.green)
                            .setIcon(R.drawable.ic_friend_add)
                            .setTitle("Connection")
                            .setMessage("You are permanently connected! If you're having trouble with this user, consider blocking or reporting.")
                            .show()
                }
                if (description != "") {
                    textViewDescription!!.text = description
                    profileStatusContainer!!.visibility = View.VISIBLE
                }
                if (verified == "yes") {
                    verifiedIcon!!.visibility = View.VISIBLE
                }
                if (lastOnline == "yes") {
                    profileOnlineIcon!!.visibility = View.VISIBLE
                }
                textViewUsername!!.text = String.format("@%s", username)
                textViewTvPosts!!.text = numPosts
                textViewNickname!!.text = nickname
                Glide.with(mContext!!)
                        .load(Constants.BASE_URL + profilePic)
                        .error(R.mipmap.ic_launcher)
                        .into(imageViewProfilePic!!)
                if (coverPic.isNotEmpty()) {
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + coverPic)
                            .error(R.mipmap.ic_launcher)
                            .into(imageViewProfileCover!!)
                }
                followersTV!!.setOnClickListener {
                    val asf: Fragment = UserListFragment()
                    val args = Bundle()
                    args.putString("query", "followers")
                    args.putString("queryID", profileUsername)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                followingTV!!.setOnClickListener {
                    val asf: Fragment = UserListFragment()
                    val args = Bundle()
                    args.putString("query", "following")
                    args.putString("queryID", profileUsername)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                connectionsTV!!.setOnClickListener {
                    val asf: Fragment = UserListFragment()
                    val args = Bundle()
                    args.putString("query", "connections")
                    args.putString("queryID", profileUsername)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                imageViewProfileCover!!.requestFocus()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun followUser(username: String, followers: String, method: String) {
        if (method == "follow") {
            followProfileButton!!.visibility = View.GONE
            followedProfileButton!!.visibility = View.VISIBLE
            var numFollowers = followers.toInt()
            numFollowers++
            followersCount!!.text = numFollowers.toString()
        } else if (method == "unfollow") {
            followedProfileButton!!.visibility = View.GONE
            followProfileButton!!.visibility = View.VISIBLE
            var numFollowers = followers.toInt()
            numFollowers--
            followersCount!!.text = numFollowers.toString()
        }
        val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_USER, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not follow/unfollow user, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = deviceUsername!!
                params["user_id"] = userID!!
                params["user_followed"] = username
                params["method"] = method
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadProfilenews() {
        val stringRequest = StringRequest(Request.Method.GET, "$Profilenews_URL?userid=$userID&userprofileid=$userProfileID&thisusername=$deviceUsername", { response: String? ->
            try {
                val profilenews = JSONArray(response)
                for (i in 0 until profilenews.length()) {
                    val profilenewsObject = profilenews.getJSONObject(i)
                    val id = profilenewsObject.getInt("id")
                    val type = profilenewsObject.getString("type")
                    val likes = profilenewsObject.getString("likes")
                    val body = profilenewsObject.getString("body")
                    val addedBy = profilenewsObject.getString("added_by")
                    val userTo = profilenewsObject.getString("user_to")
                    val dateAdded = profilenewsObject.getString("date_added")
                    val userClosed = profilenewsObject.getString("user_closed")
                    val deleted = profilenewsObject.getString("deleted")
                    val image = profilenewsObject.getString("image")
                    val userId = profilenewsObject.getString("user_id")
                    val profilePic = profilenewsObject.getString("profile_pic")
                    val verified = profilenewsObject.getString("verified")
                    val online = profilenewsObject.getString("online")
                    val nickname = profilenewsObject.getString("nickname")
                    val username = profilenewsObject.getString("username")
                    val commentcount = profilenewsObject.getString("commentcount")
                    val likedbyuserYes = profilenewsObject.getString("likedbyuseryes")
                    val form = profilenewsObject.getString("form")
                    val edited = profilenewsObject.getString("edited")
                    val profilenewsResult = ProfilenewsRecycler(id, type, likes, body, addedBy, userTo, dateAdded, userClosed, deleted, image, userId, profilePic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited)
                    profilenewsRecyclerList!!.add(profilenewsResult)
                }
                try {
                    if (profilenews.length() == 0) {
                        postsNoPosts!!.visibility = View.VISIBLE
                        postsNoPosts!!.text = mContext!!.getString(R.string.no_posts_to_show)
                    } else {
                        postsNoPosts!!.visibility = View.GONE
                    }
                } catch (ignored: Exception) {
                }
                newsadapter = ProfilenewsAdapter(mContext!!, profilenewsRecyclerList)
                profilenewsView!!.adapter = newsadapter
                profilenewsView?.scheduleLayoutAnimation()
                profilenewsView!!.isNestedScrollingEnabled = false
                profileLayout!!.visibility = View.VISIBLE
                mProgressBar!!.visibility = View.GONE
                profileNewsMoreBtn!!.setOnClickListener {
                    val ldf = SeeAllFragment()
                    val args = Bundle()
                    args.putString("queryid", profileUsername)
                    args.putString("queryidextra", userProfileID)
                    args.putString("method", "posts")
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                profileClansButton!!.setOnClickListener { postsQueryButtonClicked(profileClansButton) }
                publicsPostsButton!!.setOnClickListener { postsQueryButtonClicked(publicsPostsButton) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadProfilePublicsnews() {
        val stringRequest = StringRequest(Request.Method.GET, "$ProfilePublicsNews_URL?username=$profileUsername", { response: String? ->
            try {
                val profilepublicsnews = JSONArray(response)
                for (i in 0 until profilepublicsnews.length()) {
                    val profilenewsObject = profilepublicsnews.getJSONObject(i)
                    val username = profilenewsObject.getString("username")
                    if (SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(username)) continue
                    val id = profilenewsObject.getString("id")
                    val numposts = profilenewsObject.getString("numposts")
                    val subject = profilenewsObject.getString("subject")
                    val date = profilenewsObject.getString("date")
                    val cat = profilenewsObject.getString("cat")
                    val topicBy = profilenewsObject.getString("topic_by")
                    val type = profilenewsObject.getString("type")
                    val userId = profilenewsObject.getString("user_id")
                    val profilePic = profilenewsObject.getString("profile_pic")
                    val nickname = profilenewsObject.getString("nickname")
                    val eventDate = profilenewsObject.getString("event_date")
                    val zone = profilenewsObject.getString("zone")
                    val context = profilenewsObject.getString("context")
                    val numPlayers = profilenewsObject.getString("num_players")
                    val numAdded = profilenewsObject.getString("num_added")
                    val gamename = profilenewsObject.getString("gamename")
                    val publicsTopicResult = PublicsTopicRecycler(id, numposts, subject, date, cat, topicBy, type, userId, profilePic, nickname, username, eventDate, zone, context, numPlayers, numAdded, gamename)
                    profilepublicsnewsRecyclerList!!.add(publicsTopicResult)
                }
                profileNewsMoreBtn!!.setOnClickListener {
                    val ldf = SeeAllFragment()
                    val args = Bundle()
                    args.putString("queryid", profileUsername)
                    args.putString("method", "publics")
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                if (profilepublicsnews.length() == 0) {
                    postsNoPosts!!.visibility = View.VISIBLE
                    postsNoPosts!!.text = getString(R.string.no_posts_to_show)
                } else {
                    postsNoPosts!!.visibility = View.GONE
                }
                publicsnewsadapter = PublicsTopicAdapter(mContext!!, profilepublicsnewsRecyclerList!!)
                profilenewsView!!.adapter = publicsnewsadapter
                profilenewsView?.scheduleLayoutAnimation()
                profilenewsView!!.isNestedScrollingEnabled = false
                profileClansButton!!.setOnClickListener { postsQueryButtonClicked(profileClansButton) }
                profilePostsButton!!.setOnClickListener { postsQueryButtonClicked(profilePostsButton) }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadJoinedClans() {
        val stringRequest = StringRequest(Request.Method.GET, "$URL_JOINED_CLANS?userid=$userProfileID&username=$profileUsername",
                { response: String? ->
                    try {
                        val res = JSONObject(response!!)
                        val thread = res.getJSONArray("clans")
                        for (i in 0 until thread.length()) {
                            val obj = thread.getJSONObject(i)
                            val position = obj.getString("position")
                            val tag = obj.getString("tag")
                            val name = obj.getString("name")
                            val numMembers = obj.getString("num_members")
                            val insignia = obj.getString("insignia")
                            val games = obj.getString("games")
                            val id = obj.getString("id")
                            val avg = obj.getString("avg")
                            val clansObject = ClansRecycler(position, tag, name, numMembers, insignia, games, id, avg)
                            clans!!.add(clansObject)
                        }
                        if (thread.length() == 0) {
                            postsNoPosts!!.visibility = View.VISIBLE
                            postsNoPosts!!.text = getString(R.string.no_clans_text)
                        }
                        mProgressBar!!.visibility = View.GONE
                        clansAdapter = JoinedClansAdapter(mContext!!, clans!!)
                        profilenewsView!!.adapter = clansAdapter
                        profilenewsView?.scheduleLayoutAnimation()
                        profilenewsView!!.isNestedScrollingEnabled = false
                        publicsPostsButton!!.setOnClickListener { postsQueryButtonClicked(publicsPostsButton) }
                        profilePostsButton!!.setOnClickListener { postsQueryButtonClicked(profilePostsButton) }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
        ) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun connectionRequest(thisUsername: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, GET_REQUESTS, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (!obj.getBoolean("error")) {
                    if (obj.has("request_sent") && obj.getString("request_sent") == "yes") {
                        requestSentFriendButton!!.visibility = View.VISIBLE
                        addFriendButton!!.visibility = View.GONE
                        addFriendProgress!!.visibility = View.GONE
                    } else if (obj.has("request_received") && obj.getString("request_received") == "yes") {
                        requestedFriendButton!!.visibility = View.VISIBLE
                        addFriendButton!!.visibility = View.GONE
                        addFriendProgress!!.visibility = View.GONE
                    } else if (!(addedFriendButton!!.visibility == View.VISIBLE || userID == userProfileID)) {
                        addFriendButton!!.visibility = View.VISIBLE
                        addFriendProgress!!.visibility = View.GONE
                    }
                } else {
                    Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { Toast.makeText(mContext, "Could not get requests, please try again later...", Toast.LENGTH_LONG).show() }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["username"] = deviceUsername!!
                params["thisusername"] = thisUsername
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun addConnection(thisusername: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, ADD_CONNECTION, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (!obj.getBoolean("error")) {
                    if (obj.getString("request_sent") == "yes") {
                        Toast.makeText(mContext, "Request Sent!", Toast.LENGTH_LONG).show()
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
                params["username"] = deviceUsername!!
                params["thisusername"] = thisusername
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun acceptConnection(thisusername: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, ACCEPT_CONNECTION, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (!obj.getBoolean("error")) {
                    if (obj.getString("request_accepted") == "yes") {
                        addedFriendButton!!.visibility = View.VISIBLE
                        requestedFriendButton!!.visibility = View.GONE
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
                params["username"] = deviceUsername!!
                params["thisusername"] = thisusername
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val resultUri = result.uri
                var bitmap1: Bitmap? = null
                try {
                    if (Build.VERSION.SDK_INT >= 29) {
                        val source: ImageDecoder.Source = ImageDecoder.createSource(mContext!!.contentResolver, resultUri)
                        try {
                            bitmap1 = ImageDecoder.decodeBitmap(source)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    } else {
                        try {
                            bitmap1 = MediaStore.Images.Media.getBitmap(mContext!!.contentResolver, resultUri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }

                    if (Build.VERSION.SDK_INT < 29){
                        imageUploadBtn!!.setImageBitmap(bitmap1)
                    }else{
                        imageUploadBtn?.setImageResource(R.drawable.icons8_question_mark_64)
                        activity?.toastLong("Cannot display image cropped! (Android 10+ temporary issue, upload should work as usual.)")
                    }

                    imageToUpload = bitmap1
                } catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(activity, "Failed! Error: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestMultiplePermissions() {
        Dexter.withActivity(activity)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.isAnyPermissionPermanentlyDenied) {
                            Toast.makeText(mContext!!.applicationContext, "No permissions are granted by user!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { Toast.makeText(mContext!!.applicationContext, "Some Error! ", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    private fun postsQueryButtonClicked(clicked: Button?) {
        when {
            clicked === profilePostsButton -> {
                profilePostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.green))
                profileClansButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                publicsPostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                //TODO add progressbar
                profilenewsRecyclerList!!.clear()
                loadProfilenews()
                textViewTvPosts!!.visibility = View.VISIBLE
                profileItemsLabel!!.text = mContext!!.getString(R.string.profile_posts_text)
                profileNewsMoreBtn!!.visibility = View.VISIBLE
            }
            clicked === profileClansButton -> {
                profilePostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                publicsPostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                profileClansButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.green))
                clans!!.clear()
                loadJoinedClans()
                textViewTvPosts!!.visibility = View.GONE
                profileItemsLabel!!.text = mContext!!.getString(R.string.clans_joined_text)
                //TODO: Fix this with see all fragment --> clans
                profileNewsMoreBtn!!.visibility = View.GONE
            }
            clicked === publicsPostsButton -> {
                profileClansButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                profilePostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                publicsPostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.green))
                profilepublicsnewsRecyclerList!!.clear()
                loadProfilePublicsnews()
                textViewTvPosts!!.visibility = View.GONE
                profileItemsLabel!!.text = mContext!!.getString(R.string.publics_posts_text)
                profileNewsMoreBtn!!.visibility = View.VISIBLE
            }
        }
    }

    private fun getUserID(username: String?) {
        try {
            val stringRequest: StringRequest = object : StringRequest(Method.POST, GET_USER_ID, Response.Listener { response: String? ->
                try {
                    val obj = JSONObject(response!!)
                    if (obj.getString("error") == "false") {
                        userProfileID = obj.getString("userid")
                        loadProfileTop()
                    } else {
                        profileErrorScreen!!.visibility = View.VISIBLE
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["username"] = username!!
                    return params
                }
            }
            (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
        } catch (e: Exception) {
            // stringrequest error
        }
    }

    private fun openCropper() {
        CropImage.activity()
                .start(requireContext(), this)
    }

    companion object {
        private const val TAG = "ProfileFragment"
        private const val RG = ROOT_URL + "new_post.php"
        private const val Profilenews_URL = ROOT_URL + "profilenews_api.php"
        private const val ProfilePublicsNews_URL = ROOT_URL + "profilepublicsnews_api.php"
        private const val ProfileTop_URL = ROOT_URL + "profiletop_api.php"
        private const val GET_REQUESTS = ROOT_URL + "get_profile_requests.php"
        private const val ADD_CONNECTION = ROOT_URL + "add_connection.php"
        private const val FOLLOW_USER = ROOT_URL + "user_follow_api.php"
        private const val ACCEPT_CONNECTION = ROOT_URL + "accept_connection.php"
        private const val UPLOAD_IMAGE_URL = ROOT_URL + "new_post_image_upload.php"
        const val URL_JOINED_CLANS: String = ROOT_URL + "user_joined_clans.php"
        const val GET_USER_ID: String = ROOT_URL + "get_userid.php"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}