package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ClanManageMembers extends Fragment {

    private static final String CLAN_MEMBER_REQUESTS = Constants.ROOT_URL+"clan_member_requests.php";
    private static final String CLAN_MEMBERS = Constants.ROOT_URL+"clan_members.php";
    private TextView clanNameTop,noMemberRequests,noMembers;
    private String clanID,clanname,userID,username,clantag;
    private Context mContext;
    private ImageView backArrow;
    private RecyclerView recyclerMembers,recyclerMembersJoined;
    private SwipeRefreshLayout manageMembersSwipe;
    private ProgressBar progressBar;
    private RelativeLayout memberRequestsLayout;
    private LinearLayoutManager linearLayoutManager;
    private ClanMembersAdapter adapter;
    private List<Clan_Members_Recycler> requestsRecyclerList,membersRecyclerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View manageMembersRootView = inflater.inflate(R.layout.fragment_clan_member_manage, null);

        clanNameTop = manageMembersRootView.findViewById(R.id.clanNameTop);
        backArrow = manageMembersRootView.findViewById(R.id.backArrow);
        recyclerMembers = manageMembersRootView.findViewById(R.id.recyclerMembers);
        manageMembersSwipe = manageMembersRootView.findViewById(R.id.manageMembersSwipe);
        progressBar = manageMembersRootView.findViewById(R.id.progressBar);
        noMemberRequests = manageMembersRootView.findViewById(R.id.noMemberRequests);
        recyclerMembersJoined = manageMembersRootView.findViewById(R.id.recyclerMembersJoined);
        noMembers = manageMembersRootView.findViewById(R.id.noMembers);
        memberRequestsLayout = manageMembersRootView.findViewById(R.id.memberRequestsLayout);
        assert getArguments() != null;
        clanID = getArguments().getString("ClanId");
        clanname = getArguments().getString("Clanname");
        clantag = getArguments().getString("Clantag");
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        memberRequestsLayout.setVisibility(View.VISIBLE);
        clanNameTop.setText(clanname);
        requestsRecyclerList = new ArrayList<>();
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(mContext));
        membersRecyclerList = new ArrayList<>();
        recyclerMembersJoined.setHasFixedSize(true);
        recyclerMembersJoined.setLayoutManager(new LinearLayoutManager(mContext));
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        loadMemberRequests();
        loadMembers();
        return manageMembersRootView;
    }

    private void loadMemberRequests(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CLAN_MEMBER_REQUESTS+"?userid="+userID+"&clanid="+clanID+"&username="+username, response -> {
            try {
                JSONArray members = new JSONArray(response);
                for(int i = 0; i<members.length(); i++){
                    JSONObject membersObject = members.getJSONObject(i);
                    String username = membersObject.getString("username");
                    if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))continue;
                    String id = membersObject.getString("id");
                    String profile_pic = membersObject.getString("profile_pic");
                    String nickname = membersObject.getString("nickname");
                    String userid = membersObject.getString("userid");
                    Clan_Members_Recycler membersResult = new Clan_Members_Recycler(id,profile_pic,nickname,userid,username,clanID,clantag,"request");
                    requestsRecyclerList.add(membersResult);
                    progressBar.setVisibility(View.GONE);
                    manageMembersSwipe.setRefreshing(false);
                }
                if (members.length()==0){
                    noMemberRequests.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                recyclerMembers.setLayoutManager(linearLayoutManager);
                adapter = new ClanMembersAdapter(mContext, requestsRecyclerList);
                recyclerMembers.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadMembers(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, CLAN_MEMBERS+"?userid="+userID+"&clanid="+clanID+"&username="+username, response -> {
            try {
                JSONArray members = new JSONArray(response);
                for(int i = 0; i<members.length(); i++){
                    JSONObject membersObject = members.getJSONObject(i);
                    String username = membersObject.getString("username");
                    if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))continue;
                    String id = membersObject.getString("id");
                    String profile_pic = membersObject.getString("profile_pic");
                    String nickname = membersObject.getString("nickname");
                    String userid = membersObject.getString("userid");
                    String position = membersObject.getString("position");
                    Clan_Members_Recycler membersResult = new Clan_Members_Recycler(id,profile_pic,nickname,userid,username,clanID,clantag,position);
                    membersRecyclerList.add(membersResult);
                    progressBar.setVisibility(View.GONE);
                    manageMembersSwipe.setRefreshing(false);
                }
                if (members.length()==0){
                    noMembers.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                recyclerMembersJoined.setLayoutManager(linearLayoutManager);
                adapter = new ClanMembersAdapter(mContext, membersRecyclerList);
                recyclerMembersJoined.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
