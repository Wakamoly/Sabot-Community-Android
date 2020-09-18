package com.lucidsoftworksllc.sabotcommunity

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.text.Html
import androidx.core.app.NotificationCompat
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class NotificationsUtils(private val mContext: Context) {
    private var activityMap: MutableMap<String, Class<*>> = HashMap()

    /**
     * Displays notification based on parameters
     *
     * @param notificationVO
     * @param resultIntent
     */
    fun displayNotification(notificationVO: NotificationVO, resultIntent: Intent) {
        run {
            val message = notificationVO.message
            val title = notificationVO.title
            val iconUrl = notificationVO.iconUrl
            val action = notificationVO.action
            val destination = notificationVO.actionDestination
            val link = notificationVO.actionLink

            /*if (link!=null){
                Log.e(TAG, "displayNotification: "+link);
            }*/
            var iconBitMap: Bitmap? = null
            if (iconUrl != null) {
                try {
                    iconBitMap = if (iconUrl.contains("/profile_pics/")) {
                        getBitmapFromURL(Constants.BASE_URL + iconUrl.substring(0, iconUrl.length - 4) + "_r.JPG")
                    } else {
                        getBitmapFromURL(Constants.BASE_URL + iconUrl)
                    }
                } catch (ignored: Exception) {
                }
            }
            val icon = R.drawable.ic_launcher_trans
            if (destination == "ChatActivity") {
                NOTIFICATION_ID = 1
                if (link != null) {
                    when {
                        link.contains("user=") -> {
                            val linkfinal = link.replace("user=", "")
                            var result = 0
                            for (element in linkfinal) {
                                result += element.toInt()
                            }
                            NOTIFICATION_ID = result
                            //Log.e(TAG, "displayNotification: "+NOTIFICATION_ID);
                        }
                        link.contains("group=") -> {
                            NOTIFICATION_ID = link.replace("group=", "").toInt()
                        }
                        link.contains("requests") -> {
                            NOTIFICATION_ID = 10
                        }
                    }
                }
            } else if (destination == "FragmentContainer") {
                NOTIFICATION_ID = 2
                if (link != null) {
                    when {
                        link.contains("post.php?id=") -> {
                            NOTIFICATION_ID *= link.replace("post.php?id=", "").toInt()
                        }
                        link.contains("publics_topic.php?id=") -> {
                            NOTIFICATION_ID *= link.replace("publics_topic.php?id=", "").toInt()
                        }
                        link.contains("clan=") -> {
                            NOTIFICATION_ID *= link.replace("clan=", "").toInt()
                        }
                        link.contains("pchatroom=") -> {
                            NOTIFICATION_ID = 20 * link.replace("pchatroom=", "").toInt()
                        }
                        link.contains("user=") -> {
                            val linkfinal = link.replace("user=", "")
                            var result = 0
                            for (element in linkfinal) {
                                result += element.toInt()
                            }
                            NOTIFICATION_ID = result
                        }
                        link.contains("ptopic=") -> {
                            NOTIFICATION_ID *= link.replace("ptopic=", "").toInt()
                        }
                    }
                }
            }
            val resultPendingIntent: PendingIntent
            if (URL == action) {
                val notificationIntent = Intent(Intent.ACTION_VIEW, Uri.parse(destination))
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0)
            } else if (ACTIVITY == action && activityMap.containsKey(destination)) {
                val notificationIntent = Intent(mContext, activityMap[destination])
                notificationIntent.putExtra("link", link)
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                //PendingIntent.FLAG_UPDATE_CURRENT
                //resultIntent = new Intent(mContext, activityMap.get(destination));
                /*resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );*/
            } else {
                resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                resultPendingIntent = PendingIntent.getActivity(
                        mContext,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                )
            }
            val mBuilder = NotificationCompat.Builder(
                    mContext, CHANNEL_ID)
            val notification: Notification
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.packageName + "/raw/notification")
            //Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            notification = when {
                iconBitMap == null -> {
                    //When Inbox Style is applied, user can expand the notification
                    val inboxStyle = NotificationCompat.InboxStyle()
                    inboxStyle.addLine(message)
                    mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentIntent(resultPendingIntent)
                            .setStyle(inboxStyle)
                            .setSmallIcon(icon) //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                            .setContentText(message)
                            .setVibrate(longArrayOf(1000, 1000))
                            .setSound(alarmSound)
                            .build()
                }
                iconUrl!!.contains("/profile_pics/") -> {
                    //When Inbox Style is applied, user can expand the notification
                    val inboxStyle = NotificationCompat.BigTextStyle()
                    inboxStyle.bigText(message)
                    mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentIntent(resultPendingIntent)
                            .setStyle(inboxStyle)
                            .setLargeIcon(iconBitMap)
                            .setContentText(message)
                            .setVibrate(longArrayOf(1000, 1000))
                            .setSound(alarmSound)
                            .build()
                }
                else -> {
                    //If Bitmap is created from URL, show big icon
                    val bigPictureStyle = NotificationCompat.BigPictureStyle()
                    bigPictureStyle.setBigContentTitle(title)
                    bigPictureStyle.setSummaryText(Html.fromHtml(message).toString())
                    bigPictureStyle.bigPicture(iconBitMap)
                    mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                            .setAutoCancel(true)
                            .setContentTitle(title)
                            .setContentIntent(resultPendingIntent)
                            .setStyle(bigPictureStyle)
                            .setGroup(null)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setLargeIcon(BitmapFactory.decodeResource(mContext.resources, icon))
                            .setContentText(message)
                            .setVibrate(longArrayOf(1000, 1000))
                            .setSound(alarmSound)
                            .build()
                }
            }
            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //All notifications should go through NotificationChannel on Android 26 & above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT)
                Objects.requireNonNull(notificationManager).createNotificationChannel(channel)
            }
            Objects.requireNonNull(notificationManager).notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * Downloads push notification image before displaying it in
     * the notification tray
     *
     * @param strURL : URL of the notification Image
     * @return : BitMap representation of notification Image
     */
    private fun getBitmapFromURL(strURL: String): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Playing notification sound
     */
    fun playNotificationSound() {
        try {
            val alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.packageName + "/raw/notification")
            val r = RingtoneManager.getRingtone(mContext, alarmSound)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private var NOTIFICATION_ID = 200
        private const val TAG = "MsgingUtils"
        private const val PUSH_NOTIFICATION = "pushNotification"
        private const val CHANNEL_ID = "Sabot"
        private const val CHANNEL_NAME = "SabotC"
        private const val URL = "url"
        private const val ACTIVITY = "activity"
    }

    init {
        //Populate activity map
        activityMap["FragmentContainer"] = FragmentContainer::class.java
        activityMap["ChatActivity"] = ChatActivity::class.java
    }
}