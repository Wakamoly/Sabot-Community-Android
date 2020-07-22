package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DashViewPagerAdapter extends PagerAdapter {

    private static final String URL_CLICKED = Constants.ROOT_URL+"dashboard_ad_interaction.php";

    private Context context;
    private LayoutInflater layoutInflater;
    private List<SliderUtilsDash> sliderImg;

    public DashViewPagerAdapter(List sliderImg, Context context) {
        this.sliderImg = sliderImg;
        this.context = context;
    }

    @Override public int getCount() {
        return sliderImg.size();
    }
    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.layout_viewpager_slider_dash, null);
        final SliderUtilsDash utils = sliderImg.get(position);
        ImageView imageView = view.findViewById(R.id.sliderImageView);
        TextView textViewTitle = view.findViewById(R.id.textViewSliderTitle);
        TextView textViewDescription = view.findViewById(R.id.textViewSliderDesc);
        String finalBackImage = utils.getSliderImageUrl();
        /*if (utils.getSliderImageUrl().contains(".jpg")){
            finalBackImage = finalBackImage.substring(0, utils.getSliderImageUrl().length() - 4)+"_r.jpg";
        }else if (utils.getSliderImageUrl().contains(".png")){
            finalBackImage = finalBackImage.substring(0, utils.getSliderImageUrl().length() - 4)+"_r.png";
        }else if (utils.getSliderImageUrl().contains(".gif")){
            finalBackImage = finalBackImage.substring(0, utils.getSliderImageUrl().length() - 4)+"_r.gif";
        }*/
        Glide.with(context)
                .load(finalBackImage)
                .override(Target.SIZE_ORIGINAL)
                .into(imageView);
        view.setOnClickListener(v -> {
            if (utils.getSliderType().equals("public")) {
                FragmentPublicsCat ldf = new FragmentPublicsCat();
                Bundle args = new Bundle();
                args.putString("PublicsId", utils.getSliderID());
                ldf.setArguments(args);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
            }
            if (utils.getSliderType().equals("fragment")) {
                if (utils.getSliderTag().equals("merch")){
                    MerchFragment ldf = new MerchFragment();
                    Bundle args = new Bundle();
                    ldf.setArguments(args);
                    ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, ldf).addToBackStack(null).commit();
                }
            }
            if (utils.getSliderType().equals("url")){
                if (context instanceof FragmentContainer) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(utils.getSliderTag())));
                }
            }
            newAdClick(utils.getSliderAdID());
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);

       /* String genreString = utils.getSliderGenre();
        int len = genreString.length();
        String[] genreString2 = genreString.substring(1,len-1).split(",");

        StringBuilder sb = new StringBuilder();
        for(int i=0; i < genreString2.length; i++){
            sb.append(genreString2[i]);
            sb.append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);

        textViewGenre.setText(sb);*/
        textViewDescription.setText(utils.getSliderDescription());
        textViewTitle.setText(utils.getSliderTitle());
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }

    public void newAdClick(final String adID){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_CLICKED, response -> {}, error -> {}){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("id", adID);
                parms.put("method", "click");
                parms.put("user_id",SharedPrefManager.getInstance(context).getUserID());
                parms.put("username",SharedPrefManager.getInstance(context).getUsername());
                return parms;
            }
        };
        ((FragmentContainer)context).addToRequestQueue(stringRequest);
    }

}