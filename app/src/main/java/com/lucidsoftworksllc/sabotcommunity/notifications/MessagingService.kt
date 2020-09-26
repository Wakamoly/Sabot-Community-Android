package com.lucidsoftworksllc.sabotcommunity.notifications

import android.annotation.SuppressLint
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.MainActivity
import org.json.JSONArray
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val data = remoteMessage.data
            try {
                handleData(data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        } else if (remoteMessage.notification != null) {
            handleNotification(remoteMessage.notification)
        }
    }

    private fun handleNotification(RemoteMsgNotification: RemoteMessage.Notification?) {
        val message = RemoteMsgNotification!!.body
        val title = RemoteMsgNotification.title
        val notificationVO = NotificationVO()
        notificationVO.title = title
        notificationVO.message = message
        val resultIntent = Intent(applicationContext, MainActivity::class.java)
        val notificationUtils = NotificationsUtils(applicationContext)
        notificationUtils.displayNotification(notificationVO, resultIntent)
    }

    @Throws(JSONException::class)
    private fun handleData(data: Map<String, String>) {
        val title = data[TITLE]
        val message = data[MESSAGE]
        val iconUrl = data[IMAGE]
        val action = data[ACTION]
        val actionDestination = data[ACTION_DESTINATION]
        val actionLink = data[ACTION_LINK]
        val notificationVO = NotificationVO()
        notificationVO.title = title
        notificationVO.message = message
        notificationVO.iconUrl = iconUrl
        notificationVO.action = action
        notificationVO.actionDestination = actionDestination
        notificationVO.actionLink = actionLink
        if (!title!!.contains("players needed!")) {
            val resultIntent = Intent(applicationContext, MainActivity::class.java)
            val notificationUtils = NotificationsUtils(applicationContext)
            notificationUtils.displayNotification(notificationVO, resultIntent)
        } else {
            val lastNoti = SharedPrefManager.getInstance(applicationContext)!!.lastNoti
            val currentTime = Calendar.getInstance().time.toString()
            if (canSendNoti(lastNoti!!, currentTime, message)) {
                val resultIntent = Intent(applicationContext, MainActivity::class.java)
                val notificationUtils = NotificationsUtils(applicationContext)
                notificationUtils.displayNotification(notificationVO, resultIntent)
                SharedPrefManager.getInstance(applicationContext)!!.lastNoti = currentTime
            }
        }
    }

    @Throws(JSONException::class)
    private fun canSendNoti(lastnoti: String, now: String, message: String?): Boolean {
        val notiPlatformsData = JSONArray(SharedPrefManager.getInstance(this)!!.notiPlatforms)
        val objectName = arrayOfNulls<String>(notiPlatformsData.length())
        var notiPlatformContains = false
        for (i in 0 until notiPlatformsData.length()) {
            objectName[i] = notiPlatformsData.getString(i)
            if (message!!.contains("on " + objectName[i])) {
                notiPlatformContains = true
                break
            }
        }
        if (notiPlatformContains) {
            val notiFrequency = SharedPrefManager.getInstance(applicationContext)!!.notificationFrequency
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                    Locale.ENGLISH)
            try {
                val adjustedTime = sdf.parse(lastnoti)
                calendar.time = adjustedTime!!
                when (notiFrequency) {
                    "24 Hours" -> calendar.add(Calendar.HOUR, 24)
                    "12 Hours" -> calendar.add(Calendar.HOUR, 12)
                    "8 Hours" -> calendar.add(Calendar.HOUR, 8)
                    "4 Hours" -> calendar.add(Calendar.HOUR, 4)
                    "2 Hours" -> calendar.add(Calendar.HOUR, 2)
                    "1 Hour" -> calendar.add(Calendar.HOUR, 1)
                    "Always" -> calendar.add(Calendar.HOUR, -24)
                    "Never" -> calendar.add(Calendar.YEAR, 100)
                }
                val date2 = sdf.parse(now)
                val date1 = calendar.time
                return date1.before(date2)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
        }
        return false
    }

    companion object {
        private const val TAG = "MsgingService"
        private const val TITLE = "title"
        private const val EMPTY = ""
        private const val MESSAGE = "message"
        private const val IMAGE = "image"
        private const val ACTION = "action"
        private const val DATA = "data"
        private const val ACTION_DESTINATION = "action_destination"
        private const val ACTION_LINK = "link"
    }
}