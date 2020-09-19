package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.balysv.materialripple.MaterialRippleLayout
import com.bumptech.glide.Glide
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.models.MessageUserListRecycler
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.ChatActivity
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.yarolegovich.lovelydialog.LovelyCustomDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MessageUserListAdapter(private val users: List<MessageUserListRecycler>, private val context: Context, private val mAdapterCallback: AdapterCallback) : RecyclerView.Adapter<MessageUserListAdapter.UserListHolder>() {
    private var deviceusername: String? = null
    private var deviceuserid: String? = null

    interface AdapterCallback {
        fun onMethodCallback()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_message_user_list, parent, false)
        deviceusername = SharedPrefManager.getInstance(context)!!.username
        deviceuserid = SharedPrefManager.getInstance(context)!!.userID
        return UserListHolder(view)
    }

    override fun onBindViewHolder(holder: UserListHolder, position: Int) {
        val user = users[position]
        holder.nickname.text = user.nickname
        holder.username.text = String.format("@%s", user.username)
        holder.userDesc.text = user.desc
        holder.userListLayout.setOnClickListener { context.startActivity(Intent(context, FragmentContainer::class.java).putExtra("user_to_id", user.user_id)) }
        if (user.online == "yes") {
            holder.online.visibility = View.VISIBLE
        } else {
            holder.online.visibility = View.GONE
        }
        if (user.verified == "yes") {
            holder.verified.visibility = View.VISIBLE
        } else {
            holder.verified.visibility = View.GONE
        }
        if (user.owner == deviceusername) {
            holder.removeUserBtn.visibility = View.VISIBLE
            holder.manageUser.visibility = View.VISIBLE
            holder.manageUser.setOnClickListener { manageUser(user.username, user.position, user.groupID) }
            holder.removeUserBtn.setOnClickListener { removeUser(user.username, user.position, user.groupID) }
        } else {
            holder.manageUser.visibility = View.GONE
            if (user.canRemove == "yes") {
                if (user.position != "owner" && user.position != "admin") {
                    holder.removeUserBtn.visibility = View.VISIBLE
                    holder.removeUserBtn.setOnClickListener { removeUser(user.username, user.position, user.groupID) }
                } else {
                    holder.removeUserBtn.visibility = View.GONE
                }
            } else {
                holder.removeUserBtn.visibility = View.GONE
            }
        }
        if (user.position == "owner") {
            holder.removeUserBtn.visibility = View.GONE
            holder.manageUser.visibility = View.GONE
        }
        val profilePic = user.profile_pic.substring(0, user.profile_pic.length - 4) + "_r.JPG"
        Glide.with(context)
                .load(Constants.BASE_URL + profilePic)
                .into(holder.profilePic)
    }

    private fun removeUser(username: String, position: String, group_id: String) {
        LovelyStandardDialog(context, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_action_remove)
                .setTitle("Remove @$username?")
                .setPositiveButton(R.string.yes) {
                    val stringRequest: StringRequest = object : StringRequest(Method.POST, REMOVE_USER, Response.Listener { response: String? ->
                        try {
                            val res = JSONObject(response!!)
                            val error = res.getString("error")
                            if (error == "false") {
                                val newPositionResult = res.getString("position")
                                Toast.makeText(context, newPositionResult, Toast.LENGTH_SHORT).show()
                                mAdapterCallback.onMethodCallback()
                            } else {
                                Toast.makeText(context, "Error, please try again later... #1", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { Toast.makeText(context, "Error, please try again later... #2", Toast.LENGTH_LONG).show() }) {
                        override fun getParams(): MutableMap<String, String?> {
                            val params: MutableMap<String, String?> = HashMap()
                            params["deviceuser"] = deviceusername
                            params["deviceuserid"] = deviceuserid
                            params["group_id"] = group_id
                            params["current_position"] = position
                            params["username"] = username
                            return params
                        }
                    }
                    (context as ChatActivity).addToRequestQueue(stringRequest)
                }
                .setNegativeButton(R.string.no, null)
                .show()
    }

    private fun manageUser(username: String, position: String, group_id: String) {
        if (username != "" && position != "") {
            val li = LayoutInflater.from(context)
            val dialogView = li.inflate(R.layout.dialog_message_manage_user, null)
            val manageSaveBtn = dialogView.findViewById<Button>(R.id.manageSaveBtn)
            val manageSaveProgress = dialogView.findViewById<ProgressBar>(R.id.manageSaveProgress)
            val adminBox = dialogView.findViewById<CheckBox>(R.id.adminBox)
            val userBox = dialogView.findViewById<CheckBox>(R.id.userBox)
            if (position == "admin") {
                adminBox.isChecked = true
            } else if (position == "user") {
                userBox.isChecked = true
            }
            adminBox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
                if (adminBox.isChecked) {
                    userBox.isChecked = false
                }
            }
            userBox.setOnCheckedChangeListener { _: CompoundButton?, _: Boolean ->
                if (userBox.isChecked) {
                    adminBox.isChecked = false
                }
            }
            val dialog = LovelyCustomDialog(context)
            dialog.setView(dialogView)
                    .setTopColorRes(R.color.green)
                    .setTitle(R.string.messages_manage_user)
                    .setIcon(R.drawable.ic_person_black_24dp)
                    .setListener(R.id.manageSaveBtn) {
                        manageSaveBtn.visibility = View.GONE
                        manageSaveProgress.visibility = View.VISIBLE
                        val newPermission: String
                        if (userBox.isChecked && !adminBox.isChecked) {
                            newPermission = "user"
                        } else if (!userBox.isChecked && adminBox.isChecked) {
                            newPermission = "admin"
                        } else {
                            manageSaveBtn.visibility = View.VISIBLE
                            manageSaveProgress.visibility = View.GONE
                            Toast.makeText(context, "Please check 1 box!", Toast.LENGTH_SHORT).show()
                            return@setListener
                        }
                        val stringRequest: StringRequest = object : StringRequest(Method.POST, MANAGE_USER, Response.Listener { response: String? ->
                            try {
                                val res = JSONObject(response!!)
                                val error = res.getString("error")
                                if (error == "false") {
                                    val newPositionResult = res.getString("position")
                                    Toast.makeText(context, newPositionResult, Toast.LENGTH_SHORT).show()
                                    dialog.dismiss()
                                    mAdapterCallback.onMethodCallback()
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }, Response.ErrorListener {
                            manageSaveBtn.visibility = View.VISIBLE
                            manageSaveProgress.visibility = View.GONE
                            Toast.makeText(context, "Error, please try again later...", Toast.LENGTH_LONG).show()
                        }) {
                            override fun getParams(): MutableMap<String, String?> {
                                val params: MutableMap<String, String?> = HashMap()
                                params["deviceuser"] = deviceusername
                                params["deviceuserid"] = deviceuserid
                                params["group_id"] = group_id
                                params["current_position"] = position
                                params["new_position"] = newPermission
                                params["username"] = username
                                return params
                            }
                        }
                        (context as ChatActivity).addToRequestQueue(stringRequest)
                        dialog.dismiss()
                    }
                    .show()
        } else {
            Toast.makeText(context, "An error occured! (#3)", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    class UserListHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.username)
        var nickname: TextView = itemView.findViewById(R.id.nickname)
        var userDesc: TextView = itemView.findViewById(R.id.user_desc)
        var profilePic: CircleImageView = itemView.findViewById(R.id.profile_image)
        var online: CircleImageView = itemView.findViewById(R.id.online)
        var verified: CircleImageView = itemView.findViewById(R.id.verified)
        var userListLayout: MaterialRippleLayout = itemView.findViewById(R.id.userListLayout)
        var removeUserBtn: Button = itemView.findViewById(R.id.remove_user_btn)
        var manageUser: Button = itemView.findViewById(R.id.manage_user)

    }

    companion object {
        private const val MANAGE_USER = Constants.ROOT_URL + "messages.php/manage_user"
        private const val REMOVE_USER = Constants.ROOT_URL + "messages.php/remove_user"
    }
}