package com.lucidsoftworksllc.sabotcommunity;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lucidsoftworksllc.sabotcommunity.NotificationsUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private static final String TAG = "MsgingService";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";
    private static final String ACTION_LINK = "link";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            try {
                handleData(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification());
        }
    }

    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationsUtils notificationUtils = new NotificationsUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
    }

    private void handleData(Map<String, String> data) throws JSONException {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        String actionLink = data.get(ACTION_LINK);
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);
        notificationVO.setActionLink(actionLink);

        assert title != null;
        if (!title.contains("players needed!")){
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            NotificationsUtils notificationUtils = new NotificationsUtils(getApplicationContext());
            notificationUtils.displayNotification(notificationVO, resultIntent);
        }else{
            String lastNoti = SharedPrefManager.getInstance(getApplicationContext()).getLastNoti();
            String currentTime = String.valueOf(Calendar.getInstance().getTime());
            if (canSendNoti(lastNoti,currentTime,message)) {
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                NotificationsUtils notificationUtils = new NotificationsUtils(getApplicationContext());
                notificationUtils.displayNotification(notificationVO, resultIntent);
                SharedPrefManager.getInstance(getApplicationContext()).setLastNoti(currentTime);
            }
        }
    }

    private boolean canSendNoti(String lastnoti, String now, String message) throws JSONException {
        JSONArray notiPlatformsData = new JSONArray(SharedPrefManager.getInstance(this).getNotiPlatforms());
        String[] objectName = new String[notiPlatformsData.length()];
        boolean notiPlatformContains = false;
        for(int i=0; i<notiPlatformsData.length(); i++) {
            objectName[i] = notiPlatformsData.getString(i);
            if(message.contains("on "+objectName[i])){
                notiPlatformContains = true;
                break;
            }
        }
        if (notiPlatformContains) {
            String notiFrequency = SharedPrefManager.getInstance(getApplicationContext()).getNotificationFrequency();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
                    Locale.ENGLISH);
            try {
                Date adjustedTime = sdf.parse(lastnoti);
                calendar.setTime(adjustedTime);
                switch (notiFrequency) {
                    case "24 Hours":
                        calendar.add(Calendar.HOUR, 24);
                        break;
                    case "12 Hours":
                        calendar.add(Calendar.HOUR, 12);
                        break;
                    case "8 Hours":
                        calendar.add(Calendar.HOUR, 8);
                        break;
                    case "4 Hours":
                        calendar.add(Calendar.HOUR, 4);
                        break;
                    case "2 Hours":
                        calendar.add(Calendar.HOUR, 2);
                        break;
                    case "1 Hour":
                        calendar.add(Calendar.HOUR, 1);
                        break;
                    case "Always":
                        calendar.add(Calendar.HOUR, -24);
                        break;
                    case "Never":
                        calendar.add(Calendar.YEAR, 100);
                        break;
                }
                Date date2 = sdf.parse(now);
                Date date1 = calendar.getTime();
                return date1.before(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}