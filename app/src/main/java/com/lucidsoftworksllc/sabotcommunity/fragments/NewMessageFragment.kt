package com.lucidsoftworksllc.sabotcommunity.fragments

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lucidsoftworksllc.sabotcommunity.*
import com.lucidsoftworksllc.sabotcommunity.adapters.NewMessageSearchAdapter
import com.lucidsoftworksllc.sabotcommunity.models.SearchRecycler
import com.lucidsoftworksllc.sabotcommunity.retrofit.User
import com.lucidsoftworksllc.sabotcommunity.retrofit.UserApiInterface
import com.lucidsoftworksllc.sabotcommunity.retrofit.UsersApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NewMessageFragment : Fragment() {
    private var newMessageSearch: SearchView? = null
    private var searchMessageLayout: RelativeLayout? = null
    private var apiInterface: UserApiInterface? = null
    private var recyclerView: RecyclerView? = null
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var users: List<User?>? = null
    private var adapter: NewMessageSearchAdapter? = null
    private var backArrow: ImageView? = null
    private var newGroup: LinearLayout? = null
    private var mContext: Context? = null
    private var searchRecyclerList: List<SearchRecycler>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val newMessageRootView = inflater.inflate(R.layout.fragment_new_message, null)
        newMessageSearch = newMessageRootView.findViewById(R.id.newMessageSearch)
        searchMessageLayout = newMessageRootView.findViewById(R.id.searchMessageLayout)
        backArrow = newMessageRootView.findViewById(R.id.backArrow)
        newGroup = newMessageRootView.findViewById(R.id.newGroup)
        mContext = activity
        searchRecyclerList = ArrayList()
        recyclerView = newMessageRootView.findViewById(R.id.recyclerSearch)
        layoutManager = LinearLayoutManager(mContext)
        recyclerView?.layoutManager = layoutManager
        backArrow?.setOnClickListener { requireActivity().supportFragmentManager.popBackStackImmediate() }
        newGroup?.setOnClickListener {
            val ldf = NewGroupMessage()
            (mContext as FragmentActivity?)!!.supportFragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_in, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out).addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit()
        }
        newMessageSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchUser("users_only", query)
                if (query.isNotEmpty()) {
                    searchMessageLayout?.visibility = View.VISIBLE
                } else {
                    searchMessageLayout?.visibility = View.INVISIBLE
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val handler = Handler()
                handler.postDelayed({
                    fetchUser("users_only", newText)
                    if (newText.isNotEmpty()) {
                        searchMessageLayout?.visibility = View.VISIBLE
                    } else {
                        searchMessageLayout?.visibility = View.INVISIBLE
                    }
                }, 100)
                return false
            }
        })
        return newMessageRootView
    }

    fun fetchUser(type: String?, key: String?) {
        apiInterface = UsersApiClient.apiClient!!.create(UserApiInterface::class.java)
        val call = apiInterface?.getUsers(type, key)
        call?.enqueue(object : Callback<List<User?>?> {
            override fun onResponse(call: Call<List<User?>?>, response: Response<List<User?>?>) {
                users = response.body()
                adapter = NewMessageSearchAdapter(users!! as MutableList<User>, activity!!)
                recyclerView!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }

            override fun onFailure(call: Call<List<User?>?>, t: Throwable) {
                searchMessageLayout!!.visibility = View.INVISIBLE
                Toast.makeText(context, "Error\n$t", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search, menu)
        val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.isSubmitButtonEnabled = true
        searchView.setIconifiedByDefault(false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                fetchUser("users_only", query)
                if (query.isNotEmpty()) {
                    searchMessageLayout!!.visibility = View.VISIBLE
                } else {
                    searchMessageLayout!!.visibility = View.INVISIBLE
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                val handler = Handler()
                handler.postDelayed({
                    fetchUser("users_only", newText)
                    if (newText.isNotEmpty()) {
                        searchMessageLayout!!.visibility = View.VISIBLE
                    } else {
                        searchMessageLayout!!.visibility = View.INVISIBLE
                    }
                }, 100)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }
}