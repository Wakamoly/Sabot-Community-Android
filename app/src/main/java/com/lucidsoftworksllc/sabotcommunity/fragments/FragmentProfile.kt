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
import androidx.lifecycle.Observer
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
import com.lucidsoftworksllc.sabotcommunity.databinding.FragmentProfileBinding
import com.lucidsoftworksllc.sabotcommunity.fragments.repositories.ProfileRepo
import com.lucidsoftworksllc.sabotcommunity.fragments.viewmodels.ProfileVM
import com.lucidsoftworksllc.sabotcommunity.models.ClansRecycler
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.PublicsTopicRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.ProfileTopModel
import com.lucidsoftworksllc.sabotcommunity.network.ProfileApi
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseFragment
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import com.theartofdev.edmodo.cropper.CropImage
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.snippet_top_profilebar.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class FragmentProfile : BaseFragment<ProfileVM, FragmentProfileBinding, ProfileRepo>() {
    private var newsadapter: ProfilenewsAdapter? = null
    private var publicsnewsadapter: PublicsTopicAdapter? = null
    private var clansAdapter: JoinedClansAdapter? = null
    private var profilenewsRecyclerList: MutableList<ProfilenewsRecycler>? = null
    private var profilepublicsnewsRecyclerList: MutableList<PublicsTopicRecycler>? = null
    private var clans: MutableList<ClansRecycler>? = null

    private var mContext: Context? = null
    private var imageToUpload: Bitmap? = null
    private var profileUsername: String? = null
    private var rQueue: RequestQueue? = null
    private var jsonObject: JSONObject? = null
    private var userProfileID: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mContext = activity

        //imageToUpload = null

        profilenewsRecyclerList = ArrayList()
        profilepublicsnewsRecyclerList = ArrayList()
        clans = ArrayList()

        recyclerProfilenews.setHasFixedSize(true)
        recyclerProfilenews.layoutManager = LinearLayoutManager(mContext)

        profileRefreshLayout.setOnRefreshListener { refreshProfile() }

        addItemButton?.setOnClickListener {
            if (addPostLayout.visibility != View.GONE) {
                addPostLayout.visibility = View.GONE
            } else {
                addPostLayout.visibility = View.VISIBLE
                statusUpdate.requestFocus()
                if (statusUpdate.hasFocus()) {
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

        profileClansButtons.setOnClickListener { postsQueryButtonClicked(profileClansButtons) }
        publicsPostsButtons.setOnClickListener { postsQueryButtonClicked(publicsPostsButtons) }
        profilePostsButton.setOnClickListener { postsQueryButtonClicked(profilePostsButton) }

        subscribeObservers()

    }

    private fun refreshProfile(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
        } else {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }
        profileRefreshLayout.isRefreshing = false
        profileCover.requestFocus()
    }

    private fun subscribeObservers(){
        if (arguments != null) {
            if (requireArguments().getString("UserId") != null) {
                userProfileID = requireArguments().getString("UserId")
                viewModel.getProfileTop(deviceUserID!!, userProfileID!!.toInt(), deviceUsername.toString())
            } else if (requireArguments().getString("Username") != null && requireArguments().getString("UserId") == null) {
                getUserID(requireArguments().getString("Username"))
            } else {
                userProfileID = deviceUserID.toString()
                viewModel.getProfileTop(deviceUserID!!, userProfileID!!.toInt(), deviceUsername.toString())
            }
        } else {
            userProfileID = deviceUserID.toString()
            viewModel.getProfileTop(deviceUserID!!, userProfileID!!.toInt(), deviceUsername.toString())
        }


        viewModel.profileTop.observe(viewLifecycleOwner, Observer{
            when(it){
                is DataState.Success -> {
                    //profileLoadingScreen.visible(false)
                    profileLayout.visible(true)
                    updateProfileTopUI(it.data)
                }
                is DataState.Loading -> {
                    profileLoadingScreen.visible(true)
                }
                is DataState.Failure -> handleApiError(it) {
                    refreshProfile()
                }
            }
        })

        viewModel.profileNews.observe(viewLifecycleOwner, {
            when (it) {
                is DataState.Success -> {
                    updateProfileNewsUI(it.data)
                }
                is DataState.Loading -> {
                    // TODO: 10/22/20 Add progress bar
                }
            }
        })

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
                    statusUpdate.setText("")
                    profileCover.requestFocus()
                } else {
                    profileLoadingScreen.visible(false)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            profileLoadingScreen.visible(false)
            mContext?.toastShort("Error on Response, please try again later...")
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["added_by"] = added_by
                params["user_to"] = user_to
                params["user_id"] = deviceUserID!!.toString()
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
                            mContext?.toastShort(jsonObject.getString("message"))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        mContext?.toastShort("Failed to upload!")
                    }
                }) { }
        rQueue = Volley.newRequestQueue(mContext)
        rQueue?.add(jsonObjectRequest)
    }

    private fun updateProfileTopUI(data: ProfileTopModel){
        val id = data.id
        val nickname = data.nickname
        val username = data.username
        val description = data.description
        val verified = data.verified
        val profilePic = data.profile_pic
        val coverPic = data.cover_pic
        val numPosts = data.num_posts
        val userClosed = data.user_closed
        val userBanned = data.user_banned
        val numFriends = data.num_friends
        val followings = data.followings
        val followers = data.followers
        val twitch = data.twitch
        val mixer = data.mixer
        val psn = data.psn
        val xbox = data.xbox
        val discord = data.discord
        val steam = data.steam
        val instagram = data.instagram
        val youtube = data.youtube
        val lastOnline = data.last_online
        val count = data.count
        val average = data.average
        val clantag = data.clantag
        val blocked = data.blocked
        val supporter = data.supporter
        val discordUser = data.discord_user
        val twitter = data.twitter
        val website = data.website
        val nintendo = data.nintendo
        val isFollowing = data.isFollowing
        val isConnected = data.isConnected
        val connections = data.connections

        profileUsername = username
        if (deviceUserID == userProfileID!!.toInt()) {
            addMessageButton.visible(false)
            addFriendButton.visible(false)
            addFriendProgress.visible(false)
            followProfileButton.visible(false)
            editProfileButton.visible(true)
            setProfileCoverButton.visible(true)
            setProfilePhotoButton.visible(true)
        }
        if (blocked == "yes" || SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(username)) {
            profileErrorScreen.visible(true)
            profileLayout.visible(false)
            profileLoadingScreen.visible(false)
        } else if (userClosed == "yes" || userBanned == "yes") {
            profileDisabledScreen.visible(true)
            profileLayout.visible(false)
            profileLoadingScreen.visible(false)
        } else {
            postsQueryButtonClicked(profilePostsButton)
        }
        if (clantag != "") textViewClanTag!!.text = String.format("[%s]", clantag)
        if (supporter == "yes") {
            starIcon.visible(true)
        }
        if (discordUser.isNotEmpty()) {
            userDiscordProfileDetails.visible(true)
            userDiscordProfile.text = discordUser
        }
        if (twitter.isNotEmpty()) {
            userTwitterDetails.visible(true)
            userTwitter.text = twitter
            userTwitterDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/$twitter")))
                }
            }
        }
        if (website.isNotEmpty()) {
            profileWebsiteContainer.visible(true)
            profileWebsite.text = website
            profileWebsiteContainer.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website)))
                }
            }
        }
        if (nintendo.isNotEmpty()) {
            userSwitchDetails.visible(true)
            userSwitch.text = nintendo
        }
        profileCover.setOnClickListener {
            val asf: Fragment = PhotoViewFragment()
            val args = Bundle()
            args.putString("image", coverPic)
            asf.arguments = args
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            fragmentTransaction.replace(R.id.fragment_container, asf)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        imageViewProfilePic.setOnClickListener {
            val asf: Fragment = PhotoViewFragment()
            val args = Bundle()
            args.putString("image", profilePic)
            asf.arguments = args
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
            fragmentTransaction.replace(R.id.fragment_container, asf)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        addMessageButton.setOnClickListener { startActivity(Intent(mContext, ChatActivity::class.java).putExtra("user_to", username)) }
        followersCount.text = followers.toString()
        followingCount.text = followings.toString()
        friendsCount.text = numFriends.toString()
        if (twitch.isNotEmpty()) {
            userTwitch.text = twitch
            userTwitchDetails.visible(true)
            userTwitchDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitch.tv/$twitch")))
                }
            }
        }
        if (mixer.isNotEmpty()) {
            userMixer.text = mixer
            userMixerDetails.visible(true)
            userMixerDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://mixer.com/$mixer")))
                }
            }
        }
        if (psn.isNotEmpty()) {
            userPSN.text = psn
            userPSNDetails.visible(true)
            userPSNDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://psnprofiles.com/$psn")))
                }
            }
        }
        if (xbox.isNotEmpty()) {
            userXbox.text = xbox
            userXboxDetails.visible(true)
            userXboxDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://account.xbox.com/en-us/profile?gamertag=$xbox")))
                }
            }
        }
        if (discord.isNotEmpty()) {
            userDiscord.setText(R.string.discord_server_text)
            userDiscordDetails.visible(true)
            userDiscordDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discord.gg/$discord")))
                }
            }
        }
        if (steam.isNotEmpty()) {
            userSteam.text = steam
            userSteamDetails.visible(true)
            userSteamDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://steamcommunity.com/id/$steam")))
                }
            }
        }
        if (youtube.isNotEmpty()) {
            userYoutubeDetails.visible(true)
            userYoutubeDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/channel/$youtube")))
                }
            }
        }
        if (instagram.isNotEmpty()) {
            userInstagram.text = instagram
            userInstagramDetails.visible(true)
            userInstagramDetails.setOnClickListener {
                if (mContext is FragmentContainer) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/$instagram")))
                }
            }
        }
        val averageFloat = average.toFloat()
        profileRating.rating = averageFloat
        reviewCount.text = count.toString()
        val usernameTo: String
        if (SharedPrefManager.getInstance(mContext!!)!!.username != username) {
            usernameTo = username
            if (isFollowing == "yes") {
                followProfileButton.visible(false)
                followedProfileButton.visible(true)
            }
            if (isConnected == "yes") {
                addedFriendButton.visible(true)
                addFriendProgress.visible(false)
                addItemButton.visible(true)
            } else {
                connectionRequest(username)
            }
            if (isConnected != "yes" && userProfileID!!.toInt() != deviceUserID) {
                addItemButton.visible(false)
            }
        } else {
            // profile is device user's
            usernameTo = "none"
            if (SharedPrefManager.getInstance(mContext!!)!!.profilePic != profilePic) {
                SharedPrefManager.getInstance(mContext!!)!!.profilePic = profilePic
            }
        }
        submitStatusButton!!.setOnClickListener { view: View ->
            val body = statusUpdate!!.text.toString()
            val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
            val spinnerText = postTypeSpinner!!.selectedItem.toString()
            val form = "user"
            if (statusUpdate!!.text.toString().isNotEmpty() && spinnerText.isNotEmpty()) {
                profileLayout.visible(false)
                profileLoadingScreen.visible(true)
                submitStatus(body, addedBy!!, usernameTo, spinnerText, form)
                hideKeyboardFrom(mContext, view)
            } else {
                mContext?.toastShort("You must enter text before submitting!")
            }
        }
        profileRatingContainer!!.setOnClickListener {
            val ldf = PlayerReviewFragment()
            val args = Bundle()
            args.putString("UserId", id.toString())
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
                    args.putString("UserId", id.toString())
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
                    args.putString("id", id.toString())
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
                        mContext?.toastLong("Are you really trying to block yourself?")
                    }
                }
                true
            }
            popup.show()
        }
        followProfileButton.setOnClickListener { followUser(username, followers.toString(), "follow") }
        followedProfileButton.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_friend_add)
                    .setTitle("Unfollow @$username?")
                    .setPositiveButton(R.string.yes) { followUser(username, followers.toString(), "unfollow") }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
        addFriendButton.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_friend_add)
                    .setTitle("Connection")
                    .setMessage("Connecting is PERMANENT as this user will be able to review you at any time if accepted.\n\nConnect with @$username?")
                    .setPositiveButton(R.string.yes) {
                        addFriendButton.visible(false)
                        requestSentFriendButton.visible(true)
                        addConnection(username)
                        followUser(username, followers.toString(), "follow")
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
        requestedFriendButton.setOnClickListener {
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
                        followUser(username, followers.toString(), "follow")
                    }
                    .setNegativeButton(R.string.no, null)
                    .show()
        }
        requestSentFriendButton.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_friend_add)
                    .setTitle("Connection")
                    .setMessage("You are in the process of being permanently connected! If you're having trouble with this user, consider blocking or reporting.")
                    .show()
        }
        addedFriendButton.setOnClickListener {
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_friend_add)
                    .setTitle("Connection")
                    .setMessage("You are permanently connected! If you're having trouble with this user, consider blocking or reporting.")
                    .show()
        }
        if (description != "") {
            textViewDescription.text = description
            profileStatusContainer.visibility = View.VISIBLE
        }
        if (verified == "yes") {
            verifiedIcon.visibility = View.VISIBLE
        }
        if (lastOnline == "yes") {
            profileOnlineIcon.visibility = View.VISIBLE
        }
        textViewUsername.text = String.format("@%s", username)
        tvPosts.text = numPosts.toString()
        textViewNickname.text = nickname
        Glide.with(mContext!!)
                .load(Constants.BASE_URL + profilePic)
                .error(R.mipmap.ic_launcher)
                .into(imageViewProfilePic)
        if (coverPic.isNotEmpty()) {
            Glide.with(mContext!!)
                    .load(Constants.BASE_URL + coverPic)
                    .error(R.mipmap.ic_launcher)
                    .into(profileCover)
        }
        binding.followers.setOnClickListener {
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
        binding.following.setOnClickListener {
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
        binding.connections.setOnClickListener {
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
        profileCover.requestFocus()
    }


    private fun followUser(username: String, followers: String, method: String) {
        if (method == "follow") {
            followProfileButton.visible(false)
            followedProfileButton.visible(true)
            var numFollowers = followers.toInt()
            numFollowers++
            followersCount!!.text = numFollowers.toString()
        } else if (method == "unfollow") {
            followedProfileButton.visible(false)
            followProfileButton.visible(true)
            var numFollowers = followers.toInt()
            numFollowers--
            followersCount.text = numFollowers.toString()
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
                params["user_id"] = deviceUserID!!.toString()
                params["user_followed"] = username
                params["method"] = method
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun updateProfileNewsUI(data: List<ProfilenewsRecycler>){
        if (data.isEmpty()) {
            postsNoPosts.visible(true)
            postsNoPosts.text = mContext!!.getString(R.string.no_posts_to_show)
        } else {
            postsNoPosts.visible(false)
        }
        newsadapter = ProfilenewsAdapter(mContext!!)
        newsadapter!!.addItems(data)
        recyclerProfilenews.adapter = newsadapter
        recyclerProfilenews.scheduleLayoutAnimation()
        recyclerProfilenews.isNestedScrollingEnabled = false
        profileLoadingScreen.visible(false)
        profileNewsMoreBtn.setOnClickListener {
            val ldf = SeeAllFragment()
            val args = Bundle()
            args.putString("queryid", profileUsername)
            args.putString("queryidextra", userProfileID.toString())
            args.putString("method", "posts")
            ldf.arguments = args
            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
    }

    /*private fun loadProfilenews() {
        val stringRequest = StringRequest(Request.Method.GET, "$Profilenews_URL?userid=$deviceUserID&userprofileid=$userProfileID&thisusername=$deviceUsername", { response: String? ->
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
                recyclerProfilenews.adapter = newsadapter
                recyclerProfilenews.scheduleLayoutAnimation()
                recyclerProfilenews.isNestedScrollingEnabled = false
                profileLayout.visible(true)
                profileLoadingScreen.visible(false)
                profileNewsMoreBtn.setOnClickListener {
                    val ldf = SeeAllFragment()
                    val args = Bundle()
                    args.putString("queryid", profileUsername)
                    args.putString("queryidextra", userProfileID.toString())
                    args.putString("method", "posts")
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }*/

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
                    postsNoPosts.visible(true)
                    postsNoPosts.text = getString(R.string.no_posts_to_show)
                } else {
                    postsNoPosts.visible(false)
                }
                publicsnewsadapter = PublicsTopicAdapter(mContext!!, profilepublicsnewsRecyclerList!!)
                recyclerProfilenews.adapter = publicsnewsadapter
                recyclerProfilenews.scheduleLayoutAnimation()
                recyclerProfilenews.isNestedScrollingEnabled = false
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
                            postsNoPosts.visible(true)
                            postsNoPosts.text = getString(R.string.no_clans_text)
                        }
                        profileLoadingScreen.visible(false)
                        clansAdapter = JoinedClansAdapter(mContext!!)
                        clansAdapter?.addItems(clans)
                        recyclerProfilenews.adapter = clansAdapter
                        recyclerProfilenews.scheduleLayoutAnimation()
                        recyclerProfilenews.isNestedScrollingEnabled = false
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
                        requestSentFriendButton.visible(true)
                        addFriendButton.visible(false)
                        addFriendProgress.visible(false)
                    } else if (obj.has("request_received") && obj.getString("request_received") == "yes") {
                        requestedFriendButton.visible(true)
                        addFriendButton.visible(false)
                        addFriendProgress.visible(false)
                    } else if (!(addedFriendButton.visibility == View.VISIBLE || deviceUserID == userProfileID!!.toInt())) {
                        addFriendButton.visible(true)
                        addFriendProgress.visible(false)
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
                        addedFriendButton.visible(true)
                        requestedFriendButton.visible(false)
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
                profilePostsButton.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.green))
                profileClansButtons.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                publicsPostsButtons.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                //TODO add progressbar
                profilenewsRecyclerList!!.clear()
                viewModel.getProfileNews(deviceUserID!!.toInt(), userProfileID!!.toInt(), deviceUsername.toString())
                tvPosts.visible(true)
                profileItemsLabel.text = mContext!!.getString(R.string.profile_posts_text)
                profileNewsMoreBtn.visible(true)
            }
            clicked === profileClansButtons -> {
                profilePostsButton.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                publicsPostsButtons.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                profileClansButtons.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.green))
                clans!!.clear()
                loadJoinedClans()
                tvPosts.visible(false)
                profileItemsLabel.text = mContext!!.getString(R.string.clans_joined_text)
                //TODO: Fix this with see all fragment --> clans
                profileNewsMoreBtn.visible(true)
            }
            clicked === publicsPostsButtons -> {
                profileClansButtons!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                profilePostsButton!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.grey_80))
                publicsPostsButtons!!.setBackgroundColor(ContextCompat.getColor(mContext!!, R.color.green))
                profilepublicsnewsRecyclerList!!.clear()
                loadProfilePublicsnews()
                tvPosts.visible(false)
                profileItemsLabel.text = mContext!!.getString(R.string.publics_posts_text)
                profileNewsMoreBtn.visible(true)
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
                        viewModel.getProfileTop(deviceUserID!!, userProfileID!!.toInt(), deviceUsername.toString())
                    } else {
                        profileErrorScreen.visible(true)
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

    override fun getViewModel() = ProfileVM::class.java
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) = FragmentProfileBinding.inflate(inflater, container, false)
    override fun getFragmentRepository() = ProfileRepo(remoteDataSource.buildApi(ProfileApi::class.java, mContext?.fcmToken))
}