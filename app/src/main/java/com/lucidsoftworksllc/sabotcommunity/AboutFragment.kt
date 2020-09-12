package com.lucidsoftworksllc.sabotcommunity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {
    var icons8: TextView? = null
    var dexter: TextView? = null
    var circleImageView: TextView? = null
    var glide: TextView? = null
    var materialRipple: TextView? = null
    var simpleratingbar: TextView? = null
    var slidingdotsplash: TextView? = null
    var onesignal: TextView? = null
    var retrofit: TextView? = null
    var acra: TextView? = null
    var jsoup: TextView? = null
    var lovelydialog: TextView? = null
    var imageCropper: TextView? = null
    var privacy: TextView? = null
    var terms: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val aboutRootView = inflater.inflate(R.layout.fragment_about, null)
        retrofit = aboutRootView.findViewById(R.id.retrofit)
        retrofit?.setOnClickListener { loadUrl("https://github.com/square/retrofit") }
        onesignal = aboutRootView.findViewById(R.id.onesignal)
        onesignal?.setOnClickListener { loadUrl("https://onesignal.com") }
        slidingdotsplash = aboutRootView.findViewById(R.id.slidingdotsplash)
        slidingdotsplash?.setOnClickListener { loadUrl("https://github.com/Chabbal/slidingdotsplash") }
        icons8 = aboutRootView.findViewById(R.id.icons8)
        icons8?.setOnClickListener { loadUrl("https://icons8.com") }
        dexter = aboutRootView.findViewById(R.id.dexter)
        dexter?.setOnClickListener { loadUrl("https://github.com/Karumi/Dexter") }
        circleImageView = aboutRootView.findViewById(R.id.CircleImageView)
        circleImageView?.setOnClickListener { loadUrl("https://github.com/hdodenhof/CircleImageView") }
        glide = aboutRootView.findViewById(R.id.glide)
        glide?.setOnClickListener { loadUrl("https://github.com/bumptech/glide") }
        materialRipple = aboutRootView.findViewById(R.id.material_ripple)
        materialRipple?.setOnClickListener { loadUrl("https://github.com/balysv/material-ripple") }
        simpleratingbar = aboutRootView.findViewById(R.id.simpleratingbar)
        simpleratingbar?.setOnClickListener { loadUrl("https://github.com/FlyingPumba/SimpleRatingBar") }
        acra = aboutRootView.findViewById(R.id.acra)
        acra?.setOnClickListener { loadUrl("https://github.com/ACRA/acra") }
        jsoup = aboutRootView.findViewById(R.id.jsoup)
        jsoup?.setOnClickListener { loadUrl("https://github.com/jhy/jsoup/") }
        lovelydialog = aboutRootView.findViewById(R.id.lovelydialog)
        lovelydialog?.setOnClickListener { loadUrl("https://github.com/yarolegovich/LovelyDialog#lovelycustomdialog") }
        imageCropper = aboutRootView.findViewById(R.id.imageCropper)
        imageCropper?.setOnClickListener { loadUrl("https://github.com/ArthurHub/Android-Image-Cropper") }
        privacy = aboutRootView.findViewById(R.id.privacy)
        privacy?.setOnClickListener { loadUrl("https://app.termly.io/document/privacy-policy/96bf5c01-d39b-496c-a30e-57353b49877c") }
        terms = aboutRootView.findViewById(R.id.terms)
        terms?.setOnClickListener { loadUrl("https://sabotcommunity.com/termsandconditions.php") }
        return aboutRootView
    }

    private fun loadUrl(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}