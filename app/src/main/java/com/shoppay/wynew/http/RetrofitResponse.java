package com.shoppay.wynew.http;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/12/7.
 */

public class RetrofitResponse {
    public static RetrofitAPI instance() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ContansUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(RetrofitAPI.class);
    }
}
