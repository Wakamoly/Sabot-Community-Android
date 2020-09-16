package com.lucidsoftworksllc.sabotcommunity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_CANCELED;
import static android.view.View.GONE;

public class NewGroupMessage extends Fragment implements NewGroupMessageUserAdapter.AdapterCallback,UserListGroupMessageAdapter.AdapterCallback {

    private ImageView new_message_insignia;
    private RelativeLayout setInsigniaButton;
    private EditText etNewGroupMessageName;
    private SearchView newMessageSearch;
    private RecyclerView recyclerSearch,recyclerUsersToInvite;
    private CheckBox allUsersCanInvite,adminsCanInvite,allUsersCanChangeGroupImage,adminsCanChangeGroupImage,allUsersCanChangeGroupName,adminsCanChangeGroupName,allUsersCanMessage,adminsCanMessage,adminsCanRemoveUsers;
    private Button btnSubmit;
    private UserApiInterface apiInterface;
    private List<User> users;
    private NewGroupMessageUserAdapter adapter;
    private UserListGroupMessageAdapter adapter2;
    private ProgressBar newGroupMessageProgressBar;
    private Context mContext;
    private List<Search_Recycler> searchRecyclerList;
    private List<UserListRecycler> userListRecycler;
    private ArrayList<String> usersToInvite;
    private RecyclerView.LayoutManager layoutManager,layoutManager2;
    private final int GALLERY_INSIGNIA = 2;
    private Bitmap new_group_insigniaBitmap;
    JSONObject jsonObject;
    RequestQueue rQueue;
    public static final String URL_GROUP_SUBMIT = Constants.ROOT_URL+"messages.php/newgroup";
    public static final String UPLOAD_INSIGNIA_URL = Constants.ROOT_URL+"uploadInsigniaGroupMessage.php";

    @Override
    public void onMethodCallback(String user_id,String profile_pic,String nickname, String username, String verified, String online) {
        boolean isObjectExist = false;
        for(int i=0; i < userListRecycler.size(); i++){
            String thingy = userListRecycler.get(i).getUser_id();
            if(thingy.equals(user_id)){
                isObjectExist = true;
                break;
            }
        }
        if(!isObjectExist) {
            UserListRecycler userToAdd = new UserListRecycler(null, user_id, profile_pic, nickname, username, verified, "", null);
            userListRecycler.add(userToAdd);
            adapter2.notifyDataSetChanged();
            usersToInvite.add(username);
            Toast.makeText(mContext,"Adding @"+username,Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext,"Already added @"+username+"!",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onMethodCallbackUserList(int position, String username) {
        userListRecycler.remove(position);
        adapter2.notifyDataSetChanged();
        usersToInvite.remove(username);
        Toast.makeText(mContext,"Removed @"+username,Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View newGroupRootView = inflater.inflate(R.layout.fragment_new_group_message, null);

        new_message_insignia = newGroupRootView.findViewById(R.id.new_message_insignia);
        setInsigniaButton = newGroupRootView.findViewById(R.id.setInsigniaButton);
        etNewGroupMessageName = newGroupRootView.findViewById(R.id.etNewGroupMessageName);
        newMessageSearch = newGroupRootView.findViewById(R.id.newMessageSearch);
        recyclerSearch = newGroupRootView.findViewById(R.id.recyclerSearch);
        recyclerUsersToInvite = newGroupRootView.findViewById(R.id.recyclerUsersToInvite);
        allUsersCanInvite = newGroupRootView.findViewById(R.id.allUsersCanInvite);
        adminsCanInvite = newGroupRootView.findViewById(R.id.adminsCanInvite);
        allUsersCanChangeGroupImage = newGroupRootView.findViewById(R.id.allUsersCanChangeGroupImage);
        adminsCanChangeGroupImage = newGroupRootView.findViewById(R.id.adminsCanChangeGroupImage);
        allUsersCanChangeGroupName = newGroupRootView.findViewById(R.id.allUsersCanChangeGroupName);
        adminsCanChangeGroupName = newGroupRootView.findViewById(R.id.adminsCanChangeGroupName);
        allUsersCanMessage = newGroupRootView.findViewById(R.id.allUsersCanMessage);
        adminsCanMessage = newGroupRootView.findViewById(R.id.adminsCanMessage);
        adminsCanRemoveUsers = newGroupRootView.findViewById(R.id.adminsCanRemoveUsers);
        btnSubmit = newGroupRootView.findViewById(R.id.btnSubmit);
        newGroupMessageProgressBar = newGroupRootView.findViewById(R.id.newGroupMessageProgressBar);
        mContext = getActivity();
        new_group_insigniaBitmap = null;

        assert mContext != null;
        Glide.with(mContext)
                .load(Constants.BASE_URL + "assets/images/profile_pics/defaults/sabotblack.gif")
                .into(new_message_insignia);

        searchRecyclerList = new ArrayList<>();
        userListRecycler = new ArrayList<>();
        usersToInvite = new ArrayList<>();
        layoutManager = new LinearLayoutManager(mContext);
        recyclerSearch.setLayoutManager(layoutManager);
        layoutManager2 = new LinearLayoutManager(mContext);
        recyclerUsersToInvite.setLayoutManager(layoutManager2);
        adapter2 = new UserListGroupMessageAdapter(userListRecycler, mContext, NewGroupMessage.this);
        recyclerUsersToInvite.setAdapter(adapter2);

        setInsigniaButton.setOnClickListener(v -> {
            requestMultiplePermissions();
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_INSIGNIA);
        });

        btnSubmit.setOnClickListener(v -> {
            if (etNewGroupMessageName.getText().toString().length()>=3&&(etNewGroupMessageName.getText().toString().length()<=25)){
                newGroupMessageProgressBar.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                String optionArray = getGroupOptions();
                String inviteArray = usersToInvite.toString();
                SubmitGroupMessage(etNewGroupMessageName.getText().toString(),optionArray,inviteArray);
            }else{
                Toast.makeText(mContext, "Group name must be 3-25 characters long", Toast.LENGTH_SHORT).show();
            }
        });
        allUsersCanInvite.setOnClickListener(v -> {
            if (allUsersCanInvite.isChecked()){
                adminsCanInvite.setChecked(false);
                adminsCanInvite.setVisibility(GONE);
            }else{
                adminsCanInvite.setVisibility(View.VISIBLE);
            }
        });
        allUsersCanChangeGroupImage.setOnClickListener(v -> {
            if (allUsersCanChangeGroupImage.isChecked()){
                adminsCanChangeGroupImage.setChecked(false);
                adminsCanChangeGroupImage.setVisibility(GONE);
            }else{
                adminsCanChangeGroupImage.setVisibility(View.VISIBLE);
            }
        });
        allUsersCanChangeGroupName.setOnClickListener(v -> {
            if (allUsersCanChangeGroupName.isChecked()){
                adminsCanChangeGroupName.setChecked(false);
                adminsCanChangeGroupName.setVisibility(GONE);
            }else{
                adminsCanChangeGroupName.setVisibility(View.VISIBLE);
            }
        });
        allUsersCanMessage.setOnClickListener(v -> {
            if (allUsersCanMessage.isChecked()){
                adminsCanMessage.setChecked(false);
                adminsCanMessage.setVisibility(GONE);
            }else{
                adminsCanMessage.setVisibility(View.VISIBLE);
            }
        });

        newMessageSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchUser("users_only", query);
                if (!(query).isEmpty()) {
                    recyclerSearch.setVisibility(View.VISIBLE);
                } else {
                    recyclerSearch.setVisibility(GONE);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users_only", newText);
                    if (!(newText).isEmpty()) {
                        recyclerSearch.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSearch.setVisibility(GONE);
                    }
                }, 100);
                return false;
            }
        });

        return newGroupRootView;
    }

    public void fetchUser(String type, String key){
        apiInterface = UsersApiClient.getApiClient().create(UserApiInterface.class);
        Call<List<User>> call = apiInterface.getUsers(type, key);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, retrofit2.Response<List<User>> response) {
                users = response.body();
                adapter = new NewGroupMessageUserAdapter(users, mContext, NewGroupMessage.this);
                recyclerSearch.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                recyclerSearch.setVisibility(View.INVISIBLE);
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
                    recyclerSearch.setVisibility(View.VISIBLE);
                } else {
                    recyclerSearch.setVisibility(GONE);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(final String newText) {
                final Handler handler = new Handler();
                handler.postDelayed(() -> {
                    fetchUser("users_only", newText);
                    if (!(newText).isEmpty()) {
                        recyclerSearch.setVisibility(View.VISIBLE);
                    } else {
                        recyclerSearch.setVisibility(GONE);
                    }
                }, 100);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private String getGroupOptions(){
        ArrayList<String> optionsList = new ArrayList<>();
        if (allUsersCanChangeGroupImage.isChecked()){
            optionsList.add("allUsersCanChangeGroupImage");
        }
        if (allUsersCanChangeGroupName.isChecked()){
            optionsList.add("allUsersCanChangeGroupName");
        }
        if (allUsersCanInvite.isChecked()){
            optionsList.add("allUsersCanInvite");
        }
        if (allUsersCanMessage.isChecked()){
            optionsList.add("allUsersCanMessage");
        }
        if (adminsCanChangeGroupImage.isChecked()){
            optionsList.add("adminsCanChangeGroupImage");
        }
        if (adminsCanChangeGroupName.isChecked()){
            optionsList.add("adminsCanChangeGroupName");
        }
        if (adminsCanInvite.isChecked()){
            optionsList.add("adminsCanInvite");
        }
        if (adminsCanMessage.isChecked()){
            optionsList.add("adminsCanMessage");
        }
        if (adminsCanRemoveUsers.isChecked()){
            optionsList.add("adminsCanRemoveUsers");
        }
        return String.valueOf(optionsList);
    }

    public void SubmitGroupMessage(final String groupMessageName, final String optionArray, final String inviteArray){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL_GROUP_SUBMIT, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.getString("error").equals("false")){
                    String group_id = jsonObject.getString("groupid");
                    if(new_group_insigniaBitmap!=null){
                        uploadInsigniaImage(new_group_insigniaBitmap, group_id, etNewGroupMessageName.getText().toString());
                    }
                    ConvosFragment ldf = new ConvosFragment();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.chat_fragment_container, ldf).commit();
                }else{
                    Toast.makeText(mContext, "Network Error!", Toast.LENGTH_SHORT).show();
                    btnSubmit.setVisibility(View.VISIBLE);
                    newGroupMessageProgressBar.setVisibility(GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            newGroupMessageProgressBar.setVisibility(GONE);
            Toast.makeText(mContext,"Network Error!",Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String,String> parms= new HashMap<>();
                parms.put("group_name",groupMessageName);
                parms.put("option_array",optionArray);
                parms.put("invite_array",inviteArray);
                parms.put("username",SharedPrefManager.getInstance(mContext).getUsername());
                parms.put("userid",SharedPrefManager.getInstance(mContext).getUserID());
                return parms;
            }
        };
        ((ChatActivity)mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_INSIGNIA) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    final Bitmap bitmap2 = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), contentURI);
                    new_message_insignia.setImageBitmap(bitmap2);
                    new_group_insigniaBitmap = bitmap2;
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void uploadInsigniaImage(Bitmap bitmap, String group_id, String groupname){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 65, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("group_id", group_id);
            jsonObject.put("group_name", groupname);
            jsonObject.put("image", encodedImage);
            jsonObject.put("username", SharedPrefManager.getInstance(mContext).getUsername());
        } catch (JSONException ignored) {
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, UPLOAD_INSIGNIA_URL, jsonObject,
                jsonObject -> {
                    rQueue.getCache().clear();
                    try{
                        if(jsonObject.getString("error").equals("true")){
                            Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(mContext, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }, volleyError -> {});
        rQueue = Volley.newRequestQueue(mContext);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue.add(jsonObjectRequest);
    }
    private void requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            Toast.makeText(mContext.getApplicationContext(), "No permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> Toast.makeText(mContext.getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show())
                .onSameThread()
                .check();
    }

}