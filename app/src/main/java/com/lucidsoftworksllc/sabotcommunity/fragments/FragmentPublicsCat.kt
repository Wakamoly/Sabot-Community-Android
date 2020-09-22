package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsTopicAdapter
import com.lucidsoftworksllc.sabotcommunity.models.PublicsTopicRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentPublicsCat : Fragment() {
    private var recyclerPublicsCatBottom: RecyclerView? = null
    private var newsadapter: PublicsTopicAdapter? = null
    private var publicsRecyclerList: MutableList<PublicsTopicRecycler>? = null
    private var textViewTvClicks: TextView? = null
    private var textViewTvReviews: TextView? = null
    private var followersCount: TextView? = null
    private var textViewPublicsLow: TextView? = null
    private var textViewPublicsName: TextView? = null
    private var textViewDescription: TextView? = null
    private var publicsProfileTags: TextView? = null
    private var publicsPostsCount: TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var followProgressCat: ProgressBar? = null
    private var imageButtonPurchase: ImageButton? = null
    private var publicsClickMenu: ImageView? = null
    private var imageViewPublicsPic: ImageView? = null
    private var imageViewPublicsBackPic: ImageView? = null
    private var imageViewFacebook: ImageView? = null
    private var imageViewTwitter: ImageView? = null
    private var imageViewYoutube: ImageView? = null
    private var imageViewInstagram: ImageView? = null
    private var toolbar: Toolbar? = null
    private var mContext: Context? = null
    private var relLayout2: RelativeLayout? = null
    private var gameRating: SimpleRatingBar? = null
    private var publicsRatingContainer: LinearLayout? = null
    private var publicsReviewsContainer: LinearLayout? = null
    private var buttonLayout: LinearLayout? = null
    private var publicsPostsContainer: LinearLayout? = null
    private var gameActionBtn: Button? = null
    private var gameActionBtnFollowed: Button? = null
    private var newPublicsButton: Button? = null
    private var followToPostButton: Button? = null
    private var previousPublics: Button? = null
    private var chatRoomButton: Button? = null
    private var userID: String? = null
    private var username: String? = null
    private var publicsCatID: String? = null
    private val statusUpdate: EditText? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val publicsCatRootView = inflater.inflate(R.layout.fragment_publics_cat, container, false)
        publicsReviewsContainer = publicsCatRootView.findViewById(R.id.publicsReviewsContainer)
        followProgressCat = publicsCatRootView.findViewById(R.id.followProgressCat)
        followersCount = publicsCatRootView.findViewById(R.id.followersCount)
        mProgressBar = publicsCatRootView.findViewById(R.id.publicsCatProgressBar)
        textViewPublicsName = publicsCatRootView.findViewById(R.id.textViewPublicsName)
        textViewPublicsLow = publicsCatRootView.findViewById(R.id.textViewPublicsLow)
        imageViewFacebook = publicsCatRootView.findViewById(R.id.profileFacebookPage)
        imageViewTwitter = publicsCatRootView.findViewById(R.id.profileTwitterPage)
        imageViewYoutube = publicsCatRootView.findViewById(R.id.profileYoutubePage)
        imageViewInstagram = publicsCatRootView.findViewById(R.id.profileInstagramPage)
        imageButtonPurchase = publicsCatRootView.findViewById(R.id.profileBuyBtn)
        textViewTvClicks = publicsCatRootView.findViewById(R.id.tvClicks)
        textViewTvReviews = publicsCatRootView.findViewById(R.id.tvReviews)
        textViewDescription = publicsCatRootView.findViewById(R.id.textViewPublicsDescription)
        publicsProfileTags = publicsCatRootView.findViewById(R.id.publicsProfileDetails)
        imageViewPublicsPic = publicsCatRootView.findViewById(R.id.imageViewPublicsPic)
        imageViewPublicsBackPic = publicsCatRootView.findViewById(R.id.imageViewPublicsBackPic)
        publicsClickMenu = publicsCatRootView.findViewById(R.id.publicsClickMenu)
        toolbar = publicsCatRootView.findViewById(R.id.publicsToolBar)
        relLayout2 = publicsCatRootView.findViewById(R.id.publicsLayout)
        gameRating = publicsCatRootView.findViewById(R.id.ratingBar)
        publicsRatingContainer = publicsCatRootView.findViewById(R.id.publicsRatingContainer)
        gameActionBtn = publicsCatRootView.findViewById(R.id.profileActionBtn)
        gameActionBtnFollowed = publicsCatRootView.findViewById(R.id.profileActionBtnFollowed)
        newPublicsButton = publicsCatRootView.findViewById(R.id.newPublicsButton)
        publicsPostsCount = publicsCatRootView.findViewById(R.id.publicsPostsCount)
        followToPostButton = publicsCatRootView.findViewById(R.id.followToPostButton)
        buttonLayout = publicsCatRootView.findViewById(R.id.buttonLayout)
        previousPublics = publicsCatRootView.findViewById(R.id.previousPublics)
        chatRoomButton = publicsCatRootView.findViewById(R.id.chatRoomButton)
        publicsPostsContainer = publicsCatRootView.findViewById(R.id.publicsPostsContainer)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        publicsRecyclerList = ArrayList()
        publicsCatID = requireArguments().getString("PublicsId")
        recyclerPublicsCatBottom = publicsCatRootView.findViewById(R.id.recyclerPublicsTopics)
        recyclerPublicsCatBottom?.setHasFixedSize(true)
        recyclerPublicsCatBottom?.layoutManager = LinearLayoutManager(mContext)
        loadPublicsCatTop()
        textViewPublicsLow?.requestFocus()
        return publicsCatRootView
    }

    private fun loadPublicsCatTop() {
        val stringRequest = StringRequest(Request.Method.GET, "$Publics_Cat_URL?userid=$userID&username=$username&publicsid=$publicsCatID", { response: String? ->
            try {
                val profiletop = JSONArray(response)
                val profiletopObject = profiletop.getJSONObject(0)
                val id = profiletopObject.getString("id")
                val name = profiletopObject.getString("name")
                val genre = profiletopObject.getString("genre")
                val image = profiletopObject.getString("image")
                val backImage = profiletopObject.getString("back_image")
                val catTag = profiletopObject.getString("cat_tag")
                val catDescription = profiletopObject.getString("cat_description")
                val searchHits = profiletopObject.getString("search_hits")
                val ratings = profiletopObject.getString("ratings")
                val facebook = profiletopObject.getString("facebook")
                val twitter = profiletopObject.getString("twitter")
                val youtube = profiletopObject.getString("youtube")
                val instagram = profiletopObject.getString("instagram")
                val purchase = profiletopObject.getString("purchase")
                val steampurchase = profiletopObject.getString("steampurchase")
                val followers = profiletopObject.getString("followers")
                //String count = profiletopObject.getString("count");
                val average = profiletopObject.getString("average")
                val publicsposts = profiletopObject.getString("publicsposts")
                val followed = profiletopObject.getString("followed")
                publicsPostsCount!!.text = publicsposts
                followersCount!!.text = followers
                textViewTvReviews!!.text = ratings
                gameRating!!.rating = average.toFloat()
                imageViewPublicsPic!!.setOnClickListener {
                    val asf: Fragment = PhotoViewFragment()
                    val args = Bundle()
                    args.putString("image", image)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                imageViewPublicsBackPic!!.setOnClickListener {
                    val asf: Fragment = PhotoViewFragment()
                    val args = Bundle()
                    args.putString("image", backImage)
                    asf.arguments = args
                    val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                    fragmentTransaction.replace(R.id.fragment_container, asf)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
                if (followed == "yes") {
                    gameActionBtnFollowed!!.visibility = View.VISIBLE
                    buttonLayout!!.visibility = View.VISIBLE
                    loadPublicsCatBottom()
                } else {
                    gameActionBtn!!.visibility = View.VISIBLE
                    followToPostButton!!.visibility = View.VISIBLE
                }
                val len = genre.length
                val genreString2 = genre.substring(1, len - 1).split(",".toRegex()).toTypedArray()
                for (s in genreString2) {
                    publicsProfileTags!!.append(s)
                    publicsProfileTags!!.append("\n")
                }
                if (facebook == "") {
                    imageViewFacebook!!.visibility = View.GONE
                } else {
                    imageViewFacebook!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(facebook)))
                        }
                    }
                }
                if (instagram == "") {
                    imageViewInstagram!!.visibility = View.GONE
                } else {
                    imageViewInstagram!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(instagram)))
                        }
                    }
                }
                if (youtube == "") {
                    imageViewYoutube!!.visibility = View.GONE
                } else {
                    imageViewYoutube!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(youtube)))
                        }
                    }
                }
                if (twitter == "") {
                    imageViewTwitter!!.visibility = View.GONE
                } else {
                    imageViewTwitter!!.setOnClickListener {
                        if (mContext is FragmentContainer) {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(twitter)))
                        }
                    }
                }
                if (purchase == "" && steampurchase == "") {
                    imageButtonPurchase!!.visibility = View.GONE
                } else {
                    when {
                        purchase == "" -> {
                            imageButtonPurchase!!.setOnClickListener {
                                if (mContext is FragmentContainer) {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(steampurchase)))
                                }
                            }
                        }
                        steampurchase == "" -> {
                            imageButtonPurchase!!.setOnClickListener {
                                if (mContext is FragmentContainer) {
                                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(purchase)))
                                }
                            }
                        }
                        else -> {
                            imageButtonPurchase!!.setOnClickListener {
                                if (mContext is FragmentContainer) {
                                    val dialogClickListener = DialogInterface.OnClickListener { _: DialogInterface?, which: Int ->
                                        when (which) {
                                            DialogInterface.BUTTON_POSITIVE -> if (mContext is FragmentContainer) {
                                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(steampurchase)))
                                            }
                                            DialogInterface.BUTTON_NEUTRAL -> {
                                            }
                                            DialogInterface.BUTTON_NEGATIVE -> if (mContext is FragmentContainer) {
                                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(purchase)))
                                            }
                                        }
                                    }
                                    val builder = AlertDialog.Builder(mContext, R.style.AlertDialogStyle)
                                    builder.setMessage(R.string.purchasealterdialog).setPositiveButton(R.string.steam, dialogClickListener)
                                            .setNegativeButton(R.string.other2, dialogClickListener).setNeutralButton(R.string.back, dialogClickListener).show()
                                }
                            }
                        }
                    }
                }
                textViewDescription!!.text = catDescription
                //                  publicsProfileTags.setText(genre2);
                textViewPublicsLow!!.text = String.format("@%s", catTag)
                textViewTvClicks!!.text = searchHits
                textViewPublicsName!!.text = name
                Glide.with(mContext!!)
                        .load(Constants.BASE_URL + image)
                        .error(R.mipmap.ic_launcher)
                        .into(imageViewPublicsPic!!)
                Glide.with(mContext!!)
                        .load(Constants.BASE_URL + backImage)
                        .error(R.drawable.profile_default_cover)
                        .into(imageViewPublicsBackPic!!)
                publicsPostsContainer!!.setOnClickListener {
                    val ldf = PublicsPrevious()
                    val args = Bundle()
                    args.putString("GameId", id)
                    args.putString("gamename", name)
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                chatRoomButton!!.setOnClickListener {
                    val ldf = PublicsChatRoom()
                    val args = Bundle()
                    args.putString("game_pic", image)
                    args.putString("GameId", id)
                    args.putString("gamename", name)
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                previousPublics!!.setOnClickListener {
                    val ldf = PublicsPrevious()
                    val args = Bundle()
                    args.putString("GameId", id)
                    args.putString("gamename", name)
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                publicsReviewsContainer!!.setOnClickListener {
                    val ldf = GameReviewFragment()
                    val args = Bundle()
                    args.putString("GameId", id)
                    args.putString("gamename", name)
                    args.putString("gametag", catTag)
                    args.putString("game_pic", image)
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                publicsRatingContainer!!.setOnClickListener {
                    val ldf = GameReviewFragment()
                    val args = Bundle()
                    args.putString("GameId", id)
                    args.putString("gamename", name)
                    args.putString("gametag", catTag)
                    args.putString("game_pic", image)
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                followToPostButton!!.setOnClickListener {
                    gameActionBtn!!.visibility = View.GONE
                    followProgressCat!!.visibility = View.VISIBLE
                    val stringRequest1: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener {
                        //TODO: get verified response
                        gameActionBtnFollowed!!.visibility = View.VISIBLE
                        followProgressCat!!.visibility = View.GONE
                        buttonLayout!!.visibility = View.VISIBLE
                        recyclerPublicsCatBottom!!.visibility = View.VISIBLE
                        followToPostButton!!.visibility = View.VISIBLE
                        loadPublicsCatBottom()
                        followToPostButton!!.visibility = View.GONE
                    }, Response.ErrorListener { Toast.makeText(mContext, "Could not follow, please try again later...", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["game_id"] = id
                            params["game_name"] = name
                            params["method"] = "follow"
                            params["user_id"] = userID
                            params["username"] = username
                            return params
                        }
                    }
                    (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest1)
                }


                gameActionBtn?.setOnClickListener {
                    gameActionBtn?.isEnabled = false
                    gameActionBtn?.visibility = View.GONE
                    val buttonAppear: Animation = AnimationUtils.loadAnimation(mContext, R.anim.expand_in)
                    gameActionBtnFollowed?.visibility = View.VISIBLE
                    gameActionBtnFollowed?.startAnimation(buttonAppear)
                    Handler().postDelayed({ gameActionBtnFollowed?.isEnabled = true }, 3500)

                    val userID = SharedPrefManager.getInstance(mContext!!)!!.userID
                    val username = SharedPrefManager.getInstance(mContext!!)!!.username
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener { response: String? ->
                        try {
                            val jsonObject = JSONObject(response!!)
                            if (jsonObject.getString("error") != "false") {
                                gameActionBtn?.isEnabled = true
                                gameActionBtn?.visibility = View.VISIBLE
                                gameActionBtnFollowed?.visibility = View.GONE
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { Toast.makeText(mContext, "Could not follow, please try again later...", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["game_id"] = id
                            params["game_name"] = name
                            params["method"] = "follow"
                            params["user_id"] = userID
                            params["username"] = username
                            return params
                        }
                    }
                    (mContext as FragmentContainer).addToRequestQueue(stringRequest)
                }


                gameActionBtnFollowed?.setOnClickListener {
                    LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                            .setTopColorRes(R.color.green)
                            .setButtonsColorRes(R.color.white)
                            .setIcon(R.drawable.ic_check)
                            .setTitle(R.string.game_unfollow)
                            .setMessage(mContext?.resources?.getString(R.string.unfollow) + " " + name + "?")
                            .setPositiveButton(android.R.string.ok) {
                                gameActionBtnFollowed?.isEnabled = false
                                gameActionBtnFollowed?.visibility = View.GONE
                                val buttonAppear: Animation = AnimationUtils.loadAnimation(mContext, R.anim.expand_in)
                                gameActionBtn?.visibility = View.VISIBLE
                                gameActionBtn?.startAnimation(buttonAppear)
                                Handler().postDelayed({ gameActionBtn?.isEnabled = true }, 3500)

                                val userID = SharedPrefManager.getInstance(mContext!!)!!.userID
                                val username = SharedPrefManager.getInstance(mContext!!)!!.username
                                val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener { response: String? ->
                                    try {
                                        val jsonObject = JSONObject(response!!)
                                        if (jsonObject.getString("error") != "false") {
                                            gameActionBtnFollowed?.isEnabled = true
                                            gameActionBtnFollowed?.visibility = View.VISIBLE
                                            gameActionBtn?.visibility = View.GONE
                                        }
                                    } catch (e: JSONException) {
                                        e.printStackTrace()
                                    }
                                }, Response.ErrorListener { Toast.makeText(mContext, "Could not unfollow, please try again later...", Toast.LENGTH_LONG).show() }) {
                                    override fun getParams(): MutableMap<String, String?> {
                                        val params: MutableMap<String, String?> = HashMap()
                                        params["game_id"] = id
                                        params["game_name"] = name
                                        params["method"] = "unfollow"
                                        params["user_id"] = userID
                                        params["username"] = username
                                        return params
                                    }
                                }
                                (mContext as FragmentContainer).addToRequestQueue(stringRequest)
                            }
                            .setNegativeButton(android.R.string.no){}
                            .show()
                }


                publicsClickMenu!!.setOnClickListener { view: View? ->
                    val popup = PopupMenu(mContext, view)
                    val inflater = popup.menuInflater
                    inflater.inflate(R.menu.publics_cat_top_menu, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem ->
                        if (item.itemId == R.id.menuCatReport) {
                            val ldf = ReportFragment()
                            val args = Bundle()
                            args.putString("context", "game")
                            args.putString("type", "publics_cat")
                            args.putString("id", id)
                            ldf.arguments = args
                            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        if (item.itemId == R.id.menuCatReview) {
                            val ldf = GameReviewFragment()
                            val args = Bundle()
                            args.putString("GameId", id)
                            args.putString("gamename", name)
                            args.putString("gametag", catTag)
                            args.putString("game_pic", image)
                            ldf.arguments = args
                            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        if (item.itemId == R.id.menuCatNewPublicsTopic) {
                            val ldf = NewPublicsTopic()
                            val args = Bundle()
                            args.putString("gameid", id)
                            args.putString("gamename", name)
                            args.putString("gameimage", image)
                            ldf.arguments = args
                            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        true
                    }
                    popup.show()
                }
                newPublicsButton!!.setOnClickListener {
                    val ldf = NewPublicsTopic()
                    val args = Bundle()
                    args.putString("gameid", id)
                    args.putString("gamename", name)
                    args.putString("gameimage", image)
                    ldf.arguments = args
                    (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
                relLayout2!!.visibility = View.VISIBLE
                mProgressBar!!.visibility = View.GONE
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadPublicsCatBottom() {
        val stringRequest = StringRequest(Request.Method.GET, "$Publics_Cat_Bottom_URL?userid=$userID&publicsid=$publicsCatID", { response: String? ->
            try {
                val profilenews = JSONArray(response)
                for (i in 0 until profilenews.length()) {
                    val profilenewsObject = profilenews.getJSONObject(i)
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
                    publicsRecyclerList!!.add(publicsTopicResult)
                }
                newsadapter = PublicsTopicAdapter(mContext!!, publicsRecyclerList!!)
                recyclerPublicsCatBottom!!.adapter = newsadapter
                recyclerPublicsCatBottom!!.isNestedScrollingEnabled = false
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val TAG = "PublicsCatFragment"
        private const val Publics_Cat_URL = Constants.ROOT_URL + "publics_cat.php"
        private const val Publics_Cat_Bottom_URL = Constants.ROOT_URL + "publics_cat_bottom.php"
        private const val FOLLOW_GAME_URL = Constants.ROOT_URL + "publicscat_follow_api.php"
    }
}