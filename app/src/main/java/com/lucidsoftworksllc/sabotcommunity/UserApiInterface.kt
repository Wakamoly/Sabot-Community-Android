package com.lucidsoftworksllc.sabotcommunity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UserApiInterface {

    @GET("getUsers_api.php")
    Call<List<User>> getUsers(
            @Query("item_type") String item_type,
            @Query("key") String keyword
    );
}
