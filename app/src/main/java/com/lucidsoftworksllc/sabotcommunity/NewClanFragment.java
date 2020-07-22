package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static com.lucidsoftworksllc.sabotcommunity.Constants.ROOT_URL;

public class NewClanFragment extends Fragment {

    private ImageView newClanCover, new_clan_insignia;
    private RelativeLayout setNewClanCoverButton, setInsigniaButton;
    private TextView newClanTag, newClanName;
    private EditText etNewClanTag, etNewClanName, etNewClanDescription;
    private Button btnSubmit;
    private Context mContext;
    private String userID,username,tagTakenString;
    private ProgressBar newClanProgressBar;
    private final int GALLERY_COVER = 1;
    private final int GALLERY_INSIGNIA = 2;
    private Bitmap newClanCoverBitmap,new_clan_insigniaBitmap;
    JSONObject jsonObject;
    RequestQueue rQueue;
    public static final String URL_TAG_IN_USE = ROOT_URL + "clan_tag_used.php";
    public static final String UPLOAD_COVER_URL = Constants.ROOT_URL+"uploadCoverClan.php";
    public static final String UPLOAD_INSIGNIA_URL = Constants.ROOT_URL+"uploadInsigniaClan.php";
    public static final String URL_CLAN_SUBMIT = Constants.ROOT_URL+"submit_new_clan.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newClanRootView = inflater.inflate(R.layout.fragment_new_clan, null);

        new_clan_insignia = newClanRootView.findViewById(R.id.new_clan_insignia);
        btnSubmit = newClanRootView.findViewById(R.id.btnSubmit);
        etNewClanTag = newClanRootView.findViewById(R.id.etNewClanTag);
        etNewClanName = newClanRootView.findViewById(R.id.etNewClanName);
        etNewClanDescription = newClanRootView.findViewById(R.id.etNewClanDescription);
        newClanTag = newClanRootView.findViewById(R.id.newClanTag);
        newClanName = newClanRootView.findViewById(R.id.newClanName);
        setNewClanCoverButton = newClanRootView.findViewById(R.id.setNewClanCoverButton);
        setInsigniaButton = newClanRootView.findViewById(R.id.setInsigniaButton);
        newClanCover = newClanRootView.findViewById(R.id.newClanCover);
        newClanProgressBar = newClanRootView.findViewById(R.id.newClanProgressBar);
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        newClanCoverBitmap = null;
        new_clan_insigniaBitmap = null;
        tagTakenString = "";

        btnSubmit.setOnClickListener(v -> {
            if((etNewClanTag.getText().toString().length()>=2)){
                if (etNewClanName.getText().toString().length()>=3&&(etNewClanName.getText().toString().length()<=25)){
                    if(tagTakenString.equals("")){
                        newClanProgressBar.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.GONE);
                        SubmitClan(etNewClanTag.getText().toString(),etNewClanName.getText().toString(),etNewClanDescription.getText().toString());
                    }else{
                        etNewClanTag.requestFocus();
                        Toast.makeText(mContext, "Clan Tag Taken!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(mContext, "Clan name must be 6-25 characters long", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(mContext, "Clan tag must be at least 2 characters long", Toast.LENGTH_SHORT).show();
            }
        });

        setNewClanCoverButton.setOnClickListener(v -> {
            requestMultiplePermissions();
            Intent galleryIntent = CropImage.activity().getIntent(requireContext());
            startActivityForResult(galleryIntent, GALLERY_COVER);
        });

        setInsigniaButton.setOnClickListener(v -> {
            requestMultiplePermissions();
            Intent galleryIntent = CropImage.activity().setAspectRatio(1,1).getIntent(requireContext());
            startActivityForResult(galleryIntent, GALLERY_INSIGNIA);
        });

        etNewClanTag.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String clantag = etNewClanTag.getText().toString();
                newClanTag.setText(String.format("[%s]", clantag));
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    String clantag1 = etNewClanTag.getText().toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_TAG_IN_USE+"?userid="+userID+"&username="+username+"&tag="+ clantag1, response -> {
                        try {
                            JSONObject tagTakenObject = new JSONObject(response);
                            if(tagTakenObject.getString("error").equals("false")){
                                if(tagTakenObject.getString("result").equals("yes")){
                                    etNewClanTag.setBackgroundResource(R.color.pin);
                                    etNewClanTag.requestFocus();
                                    Toast.makeText(mContext, "Clan Tag Taken!", Toast.LENGTH_SHORT).show();
                                } else {
                                    tagTakenString = "";
                                    etNewClanTag.setBackgroundResource(R.color.colorPrimary);
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(mContext, "Network error on Response: Is Tag Taken", Toast.LENGTH_SHORT).show());
                    ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
                }, 1000);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        etNewClanName.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String clanname = etNewClanName.getText().toString();
                newClanName.setText(clanname);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
        return newClanRootView;
    }

    public void SubmitClan(final String tag, final String name, final String desc){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_CLAN_SUBMIT, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    String clanID = jsonObject.getString("clanid");
                    if(new_clan_insigniaBitmap!=null){
                        uploadInsigniaImage(new_clan_insigniaBitmap, etNewClanName.getText().toString(), etNewClanTag.getText().toString(),clanID);
                    }
                    if(newClanCoverBitmap!=null){
                        uploadCoverImage(newClanCoverBitmap, etNewClanName.getText().toString(), etNewClanTag.getText().toString(),clanID);
                    }
                    ClansListFragment ldf = new ClansListFragment();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.fragment_container, ldf).commit();
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    btnSubmit.setVisibility(View.VISIBLE);
                    newClanProgressBar.setVisibility(GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            newClanProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Error on Submit Clan, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("tag",tag);
                parms.put("name",name);
                parms.put("desc",desc);
                parms.put("username",username);
                parms.put("userid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_COVER) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap bitmap1 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
                    newClanCover.setImageBitmap(bitmap1);
                    newClanCoverBitmap = bitmap1;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Failed! Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_INSIGNIA) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    final Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
                    new_clan_insignia.setImageBitmap(bitmap2);
                    new_clan_insigniaBitmap = bitmap2;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getActivity(), "Failed! Error: "+error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadInsigniaImage(Bitmap bitmap, String clanname, String clantag, String clanID){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("clantag", clantag);
            jsonObject.put("clanname", clanname);
            jsonObject.put("image", encodedImage);
            jsonObject.put("user", username);
            jsonObject.put("clanid", clanID);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
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
                }, volleyError -> Log.e("UploadCoverFragment", volleyError.toString()));
        rQueue = Volley.newRequestQueue(mContext);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(jsonObjectRequest);
    }

    private void uploadCoverImage(Bitmap bitmap, String clanname, String clantag, String clanID){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("clantag", clantag);
            jsonObject.put("clanname", clanname);
            jsonObject.put("image", encodedImage);
            jsonObject.put("owner", username);
            jsonObject.put("clanid", clanID);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_COVER_URL, jsonObject,
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
                }, volleyError -> Log.e("UploadCoverFragment", volleyError.toString()));
        rQueue = Volley.newRequestQueue(mContext);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(jsonObjectRequest);
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
