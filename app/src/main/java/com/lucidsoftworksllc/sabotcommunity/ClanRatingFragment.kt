package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class ClanRatingFragment : Fragment() {
    var mRatingBar: SimpleRatingBar? = null
    var mRatingScale: TextView? = null
    var nicknameReview: TextView? = null
    var usernameReview: TextView? = null
    var mFeedback: EditText? = null
    var mTitle: EditText? = null
    var mSendFeedback: Button? = null
    var newReviewProgressBar: LinearLayout? = null
    var newReviewDetails: LinearLayout? = null
    var profileOnlineIcon: ImageView? = null
    private var playerRatingProfilePhoto: ImageView? = null
    var verifiedIcon: CircleImageView? = null
    var mContext: Context? = null
    var userID: String? = null
    var nickname: String? = null
    var username: String? = null
    var verified: String? = null
    var profile_pic: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val playerRatingRootView = inflater.inflate(R.layout.fragment_gamerating, container, false)
        mRatingBar = playerRatingRootView.findViewById(R.id.newReviewStarRating)
        profileOnlineIcon = playerRatingRootView.findViewById(R.id.profileOnlineIcon)
        verifiedIcon = playerRatingRootView.findViewById(R.id.verifiedIcon)
        playerRatingProfilePhoto = playerRatingRootView.findViewById(R.id.player_rating_profile_photo)
        newReviewDetails = playerRatingRootView.findViewById(R.id.newReviewDetails)
        nicknameReview = playerRatingRootView.findViewById(R.id.nicknameReview)
        usernameReview = playerRatingRootView.findViewById(R.id.usernameReview)
        mRatingScale = playerRatingRootView.findViewById<View>(R.id.tvRatingScale) as TextView
        mFeedback = playerRatingRootView.findViewById<View>(R.id.etFeedback) as EditText
        mSendFeedback = playerRatingRootView.findViewById<View>(R.id.btnSubmit) as Button
        mTitle = playerRatingRootView.findViewById(R.id.mTitle)
        newReviewProgressBar = playerRatingRootView.findViewById(R.id.newReviewProgressBar)
        mContext = activity
        loadNewReview()
        newReviewProgressBar?.visibility = View.GONE
        return playerRatingRootView
    }

    private fun submitReview(body: String, added_by: String, clan_id: String?, rating: String, title: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, New_Review_URL, Response.Listener {
            newReviewProgressBar!!.visibility = View.GONE
            Toast.makeText(mContext, "Review Posted!", Toast.LENGTH_LONG).show()
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }, Response.ErrorListener {
            newReviewProgressBar!!.visibility = View.GONE
            newReviewDetails!!.visibility = View.VISIBLE
            Toast.makeText(mContext, "Error on Response, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["added_by"] = added_by
                params["clan_id"] = clan_id!!
                params["rating"] = rating
                params["title"] = title
                return params
            }
        }
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    private fun loadNewReview() {
        val clanId = requireArguments().getString("ClanId")
        val clanname = requireArguments().getString("Clanname")
        val clantag = requireArguments().getString("Clantag")
        val clanPic = requireArguments().getString("Clan_pic")
        mRatingBar!!.setOnRatingBarChangeListener { simpleRatingBar: SimpleRatingBar, rating: Float, fromUser: Boolean ->
            mRatingScale!!.text = rating.toString()
            when (simpleRatingBar.rating.toInt()) {
                1 -> mRatingScale!!.text = getString(R.string.bad)
                2 -> mRatingScale!!.text = getString(R.string.cusi)
                3 -> mRatingScale!!.text = getString(R.string.average)
                4 -> mRatingScale!!.text = getString(R.string.good)
                5 -> mRatingScale!!.text = getString(R.string.great)
                else -> mRatingScale!!.setText(R.string.choose_a_rating_text)
            }
        }
        mSendFeedback!!.setOnClickListener {
            if (mFeedback!!.text.toString().isNotEmpty() && mTitle!!.text.toString().isNotEmpty() && mRatingBar!!.rating.toString() != "0.0") {
                LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.green)
                        .setIcon(R.drawable.ic_error)
                        .setTitle("New Review")
                        .setMessage("""
    WARNING: You risk immediate account termination if your submitted review goes against our code of conduct.
    Examples of inappropriate content include (but are not limited to):

    •Content that could be considered violent or threatening.
    •References to illegal use of alcohol, illegal drugs/illicit substances.
    •Content that is sexually suggestive or revealing, or could be considered objectionable.
    •Content that may be considered insulting, non-constructive, defamatory to individuals/organizations.
    •Staff/users' confidential or private information.
    •Any other content that is inconsistent with Sabot Community policies, code of conduct, or mission statement.

    Submit Review?
    """.trimIndent())
                        .setPositiveButton(R.string.yes) {
                            newReviewProgressBar!!.visibility = View.VISIBLE
                            newReviewDetails!!.visibility = View.GONE
                            val body = mFeedback!!.text.toString()
                            val addedBy = SharedPrefManager.getInstance(mContext!!)!!.username
                            val rating = mRatingBar!!.rating.toString()
                            val title = mTitle!!.text.toString()
                            submitReview(body, addedBy!!, clanId, rating, title)
                        }
                        .setNegativeButton(R.string.no, null)
                        .show()
            } else {
                Toast.makeText(mContext, "You must enter the required fields!", Toast.LENGTH_LONG).show()
                if (mRatingScale!!.text == "Please select a rating!") {
                    mRatingScale!!.setTextColor(Color.RED)
                }
            }
        }

        /* if(verified.equals("yes")){
            verifiedIcon.setVisibility(View.VISIBLE);
        }*/usernameReview!!.text = String.format("[%s]", clantag)
        nicknameReview!!.text = clanname
        Glide.with(mContext!!)
                .load(Constants.BASE_URL + clanPic)
                .error(R.mipmap.ic_launcher)
                .into(playerRatingProfilePhoto!!)
    }

    companion object {
        private const val New_Review_URL = Constants.ROOT_URL + "new_clan_review.php"
    }
}