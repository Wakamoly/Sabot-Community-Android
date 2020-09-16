package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lucidsoftworksllc.sabotcommunity.PaginationOnScroll.PAGE_SIZE;
import static com.lucidsoftworksllc.sabotcommunity.PaginationOnScroll.PAGE_START;

public class NotificationsFragment extends Fragment {
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int pageSize = PAGE_SIZE;
    private boolean isLoading = false;

    private static final String TAG = "NotificationsFragment";
    private static final String Notifications_URL = Constants.ROOT_URL+"getNotifications.php";
    private static final String SET_ALL_READ = Constants.ROOT_URL+"set_all_noti_read.php";

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private List<Notifications_Recycler> notifications;
    private NotificationsAdapter adapter;
    private String userID, username, deviceUsername;
    private TextView none, badgenum;
    private ImageView notiMenu;
    private SwipeRefreshLayout notificationSwipe;
    private RelativeLayout notiLayout;
    private ProgressBar progressBar;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View notificationsRootView = inflater.inflate(R.layout.fragment_notifications, null);
        setHasOptionsMenu(true);

        none = notificationsRootView.findViewById(R.id.noNotifications);
        progressBar = notificationsRootView.findViewById(R.id.progressBar);
        notificationSwipe = notificationsRootView.findViewById(R.id.notificationsSwipe);
        recyclerView = notificationsRootView.findViewById(R.id.recyclerNotifications);
        notiLayout = notificationsRootView.findViewById(R.id.notiLayout);
        notiMenu = notificationsRootView.findViewById(R.id.notiMenu);
        mContext = getActivity();
        deviceUsername = SharedPrefManager.getInstance(mContext).getUsername();
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        notifications = new ArrayList<>();

        adapter = new NotificationsAdapter(notifications,mContext);
        recyclerView.setAdapter(adapter);
        loadNotifications(currentPage);
        recyclerView.addOnScrollListener(new PaginationOnScroll(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                loadNotifications(currentPage);
            }
            @Override public boolean isLastPage() {
                return isLastPage;
            }
            @Override public boolean isLoading() {
                return isLoading;
            }
        });

        notificationSwipe.setOnRefreshListener(() -> {
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof NotificationsFragment) {
                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            notificationSwipe.setRefreshing(false);
        });

        notiMenu.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater1 = popup.getMenuInflater();
            inflater1.inflate(R.menu.noti_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuSetOpened) {
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SET_ALL_READ, response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                if (currentFragment instanceof NotificationsFragment) {
                                    FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.detach(currentFragment);
                                    fragTransaction.attach(currentFragment);
                                    fragTransaction.commit();
                                }
                                notificationSwipe.setRefreshing(false);
                            } else {
                                Toast.makeText(mContext, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
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
                    ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
                }
                return true;
            });
            popup.show();
        });

        return notificationsRootView;
    }

    private void loadNotifications(int page){
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        final ArrayList<Notifications_Recycler> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Notifications_URL,
                response -> {
                    try {
                        JSONArray notificationsArray = new JSONArray(response);
                        if(notificationsArray.length() == 0){
                            none.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                        }
                        for(int i = 0; i<notificationsArray.length(); i++){
                            JSONObject notificationsObject = notificationsArray.getJSONObject(i);
                            String id = notificationsObject.getString("id");
                            String user_to = notificationsObject.getString("user_to");
                            String user_from = notificationsObject.getString("user_from");
                            if (SharedPrefManager.getInstance(mContext).isUserBlocked(user_from))continue;
                            String message = notificationsObject.getString("message");
                            String type = notificationsObject.getString("type");
                            String link = notificationsObject.getString("link");
                            String datetime = notificationsObject.getString("datetime");
                            String opened = notificationsObject.getString("opened");
                            String viewed = notificationsObject.getString("viewed");
                            String user_id = notificationsObject.getString("user_id");
                            String profile_pic = notificationsObject.getString("profile_pic");
                            String nickname = notificationsObject.getString("nickname");
                            String verified = notificationsObject.getString("verified");
                            String last_online = notificationsObject.getString("last_online");

                            Notifications_Recycler notificationsResult = new Notifications_Recycler(id, user_to, user_from, message, type, link, datetime, opened, viewed, user_id, profile_pic, nickname, verified, last_online);
                            items.add(notificationsResult);
                        }
                        notiLayout.setVisibility(View.VISIBLE);
                        if (currentPage != PAGE_START) adapter.removeLoading();
                        progressBar.setVisibility(View.GONE);
                        adapter.addItems(items);
                        if (notificationsArray.length() == pageSize) {
                            adapter.addLoading();
                        } else {
                            isLastPage = true;
                            //adapter.removeLoading();
                        }
                        isLoading = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                        //TODO FIX THIS
                        notiLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        none.setVisibility(View.VISIBLE);
                    }
                },
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("page", String.valueOf(page));
                params.put("items", String.valueOf(pageSize));
                params.put("userid", userID);
                params.put("username", username);
                return params;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}