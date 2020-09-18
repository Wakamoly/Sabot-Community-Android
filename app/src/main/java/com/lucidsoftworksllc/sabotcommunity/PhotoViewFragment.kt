package com.lucidsoftworksllc.sabotcommunity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class PhotoViewFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val imageRootView = inflater.inflate(R.layout.fragment_photo, null)
        val imageFromProfile: ZoomableImageView = imageRootView.findViewById(R.id.imageFromProfile)
        val imageToView = requireArguments().getString("image")
        Glide.with(requireActivity())
                .load(Constants.BASE_URL + imageToView)
                .error(R.mipmap.ic_launcher)
                .into(imageFromProfile)
        return imageRootView
    }
}