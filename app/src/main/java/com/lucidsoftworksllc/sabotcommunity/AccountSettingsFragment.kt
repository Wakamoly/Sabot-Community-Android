package com.lucidsoftworksllc.sabotcommunity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyCustomDialog;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsFragment extends Fragment {

    private static final String CLOSE_ACCOUNT = Constants.ROOT_URL+"user_close_account.php";
    public CircleImageView imageViewProfilePic, saveChanges;
    private ProgressDialog progressDialog;
    public RelativeLayout editProfileLayout, setProfilePhotoButton;
    private LinearLayout clanTagLayout;
    private EditText websiteInputET,nintendoIDInputET,twitterInputET,discordUserET,discordInputET, textViewEmail, textViewNickname, textViewDescription, editTextPasswordNew, editTextPasswordOld, editTextPasswordOldVerify, twitchInputET, mixerInputET, psnInputET, xboxInputET, steamInputET, instagramInputET, youtubeInputET;
    private TextView textViewUsername, lastNotiText;
    private ProgressBar mProgressBar, spinnerProgress;
    public String userID, username, currentTag, newFrequency;
    private Button closeAccount, notiPlatformButton;
    private ImageView frequencyInfoButton;
    private static final String ProfileEdit_URL = Constants.ROOT_URL+"profileedit_api.php";
    private static final String ProfileEditSave_URL = Constants.ROOT_URL+"profileeditsave_api.php";
    private static final String LOAD_CLAN_TAGS = Constants.ROOT_URL+"load_editprofile_clan_tags.php";
    private ArrayList<ClanTagModel> ClanTagModelArrayList;
    private ArrayList<String> tags = new ArrayList<>();
    private Spinner clanTagSpinner, frequencySpinner;
    private Context mCtx;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View editProfileRootView = inflater.inflate(R.layout.fragment_editprofile, null);
        ImageView backArrow = editProfileRootView.findViewById(R.id.backArrow);
        twitchInputET = editProfileRootView.findViewById(R.id.twitchInputET);
        mixerInputET = editProfileRootView.findViewById(R.id.mixerInputET);
        psnInputET = editProfileRootView.findViewById(R.id.psnInputET);
        xboxInputET = editProfileRootView.findViewById(R.id.xboxInputET);
        steamInputET = editProfileRootView.findViewById(R.id.steamInputET);
        saveChanges = editProfileRootView.findViewById(R.id.saveChanges);
        editTextPasswordNew = editProfileRootView.findViewById(R.id.passwordNew);
        editTextPasswordOld = editProfileRootView.findViewById(R.id.password1);
        editTextPasswordOldVerify = editProfileRootView.findViewById(R.id.password2);
        editProfileLayout = editProfileRootView.findViewById(R.id.editProfileLayout);
        setProfilePhotoButton = editProfileRootView.findViewById(R.id.changeProfilePhoto);
        mProgressBar = editProfileRootView.findViewById(R.id.profileEditProgressBar);
        textViewNickname = editProfileRootView.findViewById(R.id.edit_nickname);
        textViewUsername = editProfileRootView.findViewById(R.id.editTVusername);
        textViewDescription = editProfileRootView.findViewById(R.id.description);
        imageViewProfilePic = editProfileRootView.findViewById(R.id.profile_photo);
        textViewEmail = editProfileRootView.findViewById(R.id.email);
        clanTagSpinner = editProfileRootView.findViewById(R.id.clanTagSpinner);
        spinnerProgress = editProfileRootView.findViewById(R.id.spinnerProgress);
        clanTagLayout = editProfileRootView.findViewById(R.id.clanTagLayout);
        closeAccount = editProfileRootView.findViewById(R.id.closeAccount);
        discordInputET = editProfileRootView.findViewById(R.id.discordInputET);
        frequencyInfoButton = editProfileRootView.findViewById(R.id.frequencyInfoButton);
        frequencySpinner = editProfileRootView.findViewById(R.id.frequencySpinner);
        lastNotiText = editProfileRootView.findViewById(R.id.lastNotiText);
        youtubeInputET = editProfileRootView.findViewById(R.id.youtubeInputET);
        instagramInputET = editProfileRootView.findViewById(R.id.instagramInputET);
        notiPlatformButton = editProfileRootView.findViewById(R.id.notiPlatformButton);
        discordUserET = editProfileRootView.findViewById(R.id.discordUserET);
        twitterInputET = editProfileRootView.findViewById(R.id.twitterInputET);
        nintendoIDInputET = editProfileRootView.findViewById(R.id.nintendoIDInputET);
        //websiteInputET = editProfileRootView.findViewById(R.id.websiteInputET);
        mCtx = getActivity();
        progressDialog = new ProgressDialog(mCtx);
        userID = SharedPrefManager.getInstance(mCtx).getUserID();
        username = SharedPrefManager.getInstance(mCtx).getUsername();
        currentTag = "";
        closeAccount.setOnClickListener(v -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (mCtx instanceof FragmentContainer) {
                            closeAccountAction();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:break;
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(mCtx, R.style.AlertDialogStyle);
            builder.setMessage(R.string.closing_account).setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        notiPlatformButton.setOnClickListener(v -> {
            LayoutInflater li = LayoutInflater.from(mCtx);
            View dialog_view = li.inflate(R.layout.dialog_noti_platforms, null);
            CheckBox playstationcb = dialog_view.findViewById(R.id.playstation);
            CheckBox xboxcb = dialog_view.findViewById(R.id.xbox);
            CheckBox steamcb = dialog_view.findViewById(R.id.steam);
            CheckBox pccb = dialog_view.findViewById(R.id.pc);
            CheckBox mobilecb = dialog_view.findViewById(R.id.mobile);
            CheckBox nswitchcb = dialog_view.findViewById(R.id.nswitch);
            CheckBox crossplatformcb = dialog_view.findViewById(R.id.crossplatform);
            CheckBox othercb = dialog_view.findViewById(R.id.other);
            try {
                JSONArray notiPlatformsData = new JSONArray(SharedPrefManager.getInstance(mCtx).getNotiPlatforms());
                String[] objectName = new String[notiPlatformsData.length()];
                for(int i=0; i<notiPlatformsData.length(); i++){
                    objectName[i] = notiPlatformsData.getString(i);
                    switch (objectName[i]) {
                        case "PlayStation":
                            playstationcb.setChecked(true);
                            continue;
                        case "Xbox":
                            xboxcb.setChecked(true);
                            continue;
                        case "Steam":
                            steamcb.setChecked(true);
                            continue;
                        case "PC":
                            pccb.setChecked(true);
                            continue;
                        case "Mobile":
                            mobilecb.setChecked(true);
                            continue;
                        case "Switch":
                            nswitchcb.setChecked(true);
                            continue;
                        case "Cross-Platform":
                            crossplatformcb.setChecked(true);
                            continue;
                        case "Other":
                            othercb.setChecked(true);
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            final LovelyCustomDialog dialog = new LovelyCustomDialog(mCtx);
                    dialog.setView(dialog_view)
                    .setTopColorRes(R.color.green)
                    .setTitle(R.string.get_notifications_for_platforms)
                    .setIcon(R.drawable.ic_friend_add)
                    .setMessage(R.string.noti_platform_message)
                    .setListener(R.id.saveBtn, v1 -> {
                        ArrayList<String> platformListSave = new ArrayList<>();
                        if (playstationcb.isChecked()){
                            platformListSave.add("PlayStation");
                        }
                        if (xboxcb.isChecked()){
                            platformListSave.add("Xbox");
                        }
                        if (steamcb.isChecked()){
                            platformListSave.add("Steam");
                        }
                        if (pccb.isChecked()){
                            platformListSave.add("PC");
                        }
                        if (mobilecb.isChecked()){
                            platformListSave.add("Mobile");
                        }
                        if (nswitchcb.isChecked()){
                            platformListSave.add("Switch");
                        }
                        if (crossplatformcb.isChecked()){
                            platformListSave.add("Cross-Platform");
                        }
                        if (othercb.isChecked()){
                            platformListSave.add("Other");
                        }
                        SharedPrefManager.getInstance(mCtx).setNotiPlatforms(String.valueOf(platformListSave));
                        dialog.dismiss();
                    })
                    .show();
        });
        frequencySpinner.setSelection(getIndex(frequencySpinner,SharedPrefManager.getInstance(mCtx).getNotificationFrequency()));
        lastNotiText.setText(SharedPrefManager.getInstance(mCtx).getLastNoti());

        saveChanges.setOnClickListener(v -> saveChangesClick("toProfile"));
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());

        setProfilePhotoButton.setOnClickListener(view -> new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.grey)
                .setIcon(R.drawable.ic_check)
                .setTitle(R.string.save_settings_query)
                .setMessage(R.string.leaving_account_settings)
                .setPositiveButton(R.string.yes, v -> saveChangesClick("toProfilePicture"))
                .setNegativeButton(R.string.no, v -> {
                    Fragment asf = new UploadProfilePhotoFragment();
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, asf);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                })
                .show());
        loadEditProfile();
        return editProfileRootView;
    }


    private void loadEditProfile() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ProfileEdit_URL + "?userid=" + userID + "&username=" + username, response -> {
            try {
                JSONArray profiletop = new JSONArray(response);
                JSONObject profiletopObject = profiletop.getJSONObject(0);

                String nickname = profiletopObject.getString("nickname");
                String username = profiletopObject.getString("username");
                String description = profiletopObject.getString("description");
                //String signup_date = profiletopObject.getString("signup_date");
                String profile_pic = profiletopObject.getString("profile_pic");
                String email = profiletopObject.getString("email");
                String twitch = profiletopObject.getString("twitch");
                String mixer = profiletopObject.getString("mixer");
                String psn = profiletopObject.getString("psn");
                String xbox = profiletopObject.getString("xbox");
                String steam = profiletopObject.getString("steam");
                String discord = profiletopObject.getString("discord");
                //TODO finish this
                String instagram = profiletopObject.getString("instagram");
                String youtube = profiletopObject.getString("youtube");
                //String website = profiletopObject.getString("website");
                String twitter = profiletopObject.getString("twitter");
                String nintendo = profiletopObject.getString("nintendo");
                String discord_user = profiletopObject.getString("discord_user");

                currentTag = (profiletopObject.getString("clantag"));
                frequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        newFrequency = String.valueOf(frequencySpinner.getSelectedItem());
                        SharedPrefManager.getInstance(mCtx).setNotificationFrequency(newFrequency);
                        Toast.makeText(mCtx, "Publics notification frequency changed to "+SharedPrefManager.getInstance(mCtx).getNotificationFrequency()+"!", Toast.LENGTH_SHORT).show();
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });
                frequencyInfoButton.setOnClickListener(v -> new LovelyInfoDialog(mCtx)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_info_grey_24dp)
                        .setTitle(R.string.publics_frequency_text)
                        .setMessage(R.string.publics_noti_info_message)
                        .show());
                loadClantags();

                twitchInputET.setText(twitch);
                mixerInputET.setText(mixer);
                psnInputET.setText(psn);
                xboxInputET.setText(xbox);
                steamInputET.setText(steam);
                textViewDescription.setText(description);
                String usernameText = "@"+username;
                textViewUsername.setText(usernameText);
                textViewNickname.setText(nickname);
                textViewEmail.setText(email);
                discordInputET.setText(discord);
                instagramInputET.setText(instagram);
                youtubeInputET.setText(youtube);
                discordUserET.setText(discord_user);
                //websiteInputET.setText(website);
                twitterInputET.setText(twitter);
                nintendoIDInputET.setText(nintendo);

                Glide.with(mCtx)
                        .load(Constants.BASE_URL+ profile_pic)
                        .error(R.mipmap.ic_launcher)
                        .into(imageViewProfilePic);
                mProgressBar.setVisibility(View.GONE);
                editProfileLayout.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mCtx, "Network Error!", Toast.LENGTH_SHORT).show());
        Volley.newRequestQueue(mCtx).add(stringRequest);
    }

    private void saveChangesClick(final String action){
        progressDialog.setMessage("Saving your settings...");
        progressDialog.show();
        final String instagram = instagramInputET.getText().toString().trim();
        final String youtube = youtubeInputET.getText().toString().trim();
        final String discord = discordInputET.getText().toString().trim();
        final String discordUser = discordUserET.getText().toString().trim();
        final String twitter = twitterInputET.getText().toString().trim();
        final String nintendoID = nintendoIDInputET.getText().toString().trim();
        final String twitchNew = twitchInputET.getText().toString().trim();
        final String mixerNew = mixerInputET.getText().toString().trim();
        final String psnNew = psnInputET.getText().toString().trim();
        final String xboxNew = xboxInputET.getText().toString().trim();
        final String steamNew = steamInputET.getText().toString().trim();
        final String email = textViewEmail.getText().toString().trim();
        final String bio = textViewDescription.getText().toString().trim();
        final String nickname = textViewNickname.getText().toString().trim();
        final String username = SharedPrefManager.getInstance(mCtx).getUsername();
        String spinnertext = String.valueOf(clanTagSpinner.getSelectedItem());
        final String finalspinnertext;
        if (!spinnertext.equals("None")&&!spinnertext.equals("null")&&!spinnertext.isEmpty()){
            finalspinnertext = spinnertext.substring(1, spinnertext.length() - 1);
        }else{
            finalspinnertext = "";
        }
        String errorText = "";
        String passwordNew = "";
        String passwordOld = "";
        String passwordOldVerify = "";
        if((!editTextPasswordNew.getText().toString().isEmpty())&&(!editTextPasswordOld.getText().toString().isEmpty())&&(!editTextPasswordOldVerify.getText().toString().isEmpty())){
            if (!editTextPasswordOld.getText().toString().isEmpty() || !editTextPasswordNew.getText().toString().isEmpty() || !editTextPasswordOldVerify.getText().toString().isEmpty()) {
                if (editTextPasswordOldVerify.getText().toString().equals(editTextPasswordOld.getText().toString())) {
                    passwordNew = editTextPasswordNew.getText().toString().trim();
                    passwordOld = editTextPasswordOld.getText().toString().trim();
                    passwordOldVerify = editTextPasswordOldVerify.getText().toString().trim();
                }else{
                    progressDialog.dismiss();
                    errorText = "1";
                    Toast.makeText(mCtx, "Password verify does not match old password input", Toast.LENGTH_LONG).show();
                }
            }else{
                progressDialog.dismiss();
                errorText = "2";
                Toast.makeText(mCtx, "Please fill in all 3 password fields", Toast.LENGTH_LONG).show();
            }
        }
        final String finalPasswordNew = passwordNew;
        final String finalPasswordOld = passwordOld;
        final String finalPasswordOldVerify = passwordOldVerify;
        if(errorText.isEmpty()){
            StringRequest stringRequest = new StringRequest(Request.Method.POST,
                ProfileEditSave_URL,
                    response -> {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            if (jsonObject.getString("error").equals("false")) {
                                if (action.equals("toProfile")){
                                    Fragment asf = new FragmentProfile();
                                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, asf);
                                    fragmentTransaction.commit();
                                }else if (action.equals("toProfilePicture")){
                                    Fragment asf = new UploadProfilePhotoFragment();
                                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, asf);
                                    fragmentTransaction.commit();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        progressDialog.hide();
                        Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_LONG).show();
                    }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("nickname", nickname);
                params.put("email", email);
                params.put("bio", bio);
                params.put("passwordNew", finalPasswordNew);
                params.put("passwordOld", finalPasswordOld);
                params.put("passwordVerify", finalPasswordOldVerify);
                params.put("twitch", twitchNew);
                params.put("mixer", mixerNew);
                params.put("youtube", youtube);
                params.put("discord_server", discord);
                params.put("nintendoid", nintendoID);
                params.put("twitter", twitter);
                params.put("discord_user", discordUser);
                params.put("instagram", instagram);
                params.put("psn", psnNew);
                params.put("xbox", xboxNew);
                params.put("steam", steamNew);
                params.put("clantag",finalspinnertext);
                return params;
            }
        };
        RequestHandler.getInstance(mCtx).addToRequestQueue(stringRequest);
        }
    }

    private void loadClantags(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LOAD_CLAN_TAGS+"?userid="+userID+"&username="+username,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.optString("error").equals("false")){
                            ClanTagModelArrayList = new ArrayList<>();
                            if(!obj.has("tags")){
                                clanTagLayout.setVisibility(View.GONE);
                            }else {
                                JSONArray dataArray  = obj.getJSONArray("tags");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    ClanTagModel clanTagModel = new ClanTagModel();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    if(!dataobj.getString("tag").equals("")){
                                        clanTagModel.setTag("["+dataobj.getString("tag")+"]");
                                        ClanTagModelArrayList.add(clanTagModel);
                                    }
                                }
                                for (int i = 0; i < ClanTagModelArrayList.size(); i++) {
                                    tags.add(ClanTagModelArrayList.get(i).getTag());
                                }
                                tags.add("None");
                                tags.add("[" + currentTag + "]");
                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mCtx, R.layout.spinner_item, tags);
                                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                                clanTagSpinner.setAdapter(spinnerArrayAdapter);

                                ArrayAdapter myAdap = (ArrayAdapter) clanTagSpinner.getAdapter(); //cast to an ArrayAdapter
                                int spinnerPosition = myAdap.getPosition("[" + currentTag + "]");
                                clanTagSpinner.setSelection(spinnerPosition);
                                spinnerProgress.setVisibility(View.GONE);
                                clanTagSpinner.setVisibility(View.VISIBLE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_SHORT).show());
        RequestQueue requestQueue = Volley.newRequestQueue(mCtx);
        requestQueue.add(stringRequest);
    }

    private void closeAccountAction(){
        progressDialog.setMessage("Saving your settings...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                CLOSE_ACCOUNT,
                response -> {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("error").equals("false")) {
                            Toast.makeText(mCtx,R.string.user_closed, Toast.LENGTH_SHORT).show();
                            SharedPrefManager.getInstance(mCtx).logout();
                            Intent toLogin = new Intent(mCtx, LoginActivity.class);
                            toLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            requireActivity().finish();
                            startActivity(toLogin);
                        }else{
                            Toast.makeText(mCtx, R.string.could_not_close_account, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    progressDialog.hide();
                    Toast.makeText(mCtx, error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("user_id", userID);
                params.put("noti_token", SharedPrefManager.getInstance(mCtx).getFCMToken());
                return params;
            }
        };
        RequestHandler.getInstance(mCtx).addToRequestQueue(stringRequest);
    }

    private int getIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

}





