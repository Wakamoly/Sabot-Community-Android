package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class UserListFragment extends Fragment {

    private ImageView backButton;
    private TextView queryName, nothingToShow;
    private ProgressBar progressBar;
    private RecyclerView recyclerSeeAll;
    private RelativeLayout profileErrorScreen;
    private Context mContext;
    private String deviceUserID,deviceUsername,query,queryID;
    private List<UserListRecycler> userRecyclerList;
    private UserListAdapter userlistAdapter;
    private static final String SEE_ALL_URL = Constants.ROOT_URL+"user_list_query.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View userListRootView = inflater.inflate(R.layout.fragment_user_list, null);
        backButton = userListRootView.findViewById(R.id.backButton);
        queryName = userListRootView.findViewById(R.id.queryName);
        nothingToShow = userListRootView.findViewById(R.id.nothingToShow);
        progressBar = userListRootView.findViewById(R.id.progressBar);
        profileErrorScreen = userListRootView.findViewById(R.id.profileErrorScreen);
        recyclerSeeAll = userListRootView.findViewById(R.id.recyclerSeeAll);
        mContext = getActivity();
        deviceUserID = SharedPrefManager.getInstance(mContext).getUserID();
        deviceUsername = SharedPrefManager.getInstance(mContext).getUsername();
        userRecyclerList =  new ArrayList<>();
        recyclerSeeAll.setHasFixedSize(true);
        recyclerSeeAll.setLayoutManager(new LinearLayoutManager(mContext));
        if((getArguments())!=null) {
            query = getArguments().getString("query");
            queryID = getArguments().getString("queryID");
        } else {
            profileErrorScreen.setVisibility(View.VISIBLE);
        }
        switch (query) {
            case "connections":
                queryName.setText(getString(R.string.connections));
                break;
            case "followers":
                queryName.setText(getString(R.string.label_followers));
                break;
            case "following":
                queryName.setText(getString(R.string.followingTextProfile));
                break;
            case "comment":
                queryName.setText(getString(R.string.comment_likes));
                break;
            case "post":
                queryName.setText(getString(R.string.post_likes));
                break;
        }
        backButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStackImmediate();
        });
        getQuery();
        return userListRootView;
    }

    private void getQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, SEE_ALL_URL+"?queryid="+queryID+"&query="+query+"&userid="+deviceUserID+"&deviceusername="+deviceUsername, response -> {
            try {
                JSONArray profilepublicsnews = new JSONArray(response);
                for(int i = 0; i<profilepublicsnews.length(); i++){
                    JSONObject profilenewsObject = profilepublicsnews.getJSONObject(i);
                    String username = profilenewsObject.getString("username");
                    if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))continue;
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
                if (profilepublicsnews.length()==0){
                    nothingToShow.setVisibility(View.VISIBLE);
                }
                userlistAdapter = new UserListAdapter(userRecyclerList,mContext);
                recyclerSeeAll.setAdapter(userlistAdapter);
                progressBar.setVisibility(GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            profileErrorScreen.setVisibility(View.VISIBLE);
            progressBar.setVisibility(GONE);
        });
        Volley.newRequestQueue(mContext).add(stringRequest);
    }

}
