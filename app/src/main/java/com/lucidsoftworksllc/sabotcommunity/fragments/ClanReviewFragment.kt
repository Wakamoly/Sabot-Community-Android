package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.GameReviewAdapter
import com.lucidsoftworksllc.sabotcommunity.models.GameReviewRecycler
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class ClanReviewFragment : Fragment() {
    private var tvratingsandreviews: TextView? = null
    private var tvInteger: TextView? = null
    private var gameReviewed: TextView? = null
    private var gameTagReviewed: TextView? = null
    private var numReviews: TextView? = null
    private var tvTotalReviews: TextView? = null
    private var noReviews: TextView? = null
    private var gameImageReviewed: ImageView? = null
    private var mContext: Context? = null
    private var userID: String? = null
    private var username: String? = null
    private var playerReviewCenter: RelativeLayout? = null
    private var playerReviewRecyclerLayout: RelativeLayout? = null
    private var reviews5: ProgressBar? = null
    private var reviews4: ProgressBar? = null
    private var reviews3: ProgressBar? = null
    private var reviews2: ProgressBar? = null
    private var reviews1: ProgressBar? = null
    private var playerReviewProgress: ProgressBar? = null
    private var reviewStarRating: SimpleRatingBar? = null
    private var gamereviewsView: RecyclerView? = null
    private var gamereviewRecyclerList: MutableList<GameReviewRecycler>? = null
    private var reviewButton: Button? = null
    private var joinToReview: Button? = null
    private var reviewAdapter: GameReviewAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val clanReviewRootView = inflater.inflate(R.layout.fragment_clan_review, null)
        tvratingsandreviews = clanReviewRootView.findViewById(R.id.tvratingsandreviews)
        gameReviewed = clanReviewRootView.findViewById(R.id.nameReviewed)
        gameTagReviewed = clanReviewRootView.findViewById(R.id.tagReviewed)
        gameImageReviewed = clanReviewRootView.findViewById(R.id.imageReviewed)
        playerReviewProgress = clanReviewRootView.findViewById(R.id.review_progress)
        playerReviewRecyclerLayout = clanReviewRootView.findViewById(R.id.review_recycler_layout)
        playerReviewCenter = clanReviewRootView.findViewById(R.id.review_center)
        tvTotalReviews = clanReviewRootView.findViewById(R.id.tvTotalReviews)
        numReviews = clanReviewRootView.findViewById(R.id.numReviews)
        reviewButton = clanReviewRootView.findViewById(R.id.reviewButton)
        joinToReview = clanReviewRootView.findViewById(R.id.joinToReview)
        tvInteger = clanReviewRootView.findViewById(R.id.tvInteger)
        reviews5 = clanReviewRootView.findViewById(R.id.reviews5)
        reviews4 = clanReviewRootView.findViewById(R.id.reviews4)
        reviews3 = clanReviewRootView.findViewById(R.id.reviews3)
        reviews2 = clanReviewRootView.findViewById(R.id.reviews2)
        reviews1 = clanReviewRootView.findViewById(R.id.reviews1)
        reviewStarRating = clanReviewRootView.findViewById(R.id.reviewStarRating)
        noReviews = clanReviewRootView.findViewById(R.id.noReviews)
        mContext = activity
        gamereviewRecyclerList = ArrayList()
        gamereviewsView = clanReviewRootView.findViewById(R.id.recyclerReviews)
        gamereviewsView?.setHasFixedSize(true)
        gamereviewsView?.layoutManager = LinearLayoutManager(mContext)
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        loadReviewsTop()
        loadReviews()
        tvratingsandreviews?.requestFocus()
        return clanReviewRootView
    }

    private fun loadReviewsTop() {
        val clanID = requireArguments().getString("ClanId")
        val clanName = requireArguments().getString("Clanname")
        val clanTag = requireArguments().getString("Clantag")
        val clanPic = requireArguments().getString("Clan_pic")
        val clanMembers = requireArguments().getString("Clan_members")
        reviewButton!!.setOnClickListener {
            val ldf = ClanRatingFragment()
            val args = Bundle()
            args.putString("ClanId", clanID)
            args.putString("Clanname", clanName)
            args.putString("Clantag", clanTag)
            args.putString("Clan_pic", clanPic)
            ldf.arguments = args
            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
        gameTagReviewed!!.text = String.format("[%s]", clanTag)
        gameReviewed!!.text = clanName
        Glide.with(mContext!!)
                .load(Constants.BASE_URL + clanPic)
                .error(R.mipmap.ic_launcher)
                .into(gameImageReviewed!!)
        val stringRequest = StringRequest(Request.Method.GET, "$Clan_Reviewed_TOP_URL?userid=$userID&username=$username&clanid=$clanID", { response: String? ->
            try {
                val profiletop = JSONArray(response)
                val profiletopObject = profiletop.getJSONObject(0)
                val count = profiletopObject.getInt("count")
                val average = profiletopObject.getString("average")
                val fivestarratings = profiletopObject.getInt("fivestarratings")
                val fourstarratings = profiletopObject.getInt("fourstarratings")
                val threestarratings = profiletopObject.getInt("threestarratings")
                val twostarratings = profiletopObject.getInt("twostarratings")
                val onestarratings = profiletopObject.getInt("onestarratings")
                reviews5!!.max = count
                reviews5!!.progress = fivestarratings
                reviews4!!.max = count
                reviews4!!.progress = fourstarratings
                reviews3!!.max = count
                reviews3!!.progress = threestarratings
                reviews2!!.max = count
                reviews2!!.progress = twostarratings
                reviews1!!.max = count
                reviews1!!.progress = onestarratings
                reviewStarRating!!.rating = average.toFloat()
                tvInteger!!.text = average
                numReviews!!.text = count.toString()
                if (count == 1) {
                    tvTotalReviews!!.setText(R.string.review_text)
                }
                playerReviewProgress!!.visibility = View.GONE
                playerReviewCenter!!.visibility = View.VISIBLE
                playerReviewRecyclerLayout!!.visibility = View.VISIBLE
                val array: Array<String?> = clanMembers?.split(",".toRegex())?.toTypedArray()
                        ?: arrayOfNulls(0)
                if (listOf(*array).contains(username)) {
                    reviewButton!!.visibility = View.VISIBLE
                } else {
                    joinToReview!!.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(activity, "Network Error", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadReviews() {
        val id = requireArguments().getString("ClanId")
        val stringRequest = StringRequest(Request.Method.GET, "$Clan_Reviewed_URL?userid=$userID&clanid=$id&username=$username", { response: String? ->
            try {
                val profilenews = JSONArray(response)
                for (i in 0 until profilenews.length()) {
                    val profilenewsObject = profilenews.getJSONObject(i)
                    val ratingnumber = profilenewsObject.getString("ratingnumber")
                    val title = profilenewsObject.getString("title")
                    val comments = profilenewsObject.getString("comments")
                    val reply = profilenewsObject.getString("reply")
                    val time = profilenewsObject.getString("time")
                    val profilePic = profilenewsObject.getString("profile_pic")
                    val nickname = profilenewsObject.getString("nickname")
                    val userid = profilenewsObject.getString("userid")
                    val gamenewsResult = GameReviewRecycler(ratingnumber, title, comments, reply, time, profilePic, nickname, userid)
                    gamereviewRecyclerList!!.add(gamenewsResult)
                }
                if (profilenews.length() == 0) {
                    noReviews!!.visibility = View.VISIBLE
                } else {
                    reviewAdapter = GameReviewAdapter(mContext, gamereviewRecyclerList)
                    gamereviewsView!!.adapter = reviewAdapter
                    ViewCompat.setNestedScrollingEnabled(gamereviewsView!!, false)
                    gameReviewed!!.isFocusable = true
                    gameReviewed!!.isFocusableInTouchMode = true
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network Error", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val Clan_Reviewed_TOP_URL = Constants.ROOT_URL + "clanreviewsTopGet_api.php"
        private const val Clan_Reviewed_URL = Constants.ROOT_URL + "clanreviewsGet_api.php"
    }
}