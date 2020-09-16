package com.lucidsoftworksllc.sabotcommunity;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lucidsoftworksllc.sabotcommunity.PaginationOnScroll.PAGE_SIZE;
import static com.lucidsoftworksllc.sabotcommunity.PaginationOnScroll.PAGE_START;

public class PublicsFragment extends Fragment {
    private static final String Publics_URL = Constants.ROOT_URL+"publics_api.php";
    private ProgressBar mProgressBar;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private int pageSize = PAGE_SIZE;
    private boolean isLoading = false;

    private PublicsAdapter adapter;
    private LinearLayoutManager publicsViewManager;
    private List<Publics_Recycler> publicsRecyclerList;
    private ImageView publicsPlatformFilter,publicsNewGame;
    private String filter, username, sortBy;
    private Context mContext;
    private LinearLayout sortByButton;
    private TextView sortByText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View publicsRootView = inflater.inflate(R.layout.fragment_publics, null);
        mProgressBar = publicsRootView.findViewById(R.id.progressBar);
        RecyclerView publicsView = publicsRootView.findViewById(R.id.recyclerPublics);
        publicsPlatformFilter = publicsRootView.findViewById(R.id.publicsPlatformFilter);
        sortByButton = publicsRootView.findViewById(R.id.sortByButton);
        sortByText = publicsRootView.findViewById(R.id.sortByText);
        publicsNewGame = publicsRootView.findViewById(R.id.publicsNewGame);
        mContext = getActivity();
        username = SharedPrefManager.getInstance(mContext).getUsername();
        filter = SharedPrefManager.getInstance(mContext).getCurrentPublics();
        sortBy = SharedPrefManager.getInstance(mContext).getPublicsSortBy();
        setPlatformImage(filter);
        sortByText.setText(sortBy);
        publicsPlatformFilter.setOnClickListener(v -> {
            String[] items = getResources().getStringArray(R.array.platform_array_w_all);
            new LovelyChoiceDialog(mContext)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle(R.string.platform_filter)
                    .setIcon(R.drawable.icons8_workstation_48)
                    .setMessage(getResources().getString(R.string.selected_platform)+" "+filter)
                    .setItems(items, (position, item) -> {
                        SharedPrefManager.getInstance(mContext).setCurrentPublics(item);
                        setPlatformImage(item);
                        filter = item;
                        mProgressBar.setVisibility(View.VISIBLE);
                        publicsRecyclerList.clear();
                        currentPage = PAGE_START;
                        isLastPage = false;
                        isLoading = false;
                        loadPublics(1);
                    })
                    .show();
        });
        sortByButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(mContext, v);
            String[] sortByArray = getResources().getStringArray(R.array.publics_sort_by);
            for (String s : sortByArray) {
                popup.getMenu().add(s);
            }
            popup.setOnMenuItemClickListener(item -> {
                SharedPrefManager.getInstance(mContext).setPublicsSortBy(item.toString());
                mProgressBar.setVisibility(View.VISIBLE);
                publicsRecyclerList.clear();
                currentPage = PAGE_START;
                isLastPage = false;
                isLoading = false;
                sortBy = item.toString();
                sortByText.setText(item.toString());
                loadPublics(1);
                return true;
            });
            popup.show();
        });

        publicsNewGame.setOnClickListener(v -> {
            ContactUsFragment ldf = new ContactUsFragment();
            Bundle args = new Bundle();
            args.putString("newpublics", "Add game: ");
            ldf.setArguments(args);
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, ldf).addToBackStack(null).commit();
        });
        mProgressBar.setVisibility(View.VISIBLE);
        publicsView.setHasFixedSize(true);
        publicsViewManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        publicsView.setLayoutManager(publicsViewManager);
        publicsRecyclerList = new ArrayList<>();
        adapter = new PublicsAdapter(mContext,publicsRecyclerList);
        publicsView.setAdapter(adapter);
        loadPublics(currentPage);
        publicsView.addOnScrollListener(new PaginationOnScroll(publicsViewManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                loadPublics(currentPage);
            }
            @Override public boolean isLastPage() {
                return isLastPage;
            }
            @Override public boolean isLoading() {
                return isLoading;
            }
        });

        return publicsRootView;
    }

    private void loadPublics(int page){
        Thread thisThread = new Thread(){//create thread
            @Override
            public void run() {
                final ArrayList<Publics_Recycler> items = new ArrayList<>();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, Publics_URL,
                        response -> {
                            try {
                                JSONArray publics = new JSONArray(response);
                                for(int i = 0; i<publics.length(); i++){
                                    JSONObject publicsObject = publics.getJSONObject(i);
                                    Publics_Recycler publics_recycler = new Publics_Recycler();

                                    publics_recycler.setId(publicsObject.getString("id"));
                                    publics_recycler.setTag(publicsObject.getString("tag"));
                                    publics_recycler.setTitle(publicsObject.getString("title"));
                                    publics_recycler.setGenre(publicsObject.getString("genre"));
                                    publics_recycler.setImage(publicsObject.getString("image"));
                                    publics_recycler.setNumratings(publicsObject.getString("numratings"));
                                    publics_recycler.setAvgrating(publicsObject.getString("avgrating"));
                                    publics_recycler.setPostcount(publicsObject.getString("postcount"));
                                    publics_recycler.setFollowed(publicsObject.getString("followed"));
                                    items.add(publics_recycler);
                                }
                                if (currentPage != PAGE_START) adapter.removeLoading();
                                mProgressBar.setVisibility(View.GONE);
                                adapter.addItems(items);
                                // check whether is last page or not
                                if (publics.length() == pageSize) {
                                    adapter.addLoading();
                                } else {
                                    isLastPage = true;
                                    //adapter.removeLoading();
                                }
                                isLoading = false;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> {}) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("page", String.valueOf(page));
                        params.put("items", String.valueOf(pageSize));
                        params.put("filter", filter);
                        params.put("username", username);
                        params.put("sort", sortBy);
                        return params;
                    }
                };
                ((FragmentContainer)mContext).addToRequestQueue(stringRequest);
            }
        };
        thisThread.start(); // start thread
    }

    private void setPlatformImage(String item){
        switch (item) {
            case "Xbox":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_xbox_50);
                break;
            case "PlayStation":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_playstation_50);
                break;
            case "Steam":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_steam_48);
                break;
            case "PC":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_workstation_48);
                break;
            case "Mobile":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_mobile_48);
                break;
            case "Switch":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_nintendo_switch_48);
                break;
            case "Cross-Platform":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_collect_40);
                break;
            case "Other":
                publicsPlatformFilter.setImageResource(R.drawable.icons8_question_mark_64);
                break;
            default:
                publicsPlatformFilter.setImageResource(R.drawable.ic_ellipses);
                break;
        }
    }


}
