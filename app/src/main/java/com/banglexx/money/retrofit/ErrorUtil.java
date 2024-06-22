package com.banglexx.money.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.banglexx.money.model.ErrorResponse;
import retrofit2.Response;

public class ErrorUtil {

    public static String getMessage(Response response){
        Gson gson = new GsonBuilder().create();
        ErrorResponse errorResponse = gson.fromJson(response.errorBody().charStream(), ErrorResponse.class);
        return errorResponse.getMessage();
    }
}
