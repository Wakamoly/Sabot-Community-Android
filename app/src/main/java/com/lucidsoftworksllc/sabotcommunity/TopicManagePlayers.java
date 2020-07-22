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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TopicManagePlayers  extends Fragment{

    private static final String PUBLICS_MEMBERS = Constants.ROOT_URL+"publics_players.php";

    private TextView clanNameTop,noMemberRequests,noMembers;
    private String topicID,userID,username,permission;
    private Context mContext;
    private ImageView backArrow;
    private RecyclerView recyclerMembers,recyclerMembersJoined;
    private SwipeRefreshLayout manageMembersSwipe;
    private ProgressBar progressBar;
    private RelativeLayout memberRequestsLayout;
    private LinearLayoutManager linearLayoutManager;
    private PublicsPlayersAdapter adapter;
    private PublicsPlayersNonAdminAdapter adapterNonAdmin;
    private List<Publics_Players_Recycler> requestsRecyclerList,membersRecyclerList;

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
        clanNameTop.setText(R.string.nav_publics);
        assert getArguments() != null;
        topicID = getArguments().getString("topic_id");
        permission = getArguments().getString("permission");
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        requestsRecyclerList = new ArrayList<>();
        recyclerMembers.setHasFixedSize(true);
        recyclerMembers.setLayoutManager(new LinearLayoutManager(mContext));
        membersRecyclerList = new ArrayList<>();
        recyclerMembersJoined.setHasFixedSize(true);
        recyclerMembersJoined.setLayoutManager(new LinearLayoutManager(mContext));
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        loadMembers();
        return manageMembersRootView;
    }

    private void loadMembers(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, PUBLICS_MEMBERS+"?topicid="+topicID+"&username="+username+"&permission="+permission, response -> {
            JSONObject obj;
            try {
                obj = new JSONObject(response);
                JSONArray members = obj.getJSONArray("members");
                for(int i = 0; i<members.length(); i++){
                    JSONObject membersObject = members.getJSONObject(i);
                    String username = membersObject.getString("username");
                    if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))continue;
                    String id = membersObject.getString("id");
                    String profile_pic = membersObject.getString("profile_pic");
                    String nickname = membersObject.getString("nickname");
                    String userid = membersObject.getString("userid");
                    String position = membersObject.getString("position");
                    Publics_Players_Recycler membersResult = new Publics_Players_Recycler(id,profile_pic,nickname,userid,username,topicID,position);
                    membersRecyclerList.add(membersResult);
                    progressBar.setVisibility(View.GONE);
                    manageMembersSwipe.setRefreshing(false);
                }
                if (members.length()==0){
                    noMembers.setVisibility(View.VISIBLE);
                }
                linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                recyclerMembersJoined.setLayoutManager(linearLayoutManager);
                if (permission.equals("admin")) {
                    adapter = new PublicsPlayersAdapter(mContext, membersRecyclerList);
                    recyclerMembersJoined.setAdapter(adapter);
                }else{
                    adapterNonAdmin = new PublicsPlayersNonAdminAdapter(mContext, membersRecyclerList);
                    recyclerMembersJoined.setAdapter(adapterNonAdmin);
                }
                if (!obj.isNull("memberrequests")&&permission.equals("admin")) {
                    JSONArray memberrequests = obj.getJSONArray("memberrequests");
                    for (int i = 0; i < memberrequests.length(); i++) {
                        JSONObject membersObject = memberrequests.getJSONObject(i);
                        String username = membersObject.getString("username");
                        if (SharedPrefManager.getInstance(mContext).isUserBlocked(username))
                            continue;
                        String id = membersObject.getString("id");
                        String profile_pic = membersObject.getString("profile_pic");
                        String nickname = membersObject.getString("nickname");
                        String userid = membersObject.getString("userid");
                        String position = membersObject.getString("position");
                        Publics_Players_Recycler membersResult = new Publics_Players_Recycler(id, profile_pic, nickname, userid, username, topicID, position);
                        requestsRecyclerList.add(membersResult);
                    }
                    progressBar.setVisibility(View.GONE);
                    manageMembersSwipe.setRefreshing(false);
                    if (memberrequests.length() > 0) {
                        memberRequestsLayout.setVisibility(View.VISIBLE);
                    }
                    linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
                    recyclerMembers.setLayoutManager(linearLayoutManager);
                    adapter = new PublicsPlayersAdapter(mContext, requestsRecyclerList);
                    recyclerMembers.setAdapter(adapter);
                }
                progressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
