package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.lucidsoftworksllc.sabotcommunity.R.drawable.details_button;
import static com.lucidsoftworksllc.sabotcommunity.R.drawable.red_button;

public class DashCurrentPublicsAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private List<CurrentPublicsPOJO> currentPublicsSlider;

    public DashCurrentPublicsAdapter(List currentPublicsSlider,Context context) {
        this.currentPublicsSlider = currentPublicsSlider;
        this.context = context;
    }

    @Override public int getCount() {
        return currentPublicsSlider.size();
    }
    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_viewpager_current_publics, null);
        final CurrentPublicsPOJO utils = currentPublicsSlider.get(position);
        ImageView imageView = view.findViewById(R.id.sliderImageView);
        TextView textViewTitle = view.findViewById(R.id.publicsPostTitle);
        TextView textViewDescription = view.findViewById(R.id.publicsPostDesc);
        TextView tvWhen = view.findViewById(R.id.tvWhen);
        TextView catName = view.findViewById(R.id.catName);
        TextView nickname = view.findViewById(R.id.nickname);
        CircleImageView profileImage = view.findViewById(R.id.profileImage);
        ImageView platformImage = view.findViewById(R.id.platformImage);
        TextView numPlayersAdded = view.findViewById(R.id.numPlayersAdded);
        TextView numPlayersNeeded = view.findViewById(R.id.numPlayersNeeded);
        LinearLayout whenLayout = view.findViewById(R.id.whenLayout);
        LinearLayout playingNowLayout = view.findViewById(R.id.playingNowLayout);
        textViewDescription.setText(utils.getContext());
        textViewTitle.setText(utils.getSubject());
        catName.setText(utils.getCatname());
        nickname.setText(utils.getNickname());
        numPlayersAdded.setText(utils.getNum_added());
        numPlayersNeeded.setText(utils.getNum_players());
        tvWhen.setText(utils.getEvent_date());
        if (utils.getPlaying_now().equals("yes")){
            playingNowLayout.setVisibility(View.VISIBLE);
        }else{
            playingNowLayout.setVisibility(View.GONE);
        }
        String finalBackImage = utils.getImage();
        if (utils.getImage().contains(".jpg")){
            finalBackImage = finalBackImage.substring(0, utils.getImage().length() - 4)+"_r.jpg";
        }else if (utils.getImage().contains(".png")){
            finalBackImage = finalBackImage.substring(0, utils.getImage().length() - 4)+"_r.png";
        }else if (utils.getImage().contains(".gif")){
            finalBackImage = finalBackImage.substring(0, utils.getImage().length() - 4)+"_r.gif";
        }
        Glide.with(context)
                .load(Constants.BASE_URL +finalBackImage)
                .override(Target.SIZE_ORIGINAL)
                .into(imageView);
        String profile_pic = utils.getProfile_pic().substring(0, utils.getProfile_pic().length() - 4)+"_r.JPG";
        Glide.with(context)
                .load(Constants.BASE_URL +profile_pic)
                .override(Target.SIZE_ORIGINAL)
                .into(profileImage);
        switch (utils.getType()) {
            case "Xbox":
                platformImage.setImageResource(R.drawable.icons8_xbox_50);
                platformImage.setVisibility(View.VISIBLE);
                break;
            case "PlayStation":
                platformImage.setImageResource(R.drawable.icons8_playstation_50);
                platformImage.setVisibility(View.VISIBLE);
                break;
            case "Steam":
                platformImage.setImageResource(R.drawable.icons8_steam_48);
                platformImage.setVisibility(View.VISIBLE);
                break;
            case "PC":
                platformImage.setImageResource(R.drawable.icons8_workstation_48);
                platformImage.setVisibility(View.VISIBLE);
                break;
            case "Mobile":
                platformImage.setImageResource(R.drawable.icons8_mobile_48);
                platformImage.setVisibility(View.VISIBLE);
                break;
            case "Switch":
                platformImage.setImageResource(R.drawable.icons8_nintendo_switch_48);
                platformImage.setVisibility(View.VISIBLE);
                break;
            case "Cross-Platform":
                platformImage.setImageResource(R.drawable.icons8_collect_40);
                platformImage.setVisibility(View.VISIBLE);
                break;
            default:
                platformImage.setImageResource(R.drawable.icons8_question_mark_64);
                platformImage.setVisibility(View.VISIBLE);
                break;
        }
        if (utils.getEvent_date().equals("now")) {
            whenLayout.setBackgroundResource(details_button);
        } else if (utils.getEvent_date().equals("ended")) {
            whenLayout.setBackgroundResource(red_button);
        }
        view.setOnClickListener(v -> {
            if (context instanceof FragmentContainer) {
                PublicsTopicFragment ldf = new PublicsTopicFragment ();
                Bundle args = new Bundle();
                args.putString("PublicsId", utils.getId());
                ldf.setArguments(args);
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }});
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

}