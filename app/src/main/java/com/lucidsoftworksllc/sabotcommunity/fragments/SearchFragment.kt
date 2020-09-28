package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.activities.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.adapters.SearchAdapter
import com.lucidsoftworksllc.sabotcommunity.adapters.UserListAdapter
import com.lucidsoftworksllc.sabotcommunity.models.SearchRecycler
import com.lucidsoftworksllc.sabotcommunity.models.UserListRecycler
import com.lucidsoftworksllc.sabotcommunity.others.Constants
import com.lucidsoftworksllc.sabotcommunity.others.SharedPrefManager
import com.lucidsoftworksllc.sabotcommunity.retrofit.User
import com.lucidsoftworksllc.sabotcommunity.retrofit.UserApiInterface
import com.lucidsoftworksllc.sabotcommunity.retrofit.UsersApiClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SearchFragment : Fragment() {
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var users: List<User?>? = null
    private var adapter: SearchAdapter? = null
    private var apiInterface: UserApiInterface? = null
    private var mContext: Context? = null
    private var userID: String? = null
    private var textViewOthersWhoFollow: TextView? = null
    private var textViewGameName: TextView? = null
    private var textViewNoFollows: TextView? = null
    private var searchSwipe: SwipeRefreshLayout? = null
    private var progressBar: ProgressBar? = null
    private var userRecyclerList: MutableList<UserListRecycler>? = null
    private var searchRecyclerList: List<SearchRecycler>? = null
    private var popularAdapter: UserListAdapter? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var recyclerPopular: RecyclerView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val searchRootView = inflater.inflate(R.layout.fragment_search, null)
        setHasOptionsMenu(true)
        textViewGameName = searchRootView.findViewById(R.id.textViewGameName)
        textViewOthersWhoFollow = searchRootView.findViewById(R.id.textViewOthersWhoFollow)
        textViewNoFollows = searchRootView.findViewById(R.id.textViewNoFollows)
        mContext = activity
        userID = SharedPrefManager.getInstance(mContext!!)!!.userID
        val toolbar: Toolbar = searchRootView.findViewById(R.id.searchToolBar)
        (mContext as FragmentContainer?)!!.setSupportActionBar(toolbar)
        userRecyclerList = ArrayList()
        searchRecyclerList = ArrayList()
        recyclerPopular = searchRootView.findViewById(R.id.recyclerPopular)
        recyclerPopular?.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        recyclerPopular?.layoutManager = linearLayoutManager
        progressBar = searchRootView.findViewById(R.id.progressBar)
        searchSwipe = searchRootView.findViewById(R.id.searchSwipe)
        recyclerView = searchRootView.findViewById(R.id.recyclerSearch)
        layoutManager = LinearLayoutManager(context)
        recyclerView?.layoutManager = layoutManager
        loadPopular()
        fetchUser("users", "")
        searchSwipe?.setOnRefreshListener {
            val currentFragment = requireActivity().supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (currentFragment is SearchFragment) {
                val fragTransaction = requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out)
                fragTransaction.detach(currentFragment)
                fragTransaction.attach(currentFragment)
                fragTransaction.commit()
            }
        }
        return searchRootView
    }

    fun fetchUser(type: String?, key: String?) {
        apiInterface = UsersApiClient.apiClient!!.create(UserApiInterface::class.java)
        val call = apiInterface?.getUsers(type, key)
        call!!.enqueue(object : Callback<List<User?>?> {
            override fun onResponse(call: Call<List<User?>?>, response: Response<List<User?>?>) {
                progressBar!!.visibility = View.GONE
                users = response.body()
                adapter = SearchAdapter(users!! as MutableList<User>, mContext!!)
                recyclerView!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
                searchSwipe!!.isRefreshing = false
            }

            override fun onFailure(call: Call<List<User?>?>, t: Throwable) {
                progressBar!!.visibility = View.GONE
                searchSwipe!!.visibility = View.INVISIBLE
                searchSwipe!!.isRefreshing = false
            }
        })
    }

    private fun loadPopular() {
        val stringRequest = StringRequest(Request.Method.GET, "$Publics_URL?userid=$userID", { response: String? ->
            val obj: JSONObject
            try {
                obj = JSONObject(response!!)
                val profiles: JSONArray
                val gameinfo = obj.getJSONObject("game")
                if (obj.getJSONArray("users").length() != 0) {
                    profiles = obj.getJSONArray("users")
                    val gamename = gameinfo.getString("game_name")
                    val gameId = gameinfo.getString("id")
                    if (gamename == "null" || gameId.isEmpty()) {
                        textViewNoFollows!!.visibility = View.VISIBLE
                        textViewGameName!!.visibility = View.GONE
                        textViewOthersWhoFollow!!.visibility = View.GONE
                    } else {
                        textViewNoFollows!!.visibility = View.GONE
                        textViewGameName!!.visibility = View.VISIBLE
                        textViewOthersWhoFollow!!.visibility = View.VISIBLE
                        textViewGameName!!.text = gamename
                        textViewGameName!!.setOnClickListener {
                            val ldf = FragmentPublicsCat()
                            val args = Bundle()
                            args.putString("PublicsId", gameId)
                            ldf.arguments = args
                            requireActivity().supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).add(R.id.fragment_container, ldf).addToBackStack(null).commit()
                        }
                        for (i in 0 until profiles.length()) {
                            val profilenewsObject = profiles.getJSONObject(i)
                            val username = profilenewsObject.getString("username")
                            if (SharedPrefManager.getInstance(mContext!!)!!.isUserBlocked(username)) continue
                            val id = profilenewsObject.getString("id")
                            val userId = profilenewsObject.getString("user_id")
                            val profilePic = profilenewsObject.getString("profile_pic")
                            val nickname = profilenewsObject.getString("nickname")
                            val verified = profilenewsObject.getString("verified")
                            val online = profilenewsObject.getString("online")
                            val desc = profilenewsObject.getString("desc")
                            val publicsTopicResult = UserListRecycler(id, userId, profilePic, nickname, username, verified, online, desc)
                            userRecyclerList!!.add(publicsTopicResult)
                        }
                        popularAdapter = UserListAdapter(userRecyclerList!!, mContext!!)
                        recyclerPopular!!.adapter = popularAdapter
                        progressBar!!.visibility = View.GONE
                    }
                } else {
                    textViewNoFollows!!.visibility = View.VISIBLE
                    textViewGameName!!.visibility = View.GONE
                    textViewOthersWhoFollow!!.visibility = View.GONE
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, { Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show() })
        (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchManager = mContext!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchUser("users", query)
                if (query.isNotEmpty()) {
                    searchSwipe!!.visibility = View.VISIBLE
                } else {
                    searchSwipe!!.visibility = View.INVISIBLE
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val handler = Handler()
                handler.postDelayed({
                    fetchUser("users", newText)
                    if (newText.isNotEmpty()) {
                        searchSwipe!!.visibility = View.VISIBLE
                    } else {
                        searchSwipe!!.visibility = View.INVISIBLE
                    }
                }, 100)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val TAG = "SearchFragment"
        private const val Publics_URL = Constants.ROOT_URL + "readUsers_api.php"
    }
}