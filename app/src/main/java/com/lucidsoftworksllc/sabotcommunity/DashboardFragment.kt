package com.lucidsoftworksllc.sabotcommunity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static android.view.View.GONE;
import static com.lucidsoftworksllc.sabotcommunity.PaginationOnScroll.PAGE_SIZE;
import static com.lucidsoftworksllc.sabotcommunity.PaginationOnScroll.PAGE_START;
import static com.lucidsoftworksllc.sabotcommunity.R.drawable.icons8_xbox_50;

public class DashboardFragment extends Fragment {

    private static final String DashboardAds_URL = Constants.ROOT_URL+"dashboardads_api.php";
    private static final String CURRENT_PUBLICS = Constants.ROOT_URL+"current_publics.php";
    private static final String DashboardFeed_URL = Constants.ROOT_URL+"dashboardfeed_api.php";
    private static final String URL_VIEWED = Constants.ROOT_URL+"dashboard_ad_interaction.php";
    private static final String USERS_ONLINE = Constants.ROOT_URL+"num_users_online.php";
    private SwipeRefreshLayout dashboardRefreshLayout;
    private ArrayList<SliderUtilsDash> sliderImg;
    private ArrayList<CurrentPublicsPOJO> currentPublicsList;
    private ViewPager viewPager, currentPublicsVP;
    private TabLayout sliderDotspanel, currentPublicsVPDots;
    private LinearLayout currentPublicsTV, noCurrentPublics, newsLayout;
    private TextView noPosts, numUsersOnline, numCurrentPublics, filterText, badge_text_view,newsTextView,newsTextView2;
    private RelativeLayout sliderboi, relLayoutDash2, dashContainer;
    private Button followingPostsButton, allPostsButton;
    private ProgressBar dashProgressBar, currentPublicsProgress, postsProgress;
    private Context mContext;
    private DashViewPagerAdapter viewPagerAdapter;
    private String userID, username;
    private ImageView dashboardMenu, dashboardToMessages, currentPublicsOptions;
    private String filter;
    private ArrayList<String> adNotified;
    private ScrollView dashScroll;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int pageSize = PAGE_SIZE;
    private boolean isLoading = false;
    //private AdView adView;
    private String clicked;
    private List<Profilenews_Recycler> dashboardfeedRecyclerList;
    private ProfilenewsAdapter dashboardfeedadapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View dashboardRootView = inflater.inflate(R.layout.fragment_dashboard, null);

        dashProgressBar = dashboardRootView.findViewById(R.id.dashProgressBar);
        sliderboi = dashboardRootView.findViewById(R.id.sliderboi);
        relLayoutDash2 = dashboardRootView.findViewById(R.id.relLayoutDash2);
        followingPostsButton = dashboardRootView.findViewById(R.id.followingPostsButton);
        allPostsButton = dashboardRootView.findViewById(R.id.allPostsButton);
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        RecyclerView dashboardfeedView = dashboardRootView.findViewById(R.id.dashboardfeedView);
        dashboardRefreshLayout = dashboardRootView.findViewById(R.id.dashSwipe);
        dashboardMenu = dashboardRootView.findViewById(R.id.dashboardMenu);
        dashboardToMessages = dashboardRootView.findViewById(R.id.dashboardToMessages);
        noPosts = dashboardRootView.findViewById(R.id.noPosts);
        numUsersOnline = dashboardRootView.findViewById(R.id.numUsersOnline);
        currentPublicsTV = dashboardRootView.findViewById(R.id.currentPublicsTV);
        currentPublicsProgress = dashboardRootView.findViewById(R.id.currentPublicsProgress);
        numCurrentPublics = dashboardRootView.findViewById(R.id.numCurrentPublics);
        currentPublicsOptions = dashboardRootView.findViewById(R.id.currentPublicsOptions);
        noCurrentPublics = dashboardRootView.findViewById(R.id.noCurrentPublics);
        filterText = dashboardRootView.findViewById(R.id.filterText);
        dashContainer = dashboardRootView.findViewById(R.id.dashContainer);
        badge_text_view = dashboardRootView.findViewById(R.id.badge_text_view);
        postsProgress = dashboardRootView.findViewById(R.id.postsProgress);
        newsLayout = dashboardRootView.findViewById(R.id.newsLayout);
        newsTextView = dashboardRootView.findViewById(R.id.newsTextView);
        newsTextView2 = dashboardRootView.findViewById(R.id.newsTextView2);
        dashScroll = dashboardRootView.findViewById(R.id.dashScroll);
        //adView = dashboardRootView.findViewById(R.id.adView);
        mContext = getActivity();

        dashboardfeedRecyclerList = new ArrayList<>();
        //dashboardfeedView.setHasFixedSize(true);
        dashboardfeedView.setLayoutManager(new LinearLayoutManager(mContext));
        ViewCompat.setNestedScrollingEnabled(dashboardfeedView, false);
        dashboardfeedadapter = new ProfilenewsAdapter(mContext, dashboardfeedRecyclerList);
        dashboardfeedView.setAdapter(dashboardfeedadapter);
        dashScroll.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (dashScroll.getChildAt(0).getBottom() <= (dashScroll.getHeight() + dashScroll.getScrollY())) {
                if (!isLoading&&!isLastPage){
                    isLoading = true;
                    currentPage++;
                    loadDashboardFeed(currentPage,clicked);
                }
            }
        });

        sliderImg = new ArrayList<>();
        currentPublicsList = new ArrayList<>();
        adNotified = new ArrayList<>();
        viewPager = dashboardRootView.findViewById(R.id.viewPager);
        sliderDotspanel = dashboardRootView.findViewById(R.id.SliderDots);
        sliderDotspanel.setupWithViewPager(viewPager, true);
        currentPublicsVP = dashboardRootView.findViewById(R.id.currentPublicsVP);
        currentPublicsVPDots = dashboardRootView.findViewById(R.id.currentPublicsVPDots);
        currentPublicsVPDots.setupWithViewPager(currentPublicsVP, true);
        dashboardMenu.setOnClickListener(v -> ((FragmentContainer)mContext).openDrawer());
        dashboardToMessages.setOnClickListener(v -> startActivity(new Intent(mContext, ChatActivity.class)));
        followingPostsButton.setOnClickListener(v -> postsQueryButtonClicked(followingPostsButton));
        allPostsButton.setOnClickListener(v -> postsQueryButtonClicked(allPostsButton));
        filter = SharedPrefManager.getInstance(mContext).getCurrentPublics();
        usersOnline();
        sendRequest();
        postsQueryButtonClicked(followingPostsButton);
        dashboardRefreshLayout.setOnRefreshListener(() -> {
            adNotified.clear();
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof DashboardFragment) {
                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            dashboardRefreshLayout.setRefreshing(false);
            sliderboi.requestFocus();
        });

        viewPagerAdapter = new DashViewPagerAdapter(sliderImg, mContext);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override public void onPageSelected(int position) {
                String adID = sliderImg.get(position).getSliderAdID();
                adViewed(adID);
            }
            @Override public void onPageScrollStateChanged(int state) {
                dashboardRefreshLayout.setEnabled(state != ViewPager.SCROLL_STATE_DRAGGING);
            }
        });

        currentPublicsVP.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override public void onPageSelected(int position) {}
            @Override public void onPageScrollStateChanged(int state) {
                dashboardRefreshLayout.setEnabled(state != ViewPager.SCROLL_STATE_DRAGGING);
            }
        });

        setPlatformImage(filter);
        currentPublicsOptions.setOnClickListener(v -> {
            String[] items = getResources().getStringArray(R.array.platform_array_w_all);
            new LovelyChoiceDialog(mContext)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle(R.string.platform_filter)
                    .setIcon(R.drawable.icons8_workstation_48)
                    .setMessage(getResources().getString(R.string.selected_platform)+" "+SharedPrefManager.getInstance(mContext).getCurrentPublics())
                    .setItems(items, (position, item) -> {
                        SharedPrefManager.getInstance(mContext).setCurrentPublics(item);
                        setPlatformImage(item);
                        filter = item;
                        currentPublicsProgress.setVisibility(View.VISIBLE);
                        currentPublicsList.clear();
                        getCurrentPublics();
                    })
                    .show();
        });

        /*MobileAds.initialize(mContext, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/

        hideKeyboardFrom(mContext, dashboardRootView);
        return dashboardRootView;
    }

    private void adViewed(String adID){
        String isAdViewed = "no";
        for (String adViewed : adNotified) {
            if (adViewed.contains(adID)) {
                isAdViewed = "yes";
                break;
            }
        }
        if (isAdViewed.equals("no")){
            adNotified.add(adID);
            StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_VIEWED, response -> {}, error -> {}){
                @Override protected Map<String, String> getParams()  {
                    Map<String,String> parms= new HashMap<>();
                    parms.put("id", adID);
                    parms.put("method", "view");
                    parms.put("user_id",SharedPrefManager.getInstance(mContext).getUserID());
                    parms.put("username",SharedPrefManager.getInstance(mContext).getUsername());
                    return parms;
                }
            };
            ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
        }
    }

    public void usersOnline(){
        Thread usersOnlineThread = new Thread(){//create thread
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, USERS_ONLINE+"?username="+username, response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray usersonline = obj.getJSONArray("numonline");
                        for(int i = 0; i<usersonline.length(); i++){
                            JSONObject usersonlineObj = usersonline.getJSONObject(i);
                            String num = usersonlineObj.getString("num");
                            String numpublics = usersonlineObj.getString("numpublics");
                            numUsersOnline.setText(num);
                            numCurrentPublics.setText(numpublics);
                            int unreadmessages = Integer.parseInt(usersonlineObj.getString("unreadmessages"));
                            if (unreadmessages>0){
                                badge_text_view.setVisibility(View.VISIBLE);
                                if (unreadmessages>9){
                                    badge_text_view.setText("9+");
                                }else{
                                    badge_text_view.setText(usersonlineObj.getString("unreadmessages"));
                                }
                            }
                        }
                        JSONArray dashnews = obj.getJSONArray("dashnews");
                        if (dashnews.length()!=0) {
                            newsLayout.setVisibility(View.VISIBLE);
                            for (int i = 0; i < dashnews.length(); i++) {
                                JSONObject usersonlineObj = dashnews.getJSONObject(i);
                                String toptext = usersonlineObj.getString("toptext");
                                String bottomtext = usersonlineObj.getString("bottomtext");
                                String link = usersonlineObj.getString("link");
                                newsTextView.setText(toptext);
                                newsTextView2.setText(bottomtext);
                                newsLayout.setOnClickListener(v -> mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link))));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                });
                ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
            }
        };
        usersOnlineThread.start();
    }

    public void getCurrentPublics(){
        Thread getCurrentPublicsThread = new Thread(){//create thread
            @Override
            public void run() {
                currentPublicsList.clear();
                /*if (currentPublicsVP!=null){
                    currentPublicsVP.notifyAll();
                }*/
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, CURRENT_PUBLICS+"?filter="+filter+"&username="+username, null, response -> {
                    if (response.length() == 0) {
                        noCurrentPublics.setVisibility(View.VISIBLE);
                        currentPublicsProgress.setVisibility(GONE);
                        currentPublicsTV.setOnClickListener(v -> {
                            currentPublicsProgress.setVisibility(View.VISIBLE);
                            currentPublicsList.clear();
                            getCurrentPublics();
                        });
                        noCurrentPublics.setOnClickListener(v -> {
                            Fragment asf = new PublicsFragment();
                            FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, asf);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        });
                    } else {
                        for (int i = 0; i < response.length(); i++) {
                            CurrentPublicsPOJO currentPublics = new CurrentPublicsPOJO();//TODO
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                currentPublics.setId(jsonObject.getString("id"));
                                currentPublics.setSubject(jsonObject.getString("subject"));
                                currentPublics.setCatname(jsonObject.getString("catname"));
                                currentPublics.setType(jsonObject.getString("type"));
                                currentPublics.setProfile_pic(jsonObject.getString("profile_pic"));
                                currentPublics.setNickname(jsonObject.getString("nickname"));
                                currentPublics.setEvent_date(jsonObject.getString("event_date"));
                                currentPublics.setContext(jsonObject.getString("context"));
                                currentPublics.setNum_players(jsonObject.getString("num_players"));
                                currentPublics.setNum_added(jsonObject.getString("num_added"));
                                currentPublics.setImage(jsonObject.getString("image"));
                                currentPublics.setPlayingNow(jsonObject.getString("playing_now"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            currentPublicsList.add(currentPublics);
                        }
                        DashCurrentPublicsAdapter currentPublicsAdapter = new DashCurrentPublicsAdapter(currentPublicsList, mContext);
                        currentPublicsVP.setAdapter(currentPublicsAdapter);
                        currentPublicsProgress.setVisibility(GONE);
                        noCurrentPublics.setVisibility(GONE);
                        currentPublicsTV.setOnClickListener(v -> {
                            currentPublicsProgress.setVisibility(View.VISIBLE);
                            getCurrentPublics();
                        });
                    }
                }, error -> {
                });
                DashSliderRequest.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
            }
        };
        getCurrentPublicsThread.start(); // start thread
    }

    public void sendRequest(){
        Thread sendRequestThread = new Thread(){//create thread
            @Override
            public void run() {
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, DashboardAds_URL+"?username="+username, null, response -> {
                    for(int i = 0; i < response.length(); i++){
                        SliderUtilsDash sliderUtils = new SliderUtilsDash();
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            sliderUtils.setSliderImageUrl(jsonObject.getString("cat_image"));
                            sliderUtils.setSliderDescription(jsonObject.getString("cat_description"));
                            sliderUtils.setSliderTitle(jsonObject.getString("cat_name"));
                            sliderUtils.setSliderID(jsonObject.getString("cat_id"));
                            sliderUtils.setSliderType(jsonObject.getString("type"));
                            sliderUtils.setSliderTag(jsonObject.getString("tag"));
                            sliderUtils.setSliderAdID(jsonObject.getString("ad_id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sliderImg.add(sliderUtils);
                        adViewed(sliderImg.get(0).getSliderAdID());
                        viewPagerAdapter.notifyDataSetChanged();
                    }
                    getCurrentPublics();
                }, error -> {
                });
                DashSliderRequest.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
            }
        };
        sendRequestThread.start();
    }

    private void loadDashboardFeed(int page,String method){
        Thread loadDashboardFeedThread = new Thread(){//create thread
            @Override
            public void run() {
                final ArrayList<Profilenews_Recycler> items = new ArrayList<>();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, DashboardFeed_URL, response -> {try {
                    JSONArray dashboardfeed = new JSONArray(response);
                    for(int i = 0; i<dashboardfeed.length(); i++){
                        JSONObject dashboardfeedObject = dashboardfeed.getJSONObject(i);

                        String added_by = dashboardfeedObject.getString("added_by");
                        if (SharedPrefManager.getInstance(mContext).isUserBlocked(added_by))continue;
                        int id = dashboardfeedObject.getInt("id");
                        String type = dashboardfeedObject.getString("type");
                        String likes = dashboardfeedObject.getString("likes");
                        String body = dashboardfeedObject.getString("body");
                        String user_to = dashboardfeedObject.getString("user_to");
                        String date_added = dashboardfeedObject.getString("date_added");
                        String user_closed = dashboardfeedObject.getString("user_closed");
                        String deleted = dashboardfeedObject.getString("deleted");
                        String image = dashboardfeedObject.getString("image");
                        String user_id = dashboardfeedObject.getString("user_id");
                        String profile_pic = dashboardfeedObject.getString("profile_pic");
                        String verified = dashboardfeedObject.getString("verified");
                        String online = dashboardfeedObject.getString("online");
                        String nickname = dashboardfeedObject.getString("nickname");
                        String username = dashboardfeedObject.getString("username");
                        String commentcount = dashboardfeedObject.getString("commentcount");
                        String likedbyuserYes = dashboardfeedObject.getString("likedbyuseryes");
                        String form = dashboardfeedObject.getString("form");
                        String edited = dashboardfeedObject.getString("edited");

                        Profilenews_Recycler dashboardfeedResult = new Profilenews_Recycler(id, type, likes, body, added_by, user_to, date_added, user_closed, deleted, image, user_id, profile_pic, verified, online, nickname, username, commentcount, likedbyuserYes, form, edited);
                        items.add(dashboardfeedResult);
                    }

                    if (dashboardfeed.length()==0){
                        dashProgressBar.setVisibility(View.GONE);
                        relLayoutDash2.setVisibility(View.VISIBLE);
                        noPosts.setVisibility(View.VISIBLE);
                    }else{
                        if (currentPage != PAGE_START) dashboardfeedadapter.removeLoading();
                        postsProgress.setVisibility(GONE);
                        dashboardfeedadapter.addItems(items);
                        // check whether is last page or not
                        if (dashboardfeed.length() == pageSize) {
                            dashboardfeedadapter.addLoading();
                        } else {
                            isLastPage = true;
                            //adapter.removeLoading();
                        }
                        isLoading = false;
                        noPosts.setVisibility(GONE);
                    }
                    dashProgressBar.setVisibility(View.GONE);
                    relLayoutDash2.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //TODO FIX THIS
                    dashProgressBar.setVisibility(View.GONE);
                    relLayoutDash2.setVisibility(View.VISIBLE);
                    noPosts.setVisibility(View.VISIBLE);
                    postsProgress.setVisibility(GONE);
                }
                },
                        error -> Toast.makeText(mContext, "Couldn't get dashboard feed!", Toast.LENGTH_SHORT).show()) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("page", String.valueOf(page));
                        params.put("items", String.valueOf(pageSize));
                        params.put("userid", userID);
                        params.put("username", username);
                        params.put("method", method);
                        return params;
                    }
                };
                ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
            }
        };
        loadDashboardFeedThread.start();
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /*@Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }*/

    private void setPlatformImage(String item){
        switch (item) {
            case "Xbox":
                currentPublicsOptions.setImageResource(icons8_xbox_50);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "PlayStation":
                currentPublicsOptions.setImageResource(R.drawable.icons8_playstation_50);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "Steam":
                currentPublicsOptions.setImageResource(R.drawable.icons8_steam_48);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "PC":
                currentPublicsOptions.setImageResource(R.drawable.icons8_workstation_48);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "Mobile":
                currentPublicsOptions.setImageResource(R.drawable.icons8_mobile_48);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "Switch":
                currentPublicsOptions.setImageResource(R.drawable.icons8_nintendo_switch_48);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "Cross-Platform":
                currentPublicsOptions.setImageResource(R.drawable.icons8_collect_40);
                filterText.setVisibility(View.VISIBLE);
                break;
            case "Other":
                currentPublicsOptions.setImageResource(R.drawable.icons8_question_mark_64);
                filterText.setVisibility(View.VISIBLE);
                break;
            default:
                currentPublicsOptions.setImageResource(R.drawable.ic_ellipses);
                filterText.setVisibility(GONE);
                break;
        }
    }

    private void postsQueryButtonClicked(Button click){
        postsProgress.setVisibility(View.VISIBLE);
        if (click == allPostsButton){
            dashboardfeedRecyclerList.clear();
            if (dashboardfeedadapter!=null){
                dashboardfeedadapter.notifyDataSetChanged();
            }
            int colorFrom = getResources().getColor(R.color.grey_80);
            int colorTo = getResources().getColor(R.color.green);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(750);
            colorAnimation.addUpdateListener(animator -> allPostsButton.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation.start();
            ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            colorAnimation2.setDuration(750);
            colorAnimation2.addUpdateListener(animator -> followingPostsButton.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation2.start();
            clicked = "all";
            currentPage = 1;
            loadDashboardFeed(currentPage,clicked);
        }
        if (click == followingPostsButton){
            dashboardfeedRecyclerList.clear();
            if (dashboardfeedadapter!=null){
                dashboardfeedadapter.notifyDataSetChanged();
            }
            int colorTo = getResources().getColor(R.color.grey_80);
            int colorFrom = getResources().getColor(R.color.green);
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            colorAnimation.setDuration(250);
            colorAnimation.addUpdateListener(animator -> allPostsButton.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation.start();
            ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(), colorTo, colorFrom);
            colorAnimation2.setDuration(250);
            colorAnimation2.addUpdateListener(animator -> followingPostsButton.setBackgroundColor((int) animator.getAnimatedValue()));
            colorAnimation2.start();
            clicked = "following";
            currentPage = 1;
            loadDashboardFeed(currentPage,clicked);
        }
    }

    /*private void clearBackStack() {
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStackImmediate(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }*/

}