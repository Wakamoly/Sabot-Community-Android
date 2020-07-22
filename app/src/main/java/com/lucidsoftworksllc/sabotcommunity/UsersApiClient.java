package com.lucidsoftworksllc.sabotcommunity;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UsersApiClient {

    public static Retrofit retrofit;

    public static Retrofit getApiClient(){
        if (retrofit==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.ROOT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
