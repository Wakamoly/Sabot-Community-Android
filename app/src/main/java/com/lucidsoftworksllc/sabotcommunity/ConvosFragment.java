package com.lucidsoftworksllc.sabotcommunity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class ConvosFragment extends Fragment {

    public static final String URL_FETCH_CONVOS = ROOT_URL + "messages.php/convos";

    private String deviceUsername,deviceUserID;
    private ImageView messagesNew, messagesMenu;
    private TextView noPosts;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<ConvosHelper> messages;
    private Context mCtx;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View convosRootView = inflater.inflate(R.layout.fragment_convos, null);
        mCtx = getActivity();
        messagesNew = convosRootView.findViewById(R.id.messagesNew);
        messagesMenu = convosRootView.findViewById(R.id.messagesMenu);
        noPosts = convosRootView.findViewById(R.id.noPosts);
        dialog = new ProgressDialog(mCtx);
        dialog.setMessage("Loading conversations...");
        dialog.show();
        recyclerView = convosRootView.findViewById(R.id.chatConvos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mCtx);
        recyclerView.setLayoutManager(layoutManager);
        messages = new ArrayList<>();
        deviceUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        deviceUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        messagesMenu.setOnClickListener(v -> {
            requireActivity().finish();
            startActivity(new Intent(getActivity(), FragmentContainer.class));
        });
        messagesNew.setOnClickListener(v -> {
            NewMessageFragment ldf = new NewMessageFragment();
            ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
        });
        fetchConvos();
        return convosRootView;
    }
    private void fetchConvos() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_CONVOS+"?username="+deviceUsername+"&userid="+deviceUserID,
                response -> {
                    try {
                        dialog.dismiss();
                        JSONObject res = new JSONObject(response);
                        JSONArray thread = res.getJSONArray("messages");
                        for (int i = 0; i < thread.length(); i++) {
                            JSONObject obj = thread.getJSONObject(i);
                            String sent_by = obj.getString("sent_by");
                            String profile_pic = obj.getString("profile_pic");
                            String body_split = obj.getString("body_split");
                            String time_message = obj.getString("time_message");
                            String latest_profile_pic = obj.getString("latest_profile_pic");
                            String nickname = obj.getString("nickname");
                            String verified = obj.getString("verified");
                            String last_online = obj.getString("last_online");
                            String viewed = obj.getString("viewed");
                            String id = obj.getString("id");
                            String user_from = obj.getString("user_from");
                            String type = obj.getString("type");
                            String group_id = obj.getString("group_id");
                            ConvosHelper messageObject = new ConvosHelper(sent_by,body_split,time_message,latest_profile_pic,profile_pic,nickname,verified,last_online,viewed,id, user_from,type,group_id);
                            messages.add(messageObject);
                        }
                        if(thread.length()==0){
                            noPosts.setVisibility(View.VISIBLE);
                        }
                        adapter = new ConvosThreadAdapter(mCtx, messages);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {});
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

}