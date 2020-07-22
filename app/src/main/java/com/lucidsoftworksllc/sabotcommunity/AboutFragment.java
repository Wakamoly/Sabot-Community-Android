package com.lucidsoftworksllc.sabotcommunity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment {

    TextView icons8,dexter,CircleImageView,glide,material_ripple,simpleratingbar,slidingdotsplash,onesignal,retrofit,acra,jsoup,lovelydialog,imageCropper,privacy,terms;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View aboutRootView = inflater.inflate(R.layout.fragment_about, null);

        retrofit = aboutRootView.findViewById(R.id.retrofit);
        retrofit.setOnClickListener(v -> loadUrl("https://github.com/square/retrofit"));
        onesignal = aboutRootView.findViewById(R.id.onesignal);
        onesignal.setOnClickListener(v -> loadUrl("https://onesignal.com"));
        slidingdotsplash = aboutRootView.findViewById(R.id.slidingdotsplash);
        slidingdotsplash.setOnClickListener(v -> loadUrl("https://github.com/Chabbal/slidingdotsplash"));
        icons8 = aboutRootView.findViewById(R.id.icons8);
        icons8.setOnClickListener(v -> loadUrl("https://icons8.com"));
        dexter = aboutRootView.findViewById(R.id.dexter);
        dexter.setOnClickListener(v -> loadUrl("https://github.com/Karumi/Dexter"));
        CircleImageView = aboutRootView.findViewById(R.id.CircleImageView);
        CircleImageView.setOnClickListener(v -> loadUrl("https://github.com/hdodenhof/CircleImageView"));
        glide = aboutRootView.findViewById(R.id.glide);
        glide.setOnClickListener(v -> loadUrl("https://github.com/bumptech/glide"));
        material_ripple = aboutRootView.findViewById(R.id.material_ripple);
        material_ripple.setOnClickListener(v -> loadUrl("https://github.com/balysv/material-ripple"));
        simpleratingbar = aboutRootView.findViewById(R.id.simpleratingbar);
        simpleratingbar.setOnClickListener(v -> loadUrl("https://github.com/FlyingPumba/SimpleRatingBar"));
        acra = aboutRootView.findViewById(R.id.acra);
        acra.setOnClickListener(v -> loadUrl("https://github.com/ACRA/acra"));
        jsoup = aboutRootView.findViewById(R.id.jsoup);
        jsoup.setOnClickListener(v -> loadUrl("https://github.com/jhy/jsoup/"));
        lovelydialog = aboutRootView.findViewById(R.id.lovelydialog);
        lovelydialog.setOnClickListener(v -> loadUrl("https://github.com/yarolegovich/LovelyDialog#lovelycustomdialog"));
        imageCropper = aboutRootView.findViewById(R.id.imageCropper);
        imageCropper.setOnClickListener(v -> loadUrl("https://github.com/ArthurHub/Android-Image-Cropper"));
        privacy = aboutRootView.findViewById(R.id.privacy);
        privacy.setOnClickListener(v -> loadUrl("https://app.termly.io/document/privacy-policy/96bf5c01-d39b-496c-a30e-57353b49877c"));
        terms = aboutRootView.findViewById(R.id.terms);
        terms.setOnClickListener(v -> loadUrl("https://sabotcommunity.com/termsandconditions.php"));

        return aboutRootView;
    }

    private void loadUrl(String url){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}