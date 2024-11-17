package com.example.camera_app.Service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;

public class ServiceAPIInstance {
    private static final String BASE_URL = "http://10.0.2.2:3004";
    private static ServiceAPI instance;

    private ServiceAPIInstance() {}

    public static ServiceAPI getInstance() {
        if (instance == null) {
            synchronized (ServiceAPIInstance.class) {
                if (instance == null) {
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                    httpClient.connectTimeout(60, TimeUnit.SECONDS); // 연결 타임아웃 60초 설정
                    httpClient.readTimeout(60, TimeUnit.SECONDS); // 읽기 타임아웃 60초 설정
                    httpClient.writeTimeout(60, TimeUnit.SECONDS); // 쓰기 타임아웃 60초 설정

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(httpClient.build()) // OkHttpClient 추가
                            .build();

                    instance = retrofit.create(ServiceAPI.class);
                }
            }
        }
        return instance;
    }

//    public static ServiceAPI getInstance() {
//        if (instance == null) {
//            synchronized (ServiceAPIInstance.class) {
//                if (instance == null) {
//                    Retrofit retrofit = new Retrofit.Builder()
//                            .baseUrl(BASE_URL)
//                            .addConverterFactory(GsonConverterFactory.create())
//                            .build();
//
//                    instance = retrofit.create(ServiceAPI.class);
//                }
//            }
//        }
//        return instance;
//    }
}
