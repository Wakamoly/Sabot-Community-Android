package com.lucidsoftworksllc.sabotcommunity;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String UNREAD_NUM = Constants.ROOT_URL+"get_messages_unread.php";

    private String deviceUserID,deviceUsername;
    private RequestQueue requestQueue;
    private BottomNavigationView navView;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        if(getIntent().hasExtra("user_to")) {
            String user_to;
            user_to = getIntent().getStringExtra("user_to");
            if (!user_to.isEmpty()) {
                MessageFragment ldf = new MessageFragment();
                Bundle args = new Bundle();
                args.putString("user_to", user_to);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
            }
        }else if (getIntent().hasExtra("link")&&getIntent().getStringExtra("link")!=null){
            String link = getIntent().getStringExtra("link");
            if (link!=null&&!link.isEmpty()){
                if(link.contains("group=")) {
                    String linkfinal = link.replace("group=","");
                    if (!linkfinal.isEmpty()) {
                        MessageGroupFragment ldf = new MessageGroupFragment();
                        Bundle args = new Bundle();
                        args.putString("group_id", linkfinal);
                        ldf.setArguments(args);
                        this.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                    }
                }else if(link.contains("user=")) {
                    String linkfinal = link.replace("user=","");
                    if (!linkfinal.isEmpty()) {
                        MessageFragment ldf = new MessageFragment();
                        Bundle args = new Bundle();
                        args.putString("user_to", linkfinal);
                        ldf.setArguments(args);
                        this.getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_fragment_container);
        navView = findViewById(R.id.chat_nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        deviceUsername = SharedPrefManager.getInstance(this).getUsername();
        deviceUserID = SharedPrefManager.getInstance(this).getUserID();

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        getUnreadMessagesHandler(4000);
        loadFragment(new ConvosFragment());

        if(getIntent().hasExtra("user_to")) {
            String user_to;
            user_to = getIntent().getStringExtra("user_to");
            if (!user_to.isEmpty()) {
                MessageFragment ldf = new MessageFragment();
                Bundle args = new Bundle();
                args.putString("user_to", user_to);
                ldf.setArguments(args);
                getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
            }
        }else if (getIntent().hasExtra("link")&&getIntent().getStringExtra("link")!=null){
            String link = getIntent().getStringExtra("link");
            if (link!=null&&!link.isEmpty()){
                if(link.contains("group=")) {
                    String linkfinal = link.replace("group=","");
                    if (!linkfinal.isEmpty()) {
                        MessageGroupFragment ldf = new MessageGroupFragment();
                        Bundle args = new Bundle();
                        args.putString("group_id", linkfinal);
                        ldf.setArguments(args);
                        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                    }
                }else if(link.contains("user=")) {
                    String linkfinal = link.replace("user=","");
                    if (!linkfinal.isEmpty()) {
                        MessageFragment ldf = new MessageFragment();
                        Bundle args = new Bundle();
                        args.putString("user_to", linkfinal);
                        ldf.setArguments(args);
                        getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                    }
                }else if(link.contains("requests")) {
                    MessageRequestsFragment ldf = new MessageRequestsFragment();
                    Bundle args = new Bundle();
                    ldf.setArguments(args);
                    getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                }
            }
        }


        //TODO: Make on backpressed load convos
        /*else{
            loadFragment(new ConvosFragment());
        }*/

        //TODO: Make unreadmessages() return new messages or just badge?
        /*Timer _Request_Trip_Timer = new Timer();
        _Request_Trip_Timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getUnreadMessages();
            }
        }, 30000, 30000); // First time start after 5 mili second and repeat after 30 seconds*/

    }

    public void getUnreadMessagesHandler(int delay){
        Handler chatHandler=new Handler();
        Runnable runnableCode = this::getUnreadMessages;
        chatHandler.postDelayed(runnableCode, delay);
    }

    void addToRequestQueue(StringRequest stringRequest){
        stringRequest.setShouldCache(false);// no caching url...
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void getUnreadMessages(){
        if(!shouldGetNotification(this)) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, UNREAD_NUM, response -> {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("error").equals("false")) {
                        if (obj.has("num")) {
                            BottomMenuHelper.showBadge(getApplicationContext(), navView, R.id.chat_navigation_inbox, obj.getString("num"));
                        }
                        if (obj.has("requests")) {
                            BottomMenuHelper.showBadge(getApplicationContext(), navView, R.id.chat_navigation_requests, obj.getString("requests"));
                        }
                    } else if(obj.getString("banned").equals("yes")){
                        Toast.makeText(
                                getApplicationContext(),
                                "User Banned/Closed!",
                                Toast.LENGTH_LONG).show();
                        final Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                        toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        SharedPrefManager.getInstance(ChatActivity.this).logout();
                        finish();
                        startActivity(toLogin);
                    }else{
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                    getUnreadMessagesHandler(30000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> Toast.makeText(getApplicationContext(), "Could not get messages, please try again later...", Toast.LENGTH_LONG).show()) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> parms = new HashMap<>();
                    parms.put("username", deviceUsername);
                    return parms;
                }
            };
            addToRequestQueue(stringRequest);
        }
    }

    static boolean shouldGetNotification(Context context) {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true;
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.chat_navigation_inbox:
                fragment = new ConvosFragment();
                break;
            case R.id.chat_navigation_search:
                fragment = new NewMessageFragment();
                break;
            case R.id.chat_navigation_requests:
                fragment = new MessageRequestsFragment();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            finish();
            startActivity(new Intent(this, FragmentContainer.class));
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private static class BottomMenuHelper {
        public static void showBadge(Context context, BottomNavigationView
                bottomNavigationView, @IdRes int itemId, String value) {
            removeBadge(bottomNavigationView, itemId);
            BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
            View badge = LayoutInflater.from(context).inflate(R.layout.snippet_badge_message_number, bottomNavigationView, false);
            if(!(value.equals("0"))) {
                TextView text = badge.findViewById(R.id.badge_text_view);
                text.setText(value);
                itemView.addView(badge);
            }
        }

        public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
            BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
            if (itemView.getChildCount() == 3) {
                itemView.removeViewAt(2);
            }
        }
    }

}
