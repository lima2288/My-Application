package com.myapplication.postexpandedlist;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitComments {

    private static RetrofitComments instance = null;
    private Api commentsApi;

    private RetrofitComments() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.COMMENTS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        commentsApi = retrofit.create(Api.class);
    }

    public static synchronized RetrofitComments getInstance() {
        if (instance == null) {
            instance = new RetrofitComments();
        }
        return instance;
    }

    public Api getMyApi() {
        return commentsApi;
    }
}
