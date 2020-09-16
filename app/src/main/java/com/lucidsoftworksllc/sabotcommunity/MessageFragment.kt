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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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

public class MessageFragment extends Fragment {

    private Context mCtx;
    private String thisUserID,thisUsername,user_to,profile_pic,nickname,imageUploaded,last_id;
    private TextView profileMessageToName, lastOnline;
    private ImageView sendButton, img_attachment, userMessageMenu, backMessageButton;
    private CircleImageView profileMessageToImage,verifiedView,onlineView;
    private EditText messageEditText;
    private LinearLayout cannotRespondLayout, usernameLayout;
    private LinearLayoutManager layoutManager;
    public static final String URL_FETCH_MESSAGES = ROOT_URL + "messages.php/messages";
    public static final String URL_FETCH_MORE_MESSAGES = ROOT_URL + "messages.php/get_new_messages";
    public static final String URL_SEND_MESSAGE = ROOT_URL + "messages.php/send";
    private static final String URL_USER_INFO = Constants.ROOT_URL+"messages.php/user";
    private static final String UPLOAD_IMAGE_URL = Constants.ROOT_URL+"message_image_upload.php";
    private ArrayList<MessagesHelper> messages;
    private RecyclerView messagesRecyclerView;
    private MessagesThreadAdapter adapter;
    private ProgressBar messageProgress, sendProgress;
    private Bitmap imageToUpload;
    RequestQueue rQueue;
    JSONObject jsonObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View messageRootView = inflater.inflate(R.layout.content_chat, null);

        messageEditText = messageRootView.findViewById(R.id.et_message);
        onlineView = messageRootView.findViewById(R.id.online);
        verifiedView = messageRootView.findViewById(R.id.verified);
        messageProgress = messageRootView.findViewById(R.id.messageProgress);
        sendButton = messageRootView.findViewById(R.id.img_send);
        img_attachment = messageRootView.findViewById(R.id.img_attachment);
        profileMessageToName = messageRootView.findViewById(R.id.profileMessageToName);
        profileMessageToImage = messageRootView.findViewById(R.id.profileMessageToImage);
        userMessageMenu = messageRootView.findViewById(R.id.userMessageMenu);
        backMessageButton = messageRootView.findViewById(R.id.backMessageButton);
        sendProgress = messageRootView.findViewById(R.id.sendProgress);
        cannotRespondLayout =  messageRootView.findViewById(R.id.cannotRespondLayout);
        usernameLayout = messageRootView.findViewById(R.id.usernameLayout);
        lastOnline = messageRootView.findViewById(R.id.lastOnline);
        imageUploaded = "";
        mCtx = getActivity();
        messagesRecyclerView = messageRootView.findViewById(R.id.recycler_chat_list);
        messagesRecyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(mCtx);
        //layoutManager.setReverseLayout(true); FIX THIS
        //layoutManager.setStackFromEnd(true);  AND THIS TOO
        messagesRecyclerView.setLayoutManager(layoutManager);

        assert getArguments() != null;
        user_to = getArguments().getString("user_to");
        thisUserID = SharedPrefManager.getInstance(mCtx).getUserID();
        thisUsername = SharedPrefManager.getInstance(mCtx).getUsername();
        messages = new ArrayList<>();
        backMessageButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        img_attachment.setOnClickListener(v -> {
            requestMultiplePermissions();
            openCropper();
        });

        loadUserInfo(user_to);
        getMessages();
        return messageRootView;
    }

    private void openCropper(){
        CropImage.activity()
                .start(requireContext(), this);
    }

    public void getMessages(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_MESSAGES+"?this_user="+thisUsername+"&username="+user_to+"&userid="+thisUserID,
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
                            MessagesHelper messageObject = new MessagesHelper(id,user_to,user_from,body,date,image);
                            messages.add(messageObject);
                            last_id = id;
                        }
                        newChats();
                        messageProgress.setVisibility(View.GONE);
                        messagesRecyclerView.setVisibility(View.VISIBLE);
                        adapter = new MessagesThreadAdapter(mCtx, messages, thisUsername);
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
                StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_FETCH_MORE_MESSAGES+"?this_user="+thisUsername+"&username="+user_to+"&userid="+thisUserID+"&last_id="+last_id,
                        response -> {
                            try {
                                JSONObject res = new JSONObject(response);
                                String last_online = res.getString("userLastOnline");
                                if (!last_online.equals("null")){
                                    lastOnline.setText(last_online);
                                    if (last_online.equals("Online now")){
                                        onlineView.setVisibility(View.VISIBLE);
                                    }else{
                                        onlineView.setVisibility(GONE);
                                    }
                                }
                                JSONArray thread = res.getJSONArray("messages");
                                for (int i = 0; i < thread.length(); i++) {
                                    JSONObject obj = thread.getJSONObject(i);
                                    String id = obj.getString("id");
                                    String user_from = obj.getString("user_from");
                                    String body = obj.getString("body");
                                    String image = obj.getString("image");
                                    processMessage(user_from,body,image);
                                    last_id = id;
                                }
                                newChats();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {
                        });
                ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
            }
        };
        messagesIDThread.start(); // start thread
    }

    private void processMessage(String user_string, String message, String image) {
        MessagesHelper m = new MessagesHelper(null, thisUsername, user_string, message, "Just now", image);
        messages.add(m);
        scrollToBottom();
    }

    private void sendMessage(final String user_string, final String message) {
        sendButton.setVisibility(GONE);
        sendProgress.setVisibility(View.VISIBLE);
        if (message.equalsIgnoreCase(""))
            return;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SEND_MESSAGE, response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if(jsonObject.getString("error").equals("false")){
                            if(imageToUpload != null){
                                String message_id = jsonObject.getString("messageid");
                                messageImageUpload(imageToUpload, message_id, thisUsername, user_string, message);
                            }else{
                                MessagesHelper m = new MessagesHelper(null, user_string, thisUsername, message, "Just now", "");
                                messages.add(m);
                                adapter.notifyDataSetChanged();
                                scrollToBottom();
                                sendButton.setVisibility(View.VISIBLE);
                                sendProgress.setVisibility(GONE);
                            }
                            messageEditText.setText("");
                        }else{
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            sendButton.setVisibility(View.VISIBLE);
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
                params.put("user_to", user_string);
                params.put("message", message);
                params.put("user_from", thisUsername);
                params.put("user_id", thisUserID);
                return params;
            }
        };
        ((ChatActivity)mCtx).addToRequestQueue(stringRequest);
    }

    private void loadUserInfo(final String user_to){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_USER_INFO+"?username="+user_to+"&deviceuser="+thisUsername+"&deviceuserid="+thisUserID, response -> {
            try {
                JSONObject res = new JSONObject(response);
                JSONArray thread = res.getJSONArray("messages");
                JSONObject messageObject = thread.getJSONObject(0);

                String profile_pic = messageObject.getString("profile_pic");
                String nickname = messageObject.getString("nickname");
                String verified = messageObject.getString("verified");
                String last_online = messageObject.getString("last_online");
                String last_online_text = messageObject.getString("last_online_text");
                String blocked_array = messageObject.getString("blocked_array");
                final String user_to_id = messageObject.getString("user_id");

                lastOnline.setText(last_online_text);
                String blocked = "";
                String[] blockedArray = blocked_array.split(",");
                for(String usernameBlocked : blockedArray){
                    if (usernameBlocked.equals(thisUsername) && !usernameBlocked.equals("")) {
                        blocked = "yes";
                        break;
                    }
                }
                if(!blocked.equals("yes") && !SharedPrefManager.getInstance(mCtx).isUserBlocked(user_to)){
                    sendButton.setOnClickListener(view -> {
                        String body = messageEditText.getText().toString().trim();
                        if(!(messageEditText.getText().toString().isEmpty())||(!(imageUploaded ==null)&&!(imageUploaded.isEmpty()))){
                            sendMessage(user_to,body);
                            hideKeyboardFrom(mCtx,view);
                        } else {
                            Toast.makeText(mCtx,"You must enter text before submitting!",Toast.LENGTH_LONG).show();
                        }
                    });
                    usernameLayout.setOnClickListener(view -> startActivity(new Intent(mCtx, FragmentContainer.class).putExtra("user_to_id", user_to_id)));
                    if (last_online.equals("yes")) {
                        onlineView.setVisibility(View.VISIBLE);
                    }
                    if (verified.equals("yes")) {
                        verifiedView.setVisibility(View.VISIBLE);
                    }
                    userMessageMenu.setOnClickListener(view -> {
                        PopupMenu popup = new PopupMenu(mCtx, view);
                        MenuInflater inflater = popup.getMenuInflater();
                        inflater.inflate(R.menu.message_top_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.menuPlayerReport) {
                                ReportFragment ldf = new ReportFragment();
                                Bundle args = new Bundle();
                                args.putString("context", "message");
                                args.putString("type", "user");
                                args.putString("id", user_to_id);
                                ldf.setArguments(args);
                                ((FragmentActivity)mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).addToBackStack(null).commit();
                            }
                            if (item.getItemId() == R.id.menuPlayerBlock) {
                                SharedPrefManager.getInstance(mCtx).block_user(user_to);
                                Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                                if (currentFragment instanceof MessageFragment) {
                                    FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                    fragTransaction.detach(currentFragment);
                                    fragTransaction.attach(currentFragment);
                                    fragTransaction.commit();
                                }
                            }
                            return true;
                        });
                        popup.show();
                    });
                }else{
                    cannotRespondLayout.setVisibility(View.VISIBLE);
                    userMessageMenu.setVisibility(GONE);
                }
                profileMessageToName.setText(nickname);
                String profile_pic2 = profile_pic.substring(0, profile_pic.length() - 4)+"_r.JPG";
                Glide.with(mCtx)
                        .load(Constants.BASE_URL+profile_pic2)
                        .into(profileMessageToImage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {});
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
                            MessagesHelper m = new MessagesHelper(null, user_string, thisUsername, message, "Just now", imageUploaded);
                            messages.add(m);
                            adapter.notifyDataSetChanged();
                            scrollToBottom();
                            sendButton.setVisibility(View.VISIBLE);
                            sendProgress.setVisibility(GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> Log.e("UploadCoverFragment", volleyError.toString()));
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
                    Toast.makeText(mCtx, "Failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(mCtx, "Failed! Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
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