package com.lucidsoftworksllc.sabotcommunity

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
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class PlayerReviewFragment : Fragment() {
    private var tvratingsandreviews: TextView? = null
    private var tvInteger: TextView? = null
    private var usernameReviewed: TextView? = null
    private var nicknameReviewed: TextView? = null
    private var numReviews: TextView? = null
    private var tvTotalReviews: TextView? = null
    private var noReviews: TextView? = null
    private var profileOnlineIcon: ImageView? = null
    private var verifiedIcon: CircleImageView? = null
    private var profileImageReviewed: CircleImageView? = null
    private var mContext: Context? = null
    private var userID: String? = null
    private var userProfileID: String? = null
    private var username: String? = null
    private var nickname: String? = null
    private var verified: String? = null
    private var profilePic: String? = null
    private var allFriendArray: String? = null
    private var lastOnline: String? = null
    private var playerReviewCenter: RelativeLayout? = null
    private var playerReviewRecyclerLayout: RelativeLayout? = null
    private var reviews5: ProgressBar? = null
    private var reviews4: ProgressBar? = null
    private var reviews3: ProgressBar? = null
    private var reviews2: ProgressBar? = null
    private var reviews1: ProgressBar? = null
    private var playerReviewProgress: ProgressBar? = null
    private var reviewStarRating: SimpleRatingBar? = null
    private var playerreviewsView: RecyclerView? = null
    private var playerreviewRecyclerList: MutableList<PlayerReviewRecycler>? = null
    private var reviewButton: Button? = null
    private var connectToReview: Button? = null
    private var reviewAdapter: PlayerReviewAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val playerReviewRootView = inflater.inflate(R.layout.fragment_player_review, null)
        connectToReview = playerReviewRootView.findViewById(R.id.connectToReview)
        noReviews = playerReviewRootView.findViewById(R.id.noReviews)
        tvratingsandreviews = playerReviewRootView.findViewById(R.id.tvratingsandreviews)
        profileOnlineIcon = playerReviewRootView.findViewById(R.id.profileOnlineIcon)
        verifiedIcon = playerReviewRootView.findViewById(R.id.verifiedIcon)
        usernameReviewed = playerReviewRootView.findViewById(R.id.usernameReviewed)
        nicknameReviewed = playerReviewRootView.findViewById(R.id.nicknameReviewed)
        profileImageReviewed = playerReviewRootView.findViewById(R.id.profileImageReviewed)
        playerReviewProgress = playerReviewRootView.findViewById(R.id.player_review_progress)
        playerReviewRecyclerLayout = playerReviewRootView.findViewById(R.id.player_review_recycler_layout)
        playerReviewCenter = playerReviewRootView.findViewById(R.id.player_review_center)
        tvTotalReviews = playerReviewRootView.findViewById(R.id.tvTotalReviews)
        numReviews = playerReviewRootView.findViewById(R.id.numReviews)
        reviewButton = playerReviewRootView.findViewById(R.id.reviewButton)
        tvInteger = playerReviewRootView.findViewById(R.id.tvInteger)
        reviews5 = playerReviewRootView.findViewById(R.id.reviews5)
        reviews4 = playerReviewRootView.findViewById(R.id.reviews4)
        reviews3 = playerReviewRootView.findViewById(R.id.reviews3)
        reviews2 = playerReviewRootView.findViewById(R.id.reviews2)
        reviews1 = playerReviewRootView.findViewById(R.id.reviews1)
        reviewStarRating = playerReviewRootView.findViewById(R.id.reviewStarRating)
        playerreviewRecyclerList = ArrayList()
        username = requireArguments().getString("username")
        nickname = requireArguments().getString("nickname")
        verified = requireArguments().getString("verified")
        profilePic = requireArguments().getString("profile_pic")
        allFriendArray = requireArguments().getString("all_friend_array")
        lastOnline = requireArguments().getString("last_online")
        playerreviewsView = playerReviewRootView.findViewById(R.id.recyclerPlayerReviews)
        mContext = activity
        playerreviewsView?.setHasFixedSize(true)
        playerreviewsView?.layoutManager = LinearLayoutManager(mContext)
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        loadReviewsTop()
        loadReviews()
        tvratingsandreviews?.requestFocus()
        return playerReviewRootView
    }

    private fun loadReviewsTop() {
        userProfileID = if (arguments != null) {
            requireArguments().getString("UserId")
        } else {
            userID
        }
        if (lastOnline == "yes") {
            profileOnlineIcon!!.visibility = View.VISIBLE
        }
        if (verified == "yes") {
            verifiedIcon!!.visibility = View.VISIBLE
        }
        reviewButton!!.setOnClickListener {
            val ldf = PlayerRatingFragment()
            val args = Bundle()
            args.putString("UserId", userProfileID)
            args.putString("username", username)
            args.putString("nickname", nickname)
            args.putString("verified", verified)
            args.putString("profile_pic", profilePic)
            args.putString("last_online", lastOnline)
            ldf.arguments = args
            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
        usernameReviewed!!.text = String.format("@%s", username)
        nicknameReviewed!!.text = nickname
        Glide.with(mContext!!)
                .load(Constants.BASE_URL + profilePic)
                .error(R.mipmap.ic_launcher)
                .into(profileImageReviewed!!)
        val stringRequest = StringRequest(Request.Method.GET, "$Player_Reviewed_TOP_URL?userid=$userID&userQuery=$username", { response: String? ->
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
                    tvTotalReviews!!.text = getString(R.string.review_text)
                }
                playerReviewProgress!!.visibility = View.GONE
                playerReviewCenter!!.visibility = View.VISIBLE
                playerReviewRecyclerLayout!!.visibility = View.VISIBLE
                val array = allFriendArray!!.split(",".toRegex()).toTypedArray()
                if (listOf(*array).contains(SharedPrefManager.getInstance(mContext!!)!!.username)) {
                    reviewButton!!.visibility = View.VISIBLE
                } else if (userProfileID != userID) {
                    connectToReview!!.visibility = View.VISIBLE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadReviews() {
        val stringRequest = StringRequest(Request.Method.GET, "$Player_Reviewed_URL?userid=$userID&userQuery=$username", { response: String? ->
            try {
                val profilenews = JSONArray(response)
                for (i in 0 until profilenews.length()) {
                    val profilenewsObject = profilenews.getJSONObject(i)
                    val ratingnumber = profilenewsObject.getString("ratingnumber")
                    val title = profilenewsObject.getString("title")
                    val comments = profilenewsObject.getString("comments")
                    val reply = profilenewsObject.getString("reply")
                    val time = profilenewsObject.getString("time")
                    val profilePic1 = profilenewsObject.getString("profile_pic")
                    val nickname = profilenewsObject.getString("nickname")
                    val userId = profilenewsObject.getString("userid")
                    val profilenewsResult = PlayerReviewRecycler(ratingnumber, title, comments, reply, time, profilePic1, nickname, userId)
                    playerreviewRecyclerList!!.add(profilenewsResult)
                }
                if (profilenews.length() == 0) {
                    noReviews!!.visibility = View.VISIBLE
                }
                reviewAdapter = PlayerReviewAdapter(mContext!!, playerreviewRecyclerList!!)
                playerreviewsView!!.adapter = reviewAdapter
                ViewCompat.setNestedScrollingEnabled(playerreviewsView!!, false)
                nicknameReviewed!!.isFocusable = true
                nicknameReviewed!!.isFocusableInTouchMode = true
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }) { Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show() }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    companion object {
        private const val Player_Reviewed_TOP_URL = Constants.ROOT_URL + "playerreviewsTopGet_api.php"
        private const val Player_Reviewed_URL = Constants.ROOT_URL + "playerreviewsGet_api.php"
    }
}