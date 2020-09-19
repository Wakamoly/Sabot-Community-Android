package com.lucidsoftworksllc.sabotcommunity.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.lucidsoftworksllc.sabotcommunity.Constants
import com.lucidsoftworksllc.sabotcommunity.models.CurrentPublicsPOJO
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.R.drawable
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.fragments.PublicsTopicFragment
import de.hdodenhof.circleimageview.CircleImageView

class DashCurrentPublicsAdapter(currentPublicsSlider: List<*>, private val context: Context) : PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    @Suppress("UNCHECKED_CAST")
    private val currentPublicsSlider: List<CurrentPublicsPOJO> = currentPublicsSlider as List<CurrentPublicsPOJO>
    override fun getCount(): Int {
        return currentPublicsSlider.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater!!.inflate(R.layout.layout_viewpager_current_publics, null)
        val utils = currentPublicsSlider[position]
        val imageView = view.findViewById<ImageView>(R.id.sliderImageView)
        val textViewTitle = view.findViewById<TextView>(R.id.publicsPostTitle)
        val textViewDescription = view.findViewById<TextView>(R.id.publicsPostDesc)
        val tvWhen = view.findViewById<TextView>(R.id.tvWhen)
        val catName = view.findViewById<TextView>(R.id.catName)
        val nickname = view.findViewById<TextView>(R.id.nickname)
        val profileImage: CircleImageView = view.findViewById(R.id.profileImage)
        val platformImage = view.findViewById<ImageView>(R.id.platformImage)
        val numPlayersAdded = view.findViewById<TextView>(R.id.numPlayersAdded)
        val numPlayersNeeded = view.findViewById<TextView>(R.id.numPlayersNeeded)
        val whenLayout = view.findViewById<LinearLayout>(R.id.whenLayout)
        val playingNowLayout = view.findViewById<LinearLayout>(R.id.playingNowLayout)
        textViewDescription.text = utils.context
        textViewTitle.text = utils.subject
        catName.text = utils.catname
        nickname.text = utils.nickname
        numPlayersAdded.text = utils.numAdded
        numPlayersNeeded.text = utils.numPlayers
        tvWhen.text = utils.eventDate
        if (utils.playingNow1 == "yes") {
            playingNowLayout.visibility = View.VISIBLE
        } else {
            playingNowLayout.visibility = View.GONE
        }
        var finalBackImage = utils.image
        when {
            utils.image!!.contains(".jpg") -> {
                finalBackImage = finalBackImage!!.substring(0, utils.image!!.length - 4) + "_r.jpg"
            }
            utils.image!!.contains(".png") -> {
                finalBackImage = finalBackImage!!.substring(0, utils.image!!.length - 4) + "_r.png"
            }
            utils.image!!.contains(".gif") -> {
                finalBackImage = finalBackImage!!.substring(0, utils.image!!.length - 4) + "_r.gif"
            }
        }
        Glide.with(context)
                .load(Constants.BASE_URL + finalBackImage)
                .override(Target.SIZE_ORIGINAL)
                .into(imageView)
        val profilePic = utils.profilePic!!.substring(0, utils.profilePic!!.length - 4) + "_r.JPG"
        Glide.with(context)
                .load(Constants.BASE_URL + profilePic)
                .override(Target.SIZE_ORIGINAL)
                .into(profileImage)
        when (utils.type) {
            "Xbox" -> {
                platformImage.setImageResource(drawable.icons8_xbox_50)
                platformImage.visibility = View.VISIBLE
            }
            "PlayStation" -> {
                platformImage.setImageResource(drawable.icons8_playstation_50)
                platformImage.visibility = View.VISIBLE
            }
            "Steam" -> {
                platformImage.setImageResource(drawable.icons8_steam_48)
                platformImage.visibility = View.VISIBLE
            }
            "PC" -> {
                platformImage.setImageResource(drawable.icons8_workstation_48)
                platformImage.visibility = View.VISIBLE
            }
            "Mobile" -> {
                platformImage.setImageResource(drawable.icons8_mobile_48)
                platformImage.visibility = View.VISIBLE
            }
            "Switch" -> {
                platformImage.setImageResource(drawable.icons8_nintendo_switch_48)
                platformImage.visibility = View.VISIBLE
            }
            "Cross-Platform" -> {
                platformImage.setImageResource(drawable.icons8_collect_40)
                platformImage.visibility = View.VISIBLE
            }
            else -> {
                platformImage.setImageResource(drawable.icons8_question_mark_64)
                platformImage.visibility = View.VISIBLE
            }
        }
        if (utils.eventDate == "now") {
            whenLayout.setBackgroundResource(drawable.details_button)
        } else if (utils.eventDate == "ended") {
            whenLayout.setBackgroundResource(drawable.red_button)
        }
        view.setOnClickListener {
            if (context is FragmentContainer) {
                val ldf = PublicsTopicFragment()
                val args = Bundle()
                args.putString("PublicsId", utils.id)
                ldf.arguments = args
                (context as FragmentActivity).supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
            }
        }
        val vp = container as ViewPager
        vp.addView(view, 0)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}