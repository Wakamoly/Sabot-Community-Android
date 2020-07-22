package com.lucidsoftworksllc.sabotcommunity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.view.View.GONE;
import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class PublicsChatRoom extends Fragment {

    private Context mCtx;
    private String thisUserID, thisUsername, gameID, gamename, gameimage, thisNickname, thisProfile_pic, last_id, isChatRoomFollowed;
    private TextView gameName, lastReply;
    private ImageView sendButton, chatroomGameImage, backMessageButton, userMessageMenu, img_send_disabled, chatroomFollowBtn;
    private EditText messageEditText;
    private LinearLayout cannotRespondLayout, chatroomTopLayout;
    private LinearLayoutManager layoutManager;
    private ProgressBar chatroomFollowProg;
    public static final String URL_FETCH_MESSAGES = ROOT_URL + "chatrooms.php/messages";
    public static final String URL_SEND_MESSAGE = ROOT_URL + "chatrooms.php/send";
    public static final String URL_NOTI_FOLLOW = ROOT_URL + "chatrooms.php/noti";
    private static final String URL_USER_INFO = Constants.ROOT_URL + "chatrooms.php/game";
    private static final String URL_FETCH_MESSAGES_FROM_ID = Constants.ROOT_URL + "chatrooms.php/messagesFromID";

    private ArrayList<ChatroomMessagesHelper> messages;
    private RecyclerView messagesRecyclerView;
    private ChatroomMessagesAdapter adapter;
    private ProgressBar messageProgress, sendProgress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View messageRootView = inflater.inflate(R.layout.fragment_chatroom, null);

        messageEditText = messageRootView.findViewById(R.id.et_message);
        messageProgress = messageRootView.findViewById(R.id.messageProgress);
        sendButton = messageRootView.findViewById(R.id.img_send);
        backMessageButton = messageRootView.findViewById(R.id.backMessageButton);
        sendProgress = messageRootView.findViewById(R.id.sendProgress);
        cannotRespondLayout = messageRootView.findViewById(R.id.cannotRespondLayout);
        gameName = messageRootView.findViewById(R.id.gameName);
        lastReply = messageRootView.findViewById(R.id.lastReply);
        chatroomGameImage = messageRootView.findViewById(R.id.chatroomGameImage);
        chatroomTopLayout = messageRootView.findViewById(R.id.chatroomTopLayout);
        userMessageMenu = messageRootView.findViewById(R.id.userMessageMenu);
        img_send_disabled = messageRootView.findViewById(R.id.img_send_disabled);
        chatroomFollowBtn = messageRootView.findViewById(R.id.chatroomFollowBtn);
        chatroomFollowProg = messageRootView.findViewById(R.id.chatroomFollowProg);
        mCtx = getActivity();
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list);
        messagesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mCtx);
        //layoutManager.setReverseLayout(true); FIX THIS
        //layoutManager.setStackFromEnd(true);  AND THIS TOO
        messagesRecyclerView.setLayoutManager(layoutManager);

        //TODO: FIX THESE v
        assert getArguments() != null;
        gameID = getArguments().getString("GameId");
        thisUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        thisUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        thisNickname = SharedPrefManager.getInstance(mCtx).getNickname();
        thisProfile_pic = SharedPrefManager.getInstance(mCtx).getProfilePic();
        messages = new ArrayList<>();
        backMessageButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        loadGameInfo();
        getMessages();
        return messageRootView;
    }

    public void getMessages() {
        Thread messagesIDThread = new Thread(){//create thread
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_MESSAGES+"?this_user="+thisUsername+"&gameid="+gameID+"&userid="+thisUserID,
                        response -> {
                            try {
                                JSONObject res = new JSONObject(response);
                                String isUserFollow = res.getString("isUserFollow");
                                if (isUserFollow.equals("no")){
                                    isChatRoomFollowed = "no";
                                }else{
                                    isChatRoomFollowed = "yes";
                                }
                                chatroomFollowBtn.setOnClickListener(v -> promptNoti());
                                JSONArray thread = res.getJSONArray("messages");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    String id = obj.getString("id");
                                    String message = obj.getString("message");
                                    String username = obj.getString("username");
                                    String time_message = obj.getString("time_message");
                                    String nickname = obj.getString("nickname");
                                    String profile_pic = obj.getString("profile_pic");
                                    String last_online = obj.getString("last_online");
                                    String user_level = obj.getString("user_level");
                                    String verified = obj.getString("verified");
                                    String userid = obj.getString("userid");
                                    ChatroomMessagesHelper messageObject = new ChatroomMessagesHelper(id,message,username,time_message,nickname,profile_pic,last_online,user_level,verified, userid);
                                    messages.add(messageObject);
                                    last_id = id;
                                }
                                newChats();
                                messageProgress.setVisibility(View.GONE);
                                messagesRecyclerView.setVisibility(View.VISIBLE);
                                adapter = new ChatroomMessagesAdapter(mCtx, messages, thisUsername);
                                messagesRecyclerView.setAdapter(adapter);
                                scrollToBottomNow();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                        });
                ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
            }
        };
        messagesIDThread.start(); // start thread
    }

    private void promptNoti(){
        if (isChatRoomFollowed.equals("yes")){
            new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.notify_reply)
                    .setTitle("Remove chatroom push notifications for "+gamename+"?")
                    .setPositiveButton(R.string.yes, v -> {
                        chatroomFollowProg.setVisibility(View.VISIBLE);
                        userChatroomNoti("unfollow");
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }else if (isChatRoomFollowed.equals("no")){
            new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.green)
                    .setIcon(R.drawable.notify_reply)
                    .setTitle("Receive chatroom push notifications for "+gamename+"?")
                    .setMessage("Warning: it may get annoying once this feature becomes more popular!")
                    .setPositiveButton(R.string.yes, v -> {
                        chatroomFollowProg.setVisibility(View.VISIBLE);
                        userChatroomNoti("follow");
                    })
                    .setNegativeButton(R.string.no, null)
                    .show();
        }
    }

    public void getMessagesFromID() {
        Thread messagesIDThread = new Thread(){//create thread
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_MESSAGES_FROM_ID+"?this_user="+thisUsername+"&gameid="+gameID+"&userid="+thisUserID+"&start_id="+last_id,
                        response -> {
                            try {
                                JSONObject res = new JSONObject(response);
                                JSONArray thread = res.getJSONArray("messages");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    String id = obj.getString("id");
                                    String message = obj.getString("message");
                                    String username = obj.getString("username");
                                    String time_message = obj.getString("time_message");
                                    String nickname = obj.getString("nickname");
                                    String profile_pic = obj.getString("profile_pic");
                                    String last_online = obj.getString("last_online");
                                    String user_level = obj.getString("user_level");
                                    String verified = obj.getString("verified");
                                    String userid = obj.getString("userid");
                                    processMessage(id,message,username,time_message,nickname,profile_pic,last_online,user_level,verified, userid);
                                    last_id = id;
                                    lastReply.setText(time_message);
                                }
                                newChats();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                        });
                ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
            }
        };
        messagesIDThread.start(); // start thread
    }

    private void processMessage(String id, String message, String username, String time_message, String nickname, String profile_pic, String last_online, String user_level, String verified, String userid) {
        ChatroomMessagesHelper m = new ChatroomMessagesHelper(id, message, username, time_message, nickname, profile_pic, last_online, user_level, verified, userid);
        messages.add(m);
        scrollToBottom();
    }

    private void sendMessage(final String gameID, final String message) {
        sendButton.setVisibility(GONE);
        sendProgress.setVisibility(View.VISIBLE);
        if (message.equalsIgnoreCase(""))
            return;
        messageEditText.setText("");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SEND_MESSAGE,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equals("false")) {
                            ChatroomMessagesHelper m = new ChatroomMessagesHelper(null, message, thisUsername, "Just now", thisNickname, thisProfile_pic, "yes", "0", "no", thisUserID);
                            messages.add(m);
                            adapter.notifyDataSetChanged();
                            scrollToBottom();
                        } else {
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        sendButton.setVisibility(View.VISIBLE);
                        sendProgress.setVisibility(GONE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("gameid", gameID);
                params.put("message", message);
                params.put("user_from", thisUsername);
                return params;
            }
        };
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadGameInfo() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_USER_INFO + "?gameid=" + gameID + "&deviceuser=" + thisUsername + "&deviceuserid=" + thisUserID, response -> {
            try {
                JSONObject res = new JSONObject(response);
                JSONArray thread = res.getJSONArray("messages");
                JSONObject messageObject = thread.getJSONObject(0);

                String last_reply_text = messageObject.getString("last_reply_text");
                String gamename = messageObject.getString("gamename");
                String cat_image = messageObject.getString("image");
                gameName.setText(gamename);
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + cat_image)
                        .into(chatroomGameImage);
                lastReply.setText(last_reply_text);
                sendButton.setOnClickListener(view -> {
                    String body = messageEditText.getText().toString().trim();
                    if (!(messageEditText.getText().toString()).isEmpty()) {
                        sendMessage(gameID, body);
                        hideKeyboardFrom(mCtx, view);
                    } else {
                        Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show();
                    }
                });
                userMessageMenu.setOnClickListener(view -> {
                    //TODO: Set up menu
                    /*PopupMenu popup = new PopupMenu(mCtx, view);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.message_top_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menuPlayerReport) {
                                ReportFragment ldf = new ReportFragment();
                                Bundle args = new Bundle();
                                args.putString("context", "message");
                                args.putString("type", "user");
                                args.putString("id", user_to_id);
                                ldf.setArguments(args);
                                //Inflate the fragment
                                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit();
                            }
                            if (item.getItemId() == R.id.menuPlayerBlock) {
                                SharedPrefManager.getInstance(mCtx).block_user(user_to);
                                Fragment currentFragment = getActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                if (currentFragment instanceof MessageFragment) {
                                    FragmentTransaction fragTransaction = (getActivity()).getSupportFragmentManager().beginTransaction();
                                    fragTransaction.detach(currentFragment);
                                    fragTransaction.attach(currentFragment);
                                    fragTransaction.commit();
                                }
                            }
                            return true;
                        }
                    });
                    popup.show();*/
                });
                    /*
                    if user is banned from chat
                    cannotRespondLayout.setVisibility(View.VISIBLE);
                    userMessageMenu.setVisibility(GONE);*/

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    private void userChatroomNoti(String method){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_NOTI_FOLLOW,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        chatroomFollowProg.setVisibility(GONE);
                        if (method.equals("follow")){
                            isChatRoomFollowed="yes";
                        }else if (method.equals("unfollow")){
                            isChatRoomFollowed="no";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("gameid", gameID);
                params.put("gamename", gamename);
                params.put("method", method);
                params.put("username", thisUsername);
                params.put("userid", thisUserID);
                return params;
            }
        };
        ((FragmentContainer)mCtx).addToRequestQueue(stringRequest);
    }

    public void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            Objects.requireNonNull(messagesRecyclerView.getLayoutManager()).smoothScrollToPosition(messagesRecyclerView, null, adapter.getItemCount() - 1);
    }

    private void scrollToBottomNow() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            Objects.requireNonNull(messagesRecyclerView.getLayoutManager()).scrollToPosition(adapter.getItemCount() - 1);
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void newChats(){
        if (!shouldGetNotification(mCtx)){
            Handler chatHandler=new Handler();
            Runnable runnableCode = this::getMessagesFromID;
            chatHandler.postDelayed(runnableCode, 5000);
        }else{
            Handler chatHandler=new Handler();
            Runnable runnableCode = this::newChats;
            chatHandler.postDelayed(runnableCode, 10000);
        }
    }

    static boolean shouldGetNotification(Context context) {
        ActivityManager.RunningAppProcessInfo myProcess = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(myProcess);
        if (myProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return true;
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

}