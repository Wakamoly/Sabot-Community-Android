package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Patterns
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*

class ProfilenewsAdapter(private val mCtx: Context, private val profilenewsList: MutableList<ProfilenewsRecycler>?) : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.recycler_profilenews, parent, false))
            VIEW_TYPE_LOADING -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false))
            else -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false))
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == profilenewsList!!.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return profilenewsList?.size ?: 0
    }

    fun addItems(items: List<ProfilenewsRecycler>?) {
        profilenewsList!!.addAll(items!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        profilenewsList!!.add(ProfilenewsRecycler(0, null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString()))
        notifyItemInserted(profilenewsList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        if (profilenewsList!!.size != 0) {
            val position = profilenewsList.size - 1
            val item = getItem(position)
            profilenewsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        profilenewsList!!.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): ProfilenewsRecycler {
        return profilenewsList!![position]
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var verified: CircleImageView
        var online: CircleImageView
        private var urlBits: LinearLayout
        private var urlPreview: LinearLayout
        var publicsTopicList: RelativeLayout
        var contentLayout: MaterialRippleLayout
        var likeProgress: ProgressBar
        private var urlProgress: ProgressBar
        private var sharedPrefManager: SharedPrefManager = SharedPrefManager.getInstance(mCtx)!!
        private var imageProfilenewsView: ImageView
        var imageViewProfilenewsPic: ImageView
        private var notiType: ImageView
        var likeView: ImageView
        var likedView: ImageView
        private var urlImage: ImageView
        private var tvEdited: TextView
        var textViewBody: TextView
        private var textviewaddedBy: TextView
        private var textviewdateAdded: TextView
        private var textviewuserTo: TextView
        var textViewLikes: TextView
        private var postusernameTop: TextView
        private var textViewNumComments: TextView
        private var urlTitle: TextView
        private var urlDesc: TextView
        private var textViewComments: TextView
        private var textViewLikesText: TextView
        var userID: String
        var username: String
        override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
            val profilenews = profilenewsList!![position]
            textViewBody.text = profilenews.body
            textViewNumComments.text = profilenews.commentcount
            textviewaddedBy.text = profilenews.nickname
            textviewdateAdded.text = profilenews.date_added
            textViewLikes.text = profilenews.likes
            postusernameTop.text = String.format("@%s", profilenews.added_by)
            val userTo = profilenews.user_to
            if (userTo != "none") {
                when (profilenews.form) {
                    "user" -> textviewuserTo.text = String.format("to @%s", userTo)
                    "clan" -> {
                        textviewuserTo.text = String.format("to [%s]", userTo)
                        textviewuserTo.setTextColor(ContextCompat.getColor(mCtx, R.color.pin))
                    }
                    "event" -> {
                    }
                }
            } else {
                textviewuserTo.visibility = View.GONE
            }
            when (profilenews.type) {
                "Xbox" -> {
                    notiType.setImageResource(R.drawable.icons8_xbox_50)
                    notiType.visibility = View.VISIBLE
                }
                "PlayStation" -> {
                    notiType.setImageResource(R.drawable.icons8_playstation_50)
                    notiType.visibility = View.VISIBLE
                }
                "Steam" -> {
                    notiType.setImageResource(R.drawable.icons8_steam_48)
                    notiType.visibility = View.VISIBLE
                }
                "PC" -> {
                    notiType.setImageResource(R.drawable.icons8_workstation_48)
                    notiType.visibility = View.VISIBLE
                }
                "Mobile" -> {
                    notiType.setImageResource(R.drawable.icons8_mobile_48)
                    notiType.visibility = View.VISIBLE
                }
                "Switch" -> {
                    notiType.setImageResource(R.drawable.icons8_nintendo_switch_48)
                    notiType.visibility = View.VISIBLE
                }
                "General" -> {
                }
                else -> {
                    notiType.setImageResource(R.drawable.icons8_question_mark_64)
                    notiType.visibility = View.VISIBLE
                }
            }
            contentLayout.setOnLongClickListener { v: View? ->
                val popup = PopupMenu(mCtx, v)
                val inflater = popup.menuInflater
                if (profilenews.added_by == username) {
                    inflater.inflate(R.menu.profile_post_owner, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem ->
                        if (item.itemId == R.id.menuDelete) {
                            LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(R.drawable.ic_error)
                                    .setTitle(R.string.delete_post_string)
                                    .setMessage(R.string.confirm)
                                    .setPositiveButton(R.string.yes) {
                                        val stringRequest: StringRequest = object : StringRequest(Method.POST, POST_DELETE, Response.Listener { response: String? ->
                                            try {
                                                val jsonObject = JSONObject(response!!)
                                                if (jsonObject.getString("error") == "false") {
                                                    Toast.makeText(mCtx, "Post deleted!", Toast.LENGTH_LONG).show()
                                                    contentLayout.visibility = View.GONE
                                                } else {
                                                    Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show()
                                                }
                                            } catch (e: JSONException) {
                                                e.printStackTrace()
                                            }
                                        }, Response.ErrorListener { Toast.makeText(mCtx, "Network error, please try again later...", Toast.LENGTH_LONG).show() }) {
                                            override fun getParams(): Map<String, String> {
                                                val params: MutableMap<String, String> = HashMap()
                                                params["postid"] = profilenews.id.toString()
                                                params["username"] = username
                                                params["userid"] = userID
                                                return params
                                            }
                                        }
                                        (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
                                    }
                                    .setNegativeButton(R.string.no, null)
                                    .show()
                        }
                        if (item.itemId == R.id.menuEdit) {
                            val ldf = ProfilePostEditFragment()
                            val args = Bundle()
                            args.putString("id", profilenews.id.toString())
                            ldf.arguments = args
                            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        if (item.itemId == R.id.menuReport) {
                            val ldf = ReportFragment()
                            val args = Bundle()
                            args.putString("context", profilenews.body)
                            args.putString("type", "post")
                            args.putString("id", profilenews.id.toString())
                            ldf.arguments = args
                            //Inflate the fragment
                            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        true
                    }
                } else {
                    inflater.inflate(R.menu.profile_post_nonowner, popup.menu)
                    popup.setOnMenuItemClickListener { item: MenuItem ->
                        if (item.itemId == R.id.menuReport) {
                            val ldf = ReportFragment()
                            val args = Bundle()
                            args.putString("context", profilenews.body)
                            args.putString("type", "post")
                            args.putString("id", profilenews.id.toString())
                            ldf.arguments = args
                            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        true
                    }
                }
                popup.show()
                false
            }
            contentLayout.setOnClickListener {
                val ldf = ProfilePostFragment()
                val args = Bundle()
                args.putString("id", profilenews.id.toString())
                args.putString("UserId", profilenews.user_id)
                args.putString("Username", profilenews.added_by)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            textviewaddedBy.setOnClickListener {
                val ldf = FragmentProfile()
                val args = Bundle()
                args.putString("UserId", profilenews.user_id)
                args.putString("Username", profilenews.added_by)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            val profilePic = profilenews.profile_pic.substring(0, profilenews.profile_pic.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic)
                    .into(imageViewProfilenewsPic)
            if (profilenews.image.isNotEmpty()) {
                imageProfilenewsView.visibility = View.VISIBLE
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + profilenews.image).override(1000)
                        .into(imageProfilenewsView)
            } else {
                imageProfilenewsView.visibility = View.GONE
            }
            if (profilenews.likedbyuseryes == "yes") {
                likeView.visibility = View.GONE
                likedView.visibility = View.VISIBLE
            } else {
                likeView.visibility = View.VISIBLE
                likedView.visibility = View.GONE
            }
            likeView.setOnClickListener {
                likeView.isEnabled = false
                likeView.visibility = View.GONE
                //val unlikeAnim:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.fade_out)
                val likeAppear:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)

                val newValue = (textViewLikes.text.toString().toInt() + 1).toString()
                textViewLikes.text = newValue
                likeProgress.visibility = View.GONE
                likedView.visibility = View.VISIBLE
                likedView.startAnimation(likeAppear)
                likedView.isEnabled = false
                Handler().postDelayed({ likedView.isEnabled = true }, 3500)
                //likeProgress.visibility = View.VISIBLE
                val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                    val obj: JSONObject
                    try {
                        obj = JSONObject(response!!)
                        if (obj.getBoolean("error")) {
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                            //likeProgress.visibility = View.GONE
                            likeView.visibility = View.VISIBLE
                            likeView.isEnabled = false
                            Handler().postDelayed({ likeView.isEnabled = true }, 3000)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(mCtx, "Could not like, please try again later...", Toast.LENGTH_LONG).show()
                    likeProgress.visibility = View.GONE
                    likeView.visibility = View.VISIBLE
                    likeView.isEnabled = false
                    Handler().postDelayed({ likeView.isEnabled = true }, 3000)
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["post_id"] = profilenews.id.toString()
                        params["method"] = "like"
                        params["user_to"] = profilenews.username
                        params["user_id"] = userID
                        params["username"] = username
                        return params
                    }
                }
                (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
            }
            likedView.setOnClickListener {
                likedView.isEnabled = false
                likedView.visibility = View.GONE
                val likedAppear:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)

                val newValue = (textViewLikes.text.toString().toInt() - 1).toString()
                textViewLikes.text = newValue
                likeProgress.visibility = View.GONE
                likeView.visibility = View.VISIBLE
                likeView.startAnimation(likedAppear)
                likeView.isEnabled = false
                Handler().postDelayed({ likeView.isEnabled = true }, 3500)
                val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                    val obj: JSONObject
                    try {
                        obj = JSONObject(response!!)
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                            likeProgress.visibility = View.GONE
                            likedView.visibility = View.VISIBLE
                            likedView.isEnabled = false
                            Handler().postDelayed({ likedView.isEnabled = true }, 3000)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(mCtx, "Could not remove like, please try again later...", Toast.LENGTH_LONG).show()
                    likeProgress.visibility = View.GONE
                    likedView.visibility = View.VISIBLE
                    likedView.isEnabled = false
                    Handler().postDelayed({ likeView.isEnabled = true }, 3000)
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["post_id"] = profilenews.id.toString()
                        params["method"] = "unlike"
                        params["user_to"] = profilenews.username
                        params["user_id"] = userID
                        params["username"] = username
                        return params
                    }
                }
                (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
            }
            if (profilenews.online == "yes") {
                online.visibility = View.VISIBLE
            } else {
                online.visibility = View.GONE
            }
            if (profilenews.verified == "yes") {
                verified.visibility = View.VISIBLE
            } else {
                verified.visibility = View.GONE
            }
            val bodybits = profilenews.body.split("\\s+".toRegex()).toTypedArray()
            for (item in bodybits) {
                if (Patterns.WEB_URL.matcher(item).matches()) {
                    val finalItem: String = if (!item.contains("http://") && !item.contains("https://")) {
                        "https://$item"
                    } else {
                        item
                    }
                    val imageUrl = arrayOfNulls<String>(1)
                    val title = arrayOfNulls<String>(1)
                    val desc = arrayOfNulls<String>(1)
                    urlPreview.visibility = View.VISIBLE
                    urlImage.setOnClickListener {
                        val uri = Uri.parse(finalItem)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        mCtx.startActivity(intent)
                    }
                    Thread( Runnable runnable@ {
                        try {
                            val doc = Jsoup.connect(finalItem).get()
                            val ogTags = doc.select("meta[property^=og:]")
                            if (ogTags.size <= 0) {
                                return@runnable
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
                    //Fucking code wouldn't work any other way than I'm currently capable. Fuck it, have a delay
                    val handler = Handler()
                    handler.postDelayed({
                        if (imageUrl[0] != null && imageUrl[0]!!.isEmpty()) {
                            urlImage.setImageResource(R.drawable.ic_error)
                        } else {
                            Glide.with(mCtx)
                                    .load(imageUrl[0])
                                    .error(R.drawable.ic_error)
                                    .into(urlImage)
                        }
                        if (title[0] != null) {
                            urlTitle.text = title[0]
                        } else {
                            urlTitle.text = mCtx.getString(R.string.no_content)
                        }
                        if (desc[0] != null) {
                            urlDesc.text = desc[0]
                        }
                        urlProgress.visibility = View.GONE
                        urlBits.visibility = View.VISIBLE
                    }, 5000)
                    break
                } else {
                    urlPreview.visibility = View.GONE
                }
            }
            textViewLikesText.setOnClickListener {
                val asf: Fragment = UserListFragment()
                val args = Bundle()
                args.putString("query", "post")
                args.putString("queryID", profilenews.id.toString())
                asf.arguments = args
                val fragmentTransaction = (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out)
                fragmentTransaction.add(R.id.fragment_container, asf)
                fragmentTransaction.addToBackStack(null)
                fragmentTransaction.commit()
            }
            textViewComments.setOnClickListener {
                val ldf = ProfilePostFragment()
                val args = Bundle()
                args.putString("id", profilenews.id.toString())
                args.putString("UserId", profilenews.user_id)
                args.putString("Username", profilenews.added_by)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in,R.anim.fade_out,R.anim.fade_in,R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            if (profilenews.isEdited == "yes") {
                tvEdited.visibility = View.VISIBLE
            } else {
                tvEdited.visibility = View.GONE
            }
        }

        init {
            userID = sharedPrefManager.userID!!
            username = sharedPrefManager.username!!
            verified = itemView.findViewById(R.id.verified)
            online = itemView.findViewById(R.id.online)
            likeProgress = itemView.findViewById(R.id.likeProgress)
            likeView = itemView.findViewById(R.id.like)
            likedView = itemView.findViewById(R.id.liked)
            notiType = itemView.findViewById(R.id.platformType)
            publicsTopicList = itemView.findViewById(R.id.publicsTopicList)
            textviewaddedBy = itemView.findViewById(R.id.textViewProfileName)
            postusernameTop = itemView.findViewById(R.id.postUsername_top)
            textviewuserTo = itemView.findViewById(R.id.textViewToUserName)
            imageViewProfilenewsPic = itemView.findViewById(R.id.imageViewProfilenewsPic)
            imageProfilenewsView = itemView.findViewById(R.id.profileNewsImage)
            textViewBody = itemView.findViewById(R.id.textViewBody)
            textViewLikes = itemView.findViewById(R.id.textViewNumLikes)
            textviewdateAdded = itemView.findViewById(R.id.profileCommentsDateTime_top)
            textViewNumComments = itemView.findViewById(R.id.textViewNumComments)
            urlPreview = itemView.findViewById(R.id.urlPreview)
            urlProgress = itemView.findViewById(R.id.urlProgress)
            urlImage = itemView.findViewById(R.id.urlImage)
            urlTitle = itemView.findViewById(R.id.urlTitle)
            urlDesc = itemView.findViewById(R.id.urlDesc)
            urlBits = itemView.findViewById(R.id.urlBits)
            textViewComments = itemView.findViewById(R.id.textViewComments)
            textViewLikesText = itemView.findViewById(R.id.textViewLikes)
            contentLayout = itemView.findViewById(R.id.contentLayout)
            tvEdited = itemView.findViewById(R.id.tvEdited)
        }
    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val LIKE_URL = Constants.ROOT_URL + "post_like.php"
        private const val POST_DELETE = Constants.ROOT_URL + "profile_post_action.php/post_delete"
    }
}