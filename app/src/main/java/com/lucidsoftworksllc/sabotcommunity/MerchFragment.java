package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MerchFragment extends Fragment {

    private ImageView backArrow;
    private Context mContext;
    private ProgressBar progressBar;
    private RecyclerView merch_recycler;
    private TextView nothingToShow;
    private List<MerchList> merchList;
    private MerchListAdapter merchListAdapter;

    private static final String URL_MERCH = Constants.ROOT_URL+"merch_query.php";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View merchRootView = inflater.inflate(R.layout.fragment_merch, null);

        backArrow = merchRootView.findViewById(R.id.backArrow);
        progressBar = merchRootView.findViewById(R.id.progressBar);
        merch_recycler = merchRootView.findViewById(R.id.merch_recycler);
        nothingToShow = merchRootView.findViewById(R.id.nothingToShow);
        mContext = getActivity();

        merchList =  new ArrayList<>();
        merch_recycler.setHasFixedSize(true);
        merch_recycler.setLayoutManager(new LinearLayoutManager(mContext));

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStackImmediate();
            }
        });
        getQuery();

        return merchRootView;
    }

    private void getQuery(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_MERCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray merchArray = new JSONArray(response);
                    for(int i = 0; i<merchArray.length(); i++){
                        JSONObject merchObject = merchArray.getJSONObject(i);

                        String name = merchObject.getString("name");
                        String id = merchObject.getString("id");
                        String options = merchObject.getString("options");
                        String desc = merchObject.getString("desc");
                        String image = merchObject.getString("image");
                        String price = merchObject.getString("price");
                        String sale_price = merchObject.getString("sale_price");
                        String quantity = merchObject.getString("quantity");
                        String sale_end = merchObject.getString("sale_end");
                        String active = merchObject.getString("active");

                        MerchList merchResult = new MerchList(id,name,options,desc,image,price,sale_price,quantity,sale_end,active);
                        merchList.add(merchResult);
                    }
                    if (merchArray.length()==0){
                        nothingToShow.setVisibility(View.VISIBLE);
                    }
                    merchListAdapter = new MerchListAdapter(merchList,mContext);
                    merch_recycler.setAdapter(merchListAdapter);
                    merch_recycler.setNestedScrollingEnabled(false);
                    progressBar.setVisibility(GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                nothingToShow.setVisibility(View.VISIBLE);
                progressBar.setVisibility(GONE);
            }

        });
        ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
    }

}