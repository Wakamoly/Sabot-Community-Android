package com.lucidsoftworksllc.sabotcommunity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.view.MenuInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentContainer extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    //Paypal intent request code to track onActivityResult method
    public static final int PAYPAL_REQUEST_CODE = 123;
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            .clientId(PayPalConfig.PAYPAL_CLIENT_ID);
    private static final String UNREAD_NUM = Constants.ROOT_URL+"get_notification_unread.php";
    public static final String SEND_PAYMENT_CONFIRMATION = Constants.ROOT_URL+"merch_payment.php";
    private static final String GET_CURRENT_VERSION = Constants.ROOT_URL+"get_current_version.php";
    private DrawerLayout mDrawerLayout;
    private BottomNavigationView navView;
    private String deviceUsername;
    private RelativeLayout dashContainer;
    private RequestQueue requestQueue;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        unreadNotificationsHandler(1000);
        loadFragment(new DashboardFragment());
        if(getIntent().hasExtra("user_to_id")) {
            String user_to = getIntent().getStringExtra("user_to_id");
            if (user_to != null && !user_to.isEmpty()) {
                FragmentProfile ldf = new FragmentProfile();
                Bundle args = new Bundle();
                args.putString("UserId", user_to);
                ldf.setArguments(args);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, ldf)
                        .commit();
            }
        }else if (getIntent().hasExtra("link")&&getIntent().getStringExtra("link")!=null){
            String link = getIntent().getStringExtra("link");
            if(link.contains("post.php?id=")) {
                String linkID = link.replace("post.php?id=", "");
                ProfilePostFragment ldf = new ProfilePostFragment ();
                Bundle args = new Bundle();
                args.putString("id", linkID);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }else if(link.contains("publics_topic.php?id=")) {
                String linkID = link.replace("publics_topic.php?id=", "");
                PublicsTopicFragment ldf = new PublicsTopicFragment ();
                Bundle args = new Bundle();
                args.putString("PublicsId", linkID);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }else if(link.contains("clan=")) {
                String linkID = link.replace("clan=", "");
                ClanFragment ldf = new ClanFragment();
                Bundle args = new Bundle();
                args.putString("ClanId", linkID);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }
            else if(link.contains("user=")) {
                String linkID = link.replace("user=", "");
                FragmentProfile ldf = new FragmentProfile ();
                Bundle args = new Bundle();
                args.putString("Username", linkID);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }else if(link.contains("ptopic=")) {
                String linkID = link.replace("ptopic=", "");
                PublicsTopicFragment ldf = new PublicsTopicFragment ();
                Bundle args = new Bundle();
                args.putString("PublicsId", linkID);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }else if(link.contains("pchatroom=")) {
                String linkID = link.replace("pchatroom=", "");
                PublicsChatRoom ldf = new PublicsChatRoom ();
                Bundle args = new Bundle();
                args.putString("GameId", linkID);
                ldf.setArguments(args);
                this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }else if(link.contains("review")) {
                FragmentProfile ldf = new FragmentProfile();
                Bundle args = new Bundle();
                args.putString("UserId", SharedPrefManager.getInstance(this).getUserID());
                ldf.setArguments(args);
                (this).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, ldf)
                        .commit();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        dashContainer = findViewById(R.id.dashContainer);
        deviceUsername = SharedPrefManager.getInstance(this).getUsername();
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.side_nav_view);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "getInstanceId failed", task.getException());
                        return;
                    }
                    String token = Objects.requireNonNull(task.getResult()).getToken();
                    if(!token.equals(SharedPrefManager.getInstance(getApplicationContext()).getFCMToken())){
                        SharedPrefManager.getInstance(getApplicationContext()).update_token(token);
                    }
                });
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        unreadNotificationsHandler(1000);
        loadFragment(new DashboardFragment());

        if(getIntent().hasExtra("user_to_id")) {
            String user_to = getIntent().getStringExtra("user_to_id");
            if (user_to != null && !user_to.isEmpty()) {
                FragmentProfile ldf = new FragmentProfile();
                Bundle args = new Bundle();
                args.putString("UserId", user_to);
                ldf.setArguments(args);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).commit();
            }
        }else if (getIntent().hasExtra("link")&&getIntent().getStringExtra("link")!=null){
            String link = getIntent().getStringExtra("link");
            if (link != null) {
                if(link.contains("post.php?id=")) {
                    String linkID = link.replace("post.php?id=", "");
                    ProfilePostFragment ldf = new ProfilePostFragment ();
                    Bundle args = new Bundle();
                    args.putString("id", linkID);
                    ldf.setArguments(args);
                    this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }else if(link.contains("publics_topic.php?id=")) {
                    String linkID = link.replace("publics_topic.php?id=", "");
                    PublicsTopicFragment ldf = new PublicsTopicFragment ();
                    Bundle args = new Bundle();
                    args.putString("PublicsId", linkID);
                    ldf.setArguments(args);
                    this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }else if(link.contains("clan=")) {
                    String linkID = link.replace("clan=", "");
                    ClanFragment ldf = new ClanFragment();
                    Bundle args = new Bundle();
                    args.putString("ClanId", linkID);
                    ldf.setArguments(args);
                    this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }
                else if(link.contains("user=")) {
                    String linkID = link.replace("user=", "");
                    FragmentProfile ldf = new FragmentProfile ();
                    Bundle args = new Bundle();
                    args.putString("Username", linkID);
                    ldf.setArguments(args);
                    this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }else if(link.contains("ptopic=")) {
                    String linkID = link.replace("ptopic=", "");
                    PublicsTopicFragment ldf = new PublicsTopicFragment ();
                    Bundle args = new Bundle();
                    args.putString("PublicsId", linkID);
                    ldf.setArguments(args);
                    this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }else if(link.contains("pchatroom=")) {
                    String linkID = link.replace("pchatroom=", "");
                    PublicsChatRoom ldf = new PublicsChatRoom ();
                    Bundle args = new Bundle();
                    args.putString("GameId", linkID);
                    ldf.setArguments(args);
                    this.getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }else if(link.contains("review")) {
                    FragmentProfile ldf = new FragmentProfile();
                    Bundle args = new Bundle();
                    args.putString("UserId", SharedPrefManager.getInstance(this).getUserID());
                    ldf.setArguments(args);
                    (this).getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragment_container, ldf)
                            .commit();
                }
            }
        }
        if(getIntent().hasExtra("registered")){
            new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.ic_action_accept)
                    .setTitle(R.string.registered_title)
                    .setMessage(R.string.registered_desc)
                    .setPositiveButton(R.string.yes, v -> loadFragment(new AccountSettingsFragment()))
                    .setNegativeButton(R.string.no, null)
                    .show();
        }else{
            getCurrentUpdate();
        }
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_messages: {
                    this.finish();
                    startActivity(new Intent(this, ChatActivity.class));
                    break;
                }
                case R.id.nav_settings: {
                    Fragment asf = new AccountSettingsFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
                case R.id.nav_profile: {
                    Fragment asf = new FragmentProfile();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
                case R.id.nav_publics: {
                    Fragment asf = new PublicsFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
                case R.id.nav_clans: {
                    Fragment asf = new ClansListFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
                case R.id.nav_about: {
                    Fragment asf = new AboutFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
                case R.id.nav_contact_us: {
                    Fragment asf = new ContactUsFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
                /*case R.id.nav_donate: {
                    Fragment asf = new DonateFragment();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }*/
                //TODO: FIX THIS v
                case R.id.nav_merch: {
                    Fragment asf = new MerchFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    break;
                }
            }
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        View navHeader = navigationView.getHeaderView(0);
        ImageView navBackground = navHeader.findViewById(R.id.img_header_bg);
        TextView headerUsername = navHeader.findViewById(R.id.headerUsername);
        TextView headerNickname = navHeader.findViewById(R.id.headerNickname);
        TextView versionNumber = navHeader.findViewById(R.id.versionNumber);
        versionNumber.setText(Constants.APP_VERSION_FINAL);
        headerNickname.setText(SharedPrefManager.getInstance(this).getNickname());
        String headerUsernameText = "@"+SharedPrefManager.getInstance(this).getUsername();
        headerUsername.setText(headerUsernameText);
        Glide.with(this).load(Constants.BASE_URL +SharedPrefManager.getInstance(this).getProfilePic())
                .thumbnail(0.5f)
                .into(navBackground);
    }

    public void openDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    private void getUnreadNotifications(){
        if(!shouldGetNotification(this)) {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, UNREAD_NUM, response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getString("error").equals("false")) {
                            if (obj.has("num")) {
                                BottomMenuHelper.showBadge(getApplicationContext(), navView, R.id.navigation_notifications, obj.getString("num"));
                            }
                        } else if(obj.getString("banned").equals("yes")){
                            Toast.makeText(
                                    getApplicationContext(),
                                    "User Banned/Closed!",
                                    Toast.LENGTH_LONG).show();
                            final Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                            toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            SharedPrefManager.getInstance(FragmentContainer.this).logout();
                            finish();
                            startActivity(toLogin);
                        }else{
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        unreadNotificationsHandler(6000);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {}) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parms = new HashMap<>();
                        parms.put("username", deviceUsername);
                        return parms;
                    }
                };
                addToRequestQueue(stringRequest);
            } catch (Exception ignored) {}
        }
    }

    void addToRequestQueue(StringRequest stringRequest){
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    static boolean shouldGetNotification(Context context) {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true;
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return Objects.requireNonNull(km).inKeyguardRestrictedInputMode();
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.navigation_dashboard:
                fragment = new DashboardFragment();
                break;
            case R.id.navigation_notifications:
                BottomMenuHelper.removeBadge(navView, R.id.navigation_notifications);
                fragment = new NotificationsFragment();
                break;
            case R.id.navigation_publics:
                fragment = new PublicsFragment();
                break;
            case R.id.navigation_search:
                fragment = new SearchFragment();
                break;
            case R.id.navigation_profile:
                fragment = new FragmentProfile();
                break;
        }
        return loadFragment(fragment);
    }

    public void profilePopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.profile_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menuLogout) {
                final Intent toLogin = new Intent(getApplicationContext(), LoginActivity.class);
                toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                SharedPrefManager.getInstance(FragmentContainer.this).logout();
                finish();
                startActivity(toLogin);
            }
            if (item.getItemId() == R.id.menuSettings) {
                Fragment asf = new AccountSettingsFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, asf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            return true;
        });
        popup.show();
    }

    private static class BottomMenuHelper {
        public static void showBadge(Context context, BottomNavigationView bottomNavigationView, @IdRes int itemId, String value) {
            removeBadge(bottomNavigationView, itemId);
            BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
            View badge = LayoutInflater.from(context).inflate(R.layout.snippet_badge_number, bottomNavigationView, false);
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

    public void unreadNotificationsHandler(int delay){
        Handler chatHandler=new Handler();
        Runnable runnableCode = this::getUnreadNotifications;
        chatHandler.postDelayed(runnableCode, delay);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        JSONObject jsonDetails = new JSONObject(paymentDetails);
                        JSONObject response_details = jsonDetails.getJSONObject("response");
                        String payment_id = response_details.getString("id");
                        String message = (getString(R.string.order_message1))+"\n\nPayPal order ID (screenshot this for your records): "+payment_id;
                        StringRequest stringRequest=new StringRequest(Request.Method.POST, SEND_PAYMENT_CONFIRMATION, response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(!jsonObject.getString("error").equals("false")){
                                    Toast.makeText(getApplicationContext(),"Issue with payment! Please contact an admin!",Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> Toast.makeText(getApplicationContext(),"Error on Response, please try again later...",Toast.LENGTH_LONG).show()){
                            @Override
                            protected Map<String, String> getParams()  {
                                Map<String,String> parms= new HashMap<>();
                                parms.put("payment_details",paymentDetails);
                                parms.put("payment_id",payment_id);
                                parms.put("username",SharedPrefManager.getInstance(getApplicationContext()).getUsername());
                                parms.put("email",SharedPrefManager.getInstance(getApplicationContext()).getEmail());
                                return parms;
                            }
                        };
                        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                        requestQueue.add(stringRequest);
                        new LovelyStandardDialog(this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.green)
                                .setButtonsColorRes(R.color.green)
                                .setIcon(R.drawable.ic_action_accept)
                                .setTitle(R.string.thank_you_for_your_order)
                                .setMessage(message)
                                .setPositiveButton(R.string.ok, null)
                                .show();

                    } catch (JSONException e) {
                        Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    }
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getCurrentUpdate(){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, GET_CURRENT_VERSION, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                if (obj.getString("error").equals("false")) {
                    if (obj.getInt("version") > Constants.APP_GRADLE_VERSION){
                        new LovelyStandardDialog(FragmentContainer.this, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                .setTopColorRes(R.color.green)
                                .setButtonsColorRes(R.color.green)
                                .setIcon(R.drawable.ic_action_report)
                                .setTitle(R.string.new_update_available)
                                .setMessage(R.string.gp_redirect)
                                .setPositiveButton(R.string.yes, v -> {
                                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.lucidsoftworksllc.sabotcommunity");
                                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                    getApplicationContext().startActivity(intent);
                                })
                                .setNegativeButton(R.string.no, null)
                                .show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("version",Constants.APP_VERSION_FINAL);
                parms.put("api","android");
                return parms;
            }
        };
        addToRequestQueue(stringRequest);
    }

}