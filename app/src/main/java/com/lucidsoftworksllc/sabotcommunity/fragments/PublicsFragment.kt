package com.lucidsoftworksllc.sabotcommunity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.sqlite.db.SimpleSQLiteQuery
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.R
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.PublicsRoomAdapter
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsDao
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsEntity
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import kotlin.math.ceil

class PublicsFragment : CoFragment() {
    private var mProgressBar: ProgressBar? = null
    private var adapter: PublicsRoomAdapter? = null
    private var publicsViewManager: LinearLayoutManager? = null
    private var isLastPageFrag = false
    private var isLoadingFrag = false
    private var currentPage = PaginationOnScroll.PAGE_START
    private val pageSize = PaginationOnScroll.PAGE_SIZE
    //private var publicsRecyclerList: MutableList<PublicsRecycler>? = null
    private var publicsPlatformFilter: ImageView? = null
    private var publicsNewGame: ImageView? = null
    private var username: String? = null
    private var sortBy: String? = null
    private var mContext: Context? = null
    private var sortByButton: LinearLayout? = null
    private var sortByText: TextView? = null
    private var publicsDao: PublicsDao? = null
    private var games: List<PublicsEntity>? = null
    private var publicsView: RecyclerView? = null
    private var platform: String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val publicsRootView = inflater.inflate(R.layout.fragment_publics, null)
        mProgressBar = publicsRootView.findViewById(R.id.progressBar)
        publicsView = publicsRootView.findViewById(R.id.recyclerPublics)
        publicsPlatformFilter = publicsRootView.findViewById(R.id.publicsPlatformFilter)
        sortByButton = publicsRootView.findViewById(R.id.sortByButton)
        sortByText = publicsRootView.findViewById(R.id.sortByText)
        publicsNewGame = publicsRootView.findViewById(R.id.publicsNewGame)
        mContext = activity
        publicsDao = SabotDatabase(mContext!!).getPublicsDao()
        platform = SharedPrefManager.getInstance(mContext!!)!!.currentPublics
        username = SharedPrefManager.getInstance(mContext!!)!!.username
        sortBy = SharedPrefManager.getInstance(mContext!!)!!.publicsSortBy
        setPlatformImage(platform)
        sortByText?.text = sortBy
        publicsPlatformFilter?.setOnClickListener {
            val items = resources.getStringArray(R.array.platform_array_w_all)
            LovelyChoiceDialog(mContext)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle(R.string.platform_filter)
                    .setIcon(R.drawable.icons8_workstation_48)
                    .setMessage(resources.getString(R.string.selected_platform) + " " + platform)
                    .setItems(items) { _: Int, item: String? ->
                        SharedPrefManager.getInstance(mContext!!)!!.currentPublics = item
                        setPlatformImage(item)
                        mProgressBar?.visibility = View.VISIBLE
                        adapter?.clear()
                        currentPage = PaginationOnScroll.PAGE_START
                        isLastPageFrag = false
                        isLoadingFrag = false
                        platform = item
                        databaseQuery(sortBy!!, currentPage, pageSize, platform!!)
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
                adapter?.clear()
                currentPage = PaginationOnScroll.PAGE_START
                isLastPageFrag = false
                isLoadingFrag = false
                sortBy = item.toString()
                sortByText?.text = item.toString()
                databaseQuery(sortBy!!, currentPage, pageSize, platform!!)
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
        publicsView?.setHasFixedSize(true)
        publicsViewManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        publicsView?.layoutManager = publicsViewManager


        launch {
            if (publicsDao?.numRowsAll()?.equals(0)!!){
                getAllPublics("yes")
            }else {
                getAllPublics("no")
            }
        }
        databaseQuery(sortBy!!, currentPage, pageSize, platform!!)

        publicsView?.addOnScrollListener(object : PaginationOnScroll(publicsViewManager!!) {
            override fun loadMoreItems() {
                isLoadingFrag = true
                currentPage++
                databaseQuery(sortBy!!, currentPage, pageSize, platform!!)
            }
            override fun isLastPage(): Boolean { return isLastPageFrag }
            override fun isLoading(): Boolean { return isLoadingFrag }
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

    private fun getAllPublics(init: String) {
            val stringRequest = StringRequest(Request.Method.GET, "$Publics_GET_URL?username=$username", { response: String? ->
                try {
                    launch {
                        val publics = JSONArray(response)
                        for (i in 0 until publics.length()) {
                            val publicsObject = publics.getJSONObject(i)
                            val mGame = PublicsEntity(publicsObject.getInt("id"),
                                    publicsObject.getString("title"),
                                    publicsObject.getString("genre"),
                                    publicsObject.getString("image"),
                                    publicsObject.getInt("numratings"),
                                    publicsObject.getString("avgrating"),
                                    publicsObject.getString("tag"),
                                    publicsObject.getInt("postcount"),
                                    publicsObject.getString("followed"),
                                    publicsObject.getInt("followers"),
                                    publicsObject.getString("platforms"),
                                    publicsObject.getString("active")
                            )
                            withContext(Dispatchers.Default) {
                                if (publicsDao!!.isRowIsExist(publicsObject.getInt("id"))) {
                                    publicsDao!!.updateGame(mGame)
                                } else {
                                    publicsDao!!.addGame(mGame)
                                }
                            }

                        }
                        if (init == "yes") {
                            databaseQuery(sortBy!!, currentPage, pageSize, platform!!)
                        }
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
            (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
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

    private fun databaseQuery(sortBy: String, page: Int, limit: Int, platform: String){
        mProgressBar?.visibility = View.VISIBLE
        var start: Int
        val finalPlatform = "%,$platform,%"
        var isAtPageLimit = false
        var total: Int
        var pageLimit: Int
        var queryString = String()
        val args: MutableList<Any> = ArrayList()
        var platformCondition = false
        queryString += "SELECT * FROM publicsentity WHERE active = 'yes'"

        launch {
            if (finalPlatform == "%,All,%" ||finalPlatform == "%,all,%"){
                withContext(Dispatchers.Default) {
                    total = publicsDao?.numRowsAll()!!
                    pageLimit = ceil((total.div(limit)).toDouble()).toInt()
                    println("INIT pagelimit: $pageLimit total: $total limit: $limit page: $page")
                    if (page > pageLimit) {
                        isAtPageLimit = true
                    }
                    start = if (page == 1) {
                        0
                    } else {
                        (page - 1) * limit
                    }
                }
            }else{
                platformCondition = true
                val platformArgs: MutableList<Any> = ArrayList()
                val filterNumRaw = "SELECT COUNT(id) FROM publicsentity WHERE active = 'yes' AND platforms LIKE ?"
                platformArgs.add(finalPlatform)
                val filterNumQuery = SimpleSQLiteQuery(filterNumRaw, platformArgs.toTypedArray())
                withContext(Dispatchers.Default) {
                    total = publicsDao?.getNumGamesFilterRaw(filterNumQuery)!!
                    pageLimit = ceil((total.div(limit)).toDouble()).toInt()
                    println("INIT FILTER pagelimit: $pageLimit total: $total limit: $limit page: $page")
                    if (page > pageLimit) {
                        isAtPageLimit = true
                    }
                    start = if (page == 1) {
                        0
                    } else {
                        (page - 1) * limit
                    }
                }
            }

            if (platformCondition){
                queryString += " AND platforms LIKE ?"
                args.add(finalPlatform)
            }

            queryString += when (sortBy) {
                "" -> { " ORDER BY followers DESC" }
                "Followers" -> { " ORDER BY followers DESC" }
                "Recently Added" -> { " ORDER BY id DESC" }
                "Oldest Added" -> { " ORDER BY id ASC" }
                "A-Z" -> { " ORDER BY title ASC" }
                "Open Posts" -> { " ORDER BY postcount DESC" }
                "Reviews" -> { " ORDER BY numratings DESC" }
                else -> " ORDER BY followers DESC"
            }
            queryString += " LIMIT $start, $limit"
            queryString += ";"
            val rawQuery = SimpleSQLiteQuery(queryString, args.toTypedArray())
            if (games.isNullOrEmpty()){
                games = publicsDao?.getGamesRaw(rawQuery)
                if (games?.isNotEmpty()!!){
                    CoroutineScope(Main).launch {
                        mProgressBar!!.visibility = View.GONE
                    }
                }
                adapter = PublicsRoomAdapter(mContext!!, games!! as MutableList<PublicsEntity>)
                CoroutineScope(Main).launch {
                    addGamesINIT(isAtPageLimit)
                }
            }else{
                val items = publicsDao?.getGamesRaw(rawQuery)
                CoroutineScope(Main).launch {
                    addGamesToList(isAtPageLimit, items!!)
                }
            }
        }
    }

    private fun addGamesINIT(isAtPageLimit: Boolean){
        if(games?.isNotEmpty()!!){
            publicsView?.adapter = adapter
            publicsView?.scheduleLayoutAnimation()
            mProgressBar!!.visibility = View.GONE
            if (!isAtPageLimit) {
                adapter?.addLoading()
            } else {
                isLastPageFrag = true
                adapter?.removeLoading()
            }
            isLoadingFrag = false
        }
    }

    private fun addGamesToList(isAtPageLimit: Boolean, items: List<PublicsEntity>){
        adapter?.removeLoading()
        adapter?.addItems(items)
        mProgressBar!!.visibility = View.GONE
        if (!isAtPageLimit) {
            adapter?.addLoading()
        } else {
            isLastPageFrag = true
        }
        isLoadingFrag = false
    }

    companion object {
        private const val Publics_GET_URL = Constants.ROOT_URL + "publics_getall.php"
    }
}