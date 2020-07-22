package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.app.ProgressDialog;
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
import androidx.fragment.app.FragmentTransaction;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;
import org.json.JSONArray;
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

public class ClanAdminPanel extends Fragment {

    private static final String CLAN_SAVE_SETTINGS = Constants.ROOT_URL+"save_clan_settings.php";
    private static final String LOAD_CLAN_SETTINGS = Constants.ROOT_URL+"load_clan_settings.php";
    public static final String UPLOAD_COVER_URL = Constants.ROOT_URL+"uploadCoverClan.php";
    public static final String UPLOAD_INSIGNIA_URL = Constants.ROOT_URL+"uploadInsigniaClan.php";

    private RelativeLayout topPanelLayout, clanAdminPanelCenter, errorLayout,setClanCoverButton,setClanPhotoButton;
    private MaterialRippleLayout manageMembersLayout;
    private ProgressDialog progressDialog;
    private ProgressBar clanAdminPanelProgress;
    private ImageView saveChanges, backArrow, backgroundPanel,insigniaPanel;
    private final int GALLERY_COVER = 1;
    private final int GALLERY_INSIGNIA = 2;
    private Bitmap newClanCoverBitmap,new_clan_insigniaBitmap;
    JSONObject jsonObject;
    RequestQueue rQueue;
    private TextView editTVclanname, clanTagView;
    private EditText edit_clanname, descriptionET, facebookInput, instaInput, twitterInput, youtubeInput, discordInput, websiteInput;
    private Context mContext;
    private String userID,username,clanID,mClanname,mClantag;
    private Button manageMembers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View clanAdminRootView = inflater.inflate(R.layout.fragment_clanadminpanel, null);

        topPanelLayout = clanAdminRootView.findViewById(R.id.topPanelLayout);
        clanAdminPanelCenter = clanAdminRootView.findViewById(R.id.clanAdminPanelCenter);
        clanAdminPanelProgress = clanAdminRootView.findViewById(R.id.clanAdminPanelProgress);
        saveChanges = clanAdminRootView.findViewById(R.id.saveChanges);
        backArrow = clanAdminRootView.findViewById(R.id.backArrow);
        insigniaPanel = clanAdminRootView.findViewById(R.id.insigniaPanel);
        editTVclanname = clanAdminRootView.findViewById(R.id.editTVclanname);
        edit_clanname = clanAdminRootView.findViewById(R.id.edit_clanname);
        manageMembersLayout = clanAdminRootView.findViewById(R.id.manageMembersLayout);
        errorLayout = clanAdminRootView.findViewById(R.id.errorLayout);
        facebookInput = clanAdminRootView.findViewById(R.id.facebookInput);
        instaInput = clanAdminRootView.findViewById(R.id.instaInput);
        twitterInput = clanAdminRootView.findViewById(R.id.twitterInput);
        youtubeInput = clanAdminRootView.findViewById(R.id.youtubeInput);
        discordInput = clanAdminRootView.findViewById(R.id.discordInput);
        websiteInput = clanAdminRootView.findViewById(R.id.websiteInput);
        descriptionET = clanAdminRootView.findViewById(R.id.descriptionET);
        clanTagView = clanAdminRootView.findViewById(R.id.clanTagView);
        backgroundPanel = clanAdminRootView.findViewById(R.id.backgroundPanel);
        manageMembers = clanAdminRootView.findViewById(R.id.manageMembers);
        setClanCoverButton = clanAdminRootView.findViewById(R.id.setClanCoverButton);
        setClanPhotoButton = clanAdminRootView.findViewById(R.id.setClanPhotoButton);
        progressDialog = new ProgressDialog(getActivity());
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        assert getArguments() != null;
        clanID = getArguments().getString("ClanId");


        saveChanges.setOnClickListener(v -> saveChangesClick());
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        setClanCoverButton.setOnClickListener(v -> {
            requestMultiplePermissions();
            Intent galleryIntent = CropImage.activity().getIntent(requireContext());
            startActivityForResult(galleryIntent, GALLERY_COVER);
        });
        setClanPhotoButton.setOnClickListener(v -> {
            requestMultiplePermissions();
            Intent galleryIntent = CropImage.activity().setAspectRatio(1,1).getIntent(requireContext());
            startActivityForResult(galleryIntent, GALLERY_INSIGNIA);
        });

        loadClanSettings();
        return clanAdminRootView;
    }

    private void loadClanSettings() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LOAD_CLAN_SETTINGS+"?userid="+userID+"&username="+username+"&clanid="+clanID, response -> {
            try {
                JSONArray profiletop = new JSONArray(response);
                JSONObject profiletopObject = profiletop.getJSONObject(0);

                String banned = profiletopObject.getString("banned");
                String deleted = profiletopObject.getString("deleted");
                if(banned.equals("yes")||deleted.equals("yes")){
                    clanAdminPanelProgress.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.VISIBLE);
                }else{
                    //String owner = profiletopObject.getString("owner");
                    final String id = profiletopObject.getString("id");
                    final String clanname = profiletopObject.getString("clanname");
                    final String clantag = profiletopObject.getString("clantag");
                    String description = profiletopObject.getString("description");
                    String insignia = profiletopObject.getString("insignia");
                    String background = profiletopObject.getString("background");
                    String facebook = profiletopObject.getString("facebook");
                    String instagram = profiletopObject.getString("instagram");
                    String twitter = profiletopObject.getString("twitter");
                    String youtube = profiletopObject.getString("youtube");
                    String website = profiletopObject.getString("website");
                    String discord = profiletopObject.getString("discord");
                    //String gamesarray = profiletopObject.getString("games");

                    mClanname = clanname;
                    mClantag = clantag;

                    facebookInput.setText(facebook);
                    instaInput.setText(instagram);
                    twitterInput.setText(twitter);
                    youtubeInput.setText(youtube);
                    websiteInput.setText(website);
                    descriptionET.setText(description);
                    clanTagView.setText(String.format("[%s]", clantag));
                    discordInput.setText(discord);
                    editTVclanname.setText(clanname);
                    edit_clanname.setText(clanname);
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + insignia)
                            .error(R.mipmap.ic_launcher)
                            .into(insigniaPanel);
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + background)
                            .error(R.mipmap.ic_launcher)
                            .into(backgroundPanel);
                    clanAdminPanelProgress.setVisibility(View.GONE);
                    clanAdminPanelCenter.setVisibility(View.VISIBLE);
                    manageMembers.setOnClickListener(view -> {
                        ClanManageMembers ldf = new ClanManageMembers();
                        Bundle args = new Bundle();
                        args.putString("ClanId", id);
                        args.putString("Clanname", clanname);
                        args.putString("Clantag", clantag);
                        ldf.setArguments(args);
                        ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            errorLayout.setVisibility(View.VISIBLE);
            clanAdminPanelProgress.setVisibility(View.GONE);
        });
        Volley.newRequestQueue(mContext).add(stringRequest);
    }

    private void saveChangesClick(){
        progressDialog.setMessage("Saving your settings...");
        progressDialog.show();
        final String facebook = facebookInput.getText().toString().trim();
        final String insta = instaInput.getText().toString().trim();
        final String twitter = twitterInput.getText().toString().trim();
        final String youtube = youtubeInput.getText().toString().trim();
        final String website = websiteInput.getText().toString().trim();
        final String description = descriptionET.getText().toString().trim();
        final String discord = discordInput.getText().toString().trim();
        final String clanname = edit_clanname.getText().toString().trim();
        final String username = SharedPrefManager.getInstance(getActivity()).getUsername();
        if((!clanname.isEmpty())&&(!username.isEmpty())){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                    CLAN_SAVE_SETTINGS,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (jsonObject.getString("error").equals("false")) {
                                requireActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        progressDialog.hide();
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("userid", userID);
                    params.put("clanid", clanID);
                    params.put("facebook", facebook);
                    params.put("insta", insta);
                    params.put("twitter", twitter);
                    params.put("youtube", youtube);
                    params.put("website", website);
                    params.put("description", description);
                    params.put("discord", discord);
                    params.put("clanname", clanname);
                    return params;
                }
            };
            RequestHandler.getInstance(mContext).addToRequestQueue(stringRequest);
        }else{
            progressDialog.dismiss();
            Toast.makeText(mContext, "Please fill in clan name field!", Toast.LENGTH_LONG).show();
        }
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
                    newClanCoverBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
                    uploadCoverImage(newClanCoverBitmap, mClanname, mClantag);
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
                    new_clan_insigniaBitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), resultUri);
                    uploadInsigniaImage(new_clan_insigniaBitmap,mClanname,mClantag);
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

    private void uploadInsigniaImage(Bitmap bitmap, String clanname, String clantag){
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
            jsonObject.put("owner", username);
            jsonObject.put("clanid", clanID);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_INSIGNIA_URL, jsonObject,
                jsonObject -> {
                    rQueue.getCache().clear();
                    try{
                        if(jsonObject.getString("error").equals("false")){
                            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            if (currentFragment instanceof ClanAdminPanel) {
                                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                fragTransaction.detach(currentFragment);
                                fragTransaction.attach(currentFragment);
                                fragTransaction.commit();
                            }
                        }else{
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

    private void uploadCoverImage(Bitmap bitmap, String clanname, String clantag){
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
                        if(jsonObject.getString("error").equals("false")){
                            Fragment currentFragment = requireActivity().getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            if (currentFragment instanceof ClanAdminPanel) {
                                FragmentTransaction fragTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                fragTransaction.detach(currentFragment);
                                fragTransaction.attach(currentFragment);
                                fragTransaction.commit();
                            }
                        }else{
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
                            Toast.makeText(mContext, "No permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mContext, "Error!", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

}
