package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.iarcuschin.simpleratingbar.SimpleRatingBar
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class PublicsAdapter(private val mCtx: Context, private val publicsList: MutableList<PublicsRecycler>?) : RecyclerView.Adapter<BaseViewHolder>() {
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

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.onBind(position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoaderVisible) {
            if (position == publicsList!!.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return publicsList?.size ?: 0
    }

    fun addItems(publicsitems: List<PublicsRecycler>?) {
        publicsList!!.addAll(publicsitems!!)
        notifyDataSetChanged()
    }

    fun addLoading() {
        isLoaderVisible = true
        publicsList!!.add(PublicsRecycler())
        notifyItemInserted(publicsList.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        if (publicsList!!.size != 0) {
            val position = publicsList.size - 1
            val item = getItem(position)
            publicsList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        publicsList!!.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): PublicsRecycler {
        return publicsList!![position]
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
            val publics = publicsList!![position]
            if (publics.followed == "yes") {
                publicsActionBtn.visibility = View.GONE
                publicsActionBtnFollowed.visibility = View.VISIBLE
            } else {
                publicsActionBtnFollowed.visibility = View.GONE
                publicsActionBtn.visibility = View.VISIBLE
            }
            if (publics.avgrating != null && publics.avgrating!!.isNotEmpty() && publics.avgrating != "null") {
                profileRating.visibility = View.VISIBLE
                profileRating.rating = publics.avgrating!!.toFloat()
            } else {
                profileRating.visibility = View.INVISIBLE
            }
            publicsActionBtn.setOnClickListener {
                publicsActionBtn.visibility = View.GONE
                followProgress.visibility = View.VISIBLE
                val userID = SharedPrefManager.getInstance(mCtx)!!.userID
                val username = SharedPrefManager.getInstance(mCtx)!!.username
                val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener { response: String? ->
                    try {
                        val jsonObject = JSONObject(response!!)
                        if (jsonObject.getString("error") == "false") {
                            publicsActionBtnFollowed.visibility = View.VISIBLE
                            followProgress.visibility = View.GONE
                            publics.followed = "yes"
                        } else {
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener { Toast.makeText(mCtx, "Could not follow, please try again later...", Toast.LENGTH_LONG).show() }) {
                    override fun getParams(): MutableMap<String, String?> {
                        val params: MutableMap<String, String?> = HashMap()
                        params["game_id"] = publics.id
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
                publicsActionBtnFollowed.visibility = View.GONE
                followProgress.visibility = View.VISIBLE
                LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                        .setTopColorRes(R.color.green)
                        .setButtonsColorRes(R.color.white)
                        .setIcon(R.drawable.ic_check)
                        .setTitle(R.string.game_unfollow)
                        .setMessage(mCtx.resources.getString(R.string.unfollow) + " " + publics.title + "?")
                        .setPositiveButton(android.R.string.ok) {
                            val userID = SharedPrefManager.getInstance(mCtx)!!.userID
                            val username = SharedPrefManager.getInstance(mCtx)!!.username
                            val stringRequest: StringRequest = object : StringRequest(Method.POST, FOLLOW_GAME_URL, Response.Listener { response: String? ->
                                try {
                                    val jsonObject = JSONObject(response!!)
                                    if (jsonObject.getString("error") == "false") {
                                        publicsActionBtn.visibility = View.VISIBLE
                                        followProgress.visibility = View.GONE
                                        publics.followed = "no"
                                    } else {
                                        Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }, Response.ErrorListener { Toast.makeText(mCtx, "Could not unfollow, please try again later...", Toast.LENGTH_LONG).show() }) {
                                override fun getParams(): MutableMap<String, String?> {
                                    val params: MutableMap<String, String?> = HashMap()
                                    params["game_id"] = publics.id
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
            postCount.text = publics.postcount
            textViewTitle.text = publics.title
            textViewNumRatings.text = publics.numratings
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + publics.image)
                    .into(imageView)
            publicsTopicLayout.setOnClickListener {
                if (mCtx is FragmentContainer) {
                    val ldf = FragmentPublicsCat()
                    val args = Bundle()
                    args.putString("PublicsId", publics.id)
                    ldf.arguments = args
                    (mCtx as FragmentActivity).supportFragmentManager.beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
            }
        }

    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val FOLLOW_GAME_URL = Constants.ROOT_URL + "publicscat_follow_api.php"
    }
}