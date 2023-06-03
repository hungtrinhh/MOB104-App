package com.btcdteam.easyedu.apis;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleAPI {
    public static Retrofit retrofit;
    public static final String SERVER_ENTRYPOINT = "https://fcm.googleapis.com/";

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(SERVER_ENTRYPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
