package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class MessageGroupFragment extends Fragment {

    private Context mCtx;
    private String thisUserID,thisUsername,group_id,profile_pic,group_name,imageUploaded,last_id;
    private TextView groupName, lastReply;
    private ImageView img_send, img_attachment, groupMessageMenu, backMessageButton;
    private CircleImageView groupImage,verifiedView,onlineView;
    private EditText et_message;
    private LinearLayout cannotRespondLayout, usernameLayout;
    private LinearLayoutManager layoutManager;
    public static final String URL_FETCH_MESSAGES = ROOT_URL + "messages.php/groupmessages";
    public static final String URL_FETCH_MORE_MESSAGES = ROOT_URL + "messages.php/get_new_group_messages";
    public static final String URL_SEND_MESSAGE = ROOT_URL + "messages.php/send_group";
    private static final String URL_GROUP_INFO = Constants.ROOT_URL+"messages.php/group";
    private static final String URL_MUTE_CONVO = Constants.ROOT_URL+"messages.php/mute_convo";
    private static final String URL_LEAVE_CONVO = Constants.ROOT_URL+"messages.php/leave_convo";
    private static final String UPLOAD_IMAGE_URL = Constants.ROOT_URL+"message_image_upload.php";

    private ArrayList<GroupMessagesHelper> messages;
    private RecyclerView messagesRecyclerView;
    private GroupMessagesThreadAdapter adapter;
    private ProgressBar messageProgress, sendProgress;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private View navHeader;
    private Bitmap imageToUpload;
    RequestQueue rQueue;
    JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View messageRootView = inflater.inflate(R.layout.fragment_group_message, null);

        backMessageButton = messageRootView.findViewById(R.id.backMessageButton);
        groupMessageMenu = messageRootView.findViewById(R.id.groupMessageMenu);
        groupName = messageRootView.findViewById(R.id.groupName);
        lastReply = messageRootView.findViewById(R.id.lastReply);
        messageProgress = messageRootView.findViewById(R.id.messageProgress);
        cannotRespondLayout = messageRootView.findViewById(R.id.cannotRespondLayout);
        img_attachment = messageRootView.findViewById(R.id.img_attachment);
        et_message = messageRootView.findViewById(R.id.et_message);
        img_send = messageRootView.findViewById(R.id.img_send);
        sendProgress = messageRootView.findViewById(R.id.sendProgress);
        groupImage = messageRootView.findViewById(R.id.groupImage);
        mDrawerLayout = messageRootView.findViewById(R.id.drawer_layout);
        navigationView = messageRootView.findViewById(R.id.side_nav_view);
        imageUploaded = "";

        mCtx = getActivity();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), mDrawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list);
        messagesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mCtx);
        //layoutManager.setReverseLayout(true); FIX THIS
        //layoutManager.setStackFromEnd(true);  AND THIS TOO
        messagesRecyclerView.setLayoutManager(layoutManager);

        assert getArguments() != null;
        group_id = getArguments().getString("group_id");
        thisUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        thisUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        profile_pic = SharedPrefManager.getInstance(mCtx).getProfilePic();
        group_name = "";
        messages = new ArrayList<>();
        backMessageButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        img_attachment.setOnClickListener(v -> {
            requestMultiplePermissions();
            openCropper();
        });

        loadGroupInfo(group_id);
        getMessages();
        return messageRootView;
    }

    private void openCropper(){
        CropImage.activity()
                .start(requireContext(), this);
    }

    public void getMessages(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_MESSAGES+"?this_user="+thisUsername+"&group_id="+group_id+"&userid="+thisUserID,
                response -> {
                    try {
                        JSONObject res = new JSONObject(response);
                        JSONArray thread = res.getJSONArray("messages");
                        for (int i = 0; i < thread.length(); i++) {
                            JSONObject obj = thread.getJSONObject(i);
                            String id = obj.getString("id");
                            String user_to = obj.getString("user_to");
                            String user_from = obj.getString("user_from");
                            String body = obj.getString("body");
                            String date = obj.getString("date");
                            String image = obj.getString("image");
                            String profile_pic = obj.getString("profile_pic");
                            GroupMessagesHelper messageObject = new GroupMessagesHelper(id,user_to,user_from,body,date,image,profile_pic);
                            messages.add(messageObject);
                            last_id = id;
                        }
                        newChats();
                        messageProgress.setVisibility(View.GONE);
                        messagesRecyclerView.setVisibility(View.VISIBLE);
                        adapter = new GroupMessagesThreadAdapter(mCtx, messages, thisUsername);
                        messagesRecyclerView.setAdapter(adapter);
                        scrollToBottomNow();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {});
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

    public void getMessagesFromID(){
        Thread messagesIDThread = new Thread(){//create thread
            @Override
            public void run() {
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_MORE_MESSAGES+"?this_user="+thisUsername+"&group_id="+group_id+"&userid="+thisUserID+"&last_id="+last_id,
                        response -> {
                            try {
                                JSONObject res = new JSONObject(response);
                                String last_reply = res.getString("last_reply");
                                if (last_reply.equals("removed")){
                                    Toast.makeText(mCtx,"Removed from group!",Toast.LENGTH_SHORT).show();
                                    cannotRespondLayout.setVisibility(View.VISIBLE);
                                    //TODO fix this v
                                    ((FragmentActivity)mCtx).getSupportFragmentManager().popBackStackImmediate();
                                }else{
                                    lastReply.setText(last_reply);
                                }
                                JSONArray thread = res.getJSONArray("messages");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    String id = obj.getString("id");
                                    String user_from = obj.getString("user_from");
                                    String body = obj.getString("body");
                                    String image = obj.getString("image");
                                    String time = obj.getString("time");
                                    String profile_pic = obj.getString("profile_pic");
                                    processMessage(user_from,body,image,time,profile_pic);
                                    last_id = id;
                                }
                                newChats();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {});
                ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
            }
        };
        messagesIDThread.start(); // start thread
    }

    private void processMessage(String user_string, String message, String image, String time, String profile_pic) {
        GroupMessagesHelper m = new GroupMessagesHelper(null, thisUsername, user_string, message, time, image, profile_pic);
        messages.add(m);
        scrollToBottom();
    }

    private void sendMessage(String group_id, String body) {
        img_send.setVisibility(GONE);
        sendProgress.setVisibility(View.VISIBLE);
        if (body.equalsIgnoreCase(""))
            return;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SEND_MESSAGE,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("error").equals("false")){
                            if(imageToUpload != null){
                                String message_id = jsonObject.getString("messageid");
                                messageImageUpload(imageToUpload, message_id, thisUsername, group_id, body);
                            }else{
                                GroupMessagesHelper m = new GroupMessagesHelper(null, group_id, thisUsername, body, "Just now", "", profile_pic);
                                messages.add(m);
                                adapter.notifyDataSetChanged();
                                scrollToBottom();
                                img_send.setVisibility(View.VISIBLE);
                                sendProgress.setVisibility(GONE);
                            }
                            et_message.setText("");
                        }else{
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            img_send.setVisibility(View.VISIBLE);
                            sendProgress.setVisibility(GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {}) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                params.put("message", body);
                params.put("group_name", group_name);
                params.put("user_from", thisUsername);
                return params;
            }
        };
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadGroupInfo(final String group_id){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_GROUP_INFO+"?group_id="+group_id+"&deviceuser="+thisUsername+"&deviceuserid="+thisUserID, response -> {
            //TODO put a refreshing check on ability to message
            try {
                JSONObject res = new JSONObject(response);
                JSONArray thread = res.getJSONArray("messages");
                JSONObject messageObject = thread.getJSONObject(0);

                String last_reply_text = messageObject.getString("last_reply_text");
                String name = messageObject.getString("name");
                String image = messageObject.getString("image");
                String options = messageObject.getString("options");
                String position = messageObject.getString("position");
                String date = messageObject.getString("date");
                String usersadded = messageObject.getString("users_added");
                String owner = messageObject.getString("owner");
                final String[] muted = {messageObject.getString("muted")};

                String allUsersCanChangeGroupImage = "no";
                String allUsersCanChangeGroupName = "no";
                String allUsersCanInvite = "no";
                String allUsersCanMessage = "no";
                String adminsCanChangeGroupImage = "no";
                String adminsCanChangeGroupName = "no";
                String adminsCanInvite = "no";
                String adminsCanMessage = "no";
                String adminsCanRemoveUsers = "no";
                String[] optionsSplit = options.split( ", ");
                for ( String option : optionsSplit ) {
                    switch (option){
                        case "allUsersCanChangeGroupImage":
                            allUsersCanChangeGroupImage = "yes";
                            break;
                        case "allUsersCanChangeGroupName":
                            allUsersCanChangeGroupName = "yes";
                            break;
                        case "allUsersCanInvite":
                            allUsersCanInvite = "yes";
                            break;
                        case "allUsersCanMessage":
                            allUsersCanMessage = "yes";
                            break;
                        case "adminsCanChangeGroupImage":
                            adminsCanChangeGroupImage = "yes";
                            break;
                        case "adminsCanChangeGroupName":
                            adminsCanChangeGroupName = "yes";
                            break;
                        case "adminsCanInvite":
                            adminsCanInvite = "yes";
                            break;
                        case "adminsCanMessage":
                            adminsCanMessage = "yes";
                            break;
                        case "adminsCanRemoveUsers":
                            adminsCanRemoveUsers = "yes";
                            break;
                    }
                }
                String canChangeGroupImage="no";
                String canChangeGroupName="no";
                String canInvite="no";
                String canMessage="no";
                String canRemoveUsers="no";
                String canChangeUserPriv="no";
                String canChangeOptions="no";
                switch (position) {
                    case "owner":
                        canChangeGroupImage = "yes";
                        canChangeGroupName = "yes";
                        canInvite = "yes";
                        canMessage = "yes";
                        canRemoveUsers = "yes";
                        canChangeUserPriv = "yes";
                        canChangeOptions = "yes";
                        break;
                    case "admin":
                        if (adminsCanChangeGroupImage.equals("yes")) {
                            canChangeGroupImage = "yes";
                        } else if (allUsersCanChangeGroupImage.equals("yes")) {
                            canChangeGroupImage = "yes";
                        }
                        if (adminsCanChangeGroupName.equals("yes")) {
                            canChangeGroupName = "yes";
                        } else if (allUsersCanChangeGroupName.equals("yes")) {
                            canChangeGroupName = "yes";
                        }
                        if (adminsCanInvite.equals("yes")) {
                            canInvite = "yes";
                        } else if (allUsersCanInvite.equals("yes")) {
                            canInvite = "yes";
                        }
                        if (adminsCanMessage.equals("yes")) {
                            canMessage = "yes";
                        } else if (allUsersCanMessage.equals("yes")) {
                            canMessage = "yes";
                        }
                        if (adminsCanRemoveUsers.equals("yes")) {
                            canRemoveUsers = "yes";
                        }
                        break;
                    case "user":
                        if (allUsersCanMessage.equals("yes")) {
                            canMessage = "yes";
                        }
                        if (allUsersCanChangeGroupImage.equals("yes")) {
                            canChangeGroupImage = "yes";
                        }
                        if (allUsersCanChangeGroupName.equals("yes")) {
                            canChangeGroupName = "yes";
                        }
                        if (allUsersCanInvite.equals("yes")) {
                            canInvite = "yes";
                        }
                        break;
                }
                if (!canMessage.equals("yes")){
                    cannotRespondLayout.setVisibility(View.VISIBLE);
                }
                groupName.setOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.END));
                groupImage.setOnClickListener(v -> mDrawerLayout.openDrawer(GravityCompat.END));
                String finalCanRemoveUsers = canRemoveUsers;
                String finalCanInvite = canInvite;
                String finalCanChangeGroupImage = canChangeGroupImage;
                String finalCanChangeGroupName = canChangeGroupName;
                String finalCanChangeOptions = canChangeOptions;
                navigationView.setNavigationItemSelectedListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.mute_convo: {
                            if (muted[0].equals("yes")){
                                new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                        .setTopColorRes(R.color.green)
                                        .setButtonsColorRes(R.color.green)
                                        .setIcon(R.drawable.ic_friend_cancel)
                                        .setTitle("Un-mute "+name+"?")
                                        .setPositiveButton(R.string.yes, v -> {
                                            muted[0] = "no";
                                            muteConvo("unmute");
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();
                            }else if (muted[0].equals("no")){
                                new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                        .setTopColorRes(R.color.green)
                                        .setButtonsColorRes(R.color.green)
                                        .setIcon(R.drawable.ic_friend_cancel)
                                        .setTitle("Mute "+name+"?")
                                        .setPositiveButton(R.string.yes, v -> {
                                            muted[0] = "yes";
                                            muteConvo("mute");
                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();
                            }
                            break;
                        }
                        case R.id.players_added: {
                            Fragment asf = new MessageUserListFragment();
                            Bundle args = new Bundle();
                            args.putString("query", "players_added");
                            args.putString("queryID", group_id);
                            args.putString("permission", position);
                            args.putString("owner", owner);
                            args.putString("canRemove", finalCanRemoveUsers);
                            asf.setArguments(args);
                            FragmentTransaction fragmentTransaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.chat_fragment_container, asf);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case R.id.invite_players: {
                            if (finalCanInvite.equals("yes")){
                                Fragment asf = new MessageGroupInviteFragment();
                                Bundle args = new Bundle();
                                args.putString("group_id", group_id);
                                asf.setArguments(args);
                                FragmentTransaction fragmentTransaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                                fragmentTransaction.replace(R.id.chat_fragment_container, asf);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                            break;
                        }
                        case R.id.group_options: {
                            Fragment asf = new MessageGroupOptionsFragment();
                            Bundle args = new Bundle();
                            args.putString("canChangeGroupImage", finalCanChangeGroupImage);
                            args.putString("canChangeGroupName", finalCanChangeGroupName);
                            args.putString("canChangeOptions", finalCanChangeOptions);
                            args.putString("group_id", group_id);
                            asf.setArguments(args);
                            FragmentTransaction fragmentTransaction = ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.chat_fragment_container, asf);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            break;
                        }
                        case R.id.leave_group:{
                            new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(R.drawable.ic_friend_cancel)
                                    .setTitle(R.string.leave_group_message_text)
                                    .setPositiveButton(R.string.yes, v -> leaveConvo())
                                    .setNegativeButton(R.string.no, null)
                                    .show();
                        }
                    }
                    mDrawerLayout.closeDrawer(GravityCompat.END);
                    return true;
                });
                navHeader = navigationView.getHeaderView(0);
                ImageView navBackground = navHeader.findViewById(R.id.img_header_bg);
                TextView headerNickname = navHeader.findViewById(R.id.headerNickname);
                TextView dateCreated = navHeader.findViewById(R.id.date);
                TextView usersAdded = navHeader.findViewById(R.id.numAdded);
                TextView mute_convo = navHeader.findViewById(R.id.mute_convo);
                usersAdded.setText(String.format("Users: %s", usersadded));
                dateCreated.setText(String.format("Created: %s", date));
                headerNickname.setText(name);
                Glide.with(mCtx).load(Constants.BASE_URL +image)
                        .thumbnail(0.5f)
                        .into(navBackground);
                groupName.setText(name);
                group_name = name;
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + image)
                        .into(groupImage);
                lastReply.setText(last_reply_text);
                if (canMessage.equals("yes")){
                    img_send.setOnClickListener(view -> {
                        String body = et_message.getText().toString().trim();
                        if(!(et_message.getText().toString().isEmpty())||(!(imageUploaded ==null)&&!(imageUploaded.isEmpty()))){
                            sendMessage(group_id, body);
                            hideKeyboardFrom(mCtx, view);
                        } else {
                            Toast.makeText(mCtx, "You must enter text before submitting!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                groupMessageMenu.setOnClickListener(view -> {
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
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

    private void muteConvo(String method) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MUTE_CONVO,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mCtx, "An error occured!", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                params.put("user_muted", "");
                params.put("method", method);
                params.put("username", thisUsername);
                params.put("userid", thisUserID);
                return params;
            }
        };
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

    private void leaveConvo() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LEAVE_CONVO,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equals("false")){
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            ConvosFragment ldf = new ConvosFragment();
                            ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).commit();
                        }else{
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("group_id", group_id);
                params.put("username", thisUsername);
                params.put("userid", thisUserID);
                return params;
            }
        };
        //Disabling retry to prevent duplicate messages
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
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

    private void messageImageUpload(Bitmap bitmap, String message_id, String username, final String user_string, final String message){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("message_id", message_id);
            jsonObject.put("image", encodedImage);
            jsonObject.put("user_from", username);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_IMAGE_URL, jsonObject,
                jsonObject -> {
                    rQueue.getCache().clear();
                    try{
                        if(!jsonObject.getString("error").equals("true")){
                            img_attachment.setImageResource(R.drawable.ic_attach_file_grey_24dp);
                            imageToUpload = null;
                            imageUploaded = jsonObject.getString("imagepath");
                            GroupMessagesHelper m = new GroupMessagesHelper(null, user_string, thisUsername, message, "Just now", imageUploaded, profile_pic);
                            messages.add(m);
                            adapter.notifyDataSetChanged();
                            scrollToBottom();
                            img_send.setVisibility(View.VISIBLE);
                            sendProgress.setVisibility(GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> {});
        rQueue = Volley.newRequestQueue(mCtx);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(jsonObjectRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(mCtx.getContentResolver(), resultUri);
                    img_attachment.setImageBitmap(bitmap1);
                    imageToUpload = bitmap1;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mCtx, "Image loading failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Failed! Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(mCtx.getApplicationContext(), "No permissions granted!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mCtx.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

    public void newChats(){
        if (!shouldGetNotification(mCtx)){
            Handler chatHandler=new Handler();
            Runnable runnableCode = this::getMessagesFromID;
            chatHandler.postDelayed(runnableCode, 3000);
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