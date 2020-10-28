package com.lucidsoftworksllc.sabotcommunity.adapters

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
import androidx.recyclerview.widget.*
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.fragments.*
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewHolder
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.others.visible
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ProfilenewsAdapter(private val mCtx: Context,
                         private val interaction: Interaction? = null) : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    private val profilenewsList: MutableList<ProfilenewsRecycler> = ArrayList()
    private val NO_MORE_RESULTS = -1
    private val NO_MORE_POSTS = ProfilenewsRecycler(NO_MORE_RESULTS, null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString())
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

    /*internal inner class PostRecyclerChangeCallback(
            private val adapter: ProfilenewsAdapter
    ) : ListUpdateCallback {

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            adapter.notifyItemRangeChanged(position, count, payload)
        }

        override fun onInserted(position: Int, count: Int) {
            adapter.notifyItemRangeChanged(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            adapter.notifyDataSetChanged()
        }

        override fun onRemoved(position: Int, count: Int) {
            adapter.notifyDataSetChanged()
        }
    }

    fun submitList(
            postList: List<ProfilenewsRecycler>?,
            isQueryExhausted: Boolean
    ){
        val newList = postList?.toMutableList()
        if (isQueryExhausted)
            newList?.add(NO_MORE_POSTS)
        val commitCallback = Runnable {
            // if process died must restore list position
            // very annoying
            interaction?.restoreListPosition()
        }
        differ.submitList(newList, commitCallback)
    }

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ProfilenewsRecycler>() {

        override fun areItemsTheSame(oldItem: ProfilenewsRecycler, newItem: ProfilenewsRecycler): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProfilenewsRecycler, newItem: ProfilenewsRecycler): Boolean {
            return oldItem == newItem
        }

    }

    private val differ =
            AsyncListDiffer(
                    PostRecyclerChangeCallback(this),
                    AsyncDifferConfig.Builder(DIFF_CALLBACK).build()
            )
*/
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == profilenewsList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return profilenewsList.size ?: 0
    }

    fun clear() {
        profilenewsList.clear()
        notifyDataSetChanged()
    }

    fun addItems(items: List<ProfilenewsRecycler>?) {
        profilenewsList.addAll(items!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        profilenewsList.add(ProfilenewsRecycler(0, null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString()))
        notifyItemInserted(profilenewsList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        if (profilenewsList.size != 0) {
            val position = profilenewsList.size - 1
            val item = getItem(position)
            profilenewsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ProfilenewsRecycler {
        return profilenewsList[position]
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
            val profilenews = profilenewsList[position]
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
                textviewuserTo.visible(false)
            }
            when (profilenews.type) {
                "Xbox" -> {
                    notiType.setImageResource(R.drawable.icons8_xbox_50)
                    notiType.visible(true)
                }
                "PlayStation" -> {
                    notiType.setImageResource(R.drawable.icons8_playstation_50)
                    notiType.visible(true)
                }
                "Steam" -> {
                    notiType.setImageResource(R.drawable.icons8_steam_48)
                    notiType.visible(true)
                }
                "PC" -> {
                    notiType.setImageResource(R.drawable.icons8_workstation_48)
                    notiType.visible(true)
                }
                "Mobile" -> {
                    notiType.setImageResource(R.drawable.icons8_mobile_48)
                    notiType.visible(true)
                }
                "Switch" -> {
                    notiType.setImageResource(R.drawable.icons8_nintendo_switch_48)
                    notiType.visible(true)
                }
                "General" -> { }
                else -> {
                    notiType.setImageResource(R.drawable.icons8_question_mark_64)
                    notiType.visible(true)
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
                                                    contentLayout.visible(false)
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
                            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        if (item.itemId == R.id.menuReport) {
                            val ldf = ReportFragment()
                            val args = Bundle()
                            args.putString("context", profilenews.body)
                            args.putString("type", "post")
                            args.putString("id", profilenews.id.toString())
                            ldf.arguments = args
                            //Inflate the fragment
                            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
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
                            (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
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
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            textviewaddedBy.setOnClickListener {
                val ldf = FragmentProfile()
                val args = Bundle()
                args.putString("UserId", profilenews.user_id)
                args.putString("Username", profilenews.added_by)
                ldf.arguments = args
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            val profilePic = profilenews.profile_pic.substring(0, profilenews.profile_pic.length - 4) + "_r.JPG"
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profilePic)
                    .into(imageViewProfilenewsPic)
            if (profilenews.image.isNotEmpty()) {
                imageProfilenewsView.visible(true)
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + profilenews.image).override(1000)
                        .into(imageProfilenewsView)
            } else {
                imageProfilenewsView.visible(false)
            }
            if (profilenews.likedbyuseryes == "yes") {
                likeView.visible(false)
                likedView.visible(true)
            } else {
                likeView.visible(true)
                likedView.visible(false)
            }
            likeView.setOnClickListener {
                likeView.isEnabled = false
                likeView.visible(false)
                //val unlikeAnim:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.fade_out)
                val likeAppear:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)

                val newValue = (textViewLikes.text.toString().toInt() + 1).toString()
                textViewLikes.text = newValue
                likeProgress.visible(false)
                likedView.visible(true)
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
                            likeView.visible(true)
                            likeView.isEnabled = false
                            Handler().postDelayed({ likeView.isEnabled = true }, 3000)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(mCtx, "Could not like, please try again later...", Toast.LENGTH_LONG).show()
                    likeProgress.visible(false)
                    likeView.visible(true)
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
                likedView.visible(false)
                val likedAppear:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)

                val newValue = (textViewLikes.text.toString().toInt() - 1).toString()
                textViewLikes.text = newValue
                likeProgress.visible(false)
                likeView.visible(true)
                likeView.startAnimation(likedAppear)
                likeView.isEnabled = false
                Handler().postDelayed({ likeView.isEnabled = true }, 3500)
                val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                    val obj: JSONObject
                    try {
                        obj = JSONObject(response!!)
                        if (!obj.getBoolean("error")) {
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                            likeProgress.visible(false)
                            likedView.visible(true)
                            likedView.isEnabled = false
                            Handler().postDelayed({ likedView.isEnabled = true }, 3000)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(mCtx, "Could not remove like, please try again later...", Toast.LENGTH_LONG).show()
                    likeProgress.visible(false)
                    likedView.visible(true)
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
                online.visible(true)
            } else {
                online.visible(false)
            }
            if (profilenews.verified == "yes") {
                verified.visible(true)
            } else {
                verified.visible(false)
            }
            val bodybits = profilenews.body.split("\\s+".toRegex()).toTypedArray()
            for (item in bodybits) {
                if (Patterns.WEB_URL.matcher(item).matches()) {
                    val finalItem: String = if (!item.contains("http://") && !item.contains("https://")) {
                        "https://$item"
                    } else {
                        if (item.contains("http://")){
                            item.replace("http://","https://")
                        }else{
                            item
                        }
                    }
                    val imageUrl = arrayOfNulls<String>(1)
                    val title = arrayOfNulls<String>(1)
                    val desc = arrayOfNulls<String>(1)
                    urlPreview.visible(true)
                    urlImage.setOnClickListener {
                        val uri = Uri.parse(finalItem)
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        mCtx.startActivity(intent)
                    }
                        try {
                            CoroutineScope(IO).launch {
                                val doc = Jsoup.connect(finalItem).get()
                                val ogTags = doc.select("meta[property^=og:]")
                                if (ogTags.size <= 0) {
                                    return@launch
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
                                addPostLinkBits(imageUrl[0], title[0], desc[0])
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    break
                } else {
                    urlPreview.visible(false)
                }
                break
            }
            textViewLikesText.setOnClickListener {
                val asf: Fragment = UserListFragment()
                val args = Bundle()
                args.putString("query", "post")
                args.putString("queryID", profilenews.id.toString())
                asf.arguments = args
                val fragmentTransaction = (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
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
                (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            if (profilenews.isEdited == "yes") {
                tvEdited.visible(true)
            } else {
                tvEdited.visible(false)
            }
        }

        private fun addPostLinkBits(imageUrl: String?, title: String?, desc: String?){
            CoroutineScope(Main).launch {
                if (imageUrl!!.isEmpty()) {
                    urlImage.setImageResource(R.drawable.ic_error)
                } else {
                    Glide.with(mCtx)
                            .load(imageUrl)
                            .error(R.drawable.ic_error)
                            .into(urlImage)
                }
                urlTitle.text = title
                urlDesc.text = desc
                urlProgress.visible(false)
                urlBits.visible(true)
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

    interface Interaction {

        fun onItemSelected(position: Int, item: ProfilenewsRecycler)

        fun restoreListPosition()
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