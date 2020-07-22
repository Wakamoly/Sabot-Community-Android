package com.lucidsoftworksllc.sabotcommunity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    private static final String Publics_URL = Constants.ROOT_URL+"readUsers_api.php";

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> users;
    private SearchAdapter adapter;
    private UserApiInterface apiInterface;
    private Context mContext;
    private String userID;
    private TextView textViewOthersWhoFollow,textViewGameName,textViewNoFollows;
    private SwipeRefreshLayout searchSwipe;
    private ProgressBar progressBar;
    private List<UserListRecycler> userRecyclerList;
    private List<Search_Recycler> searchRecyclerList;
    private UserListAdapter popularAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerPopular;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View searchRootView = inflater.inflate(R.layout.fragment_search, null);
        setHasOptionsMenu(true);
        textViewGameName = searchRootView.findViewById(R.id.textViewGameName);
        textViewOthersWhoFollow = searchRootView.findViewById(R.id.textViewOthersWhoFollow);
        textViewNoFollows = searchRootView.findViewById(R.id.textViewNoFollows);
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        androidx.appcompat.widget.Toolbar toolbar = searchRootView.findViewById(R.id.searchToolBar);
        ((FragmentContainer)mContext).setSupportActionBar(toolbar);
        userRecyclerList = new ArrayList<>();
        searchRecyclerList = new ArrayList<>();
        recyclerPopular = searchRootView.findViewById(R.id.recyclerPopular);
        recyclerPopular.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerPopular.setLayoutManager(linearLayoutManager);
        progressBar = searchRootView.findViewById(R.id.progressBar);
        searchSwipe = searchRootView.findViewById(R.id.searchSwipe);
        recyclerView = searchRootView.findViewById(R.id.recyclerSearch);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        loadPopular();
        fetchUser("users", "");
        searchSwipe.setOnRefreshListener(() -> {
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof SearchFragment) {
                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
        });
        return searchRootView;
    }

    public void fetchUser(String type, String key){
        apiInterface = UsersApiClient.getApiClient().create(UserApiInterface.class);
        Call<List<User>> call = apiInterface.getUsers(type, key);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                progressBar.setVisibility(View.GONE);
                users = response.body();
                adapter = new SearchAdapter(users, mContext);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                searchSwipe.setRefreshing(false);
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                searchSwipe.setVisibility(View.INVISIBLE);
                searchSwipe.setRefreshing(false);
            }
        });
    }

    private void loadPopular(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Publics_URL+"?userid="+userID, (com.android.volley.Response.Listener<String>) response -> {
            JSONObject obj;
            try {
                obj = new JSONObject(response);
                JSONArray profiles;
                JSONObject gameinfo = obj.getJSONObject("game");
                if (obj.getJSONArray("users").length()!=0) {
                    profiles = obj.getJSONArray("users");
                    String gamename = gameinfo.getString("game_name");
                    final String game_id = gameinfo.getString("id");
                    if (gamename.equals("null")||game_id.isEmpty()){
                        textViewNoFollows.setVisibility(View.VISIBLE);
                        textViewGameName.setVisibility(View.GONE);
                        textViewOthersWhoFollow.setVisibility(View.GONE);
                    }else{
                        textViewNoFollows.setVisibility(View.GONE);
                        textViewGameName.setVisibility(View.VISIBLE);
                        textViewOthersWhoFollow.setVisibility(View.VISIBLE);
                        textViewGameName.setText(gamename);
                        textViewGameName.setOnClickListener((View.OnClickListener) v -> {
                            FragmentPublicsCat ldf = new FragmentPublicsCat();
                            Bundle args = new Bundle();
                            args.putString("PublicsId", game_id);
                            ldf.setArguments(args);
                            requireActivity().getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        });
                        for (int i = 0; i < profiles.length(); i++) {
                            JSONObject profilenewsObject = profiles.getJSONObject(i);
                            String username = profilenewsObject.getString("username");
                            if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))
                                continue;
                            String id = profilenewsObject.getString("id");
                            String user_id = profilenewsObject.getString("user_id");
                            String profile_pic = profilenewsObject.getString("profile_pic");
                            String nickname = profilenewsObject.getString("nickname");
                            String verified = profilenewsObject.getString("verified");
                            String online = profilenewsObject.getString("online");
                            String desc = profilenewsObject.getString("desc");
                            UserListRecycler publicsTopicResult = new UserListRecycler(id, user_id, profile_pic, nickname, username, verified, online, desc);
                            userRecyclerList.add(publicsTopicResult);
                        }
                        popularAdapter = new UserListAdapter(userRecyclerList, mContext);
                        recyclerPopular.setAdapter(popularAdapter);
                        progressBar.setVisibility(View.GONE);
                    }
                }else{
                    textViewNoFollows.setVisibility(View.VISIBLE);
                    textViewGameName.setVisibility(View.GONE);
                    textViewOthersWhoFollow.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, (com.android.volley.Response.ErrorListener) error -> Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);
        final SearchManager searchManager = (SearchManager) mContext.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        }
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser("users", query);
                if (!(query).isEmpty()) {
                    searchSwipe.setVisibility(View.VISIBLE);
                } else {
                    searchSwipe.setVisibility(View.INVISIBLE);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users", newText);
                    if (!(newText).isEmpty()) {
                        searchSwipe.setVisibility(View.VISIBLE);
                    } else {
                        searchSwipe.setVisibility(View.INVISIBLE);
                    }
                }, 100);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}