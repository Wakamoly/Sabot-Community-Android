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
import java.util.Objects;

import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class MessageRequestsFragment extends Fragment implements MessageRequestsAdapter.AdapterCallback{

    public static final String URL_FETCH_REQUESTS = ROOT_URL + "messages.php/message_requests";

    private String deviceUsername,deviceUserID;
    private ImageView messagesNew, messagesMenu;
    private TextView noPosts, messagesText;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<MessageRequestsHelper> requests;
    private Context mCtx;

    @Override
    public void onMethodCallback(int position) {
        requests.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View convosRootView = inflater.inflate(R.layout.fragment_convos, null);
        mCtx = getActivity();
        messagesNew = convosRootView.findViewById(R.id.messagesNew);
        messagesMenu = convosRootView.findViewById(R.id.messagesMenu);
        noPosts = convosRootView.findViewById(R.id.noPosts);
        messagesText = convosRootView.findViewById(R.id.messagesText);
        messagesText.setText(R.string.message_requests_text);
        noPosts.setText(R.string.no_message_requests_text);
        dialog = new ProgressDialog(mCtx);
        dialog.setMessage("Loading message requests...");
        dialog.show();
        recyclerView = convosRootView.findViewById(R.id.chatConvos);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mCtx);
        recyclerView.setLayoutManager(layoutManager);
        requests = new ArrayList<>();
        deviceUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        deviceUserID = SharedPrefManager.getInstance(mCtx).getUserID();

        messagesMenu.setOnClickListener(v -> {
            requireActivity().finish();
            startActivity(new Intent(mCtx, FragmentContainer.class));
        });
        messagesNew.setOnClickListener(v -> {
            NewMessageFragment ldf = new NewMessageFragment();
            ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
        });
        fetchConvos();
        return convosRootView;
    }
    private void fetchConvos() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_REQUESTS+"?username="+deviceUsername+"&userid="+deviceUserID,
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
                            String id = obj.getString("id");
                            String user_from = obj.getString("user_from");
                            String type = obj.getString("type");
                            String group_id = obj.getString("group_id");
                            MessageRequestsHelper messageObject = new MessageRequestsHelper(sent_by,body_split,time_message,latest_profile_pic,profile_pic,nickname,id, user_from,type,group_id);
                            requests.add(messageObject);
                        }
                        if(thread.length()==0){
                            noPosts.setVisibility(View.VISIBLE);
                        }
                        adapter = new MessageRequestsAdapter(mCtx, requests, MessageRequestsFragment.this);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                });
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

}