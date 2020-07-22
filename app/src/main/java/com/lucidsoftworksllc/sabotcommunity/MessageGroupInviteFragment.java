package com.lucidsoftworksllc.sabotcommunity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

import static android.view.View.GONE;

public class MessageGroupInviteFragment extends Fragment implements NewGroupMessageUserAdapter.AdapterCallback,UserListGroupMessageAdapter.AdapterCallback {

    private SearchView messageSearch;
    private RecyclerView recyclerSearch, recyclerUsersToInvite, recyclerUsersInGroup;
    private Button btnSubmit;
    private UserApiInterface apiInterface;
    private List<User> users;
    private TextView noPlayers;
    private NewGroupMessageUserAdapter adapter;
    private UserListGroupMessageAdapter adapter2;
    private UserListGroupMessageCurrentAdapter adapter3;
    private ProgressBar groupMessageInviteProgressBar, currentPlayersProgress;
    private Context mContext;
    private List<Search_Recycler> searchRecyclerList;
    private List<UserListRecycler> userListRecycler,playersInGroupRecycler;
    private ArrayList<String> usersToInvite;
    private RecyclerView.LayoutManager layoutManager, layoutManager2, layoutManager3;
    private List<UserListRecycler> userRecyclerList;
    private String query,queryID,deviceUserID,deviceUsername;
    public static final String URL_INVITE_SUBMIT = Constants.ROOT_URL + "messages.php/invite_players";
    public static final String URL_CURRENT_PLAYERS = Constants.ROOT_URL+"message_user_list_query.php";

    @Override
    public void onMethodCallback(String user_id, String profile_pic, String nickname, String username, String verified, String online) {
        boolean isObjectExist = false;
        for (int i = 0; i < userListRecycler.size(); i++) {
            String thingy = userListRecycler.get(i).getUser_id();
            if (thingy.equals(user_id)) {
                isObjectExist = true;
                break;
            }
        }
        for (int i = 0; i < playersInGroupRecycler.size(); i++) {
            String thingy = playersInGroupRecycler.get(i).getUser_id();
            if (thingy.equals(user_id)) {
                isObjectExist = true;
                break;
            }
        }
        if (!isObjectExist) {
            boolean isObjectExist2 = false;
            for (int i = 0; i < playersInGroupRecycler.size(); i++) {
                String thingy = playersInGroupRecycler.get(i).getUser_id();
                if (thingy.equals(user_id)) {
                    isObjectExist2 = true;
                    break;
                }
            }
            if (!isObjectExist2){
                UserListRecycler userToAdd = new UserListRecycler(null, user_id, profile_pic, nickname, username, verified, "", null);
                userListRecycler.add(userToAdd);
                adapter2.notifyDataSetChanged();
                usersToInvite.add(username);
                Toast.makeText(mContext, "Adding @" + username+" to invite queue", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "@"+username+" is already in the group!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(mContext, "Already added @" + username + " to invite queue!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMethodCallbackUserList(int position, String username) {
        userListRecycler.remove(position);
        adapter2.notifyDataSetChanged();
        usersToInvite.remove(username);
        Toast.makeText(mContext, "Removed @" + username, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newGroupInviteRootView = inflater.inflate(R.layout.fragment_groupmessage_invite_players, null);

        messageSearch = newGroupInviteRootView.findViewById(R.id.messageSearch);
        recyclerUsersInGroup = newGroupInviteRootView.findViewById(R.id.recyclerUsersInGroup);
        recyclerSearch = newGroupInviteRootView.findViewById(R.id.recyclerSearch);
        recyclerUsersToInvite = newGroupInviteRootView.findViewById(R.id.recyclerUsersToInvite);
        noPlayers = newGroupInviteRootView.findViewById(R.id.noPlayers);
        btnSubmit = newGroupInviteRootView.findViewById(R.id.btnSubmit);
        currentPlayersProgress = newGroupInviteRootView.findViewById(R.id.currentPlayersProgress);
        groupMessageInviteProgressBar = newGroupInviteRootView.findViewById(R.id.groupMessageInviteProgressBar);
        mContext = getActivity();

        if((getArguments())!=null) {
            query = "group_players_added";
            queryID = getArguments().getString("group_id");
            deviceUserID = SharedPrefManager.getInstance(mContext).getUserID();
            deviceUsername = SharedPrefManager.getInstance(mContext).getUsername();
        }
        assert mContext != null;

        searchRecyclerList = new ArrayList<>();
        userListRecycler = new ArrayList<>();
        playersInGroupRecycler = new ArrayList<>();
        usersToInvite = new ArrayList<>();
        layoutManager = new LinearLayoutManager(mContext);
        recyclerSearch.setLayoutManager(layoutManager);
        layoutManager2 = new LinearLayoutManager(mContext);
        recyclerUsersToInvite.setLayoutManager(layoutManager2);
        adapter2 = new UserListGroupMessageAdapter(userListRecycler, mContext, MessageGroupInviteFragment.this);
        recyclerUsersToInvite.setAdapter(adapter2);
        layoutManager3 = new LinearLayoutManager(mContext);

        btnSubmit.setOnClickListener(v -> {
            if (!usersToInvite.isEmpty()) {
                groupMessageInviteProgressBar.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                String inviteArray = usersToInvite.toString();
                InviteUsers(queryID, inviteArray);
            } else {
                Toast.makeText(mContext, "Group name must be 3-25 characters long", Toast.LENGTH_SHORT).show();
            }
        });

        messageSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser("users_only", query);
                if (!(query).isEmpty()) {
                    recyclerSearch.setVisibility(View.VISIBLE);
                } else {
                    recyclerSearch.setVisibility(GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users_only", newText);
                    if (!(newText).isEmpty()) {
                        recyclerSearch.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSearch.setVisibility(GONE);
                    }
                }, 100);
                return false;
            }
        });

        getPlayers();
        return newGroupInviteRootView;
    }

    private void InviteUsers(String group_id, String invite_array){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_INVITE_SUBMIT,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("error").equals("false")){
                            Toast.makeText(mContext, "Invited player(s)!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStackImmediate();
                        }else{
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            groupMessageInviteProgressBar.setVisibility(GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                params.put("invite_array", invite_array);
                params.put("username", deviceUsername);
                params.put("user_id", deviceUserID);
                return params;
            }
        };
        ((ChatActivity)mContext).addToRequestQueue(stringRequest);
    }

    private void getPlayers(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_CURRENT_PLAYERS+"?queryid="+queryID+"&query="+query+"&userid="+deviceUserID+"&deviceusername="+deviceUsername, response -> {
            try {
                JSONArray profilepublicsnews = new JSONArray(response);
                for(int i = 0; i<profilepublicsnews.length(); i++){
                    JSONObject profilenewsObject = profilepublicsnews.getJSONObject(i);
                    String username = profilenewsObject.getString("username");
                    String id = profilenewsObject.getString("id");
                    String user_id = profilenewsObject.getString("user_id");
                    String profile_pic = profilenewsObject.getString("profile_pic");
                    String nickname = profilenewsObject.getString("nickname");
                    String verified = profilenewsObject.getString("verified");
                    String online = profilenewsObject.getString("online");
                    String desc = profilenewsObject.getString("desc");

                    UserListRecycler result = new UserListRecycler(id, user_id, profile_pic, nickname, username, verified, online, desc);
                    playersInGroupRecycler.add(result);
                }
                recyclerUsersInGroup.setLayoutManager(layoutManager3);
                adapter3 = new UserListGroupMessageCurrentAdapter(playersInGroupRecycler, mContext);
                recyclerUsersInGroup.setAdapter(adapter3);
                if (profilepublicsnews.length()==0){
                    noPlayers.setVisibility(View.VISIBLE);
                }
                currentPlayersProgress.setVisibility(GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> currentPlayersProgress.setVisibility(GONE));
        ((ChatActivity)mContext).addToRequestQueue(stringRequest);
    }

    public void fetchUser(String type, String key) {
        apiInterface = UsersApiClient.getApiClient().create(UserApiInterface.class);
        Call<List<User>> call = apiInterface.getUsers(type, key);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, retrofit2.Response<List<User>> response) {
                users = response.body();
                adapter = new NewGroupMessageUserAdapter(users, mContext, MessageGroupInviteFragment.this);
                recyclerSearch.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                recyclerSearch.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Error\n" + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        final SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser("users_only", query);
                if (!(query).isEmpty()) {
                    recyclerSearch.setVisibility(View.VISIBLE);
                } else {
                    recyclerSearch.setVisibility(GONE);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users_only", newText);
                    if (!(newText).isEmpty()) {
                        recyclerSearch.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSearch.setVisibility(GONE);
                    }
                }, 100);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}