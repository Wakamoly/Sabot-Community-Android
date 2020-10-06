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
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.ProfilenewsAdapter
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.others.toastLong
import com.theartofdev.edmodo.cropper.CropImage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ClanFragment : Fragment() {
    private var clanEventsRecycler: RecyclerView? = null
    private var clanNewsRecycler: RecyclerView? = null
    private var clanNewsAdapter: ProfilenewsAdapter? = null
    private var clanNewsRecyclerList: MutableList<ProfilenewsRecycler>? = null
    private var textViewTvClicks: TextView? = null
    private var textViewTvReviews: TextView? = null
    private var eventsCount: TextView? = null
    private var textViewClanLow: TextView? = null
    private var textViewClanName: TextView? = null
    private var textViewClanDescription: TextView? = null
    private var clanProfileTags: TextView? = null
    private var textViewMembersNum: TextView? = null
    private var clanCreatedOn: TextView? = null
    private var tvPosts: TextView? = null
    private var tvClanEvents: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var followProgressClan: ProgressBar? = null
    private var newProgress: ProgressBar? = null
    private var imageButtonWebsite: ImageView? = null
    private var backArrow: ImageView? = null
    private var clanClickMenu: ImageView? = null
    private var imageViewClanPic: ImageView? = null
    private var imageViewClanBackPic: ImageView? = null
    private var imageViewFacebook: ImageView? = null
    private var imageViewTwitter: ImageView? = null
    private var imageViewYoutube: ImageView? = null
    private var imageViewInstagram: ImageView? = null
    private var imageViewDiscord: ImageView? = null
    private var imageUploadBtn2: ImageView? = null
    private var toolbar: Toolbar? = null
    private var mContext: Context? = null
    private var clanLayout: RelativeLayout? = null
    private var errorLayout: RelativeLayout? = null
    private var clanRating: SimpleRatingBar? = null
    private var clanRatingContainer: LinearLayout? = null
    private var clanReviewsContainer: LinearLayout? = null
    private var memberLayout: LinearLayout? = null
    private var addPostLayout: LinearLayout? = null
    private var clanEventsSpotlight: LinearLayout? = null
    private var clanNewsSpotlight: LinearLayout? = null
    private var clanActionBtn: Button? = null
    private var clanActionBtnJoined: Button? = null
    private var clanActionBtnRequested: Button? = null
    private var clanActionAdmin: Button? = null
    private var clanNewPost: Button? = null
    private var clanNewEvent: Button? = null
    private var submitStatusButton: Button? = null
    private var clanNewsMoreBtn: Button? = null
    private var userID: String? = null
    private var username: String? = null
    private var clanID: String? = null
    private var statusUpdate: EditText? = null
    private var postTypeSpinner: Spinner? = null
    private var imageToUpload: Bitmap? = null
    var rQueue: RequestQueue? = null
    var jsonObject: JSONObject? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clanRootView = inflater.inflate(R.layout.fragment_clan, null)
        clanReviewsContainer = clanRootView.findViewById(R.id.clanReviewsContainer)
        eventsCount = clanRootView.findViewById(R.id.eventsCount)
        mProgressBar = clanRootView.findViewById(R.id.clanCatProgressBar)
        textViewClanName = clanRootView.findViewById(R.id.textViewClanName)
        textViewClanLow = clanRootView.findViewById(R.id.textViewClanLow)
        imageViewFacebook = clanRootView.findViewById(R.id.clanFacebookPage)
        imageViewTwitter = clanRootView.findViewById(R.id.clanTwitterPage)
        imageViewYoutube = clanRootView.findViewById(R.id.clanYoutubePage)
        imageViewInstagram = clanRootView.findViewById(R.id.clanInstagramPage)
        imageButtonWebsite = clanRootView.findViewById(R.id.clanWebsiteBtn)
        imageViewDiscord = clanRootView.findViewById(R.id.clanDiscordChannel)
        textViewTvClicks = clanRootView.findViewById(R.id.tvClicks)
        textViewMembersNum = clanRootView.findViewById(R.id.membersNum)
        textViewTvReviews = clanRootView.findViewById(R.id.numReviews)
        textViewClanDescription = clanRootView.findViewById(R.id.textViewClanDescription)
        clanProfileTags = clanRootView.findViewById(R.id.clanProfileDetails)
        imageViewClanPic = clanRootView.findViewById(R.id.imageViewClanPic)
        imageViewClanBackPic = clanRootView.findViewById(R.id.imageViewClanBackPic)
        clanClickMenu = clanRootView.findViewById(R.id.clanClickMenu)
        toolbar = clanRootView.findViewById(R.id.clanToolBar)
        clanLayout = clanRootView.findViewById(R.id.clanLayout)
        clanRating = clanRootView.findViewById(R.id.ratingBar)
        clanRatingContainer = clanRootView.findViewById(R.id.clanRatingContainer)
        clanActionBtn = clanRootView.findViewById(R.id.clanActionBtn)
        followProgressClan = clanRootView.findViewById(R.id.followProgressClan)
        clanActionBtnJoined = clanRootView.findViewById(R.id.clanActionBtnJoined)
        clanActionBtnRequested = clanRootView.findViewById(R.id.clanActionBtnRequested)
        clanActionAdmin = clanRootView.findViewById(R.id.clanActionAdmin)
        clanCreatedOn = clanRootView.findViewById(R.id.clanCreatedOn)
        memberLayout = clanRootView.findViewById(R.id.memberLayout)
        addPostLayout = clanRootView.findViewById(R.id.addPostLayout)
        clanNewPost = clanRootView.findViewById(R.id.clanNewPost)
        statusUpdate = clanRootView.findViewById(R.id.statusUpdate)
        clanNewEvent = clanRootView.findViewById(R.id.clanNewEvent)
        tvPosts = clanRootView.findViewById(R.id.tvPosts)
        tvClanEvents = clanRootView.findViewById(R.id.tvClanEvents)
        clanEventsSpotlight = clanRootView.findViewById(R.id.clanEventsSpotlight)
        clanNewsSpotlight = clanRootView.findViewById(R.id.clanNewsSpotlight)
        postTypeSpinner = clanRootView.findViewById(R.id.postTypeSpinner)
        imageUploadBtn2 = clanRootView.findViewById(R.id.imageUploadBtn2)
        submitStatusButton = clanRootView.findViewById(R.id.submitStatusButton)
        newProgress = clanRootView.findViewById(R.id.newProgress)
        clanClickMenu = clanRootView.findViewById(R.id.clanClickMenu)
        errorLayout = clanRootView.findViewById(R.id.errorLayout)
        backArrow = clanRootView.findViewById(R.id.backArrow)
        clanNewsMoreBtn = clanRootView.findViewById(R.id.profileNewsMoreBtn)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        clanID = requireArguments().getString("ClanId")
        clanNewsRecyclerList = ArrayList()
        clanNewsRecycler = clanRootView.findViewById(R.id.recyclerProfilenews)
        clanNewsRecycler?.setHasFixedSize(true)
        clanNewsRecycler?.layoutManager = LinearLayoutManager(mContext)
        clanNewsRecycler?.isNestedScrollingEnabled = false

        //clanEventsRecyclerList = new ArrayList<>();
        clanEventsRecycler = clanRootView.findViewById(R.id.recyclerClanEvents)
        clanEventsRecycler?.setHasFixedSize(true)
        clanEventsRecycler?.layoutManager = LinearLayoutManager(mContext)
        clanEventsRecycler?.isNestedScrollingEnabled = false
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        /*refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof ClanFragment) {
                    FragmentTransaction fragTransaction =   (getActivity()).getSupportFragmentManager().beginTransaction();
                    fragTransaction.detach(currentFragment);
                    fragTransaction.attach(currentFragment);
                    fragTransaction.commit();
                }
                profileRefreshLayout.setRefreshing(false);
                textViewUsername.requestFocus();
            }
        });*/
        loadClanTop()
        return clanRootView
    }

    private fun openCropper() {
        requestMultiplePermissions()
        CropImage.activity()
                .start(requireContext(), this)
    }

    private fun loadClanTop() {
        val stringRequest = StringRequest(Request.Method.GET, "$CLAN_TOP?userid=$userID&username=$username&clanid=$clanID", { response: String? ->
            try {
                val profiletop = JSONArray(response!!)
                val profiletopObject = profiletop.getJSONObject(0)
                val banned = profiletopObject.getString("banned")
                val deleted = profiletopObject.getString("deleted")
                if (banned == "no" && deleted == "no") {
                    val id = profiletopObject.getString("id")
                    val name = profiletopObject.getString("name")
                    val tag = profiletopObject.getString("tag")
                    val members = profiletopObject.getString("members")
                    val numMembers = profiletopObject.getString("num_members")
                    val insignia = profiletopObject.getString("insignia")
                    val background = profiletopObject.getString("background")
                    val numSearches = profiletopObject.getString("num_searches")
                    val avgrating = profiletopObject.getString("avgrating")
                    val facebook = profiletopObject.getString("facebook")
                    val twitter = profiletopObject.getString("twitter")
                    val youtube = profiletopObject.getString("youtube")
                    val instagram = profiletopObject.getString("instagram")
                    val owner = profiletopObject.getString("owner")
                    val createdOn = profiletopObject.getString("created_on")
                    val description = profiletopObject.getString("description")
                    val games = profiletopObject.getString("games")
                    val numEvents = profiletopObject.getString("num_events")
                    val numReviews = profiletopObject.getString("num_reviews")
                    val discord = profiletopObject.getString("discord")
                    val website = profiletopObject.getString("website")
                    val numPosts = profiletopObject.getString("num_posts")
                    tvPosts!!.text = numPosts
                    tvClanEvents!!.text = numEvents
                    val array = members.split(",".toRegex()).toTypedArray()

                    //if player is owner of clan
                    when {
                        owner == username -> {
                            followProgressClan!!.visibility = View.GONE
                            clanActionAdmin!!.visibility = View.VISIBLE
                            clanActionAdmin!!.setOnClickListener {
                                val asf: Fragment = PhotoViewFragment()
                                val args = Bundle()
                                args.putString("ClanId", clanID)
                                asf.arguments = args
                                val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                                fragmentTransaction.replace(R.id.fragment_container, asf)
                                fragmentTransaction.addToBackStack(null)
                                fragmentTransaction.commit()
                            }
                            memberLayout!!.visibility = View.VISIBLE
                            clanNewEvent!!.setOnClickListener { Toast.makeText(mContext, "Coming Soon!", Toast.LENGTH_SHORT).show() }
                            clanNewPost!!.setOnClickListener {
                                addPostLayout!!.visibility = View.VISIBLE
                                statusUpdate!!.isFocusable = true
                                imageUploadBtn2!!.setOnClickListener {
                                    requestMultiplePermissions()
                                    openCropper()
                                }
                                submitStatusButton!!.setOnClickListener { view: View ->
                                    val body = statusUpdate!!.text.toString()
                                    val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
                                    val spinnerText = postTypeSpinner!!.selectedItem.toString()
                                    val form = "clan"
                                    if (statusUpdate!!.text.toString().isNotEmpty() && spinnerText.isNotEmpty()) {
                                        clanLayout!!.visibility = View.GONE
                                        mProgressBar!!.visibility = View.VISIBLE
                                        submitClanPost(body, addedBy!!, tag, spinnerText, form)
                                        hideKeyboardFrom(mContext, view)
                                    } else {
                                        Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                            clanActionAdmin!!.setOnClickListener {
                                val ldf = ClanAdminPanel()
                                val args = Bundle()
                                args.putString("ClanId", id)
                                ldf.arguments = args
                                requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                        }
                        listOf(*array).contains(username) -> {
                            followProgressClan!!.visibility = View.GONE
                            clanActionBtnJoined!!.visibility = View.VISIBLE
                            memberLayout!!.visibility = View.VISIBLE
                            clanNewEvent!!.setOnClickListener { Toast.makeText(mContext, "Coming Soon!", Toast.LENGTH_SHORT).show() }
                            clanNewPost!!.setOnClickListener {
                                addPostLayout!!.visibility = View.VISIBLE
                                statusUpdate!!.isFocusable = true
                                imageUploadBtn2!!.setOnClickListener {
                                    requestMultiplePermissions()
                                    openCropper()
                                }
                                submitStatusButton!!.setOnClickListener { view: View ->
                                    val body = statusUpdate!!.text.toString()
                                    val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
                                    val spinnerText = postTypeSpinner!!.selectedItem.toString()
                                    val form = "clan"
                                    if (statusUpdate!!.text.toString().isNotEmpty() && spinnerText.isNotEmpty()) {
                                        clanLayout!!.visibility = View.GONE
                                        mProgressBar!!.visibility = View.VISIBLE
                                        submitClanPost(body, addedBy!!, tag, spinnerText, form)
                                        hideKeyboardFrom(mContext, view)
                                    } else {
                                        Toast.makeText(mContext, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                        else -> {
                            clanUserRequest(clanID)
                        }
                    }
                    clanCreatedOn!!.text = String.format("Started on: %s", createdOn)
                    eventsCount!!.text = numEvents
                    textViewTvReviews!!.text = numReviews
                    textViewMembersNum!!.text = numMembers
                    if (avgrating != "null") {
                        clanRating!!.rating = avgrating.toFloat()
                    }
                    textViewTvReviews!!.text = numReviews
                    if (games != ",") {
                        val len = games.length
                        val genreString2 = games.substring(1, len - 1).split(",".toRegex()).toTypedArray()
                        for (s in genreString2) {
                            clanProfileTags!!.append(s)
                            clanProfileTags!!.append("\n")
                        }
                    } else {
                        clanProfileTags!!.visibility = View.GONE
                    }
                    if (facebook == "") {
                        imageViewFacebook!!.visibility = View.GONE
                    } else {
                        val ffacebook: String = if (!facebook.contains("https://")) {
                            "https://www.facebook.com/$facebook"
                        } else {
                            "www.facebook.com/$facebook"
                        }
                        imageViewFacebook!!.setOnClickListener {
                            if (mContext is FragmentContainer) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ffacebook)))
                            }
                        }
                    }
                    if (instagram == "") {
                        imageViewInstagram!!.visibility = View.GONE
                    } else {
                        val finstagram: String = if (!instagram.contains("https://")) {
                            "https://www.instagram.com/$instagram"
                        } else {
                            "www.instagram.com/$instagram"
                        }
                        imageViewInstagram!!.setOnClickListener {
                            if (mContext is FragmentContainer) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(finstagram)))
                            }
                        }
                    }
                    if (youtube == "") {
                        imageViewYoutube!!.visibility = View.GONE
                    } else {
                        val fyoutube: String = if (!youtube.contains("https://")) {
                            "https://youtube.com/user/$youtube"
                        } else {
                            "youtube.com/user/$youtube"
                        }
                        imageViewYoutube!!.setOnClickListener {
                            if (mContext is FragmentContainer) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fyoutube)))
                            }
                        }
                    }
                    if (twitter == "") {
                        imageViewTwitter!!.visibility = View.GONE
                    } else {
                        val ftwitter: String = if (!twitter.contains("https://")) {
                            "https://www.twitter.com/$twitter"
                        } else {
                            "www.twitter.com/$twitter"
                        }
                        imageViewTwitter!!.setOnClickListener {
                            if (mContext is FragmentContainer) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(ftwitter)))
                            }
                        }
                    }
                    if (discord == "") {
                        imageViewDiscord!!.visibility = View.GONE
                    } else {
                        val fdiscord: String = if (!discord.contains("https://")) {
                            "https://www.discord.gg/$discord"
                        } else {
                            "www.discord.gg/$discord"
                        }
                        imageViewDiscord!!.setOnClickListener {
                            if (mContext is FragmentContainer) {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fdiscord)))
                            }
                        }
                    }
                    if (website == "") {
                        imageButtonWebsite!!.visibility = View.GONE
                    } else {
                        val fwebsite: String = if (!website.contains("https://")) {
                            "https://$website"
                        } else {
                            website
                        }
                        imageButtonWebsite!!.setOnClickListener {
                            val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                                when (which) {
                                    DialogInterface.BUTTON_POSITIVE -> if (mContext is FragmentContainer) {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fwebsite)))
                                    }
                                    DialogInterface.BUTTON_NEGATIVE -> {
                                    }
                                }
                            }
                            val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                            builder.setMessage("WARNING: You're leaving Sabot Community and opening this clan's website. Malicious intent should be reported.\nYou're being directed to:\n$fwebsite\n\nContinue?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show()
                        }
                    }
                    textViewClanDescription!!.text = description
                    //publicsProfileTags.setText(genre2);
                    textViewClanLow!!.text = String.format("[%s]", tag)
                    textViewClanLow!!.setTextColor(ContextCompat.getColor(mContext!!, R.color.pin))
                    textViewTvClicks!!.text = numSearches
                    textViewClanName!!.text = name
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + insignia)
                            .error(R.mipmap.ic_launcher)
                            .into(imageViewClanPic!!)
                    Glide.with(mContext!!)
                            .load(Constants.BASE_URL + background)
                            .error(R.drawable.profile_default_cover)
                            .into(imageViewClanBackPic!!)
                    clanReviewsContainer!!.setOnClickListener {
                        val ldf = ClanReviewFragment()
                        val args = Bundle()
                        args.putString("ClanId", id)
                        args.putString("Clanname", name)
                        args.putString("Clantag", tag)
                        args.putString("Clan_pic", insignia)
                        args.putString("Clan_members", members)
                        ldf.arguments = args
                        (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    clanRatingContainer!!.setOnClickListener {
                        val ldf = ClanReviewFragment()
                        val args = Bundle()
                        args.putString("ClanId", id)
                        args.putString("Clanname", name)
                        args.putString("Clantag", tag)
                        args.putString("Clan_pic", insignia)
                        args.putString("Clan_members", members)
                        ldf.arguments = args
                        (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                    clanActionBtn!!.setOnClickListener {
                        clanActionBtn!!.visibility = View.GONE
                        followProgressClan!!.visibility = View.VISIBLE
                        requestToJoinClan(clanID, name)
                    }
                    clanActionBtnJoined!!.setOnClickListener {
                        val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                            when (which) {
                                DialogInterface.BUTTON_POSITIVE -> {
                                    clanActionBtnJoined!!.visibility = View.GONE
                                    followProgressClan!!.visibility = View.VISIBLE
                                    leaveClan(clanID)
                                }
                                DialogInterface.BUTTON_NEGATIVE -> {
                                }
                            }
                        }
                        val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                        builder.setMessage("Leave clan?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show()
                    }
                    clanClickMenu!!.setOnClickListener { v: View? ->
                        val popup = PopupMenu(mContext, v)
                        val inflater = popup.menuInflater
                        inflater.inflate(R.menu.clan_top_menu, popup.menu)
                        popup.setOnMenuItemClickListener { item: MenuItem ->
                            if (item.itemId == R.id.menuClanReview) {
                                val ldf = ClanReviewFragment()
                                val args = Bundle()
                                args.putString("ClanId", id)
                                args.putString("Clanname", name)
                                args.putString("Clantag", tag)
                                args.putString("Clan_pic", insignia)
                                args.putString("Clan_members", members)
                                ldf.arguments = args
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                            if (item.itemId == R.id.menuClanReport) {
                                val ldf = ReportFragment()
                                val args = Bundle()
                                args.putString("context", "clan")
                                args.putString("type", "clan")
                                args.putString("id", id)
                                ldf.arguments = args
                                (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                            }
                            true
                        }
                        popup.show()
                    }
                    clanLayout!!.visibility = View.VISIBLE
                    mProgressBar!!.visibility = View.GONE
                    loadClanPosts()
                    //loadClanEvents();
                } else {
                    mProgressBar!!.visibility = View.GONE
                    errorLayout!!.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun submitClanPost(body: String, added_by: String, user_to: String, type: String, form: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, RG, Response.Listener { response: String? ->
            try {
                val jsonObject = JSONObject(response!!)
                if (jsonObject.getString("error") == "false") {
                    if (imageToUpload != null) {
                        val postId = jsonObject.getString("postid")
                        postImageUpload(imageToUpload!!, postId, username)
                    } else {
                        resetFragment()
                    }
                    statusUpdate!!.setText("")
                } else {
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
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
                params["clanid"] = clanID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
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
                        if (jsonObject.getString("error") != "true") {
                            resetFragment()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }) { volleyError: VolleyError -> Log.e("UploadCoverFragment", volleyError.toString()) }
        rQueue = Volley.newRequestQueue(mContext)
        rQueue?.add(jsonObjectRequest)
    }

    private fun loadClanPosts() {
        val stringRequest = StringRequest(Request.Method.GET, "$CLAN_BOTTOM_POSTS?userid=$userID&clanid=$clanID&thisusername=$username", { response: String? ->
            try {
                val clannews = JSONArray(response)
                for (i in 0 until clannews.length()) {
                    val clannewsObject = clannews.getJSONObject(i)
                    val id = clannewsObject.getInt("id")
                    val type = clannewsObject.getString("type")
                    val likes = clannewsObject.getString("likes")
                    val body = clannewsObject.getString("body")
                    val addedBy = clannewsObject.getString("added_by")
                    val userTo = clannewsObject.getString("user_to")
                    val dateAdded = clannewsObject.getString("date_added")
                    val userClosed = clannewsObject.getString("user_closed")
                    val deleted = clannewsObject.getString("deleted")
                    val image = clannewsObject.getString("image")
                    val userId = clannewsObject.getString("user_id")
                    val profilePic = clannewsObject.getString("profile_pic")
                    val verified = clannewsObject.getString("verified")
                    val online = clannewsObject.getString("online")
                    val nickname = clannewsObject.getString("nickname")
                    val username = clannewsObject.getString("username")
                    val commentcount = clannewsObject.getString("commentcount")
                    val likedbyuserYes = clannewsObject.getString("likedbyuseryes")
                    val form = clannewsObject.getString("form")
                    val edited = clannewsObject.getString("edited")
                    val clanpostsRecycler = ProfilenewsRecycler(id, type, likes, body, addedBy, userTo, dateAdded, userClosed, deleted, image, userId, profilePic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited)
                    clanNewsRecyclerList!!.add(clanpostsRecycler)
                }
                clanNewsAdapter = ProfilenewsAdapter(mContext!!, clanNewsRecyclerList)
                clanNewsRecycler!!.adapter = clanNewsAdapter
                if (clannews.length() == 0) {
                    clanNewsSpotlight!!.visibility = View.GONE
                } else {
                    newProgress!!.visibility = View.GONE
                }
                clanNewsMoreBtn!!.setOnClickListener {
                    val ldf = SeeAllFragment()
                    val args = Bundle()
                    args.putString("queryid", clanID)
                    args.putString("method", "clan_posts")
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    /*private void loadClanEvents(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CLAN_BOTTOM_EVENTS+"?userid="+userID+"&username="+username+"&clanid="+clanID, response -> {
            try {
                JSONArray events = new JSONArray(response);
                for(int i = 0; i<events.length(); i++){
                    JSONObject clanEventsObject = events.getJSONObject(i);

                    String id = clanEventsObject.getString("id");
                    String numposts = clanEventsObject.getString("numposts");
                    String subject = clanEventsObject.getString("subject");
                    String date = clanEventsObject.getString("date");
                    String cat = clanEventsObject.getString("cat");
                    String topic_by = clanEventsObject.getString("topic_by");
                    String type = clanEventsObject.getString("type");
                    String user_id = clanEventsObject.getString("user_id");
                    String profile_pic = clanEventsObject.getString("profile_pic");
                    String nickname = clanEventsObject.getString("nickname");
                    String username = clanEventsObject.getString("username");

                    //ClanEvent_Recycler clanEvent_Recycler = new ClanEvent_Recycler(id, numposts, subject, date, cat, topic_by, type, user_id, profile_pic, nickname, username);
                    //clanEventsRecyclerList.add(clanEvent_Recycler);
                }
                //clanEventsAdapter = new EventAdapter(mContext, clanEventsRecyclerList);
                //clanEventsRecycler.setAdapter(clanEventsAdapter);
                clanLayout.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }*/
    private fun clanUserRequest(clan_id: String?) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, GET_REQUESTS, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    if (obj.has("request_sent") && obj.getString("request_sent") == "yes") {
                        clanActionBtnRequested!!.visibility = View.VISIBLE
                        clanActionBtn!!.visibility = View.GONE
                    } else {
                        clanActionBtn!!.visibility = View.VISIBLE
                    }
                    followProgressClan!!.visibility = View.GONE
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
                params["clan_id"] = clan_id!!
                params["thisusername"] = username!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun requestToJoinClan(clan_id: String?, clan_name: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, JOIN_CLAN, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (!obj.getBoolean("error")) {
                    if (obj.getString("result") == "yes") {
                        clanActionBtnRequested!!.visibility = View.VISIBLE
                        followProgressClan!!.visibility = View.GONE
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
                params["clanid"] = clan_id!!
                params["clanname"] = clan_name
                params["thisusername"] = username!!
                params["thisuserid"] = userID!!
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun leaveClan(clan_id: String?) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, LEAVE_CLAN, Response.Listener { response: String? ->
            try {
                val obj = JSONObject(response!!)
                if (!obj.getBoolean("error")) {
                    if (obj.getString("result") == "yes") {
                        resetFragment()
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
                params["clanid"] = clan_id!!
                params["thisusername"] = username!!
                params["thisuserid"] = userID!!
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
                        imageUploadBtn2!!.setImageBitmap(bitmap1)
                    }else{
                        imageUploadBtn2?.setImageResource(R.drawable.icons8_question_mark_64)
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
                            Toast.makeText(mContext, "No permissions are granted by user!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).withErrorListener { error: DexterError? -> Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show() }
                .onSameThread()
                .check()
    }

    private fun resetFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).commitNowAllowingStateLoss()
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().attach(this).commitAllowingStateLoss()
        } else {
            (mContext as FragmentActivity).supportFragmentManager.beginTransaction().detach(this).attach(this).commit()
        }
        //refresh.setRefreshing(false);
    }

    companion object {
        private const val TAG = "ClanFrag"
        private const val CLAN_TOP = Constants.ROOT_URL + "clan_top.php"
        private const val CLAN_BOTTOM_POSTS = Constants.ROOT_URL + "clan_bottom_posts.php"
        private const val CLAN_BOTTOM_EVENTS = Constants.ROOT_URL + "clan_bottom_events.php"
        private const val JOIN_CLAN = Constants.ROOT_URL + "clan_join.php"
        private const val GET_REQUESTS = Constants.ROOT_URL + "get_clan_requests.php"
        private const val LEAVE_CLAN = Constants.ROOT_URL + "leave_clan.php"
        private const val RG = Constants.ROOT_URL + "new_post.php"
        private const val UPLOAD_IMAGE_URL = Constants.ROOT_URL + "new_post_image_upload.php"
        fun hideKeyboardFrom(context: Context?, view: View) {
            val imm = context!!.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}