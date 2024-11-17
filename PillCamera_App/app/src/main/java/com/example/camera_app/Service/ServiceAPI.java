package com.example.camera_app.Service;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.io.File;
import java.util.List;

public interface ServiceAPI {
    String ServiceURL = "http://10.0.2.2:3004";

    //get -> select
    //post -> insert
    //put -> update
    //delete -> delete


    @FormUrlEncoded
    @POST("/login")
    Call<ServiceAPIResponse> postLogin(
            @Field("id") String id,
            @Field("pw") String pw
    );

    @FormUrlEncoded
    @POST("/checkid")
    Call<ServiceAPIResponse> postCheckID(
            @Field("id") String id
    );


    @FormUrlEncoded
    @POST("/join")
    Call<ServiceAPIResponse> postJoin(
            @Field("name") String name,
            @Field("id") String id,
            @Field("pw") String pw
    );

    @Multipart
    @POST("/upload/{id}")
    Call<ServiceAPIResponse> uploadImage(@Path("id") String userId, @Part MultipartBody.Part image);

//    @GET("/download/{id}/{filename}")
//    Call<ServiceAPIResponse> downloadImage(@Path("id") String userId, @Path("filename") String fileName);

    @GET("/download/{id}/{filename}")
    Call<List<ServiceAPIResponse>> downloadImage(@Path("id") String userId, @Path("filename") String fileName);

}
