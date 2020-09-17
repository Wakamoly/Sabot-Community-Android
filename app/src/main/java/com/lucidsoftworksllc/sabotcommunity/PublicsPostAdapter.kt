package com.lucidsoftworksllc.sabotcommunity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.PublicsPostAdapter.PublicsPostViewHolder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PublicsPostAdapter(private val mCtx: Context, private val publicsPostList: List<PublicsPostRecycler>) : RecyclerView.Adapter<PublicsPostViewHolder>() {
    var subCommentAdapter: PublicsSubCommentAdapter? = null
    var subCommentList: MutableList<PublicsSubCommentsRecycler>? = null
    private var layoutManager: LinearLayoutManager? = null
    private val isLoadingAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublicsPostViewHolder {
        var holder: PublicsPostViewHolder? = null
        val inflater = LayoutInflater.from(mCtx)
        when (viewType) {
            ITEM -> holder = getViewHolder(inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                holder = LoadingVH(v2)
            }
        }
        return holder!!
    }

    private fun getViewHolder(inflater: LayoutInflater): PublicsPostViewHolder {
        val holder: PublicsPostViewHolder
        val v1 = inflater.inflate(R.layout.recycler_publics_posts, null)
        holder = PublicsPostViewHolder(v1)
        return holder
    }

    private fun topicVote(holder: PublicsPostViewHolder, method: String, layout_id: String, user_to: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, VOTE_URL, Response.Listener { response: String? ->
            val obj: JSONObject
            try {
                obj = JSONObject(response!!)
                if (obj.getString("error") == "false") {
                    if (obj.getString("layout") == "post") {
                        when {
                            obj.getString("method") == "up" -> {
                                val newValue = (holder.textViewNumPublicsPoints.text.toString().toInt() + 1).toString()
                                holder.textViewNumPublicsPoints.text = newValue
                                holder.voteProgress.visibility = View.GONE
                                holder.publicsPostsUpvoteGreen.visibility = View.VISIBLE
                                holder.publicsPostsDownvoteWhite.visibility = View.VISIBLE
                                holder.publicsPostsUpvoteGreen.isEnabled = false
                                holder.publicsPostsDownvoteWhite.isEnabled = false
                                Handler().postDelayed({
                                    holder.publicsPostsUpvoteGreen.isEnabled = true
                                    holder.publicsPostsDownvoteWhite.isEnabled = true
                                }, 3000)
                            }
                            obj.getString("method") == "down" -> {
                                val newValue = holder.textViewNumPublicsPoints.text.toString().toInt().toString()
                                holder.textViewNumPublicsPoints.text = newValue
                                holder.voteProgress.visibility = View.GONE
                                holder.publicsPostsUpvoteWhite.visibility = View.VISIBLE
                                holder.publicsPostsDownvoteRed.visibility = View.VISIBLE
                                holder.publicsPostsUpvoteWhite.isEnabled = false
                                holder.publicsPostsDownvoteRed.isEnabled = false
                                Handler().postDelayed({
                                    holder.publicsPostsUpvoteWhite.isEnabled = true
                                    holder.publicsPostsDownvoteRed.isEnabled = true
                                }, 3000)
                            }
                            else -> {
                                var finalValue = ""
                                if (obj.getString("result") == "-1") {
                                    finalValue = (holder.textViewNumPublicsPoints.text.toString().toInt() - 1).toString()
                                } else if (obj.getString("result") == "+1") {
                                    finalValue = (holder.textViewNumPublicsPoints.text.toString().toInt() + 1).toString()
                                }
                                holder.textViewNumPublicsPoints.text = finalValue
                                holder.publicsPostsUpvoteWhite.visibility = View.VISIBLE
                                holder.publicsPostsDownvoteWhite.visibility = View.VISIBLE
                                holder.voteProgress.visibility = View.GONE
                                holder.publicsPostsUpvoteWhite.isEnabled = false
                                holder.publicsPostsDownvoteWhite.isEnabled = false
                                Handler().postDelayed({
                                    holder.publicsPostsUpvoteWhite.isEnabled = true
                                    holder.publicsPostsDownvoteWhite.isEnabled = true
                                }, 3000)
                            }
                        }
                    }
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                    holder.voteProgress.visibility = View.GONE
                    holder.publicsPostsUpvoteWhite.visibility = View.VISIBLE
                    holder.publicsPostsDownvoteWhite.visibility = View.VISIBLE
                    holder.publicsPostsUpvoteWhite.isEnabled = false
                    holder.publicsPostsDownvoteWhite.isEnabled = false
                    Handler().postDelayed({
                        holder.publicsPostsUpvoteWhite.isEnabled = true
                        holder.publicsPostsDownvoteWhite.isEnabled = true
                    }, 3000)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener {
            Toast.makeText(mCtx, "Could not vote, please try again later...", Toast.LENGTH_LONG).show()
            holder.voteProgress.visibility = View.GONE
            holder.publicsPostsUpvoteWhite.visibility = View.VISIBLE
            holder.publicsPostsDownvoteWhite.visibility = View.VISIBLE
            holder.publicsPostsUpvoteWhite.isEnabled = false
            holder.publicsPostsDownvoteWhite.isEnabled = false
            Handler().postDelayed({
                holder.publicsPostsUpvoteWhite.isEnabled = true
                holder.publicsPostsDownvoteWhite.isEnabled = true
            }, 3000)
        }) {
            override fun getParams(): Map<String, String> {
                val parms: MutableMap<String, String> = HashMap()
                parms["post_id"] = layout_id
                parms["method"] = method
                parms["layout"] = "post"
                parms["user_to"] = user_to
                parms["user_id"] = holder.userID
                parms["username"] = holder.deviceusername
                return parms
            }
        }
        (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
    }

    override fun onBindViewHolder(holder: PublicsPostViewHolder, position: Int) {
        val publics = publicsPostList[position]
        holder.publicsPostsClantag.text = publics.clantag
        holder.textViewNickname.text = publics.nickname
        holder.textViewPostBody.text = publics.post_content
        holder.publicsPostsDateTime.text = publics.post_date
        holder.textViewUsername.text = String.format("@%s", publics.username)
        holder.textViewNumPublicsPoints.text = publics.votes
        holder.numReplies.text = publics.replies
        if (publics.online == "yes") {
            holder.online.visibility = View.VISIBLE
        } else {
            holder.online.visibility = View.GONE
        }
        if (publics.verified == "yes") {
            holder.verified.visibility = View.VISIBLE
        } else {
            holder.verified.visibility = View.GONE
        }
        val profilePic = publics.profile_pic.substring(0, publics.profile_pic.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.imageView)
        holder.textViewNickname.setOnClickListener {
            if (mCtx is FragmentContainer) {
                val ldf = FragmentProfile()
                val args = Bundle()
                args.putString("UserId", publics.user_id)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
        }
        if (publics.voted == "up") {
            holder.publicsPostsUpvoteWhite.visibility = View.GONE
            holder.publicsPostsUpvoteGreen.visibility = View.VISIBLE
        } else if (publics.voted == "down") {
            holder.publicsPostsDownvoteWhite.visibility = View.GONE
            holder.publicsPostsDownvoteRed.visibility = View.VISIBLE
        }
        holder.publicsPostsUpvoteWhite.setOnClickListener {
            holder.publicsPostsDownvoteRed.visibility = View.GONE
            holder.publicsPostsDownvoteWhite.visibility = View.GONE
            holder.publicsPostsUpvoteGreen.visibility = View.GONE
            holder.publicsPostsUpvoteWhite.visibility = View.GONE
            holder.voteProgress.visibility = View.VISIBLE
            topicVote(holder, "up", publics.post_id, publics.post_by)
        }
        holder.publicsPostsUpvoteGreen.setOnClickListener {
            holder.publicsPostsDownvoteRed.visibility = View.GONE
            holder.publicsPostsDownvoteWhite.visibility = View.GONE
            holder.publicsPostsUpvoteGreen.visibility = View.GONE
            holder.publicsPostsUpvoteWhite.visibility = View.GONE
            holder.voteProgress.visibility = View.VISIBLE
            topicVote(holder, "remove", publics.post_id, publics.post_by)
        }
        holder.publicsPostsDownvoteWhite.setOnClickListener {
            holder.publicsPostsDownvoteRed.visibility = View.GONE
            holder.publicsPostsDownvoteWhite.visibility = View.GONE
            holder.publicsPostsUpvoteGreen.visibility = View.GONE
            holder.publicsPostsUpvoteWhite.visibility = View.GONE
            holder.voteProgress.visibility = View.VISIBLE
            topicVote(holder, "down", publics.post_id, publics.post_by)
        }
        holder.publicsPostsDownvoteRed.setOnClickListener {
            holder.publicsPostsDownvoteRed.visibility = View.GONE
            holder.publicsPostsDownvoteWhite.visibility = View.GONE
            holder.publicsPostsUpvoteGreen.visibility = View.GONE
            holder.publicsPostsUpvoteWhite.visibility = View.GONE
            holder.voteProgress.visibility = View.VISIBLE
            topicVote(holder, "remove", publics.post_id, publics.post_by)
        }
        holder.userPublicsPostsListLayout.setOnClickListener { replyLayoutClick(holder, publics.post_id, publics.id) }
        holder.replyText.setOnClickListener { replyLayoutClick(holder, publics.post_id, publics.id) }
    }

    private fun replyLayoutClick(holder: PublicsPostViewHolder, postid: String, topicid: String) {
        if (holder.replyEdittext.visibility == View.VISIBLE) {
            holder.replyText.visibility = View.VISIBLE
            holder.replyEdittext.visibility = View.GONE
            holder.replyButton.isEnabled = false
            holder.repliesRecycler.visibility = View.GONE
            holder.repliesProgress.visibility = View.GONE
            holder.replyButton.background = ContextCompat.getDrawable(mCtx,R.drawable.grey_transparent_wide_blob)
        } else if (holder.replyEdittext.visibility == View.GONE) {
            holder.replyText.visibility = View.GONE
            holder.replyEdittext.visibility = View.VISIBLE
            holder.replyButton.isEnabled = true
            holder.repliesRecycler.visibility = View.VISIBLE
            holder.repliesProgress.visibility = View.VISIBLE
            holder.replyButton.background = ContextCompat.getDrawable(mCtx,R.drawable.details_button)
            holder.replyButton.setOnClickListener { v: View ->
                val body = holder.replyEdittext.text.toString()
                val addedBy = holder.deviceusername
                val addedByUserId = holder.userID
                if (body.isNotEmpty()) {
                    holder.replyButton.visibility = View.GONE
                    holder.replyButtonProgress.visibility = View.VISIBLE
                    submitComment(body, addedBy, addedByUserId, postid, topicid, holder)
                    holder.replyEdittext.text.clear()
                    hideKeyboardFrom(mCtx, v)
                } else {
                    Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show()
                }
            }
            layoutManager = LinearLayoutManager(mCtx)
            layoutManager!!.stackFromEnd = true
            holder.repliesRecycler.layoutManager = layoutManager
            subCommentList = ArrayList()
            subCommentAdapter = PublicsSubCommentAdapter(mCtx, subCommentList!!)
            holder.repliesRecycler.adapter = subCommentAdapter
            val stringRequest = StringRequest(Request.Method.GET, SUB_COMMENTS_URL + "?userid=" + holder.userID + "&username=" + holder.deviceusername + "&postid=" + postid, { response: String? ->
                try {
                    val profilenews = JSONArray(response)
                    for (i in 0 until profilenews.length()) {
                        val profilenewsObject = profilenews.getJSONObject(i)
                        val online = profilenewsObject.getString("online")
                        val postId = profilenewsObject.getString("post_id")
                        val reply = profilenewsObject.getString("post_content")
                        val postDate = profilenewsObject.getString("post_date")
                        val profilePic = profilenewsObject.getString("profile_pic")
                        val nickname = profilenewsObject.getString("nickname")
                        val username = profilenewsObject.getString("username")
                        val userid = profilenewsObject.getString("userid")
                        val verified = profilenewsObject.getString("verified")
                        val clantag = profilenewsObject.getString("clantag")
                        val subCommentResult = PublicsSubCommentsRecycler(online, postId, reply, postDate, profilePic, nickname, userid, username, verified, clantag)
                        subCommentList?.add(subCommentResult)
                    }
                    subCommentAdapter!!.notifyDataSetChanged()
                    holder.repliesProgress.visibility = View.GONE
                } catch (e: JSONException) {
                    holder.repliesProgress.visibility = View.GONE
                    e.printStackTrace()
                }
            }) { holder.repliesProgress.visibility = View.GONE }
            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
        }
    }

    private fun submitComment(body: String, added_by: String, userID: String, post_id: String, topic_id: String, holder: PublicsPostViewHolder) {
        val profilePic = SharedPrefManager.getInstance(mCtx)!!.profilePic
        val nickname = SharedPrefManager.getInstance(mCtx)!!.nickname
        val stringRequest: StringRequest = object : StringRequest(Method.POST, SUB_COMMENTS_SUBMIT_URL,
                Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") != "true") {
                            subCommentList!!.add(PublicsSubCommentsRecycler("yes", null.toString(), body, "Just Now", profilePic!!, nickname!!, userID, added_by, "no", null.toString()))
                            holder.replyButton.visibility = View.VISIBLE
                            holder.replyButtonProgress.visibility = View.GONE
                            subCommentAdapter!!.notifyDataSetChanged()
                        } else {
                            holder.replyButtonProgress.visibility = View.GONE
                            Toast.makeText(mCtx, "Something went wrong, please try again later...", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                        Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
            holder.replyButtonProgress.visibility = View.GONE
            Toast.makeText(mCtx, "Network error, please try again later...", Toast.LENGTH_LONG).show()
        }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["body"] = body
                params["added_by"] = added_by
                params["post_id"] = post_id
                params["topic_id"] = topic_id
                params["user_id"] = userID
                return params
            }
        }
        (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
    }

    private inner class LoadingVH(itemView: View) : PublicsPostViewHolder(itemView)

    override fun getItemCount(): Int {
        return publicsPostList.size
    }

    open inner class PublicsPostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.publicsPostsProfile_image)
        var publicsPostsUpvoteWhite: ImageView = itemView.findViewById(R.id.publicsPostsUpvoteWhiteBottom)
        var publicsPostsDownvoteWhite: ImageView = itemView.findViewById(R.id.publicsPostsDownvoteWhiteBottom)
        var publicsPostsUpvoteGreen: ImageView = itemView.findViewById(R.id.publicsPostsUpvoteGreen)
        var publicsPostsDownvoteRed: ImageView = itemView.findViewById(R.id.publicsPostsDownvoteRed)
        var replyButton: ImageView = itemView.findViewById(R.id.reply_button)
        var online: ImageView = itemView.findViewById(R.id.online)
        var verified: ImageView = itemView.findViewById(R.id.verified)
        var textViewNickname: TextView = itemView.findViewById(R.id.publicsPostsNickname)
        var textViewUsername: TextView = itemView.findViewById(R.id.publicsPostsUsername)
        var textViewPostBody: TextView = itemView.findViewById(R.id.publicsPostBody)
        var textViewNumPublicsPoints: TextView = itemView.findViewById(R.id.textViewNumPublicsPoints)
        var publicsPostsDateTime: TextView = itemView.findViewById(R.id.publicsPostsDateTime)
        var numReplies: TextView = itemView.findViewById(R.id.num_replies)
        var publicsPostsClantag: TextView = itemView.findViewById(R.id.publicsPostsClantag)
        var voteProgress: ProgressBar = itemView.findViewById(R.id.voteProgress)
        var userID: String = SharedPrefManager.getInstance(mCtx)!!.userID!!
        var deviceusername: String = SharedPrefManager.getInstance(mCtx)!!.username!!
        var replyLayout: LinearLayout = itemView.findViewById(R.id.reply_layout)
        var replyText: LinearLayout = itemView.findViewById(R.id.reply_text)
        var replyEdittext: EditText = itemView.findViewById(R.id.reply_edittext)
        var repliesRecycler: RecyclerView = itemView.findViewById(R.id.replies_recycler)
        var userPublicsPostsListLayout: MaterialRippleLayout = itemView.findViewById(R.id.userPublicsPostsListLayout)
        var repliesProgress: ProgressBar = itemView.findViewById(R.id.replies_progress)
        var replyButtonProgress: ProgressBar = itemView.findViewById(R.id.reply_button_progress)

    }

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
        private const val VOTE_URL = Constants.ROOT_URL + "publics_topic_vote.php"
        private const val SUB_COMMENTS_URL = Constants.ROOT_URL + "publics_post_subcomments.php"
        private const val SUB_COMMENTS_SUBMIT_URL = Constants.ROOT_URL + "publics_subcomment_submit.php"
        fun hideKeyboardFrom(context: Context, view: View) {
            val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}