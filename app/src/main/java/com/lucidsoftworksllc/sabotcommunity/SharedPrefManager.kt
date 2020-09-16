package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SharedPrefManager {
    private static SharedPrefManager mInstance;
    private Context mCtx;

    private static final String BLOCK_USER_URL = Constants.ROOT_URL+"user_block.php";
    private static final String GET_USER_FRIENDS = Constants.ROOT_URL+"sharedprefs_getfriendarray.php";
    private static final String SET_SHIPMENT_INFO = Constants.ROOT_URL+"sharedprefs_saveshipmentinfo.php";
    private static final String STORE_FCM_TOKEN = Constants.ROOT_URL+"messages.php/storefcmtoken";
    private static final String SHARED_PREF_USER = "userpref";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_NICKNAME = "nickname";
    private static final String KEY_USER_EMAIL = "useremail";
    private static final String KEY_PROFILE_PIC = "profilepic";
    private static final String KEY_USER_ID = "userid";
    //private static final String KEY_USERS_FOLLOWED = "usersfollowed";
    //private static final String KEY_GAMES_FOLLOWED = "gamesfollowed";
    //private static final String KEY_USERS_FRIENDS = "usersfriends";
    private static final String KEY_FCM_TOKEN = "fcmtoken";
    private static final String KEY_BLOCKED_ARRAY = "blockedarray";
    //private static final String KEY_ADS_VIEWED = "adsviewed";
    //private static final String KEY_ADS_CLICKED = "adsclicked";
    private static final String KEY_DASH_CURRENT_PUBLICS = "currentpublics";
    private static final String KEY_NOTI_FREQUENCY = "notifrequency";
    private static final String KEY_LAST_NOTI = "lastnoti";
    private static final String KEY_SHIPMENT_INFO = "shipmentinfo";
    private static final String KEY_PUBLICS_SORT_BY = "publicssortby";
    private static final String KEY_NOTI_PLATFORMS = "notiplatforms";

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    void userLogin(String userid, String username, String nickname, String email, String profilepic, String usersfollowed, String gamesfollowed, String usersfriends, String blockarray){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID, userid);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_NICKNAME, nickname);
        editor.putString(KEY_PROFILE_PIC, profilepic);
        //editor.putString(KEY_USERS_FOLLOWED, usersfollowed);
        //editor.putString(KEY_GAMES_FOLLOWED, gamesfollowed);
        //editor.putString(KEY_USERS_FRIENDS, usersfriends);
        editor.putString(KEY_BLOCKED_ARRAY, blockarray);
        //editor.putString(KEY_ADS_CLICKED, ",");
        //editor.putString(KEY_ADS_VIEWED, ",");
        editor.apply();
    }

    boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null) != null;
    }

    boolean isUserBlocked(String username){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        String usersBlockedArray = sharedPreferences.getString(KEY_BLOCKED_ARRAY, null);
        assert usersBlockedArray != null;
        String[] blockedarray = usersBlockedArray.split(",");
        return Arrays.asList(blockedarray).contains(username);
    }

    void logout(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    void update_token(final String fcm_token){
        final SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, STORE_FCM_TOKEN, response -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_FCM_TOKEN, fcm_token);
            editor.apply();
        }, error -> Log.d("update_token", "onErrorResponse: "+error)){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("token",fcm_token);
                parms.put("user_id", Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)));
                parms.put("username", Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)));
                return parms;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
    }

    void block_user(final String username){
        final SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        final String new_block_key = sharedPreferences.getString(KEY_BLOCKED_ARRAY, null)+username+",";
        StringRequest stringRequest=new StringRequest(Request.Method.POST, BLOCK_USER_URL, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_BLOCKED_ARRAY, new_block_key);
                    editor.apply();
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx,"Could not block, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("new_block_key",new_block_key);
                parms.put("user_blocked",username);
                parms.put("user_id", Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)));
                parms.put("username", Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)));
                return parms;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
    }

    /*void getFriendArray(){
        final SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GET_USER_FRIENDS, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERS_FRIENDS, obj.getString("connections"));
                    editor.apply();
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx,"Could not get connections, please try again later...",Toast.LENGTH_LONG).show()){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("user_id", Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)));
                parms.put("username", Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)));
                return parms;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
    }*/

    public String getProfilePic(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PROFILE_PIC, null);
    }
    void setProfilePic(String profilePic){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROFILE_PIC, profilePic);
        editor.apply();
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME, null);
    }

    public String getUserID(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_ID, null);
    }

    public String getNickname(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_NICKNAME, null);
    }

    public String getFCMToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_FCM_TOKEN, null);
    }

    /*public String getUsersFriends(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERS_FRIENDS, null);
    }*/

    public String getEmail(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USER_EMAIL, null);
    }

    public String getCurrentPublics(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_DASH_CURRENT_PUBLICS, null) == null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_DASH_CURRENT_PUBLICS, "All");
            editor.apply();
        }
        return sharedPreferences.getString(KEY_DASH_CURRENT_PUBLICS, null);
    }
    public void setCurrentPublics(String filter){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_DASH_CURRENT_PUBLICS, filter);
        editor.apply();
        sharedPreferences.getString(KEY_DASH_CURRENT_PUBLICS, null);
    }

    public String getPublicsSortBy(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_PUBLICS_SORT_BY, null) == null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_PUBLICS_SORT_BY, "Followers");
            editor.apply();
        }
        return sharedPreferences.getString(KEY_PUBLICS_SORT_BY, null);
    }
    public void setPublicsSortBy(String sort){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PUBLICS_SORT_BY, sort);
        editor.apply();
        sharedPreferences.getString(KEY_PUBLICS_SORT_BY, null);
    }

    public String getNotificationFrequency(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_NOTI_FREQUENCY, null) == null){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOTI_FREQUENCY, "Always");
            editor.apply();
        }
        return sharedPreferences.getString(KEY_NOTI_FREQUENCY, null);
    }

    public void setNotificationFrequency(String frequency){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOTI_FREQUENCY, frequency);
        editor.apply();
        sharedPreferences.getString(KEY_NOTI_FREQUENCY, null);
    }

    public void setLastNoti(String now){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_LAST_NOTI, now);
        editor.apply();
        sharedPreferences.getString(KEY_LAST_NOTI, null);
    }

    public String getLastNoti(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_LAST_NOTI, null) == null){
            Date currentTime = Calendar.getInstance().getTime();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_LAST_NOTI, String.valueOf(currentTime));
            editor.apply();
        }
        return sharedPreferences.getString(KEY_LAST_NOTI, null);
    }

    public String getNotiPlatforms() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_NOTI_PLATFORMS, null) == null){
            ArrayList<String> platformList = new ArrayList<>();
            platformList.add("PlayStation");
            platformList.add("Xbox");
            platformList.add("Steam");
            platformList.add("PC");
            platformList.add("Mobile");
            platformList.add("Switch");
            platformList.add("Cross-Platform");
            platformList.add("Other");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_NOTI_PLATFORMS, String.valueOf(platformList));
            editor.apply();
        }
        return sharedPreferences.getString(KEY_NOTI_PLATFORMS, null);
    }

    void setNotiPlatforms(String platformarray) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_NOTI_PLATFORMS, platformarray);
        editor.apply();
    }

    public String getShipmentInfo() throws JSONException {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        if (sharedPreferences.getString(KEY_SHIPMENT_INFO, null) == null){
            JSONObject shipmentInfoObject = new JSONObject();
            JSONObject shipmentData = new JSONObject();
            shipmentData.put("fullname", "");
            shipmentData.put("street", "");
            shipmentData.put("city", "");
            shipmentData.put("state", "");
            shipmentData.put("apt", "");
            shipmentData.put("zip", "");
            shipmentData.put("country", "");
            shipmentInfoObject.put("shipmentInfo", shipmentData);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_SHIPMENT_INFO, String.valueOf(shipmentInfoObject));
            editor.apply();
        }
        return sharedPreferences.getString(KEY_SHIPMENT_INFO, null);
    }

    boolean setShipmentInfo(String fullname,String street,String city,String state,String apt,String zip,String country) throws JSONException {
        final String[] result = new String[1];
        result[0] = "True";
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_USER, Context.MODE_PRIVATE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, SET_SHIPMENT_INFO, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (!obj.getBoolean("error")) {
                    JSONObject shipmentInfoObject = new JSONObject();
                    JSONObject shipmentData = new JSONObject();
                    shipmentData.put("fullname", fullname);
                    shipmentData.put("street", street);
                    shipmentData.put("city", city);
                    shipmentData.put("state", state);
                    shipmentData.put("apt", apt);
                    shipmentData.put("zip", zip);
                    shipmentData.put("country", country);
                    shipmentInfoObject.put("shipmentInfo", shipmentData);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_SHIPMENT_INFO, String.valueOf(shipmentInfoObject));
                    editor.apply();
                    result[0] = "True";
                } else {
                    Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                    result[0] = "yes";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                result[0] = "yes";
            }
        }, error -> {
            Toast.makeText(mCtx,"Could not save shipment info! Error #3",Toast.LENGTH_LONG).show();
            result[0] = "yes";
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("user_id", Objects.requireNonNull(sharedPreferences.getString(KEY_USER_ID, null)));
                parms.put("username", Objects.requireNonNull(sharedPreferences.getString(KEY_USERNAME, null)));
                parms.put("fullname", fullname);
                parms.put("street", street);
                parms.put("city", city);
                parms.put("state", state);
                parms.put("apt", apt);
                parms.put("zip", zip);
                parms.put("country", country);
                return parms;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
        return Boolean.parseBoolean(result[0]);
    }

}
