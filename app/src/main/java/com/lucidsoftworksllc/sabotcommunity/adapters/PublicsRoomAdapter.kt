package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.db.PublicsEntity
import com.lucidsoftworksllc.sabotcommunity.fragments.FragmentPublicsCat
import com.lucidsoftworksllc.sabotcommunity.models.PublicsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.BaseViewHolder
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class PublicsRoomAdapter(private val mCtx: Context, private val games: MutableList<PublicsEntity>) : RecyclerView.Adapter<BaseViewHolder>() {

    //class GameViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.recycler_publics, parent, false))
            VIEW_TYPE_LOADING -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false))
            else -> ProgressHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false))
        }
    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    override fun getItemCount() = games.size

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == games.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.publicsImageView)
        private var textViewTitle: TextView = itemView.findViewById(R.id.textViewTitle)
        private var textViewNumRatings: TextView = itemView.findViewById(R.id.reviewCount)
        private var postCount: TextView = itemView.findViewById(R.id.postCount)
        private var publicsTopicLayout: LinearLayout = itemView.findViewById(R.id.recyclerPublicsLayout)
        var publicsActionBtn: Button = itemView.findViewById(R.id.publicsActionBtn)
        var publicsActionBtnFollowed: Button = itemView.findViewById(R.id.publicsActionBtnFollowed)
        private var profileRating: SimpleRatingBar = itemView.findViewById(R.id.profileRating)
        var followProgress: ProgressBar = itemView.findViewById(R.id.followProgress)
        override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
            val publics = games[position]
            if (publics.followed == "yes") {
                publicsActionBtn.visibility = View.GONE
                publicsActionBtnFollowed.visibility = View.VISIBLE
            } else {
                publicsActionBtnFollowed.visibility = View.GONE
                publicsActionBtn.visibility = View.VISIBLE
            }
            if (publics.avgrating.isNotEmpty() && publics.avgrating != "null") {
                profileRating.visibility = View.VISIBLE
                profileRating.rating = publics.avgrating.toFloat()
            } else {
                profileRating.visibility = View.INVISIBLE
            }
            publicsActionBtn.setOnClickListener {
                publicsActionBtn.isEnabled = false
                publicsActionBtn.visibility = View.GONE
                val buttonAppear: Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)
                publicsActionBtnFollowed.visibility = View.VISIBLE
                publicsActionBtnFollowed.startAnimation(buttonAppear)
                publics.followed = "yes"
                Handler().postDelayed({ publicsActionBtnFollowed.isEnabled = true }, 3500)

                val userID = SharedPrefManager.getInstance(mCtx)!!.userID
                val username = SharedPrefManager.getInstance(mCtx)!!.username
                val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") != "false") {
                            publicsActionBtn.isEnabled = true
                            publicsActionBtn.visibility = View.VISIBLE
                            publicsActionBtnFollowed.visibility = View.GONE
                            publics.followed = "no"
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { Toast.makeText(mCtx, "Could not follow, please try again later...", Toast.LENGTH_LONG).show() }) {
                    override fun getParams(): MutableMap<String, String?> {
                        val params: MutableMap<String, String?> = HashMap()
                        params["game_id"] = publics.id.toString()
                        params["game_name"] = publics.title
                        params["method"] = "follow"
                        params["user_id"] = userID
                        params["username"] = username
                        return params
                    }
                }
                (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
            }
            publicsActionBtnFollowed.setOnClickListener {
                LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.white)
                        .setIcon(R.drawable.ic_check)
                        .setTitle(R.string.game_unfollow)
                        .setMessage(mCtx.resources.getString(R.string.unfollow) + " " + publics.title + "?")
                        .setPositiveButton(android.R.string.ok) {
                            publicsActionBtnFollowed.isEnabled = false
                            publicsActionBtnFollowed.visibility = View.GONE
                            val buttonAppear: Animation = AnimationUtils.loadAnimation(mCtx, R.anim.expand_in)
                            publicsActionBtn.visibility = View.VISIBLE
                            publicsActionBtn.startAnimation(buttonAppear)
                            publics.followed = "no"
                            Handler().postDelayed({ publicsActionBtn.isEnabled = true }, 3500)

                            val userID = SharedPrefManager.getInstance(mCtx)!!.userID
                            val username = SharedPrefManager.getInstance(mCtx)!!.username
                            val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener { response: String? ->
                                try {
                                    val jsonObject = JSONObject(response!!)
                                    if (jsonObject.getString("error") != "false") {
                                        publicsActionBtnFollowed.isEnabled = true
                                        publicsActionBtnFollowed.visibility = View.VISIBLE
                                        publicsActionBtn.visibility = View.GONE
                                        publics.followed = "yes"
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }, Response.ErrorListener { Toast.makeText(mCtx, "Could not unfollow, please try again later...", Toast.LENGTH_LONG).show() }) {
                                override fun getParams(): MutableMap<String, String?> {
                                    val params: MutableMap<String, String?> = HashMap()
                                    params["game_id"] = publics.id.toString()
                                    params["game_name"] = publics.title
                                    params["method"] = "unfollow"
                                    params["user_id"] = userID
                                    params["username"] = username
                                    return params
                                }
                            }
                            (mCtx as FragmentContainer).addToRequestQueue(stringRequest)
                        }
                        .setNegativeButton(android.R.string.no) {
                            followProgress.visibility = View.GONE
                            publicsActionBtnFollowed.visibility = View.VISIBLE
                        }
                        .show()
            }
            postCount.text = publics.postcount.toString()
            textViewTitle.text = publics.title
            textViewNumRatings.text = publics.numratings.toString()
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + publics.image)
                    .into(imageView)
            publicsTopicLayout.setOnClickListener {
                if (mCtx is FragmentContainer) {
                    val ldf = FragmentPublicsCat()
                    val args = Bundle()
                    args.putString("PublicsId", publics.id.toString())
                    ldf.arguments = args
                    (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
            }
        }

    }

    fun addItems(publicsitems: List<PublicsEntity>) {
        games.addAll(publicsitems)
        notifyDataSetChanged()
    }

    fun clear() {
        games.clear()
        notifyDataSetChanged()
    }

    fun addLoading() {
        println("Added loading! Adapter")
        isLoaderVisible = true
        games.add(PublicsEntity(0, "","","",0,"","",0,"",0,"", ""))
        notifyItemInserted(games.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        if (games.size != 0) {
            val position = games.size - 1
            games.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun getItem(position: Int): PublicsEntity {
        return games[position]
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val FOLLOW_GAME_URL = Constants.ROOT_URL + "publicscat_follow_api.php"
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

}