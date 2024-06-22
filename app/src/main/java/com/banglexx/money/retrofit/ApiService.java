package com.banglexx.money.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiService {
    private final static String BASE_URL = "https://appskeuangan.000webhostapp.com/";
    public static ApiEndpoint endpoint(){

        final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl( BASE_URL )
                .client( client )
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create( ApiEndpoint.class );
    }
}
