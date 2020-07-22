package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.widget.SearchView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.app.Activity.RESULT_CANCELED;
import static android.view.View.GONE;

public class MessageGroupOptionsFragment extends Fragment {
    private ImageView new_message_insignia;
    private LinearLayout groupOptionsLayout;
    private TextView messageName;
    private RelativeLayout setInsigniaButton;
    private EditText etNewGroupMessageName;
    private CheckBox allUsersCanInvite,adminsCanInvite,allUsersCanChangeGroupImage,adminsCanChangeGroupImage,allUsersCanChangeGroupName,adminsCanChangeGroupName,allUsersCanMessage,adminsCanMessage,adminsCanRemoveUsers;
    private Button btnSubmit;
    private ProgressBar submitGroupOptionsProg, groupOptionsLoading;
    private Context mContext;
    private final int GALLERY_INSIGNIA = 2;
    private Bitmap new_group_insigniaBitmap;
    JSONObject jsonObject;
    RequestQueue rQueue;
    private String deviceUserID,deviceUsername,canChangeGroupImage,canChangeGroupName,canChangeOptions,group_id;
    public static final String URL_LOAD_GROUP_OPTIONS = Constants.ROOT_URL+"messages.php/loadgroupoptions";
    public static final String URL_GROUP_OPTIONS_SUBMIT = Constants.ROOT_URL+"messages.php/updategroupoptions";
    public static final String UPLOAD_INSIGNIA_URL = Constants.ROOT_URL+"uploadInsigniaGroupMessage.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View groupOptionsRootView = inflater.inflate(R.layout.fragment_group_message_options, null);

        new_message_insignia = groupOptionsRootView.findViewById(R.id.new_message_insignia);
        setInsigniaButton = groupOptionsRootView.findViewById(R.id.setInsigniaButton);
        etNewGroupMessageName = groupOptionsRootView.findViewById(R.id.etNewGroupMessageName);
        allUsersCanInvite = groupOptionsRootView.findViewById(R.id.allUsersCanInvite);
        adminsCanInvite = groupOptionsRootView.findViewById(R.id.adminsCanInvite);
        allUsersCanChangeGroupImage = groupOptionsRootView.findViewById(R.id.allUsersCanChangeGroupImage);
        adminsCanChangeGroupImage = groupOptionsRootView.findViewById(R.id.adminsCanChangeGroupImage);
        allUsersCanChangeGroupName = groupOptionsRootView.findViewById(R.id.allUsersCanChangeGroupName);
        adminsCanChangeGroupName = groupOptionsRootView.findViewById(R.id.adminsCanChangeGroupName);
        allUsersCanMessage = groupOptionsRootView.findViewById(R.id.allUsersCanMessage);
        adminsCanMessage = groupOptionsRootView.findViewById(R.id.adminsCanMessage);
        adminsCanRemoveUsers = groupOptionsRootView.findViewById(R.id.adminsCanRemoveUsers);
        btnSubmit = groupOptionsRootView.findViewById(R.id.btnSubmit);
        submitGroupOptionsProg = groupOptionsRootView.findViewById(R.id.submitGroupOptionsProg);
        groupOptionsLoading = groupOptionsRootView.findViewById(R.id.groupOptionsLoading);
        groupOptionsLayout = groupOptionsRootView.findViewById(R.id.groupOptionsLayout);
        messageName = groupOptionsRootView.findViewById(R.id.messageName);
        mContext = getActivity();
        new_group_insigniaBitmap = null;

        if((getArguments())!=null) {
            canChangeGroupImage = getArguments().getString("canChangeGroupImage");
            canChangeGroupName = getArguments().getString("canChangeGroupName");
            canChangeOptions = getArguments().getString("canChangeOptions");
            group_id = getArguments().getString("group_id");
            deviceUserID = SharedPrefManager.getInstance(mContext).getUserID();
            deviceUsername = SharedPrefManager.getInstance(mContext).getUsername();
        }

        getGroupOptionsInfo();
        return groupOptionsRootView;
    }

    private void getGroupOptionsInfo(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_LOAD_GROUP_OPTIONS+"?group_id="+group_id+"&deviceuser="+deviceUsername+"&deviceuserid="+deviceUserID, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    JSONArray thread = jsonObject.getJSONArray("messages");
                    JSONObject messageObject = thread.getJSONObject(0);

                    String name = messageObject.getString("name");
                    String image = messageObject.getString("image");
                    String options = messageObject.getString("options");
                    etNewGroupMessageName.setText(name);
                    messageName.setText(name);
                    String[] optionsSplit = options.split( ", ");
                    for ( String option : optionsSplit ) {
                        switch (option){
                            case "allUsersCanChangeGroupImage":
                                allUsersCanChangeGroupImage.setChecked(true);
                                break;
                            case "allUsersCanChangeGroupName":
                                allUsersCanChangeGroupName.setChecked(true);
                                break;
                            case "allUsersCanInvite":
                                allUsersCanInvite.setChecked(true);
                                break;
                            case "allUsersCanMessage":
                                allUsersCanMessage.setChecked(true);
                                break;
                            case "adminsCanChangeGroupImage":
                                adminsCanChangeGroupImage.setChecked(true);
                                break;
                            case "adminsCanChangeGroupName":
                                adminsCanChangeGroupName.setChecked(true);
                                break;
                            case "adminsCanInvite":
                                adminsCanInvite.setChecked(true);
                                break;
                            case "adminsCanMessage":
                                adminsCanMessage.setChecked(true);
                                break;
                            case "adminsCanRemoveUsers":
                                adminsCanRemoveUsers.setChecked(true);
                                break;
                        }
                    }

                    if (canChangeGroupImage.equals("yes")||canChangeGroupName.equals("yes")||canChangeOptions.equals("yes")){
                        btnSubmit.setVisibility(View.VISIBLE);
                        if (canChangeGroupName.equals("yes")&&canChangeOptions.equals("yes")){
                            btnSubmit.setOnClickListener(v -> {
                                if (etNewGroupMessageName.getText().toString().length()>=3&&(etNewGroupMessageName.getText().toString().length()<=25)){
                                    submitGroupOptionsProg.setVisibility(View.VISIBLE);
                                    btnSubmit.setVisibility(View.GONE);
                                    String optionArray = getGroupOptions();
                                    UpdateGroupMessage(etNewGroupMessageName.getText().toString(),optionArray);
                                }else{
                                    Toast.makeText(mContext, "Group name must be 3-25 characters long", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(canChangeGroupName.equals("yes")){
                            btnSubmit.setOnClickListener(v -> {
                                submitGroupOptionsProg.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.GONE);
                                UpdateGroupMessage(etNewGroupMessageName.getText().toString(),"");
                            });
                        }else if(canChangeGroupImage.equals("yes")){
                            btnSubmit.setOnClickListener(v -> {
                                submitGroupOptionsProg.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.GONE);
                                uploadInsigniaImage(new_group_insigniaBitmap, group_id, messageName.getText().toString());
                            });
                        }
                    }
                    if (canChangeOptions.equals("yes")){
                        allUsersCanChangeGroupImage.setEnabled(true);
                        allUsersCanChangeGroupName.setEnabled(true);
                        allUsersCanInvite.setEnabled(true);
                        allUsersCanMessage.setEnabled(true);
                        adminsCanChangeGroupImage.setEnabled(true);
                        adminsCanChangeGroupName.setEnabled(true);
                        adminsCanInvite.setEnabled(true);
                        adminsCanMessage.setEnabled(true);
                        adminsCanRemoveUsers.setEnabled(true);
                        allUsersCanInvite.setOnClickListener(v -> {
                            if (allUsersCanInvite.isChecked()){
                                adminsCanInvite.setChecked(false);
                                adminsCanInvite.setVisibility(GONE);
                            }else{
                                adminsCanInvite.setVisibility(View.VISIBLE);
                            }
                        });
                        allUsersCanChangeGroupImage.setOnClickListener(v -> {
                            if (allUsersCanChangeGroupImage.isChecked()){
                                adminsCanChangeGroupImage.setChecked(false);
                                adminsCanChangeGroupImage.setVisibility(GONE);
                            }else{
                                adminsCanChangeGroupImage.setVisibility(View.VISIBLE);
                            }
                        });
                        allUsersCanChangeGroupName.setOnClickListener(v -> {
                            if (allUsersCanChangeGroupName.isChecked()){
                                adminsCanChangeGroupName.setChecked(false);
                                adminsCanChangeGroupName.setVisibility(GONE);
                            }else{
                                adminsCanChangeGroupName.setVisibility(View.VISIBLE);
                            }
                        });
                        allUsersCanMessage.setOnClickListener(v -> {
                            if (allUsersCanMessage.isChecked()){
                                adminsCanMessage.setChecked(false);
                                adminsCanMessage.setVisibility(GONE);
                            }else{
                                adminsCanMessage.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    if (canChangeGroupName.equals("yes")){
                        etNewGroupMessageName.setVisibility(View.VISIBLE);
                    }
                    if (canChangeGroupImage.equals("yes")){
                        setInsigniaButton.setVisibility(View.VISIBLE);
                        setInsigniaButton.setOnClickListener(v -> {
                            requestMultiplePermissions();
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, GALLERY_INSIGNIA);
                        });
                    }
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + image)
                            .into(new_message_insignia);
                    groupOptionsLoading.setVisibility(GONE);
                    groupOptionsLayout.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
                    btnSubmit.setVisibility(GONE);
                    submitGroupOptionsProg.setVisibility(GONE);
                    groupOptionsLoading.setVisibility(GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
                btnSubmit.setVisibility(GONE);
                submitGroupOptionsProg.setVisibility(GONE);
                groupOptionsLoading.setVisibility(GONE);
            }
        }, error -> {
            Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
            btnSubmit.setVisibility(GONE);
            submitGroupOptionsProg.setVisibility(GONE);
            groupOptionsLoading.setVisibility(GONE);
        });
        ((ChatActivity)mContext).addToRequestQueue(stringRequest);
    }

    private String getGroupOptions(){
        ArrayList<String> optionsList = new ArrayList<>();
        if (allUsersCanChangeGroupImage.isChecked()){
            optionsList.add("allUsersCanChangeGroupImage");
        }
        if (allUsersCanChangeGroupName.isChecked()){
            optionsList.add("allUsersCanChangeGroupName");
        }
        if (allUsersCanInvite.isChecked()){
            optionsList.add("allUsersCanInvite");
        }
        if (allUsersCanMessage.isChecked()){
            optionsList.add("allUsersCanMessage");
        }
        if (adminsCanChangeGroupImage.isChecked()){
            optionsList.add("adminsCanChangeGroupImage");
        }
        if (adminsCanChangeGroupName.isChecked()){
            optionsList.add("adminsCanChangeGroupName");
        }
        if (adminsCanInvite.isChecked()){
            optionsList.add("adminsCanInvite");
        }
        if (adminsCanMessage.isChecked()){
            optionsList.add("adminsCanMessage");
        }
        if (adminsCanRemoveUsers.isChecked()){
            optionsList.add("adminsCanRemoveUsers");
        }
        return String.valueOf(optionsList);
    }

    public void UpdateGroupMessage(final String groupMessageName, final String optionArray){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_GROUP_OPTIONS_SUBMIT, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    if(new_group_insigniaBitmap!=null){
                        uploadInsigniaImage(new_group_insigniaBitmap, group_id, etNewGroupMessageName.getText().toString());
                    }
                    ConvosFragment ldf = new ConvosFragment();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
                }else{
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
                    btnSubmit.setVisibility(View.VISIBLE);
                    submitGroupOptionsProg.setVisibility(GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            submitGroupOptionsProg.setVisibility(GONE);
            Toast.makeText(mContext,"Network Error!",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("group_name",groupMessageName);
                parms.put("option_array",optionArray);
                parms.put("group_id",group_id);
                parms.put("username",SharedPrefManager.getInstance(mContext).getUsername());
                parms.put("userid",SharedPrefManager.getInstance(mContext).getUserID());
                return parms;
            }
        };
        ((ChatActivity)mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_INSIGNIA) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    final Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
                    new_message_insignia.setImageBitmap(bitmap2);
                    new_group_insigniaBitmap = bitmap2;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadInsigniaImage(Bitmap bitmap, String group_id, String groupname){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("group_id", group_id);
            jsonObject.put("group_name", groupname);
            jsonObject.put("image", encodedImage);
            jsonObject.put("username", SharedPrefManager.getInstance(mContext).getUsername());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_INSIGNIA_URL, jsonObject,
                jsonObject -> {
                    rQueue.getCache().clear();
                    try{
                        if(jsonObject.getString("error").equals("true")){
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> {});
        rQueue = Volley.newRequestQueue(mContext);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(jsonObjectRequest);
    }
    private void requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(mContext.getApplicationContext(), "No permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mContext.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

}
