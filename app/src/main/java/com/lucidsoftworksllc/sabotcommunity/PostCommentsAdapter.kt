package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.PostCommentsAdapter.ProfileCommentsViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class PostCommentsAdapter(private val mCtx: Context, private val postCommentsList: List<PostCommentRecycler>) : RecyclerView.Adapter<ProfileCommentsViewHolder>() {
    private val isLoadingAdded = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileCommentsViewHolder {
        var holder: ProfileCommentsViewHolder? = null
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

    private fun getViewHolder(inflater: LayoutInflater): ProfileCommentsViewHolder {
        val holder: ProfileCommentsViewHolder
        val v1 = inflater.inflate(R.layout.recycler_profilenews, null)
        holder = ProfileCommentsViewHolder(v1)
        return holder
    }

    override fun onBindViewHolder(holder: ProfileCommentsViewHolder, position: Int) {
        val comment = postCommentsList[position]
        holder.textViewNickname.text = comment.nickname
        holder.textViewPostBody.text = comment.body
        val bodybits = comment.body.split("\\s+".toRegex()).toTypedArray()
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
                holder.urlPreview.visibility = View.VISIBLE
                holder.urlImage.setOnClickListener {
                    val uri = Uri.parse(finalItem)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    mCtx.startActivity(intent)
                }
                Thread( Runnable {
                    try {
                        val doc = Jsoup.connect(finalItem).get()
                        val ogTags = doc.select("meta[property^=og:]")
                        if (ogTags.size <= 0) {
                            return@Runnable
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

                val handler = Handler()
                handler.postDelayed({
                    Glide.with(mCtx)
                            .load(imageUrl[0])
                            .error(R.drawable.ic_error)
                            .into(holder.urlImage)
                    if (title[0] != null) {
                        holder.urlTitle.text = title[0]
                    } else {
                        holder.urlTitle.text = mCtx.getString(R.string.no_content)
                    }
                    if (desc[0] != null) {
                        holder.urlDesc.text = desc[0]
                    }
                    holder.urlProgress.visibility = View.GONE
                    holder.urlBits.visibility = View.VISIBLE
                }, 5000)
                break
            }
        }
        holder.textViewPostDateTime.text = comment.time
        holder.textViewUsername.text = String.format("@%s", comment.username)
        holder.textViewNumLikes.text = comment.likes
        holder.textViewUsernameTo.visibility = View.GONE
        holder.visIfPost.visibility = View.GONE
        holder.textViewUsername.setTextColor(ContextCompat.getColor(mCtx, R.color.light_blue))
        val profilePic = comment.profile_pic.substring(0, comment.profile_pic.length - 4) + "_r.JPG"
        Glide.with(mCtx)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.imageView)
        holder.textViewNickname.setOnClickListener {
            if (mCtx is FragmentContainer) {
                val ldf = FragmentProfile()
                val args = Bundle()
                args.putString("UserId", comment.user_id)
                args.putString("Username", comment.username)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
        }
        if (comment.likedbyuseryes == "yes") {
            holder.likeView.visibility = View.GONE
            holder.likedView.visibility = View.VISIBLE
        }
        holder.likeView.setOnClickListener {
            holder.likeView.visibility = View.GONE
            holder.likeProgress.visibility = View.VISIBLE
            val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                val obj: JSONObject
                try {
                    obj = JSONObject(response!!)
                    if (!obj.getBoolean("error")) {
                        val newValue = (holder.textViewNumLikes.text.toString().toInt() + 1).toString()
                        holder.textViewNumLikes.text = newValue
                        holder.likeProgress.visibility = View.GONE
                        holder.likedView.visibility = View.VISIBLE
                        holder.likedView.isEnabled = false
                        Handler().postDelayed({ holder.likedView.isEnabled = true }, 3500)
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                        holder.likeProgress.visibility = View.GONE
                        holder.likeView.visibility = View.VISIBLE
                        holder.likeView.isEnabled = false
                        Handler().postDelayed({ holder.likeView.isEnabled = true }, 3000)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(mCtx, "Could not like, please try again later...", Toast.LENGTH_LONG).show()
                holder.likeProgress.visibility = View.GONE
                holder.likeView.visibility = View.VISIBLE
                holder.likeView.isEnabled = false
                Handler().postDelayed({ holder.likeView.isEnabled = true }, 3000)
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["comment_id"] = comment.id
                    params["method"] = "like"
                    params["user_to"] = comment.username
                    params["user_id"] = holder.userID
                    params["username"] = holder.username
                    params["body"] = comment.body
                    params["post_id"] = comment.post_id
                    return params
                }
            }
            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
        }
        holder.likedView.setOnClickListener {
            holder.likedView.visibility = View.GONE
            holder.likeProgress.visibility = View.VISIBLE
            val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                val obj: JSONObject
                try {
                    obj = JSONObject(response!!)
                    if (!obj.getBoolean("error")) {
                        val newValue = (holder.textViewNumLikes.text.toString().toInt() - 1).toString()
                        holder.textViewNumLikes.text = newValue
                        holder.likeProgress.visibility = View.GONE
                        holder.likeView.visibility = View.VISIBLE
                        holder.likeView.isEnabled = false
                        Handler().postDelayed({ holder.likeView.isEnabled = true }, 3500)
                    } else {
                        Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                        holder.likeProgress.visibility = View.GONE
                        holder.likedView.visibility = View.VISIBLE
                        holder.likedView.isEnabled = false
                        Handler().postDelayed({ holder.likedView.isEnabled = true }, 3000)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener {
                Toast.makeText(mCtx, "Could not remove like, please try again later...", Toast.LENGTH_LONG).show()
                holder.likeProgress.visibility = View.GONE
                holder.likedView.visibility = View.VISIBLE
                holder.likedView.isEnabled = false
                Handler().postDelayed({ holder.likeView.isEnabled = true }, 3000)
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String, String> = HashMap()
                    params["comment_id"] = comment.id
                    params["method"] = "unlike"
                    params["user_to"] = comment.username
                    params["user_id"] = holder.userID
                    params["username"] = holder.username
                    params["body"] = comment.body
                    params["post_id"] = comment.post_id
                    return params
                }
            }
            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
        }
        if (comment.online == "yes") {
            holder.online.visibility = View.VISIBLE
        } else {
            holder.online.visibility = View.GONE
        }
        if (comment.verified == "yes") {
            holder.verified.visibility = View.VISIBLE
        } else {
            holder.verified.visibility = View.GONE
        }
        holder.textViewLikes.setOnClickListener {
            val asf: Fragment = UserListFragment()
            val args = Bundle()
            args.putString("query", "comment")
            args.putString("queryID", comment.id)
            asf.arguments = args
            val fragmentTransaction = (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
            fragmentTransaction.add(R.id.fragment_container, asf)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
        if (comment.isEdited == "yes") {
            holder.tvEdited.visibility = View.VISIBLE
        } else {
            holder.tvEdited.visibility = View.GONE
        }
        holder.contentDivider.visibility = View.GONE
    }

    private inner class LoadingVH(itemView: View) : ProfileCommentsViewHolder(itemView)

    override fun getItemCount(): Int {
        return postCommentsList.size
    }

    open inner class ProfileCommentsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var verified: CircleImageView
        var online: CircleImageView
        private var contentLayout: MaterialRippleLayout
        var urlBits: LinearLayout
        var urlPreview: LinearLayout
        private var likesLayout: LinearLayout
        var imageView: ImageView
        var urlImage: ImageView
        var visIfPost: RelativeLayout
        var likeProgress: ProgressBar
        var urlProgress: ProgressBar
        var likeView: ImageView
        var likedView: ImageView
        var contentDivider: ImageView
        var tvEdited: TextView
        var textViewNickname: TextView
        var textViewUsername: TextView
        var textViewUsernameTo: TextView
        var textViewPostBody: TextView
        var textViewPostDateTime: TextView
        var textViewNumLikes: TextView
        var urlTitle: TextView
        var urlDesc: TextView
        var textViewLikes: TextView
        var userID: String
        var username: String
        private var sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(mCtx)!!

        init {
            userID = sharedPrefManager.userID!!
            verified = itemView.findViewById(R.id.verified)
            online = itemView.findViewById(R.id.online)
            username = sharedPrefManager.username!!
            visIfPost = itemView.findViewById(R.id.visIfPost)
            imageView = itemView.findViewById(R.id.imageViewProfilenewsPic)
            likeProgress = itemView.findViewById(R.id.likeProgress)
            likeView = itemView.findViewById(R.id.like)
            likedView = itemView.findViewById(R.id.liked)
            textViewNickname = itemView.findViewById(R.id.textViewProfileName)
            textViewUsername = itemView.findViewById(R.id.postUsername_top)
            textViewUsernameTo = itemView.findViewById(R.id.textViewToUserName)
            textViewPostBody = itemView.findViewById(R.id.textViewBody)
            textViewPostDateTime = itemView.findViewById(R.id.profileCommentsDateTime_top)
            textViewNumLikes = itemView.findViewById(R.id.textViewNumLikes)
            urlPreview = itemView.findViewById(R.id.urlPreview)
            urlProgress = itemView.findViewById(R.id.urlProgress)
            urlImage = itemView.findViewById(R.id.urlImage)
            urlTitle = itemView.findViewById(R.id.urlTitle)
            urlDesc = itemView.findViewById(R.id.urlDesc)
            urlBits = itemView.findViewById(R.id.urlBits)
            likesLayout = itemView.findViewById(R.id.likesLayout)
            contentLayout = itemView.findViewById(R.id.contentLayout)
            textViewLikes = itemView.findViewById(R.id.textViewLikes)
            contentDivider = itemView.findViewById(R.id.contentDivider)
            tvEdited = itemView.findViewById(R.id.tvEdited)
        }
    }

    companion object {
        private const val LIKE_URL = Constants.ROOT_URL + "comment_like.php"
        private const val ITEM = 0
        private const val LOADING = 1
    }
}