package com.lucidsoftworksllc.sabotcommunity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class ClansListFragment extends Fragment {

    public static final String URL_JOINED_CLANS = ROOT_URL + "joined_clans.php";

    private TextView noClans;
    private SwipeRefreshLayout clansSwipe;
    private ProgressBar clansProgressBar;
    private RelativeLayout clansLayout;
    private Context mContext;
    private ProgressDialog dialog;
    private String deviceUserID, deviceUsername;
    private ImageView clansMenu;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private List<Clans_Recycler> clans;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View clansRootView = inflater.inflate(R.layout.fragment_clans_list, null);

        noClans = clansRootView.findViewById(R.id.noClans);
        clansSwipe = clansRootView.findViewById(R.id.clansSwipe);
        clansProgressBar = clansRootView.findViewById(R.id.clansProgressBar);
        clansLayout = clansRootView.findViewById(R.id.clansLayout);
        recyclerView = clansRootView.findViewById(R.id.recyclerClans);
        clansMenu = clansRootView.findViewById(R.id.clansMenu);
        mContext = getActivity();
        deviceUserID = SharedPrefManager.getInstance(mContext).getUserID();
        deviceUsername = SharedPrefManager.getInstance(mContext).getUsername();
        clans = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        dialog = new ProgressDialog(mContext);
        dialog.setMessage("Loading joined clans...");
        dialog.show();
        adapter = new JoinedClansAdapter(mContext, clans);
        recyclerView.setAdapter(adapter);
        clansMenu.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(mContext, view);
            MenuInflater inflater1 = popup.getMenuInflater();
            inflater1.inflate(R.menu.clans_list_top_options_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menuNewClan) {
                    NewClanFragment ldf = new NewClanFragment();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }
                return true;
            });
            popup.show();
        });

        clansSwipe.setOnRefreshListener(() -> {
            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment instanceof ClansListFragment) {
                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragTransaction.detach(currentFragment);
                fragTransaction.attach(currentFragment);
                fragTransaction.commit();
            }
            clansSwipe.setRefreshing(false);
        });
        loadJoinedClans();
        return clansRootView;
    }

    private void loadJoinedClans(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_JOINED_CLANS+"?username="+deviceUsername+"&userid="+deviceUserID,
                response -> {
                    try {
                        JSONObject res = new JSONObject(response);
                        JSONArray thread = res.getJSONArray("clans");
                        for (int i = 0; i < thread.length(); i++) {
                            JSONObject obj = thread.getJSONObject(i);
                            String position = obj.getString("position");
                            String tag = obj.getString("tag");
                            String name = obj.getString("name");
                            String num_members = obj.getString("num_members");
                            String insignia = obj.getString("insignia");
                            String games = obj.getString("games");
                            String id = obj.getString("id");
                            String avg = obj.getString("avg");
                            Clans_Recycler clansObject = new Clans_Recycler(position,tag,name,num_members,insignia,games,id,avg);
                            clans.add(clansObject);
                        }
                        if (thread.length()==0){
                            noClans.setVisibility(View.VISIBLE);
                            noClans.setOnClickListener(v -> {
                                NewClanFragment ldf = new NewClanFragment();
                                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                            });
                        }
                        dialog.dismiss();
                        clansProgressBar.setVisibility(View.GONE);
                        clansLayout.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {});
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}
