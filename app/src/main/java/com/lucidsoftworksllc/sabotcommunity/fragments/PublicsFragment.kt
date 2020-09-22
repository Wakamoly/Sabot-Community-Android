package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsAdapter
import com.lucidsoftworksllc.sabotcommunity.models.PublicsRecycler
import com.lucidsoftworksllc.sabotcommunity.others.CoFragment
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.PaginationOnScroll
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class PublicsFragment : CoFragment() {
    private var mProgressBar: ProgressBar? = null
    private var currentPage = PaginationOnScroll.PAGE_START
    private var isLastPage = false
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    private var isLoading = false
    private var adapter: PublicsAdapter? = null
    private var publicsViewManager: LinearLayoutManager? = null
    private var publicsRecyclerList: MutableList<PublicsRecycler>? = null
    private var publicsPlatformFilter: ImageView? = null
    private var publicsNewGame: ImageView? = null
    private var filter: String? = null
    private var username: String? = null
    private var sortBy: String? = null
    private var mContext: Context? = null
    private var sortByButton: LinearLayout? = null
    private var sortByText: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val publicsRootView = inflater.inflate(R.layout.fragment_publics, null)
        mProgressBar = publicsRootView.findViewById(R.id.progressBar)
        val publicsView: RecyclerView = publicsRootView.findViewById(R.id.recyclerPublics)
        publicsPlatformFilter = publicsRootView.findViewById(R.id.publicsPlatformFilter)
        sortByButton = publicsRootView.findViewById(R.id.sortByButton)
        sortByText = publicsRootView.findViewById(R.id.sortByText)
        publicsNewGame = publicsRootView.findViewById(R.id.publicsNewGame)
        mContext = activity
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        filter = SharedPrefManager.getInstance(mContext!!)!!.currentPublics
        sortBy = SharedPrefManager.getInstance(mContext!!)!!.publicsSortBy
        setPlatformImage(filter)
        sortByText?.text = sortBy
        publicsPlatformFilter?.setOnClickListener {
            val items = resources.getStringArray(R.array.platform_array_w_all)
            LovelyChoiceDialog(mContext)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle(R.string.platform_filter)
                    .setIcon(R.drawable.icons8_workstation_48)
                    .setMessage(resources.getString(R.string.selected_platform) + " " + filter)
                    .setItems(items) { _: Int, item: String? ->
                        SharedPrefManager.getInstance(mContext!!)!!.currentPublics = item
                        setPlatformImage(item)
                        filter = item
                        mProgressBar?.visibility = View.VISIBLE
                        publicsRecyclerList!!.clear()
                        currentPage = PaginationOnScroll.PAGE_START
                        isLastPage = false
                        isLoading = false
                        loadPublics(1)
                    }
                    .show()
        }
        sortByButton?.setOnClickListener { v: View? ->
            val popup = PopupMenu(mContext, v)
            val sortByArray = resources.getStringArray(R.array.publics_sort_by)
            for (s in sortByArray) {
                popup.menu.add(s)
            }
            popup.setOnMenuItemClickListener { item: MenuItem ->
                SharedPrefManager.getInstance(mContext!!)!!.publicsSortBy = item.toString()
                mProgressBar?.visibility = View.VISIBLE
                publicsRecyclerList!!.clear()
                currentPage = PaginationOnScroll.PAGE_START
                isLastPage = false
                isLoading = false
                sortBy = item.toString()
                sortByText?.text = item.toString()
                loadPublics(1)
                true
            }
            popup.show()
        }
        publicsNewGame?.setOnClickListener {
            val ldf = ContactUsFragment()
            val args = Bundle()
            args.putString("newpublics", "Add game: ")
            ldf.arguments = args
            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
        }
        mProgressBar?.visibility = View.VISIBLE
        publicsView.setHasFixedSize(true)
        publicsViewManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        publicsView.layoutManager = publicsViewManager
        publicsRecyclerList = ArrayList()
        adapter = PublicsAdapter(mContext!!, publicsRecyclerList)
        publicsView.adapter = adapter
        loadPublics(currentPage)
        publicsView.addOnScrollListener(object : PaginationOnScroll(publicsViewManager!!) {
            override fun loadMoreItems() {
                isLoading = true
                currentPage++
                loadPublics(currentPage)
            }
            override var isLastPage: Boolean = false
            override var isLoading: Boolean = false
        })

        if(SharedPrefManager.getInstance(mContext!!)!!.firstPublicsFragment == "show"){
            LovelyStandardDialog(mContext, LovelyStandardDialog.ButtonLayout.VERTICAL)
                    .setTopColorRes(R.color.green)
                    .setButtonsColorRes(R.color.white)
                    .setIcon(R.drawable.ic_check)
                    .setTitle(R.string.first_publics_frag_title)
                    .setMessage(mContext!!.resources.getString(R.string.first_publics_frag))
                    .setPositiveButton(android.R.string.ok){}
                    .setNegativeButton(R.string.dont_show_again) {
                        SharedPrefManager.getInstance(mContext!!)!!.firstPublicsFragment = "never"
                    }
                    .show()
        }

        return publicsRootView
    }

    private fun loadPublics(page: Int) {
        val thisThread: Thread = object : Thread() {
            //create thread
            override fun run() {
                val items = ArrayList<PublicsRecycler>()
                val stringRequest: StringRequest = object : StringRequest(Method.POST, Publics_URL,
                        Response.Listener { response: String? ->
                            try {
                                val publics = JSONArray(response)
                                for (i in 0 until publics.length()) {
                                    val publicsObject = publics.getJSONObject(i)
                                    val publicsRecycler = PublicsRecycler()
                                    publicsRecycler.id = publicsObject.getString("id")
                                    publicsRecycler.tag = publicsObject.getString("tag")
                                    publicsRecycler.title = publicsObject.getString("title")
                                    publicsRecycler.genre = publicsObject.getString("genre")
                                    publicsRecycler.image = publicsObject.getString("image")
                                    publicsRecycler.numratings = publicsObject.getString("numratings")
                                    publicsRecycler.avgrating = publicsObject.getString("avgrating")
                                    publicsRecycler.postcount = publicsObject.getString("postcount")
                                    publicsRecycler.followed = publicsObject.getString("followed")
                                    items.add(publicsRecycler)
                                }
                                if (currentPage != PaginationOnScroll.PAGE_START) adapter!!.removeLoading()
                                mProgressBar!!.visibility = View.GONE
                                adapter!!.addItems(items)
                                // check whether is last page or not
                                if (publics.length() == pageSize) {
                                    adapter!!.addLoading()
                                } else {
                                    isLastPage = true
                                    //adapter.removeLoading();
                                }
                                isLoading = false
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        },
                        Response.ErrorListener { }) {
                    override fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["page"] = page.toString()
                        params["items"] = pageSize.toString()
                        params["filter"] = filter!!
                        params["username"] = username!!
                        params["sort"] = sortBy!!
                        return params
                    }
                }
                (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
            }
        }
        thisThread.start() // start thread
    }

    private fun setPlatformImage(item: String?) {
        when (item) {
            "Xbox" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_xbox_50)
            "PlayStation" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_playstation_50)
            "Steam" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_steam_48)
            "PC" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_workstation_48)
            "Mobile" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_mobile_48)
            "Switch" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_nintendo_switch_48)
            "Cross-Platform" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_collect_40)
            "Other" -> publicsPlatformFilter!!.setImageResource(R.drawable.icons8_question_mark_64)
            else -> publicsPlatformFilter!!.setImageResource(R.drawable.ic_ellipses)
        }
    }

    companion object {
        private const val Publics_URL = Constants.ROOT_URL + "publics_api.php"
    }
}