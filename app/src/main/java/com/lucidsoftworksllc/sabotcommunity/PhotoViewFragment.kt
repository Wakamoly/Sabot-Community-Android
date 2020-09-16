package com.lucidsoftworksllc.sabotcommunity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

public class PhotoViewFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View imageRootView = inflater.inflate(R.layout.fragment_photo, null);

        ZoomableImageView imageFromProfile = imageRootView.findViewById(R.id.imageFromProfile);
        assert getArguments() != null;
        String imageToView = getArguments().getString("image");

        Glide.with(requireActivity())
                .load(Constants.BASE_URL + imageToView)
                .error(R.mipmap.ic_launcher)
                .into(imageFromProfile);

        return imageRootView;
    }

}
