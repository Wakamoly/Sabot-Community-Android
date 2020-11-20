package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.fragments.*
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.active_label.SocialTextView
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewHolder
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.add_layout_profilenewsimage.view.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class ProfilenewsAdapter(private val mCtx: Context,
                         private val interaction: Interaction? = null) : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    private val profilenewsList: MutableList<ProfilenewsRecycler> = ArrayList()
    private val userID = mCtx.deviceUserID
    private val username = mCtx.deviceUsername
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
        return profilenewsList.size
    }

    fun clear() {
        profilenewsList.clear()
        notifyDataSetChanged()
    }

    fun addItems(items: List<ProfilenewsRecycler>) {
        profilenewsList.addAll(items)
        notifyItemRangeInserted(profilenewsList.size - 1, items.size)
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
            //val item = getItem(position)
            profilenewsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun removeItem(position: Int){
        if (profilenewsList.isNotEmpty()){
            profilenewsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): ProfilenewsRecycler {
        return profilenewsList[position]
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private var verified: CircleImageView = itemView.findViewById(R.id.verified)
        private var online: CircleImageView = itemView.findViewById(R.id.online)
        private var contentLayout: MaterialRippleLayout = itemView.findViewById(R.id.contentLayout)
        private var postContext: RelativeLayout = itemView.findViewById(R.id.postContext)
        private var likeProgress: ProgressBar = itemView.findViewById(R.id.likeProgress)
        private var imageInflater: View? = null
        private var imageViewProfilenewsPic: ImageView = itemView.findViewById(R.id.imageViewProfilenewsPic)
        private var notiType: ImageView = itemView.findViewById(R.id.platformType)
        private var likeView: ImageView = itemView.findViewById(R.id.like)
        private var likedView: ImageView = itemView.findViewById(R.id.liked)
        private var tvEdited: TextView = itemView.findViewById(R.id.tvEdited)
        private var textViewBody: SocialTextView = itemView.findViewById(R.id.textViewBody)
        private var textviewaddedBy: TextView = itemView.findViewById(R.id.textViewProfileName)
        private var textviewdateAdded: TextView = itemView.findViewById(R.id.profileCommentsDateTime_top)
        private var textviewuserTo: TextView = itemView.findViewById(R.id.textViewToUserName)
        private var textViewLikes: TextView = itemView.findViewById(R.id.textViewNumLikes)
        private var postusernameTop: TextView = itemView.findViewById(R.id.postUsername_top)
        private var textViewNumComments: TextView = itemView.findViewById(R.id.textViewNumComments)
        private var textViewComments: TextView = itemView.findViewById(R.id.textViewComments)
        private var textViewLikesText: TextView = itemView.findViewById(R.id.textViewLikes)
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
                        // TODO: 11/12/20 Not implemented
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
                                                    removeItem(position)
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
                                                params["username"] = this@ProfilenewsAdapter.username
                                                params["userid"] = this@ProfilenewsAdapter.userID.toString()
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
                true
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
                if (imageInflater == null){
                    imageInflater = LayoutInflater.from(mCtx).inflate(R.layout.add_layout_profilenewsimage, null, false)
                    postContext.addView(imageInflater)
                    imageInflater?.below(textViewBody)
                }
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + profilenews.image).override(1000)
                        .into(imageInflater!!.profileNewsImage)
            }else if (imageInflater != null) {
                postContext.removeView(imageInflater)
                imageInflater = null
            }

            when (profilenews.likedbyuseryes){
                "yes" -> {
                    likeView.visible(false)
                    likedView.visible(true)
                }
                else -> {
                    likeView.visible(true)
                    likedView.visible(false)
                }
            }

            likeView.setOnClickListener {
                likeView.isEnabled = false
                likeView.visible(false)
                val likeAppear:Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)

                val newValue = (textViewLikes.text.toString().toInt() + 1).toString()
                textViewLikes.text = newValue
                likeProgress.visible(false)
                likedView.visible(true)
                likedView.startAnimation(likeAppear)
                likedView.isEnabled = false
                Handler(Looper.getMainLooper()).postDelayed({ likedView.isEnabled = true }, 3500)
                val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                    val obj: JSONObject
                    try {
                        obj = JSONObject(response!!)
                        if (obj.getBoolean("error")) {
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                            likeView.visible(true)
                            likeView.isEnabled = false
                            Handler(Looper.getMainLooper()).postDelayed({ likeView.isEnabled = true }, 3000)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(mCtx, "Could not like, please try again later...", Toast.LENGTH_LONG).show()
                    likeProgress.visible(false)
                    likeView.visible(true)
                    likeView.isEnabled = false
                    Handler(Looper.getMainLooper()).postDelayed({ likeView.isEnabled = true }, 3000)
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["post_id"] = profilenews.id.toString()
                        params["method"] = "like"
                        params["user_to"] = profilenews.username
                        params["user_id"] = this@ProfilenewsAdapter.userID.toString()
                        params["username"] = this@ProfilenewsAdapter.username.toString()
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
                Handler(Looper.getMainLooper()).postDelayed({ likeView.isEnabled = true }, 3500)
                val stringRequest: StringRequest = object : StringRequest(Method.POST, LIKE_URL, Response.Listener { response: String? ->
                    val obj: JSONObject
                    try {
                        obj = JSONObject(response!!)
                        if (!obj.getBoolean("error")) {
//                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show()
                            likeProgress.visible(false)
                            likedView.visible(true)
                            likedView.isEnabled = false
                            Handler(Looper.getMainLooper()).postDelayed({ likedView.isEnabled = true }, 3000)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(mCtx, "Could not remove like, please try again later...", Toast.LENGTH_LONG).show()
                    likeProgress.visible(false)
                    likedView.visible(true)
                    likedView.isEnabled = false
                    Handler(Looper.getMainLooper()).postDelayed({ likeView.isEnabled = true }, 3000)
                }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["post_id"] = profilenews.id.toString()
                        params["method"] = "unlike"
                        params["user_to"] = profilenews.username
                        params["user_id"] = this@ProfilenewsAdapter.userID.toString()
                        params["username"] = this@ProfilenewsAdapter.username.toString()
                        return params
                    }
                }
                (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
            }
            when (profilenews.online){
                "yes" -> online.visible(true)
                else -> online.visible(false)
            }
            when (profilenews.verified){
                "yes" -> verified.visible(true)
                else -> verified.visible(false)
            }

            textViewBody.setClicks(mCtx)

            // TODO: 11/11/20 remove url preview, disable hyperlink clicks and enable both on profile post frag
            //urlPreview.visible(false)

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