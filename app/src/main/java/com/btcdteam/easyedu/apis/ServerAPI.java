package com.btcdteam.easyedu.apis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPI {
    public static Retrofit retrofit;
    public static final String LOCAL_BASEURL = "http://192.168.31.158:3000/";
    public static final String SERVER_BASEURL = "https://api.easyedu.online/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_BASEURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
