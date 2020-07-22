package com.lucidsoftworksllc.sabotcommunity;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.text.Html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NotificationsUtils {
    private static int NOTIFICATION_ID = 200;
    private static final String TAG = "MsgingUtils";
    private static final String PUSH_NOTIFICATION = "pushNotification";
    private static final String CHANNEL_ID = "Sabot";
    private static final String CHANNEL_NAME = "SabotC";
    private static final String URL = "url";
    private static final String ACTIVITY = "activity";
    Map<String, Class> activityMap = new HashMap<>();
    private Context mContext;

    public NotificationsUtils(Context mContext) {
        this.mContext = mContext;
        //Populate activity map
        activityMap.put("FragmentContainer", FragmentContainer.class);
        activityMap.put("ChatActivity", ChatActivity.class);
    }

    /**
     * Displays notification based on parameters
     *
     * @param notificationVO
     * @param resultIntent
     */
    public void displayNotification(NotificationVO notificationVO, Intent resultIntent) {
        {
            String message = notificationVO.getMessage();
            String title = notificationVO.getTitle();
            String iconUrl = notificationVO.getIconUrl();
            String action = notificationVO.getAction();
            String destination = notificationVO.getActionDestination();
            String link = notificationVO.getActionLink();

            /*if (link!=null){
                Log.e(TAG, "displayNotification: "+link);
            }*/

            Bitmap iconBitMap = null;
            if (iconUrl != null) {
                try {
                    if(iconUrl.contains("/profile_pics/")){
                        iconBitMap = getBitmapFromURL(Constants.BASE_URL+iconUrl.substring(0, iconUrl.length() - 4)+"_r.JPG");
                    }else{
                        iconBitMap = getBitmapFromURL(Constants.BASE_URL+iconUrl);
                    }
                }
                catch (Exception ignored) {
                }
            }
            final int icon = R.drawable.ic_launcher_trans;

            if (destination.equals("ChatActivity")){
                NOTIFICATION_ID = 1;
                if (link!=null){
                    if(link.contains("user=")) {
                        String linkfinal = link.replace("user=","");
                        int result = 0;
                        for (int i = 0; i < linkfinal.length(); i++) {
                            final char ch = linkfinal.charAt(i);
                            result += ch;
                        }
                        NOTIFICATION_ID = (result);
                        //Log.e(TAG, "displayNotification: "+NOTIFICATION_ID);
                    }else if(link.contains("group=")) {
                        NOTIFICATION_ID = Integer.parseInt(link.replace("group=", ""));
                    }else if(link.contains("requests")) {
                        NOTIFICATION_ID = 10;
                    }
                }
            }else if(destination.equals("FragmentContainer")){
                NOTIFICATION_ID = 2;
                if (link!=null){
                    if(link.contains("post.php?id=")) {
                        NOTIFICATION_ID = NOTIFICATION_ID*Integer.parseInt(link.replace("post.php?id=", ""));
                    }else if(link.contains("publics_topic.php?id=")) {
                        NOTIFICATION_ID = NOTIFICATION_ID*Integer.parseInt(link.replace("publics_topic.php?id=", ""));
                    }else if(link.contains("clan=")) {
                        NOTIFICATION_ID = NOTIFICATION_ID*Integer.parseInt(link.replace("clan=", ""));
                    }else if(link.contains("pchatroom=")) {
                        NOTIFICATION_ID = 20*Integer.parseInt(link.replace("pchatroom=", ""));
                    }else if(link.contains("user=")) {
                        String linkfinal = link.replace("user=","");
                        int result = 0;
                        for (int i = 0; i < linkfinal.length(); i++) {
                            final char ch = linkfinal.charAt(i);
                            result += ch;
                        }
                        NOTIFICATION_ID = (result);
                    }else if(link.contains("ptopic=")) {
                        NOTIFICATION_ID = NOTIFICATION_ID*Integer.parseInt(link.replace("ptopic=", ""));
                    }
                }
            }

            PendingIntent resultPendingIntent;
            if (URL.equals(action)) {
                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(destination));
                resultPendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
            } else if (ACTIVITY.equals(action) && activityMap.containsKey(destination)) {
                Intent notificationIntent = new Intent(mContext, activityMap.get(destination));
                notificationIntent.putExtra("link", link);
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultPendingIntent = PendingIntent.getActivity(mContext,0,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
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
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                resultPendingIntent =
                        PendingIntent.getActivity(
                                mContext,
                                0,
                                resultIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
            }


            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext, CHANNEL_ID);

            Notification notification;
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            //Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);

            if (iconBitMap == null) {
                //When Inbox Style is applied, user can expand the notification
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.addLine(message);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(inboxStyle)
                        .setSmallIcon(icon)
                        //.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(message)
                        .setVibrate(new long[] { 1000, 1000 })
                        .setSound(alarmSound)
                        .build();

            } else if(iconUrl.contains("/profile_pics/")){
                //When Inbox Style is applied, user can expand the notification
                NotificationCompat.BigTextStyle inboxStyle = new NotificationCompat.BigTextStyle();

                inboxStyle.bigText(message);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(inboxStyle)
                        .setLargeIcon(iconBitMap)
                        .setContentText(message)
                        .setVibrate(new long[] { 1000, 1000 })
                        .setSound(alarmSound)
                        .build();
            }else {
                //If Bitmap is created from URL, show big icon
                NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
                bigPictureStyle.setBigContentTitle(title);
                bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
                bigPictureStyle.bigPicture(iconBitMap);
                notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(System.currentTimeMillis())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setStyle(bigPictureStyle)
                        .setGroup(null)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                        .setContentText(message)
                        .setVibrate(new long[] { 1000, 1000 })
                        .setSound(alarmSound)
                        .build();
            }

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            //All notifications should go through NotificationChannel on Android 26 & above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_DEFAULT);
                Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
            }
            Objects.requireNonNull(notificationManager).notify(NOTIFICATION_ID, notification);
            //playNotificationSound();
        }
    }

    /**
     * Downloads push notification image before displaying it in
     * the notification tray
     *
     * @param strURL : URL of the notification Image
     * @return : BitMap representation of notification Image
     */
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            java.net.URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Playing notification sound
     */
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}