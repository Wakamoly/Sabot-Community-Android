package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilenewsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private static final String LIKE_URL = Constants.ROOT_URL+"post_like.php";
    private static final String POST_DELETE = Constants.ROOT_URL+"profile_post_action.php/post_delete";
    private Context mCtx;
    private List<Profilenews_Recycler> profilenewsList;

    public ProfilenewsAdapter(Context mCtx, List<Profilenews_Recycler> profilenewsList) {
        this.mCtx = mCtx;
        this.profilenewsList = profilenewsList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_profilenews, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
            default:
                return null;
        }
    }

    @Override public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {holder.onBind(position);}

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == profilenewsList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return profilenewsList == null ? 0 : profilenewsList.size();
    }

    public void addItems(List<Profilenews_Recycler> items) {
        profilenewsList.addAll(items);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        profilenewsList.add(new Profilenews_Recycler(0,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null));
        notifyItemInserted(profilenewsList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        if (profilenewsList.size()!=0){
            int position = profilenewsList.size() - 1;
            Profilenews_Recycler item = getItem(position);
            if (item != null) {
                profilenewsList.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void clear() {
        profilenewsList.clear();
        notifyDataSetChanged();
    }

    Profilenews_Recycler getItem(int position) {
        return profilenewsList.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        CircleImageView verified, online;
        LinearLayout urlBits, urlPreview;
        RelativeLayout publicsTopicList;
        MaterialRippleLayout contentLayout;
        ProgressBar likeProgress, urlProgress;
        SharedPrefManager Sharedprefmanager = SharedPrefManager.getInstance(mCtx);
        ImageView imageProfilenewsView, imageViewProfilenewsPic, notiType, likeView, likedView, urlImage;
        TextView tvEdited, textViewBody, textViewAdded_by, textViewDate_added, textViewUser_to, textViewLikes, postUsername_top, textViewNumComments, urlTitle, urlDesc, textViewComments, textViewLikesText;
        String userID, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userID = Sharedprefmanager.getUserID();
            username = Sharedprefmanager.getUsername();
            verified = itemView.findViewById(R.id.verified);
            online = itemView.findViewById(R.id.online);
            likeProgress = itemView.findViewById(R.id.likeProgress);
            likeView = itemView.findViewById(R.id.like);
            likedView = itemView.findViewById(R.id.liked);
            notiType = itemView.findViewById(R.id.platformType);
            publicsTopicList = itemView.findViewById(R.id.publicsTopicList);
            textViewAdded_by = itemView.findViewById(R.id.textViewProfileName);
            postUsername_top = itemView.findViewById(R.id.postUsername_top);
            textViewUser_to = itemView.findViewById(R.id.textViewToUserName);
            imageViewProfilenewsPic = itemView.findViewById(R.id.imageViewProfilenewsPic);
            imageProfilenewsView = itemView.findViewById(R.id.profileNewsImage);
            textViewBody = itemView.findViewById(R.id.textViewBody);
            textViewLikes = itemView.findViewById(R.id.textViewNumLikes);
            textViewDate_added = itemView.findViewById(R.id.profileCommentsDateTime_top);
            textViewNumComments = itemView.findViewById(R.id.textViewNumComments);
            urlPreview = itemView.findViewById(R.id.urlPreview);
            urlProgress = itemView.findViewById(R.id.urlProgress);
            urlImage = itemView.findViewById(R.id.urlImage);
            urlTitle = itemView.findViewById(R.id.urlTitle);
            urlDesc = itemView.findViewById(R.id.urlDesc);
            urlBits = itemView.findViewById(R.id.urlBits);
            textViewComments = itemView.findViewById(R.id.textViewComments);
            textViewLikesText = itemView.findViewById(R.id.textViewLikes);
            contentLayout = itemView.findViewById(R.id.contentLayout);
            tvEdited = itemView.findViewById(R.id.tvEdited);
        }

        @Override protected void clear() {}

        public void onBind(int position) {
            super.onBind(position);
            final Profilenews_Recycler profilenews = profilenewsList.get(position);
            textViewBody.setText(profilenews.getBody());
            textViewNumComments.setText(profilenews.getCommentcount());
            textViewAdded_by.setText(profilenews.getNickname());
            textViewDate_added.setText(profilenews.getDate_added());
            textViewLikes.setText(profilenews.getLikes());
            postUsername_top.setText(String.format("@%s", profilenews.getAdded_by()));
            String User_to = profilenews.getUser_to();
            if (!User_to.equals("none")) {
                switch (profilenews.getForm()) {
                    case "user":
                        textViewUser_to.setText(String.format("to @%s", User_to));
                        break;
                    case "clan":
                        textViewUser_to.setText(String.format("to [%s]", User_to));
                        textViewUser_to.setTextColor(ContextCompat.getColor(mCtx, R.color.pin));
                        break;
                    case "event":
                        break;
                }
            } else {
                textViewUser_to.setVisibility(View.GONE);
            }
            switch (profilenews.getType()) {
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
                case "Mobile":
                    notiType.setImageResource(R.drawable.icons8_mobile_48);
                    notiType.setVisibility(View.VISIBLE);
                    break;
                case "Switch":
                    notiType.setImageResource(R.drawable.icons8_nintendo_switch_48);
                    notiType.setVisibility(View.VISIBLE);
                    break;
                case "General":
                    break;
                default:
                    notiType.setImageResource(R.drawable.icons8_question_mark_64);
                    notiType.setVisibility(View.VISIBLE);
                    break;
            }

            contentLayout.setOnLongClickListener(v -> {
                PopupMenu popup = new PopupMenu(mCtx, v);
                MenuInflater inflater = popup.getMenuInflater();
                if (profilenews.getAdded_by().equals(username)) {
                    inflater.inflate(R.menu.profile_post_owner, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.menuDelete) {
                            new LovelyStandardDialog(mCtx, LovelyStandardDialog.ButtonLayout.VERTICAL)
                                    .setTopColorRes(R.color.green)
                                    .setButtonsColorRes(R.color.green)
                                    .setIcon(R.drawable.ic_error)
                                    .setTitle(R.string.delete_post_string)
                                    .setMessage(R.string.confirm)
                                    .setPositiveButton(R.string.yes, v1 -> {
                                        StringRequest stringRequest = new StringRequest(Request.Method.POST, POST_DELETE, response -> {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if (jsonObject.getString("error").equals("false")) {
                                                    Toast.makeText(mCtx, "Post deleted!", Toast.LENGTH_LONG).show();
                                                    contentLayout.setVisibility(View.GONE);
                                                } else {
                                                    Toast.makeText(mCtx, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }, error -> Toast.makeText(mCtx, "Network error, please try again later...", Toast.LENGTH_LONG).show()) {
                                            @Override
                                            protected Map<String, String> getParams() {
                                                Map<String, String> parms = new HashMap<>();
                                                parms.put("postid", Integer.toString(profilenews.getId()));
                                                parms.put("username", username);
                                                parms.put("userid", userID);
                                                return parms;
                                            }
                                        };
                                        ((FragmentContainer) mCtx).addToRequestQueue(stringRequest);
                                    })
                                    .setNegativeButton(R.string.no, null)
                                    .show();
                        }
                        if (item.getItemId() == R.id.menuEdit) {
                            ProfilePostEditFragment ldf = new ProfilePostEditFragment();
                            Bundle args = new Bundle();
                            args.putString("id", Integer.toString(profilenews.getId()));
                            ldf.setArguments(args);
                            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }
                        if (item.getItemId() == R.id.menuReport) {
                            ReportFragment ldf = new ReportFragment();
                            Bundle args = new Bundle();
                            args.putString("context", profilenews.getBody());
                            args.putString("type", "post");
                            args.putString("id", Integer.toString(profilenews.getId()));
                            ldf.setArguments(args);
                            //Inflate the fragment
                            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }
                        return true;
                    });
                } else {
                    inflater.inflate(R.menu.profile_post_nonowner, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.menuReport) {
                            ReportFragment ldf = new ReportFragment();
                            Bundle args = new Bundle();
                            args.putString("context", profilenews.getBody());
                            args.putString("type", "post");
                            args.putString("id", Integer.toString(profilenews.getId()));
                            ldf.setArguments(args);
                            ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                        }
                        return true;
                    });
                }
                popup.show();
                return false;
            });
            contentLayout.setOnClickListener(v -> {
                ProfilePostFragment ldf = new ProfilePostFragment();
                Bundle args = new Bundle();
                args.putString("id", String.valueOf(profilenews.getId()));
                args.putString("UserId", profilenews.getUser_id());
                args.putString("Username", profilenews.getAdded_by());
                ldf.setArguments(args);
                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            });
            textViewAdded_by.setOnClickListener(v -> {
                FragmentProfile ldf = new FragmentProfile();
                Bundle args = new Bundle();
                args.putString("UserId", profilenews.getUser_id());
                args.putString("Username", profilenews.getAdded_by());
                ldf.setArguments(args);
                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            });

            String profile_pic = profilenews.getProfile_pic().substring(0, profilenews.getProfile_pic().length() - 4) + "_r.JPG";
            Glide.with(mCtx)
                    .load(Constants.BASE_URL + profile_pic)
                    .into(imageViewProfilenewsPic);
            if (!profilenews.getImage().isEmpty()) {
                imageProfilenewsView.setVisibility(View.VISIBLE);
                Glide.with(mCtx)
                        .load(Constants.BASE_URL + profilenews.getImage()).override(1000)
                        .into(imageProfilenewsView);
            } else {
                imageProfilenewsView.setVisibility(View.GONE);
            }

            if (profilenews.getLikedbyuseryes().equals("yes")) {
                likeView.setVisibility(View.GONE);
                likedView.setVisibility(View.VISIBLE);
            } else {
                likeView.setVisibility(View.VISIBLE);
                likedView.setVisibility(View.GONE);
            }

            likeView.setOnClickListener(v -> {
                likeView.setVisibility(View.GONE);
                likeProgress.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKE_URL, response -> {
                    JSONObject obj;
                    try {
                        obj = new JSONObject(response);
                        if (!obj.getBoolean("error")) {
                            String newValue = Integer.toString(Integer.parseInt(textViewLikes.getText().toString()) + 1);
                            textViewLikes.setText(newValue);
                            likeProgress.setVisibility(View.GONE);
                            likedView.setVisibility(View.VISIBLE);
                            likedView.setEnabled(false);
                            new Handler().postDelayed(() -> likedView.setEnabled(true), 3500);
                        } else {
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                            likeProgress.setVisibility(View.GONE);
                            likeView.setVisibility(View.VISIBLE);
                            likeView.setEnabled(false);
                            new Handler().postDelayed(() -> likeView.setEnabled(true), 3000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(mCtx, "Could not like, please try again later...", Toast.LENGTH_LONG).show();
                    likeProgress.setVisibility(View.GONE);
                    likeView.setVisibility(View.VISIBLE);
                    likeView.setEnabled(false);
                    new Handler().postDelayed(() -> likeView.setEnabled(true), 3000);
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parms = new HashMap<>();
                        parms.put("post_id", String.valueOf(profilenews.getId()));
                        parms.put("method", "like");
                        parms.put("user_to", profilenews.getUsername());
                        parms.put("user_id", userID);
                        parms.put("username", username);
                        return parms;
                    }
                };
                ((FragmentContainer) mCtx).addToRequestQueue(stringRequest);
            });

            likedView.setOnClickListener(v -> {
                likedView.setVisibility(View.GONE);
                likeProgress.setVisibility(View.VISIBLE);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, LIKE_URL, response -> {
                    JSONObject obj;
                    try {
                        obj = new JSONObject(response);
                        if (!obj.getBoolean("error")) {
                            String newValue = Integer.toString(Integer.parseInt(textViewLikes.getText().toString()) - 1);
                            textViewLikes.setText(newValue);
                            likeProgress.setVisibility(View.GONE);
                            likeView.setVisibility(View.VISIBLE);
                            likeView.setEnabled(false);
                            new Handler().postDelayed(() -> likeView.setEnabled(true), 3500);
                        } else {
                            Toast.makeText(mCtx, obj.getString("message"), Toast.LENGTH_LONG).show();
                            likeProgress.setVisibility(View.GONE);
                            likedView.setVisibility(View.VISIBLE);
                            likedView.setEnabled(false);
                            new Handler().postDelayed(() -> likedView.setEnabled(true), 3000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
                    Toast.makeText(mCtx, "Could not remove like, please try again later...", Toast.LENGTH_LONG).show();
                    likeProgress.setVisibility(View.GONE);
                    likedView.setVisibility(View.VISIBLE);
                    likedView.setEnabled(false);
                    new Handler().postDelayed(() -> likeView.setEnabled(true), 3000);
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> parms = new HashMap<>();
                        parms.put("post_id", String.valueOf(profilenews.getId()));
                        parms.put("method", "unlike");
                        parms.put("user_to", profilenews.getUsername());
                        parms.put("user_id", userID);
                        parms.put("username", username);
                        return parms;
                    }
                };
                ((FragmentContainer) mCtx).addToRequestQueue(stringRequest);
            });

            if (profilenews.getOnline().equals("yes")) {
                online.setVisibility(View.VISIBLE);
            } else {
                online.setVisibility(View.GONE);
            }
            if (profilenews.getVerified().equals("yes")) {
                verified.setVisibility(View.VISIBLE);
            } else {
                verified.setVisibility(View.GONE);
            }

            String[] bodybits = profilenews.getBody().split("\\s+");
            for (final String item : bodybits) {
                if (android.util.Patterns.WEB_URL.matcher(item).matches()) {
                    final String finalItem;
                    if (!item.contains("http://") && !item.contains("https://")) {
                        finalItem = "https://" + item;
                    } else {
                        finalItem = item;
                    }
                    final String[] imageUrl = new String[1];
                    final String[] title = new String[1];
                    final String[] desc = new String[1];
                    urlPreview.setVisibility(View.VISIBLE);
                    urlImage.setOnClickListener(v -> {
                        Uri uri = Uri.parse(finalItem);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        mCtx.startActivity(intent);
                    });
                    new Thread(() -> {
                        try {
                            Document doc = Jsoup.connect(finalItem).get();
                            Elements ogTags = doc.select("meta[property^=og:]");
                            if (ogTags.size() <= 0) {
                                return;
                            }
                            Elements metaOgTitle = doc.select("meta[property=og:title]");
                            if (metaOgTitle != null) {
                                title[0] = metaOgTitle.attr("content");
                            } else {
                                title[0] = doc.title();
                            }
                            Elements metaOgDesc = doc.select("meta[property=og:description]");
                            if (metaOgDesc != null) {
                                desc[0] = metaOgDesc.attr("content");
                            }
                            Elements metaOgImage = doc.select("meta[property=og:image]");
                            if (metaOgImage != null) {
                                imageUrl[0] = metaOgImage.attr("content");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    //Fucking code wouldn't work any other way than I'm currently capable. Fuck it, have a delay
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if (imageUrl[0] != null && imageUrl[0].isEmpty()) {
                            urlImage.setImageResource(R.drawable.ic_error);
                        } else {
                            Glide.with(mCtx)
                                    .load(imageUrl[0])
                                    .error(R.drawable.ic_error)
                                    .into(urlImage);
                        }
                        if (title[0] != null) {
                            urlTitle.setText(title[0]);
                        } else {
                            urlTitle.setText(mCtx.getString(R.string.no_content));
                        }
                        if (desc[0] != null) {
                            urlDesc.setText(desc[0]);
                        }
                        urlProgress.setVisibility(View.GONE);
                        urlBits.setVisibility(View.VISIBLE);
                    }, 5000);
                    break;
                } else {
                    urlPreview.setVisibility(View.GONE);
                }
            }

            textViewLikesText.setOnClickListener(v -> {
                Fragment asf = new UserListFragment();
                Bundle args = new Bundle();
                args.putString("query", "post");
                args.putString("queryID", String.valueOf(profilenews.getId()));
                asf.setArguments(args);
                FragmentTransaction fragmentTransaction = ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.fragment_container, asf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            });
            textViewComments.setOnClickListener(v -> {
                ProfilePostFragment ldf = new ProfilePostFragment();
                Bundle args = new Bundle();
                args.putString("id", String.valueOf(profilenews.getId()));
                args.putString("UserId", profilenews.getUser_id());
                args.putString("Username", profilenews.getAdded_by());
                ldf.setArguments(args);
                ((FragmentActivity) mCtx).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            });
            if (profilenews.getIsEdited().equals("yes")) {
                tvEdited.setVisibility(View.VISIBLE);
            } else {
                tvEdited.setVisibility(View.GONE);
            }
        }
    }

        public static class ProgressHolder extends BaseViewHolder {
            ProgressHolder(View itemView) {
                super(itemView);
            }

            @Override
            protected void clear() {
            }
        }
}
