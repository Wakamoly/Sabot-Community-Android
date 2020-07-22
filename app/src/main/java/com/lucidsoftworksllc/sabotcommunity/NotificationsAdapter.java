package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    private List<Notifications_Recycler> notifications;
    private Context context;
    private static final String SET_READ = Constants.ROOT_URL+"set_noti_read.php";

    public NotificationsAdapter(List<Notifications_Recycler> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new NotificationsAdapter.ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_notifications, parent, false));
            case VIEW_TYPE_LOADING:
                return new NotificationsAdapter.ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
        //TODO Temporary v
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == notifications.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        return notifications == null ? 0 : notifications.size();
    }

    public void addItems(List<Notifications_Recycler> items) {
        notifications.addAll(items);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        notifications.add(new Notifications_Recycler(null,null,null,null,null,null,null,null,null,null,null,null,null,null));
        notifyItemInserted(notifications.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        if (notifications.size()!=0){
            int position = notifications.size() - 1;
            Notifications_Recycler item = getItem(position);
            if (item != null) {
                notifications.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void clear() {
        notifications.clear();
        notifyDataSetChanged();
    }

    Notifications_Recycler getItem(int position) {
        return notifications.get(position);
    }

    public class ViewHolder extends BaseViewHolder {
        TextView nickname, body, datetime;
        ImageView profile_pic_view;
        CircleImageView online, notiType, verified;
        MaterialRippleLayout notiLayout;
        SharedPrefManager Sharedprefmanager = SharedPrefManager.getInstance(context);
        String username,userid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notiLayout = itemView.findViewById(R.id.notiLayout);
            nickname = itemView.findViewById(R.id.notificationsNickname);
            body = itemView.findViewById(R.id.notificationsBody);
            datetime = itemView.findViewById(R.id.notificationsDateTime);
            profile_pic_view = itemView.findViewById(R.id.notificationsImageView);
            online = itemView.findViewById(R.id.online);
            notiType = itemView.findViewById(R.id.notiType);
            verified = itemView.findViewById(R.id.verified);
            userid = Sharedprefmanager.getUserID();
            username = Sharedprefmanager.getUsername();
        }

        @Override
        protected void clear(){}

        public void onBind(int position) {
            super.onBind(position);
            final Notifications_Recycler notification = notifications.get(position);

        /*String wordToFind = "@"+notification.getMessage().lastIndexOf("@")+1;
        Pattern word = Pattern.compile(wordToFind);
        Matcher match = word.matcher(notification.getMessage());
        Spannable wordToSpan = new SpannableString(notification.getMessage());
        while (match.find()) {
            wordToSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.light_blue)), match.start(), match.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        holder.body.setText(wordToSpan);

        String [] bodybits = notification.getMessage().split("\\s+");
        // get every part
        for( String item : bodybits ) {
            if(item.contains("@")) {
                String itemRevised = " <font color='"
                        + ContextCompat.getColor(context,R.color.light_blue) + "'>" + item
                        + "</font>";
                item.replaceAll(item,itemRevised);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.body.setText(Html.fromHtml(bodybits, Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.body.setText(Html.fromHtml(bodybits));
        }*/

            nickname.setText(notification.getNickname());
            body.setText(notification.getMessage());
            datetime.setText(notification.getDatetime());
            if(notification.getVerified().equals("yes")){
                verified.setVisibility(View.VISIBLE);
            }else{
                verified.setVisibility(View.GONE);
            }
            if(notification.getLast_online().equals("yes")){
                online.setVisibility(View.VISIBLE);
            }else{
                online.setVisibility(View.GONE);
            }
            if(notification.getOpened().equals("yes")){
                notiLayout.setBackgroundColor(Color.parseColor("#111111"));
            } else{
                notiLayout.setBackgroundColor(Color.parseColor("#222222"));
            }

            switch (notification.getType()) {
                case "post_comment":
                case "profile_post":
                    notiType.setImageResource(R.drawable.notify_comment);
                    break;
                case "comment_like":
                case "like":
                    notiType.setImageResource(R.drawable.notify_like);
                    break;
                case "new_follower":
                case "new_connection_request":
                    notiType.setImageResource(R.drawable.notify_follower);
                    break;
                case "publics_comment":
                case "comment":
                    notiType.setImageResource(R.drawable.notify_reply);
                    break;
            }
            String profile_pic = notification.getProfile_pic().substring(0, notification.getProfile_pic().length() - 4)+"_r.JPG";
            Glide.with(context)
                    .load(Constants.BASE_URL+ profile_pic)
                    .into(profile_pic_view);
            notiLayout.setOnClickListener(v -> {
                if (!notification.getOpened().equals("yes")){
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, SET_READ, response -> {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.getBoolean("error")) {
                                Toast.makeText(context, obj.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> Toast.makeText(context, "Could not set notification as read, please try again later...", Toast.LENGTH_LONG).show()) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> parms = new HashMap<>();
                            parms.put("username", notification.getUser_to());
                            parms.put("id", notification.getId());
                            return parms;
                        }
                    };
                    ((FragmentContainer)context).addToRequestQueue(stringRequest);
                }
                if(notification.getLink().contains("post.php?id=")) {
                    String linkID = notification.getLink().replace("post.php?id=", "");
                    if (context instanceof FragmentContainer) {
                        ProfilePostFragment ldf = new ProfilePostFragment ();
                        Bundle args = new Bundle();
                        args.putString("id", linkID);
                        ldf.setArguments(args);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    }
                }else if(notification.getLink().contains("publics_topic.php?id=")) {
                    String linkID = notification.getLink().replace("publics_topic.php?id=", "");
                    if (context instanceof FragmentContainer) {
                        PublicsTopicFragment ldf = new PublicsTopicFragment();
                        Bundle args = new Bundle();
                        args.putString("PublicsId", linkID);
                        ldf.setArguments(args);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    }
                }else if(notification.getLink().contains("clan=")) {
                    String linkID = notification.getLink().replace("clan=", "");
                    if (context instanceof FragmentContainer) {
                        ClanFragment ldf = new ClanFragment();
                        Bundle args = new Bundle();
                        args.putString("ClanId", linkID);
                        ldf.setArguments(args);
                        ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    }
                }else if(notification.getLink().contains("user=")) {
                    String linkID = notification.getUser_id();
                    if (context instanceof FragmentContainer) {
                        FragmentProfile ldf = new FragmentProfile ();
                        Bundle args = new Bundle();
                        args.putString("UserId", linkID);
                        ldf.setArguments(args);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    }
                }else if(notification.getLink().contains("ptopic=")) {
                    String linkID = notification.getLink().replace("ptopic=", "");
                    if (context instanceof FragmentContainer) {
                        PublicsTopicFragment ldf = new PublicsTopicFragment ();
                        Bundle args = new Bundle();
                        args.putString("PublicsId", linkID);
                        ldf.setArguments(args);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
                    }
                }else if(notification.getLink().contains("review")) {
                    if (context instanceof FragmentContainer) {
                        FragmentProfile ldf = new FragmentProfile();
                        Bundle args = new Bundle();
                        args.putString("UserId", userid);
                        ldf.setArguments(args);
                        ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).commit();
                    }
                }
            });
        }
    }

    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
        }
        @Override
        protected void clear() {}
    }

}
