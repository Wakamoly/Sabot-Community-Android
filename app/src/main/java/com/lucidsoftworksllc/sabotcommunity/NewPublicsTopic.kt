package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
import static android.view.View.GONE;

public class NewPublicsTopic extends Fragment {

    private static final String SUBMIT_TOPIC = Constants.ROOT_URL+"publics_topic_submit.php";
    private static final String TOPIC_NOTIFY = Constants.ROOT_URL+"publics_action.php/post_notify";
    private static final String LOAD_GAME_PLATFORMS = Constants.ROOT_URL+"load_game_platforms.php";

    private String gameid, gamename, platform, finalPlatform, numPlayers, whenText, gameimage, userID, username;
    private Context mContext;
    private CheckBox playingNowCheck;
    private Button btnSubmit;
    private Spinner platformSpinner, whenSpinner, numPlayersSpinner;
    private EditText etOther, etSubject, etDescription;
    private ProgressBar newPublicsTopicProgressBar, spinnerProgress;
    private TextView textViewGame,howTo,whenTextView;
    private ImageView backArrow, newTopicImage, platformInfo, whenInfo, playersInfo, subjectInfo, descInfo;
    private LinearLayout submitDetails;
    private ArrayList<PlatformModel> PlatformArrayList;
    private ArrayList<String> platforms = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newPublicsRootView = inflater.inflate(R.layout.fragment_newpublicstopic, null);

        mContext = getActivity();
        btnSubmit = newPublicsRootView.findViewById(R.id.btnSubmit);
        textViewGame = newPublicsRootView.findViewById(R.id.textViewGame);
        platformSpinner = newPublicsRootView.findViewById(R.id.platformSpinner);
        etOther = newPublicsRootView.findViewById(R.id.etOther);
        etSubject = newPublicsRootView.findViewById(R.id.etSubject);
        whenSpinner = newPublicsRootView.findViewById(R.id.whenSpinner);
        etDescription = newPublicsRootView.findViewById(R.id.etDescription);
        numPlayersSpinner = newPublicsRootView.findViewById(R.id.numPlayersSpinner);
        newPublicsTopicProgressBar = newPublicsRootView.findViewById(R.id.newPublicsTopicProgressBar);
        submitDetails = newPublicsRootView.findViewById(R.id.submitDetails);
        newTopicImage = newPublicsRootView.findViewById(R.id.newTopicImage);
        spinnerProgress = newPublicsRootView.findViewById(R.id.spinnerProgress);
        platformInfo = newPublicsRootView.findViewById(R.id.platformInfo);
        whenInfo = newPublicsRootView.findViewById(R.id.whenInfo);
        playersInfo = newPublicsRootView.findViewById(R.id.playersInfo);
        subjectInfo = newPublicsRootView.findViewById(R.id.subjectInfo);
        descInfo = newPublicsRootView.findViewById(R.id.descInfo);
        howTo = newPublicsRootView.findViewById(R.id.howTo);
        playingNowCheck = newPublicsRootView.findViewById(R.id.playingNowCheck);
        whenTextView = newPublicsRootView.findViewById(R.id.whenTextView);
        userID = SharedPrefManager.getInstance(getActivity()).getUserID();
        username = SharedPrefManager.getInstance(getActivity()).getUsername();

        assert getArguments() != null;
        gameid = getArguments().getString("gameid");
        gamename = getArguments().getString("gamename");
        gameimage = getArguments().getString("gameimage");
        textViewGame.setText(gamename);
        backArrow = newPublicsRootView.findViewById(R.id.backArrow);
        playingNowCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                whenTextView.setText(R.string.keep_post_open);
            }
            else{
                whenTextView.setText(R.string.when);
            }
        });
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        platform = String.valueOf(platformSpinner.getSelectedItem());
        numPlayers = String.valueOf(numPlayersSpinner.getSelectedItem());
        whenText = String.valueOf(whenSpinner.getSelectedItem());
        Glide.with(mContext)
                .load(Constants.BASE_URL+gameimage)
                .error(R.mipmap.ic_launcher)
                .into(newTopicImage);
        howTo.setOnClickListener(v -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_info_grey_24dp)
                .setTitle(R.string.how_does_this_work)
                .setMessage(R.string.how_it_works)
                .setPositiveButton(android.R.string.ok, v1 -> {
                })
                .show());

        platformInfo.setOnClickListener(v -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_info_grey_24dp)
                .setTitle(R.string.platform)
                .setMessage(R.string.platform_how_it_works)
                .setPositiveButton(android.R.string.ok, v1 -> {
                })
                .show());

        whenInfo.setOnClickListener(v -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_info_grey_24dp)
                .setTitle(R.string.when)
                .setMessage(R.string.when_how_it_works)
                .setPositiveButton(android.R.string.ok, v1 -> {
                })
                .show());

        playersInfo.setOnClickListener(v -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_info_grey_24dp)
                .setTitle(R.string.players_needed)
                .setMessage(R.string.players_how_it_works)
                .setPositiveButton(android.R.string.ok, v1 -> {
                })
                .show());

        subjectInfo.setOnClickListener(v -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_info_grey_24dp)
                .setTitle(R.string.subject_text)
                .setMessage(R.string.subject_how_it_works)
                .setPositiveButton(android.R.string.ok, v1 -> {
                })
                .show());

        descInfo.setOnClickListener(v -> new LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                .setTopColorRes(R.color.green)
                .setButtonsColorRes(R.color.green)
                .setIcon(R.drawable.ic_info_grey_24dp)
                .setTitle(R.string.description_text)
                .setMessage(R.string.desc_how_it_works)
                .setPositiveButton(android.R.string.ok, v1 -> {
                })
                .show());

        numPlayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numPlayers = String.valueOf(numPlayersSpinner.getSelectedItem());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        whenSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                whenText = String.valueOf(whenSpinner.getSelectedItem());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        platformSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                platform = String.valueOf(platformSpinner.getSelectedItem());
                if (platform.equals("Other"))
                    etOther.setVisibility(View.VISIBLE);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSubmit.setOnClickListener(v -> {
            String body = etDescription.getText().toString();
            String subject = etSubject.getText().toString();
            if (platform.equals("Other")){
                finalPlatform = etOther.getText().toString();
            }else{
                finalPlatform = platform;
            }
            if(!body.equals("")&&!subject.equals("")&&!finalPlatform.equals("")){
                SubmitPublicsTopic(finalPlatform, whenText, numPlayers, gameid, body, subject, userID, username);
            }else{
                Toast.makeText(mContext,"Please fill in each field!", Toast.LENGTH_SHORT).show();
            }
        });

        loadPlatforms();
        return newPublicsRootView;
    }

    private void SubmitPublicsTopic(final String finalPlatform, final String whenText, final String numPlayers, final String gameid, final String body, final String subject, final String submitted_by_id, final String submitted_by){
        submitDetails.setVisibility(GONE);
        newPublicsTopicProgressBar.setVisibility(View.VISIBLE);
        String isPlayingNow = "no";
        if (playingNowCheck.isChecked()){
            isPlayingNow = "yes";
        }
        String finalIsPlayingNow = isPlayingNow;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, SUBMIT_TOPIC, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    Toast.makeText(mContext,"New Publics posted!", Toast.LENGTH_LONG).show();
                    if (jsonObject.has("topicid")){
                        sendToTopic(jsonObject.getString("topicid"));
                    }else {
                        requireActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    newPublicsTopicProgressBar.setVisibility(GONE);
                    submitDetails.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            newPublicsTopicProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Error, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("platform",finalPlatform);
                parms.put("whentext",whenText);
                parms.put("numplayers",numPlayers);
                parms.put("gameid",gameid);
                parms.put("submitted_by_id",submitted_by_id);
                parms.put("body",body);
                parms.put("subject",subject);
                parms.put("submitted_by",submitted_by);
                parms.put("gamename",gamename);
                parms.put("isplayingnow", finalIsPlayingNow);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void sendToTopic(String topicID){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, TOPIC_NOTIFY, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    Toast.makeText(mContext,"Players notified!", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            newPublicsTopicProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Error on notifying!",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("topicID",topicID);
                parms.put("username",username);
                parms.put("gameid",gameid);
                parms.put("gamename",gamename);
                parms.put("platform",finalPlatform);
                parms.put("numplayers",numPlayers);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
        if (mContext instanceof FragmentContainer) {
            PublicsTopicFragment ldf = new PublicsTopicFragment ();
            Bundle args = new Bundle();
            args.putString("PublicsId", topicID);
            ldf.setArguments(args);
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).commit();
        }
    }

    private void loadPlatforms(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LOAD_GAME_PLATFORMS+"?userid="+userID+"&username="+username+"&gameid="+gameid,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if(obj.optString("error").equals("false")){
                            PlatformArrayList = new ArrayList<>();
                            JSONArray dataArray  = obj.getJSONArray("platforms");
                            for (int i = 0; i < dataArray.length(); i++) {
                                PlatformModel platformModel = new PlatformModel();
                                JSONObject dataobj = dataArray.getJSONObject(i);
                                if(!dataobj.getString("platform").equals("")){
                                    platformModel.setPlatform(dataobj.getString("platform"));
                                    PlatformArrayList.add(platformModel);
                                }
                            }
                            for (int i = 0; i < PlatformArrayList.size(); i++) {
                                platforms.add(PlatformArrayList.get(i).getPlatform());
                            }
                            platforms.add("Other");
                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(mContext, R.layout.spinner_item, platforms);
                            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            platformSpinner.setAdapter(spinnerArrayAdapter);
                            platformSpinner.setSelection(0);
                            spinnerProgress.setVisibility(View.GONE);
                            platformSpinner.setVisibility(View.VISIBLE);
                        }else{
                            platformSpinner.setVisibility(GONE);
                            spinnerProgress.setVisibility(GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(mContext, error.getMessage(), Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}