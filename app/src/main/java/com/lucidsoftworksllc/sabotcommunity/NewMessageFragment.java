package com.lucidsoftworksllc.sabotcommunity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewMessageFragment extends Fragment {

    private SearchView newMessageSearch;
    private RelativeLayout searchMessageLayout;
    private UserApiInterface apiInterface;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<User> users;
    private NewMessageSearchAdapter adapter;
    private ImageView backArrow;
    private LinearLayout newGroup;
    private Context mContext;

    List<Search_Recycler> searchRecyclerList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newMessageRootView = inflater.inflate(R.layout.fragment_new_message, null);

        newMessageSearch = newMessageRootView.findViewById(R.id.newMessageSearch);
        searchMessageLayout = newMessageRootView.findViewById(R.id.searchMessageLayout);
        backArrow = newMessageRootView.findViewById(R.id.backArrow);
        newGroup = newMessageRootView.findViewById(R.id.newGroup);
        mContext = getActivity();
        searchRecyclerList = new ArrayList<>();
        recyclerView = newMessageRootView.findViewById(R.id.recyclerSearch);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        backArrow.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStackImmediate());
        newGroup.setOnClickListener(v -> {
            NewGroupMessage ldf = new NewGroupMessage();
            ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.chat_fragment_container, ldf).commit();
        });

        newMessageSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser("users_only", query);
                if (!(query).isEmpty()) {
                    searchMessageLayout.setVisibility(View.VISIBLE);
                } else {
                    searchMessageLayout.setVisibility(View.INVISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users_only", newText);
                    if (!(newText).isEmpty()) {
                        searchMessageLayout.setVisibility(View.VISIBLE);
                    } else {
                        searchMessageLayout.setVisibility(View.INVISIBLE);
                    }
                }, 100);
                return false;
            }
        });

        return newMessageRootView;
    }

    public void fetchUser(String type, String key){
        apiInterface = UsersApiClient.getApiClient().create(UserApiInterface.class);
        Call<List<User>> call = apiInterface.getUsers(type, key);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                users = response.body();
                adapter = new NewMessageSearchAdapter(users, getActivity());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                searchMessageLayout.setVisibility(View.INVISIBLE);
                Toast.makeText(getContext(), "Error\n"+t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search, menu);

        final SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.searchView).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser("users_only", query);
                if (!(query).isEmpty()) {
                    searchMessageLayout.setVisibility(View.VISIBLE);
                } else {
                    searchMessageLayout.setVisibility(View.INVISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users_only", newText);
                    if (!(newText).isEmpty()) {
                        searchMessageLayout.setVisibility(View.VISIBLE);
                    } else {
                        searchMessageLayout.setVisibility(View.INVISIBLE);
                    }
                }, 100);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

}
