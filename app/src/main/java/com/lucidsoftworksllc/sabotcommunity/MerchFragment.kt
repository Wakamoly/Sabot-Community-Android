package com.lucidsoftworksllc.sabotcommunity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import org.json.JSONException
import java.util.*

class MerchFragment : Fragment() {
    private var backArrow: ImageView? = null
    private var mContext: Context? = null
    private var progressBar: ProgressBar? = null
    private var merch_recycler: RecyclerView? = null
    private var nothingToShow: TextView? = null
    private var merchList: MutableList<MerchList>? = null
    private var merchListAdapter: MerchListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val merchRootView = inflater.inflate(R.layout.fragment_merch, null)
        backArrow = merchRootView.findViewById(R.id.backArrow)
        progressBar = merchRootView.findViewById(R.id.progressBar)
        merch_recycler = merchRootView.findViewById(R.id.merch_recycler)
        nothingToShow = merchRootView.findViewById(R.id.nothingToShow)
        mContext = activity
        merchList = ArrayList()
        merch_recycler?.setHasFixedSize(true)
        merch_recycler?.layoutManager = LinearLayoutManager(mContext)
        backArrow?.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
        query
        return merchRootView
    }

    private val query: Unit
        get() {
            val stringRequest = StringRequest(Request.Method.GET, URL_MERCH, { response ->
                try {
                    val merchArray = JSONArray(response)
                    for (i in 0 until merchArray.length()) {
                        val merchObject = merchArray.getJSONObject(i)
                        val name = merchObject.getString("name")
                        val id = merchObject.getString("id")
                        val options = merchObject.getString("options")
                        val desc = merchObject.getString("desc")
                        val image = merchObject.getString("image")
                        val price = merchObject.getString("price")
                        val salePrice = merchObject.getString("sale_price")
                        val quantity = merchObject.getString("quantity")
                        val saleEnd = merchObject.getString("sale_end")
                        val active = merchObject.getString("active")
                        val merchResult = MerchList(id, name, options, desc, image, price, salePrice, quantity, saleEnd, active)
                        merchList!!.add(merchResult)
                    }
                    if (merchArray.length() == 0) {
                        nothingToShow!!.visibility = View.VISIBLE
                    }
                    merchListAdapter = MerchListAdapter(merchList!!, mContext!!)
                    merch_recycler!!.adapter = merchListAdapter
                    merch_recycler!!.isNestedScrollingEnabled = false
                    progressBar!!.visibility = View.GONE
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) {
                nothingToShow!!.visibility = View.VISIBLE
                progressBar!!.visibility = View.GONE
            }
            (mContext as FragmentContainer?)!!.addToRequestQueue(stringRequest)
        }

    companion object {
        private const val URL_MERCH = Constants.ROOT_URL + "merch_query.php"
    }
}