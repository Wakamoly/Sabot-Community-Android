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
import com.lucidsoftworksllc.sabotcommunity.db.PublicsDao
import com.lucidsoftworksllc.sabotcommunity.db.PublicsEntity
import com.lucidsoftworksllc.sabotcommunity.db.SabotDatabase
import com.lucidsoftworksllc.sabotcommunity.others.*
import com.yarolegovich.lovelydialog.LovelyChoiceDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
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
                //println("Loading more items! $currentPage isloadingFrag:$isLoadingFrag isloading:$isLoading islastpagefrag:$isLastPageFrag islastpage:$isLastPage")
                databaseQuery(sortBy!!, currentPage, pageSize, platform!!)
            }

            override fun isLastPage(): Boolean {
                return isLastPageFrag
            }

            override fun isLoading(): Boolean {
                return isLoadingFrag
            }
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

    private fun databaseQuery(sortBy: String, page: Int, limit: Int, platform: String){
        println("sort:$sortBy page:$page limit:$limit plat:$platform")
        var start = 0
        val finalPlatform = "%,$platform,%"
        var isAtPageLimit = false
        var total: Int
        var pageLimit: Int

        // Query string
        var queryString = String()
        // List of bind parameters
        val args: MutableList<Any> = ArrayList()
        var platformCondition = false

        // Beginning of query string
        queryString += "SELECT * FROM publicsentity"

        launch {
            if (finalPlatform == "%,All,%" ||finalPlatform == "%,all,%"){
                async { total = publicsDao?.numRowsAll()!!
                    pageLimit = ceil((total.div(limit)).toDouble()).toInt()
                    println("INIT pagelimit: $pageLimit total: $total limit: $limit page: $page")
                    if(page <= pageLimit) {
                        isAtPageLimit = page == pageLimit
                        start = if (page == 1) {
                            0
                        } else {
                            (page - 1) * limit
                        }
                    }else{
                        isAtPageLimit = true
                    }
                }.await()
            }else{
                platformCondition = true
                val platformArgs: MutableList<Any> = ArrayList()
                val filterNumRaw = "SELECT COUNT(id) FROM publicsentity WHERE platforms LIKE ?"
                platformArgs.add(finalPlatform)
                val filterNumQuery = SimpleSQLiteQuery(filterNumRaw, platformArgs.toTypedArray())
                async { total = publicsDao?.getNumGamesFilterRaw(filterNumQuery)!!
                    pageLimit = ceil((total.div(limit)).toDouble()).toInt()
                    println("INIT FILTER pagelimit: $pageLimit total: $total limit: $limit page: $page")
                    if(page <= pageLimit) {
                        isAtPageLimit = page == pageLimit
                        start = if (page == 1) {
                            0
                        } else {
                            (page - 1) * limit
                        }
                    }else{
                        isAtPageLimit = true
                    }
                }.await()
            }

            if (platformCondition){
                queryString += " WHERE platforms LIKE ?"
                args.add(finalPlatform)
            }

            var sortByFinal = ""
            when (sortBy) {
                "" -> {
                    sortByFinal = " ORDER BY followers DESC"
                }
                "Followers" -> {
                    sortByFinal = " ORDER BY followers DESC"
                }
                "Recently Added" -> {
                    sortByFinal = " ORDER BY id DESC"
                }
                "Oldest Added" -> {
                    sortByFinal = " ORDER BY id ASC"
                }
                "A-Z" -> {
                    sortByFinal = " ORDER BY title ASC"
                }
                "Open Posts" -> {
                    sortByFinal = " ORDER BY postcount DESC"
                }
                "Reviews" -> {
                    sortByFinal = " ORDER BY numratings DESC"
                }
            }
            queryString += sortByFinal
            queryString += " LIMIT $start, $limit"
            // End of query string
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
        publicsView?.adapter = adapter
        //if (currentPage != PaginationOnScroll.PAGE_START) adapter?.removeLoading()
        mProgressBar!!.visibility = View.GONE
        if (!isAtPageLimit) {
            adapter?.addLoading()
        } else {
            isLastPageFrag = true
            adapter?.removeLoading()
        }
        isLoadingFrag = false
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

    private fun getAllPublics(init: String) {
            val stringRequest = StringRequest(Request.Method.GET, "$Publics_GET_URL?username=$username", { response: String? ->
                try {
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
                                publicsObject.getString("platforms")
                        )
                        launch {
                            mContext?.let {
                                if (publicsDao!!.isRowIsExist(publicsObject.getInt("id"))) {
                                    publicsDao!!.updateGame(mGame)
                                } else {
                                    publicsDao!!.addGame(mGame)
                                    CoroutineScope(Main).launch {
                                        mContext!!.toast("Game added!")
                                    }
                                }
                            }
                        }
                    }
                    if (init == "yes") {
                        databaseQuery(sortBy!!, currentPage, pageSize, platform!!)
                    }
                    mProgressBar!!.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { Toast.makeText(mContext, "Network error!", Toast.LENGTH_SHORT).show() }
            (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    /*private fun loadPublics(page: Int) {
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
    }*/

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
        private const val Publics_GET_URL = Constants.ROOT_URL + "publics_getall.php"
    }
}