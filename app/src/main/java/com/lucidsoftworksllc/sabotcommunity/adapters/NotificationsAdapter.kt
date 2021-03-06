package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationDataModel
import com.lucidsoftworksllc.sabotcommunity.fragments.ClanFragment
import com.lucidsoftworksllc.sabotcommunity.fragments.FragmentProfile
import com.lucidsoftworksllc.sabotcommunity.fragments.ProfilePostFragment
import com.lucidsoftworksllc.sabotcommunity.fragments.PublicsTopicFragment
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.lucidsoftworksllc.sabotcommunity.others.active_label.SocialTextView
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseViewHolder
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class NotificationsAdapter(private val context: Context) : RecyclerView.Adapter<BaseViewHolder>() {
    private var isLoaderVisible = false
    private val userID = context.deviceUserID
    private val username = context.deviceUsername
    private val notifications: MutableList<NotificationCacheEntity> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            VIEW_TYPE_NORMAL -> ViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.recycler_notifications, parent, false))
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
            if (position == notifications.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        } else {
            VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun addItems(items: List<NotificationCacheEntity>) {
        if (notifications.isEmpty()){
            notifications.addAll(items)
        }else{
            for (item in items.indices){
                var addNew = true
                for (noti in notifications.indices){
                    if (items[item].id == notifications[noti].id){
                        if (items[item].deleted == "yes" || notifications[noti].deleted == "yes"){
                            notifications.removeAt(noti)
                        }else{
                            notifications[noti] = items[item]
                        }
                        addNew = false
                        break
                    }
                }
                if (addNew){
                    notifications.add(0, items[item])
                }
            }
        }
        notifyDataSetChanged()
    }

    fun addItemsToTop(items: List<NotificationCacheEntity>) {
        notifications.addAll(0, items)
        notifyItemRangeInserted(0, items.size)
    }

    fun addLoading() {
        isLoaderVisible = true
        notifications.add(NotificationCacheEntity(0, null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString(), null.toString()))
        notifyItemInserted(notifications.size - 1)
    }

    fun removeLoading() {
        isLoaderVisible = false
        if (notifications.isNotEmpty()) {
            val position = notifications.size - 1
            //val item = getItem(position)
            notifications.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position >= 0) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.slide_in)
            viewToAnimate.startAnimation(animation)
        }
    }

    fun size(): Int {
        return notifications.size
    }

    fun clear() {
        notifications.clear()
        notifyDataSetChanged()
    }

    private fun getItem(position: Int): NotificationCacheEntity {
        return notifications[position]
    }

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private var nickname: TextView = itemView.findViewById(R.id.notificationsNickname)
        private var body: SocialTextView = itemView.findViewById(R.id.notificationsBody)
        private var datetime: TextView = itemView.findViewById(R.id.notificationsDateTime)
        private var profilePicView: ImageView = itemView.findViewById(R.id.notificationsImageView)
        private var online: CircleImageView = itemView.findViewById(R.id.online)
        private var notiType: CircleImageView = itemView.findViewById(R.id.notiType)
        private var verified: CircleImageView = itemView.findViewById(R.id.verified)
        private var notiLayout: MaterialRippleLayout = itemView.findViewById(R.id.notiLayout)
        override fun clear() {}
        override fun onBind(position: Int) {
            super.onBind(position)
            val notification = notifications[position]

            /*String wordToFind = "@"+notification.getMessage().lastIndexOf("@")+1;
        Pattern word = Pattern.compile(wordToFind);
        Matcher match = word.matcher(notification.getMessage());
        Spannable wordToSpan = new SpannableString(notification.getMessage());
        while (match.find()) {
            wordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.light_blue)), match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.body.setText(wordToSpan);

        String [] bodybits = notification.getMessage().split("\\s+");
        // get every part
        for( String item : bodybits ) {
            if(item.contains("@")) {
                String itemRevised = " <font color='"
                        + ContextCompat.getColor(context,R.color.light_blue) + "'>" + item
                        + "</font>";
                item.replaceAll(item,itemRevised);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.body.setText(Html.fromHtml(bodybits, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.body.setText(Html.fromHtml(bodybits));
        }*/

            nickname.text = notification.nickname
            body.text = notification.message
            body.setClicks(context)
            datetime.text = getTimeAgo(notification.datetime, context)
            online.visible(isUserOnline(notification.last_online))
            if (notification.verified == "yes") {
                verified.visibility = View.VISIBLE
            } else {
                verified.visibility = View.GONE
            }
            if (notification.opened == "yes") {
                notiLayout.setBackgroundColor(Color.parseColor("#111111"))
            } else {
                notiLayout.setBackgroundColor(Color.parseColor("#222222"))
            }
            when (notification.type) {
                "post_comment", "profile_post" -> notiType.setImageResource(R.drawable.notify_comment)
                "comment_like", "like" -> notiType.setImageResource(R.drawable.notify_like)
                "new_follower", "new_connection_request", "publics_topic_submitted" -> notiType.setImageResource(R.drawable.notify_follower)
                "publics_comment", "comment" -> notiType.setImageResource(R.drawable.notify_reply)
            }
            val profilePic = notification.profile_pic.substring(0, notification.profile_pic.length - 4) + "_r.JPG"
            Glide.with(context)
                    .load(Constants.BASE_URL + profilePic)
                    .into(profilePicView)
            notiLayout.setOnClickListener {
                if (notification.opened != "yes") {
                    //TODO("Add opened to internal db")

                    val stringRequest: StringRequest = object : StringRequest(Method.POST, SET_READ, Response.Listener { response: String? ->
                        try {
                            val obj = JSONObject(response!!)
                            if (obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { Toast.makeText(context, "Could not set notification as read, please try again later...", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): Map<String, String> {
                            val params: MutableMap<String, String> = HashMap()
                            params["username"] = notification.user_to
                            params["id"] = notification.id.toString()
                            return params
                        }
                    }
                    (context as FragmentContainer).addToRequestQueue(stringRequest)
                }
                if (notification.link.contains("post.php?id=")) {
                    val linkID = notification.link.replace("post.php?id=", "")
                    if (context is FragmentContainer) {
                        val ldf = ProfilePostFragment()
                        val args = Bundle()
                        args.putString("id", linkID)
                        ldf.arguments = args
                        (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                } else if (notification.link.contains("publics_topic.php?id=")) {
                    val linkID = notification.link.replace("publics_topic.php?id=", "")
                    if (context is FragmentContainer) {
                        val ldf = PublicsTopicFragment()
                        val args = Bundle()
                        args.putString("PublicsId", linkID)
                        ldf.arguments = args
                        (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                } else if (notification.link.contains("clan=")) {
                    val linkID = notification.link.replace("clan=", "")
                    if (context is FragmentContainer) {
                        val ldf = ClanFragment()
                        val args = Bundle()
                        args.putString("ClanId", linkID)
                        ldf.arguments = args
                        (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                } else if (notification.link.contains("user=")) {
                    val linkID = notification.user_id
                    if (context is FragmentContainer) {
                        val ldf = FragmentProfile()
                        val args = Bundle()
                        args.putString("UserId", linkID)
                        ldf.arguments = args
                        (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                } else if (notification.link.contains("ptopic=")) {
                    val linkID = notification.link.replace("ptopic=", "")
                    if (context is FragmentContainer) {
                        val ldf = PublicsTopicFragment()
                        val args = Bundle()
                        args.putString("PublicsId", linkID)
                        ldf.arguments = args
                        (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                    }
                } else if (notification.link.contains("review")) {
                    if (context is FragmentContainer) {
                        val ldf = FragmentProfile()
                        val args = Bundle()
                        args.putString("UserId", userID)
                        ldf.arguments = args
                        (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).commit()
                    }
                }
            }
            CoroutineScope(Dispatchers.IO).launch { SabotDatabase.invoke(context).getNotificationsDao().setOpened(notification.id) }
            setAnimation(itemView, position)
        }
    }

    class ProgressHolder internal constructor(itemView: View?) : BaseViewHolder(itemView) {
        override fun clear() {}
    }

    companion object {
        private const val VIEW_TYPE_LOADING = 0
        private const val VIEW_TYPE_NORMAL = 1
        private const val SET_READ = Constants.ROOT_URL + "set_noti_read.php"
    }
}