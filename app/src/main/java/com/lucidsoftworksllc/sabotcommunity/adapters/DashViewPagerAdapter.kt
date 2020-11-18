package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.fragments.FragmentPublicsCat
import com.lucidsoftworksllc.sabotcommunity.fragments.MerchFragment
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.DashboardAdModelItem
import com.lucidsoftworksllc.sabotcommunity.others.Constants.BASE_URL
import java.util.*
import kotlin.collections.ArrayList

class DashViewPagerAdapter(private val sliderImgAdsModel: ArrayList<DashboardAdModelItem?>, private val context: Context) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    override fun getCount(): Int {
        return sliderImgAdsModel.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    fun addItems(items: List<DashboardAdModelItem>){
        sliderImgAdsModel.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        sliderImgAdsModel.clear()
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.layout_viewpager_slider_dash, null)
        val utils = sliderImgAdsModel[position]
        val imageView = view.findViewById<ImageView>(R.id.sliderImageView)
        val textViewTitle = view.findViewById<TextView>(R.id.textViewSliderTitle)
        val textViewDescription = view.findViewById<TextView>(R.id.textViewSliderDesc)
        val finalBackImage = BASE_URL + utils?.cat_image
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
                .into(imageView)
        view.setOnClickListener {
            if (utils?.type == "public") {
                val ldf = FragmentPublicsCat()
                val args = Bundle()
                args.putString("PublicsId", utils.cat_id.toString())
                ldf.arguments = args
                (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
            if (utils?.type == "fragment") {
                if (utils.tag == "merch") {
                    val ldf = MerchFragment()
                    val args = Bundle()
                    ldf.arguments = args
                    (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).replace(R.id.fragment_container, ldf).addToBackStack(null).commit()
                }
            }
            if (utils?.type == "url") {
                (context as? FragmentContainer)?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(utils.tag)))
            }
            utils?.ad_id?.let { it1 -> newAdClick(it1) }
        }
        val vp = container as ViewPager
        vp.addView(view, 0)

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
        textViewDescription.text = utils?.cat_description
        textViewTitle.text = utils?.cat_name
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

    private fun newAdClick(adID: String) {
        val stringRequest: StringRequest = object : StringRequest(Method.POST, URL_CLICKED, Response.Listener { }, Response.ErrorListener { }) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["id"] = adID
                params["method"] = "click"
                params["user_id"] = SharedPrefManager.getInstance(context)!!.userID!!
                params["username"] = SharedPrefManager.getInstance(context)!!.username!!
                return params
            }
        }
        (context as FragmentContainer).addToRequestQueue(stringRequest)
    }

    companion object {
        private const val URL_CLICKED = Constants.ROOT_URL + "dashboard_ad_interaction.php"
    }

}