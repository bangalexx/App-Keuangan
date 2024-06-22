package com.banglexx.money.retrofit;


import com.banglexx.money.model.CategoryResponse;
import com.banglexx.money.model.LoginResponse;
import com.banglexx.money.model.TransactionRequest;
import com.banglexx.money.model.TransactionResponse;
import com.banglexx.money.model.ProfileResponse;
import com.banglexx.money.model.SubmitResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiEndpoint {

    @FormUrlEncoded
    @POST("api/login.php")
    Call<LoginResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/register.php")
    Call<SubmitResponse> register(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("api/profile.php")
    Call<ProfileResponse> profile(
            @Query("user_id") Integer user_id
    );

    @Multipart
    @POST("api/upload-foto.php")
    Call<SubmitResponse> upload(
            @Part ("file") MultipartBody.Part file,
            @Field("user_id") String user_id
    );

    @GET("api/list-transaction.php")
    Call<TransactionResponse> listTransaction(
            @Query("user_id") Integer user_id
    );

    @GET("api/list-transaction.php")
    Call<TransactionResponse> listTransactionFilter(
            @Query("user_id") Integer user_id,
            @Query("start_date") String start_date,
            @Query("end_date") String end_date
    );

    @GET("api/list-category.php")
    Call<CategoryResponse> listCategory();

    @FormUrlEncoded
    @POST("api/transaction.php")
    Call<SubmitResponse> transaction(
            @Field("user_id") Integer user_id,
            @Field("category_id") Integer category_id,
            @Field("description") String description,
            @Field("amount") Long amount,
            @Field("type") String type
    );

    @PUT("api/transaction-edit.php")
    Call<SubmitResponse> transaction(
            @Body TransactionRequest transactionRequest
    );

    @DELETE("api/transaction-delete.php")
    Call<SubmitResponse> transaction(
            @Query("id") Integer id
    );
}
