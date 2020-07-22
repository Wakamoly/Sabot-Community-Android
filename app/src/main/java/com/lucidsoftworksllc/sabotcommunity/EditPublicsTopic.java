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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.view.View.GONE;

public class EditPublicsTopic extends Fragment {

    private static final String EDIT_TOPIC = Constants.ROOT_URL+"publics_topic_edit_submit.php";
    private static final String LOAD_GAME_PLATFORMS = Constants.ROOT_URL+"load_game_platforms.php";

    private String gamename, platform, finalPlatform, numPlayers, gameimage, userID, username, topic_id, content, gameid, title;
    private Context mContext;
    private Button btnSubmit;
    private Spinner platformSpinner, numPlayersSpinner;
    private EditText etOther, etSubject, etDescription;
    private ProgressBar newPublicsTopicProgressBar, spinnerProgress;
    private TextView textViewGame;
    private ImageView backArrow, newTopicImage;
    private LinearLayout submitDetails;
    private ArrayList<PlatformModel> PlatformArrayList;
    private ArrayList<String> platforms = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newPublicsRootView = inflater.inflate(R.layout.fragment_editpublicstopic, null);

        mContext = getActivity();
        btnSubmit = newPublicsRootView.findViewById(R.id.btnSubmit);
        textViewGame = newPublicsRootView.findViewById(R.id.textViewGame);
        platformSpinner = newPublicsRootView.findViewById(R.id.platformSpinner);
        etOther = newPublicsRootView.findViewById(R.id.etOther);
        etSubject = newPublicsRootView.findViewById(R.id.etSubject);
        etDescription = newPublicsRootView.findViewById(R.id.etDescription);
        numPlayersSpinner = newPublicsRootView.findViewById(R.id.numPlayersSpinner);
        newPublicsTopicProgressBar = newPublicsRootView.findViewById(R.id.newPublicsTopicProgressBar);
        submitDetails = newPublicsRootView.findViewById(R.id.submitDetails);
        newTopicImage = newPublicsRootView.findViewById(R.id.newTopicImage);
        spinnerProgress = newPublicsRootView.findViewById(R.id.spinnerProgress);
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        assert getArguments() != null;
        gamename = getArguments().getString("gamename");
        gameimage = getArguments().getString("gameimage");
        gameid = getArguments().getString("gameid");
        topic_id = getArguments().getString("topic_id");
        content = getArguments().getString("content");
        title = getArguments().getString("title");
        numPlayers = getArguments().getString("num_players");
        textViewGame.setText(gamename);
        etDescription.setText(content);
        etSubject.setText(title);
        backArrow = newPublicsRootView.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        platform = String.valueOf(platformSpinner.getSelectedItem());
        numPlayersSpinner.setSelection(((ArrayAdapter)numPlayersSpinner.getAdapter()).getPosition(numPlayers));
        Glide.with(mContext)
                .load(Constants.BASE_URL+gameimage)
                .error(R.mipmap.ic_launcher)
                .into(newTopicImage);

        numPlayersSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numPlayers = String.valueOf(numPlayersSpinner.getSelectedItem());
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
                SubmitEditPublicsTopic(finalPlatform, numPlayers, topic_id, body, subject, userID, username);
            }else{
                Toast.makeText(mContext,"Please fill in each field!", Toast.LENGTH_SHORT).show();
            }
        });

        loadPlatforms();
        return newPublicsRootView;
    }

    private void SubmitEditPublicsTopic(final String finalPlatform, final String numPlayers, final String topic_id, final String body, final String subject, final String submitted_by_id, final String submitted_by){
        submitDetails.setVisibility(GONE);
        newPublicsTopicProgressBar.setVisibility(View.VISIBLE);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, EDIT_TOPIC, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    Toast.makeText(mContext,"Post edited!", Toast.LENGTH_LONG).show();
                    if (jsonObject.has("topicid")){
                        if (mContext instanceof FragmentContainer) {
                            PublicsTopicFragment ldf = new PublicsTopicFragment ();
                            Bundle args = new Bundle();
                            args.putString("PublicsId", jsonObject.getString("topicid"));
                            ldf.setArguments(args);
                            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).commit();
                        }
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
                parms.put("numplayers",numPlayers);
                parms.put("topic_id",topic_id);
                parms.put("submitted_by_id",submitted_by_id);
                parms.put("body",body);
                parms.put("subject",subject);
                parms.put("submitted_by",submitted_by);
                parms.put("gamename",gamename);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
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