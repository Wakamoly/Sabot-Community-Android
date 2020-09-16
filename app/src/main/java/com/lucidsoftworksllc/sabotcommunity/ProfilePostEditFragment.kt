package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.view.View.GONE;
public class ProfilePostEditFragment extends Fragment {

    private static final String ProfilePost_URL = Constants.ROOT_URL+"profilePostEdit.php";
    private static final String POST_EDIT_SAVE = Constants.ROOT_URL+"profile_post_action.php/post_save";

    private CircleImageView verifiedView, onlineView;
    private LinearLayout urlBits,urlPreview;
    private RelativeLayout publicsTopicList;
    private MaterialRippleLayout contentLayout;
    private ProgressBar postProgressBar, urlProgress;
    private ImageView imageProfilenewsView, imageViewProfilenewsPic, notiType, likeView, likedView, urlImage,saveChanges,backArrow;
    private TextView tvEdited, textViewAdded_by, textViewDate_added, textViewUser_to, textViewLikes, postUsername_top, textViewNumComments, urlTitle, urlDesc, textViewComments, textViewLikesText;
    private String userID, username;
    private EditText textViewBody;
    private Context mContext;
    private String postID;
    private Button saveButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View editProfilePostRootView = inflater.inflate(R.layout.fragment_editprofilepost, null);

        verifiedView = editProfilePostRootView.findViewById(R.id.verified);
        onlineView = editProfilePostRootView.findViewById(R.id.online);
        postProgressBar = editProfilePostRootView.findViewById(R.id.postProgressBar);
        likeView = editProfilePostRootView.findViewById(R.id.like);
        likedView = editProfilePostRootView.findViewById(R.id.liked);
        notiType = editProfilePostRootView.findViewById(R.id.platformType);
        publicsTopicList = editProfilePostRootView.findViewById(R.id.publicsTopicList);
        textViewAdded_by = editProfilePostRootView.findViewById(R.id.textViewProfileName);
        postUsername_top = editProfilePostRootView.findViewById(R.id.postUsername_top);
        textViewUser_to = editProfilePostRootView.findViewById(R.id.textViewToUserName);
        imageViewProfilenewsPic = editProfilePostRootView.findViewById(R.id.imageViewProfilenewsPic);
        imageProfilenewsView = editProfilePostRootView.findViewById(R.id.profileNewsImage);
        textViewBody = editProfilePostRootView.findViewById(R.id.textViewBody);
        textViewLikes = editProfilePostRootView.findViewById(R.id.textViewNumLikes);
        textViewDate_added = editProfilePostRootView.findViewById(R.id.profileCommentsDateTime_top);
        textViewNumComments = editProfilePostRootView.findViewById(R.id.textViewNumComments);
        urlPreview = editProfilePostRootView.findViewById(R.id.urlPreview);
        urlProgress = editProfilePostRootView.findViewById(R.id.urlProgress);
        urlImage = editProfilePostRootView.findViewById(R.id.urlImage);
        urlTitle = editProfilePostRootView.findViewById(R.id.urlTitle);
        urlDesc = editProfilePostRootView.findViewById(R.id.urlDesc);
        urlBits = editProfilePostRootView.findViewById(R.id.urlBits);
        textViewComments = editProfilePostRootView.findViewById(R.id.textViewComments);
        textViewLikesText = editProfilePostRootView.findViewById(R.id.textViewLikes);
        contentLayout = editProfilePostRootView.findViewById(R.id.contentLayout);
        tvEdited = editProfilePostRootView.findViewById(R.id.tvEdited);
        saveButton = editProfilePostRootView.findViewById(R.id.saveButton);
        saveChanges = editProfilePostRootView.findViewById(R.id.saveChanges);
        backArrow = editProfilePostRootView.findViewById(R.id.backArrow);
        mContext = getActivity();
        userID = SharedPrefManager.getInstance(mContext).getUserID();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        assert getArguments() != null;
        postID = getArguments().getString("id");

        textViewBody.requestFocus();
        if (textViewBody.hasFocus()) {
            InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
        saveChanges.setOnClickListener(v -> savePost());
        saveButton.setOnClickListener(v -> savePost());
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        loadprofilePost();
        return editProfilePostRootView;
    }

    private void savePost(){
        saveChanges.setVisibility(GONE);
        saveButton.setVisibility(GONE);
        postProgressBar.setVisibility(View.VISIBLE);
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        String body = textViewBody.getText().toString();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, POST_EDIT_SAVE, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    Toast.makeText(mContext,"Saved!", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStackImmediate();
                }else{
                    Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    saveChanges.setVisibility(View.VISIBLE);
                    saveButton.setVisibility(View.VISIBLE);
                    postProgressBar.setVisibility(GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            saveChanges.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            postProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Network error, please try again later...",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("body",body);
                parms.put("postid",postID);
                parms.put("username",username);
                parms.put("userid",userID);
                return parms;
            }
        };
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

    private void loadprofilePost() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ProfilePost_URL+"?postID="+postID+"&username="+username+"&userid="+userID, response -> {
            try {
                JSONArray profilepost = new JSONArray(response);
                JSONObject profilepostObject = profilepost.getJSONObject(0);

                //int id = profilepostObject.getInt("id");
                String type = profilepostObject.getString("type");
                final String likes = profilepostObject.getString("likes");
                String body = profilepostObject.getString("body");
                String user_to = profilepostObject.getString("user_to");
                String date_added = profilepostObject.getString("date_added");
                final String image = profilepostObject.getString("image");
                String profile_pic = profilepostObject.getString("profile_pic");
                String nickname = profilepostObject.getString("nickname");
                final String username = profilepostObject.getString("username");
                String commentcount = profilepostObject.getString("commentcount");
                String likedbyuserYes = profilepostObject.getString("likedbyuseryes");
                String online = profilepostObject.getString("online");
                String verified = profilepostObject.getString("verified");
                String form = profilepostObject.getString("form");

                if (!user_to.equals("none")){
                    textViewUser_to.setVisibility(View.VISIBLE);
                    switch (form) {
                        case "user":
                            textViewUser_to.setText(String.format("to @%s", user_to));
                            break;
                        case "clan":
                            textViewUser_to.setText(String.format("to [%s]", user_to));
                            textViewUser_to.setTextColor(ContextCompat.getColor(mContext,R.color.pin));
                            break;
                        case "event":
                            break;
                    }
                }
                switch (type) {
                    case "Xbox":
                        notiType.setImageResource(R.drawable.icons8_xbox_50);
                        notiType.setVisibility(View.VISIBLE);
                        break;
                    case "PlayStation":
                        notiType.setImageResource(R.drawable.icons8_playstation_50);
                        notiType.setVisibility(View.VISIBLE);
                        break;
                    case "Steam":
                        notiType.setImageResource(R.drawable.icons8_steam_48);
                        notiType.setVisibility(View.VISIBLE);
                        break;
                    case "PC":
                        notiType.setImageResource(R.drawable.icons8_workstation_48);
                        notiType.setVisibility(View.VISIBLE);
                        break;
                }
                String [] bodybits = body.split("\\s+");
                for( final String item : bodybits ) {
                    if(android.util.Patterns.WEB_URL.matcher(item).matches()) {
                        final String finalItem;
                        if(!item.contains("http://")&&!item.contains("https://")){
                            finalItem = "https://"+item;
                        }else {
                            finalItem = item;
                        }
                        final String[] imageUrl = {null};
                        final String[] title = new String[1];
                        final String[] desc = {null};
                        urlPreview.setVisibility(View.VISIBLE);
                        urlImage.setOnClickListener(v -> {
                            Uri uri = Uri.parse(finalItem);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            mContext.startActivity(intent);
                        });
                        new Thread(() -> {
                            try {
                                Document doc = Jsoup.connect(finalItem).get();
                                Elements ogTags = doc.select("meta[property^=og:]");
                                if (ogTags.size() <= 0) {
                                    return;
                                }
                                Elements metaOgTitle = doc.select("meta[property=og:title]");
                                if (metaOgTitle!=null) {
                                    title[0] = metaOgTitle.attr("content");
                                }
                                else {
                                    title[0] = doc.title();
                                }
                                Elements metaOgDesc = doc.select("meta[property=og:description]");
                                if (metaOgDesc!=null) {
                                    desc[0] = metaOgDesc.attr("content");
                                }
                                Elements metaOgImage = doc.select("meta[property=og:image]");
                                if (metaOgImage!=null) {
                                    imageUrl[0] = metaOgImage.attr("content");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).start();
                        //Fucking code wouldn't work any other way than I'm currently capable. Fuck it, have a delay
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            Glide.with(mContext)
                                    .load(imageUrl[0])
                                    .error(R.drawable.ic_error)
                                    .into(urlImage);
                            if (title[0] !=null) {
                                urlTitle.setText(title[0]);
                            }else{
                                urlTitle.setText(mContext.getString(R.string.no_content));
                            }
                            if (desc[0] !=null) {
                                urlDesc.setText(desc[0]);
                            }
                            urlProgress.setVisibility(View.GONE);
                            urlBits.setVisibility(View.VISIBLE);
                        }, 5000);
                        break;
                    }
                }
                textViewAdded_by.setText(nickname);
                postUsername_top.setText(String.format("@%s", username));
                textViewBody.setText(body);
                textViewDate_added.setText(date_added);
                textViewLikes.setText(likes);
                textViewNumComments.setText(commentcount);
                if (online.equals("yes")) {
                    onlineView.setVisibility(View.VISIBLE);
                }
                if (verified.equals("yes")) {
                    verifiedView.setVisibility(View.VISIBLE);
                }
                if(likedbyuserYes.equals("yes")){
                    likeView.setVisibility(View.GONE);
                    likedView.setVisibility(View.VISIBLE);
                }
                String profile_pic2 = profile_pic.substring(0, profile_pic.length() - 4)+"_r.JPG";
                Glide.with(mContext)
                        .load(Constants.BASE_URL + profile_pic2)
                        .into(imageViewProfilenewsPic);
                if (!image.isEmpty()) {
                    Glide.with(mContext)
                            .load(Constants.BASE_URL + image).override(1000)
                            .into(imageProfilenewsView);
                }
                postProgressBar.setVisibility(View.GONE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(mContext, "Error on Response: Dashboard Feed", Toast.LENGTH_SHORT).show());
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}